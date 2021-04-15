import Vue from 'vue'
import App from './App.vue'
import router from '@/js/router'
import vuetify from '@/js/vuetify'

import '@/js/regist'
import '@/js/axios'

// Vue.config.productionTip = false

export default new Vue({
  router,
  vuetify,
  render: (h) => h(App),
}).$mount('#app')
