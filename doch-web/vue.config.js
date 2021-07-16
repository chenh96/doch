module.exports = {
  publicPath: './',
  transpileDependencies: ['vuetify'],
  devServer: {
    proxy: {
      '/api': {
        //这里最好有一个 /
        target: 'https://doc.chenh.tech/api/', // 后台接口域名
        secure: true, // 如果是https接口，需要配置这个参数
        changeOrigin: true, //是否跨域
        pathRewrite: {
          '^/api': '',
        },
      },
    },
  },
}
