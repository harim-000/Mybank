const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  chainWebpack: config => {
    config.module
        .rule('file-loader')
        .test(/\.(pdf|docx|pptx|xlsx|txt|exe|zip)$/)
        .use('file-loader')
        .loader('file-loader')
        .options({
          name: 'files/[name].[ext]'
        });
  },
  transpileDependencies: true,
  outputDir: '../backend/src/main/resources/static', // Build Directory
  devServer: {
    // overlay: false,
    historyApiFallback: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
<<<<<<< HEAD
        ws: false,
=======
        ws: true,
>>>>>>> 8d00f5cc8fb72e37539e8f361256dfdbfc57342a
        changeOrigin: true
      }
    }
  }
});