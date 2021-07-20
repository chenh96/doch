<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
}
</script>

<style>
html {
  overflow-y: hidden !important;
}

#app {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
}

.hljs,
.markdown-body pre code {
  background: transparent !important;
}

::-webkit-scrollbar {
  width: 8px !important;
  height: 8px !important;
  background-color: inherit !important;
}

*::-webkit-scrollbar-track {
  background-color: #fafafa !important;
}

::-webkit-scrollbar-thumb {
  border-radius: 4px !important;
  background-color: #ccc !important;
}
</style>
