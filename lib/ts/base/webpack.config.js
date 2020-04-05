var configBase = require("./webpack.config.base");
var DtsBundleWebpack = require('dts-bundle-webpack')
var merge = require("webpack-merge");
var path = require('path');
var webpack = require("webpack");

var distPath = path.join(__dirname, 'dist');


module.exports = function (env, argv) {
  return merge(configBase(env, argv), {
    context: path.join(__dirname, 'src'),
    entry: {
      "index": './ts/paradicms/lib/base/index.ts'
    },
    // https://github.com/facebook/react/issues/13991
    externals: {
      react: "react",
    },
    output: {
      path: distPath,
      filename: '[name].js',
      library: "@paradicms/base",
      libraryTarget: "umd",
      publicPath: ''
    },
    plugins: [
      new DtsBundleWebpack({
        main: path.join(__dirname, "out", "index.d.ts"),
        name: "@paradicms/base",
        out: path.join(__dirname, "dist", "index.d.ts")
      }),
      new webpack.WatchIgnorePlugin([
        /\.js$/,
        /\.d\.ts$/
      ])
    ]
  });
}
