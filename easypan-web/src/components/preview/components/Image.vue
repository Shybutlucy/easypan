<template>
  <div class="image-viewer">
    <el-image-viewer
      :initial-index="previewImgIndex"
      hide-on-click-modal
      :url-list="imageList"
      @close="closeImgViewer"
      v-if="previewImgIndex != null"
    >
    </el-image-viewer>
  </div>
</template>

<script>
export default {
  name: 'PreviewImage'
}
</script>
<script setup>
import { ref } from 'vue'

const props = defineProps({
  imageList: {
    type: Array
  }
})

const previewImgIndex = ref(null)

function show(index) {
  previewImgIndex.value = index
}

function closeImgViewer() {
  previewImgIndex.value = null
}

defineExpose({ show })
</script>
<style lang="scss" scoped></style>
