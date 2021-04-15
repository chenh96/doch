export default {
  // baseUrl: 'http://127.0.0.1:8848/',
  baseUrl: getCurrentUrl(),
}

/**
 * 获取当前URL，仅支持#模式
 */
function getCurrentUrl() {
  let origin = location.origin
  let path = location.pathname

  path =
    '/' +
    path
      .split('/')
      .filter((p) => p && p.trim() && p.trim() !== 'index.html')
      .join('/')
  path = path.endsWith('/') ? path : path + '/'

  return origin + path
}
