const { webpackMerge, htmlOverlay, webpackServeConfig } = require('just-scripts');
module.exports = webpackMerge(
  webpackServeConfig,
  htmlOverlay({
    template: 'public/index.html',
  }),
  {
    // Here you can custom webpack configurations
    output: {
      publicPath: '/'
    },
    
    devServer: {
      // it is React app, so let's use default port 3000.
      // https://webpack.js.org/configuration/dev-server/#devserverport
      port: 3000,

      // webpack doesn't expose content when it is in Docker image. So let's
      // allow connection from outside world ... in that case from outside of the container.
      host: '0.0.0.0'
    },
  }
);
