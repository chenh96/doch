import Vue from 'vue'
import VueRouter from 'vue-router'
// import Home from '@/vue/Home'
// import Login from '@/vue/Login'
// import Project from '@/vue/Project'
// import Document from '@/vue/Document'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/vue/Home')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/vue/Login')
  },
  {
    path: '/project',
    name: 'Project',
    component: () => import('@/vue/Project')
  },
  {
    path: '/document',
    name: 'Document',
    component: () => import('@/vue/Document')
  },
]

const router = new VueRouter({
  routes,
})

export default router
