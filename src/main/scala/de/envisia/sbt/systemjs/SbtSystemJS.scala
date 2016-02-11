package de.envisia.sbt.systemjs

import sbt._
import sbt.Keys._
import com.typesafe.sbt.web.SbtWeb
import com.typesafe.sbt.jse.{SbtJsEngine, SbtJsTask}
import com.typesafe.sbt.web.pipeline.Pipeline



object Import {

  object SystemJsKeys {
    val systemjs = TaskKey[Pipeline.Stage]("systemjs", "Generate js files from es6 js files.")
    val appDir = SettingKey[File]("systemjs-app-dir", "Top level directory that contains your app js files.")
    val mainFile = TaskKey[File]("systemjs-main-file", "The main SystemJS file")
    val mainConfigFile = TaskKey[File]("systemjs-config-file", "System JS cfg file")
    val outputFile = TaskKey[File]("systemjs-output-file", "The file where everything will be put to")
    val dir = SettingKey[File]("systemjs-dir", "By default, all modules are located relative to this path. In effect this is the target directory for systemjs.")

    // val webpackAppDir = TaskKey[File]("webpackAppDir", "The full path to your app directory")
    // val webpackEntryPoint = TaskKey[File]("webpackEntryPoint", "The full path to the entry point file.")
    // val webpackOutputFilename = TaskKey[String]("webpackOutputFilename", "A filename of the output javascript file.")
  }

}


object SbtSystemJS extends AutoPlugin {

  override def requires = SbtJsTask

  override def trigger = AllRequirements

  val autoImport = Import

  import SbtWeb.autoImport._
  import WebKeys._
  import SbtJsEngine.autoImport.JsEngineKeys._
  import SbtJsTask.autoImport.JsTaskKeys._
  import autoImport._
  import SystemJsKeys._

  override def projectSettings = Seq(
    appDir := (webTarget in systemjs).value / "public" / "main" / "app",

    dir := (webTarget in systemjs).value / "public" / "main",

    includeFilter in systemjs := GlobFilter("*.js"),
    excludeFilter in systemjs := HiddenFileFilter,

    mainFile := appDir.value /  "main.js",
    mainConfigFile := appDir.value / "cfg.js",
    outputFile := dir.value / "out.min.js",

    systemjs := runBuilder.dependsOn(webJarsNodeModules in Plugin).value
  )

  private def runBuilder: Def.Initialize[Task[Pipeline.Stage]] = Def.task { mappings =>
    val include = (includeFilter in systemjs).value
    val exclude = (excludeFilter in systemjs).value
    val builderMappings = mappings.filter(f => !f._1.isDirectory && include.accept(f._1) && !exclude.accept(f._1))

    // One way of declaring dependencies
    (nodeModules in Plugin).value
    (nodeModules in Assets).value
    (nodeModules in TestAssets).value

    val modules = (
      (nodeModuleDirectories in Plugin).value ++
        (nodeModuleDirectories in Assets).value ++
        (nodeModuleDirectories in TestAssets).value
      ).map(_.getCanonicalPath)

    SbtWeb.syncMappings(
      streams.value.cacheDirectory,
      builderMappings,
      (webTarget in systemjs).value / "public" / "main"
    )

    val cacheDirectory = streams.value.cacheDirectory / systemjs.key.label
    val runUpdate = FileFunction.cached(cacheDirectory, FilesInfo.hash) { _ =>
      streams.value.log.info("Building JavaScript with SystemJS")



      SbtJsTask.executeJs(
        state.value,
        // EngineType.Node,
        (engineType in systemjs).value,
        (command in systemjs).value,
        modules,
        new File(getClass.getClassLoader.getResource("systemjs-builder-shell.js").toURI),
        Seq(appDir.value.toString, mainConfigFile.value.toString, mainFile.value.toString, outputFile.value.toString),
        (timeoutPerSource in systemjs).value * builderMappings.size
      )

      dir.value.***.get.toSet
    }

    val buildtMappings = runUpdate(appDir.value.***.get.toSet).filter(_.isFile).pair(relativeTo(dir.value))
    (mappings.toSet -- builderMappings.toSet ++ buildtMappings).toSeq
  }

}
