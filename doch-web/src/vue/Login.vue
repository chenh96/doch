<template>
  <layout>
    <v-container class="d-flex justify-center fill-height grey lighten-5" fluid>
      <v-card class="mb-16" :loading="fetching" width="400" max-width="90%">
        <v-card-title>
          DOCH {{ mode === 'register' ? ' - 注册' : '' }}
        </v-card-title>

        <v-card-text>
          <v-form ref="form" :disabled="fetching">
            <v-text-field
              v-model="username"
              :rules="usernameRules"
              label="用户名"
              validate-on-blur
              @keydown.enter="handleConfirm"
            ></v-text-field>

            <v-text-field
              v-model="password"
              :rules="passwordRules"
              type="password"
              label="密码"
              validate-on-blur
              @keydown.enter="handleConfirm"
            ></v-text-field>

            <v-text-field
              v-if="mode === 'register'"
              v-model="repeatPassword"
              :rules="repeatPasswordRules"
              type="password"
              label="确认密码"
              validate-on-blur
              @keydown.enter="handleConfirm"
            ></v-text-field>
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-row class="ma-2">
            <v-btn
              color="green white--text"
              @click="handleLogin"
              :disabled="fetching"
            >
              登录
            </v-btn>

            <v-divider class="mx-4" vertical></v-divider>

            <v-btn
              @click="handleRegister"
              :disabled="fetching"
              color="blue white--text"
            >
              注册
            </v-btn>

            <v-spacer></v-spacer>

            <v-btn href="https://github.com/chenh96/doch" target="_blank" icon>
              <v-icon>mdi-github</v-icon>
            </v-btn>
          </v-row>
        </v-card-actions>
      </v-card>
    </v-container>
  </layout>
</template>

<script>
import auth from '@/js/auth'

export default {
  name: 'Login',
  data() {
    return {
      fetching: false,

      mode: 'login',

      username: '',
      password: '',
      repeatPassword: '',
      usernameRules: [
        (val) => !!val || '请输入用户名',
        (val) =>
          (val && val.length >= 6 && val.length <= 32) ||
          '请输入6到32位长度的用户名',
      ],
      passwordRules: [
        (val) => !!val || '请输入密码',
        (val) =>
          (val && val.length >= 6 && val.length <= 32) ||
          '请输入6到32位长度的密码',
      ],
      repeatPasswordRules: [
        (val) => !!val || '请再次输入密码',
        (val) => (val && val === this.password) || '请重新检查密码',
      ],
    }
  },
  methods: {
    fetchLogin() {
      if (this.fetching || !this.$refs.form.validate()) {
        return
      }
      this.fetching = true
      this.$axios
        .post('/user/login', {
          username: this.username,
          password: this.password,
        })
        .then((token) => {
          auth.setToken(token)
          this.fetching = false
          this.$router.push('/')
        })
        .catch(() => {
          this.fetching = false
        })
    },
    fetchRegister() {
      if (this.fetching || !this.$refs.form.validate()) {
        return
      }
      this.fetching = true
      this.$axios
        .post('/user/register', {
          username: this.username,
          password: this.password,
        })
        .then(() => {
          this.fetching = false
          this.fetchLogin()
        })
        .catch(() => {
          this.fetching = false
        })
    },
    handleLogin() {
      if (this.mode !== 'login') {
        this.mode = 'login'
        return
      }
      this.fetchLogin()
    },
    handleRegister() {
      if (this.mode !== 'register') {
        this.mode = 'register'
        return
      }
      this.fetchRegister()
    },
    handleConfirm(e) {
      e.target.blur()
      if (this.mode === 'login') {
        this.fetchLogin()
      } else if (this.mode === 'register') {
        this.fetchRegister()
      }
    },
  },
}
</script>

<style scoped></style>
