<template>
  <div class="window" v-if="show">
    <div class="window-mask" @click="close" v-if="show"></div>
    <div
      class="window-content"
      :style="{ top: '0px', left: windowContentLeft + 'px', width: windowContentWidth + 'px' }"
    >
      <div class="title">
        <div class="close" @click="close">
          <el-icon size="20" color="#fff"><CloseOutlined /></el-icon>
        </div>
        <span class="title">{{ title }}</span>
      </div>
      <div class="content-body" :style="{ 'align-items': align }">
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Window',
  components: { CloseOutlined }
}
</script>
<script setup>
import { useWindowSizeFn } from '@/utils/useWindowSizeFn'
import { CloseOutlined } from '@vicons/antd'
import { computed, ref } from 'vue'

const props = defineProps({
  show: {
    type: Boolean
  },
  width: {
    type: Number,
    default: 1000
  },
  title: {
    type: String
  },
  align: {
    type: String,
    default: 'top'
  }
})
const emit = defineEmits(['close'])
const windowWidth = ref(window.innerWidth)
const windowContentWidth = computed(() => {
  return props.width > windowWidth.value ? windowWidth.value : props.width
})
const windowContentLeft = computed(() => {
  let left = windowWidth.value - props.width
  return left < 0 ? 0 : left / 2
})

const close = () => {
  emit('close')
}
// 自适应窗口变化
const resizeWidth = () => {
  windowWidth.value = window.innerWidth
}
useWindowSizeFn(resizeWidth, 280)
</script>
<style lang="scss" scoped>
.window {
  .window-mask {
    top: 0px;
    left: 0px;
    width: 100%;
    height: calc(100vh);
    z-index: 200;
    opacity: 0.5;
    background-color: #000;
    position: fixed;
  }
  .close {
    z-index: 202;
    cursor: pointer;
    position: absolute;
    top: 40px;
    right: 30px;
    width: 44px;
    height: 44px;
    border-radius: 22px;
    background-color: #606266;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .window-content {
    top: 0px;
    z-index: 201;
    position: absolute;
    background-color: #fff;
    .title {
      text-align: center;
      line-height: 40px;
      border-bottom: 1px solid #ddd;
      font-weight: bold;
    }
    .content-body {
      display: flex;
      overflow: auto;
      height: calc(100vh - 41px);
    }
  }
}
</style>
