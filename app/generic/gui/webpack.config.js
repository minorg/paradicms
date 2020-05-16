const configBase = require("../../../lib/ts/base/webpack.config.base");
const configDevServer = require("../../../lib/ts/base/webpack.config.devServer");
const path = require('path');
const merge = require("webpack-merge");
const CopyWebpackPlugin = require('copy-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

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
    module: {
      rules: [
        {
          test: /\.(scss)$/,
          use: [
            MiniCssExtractPlugin.loader,
            {
              loader: 'css-loader', // translates CSS into CommonJS modules
            }, {
              loader: 'postcss-loader', // Run post css actions
              options: {
                plugins: function() { // post css plugins, can be exported to postcss.config.js
                  return [
                    require('precss'),
                    require('autoprefixer')
                  ];
                }
              }
            }, {
              loader: 'sass-loader' // compiles Sass to CSS
            }
          ]
        }
      ]
    },
    plugins: [
      new CopyWebpackPlugin([{
        from: 'img',
        to: path.join(distPath, 'img/')
      }, 'robots.txt']),
      new MiniCssExtractPlugin({
        disable: argv.mode !== "production",
        filename: 'css/[name].css'
      }),
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
