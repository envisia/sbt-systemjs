var Builder = require('systemjs-builder');

var base_path = process.argv[2];
var cfg_file = process.argv[3];

var main_file = process.argv[4];
var output_file = process.argv[5];

var builder = new Builder(base_path, cfg_file);

builder.buildStatic(main_file, output_file, { minify: true, sourceMaps: true });
