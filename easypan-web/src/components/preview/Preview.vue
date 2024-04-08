<template>
  <div>
    <PreviewImage
      ref="previewImageRef"
      :image-list="[imageUrl]"
      v-if="fileInfo.fileCategory == FileCategory.IMAGE"
    />
    <Window
      v-else
      :show="windowShow"
      @close="closeWindow"
      :width="fileInfo.fileCategory == FileCategory.VIDEO ? 1200 : 900"
      :title="fileInfo.filename"
      :align="fileInfo.fileCategory == FileCategory.VIDEO ? 'center' : 'top'"
    >
      <PreviewVideo :url="url" v-if="fileInfo.fileCategory == FileCategory.VIDEO" />
      <PreviewDoc
        :url="url"
        :type="fileInfo.fileType"
        v-if="fileInfo.fileType == 4 || fileInfo.fileType == 5 || fileInfo.fileType == 6"
      />
      <PreviewCode
        :url="url"
        :language="language"
        v-if="fileInfo.fileType == 8 || fileInfo.fileType == 7"
      />
      <PreviewAudio :url="url" v-if="fileInfo.fileCategory == FileCategory.MUSIC" />
      <PreviewOther
        :downloadUrl="downloadUrl"
        :createDownloadUrl="createDownloadUrl"
        :file-info="fileInfo"
        v-if="fileInfo.fileCategory == FileCategory.OTHER"
      />
    </Window>
  </div>
</template>

<script>
export default {
  name: 'Preview'
}
</script>
<script setup>
import { nextTick, ref, computed } from 'vue'
import PreviewImage from './components/Image.vue'
import { FileCategory } from '@/enums/fileCategoryEnum'
import Window from '@/components/Window.vue'
import PreviewVideo from './components/Video.vue'
import fileSetting from '@/settings/fileSetting'
import PreviewDoc from './components/Doc.vue'
import PreviewCode from './components/Code.vue'
import { ElMessage } from 'element-plus'
import PreviewOther from './components/Other.vue'
import PreviewAudio from './components/Audio.vue'
import { useGlobalSetting } from '@/hooks/setting'

const { fileUrlMap } = fileSetting
const url = ref(null)
const downloadUrl = ref(null)
const createDownloadUrl = ref(null)
const previewImageRef = ref()
const fileInfo = ref({})
const windowShow = ref(false)
const language = ref(null)

const globalSetting = useGlobalSetting()
const imgUrl = globalSetting.imgUrl
const imageUrl = computed(() => {
  return imgUrl + fileInfo.value.fileCover?.replaceAll('_.', '.')
})

function showPreview(data, showPart) {
  fileInfo.value = data
  language.value = data.filename.substring(data.filename.lastIndexOf('.') + 1)
  if (/^doc$/.test(data.filename)) {
    ElMessage.info('抱歉，该类型文件暂不支持预览')
    return
  }
  if (FileCategory.IMAGE == data.fileCategory) {
    nextTick(() => {
      previewImageRef.value.show(0)
    })
  } else {
    windowShow.value = true
    let _url = fileUrlMap[showPart].fileUrl
    let _createDownloadUrl = fileUrlMap[showPart].createDownloadUrl
    let _downloadUrl = fileUrlMap[showPart].downloadUrl
    if (FileCategory.VIDEO == data.fileCategory) {
      _url = fileUrlMap[showPart].videoUrl
    }
    if (showPart == 0) {
      _url = _url + '/' + data.id
      _createDownloadUrl = _createDownloadUrl + '/' + data.id
      downloadUrl.value = _downloadUrl
    } else if (showPart == 1) {
      _url = _url + '/' + data.userId + '/' + data.id
      _createDownloadUrl = _createDownloadUrl + '/' + data.userId + '/' + data.id
    }
    url.value = _url
    downloadUrl.value = _downloadUrl
    createDownloadUrl.value = _createDownloadUrl
  }
}

function closeWindow() {
  windowShow.value = false
}
defineExpose({ showPreview })
</script>
<style lang="scss" scoped></style>
