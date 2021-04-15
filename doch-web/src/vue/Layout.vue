<template>
  <v-app>
    <slot />

    <v-snackbar
      id="toast"
      v-if="toast.message"
      v-model="toast.show"
      :color="toast.color"
      timeout="2000"
    >
      {{ toast.message }}
    </v-snackbar>
  </v-app>
</template>

<script>
import Vue from 'vue'

import toast from '@/js/toast'

export default {
  name: 'Layout',
  mounted() {
    this.initToast()
  },
  data() {
    return {
      toast: toast.state,
    }
  },
  methods: {
    initToast() {
      Vue.prototype.$toast = (msg, color) => {
        let show = () => {
          toast.setShow(true)
          toast.setMessage('')
          this.$nextTick(() => {
            toast.setMessage(msg)
            toast.setColor(color)
          })
        }

        if (this.toastShow) {
          toast.setShow(false)
          setTimeout(show, 200)
        } else {
          show()
        }
      }
    },
  },
}
</script>

<style scoped>
#toast * {
  text-align: center;
}
</style>
