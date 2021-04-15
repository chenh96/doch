import axios from 'axios'
import Vue from 'vue'
import that from '@/main'
import config from '@/js/config'
import auth from '@/js/auth'

axios.defaults.headers.post['Content-Type'] = 'application/json'

const request = axios.create({
  baseURL: config.baseUrl,
  timeout: 60000,
})

request.interceptors.request.use(
  (config) => {
    let token = auth.getToken()
    if (token) {
      config.headers[auth.tokenKey] = token
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    let result = response.data

    if (result.code === 200) {
      return result.data
    }

    axios.CancelToken.source().cancel()

    // 需要登录
    if (result.code === 300) {
      that.$toast(result.message, 'orange')
      that.$router.push('/login')
    } else {
      that.$toast(result.message, 'red')
    }

    return Promise.reject(new Error(result.data || 'Error'))
  },
  (error) => {
    axios.CancelToken.source().cancel()
    console.error(error)
    that.$toast('后台程序异常', 'red')
    return Promise.reject(error)
  }
)

/**
 * 预处理请求参数
 */
function formatData(params) {
  if (params instanceof FormData) {
    return params
  }

  let formData = new FormData()

  if (params) {
    Object.keys(params).forEach((key) => {
      if (params[key] === undefined) {
        return
      }

      formData.append(key, params[key])
    })
  }

  return formData
}

function formatParams(params) {
  if (params) {
    Object.keys(params).forEach((key) => {
      if (params[key] === undefined) {
        return
      }

      if (params[key] instanceof Array) {
        params[key] = params[key].join(',')
      }
    })
  }
  return params
}

Vue.prototype.$axios = {
  get(url, params) {
    return request.get(url, {
      params: formatParams(params),
    })
  },
  post(url, params) {
    return request.post(url, formatData(params))
  },
  delete(url, params) {
    return request.delete(url, {
      params: formatParams(params),
    })
  },
}
