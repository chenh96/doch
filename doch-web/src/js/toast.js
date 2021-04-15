export default {
  state: {
    show: false,
    message: '',
    color: '',
  },
  setShow(val) {
    this.state.show = val
  },
  getShow() {
    return this.state.show
  },
  setMessage(val) {
    this.state.message = val
  },
  getMessage() {
    return this.state.message
  },
  setColor(val) {
    this.state.color = val
  },
  getColor() {
    return this.state.color
  },
}
