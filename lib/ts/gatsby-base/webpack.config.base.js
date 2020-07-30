const webpack = require('webpack');

const TsconfigPathsPlugin = require('tsconfig-paths-webpack-plugin');

module.exports = function (env, argv) {
  return {
    module: {
      rules: [
        // .ts, .tsx
        {
          test: /\.tsx?$/,
          loader: "ts-loader",
        }
      ]
    },
    resolve: {
      extensions: ['.ts', '.tsx', '.js'],
      // Fix webpack's default behavior to not load packages with jsnext:main module
      // (jsnext:main directs not usually distributable es6 format, but es6 sources)
      mainFields: ['module', 'browser', 'main'],
      plugins: [new TsconfigPathsPlugin()]
    },
    target: 'web'
  };
}
