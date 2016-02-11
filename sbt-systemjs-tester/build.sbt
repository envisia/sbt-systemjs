import SystemJsKeys._
import WebJs._

lazy val root = (project in file(".")).enablePlugins(SbtWeb)

pipelineStages := Seq(systemjs)

// JsEngineKeys.engineType := JsEngineKeys.EngineType.Node