const configBase = require("../../../lib/ts/base/webpack.config.base");
const configDevServer = require("../../../lib/ts/base/webpack.config.devServer");
const path = require('path');
const merge = require("webpack-merge");
const CopyWebpackPlugin = require('copy-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = function (env, argv) {
  const distPath = path.join(__dirname, 'dist');

  return merge(configBase(env, argv), configDevServer(distPath), {
    context: path.join(__dirname, 'src'),
    entry: {
      "generic-gui": './ts/paradicms/app/generic/main.tsx'
    },
    output: {
      path: distPath,
      filename: 'js/[name].js',
      publicPath: ''
    },
    plugins: [
      new CopyWebpackPlugin([{
        from: 'img',
        to: path.join(distPath, 'img/')
      }, 'robots.txt']),
      new HtmlWebpackPlugin({
        hash: true,
        template: 'index.html'
      })
    ],
    resolve: {
      alias: {
        // https://github.com/facebook/react/issues/13991
        react: path.resolve("..", "..", "..", "node_modules", "react")
      }
    }
  });
}
