export default {
  tokenKey: 'Token',
  getToken() {
    return localStorage.getItem(this.tokenKey)
  },
  setToken(token) {
    localStorage.setItem(this.tokenKey, token)
  },
  removeToken() {
    localStorage.removeItem(this.tokenKey)
  },
}
