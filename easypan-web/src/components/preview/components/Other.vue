<template>
  <div class="other-previewer">
    <Icon class="file-icon" :file-type="fileInfo.fileType" :width="92" />
    <span class="filename">{{ fileInfo.filename }}</span>
    <span class="desc">暂不支持在线预览，我们会持续优化，敬请期待</span>
    <el-button type="primary" class="download" @click="download"
      >下载 {{ size2Str(fileInfo.fileSize) }}</el-button
    >
  </div>
</template>

<script>
export default {
  name: 'PreviewDownload'
}
</script>
<script setup>
import Icon from '@/components/Icon.vue'
import { size2Str } from '@/utils'
import { createDownloadUrl } from '@/api/file'

const props = defineProps({
  downloadUrl: {
    type: String
  },
  createDownloadUrl: {
    type: String
  },
  fileInfo: {
    type: Object
  }
})

const download = async () => {
  const res = await createDownloadUrl(props.createDownloadUrl)
  if (!res) return
  window.location.href = props.downloadUrl + '/' + res.code
}
</script>
<style lang="scss" scoped>
.other-previewer {
  width: 100%;
  height: 100%;
  padding: 80px 140px;
  display: -ms-flexbox;
  display: flex;
  -ms-flex-direction: column;
  flex-direction: column;
  -ms-flex-align: center;
  align-items: center;
  .file-icon {
    margin-top: 220px;
  }
  .filename {
    font-size: 14px;
    line-height: 1.5;
    margin-top: 24px;
    text-align: center;
    color: rgb(37, 38, 43);
    font-weight: 500;
  }
  .desc {
    font-size: 12px;
    line-height: 1.6;
    margin-top: 8px;
    color: rgba(37, 38, 43, 0.36);
  }
  .download {
    height: 36px;
    margin-top: 40px;
  }
}
</style>
