const webpack = require('webpack');

const TsconfigPathsPlugin = require('tsconfig-paths-webpack-plugin');

module.exports = function (env, argv) {
  return {
    // https://webpack.js.org/configuration/devtool/
    devtool: argv.mode === "production" ? undefined : 'cheap-module-eval-source-map',
    module: {
      rules: [
        {
          test: /\.(graphql|gql)$/,
          exclude: /node_modules/,
          loader: "graphql-tag/loader"
        },
        // .ts, .tsx
        {
          test: /\.tsx?$/,
          use: [
            argv.mode !== "production" && {
              loader: 'babel-loader'
            },
            'ts-loader'
          ].filter(Boolean)
        }
      ]
    },
    plugins: [
      new webpack.DefinePlugin({
        DEVELOPMENT: argv.mode === "development",
        PRODUCTION: argv.mode === "production"
      })
    ],
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
