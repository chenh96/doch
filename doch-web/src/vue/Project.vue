<template>
  <layout>
    <v-app-bar class="grey lighten-5" app flat clipped-left>
      <v-progress-linear
        :active="fetchingDocuments || fetchingDocument"
        indeterminate
        absolute
        top
      ></v-progress-linear>

      <v-btn v-show="!password" to="/" icon>
        <v-icon>mdi-arrow-left</v-icon>
      </v-btn>

      <v-btn class="text-subtitle-1" to="/" text>
        DOCH
      </v-btn>

      <v-divider
        class="mx-2"
        v-show="!password.startsWith('pub-')"
        inset
        vertical
      ></v-divider>

      <v-btn
        v-show="!password.startsWith('pub-')"
        icon
        :to="{
          path: '/document',
          query: { projectId: projectId, password: password },
        }"
      >
        <v-icon>mdi-plus-circle-outline</v-icon>
      </v-btn>

      <v-btn
        v-show="documentId && !password.startsWith('pub-')"
        :to="{
          path: '/document',
          query: {
            projectId: projectId,
            documentId: documentId,
            password: password,
          },
        }"
        icon
      >
        <v-icon>mdi-circle-edit-outline</v-icon>
      </v-btn>

      <v-btn
        v-show="documentId && !password.startsWith('pub-')"
        @click="deleteDialogShow = true"
        icon
      >
        <v-icon>mdi-trash-can-outline</v-icon>
      </v-btn>

      <v-divider v-if="!password" class="mx-2" inset vertical></v-divider>

      <v-btn v-if="!password" @click="fetchShares" icon>
        <v-icon>mdi-share-variant-outline</v-icon>
      </v-btn>
    </v-app-bar>

    <v-main
      class="fill-height grey lighten-5 px-2"
      v-if="!fetchingDocuments && documents.length <= 0"
      fluid
    >
      <p class="pa-4 grey--text text--darken-2">
        当前还没有文档
      </p>
    </v-main>

    <v-navigation-drawer
      v-if="documents.length > 0"
      style="border-top: 1px solid #f2f6fc"
      app
      clipped
      floating
      permanent
    >
      <v-treeview
        :items="parents"
        :active="active"
        :open="open"
        @update:active="handleChoose"
        item-key="id"
        activatable
        open-on-click
        dense
      >
        <template v-slot:prepend="{ item, open }">
          <v-icon v-if="item.isParent">
            {{ open ? 'mdi-folder-open' : 'mdi-folder' }}
          </v-icon>
          <v-icon v-else>
            mdi-file-document-outline
          </v-icon>
        </template>
      </v-treeview>
    </v-navigation-drawer>

    <v-main class="grey lighten-5 fill-height" v-if="documents.length > 0">
      <mavon-editor
        id="editor"
        class="fill-height"
        v-if="documents.length > 0 && documentId"
        :value="document.content"
        :placeholder="' '"
        :box-shadow="false"
        :toolbars-flag="false"
        :subfield="false"
        :editable="false"
        :short-cut="false"
        :external-link="externalLink"
        default-open="preview"
        preview-background="#ffffff"
      ></mavon-editor>
    </v-main>

    <v-dialog v-model="deleteDialogShow" max-width="400">
      <v-card :loading="deletingDocument">
        <v-card-title> 即将删除 {{ document.name }} </v-card-title>
        <v-card-text>
          您确定要删除该文档吗？该操作将无法挽回！
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="deleteDialogShow = false" color="green darken-1" text>
            取消
          </v-btn>
          <v-btn
            @click="fetchDeleteDocument"
            :disabled="deletingDocument"
            color="red darken-1"
            text
          >
            我意已决
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="shareDialogShow" max-width="800">
      <v-card :loading="sharing">
        <v-card-title> 分享项目 </v-card-title>
        <v-card-text id="share-content">
          <v-container class="pa-2" fluid>
            <v-row
              v-show="!sharing && !publicPassword && !privatePassword"
              class="mt-4"
            >
              <span>当前项目已关闭分享</span>
            </v-row>
            <v-row v-show="publicPassword">
              <span>仅可读：</span>
            </v-row>
            <v-row v-show="publicPassword">
              <code>{{ url + publicPassword }}</code>
            </v-row>
            <v-row v-show="privatePassword" class="mt-8">
              <span>可编辑：</span>
            </v-row>
            <v-row v-show="privatePassword">
              <code>{{ url + privatePassword }}</code>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="shareDialogShow = false" text>
            关闭
          </v-btn>
          <v-btn
            v-show="publicPassword || privatePassword"
            :disabled="sharing"
            @click="fetchCancelShare"
            color="orange white--text"
            text
          >
            取消分享
          </v-btn>
          <v-btn
            :disabled="sharing"
            @click="fetchShare"
            color="green white--text"
            text
          >
            {{ publicPassword || privatePassword ? '刷新密码' : '开启分享' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </layout>
</template>

<script>
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'

export default {
  name: 'Project',
  components: {
    mavonEditor,
  },
  mounted() {
    this.password = this.$route.query.password || ''

    let projectId = this.$route.query.projectId
    if (projectId !== undefined && projectId !== null && projectId !== '') {
      try {
        this.projectId = parseInt(projectId)
      } catch (err) {
        this.$router.push('/')
        return
      }
    } else {
      this.$router.push('/')
      return
    }

    let documentId = this.$route.query.documentId
    if (documentId !== undefined && documentId !== null && documentId !== '') {
      try {
        this.documentId = parseInt(documentId)
      } catch (err) {
        this.documentId = ''
      }
    } else {
      this.documentId = ''
    }

    this.fetchDocuments()
  },
  data() {
    return {
      fetchingDocuments: false,
      fetchingDocument: false,

      projectId: '',

      documents: [],
      parents: [],
      active: [],
      open: [],

      documentId: '',
      document: {},

      deleteDialogShow: false,
      deletingDocument: false,

      shareDialogShow: false,
      sharing: false,
      publicPassword: '',
      privatePassword: '',

      password: '',

      externalLink: {
        markdown_css: false,
        hljs_js: () => '/md/highlightjs/highlight.min.js',
        hljs_css: (css) => '/md/highlightjs/styles/' + css + '.min.css',
        hljs_lang: (lang) => '/md/highlightjs/languages/' + lang + '.min.js',
        katex_css: () => '/md/katex/katex.min.css',
        katex_js: () => '/md/katex/katex.min.js',
      },
    }
  },
  methods: {
    fetchDocuments() {
      if (this.fetchingDocuments) {
        return
      }
      this.fetchingDocuments = true
      this.$axios
        .get(this.password ? '/share' : '/document', {
          projectId: this.projectId,
          password: this.password,
        })
        .then((documents) => {
          this.documents = documents
          this.computeParents()
          this.fetchingDocuments = false
          this.chooseCurrent()
        })
        .catch(() => {
          this.fetchingDocuments = false
        })
    },
    fetchDocument() {
      if (this.fetchingDocument) {
        return
      }
      this.fetchingDocument = true
      this.$axios
        .get(this.password ? '/share' : '/document', {
          projectId: this.projectId,
          documentId: this.documentId,
          password: this.password,
        })
        .then((document) => {
          this.document = document
          this.fetchingDocument = false
          if (this.documentId != this.$route.query.documentId) {
            this.$router.replace({
              query: {
                projectId: this.projectId,
                documentId: this.documentId,
                password: this.password,
              },
            })
          }
        })
        .catch(() => {
          this.fetchingDocument = false
        })
    },
    fetchDeleteDocument() {
      if (this.deletingDocument) {
        return
      }
      this.deletingDocument = true
      this.$axios
        .delete(this.password ? '/share' : '/document', {
          documentId: this.documentId,
          password: this.password,
        })
        .then(() => {
          this.documentId = ''
          this.deletingDocument = false
          this.deleteDialogShow = false
          this.fetchDocuments()
        })
        .catch(() => {
          this.deletingDocument = false
        })
    },
    fetchShares() {
      if (this.sharing) {
        return
      }
      this.shareDialogShow = true
      this.sharing = true
      this.$axios
        .get('/project/share', { id: this.projectId })
        .then((passwords) => {
          this.publicPassword = passwords.public
          this.privatePassword = passwords.private
          this.sharing = false
        })
        .catch(() => {
          this.sharing = false
        })
    },
    fetchShare() {
      if (this.sharing) {
        return
      }
      this.sharing = true
      this.$axios
        .post('/project/share', { id: this.projectId })
        .then((passwords) => {
          this.publicPassword = passwords.public
          this.privatePassword = passwords.private
          this.sharing = false
        })
        .catch(() => {
          this.sharing = false
        })
    },
    fetchCancelShare() {
      if (this.sharing) {
        return
      }
      this.sharing = true
      this.$axios
        .delete('/project/share', { id: this.projectId })
        .then((passwords) => {
          this.publicPassword = passwords.public
          this.privatePassword = passwords.private
          this.sharing = false
        })
        .catch(() => {
          this.sharing = false
        })
    },
    computeParents() {
      let parentNames = Array.from(
        new Set(this.documents.map((doc) => doc.parent))
      ).sort((p, n) => p.localeCompare(n, 'zh'))
      let parents = []
      for (let i = 0; i < parentNames.length; i++) {
        let docs = this.documents
          .filter((doc) => doc.parent === parentNames[i])
          .sort((p, n) => p.name.localeCompare(n.name, 'zh'))
        parents.push({
          id: 'Parent' + i,
          name: parentNames[i],
          children: docs,
          isParent: true,
        })
      }
      this.parents = parents
    },
    chooseFirst() {
      if (this.parents.length > 0 && this.parents[0].children.length > 0) {
        this.open = ['Parent0']
        this.handleChoose([this.parents[0].children[0].id])
      }
    },
    chooseCurrent() {
      for (let parent of this.parents) {
        for (let doc of parent.children) {
          if (doc.id === this.documentId) {
            this.open = [parent.id]
            this.handleChoose([doc.id])
            return
          }
        }
      }
      this.chooseFirst()
    },
    handleChoose(arr) {
      if (arr.length > 0) {
        this.documentId = arr[0]
      }

      this.active = [this.documentId]
      this.fetchDocument()
    },
  },
  computed: {
    url() {
      return `${location.href.split('?')[0]}?projectId=${
        this.projectId
      }&documentId=${this.documentId}&password=`
    },
  },
}
</script>

<style scoped>
@import '/md/markdown/github-markdown.min.css';

#editor {
  border-radius: 0;
  z-index: 0;
}

#share-content {
  height: 30vh;
  overflow: auto;
}
</style>
