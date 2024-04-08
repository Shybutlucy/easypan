<template>
  <el-config-provider :locale="locale" :message="config">
    <RouterView />
  </el-config-provider>
</template>
<script setup>
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { reactive } from 'vue'
import { RouterView } from 'vue-router'

const locale = zhCn
const config = reactive({
  max: 1
})
</script>
<style lang="scss">
#app {
  overflow: hidden;
  height: 100%;
}
</style>
