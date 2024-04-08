<template>
  <div class="detail-main">
    <div class="detail-title">
      <span>分享详情</span>
    </div>
    <div class="muti-file" v-if="!selectFile && value.length > 1">
      <Icon icon-name="folder_big" :width="128" />
    </div>
    <div class="detail-content" v-if="selectFile">
      <div class="filename">
        <template
          v-if="(selectFile.fileType == 3 || selectFile.fileType == 1) && selectFile.status == 2"
        >
          <!-- 视频，图片，且转码成功才有封面 -->
          <Icon :cover="selectFile.fileCover" />
        </template>
        <template v-else>
          <Icon v-if="selectFile.folderType == 0" :file-type="selectFile.fileType" />
          <!-- 文件夹 -->
          <Icon v-else="selectFile.folderType == 1" icon-name="folder_2" />
        </template>
        <span class="text">{{ selectFile.filename }}</span>
      </div>
      <template v-for="item in details">
        <div class="content-item" v-if="item.label !== 'divider'">
          <div class="label">{{ item.label }}</div>
          <div class="value">{{ selectFile[item.key] || 0 }}</div>
        </div>
        <div class="divider" v-else></div>
      </template>
    </div>
    <div class="detail-empty" v-if="value.length === 0">
      <img
        src="https://nd-static.bdstatic.com/m-static/v20-main/home/img/empty-folder.55c81ea2.png"
        alt=""
      />
      <p>选中文件，查看详情</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ShareDetail'
}
</script>
<script setup>
import { computed } from 'vue'
import Icon from '@/components/Icon.vue'

const details = [
  { label: '分享时间', key: 'createTime' },
  { label: '有效期', key: 'expireTime' },
  { label: '提取码', key: 'code' },
  { label: 'divider', key: 'divider' },
  { label: '浏览', key: 'browseCount' },
  { label: '保存', key: 'saveCount' },
  { label: '下载', key: ' downloadCount' }
]

const props = defineProps({
  value: {
    type: Array,
    default: []
  }
})

const selectFile = computed(() => (props.value.length === 1 ? props.value[0] : null))
</script>
<style lang="scss" scoped>
.detail-main {
  padding: 24px;
  padding-top: 16px;
}
.detail-title {
  color: #03081a;
  font-weight: 600;
  padding-bottom: 15px;
}
.detail-empty {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 100%;
  text-align: center;
  -webkit-transform: translate(-50%, -50%);
  transform: translate(-50%, -50%);
  img {
    width: 120px;
    height: 120px;
    margin-bottom: 8px;
  }
  p {
    color: #818999;
    font-weight: 400;
    font-size: 12px;
    line-height: 12px;
  }
}
.muti-file {
  background-color: rgba(214, 220, 224, 0.15);
  width: 100%;
  min-height: 134px;
  border-radius: 13px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}
.detail-content {
  .filename {
    width: 100%;
    font-size: 14px;
  }
  .content-item {
    padding: 7px 0;
    line-height: 20px;
    .label {
      color: #878c9c;
    }
    .value {
      color: #03081a;
    }
  }
  .divider {
    margin: 7px 0;
    border-bottom: 1px solid #d4d7de;
  }
}
</style>
