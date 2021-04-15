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
</style>
