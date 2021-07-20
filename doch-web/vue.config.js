module.exports = {
  publicPath: './',
  transpileDependencies: ['vuetify'],
  devServer: {
    proxy: {
      '/api': {
        target: 'https://doc.chenh.tech/api/',
        secure: true,
        changeOrigin: true,
        pathRewrite: {
          '^/api': '',
        },
      },
    },
  },
}
