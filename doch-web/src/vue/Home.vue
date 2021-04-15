<template>
  <layout>
    <v-app-bar class="grey lighten-5" app flat>
      <v-progress-linear
        :active="fetchingProjects"
        indeterminate
        absolute
        top
      ></v-progress-linear>

      <v-btn class="text-subtitle-1" @click="handleReload" text>
        DOCH
      </v-btn>

      <v-divider class="mx-2" inset vertical></v-divider>

      <v-btn @click="handleNewProject" icon>
        <v-icon>mdi-plus-circle-outline</v-icon>
      </v-btn>

      <v-spacer></v-spacer>

      <v-btn @click="handleEditUser" icon>
        <v-icon>mdi-account-edit-outline</v-icon>
      </v-btn>

      <v-btn class="mx-2" @click="handleLogout" icon>
        <v-icon>mdi-logout</v-icon>
      </v-btn>
    </v-app-bar>

    <v-main class="grey lighten-5">
      <v-container class="px-2 py-0" fluid>
        <p
          v-if="!fetchingProjects && projects.length <= 0"
          class="pa-4 grey--text text--darken-2"
        >
          当前还没有项目
        </p>

        <v-card
          class="d-inline-block ma-2 project-card"
          v-for="project of projects"
          :key="project.id"
          :to="{ path: '/project', query: { projectId: project.id } }"
          min-width="250"
          max-width="400"
          hover
        >
          <v-card-title>
            {{ project.name }}
          </v-card-title>

          <v-card-actions class="project-card-actions">
            <v-row class="ma-2" align="center">
              <span class="d-flex">
                <v-icon dense left>
                  mdi-file-document-multiple-outline
                </v-icon>
                {{ project.documentNumber }}
              </span>
              <v-spacer></v-spacer>
              <v-btn @click.prevent="handleDeleteProject(project)" icon>
                <v-icon>mdi-trash-can-outline</v-icon>
              </v-btn>
              <v-btn
                class="mx-2"
                @click.prevent="handleTransfer(project.id)"
                icon
              >
                <v-icon>mdi-account-switch-outline</v-icon>
              </v-btn>
              <v-btn @click.prevent="handleEditProject(project)" icon>
                <v-icon>mdi-circle-edit-outline</v-icon>
              </v-btn>
            </v-row>
          </v-card-actions>
        </v-card>
      </v-container>
    </v-main>

    <v-dialog v-model="editDialogShow" max-width="400">
      <v-card :loading="fetchingEditProject">
        <v-card-title>
          <span v-if="typeof projectId === 'number'">
            修改项目信息
          </span>
          <span v-else>新增一个项目</span>
        </v-card-title>

        <v-card-text>
          <v-form ref="editForm" :disabled="fetchingEditProject">
            <v-text-field
              v-model="projectName"
              :rules="projectNameRules"
              label="项目名"
              validate-on-blur
            ></v-text-field>
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="editDialogShow = false" text>
            取消
          </v-btn>
          <v-btn
            @click="fetchEditProject"
            :disabled="fetchingEditProject"
            color="green darken-1"
            text
          >
            保存
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="deleteDialogShow" max-width="400">
      <v-card :loading="deletingProject">
        <v-card-title> 即将删除 {{ deleteProjectName }} </v-card-title>
        <v-card-text>
          您确定要删除该项目吗？该操作将无法挽回！
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="deleteDialogShow = false" color="green darken-1" text>
            取消
          </v-btn>
          <v-btn
            @click="fetchDeleteProject"
            :disabled="deletingProject"
            color="red darken-1"
            text
          >
            我意已决
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="userDialogShow" max-width="400">
      <v-card :loading="savingUser">
        <v-card-title>
          修改账户信息
        </v-card-title>
        <v-card-text>
          <v-form ref="userForm" :disabled="fetchingEditProject">
            <v-text-field
              v-model="username"
              :rules="usernameRules"
              label="用户名"
              validate-on-blur
            ></v-text-field>
            <v-text-field
              v-model="password"
              :rules="passwordRules"
              type="password"
              label="密码"
              validate-on-blur
            ></v-text-field>
            <v-text-field
              v-model="repeatPassword"
              :rules="repeatPasswordRules"
              type="password"
              label="确认密码"
              validate-on-blur
            ></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="userDialogShow = false" text>
            取消
          </v-btn>
          <v-btn
            :disabled="savingUser"
            @click="fetchSaveUser"
            color="green white--text"
            text
          >
            保存
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="transferDialogShow" max-width="400">
      <v-card :loading="transferingProject">
        <v-card-title>
          向其他用户转移项目
        </v-card-title>

        <v-card-text>
          <v-form ref="transferForm" :disabled="fetchingEditProject">
            <v-text-field
              v-model="transferUsername"
              :rules="usernameRules"
              label="用户名"
              validate-on-blur
            ></v-text-field>
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="transferDialogShow = false" text>
            取消
          </v-btn>
          <v-btn
            @click="fetchTransferProject"
            :disabled="transferingProject"
            color="orange darken-1"
            text
          >
            转移
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </layout>
</template>

<script>
import auth from '@/js/auth'

export default {
  name: 'Home',
  mounted() {
    this.fetchProjects()
  },
  data() {
    return {
      fetchingProjects: false,
      projects: [],

      editDialogShow: false,
      fetchingEditProject: false,

      projectId: '',
      projectName: '',
      projectNameRules: [
        (val) => !!val || '请输入项目名',
        (val) =>
          (val && val.length >= 2 && val.length <= 32) ||
          '请输入2到32位长度的项目名',
      ],

      deleteDialogShow: false,
      deletingProject: false,
      deleteProjectId: '',
      deleteProjectName: '',

      userDialogShow: false,
      savingUser: false,
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

      transferDialogShow: false,
      transferingProject: false,
      transferProjectId: '',
      transferUsername: '',
    }
  },
  methods: {
    fetchProjects() {
      if (this.fetchingProjects) {
        return
      }
      this.fetchingProjects = true
      this.$axios
        .get('/project')
        .then((projects) => {
          this.projects = projects
          this.fetchingProjects = false
        })
        .catch(() => {
          this.fetchingProjects = false
        })
    },
    fetchEditProject() {
      if (this.fetchingEditProject || !this.$refs.editForm.validate()) {
        return
      }
      this.fetchingEditProject = true
      this.$axios
        .post('/project', { id: this.projectId, name: this.projectName })
        .then(() => {
          this.fetchingEditProject = false
          this.editDialogShow = false
          this.fetchProjects()
        })
        .catch(() => {
          this.fetchingEditProject = false
        })
    },
    fetchDeleteProject() {
      if (this.deletingProject) {
        return
      }
      this.deletingProject = true
      this.$axios
        .delete('/project', { id: this.deleteProjectId })
        .then(() => {
          this.deletingProject = false
          this.deleteDialogShow = false
          this.fetchProjects()
        })
        .catch(() => {
          this.deletingProject = false
        })
    },
    fetchSaveUser() {
      if (this.savingUser || !this.$refs.userForm.validate()) {
        return
      }
      this.savingUser = true
      this.$axios
        .post('/user', { username: this.username, password: this.password })
        .then(() => {
          this.userDialogShow = false
          this.savingUser = false
          this.$router.push('/login')
        })
        .catch(() => {
          this.savingUser = false
        })
    },
    fetchTransferProject() {
      if (this.transferingProject || !this.$refs.transferForm.validate()) {
        return
      }
      this.transferingProject = true
      this.$axios
        .post('/project/transfer', {
          id: this.transferProjectId,
          username: this.transferUsername,
        })
        .then(() => {
          this.transferDialogShow = false
          this.transferingProject = false
          this.fetchProjects()
        })
        .catch(() => {
          this.transferingProject = false
        })
    },
    handleNewProject() {
      this.$refs.editForm && this.$refs.editForm.resetValidation()
      this.projectId = ''
      this.projectName = ''
      this.editDialogShow = true
    },
    handleEditProject(project) {
      this.$refs.editForm && this.$refs.editForm.resetValidation()
      this.projectId = project.id
      this.projectName = project.name
      this.editDialogShow = true
    },
    handleEditUser() {
      this.$refs.userForm && this.$refs.userForm.resetValidation()
      this.username = ''
      this.password = ''
      this.repeatPassword = ''
      this.userDialogShow = true
    },
    handleTransfer(id) {
      this.$refs.transferForm && this.$refs.transferForm.resetValidation()
      this.transferProjectId = id
      this.transferDialogShow = true
    },
    handleDeleteProject(project) {
      this.deleteProjectId = project.id
      this.deleteProjectName = project.name
      this.deleteDialogShow = true
    },
    handleLogout() {
      auth.removeToken()
      this.$router.push('/login')
    },
    handleReload() {
      location.reload()
    },
  },
}
</script>

<style scoped>
.project-card-actions .v-btn {
  visibility: hidden;
}

.project-card:hover .project-card-actions .v-btn {
  visibility: visible;
}
</style>
