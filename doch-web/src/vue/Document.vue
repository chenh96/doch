<template>
  <layout>
    <v-app-bar class="grey lighten-5" app flat>
      <v-progress-linear
        :active="fetchingGet || fetchingSave"
        indeterminate
        absolute
        top
      ></v-progress-linear>

      <v-btn
        icon
        :to="{
          path: '/project',
          query: { projectId: projectId, password: password },
        }"
      >
        <v-icon>mdi-arrow-left</v-icon>
      </v-btn>

      <v-btn class="text-subtitle-1" to="/" text>
        DOCH
      </v-btn>

      <v-divider class="mx-2" inset vertical></v-divider>

      <v-btn @click="fetchCopies" icon>
        <v-icon size="20">mdi-content-copy</v-icon>
      </v-btn>

      <v-btn @click="handleSave" icon>
        <v-icon>mdi-content-save-outline</v-icon>
      </v-btn>

      <v-divider
        class="ml-2 mr-4"
        v-if="parent || name"
        inset
        vertical
      ></v-divider>

      <span class="text-truncate"> {{ parent }} </span>
      <span class="text-truncate mx-2" v-show="parent && name"> - </span>
      <span class="text-truncate"> {{ name }} </span>
    </v-app-bar>

    <v-main class="fill-height">
      <mavon-editor
        id="editor"
        class="fill-height"
        v-model="content"
        :toolbars="toolbars"
        :box-shadow="false"
        :external-link="externalLink"
      ></mavon-editor>
    </v-main>

    <v-dialog v-model="copyDialogShow" max-width="400" max-height="100%">
      <v-card :loading="fetchingCopy">
        <v-card-title>
          复制文档
        </v-card-title>

        <v-card-text id="copy-list">
          <v-list nav dense>
            <v-list-item-group>
              <v-list-item
                v-for="copy in copies"
                :key="copy.id"
                @click="fetchContent(copy.id)"
              >
                <v-list-item-content>
                  <v-list-item-title>
                    {{ copy.name }}
                  </v-list-item-title>
                </v-list-item-content>
              </v-list-item>
            </v-list-item-group>
          </v-list>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="copyDialogShow = false" text>
            取消
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="saveDialogShow" max-width="400">
      <v-card :loading="fetchingSave">
        <v-card-title>
          <span v-if="typeof documentId === 'number'">
            修改文档
          </span>
          <span v-else>新增文档</span>
        </v-card-title>

        <v-card-text>
          <v-form ref="saveForm" :disabled="fetchingSave">
            <v-combobox
              v-if="parents.length > 0"
              v-model="parent"
              :items="parents"
              :rules="parentRules"
              label="分组名"
              validate-on-blur
            ></v-combobox>
            <v-text-field
              v-else
              v-model="parent"
              :rules="parentRules"
              label="分组名"
              validate-on-blur
            ></v-text-field>
            <v-text-field
              v-model="name"
              :rules="nameRules"
              label="文档名"
              validate-on-blur
            ></v-text-field>
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="saveDialogShow = false" text>
            取消
          </v-btn>
          <v-btn
            @click="fetchSave"
            :disabled="fetchingSave"
            color="green darken-1"
            text
          >
            保存
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
  name: 'Document',
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

    this.fetchInfo()
  },
  data() {
    return {
      projectId: '',
      documentId: '',

      fetchingGet: false,

      saveDialogShow: false,
      fetchingSave: false,
      parents: [],
      parentRules: [
        (val) => !!val || '请输入分组',
        (val) =>
          (val && val.length >= 2 && val.length <= 32) ||
          '请输入2到32位长度的分组名',
      ],
      nameRules: [
        (val) => !!val || '请输入文档名',
        (val) =>
          (val && val.length >= 2 && val.length <= 32) ||
          '请输入2到32位长度的文档名',
      ],

      copies: [],
      copyDialogShow: false,
      fetchingCopy: false,

      name: '',
      parent: '',
      content: '',

      password: '',

      toolbars: {
        bold: true, // 粗体
        italic: true, // 斜体
        header: true, // 标题
        underline: true, // 下划线
        strikethrough: true, // 中划线
        mark: true, // 标记
        superscript: true, // 上角标
        subscript: true, // 下角标
        quote: true, // 引用
        ol: true, // 有序列表
        ul: true, // 无序列表
        link: true, // 链接
        // imagelink: true, // 图片链接
        code: true, // code
        table: true, // 表格
        // fullscreen: true, // 全屏编辑
        // readmodel: true, // 沉浸式阅读
        htmlcode: true, // 展示html源码
        help: true, // 帮助
        /* 1.3.5 */
        undo: true, // 上一步
        redo: true, // 下一步
        trash: true, // 清空
        /* 1.4.2 */
        navigation: true, // 导航目录
        /* 2.1.8 */
        alignleft: true, // 左对齐
        aligncenter: true, // 居中
        alignright: true, // 右对齐
        /* 2.2.1 */
        subfield: true, // 单双栏模式
        preview: true, // 预览
      },

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
    fetchInfo() {
      if (this.fetchingGet) {
        return
      }
      this.fetchingGet = true
      this.$axios
        .get(this.password ? '/share/info' : '/document/info', {
          documentId: this.documentId,
          projectId: this.projectId,
          password: this.password,
        })
        .then((info) => {
          this.parents = info.parents
          this.parent = info.document && info.document.parent
          this.name = info.document && info.document.name
          this.content = info.document && info.document.content
          this.fetchingGet = false
        })
        .catch(() => {
          this.fetchingGet = false
        })
    },
    fetchCopies() {
      this.copyDialogShow = true
      if (this.copies.length > 0 || this.fetchingCopy) {
        return
      }
      this.fetchingCopy = true
      this.$axios
        .get(this.password ? '/share' : '/document', {
          projectId: this.projectId,
          password: this.password,
        })
        .then((documents) => {
          this.copies = documents
          this.fetchingCopy = false
        })
        .catch(() => {
          this.fetchingCopy = false
        })
    },
    fetchContent(id) {
      if (this.fetchingCopy) {
        return
      }
      this.fetchingCopy = true
      this.$axios
        .get(this.password ? '/share' : '/document', {
          documentId: id,
          projectId: this.projectId,
          password: this.password,
        })
        .then((document) => {
          this.content = document.content
          this.fetchingCopy = false
          this.copyDialogShow = false
        })
        .catch(() => {
          this.fetchingCopy = false
        })
    },
    fetchSave() {
      if (this.fetchingSave || !this.$refs.saveForm.validate()) {
        return
      }
      this.fetchingSave = true
      this.$axios
        .post(this.password ? '/share' : '/document', {
          documentId: this.documentId,
          projectId: this.projectId,
          parent: this.parent,
          name: this.name,
          content: this.content,
          password: this.password,
        })
        .then((document) => {
          this.fetchingSave = false
          this.$router.push({
            path: '/project',
            query: {
              projectId: this.projectId,
              documentId: document.id,
              password: this.password,
            },
          })
        })
        .catch(() => {
          this.fetchingSave = false
        })
    },
    handleSave() {
      this.$refs.saveForm && this.$refs.saveForm.resetValidation()
      this.saveDialogShow = true
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

#copy-list {
  max-height: 40vh;
  overflow: auto;
}
</style>
