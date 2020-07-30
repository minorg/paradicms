const configBase = require("./webpack.config.base");
const DtsBundleWebpack = require('dts-bundle-webpack')
const merge = require("webpack-merge");
const path = require('path');
const webpack = require("webpack");


module.exports = function (env, argv) {
  return merge(configBase(env, argv), {
    context: path.join(__dirname, 'src'),
    entry: {
      "index": './index.ts'
    },
    // https://github.com/facebook/react/issues/13991
    externals: {
      react: "react",
    },
    output: {
      path: path.join(__dirname, 'dist'),
      filename: '[name].js',
      library: "@paradicms/gatsby-base",
      libraryTarget: "commonjs",
      publicPath: ''
    },
    plugins: [
      new DtsBundleWebpack({
        main: path.join(__dirname, "out", "index.d.ts"),
        name: "@paradicms/gatsby-base",
        out: path.join(__dirname, "dist", "index.d.ts")
      }),
      new webpack.WatchIgnorePlugin([
        /\.js$/,
        /\.d\.ts$/
      ])
    ]
  });
}
