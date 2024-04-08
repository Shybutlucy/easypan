<template>
  <div class="uploader-panel">
    <div class="uploader-header">
      <span>上传完成（{{ successNum }}/{{ fileList.length }}）</span>
    </div>
    <div class="uploader-body">
      <ul class="file-list">
        <li v-for="(item, index) in fileList" class="file-item">
          <div class="file-info">
            <div class="file-icon">
              <img
                src="https://staticwx.cdn.bcebos.com/mini-program/images/ic_image_v2.png"
                alt=""
              />
            </div>
            <div class="file-progress">
              <div
                :class="[
                  'name-text',
                  !showProgress(item.status) || item.cancel ? 'no-progress' : ''
                ]"
              >
                {{ item.filename }}
              </div>
              <el-progress
                :percentage="item.progress"
                :stroke-width="3"
                striped
                :show-text="false"
                striped-flow
                :duration="20"
                v-show="showProgress(item.status) && !item.cancel"
              ></el-progress>
              <div
                :class="['status', !showProgress(item.status) || item.cancel ? 'no-progress' : '']"
              >
                <span class="file-status" v-show="item == STATUS.init.value">
                  <span class="error">
                    <span class="cursor-p">{{ STATUS.init.desc }}</span>
                  </span>
                </span>
                <div class="file-size">{{ size2Str(item.totalSize) }}</div>
                <span class="speed"></span>
              </div>
            </div>
          </div>
          <div class="file-operate">
            <span
              title="所在文件夹"
              class="common-icon-container"
              v-show="
                item.status == STATUS.upload_finish.value ||
                item.status == STATUS.upload_seconds.value
              "
            >
              <Icon iconName="open_folder" custom-class="img" :width="28" />
            </span>
            <span
              title="暂停"
              class="common-icon-container"
              v-show="item.status == STATUS.uploading.value && !item.pause && !item.cancel"
              @click="pauseUpload(item.uid)"
            >
              <Icon iconName="pause" custom-class="img" :width="28" />
            </span>
            <span
              title="继续上传"
              class="common-icon-container"
              v-show="item.status == STATUS.uploading.value && item.pause"
              @click="continueUpload(item.uid)"
            >
              <Icon iconName="reupload" custom-class="img" :width="28" />
            </span>
            <span
              title="重试"
              class="common-icon-container"
              v-show="item.cancel"
              @click="retryUpload(item.uid)"
            >
              <Icon iconName="retry" custom-class="img" :width="28" />
            </span>
            <span
              title="取消"
              class="common-icon-container"
              v-show="
                (item.status == STATUS.uploading.value || item.status == STATUS.init.value) &&
                !item.cancel
              "
              @click="cancelUpload(item.uid)"
            >
              <Icon iconName="cancel" custom-class="img" :width="28" />
            </span>
          </div>
        </li>
      </ul>
      <span class="always-tips">- 仅展示本次上传任务 -</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'FileUploader',
  components: { Icon }
}
</script>
<script setup>
import { computed, ref } from 'vue'
import { STATUS } from '@/enums/statusEnum'
import Icon from '@/components/Icon.vue'
import { size2Str } from '@/utils'
import SparkMD5 from 'spark-md5'
import { uploadFile } from '@/api/file'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['uploadCallback'])

const chunkSize = 1024 * 1024 * 5
const fileList = ref([])
const delList = ref([])
const successNum = computed(() => {
  return fileList.value.filter((item) => {
    return item.status == STATUS.upload_finish.value || item.status == STATUS.upload_seconds.value
  }).length
})
function showProgress(status) {
  return status == STATUS.uploading.value || status == STATUS.init.value
}

const addFile = async (file, filePid) => {
  const fileItem = {
    file: file,
    uid: file.uid,
    md5: null,
    filename: file.name,
    status: STATUS.init.value,
    uploadSize: 0,
    totalSize: file.size,
    progress: 0,
    pause: false,
    cancel: false,
    chunkIndex: 0,
    filePid: filePid,
    errorMsg: null
  }
  fileList.value.unshift(fileItem)
  if (fileItem.totalSize == 0) {
    fileItem.status = STATUS.empty.value
    return
  }
  let md5FileUid = await computeMD5(fileItem)
  if (md5FileUid == null) {
    return
  }
  fileUpload(md5FileUid)
}

defineExpose({ addFile })

async function computeMD5(fileItem) {
  let file = fileItem.file
  let blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice
  let chunks = Math.ceil(file.size / chunkSize)
  let currentChunk = 0
  let spark = new SparkMD5.ArrayBuffer()
  let fileReader = new FileReader()

  let loadNext = () => {
    let start = currentChunk * chunkSize
    let end = start + chunkSize >= file.size ? file.size : chunkSize
    fileReader.readAsArrayBuffer(blobSlice.call(file, start, end))
  }
  loadNext()

  return new Promise((resolve, reject) => {
    let resultFile = getFileByUid(file.uid)
    resultFile.status = STATUS.init.value
    fileReader.onload = (e) => {
      spark.append(e.target.result)
      currentChunk++
      if (currentChunk < chunks) {
        // console.log(`第${file.name}, ${currentChunk}分片解析完成，开始第${currentChunk+1}`)
        let percent = Math.floor((currentChunk / chunks) * 100)
        resultFile.progress = percent
        loadNext()
      } else {
        let md5 = spark.end()
        spark.destroy()
        resultFile.progress = 100
        resultFile.md5 = md5
        // setTimeout(() => {
        resultFile.progress = 0
        resultFile.status = STATUS.uploading.value
        // }, 500)
        resolve(fileItem.uid)
      }
    }
    fileReader.onerror = () => {
      resultFile.progress = -1
      resultFile.status = STATUS.fail.value
      resolve(fileItem.uid)
    }
  }).catch((error) => {
    console.log(error)
    return null
  })
}
// 获取文件
const getFileByUid = (uid) => {
  let file = fileList.value.find((item) => {
    return item.file.uid == uid
  })
  return file
}

const fileUpload = async (uid, chunkIndex) => {
  chunkIndex = chunkIndex ? chunkIndex : 0
  // 分片上传
  let currentFile = getFileByUid(uid)
  const file = currentFile.file
  const fileSize = currentFile.totalSize
  const chunks = Math.ceil(fileSize / chunkSize)
  for (let i = chunkIndex; i < chunks; i++) {
    let delIndex = delList.value.indexOf(uid)
    if (delIndex != -1) {
      delList.value.splice(delIndex, 1)
      break
    }
    currentFile = getFileByUid(uid)
    if (currentFile.pause) {
      break
    }
    if (currentFile.cancel) {
      break
    }
    let start = i * chunkSize
    let end = start + chunkSize >= fileSize ? fileSize : start + chunkSize
    let chunkFile = file.slice(start, end)
    try {
      let result = await uploadFile(
        {
          file: chunkFile,
          filename: file.name,
          fileMd5: currentFile.md5,
          chunkIndex: i,
          chunks: chunks,
          id: currentFile.id,
          filePid: currentFile.filePid
        },
        {
          onUploadProgress: (progress) => {
            let loaded = progress.loaded
            if (loaded > fileSize) {
              loaded = fileSize
            }
            currentFile.uploadSize = i * chunkSize + loaded
            currentFile.progress = Math.floor((currentFile.uploadSize / fileSize) * 100)
          }
        }
      )
      currentFile.id = result.id
      currentFile.status = STATUS[result.status].value
      currentFile.chunkIndex = i
      if (
        result.status == STATUS.upload_seconds.value ||
        result.status == STATUS.upload_finish.value
      ) {
        ElMessage.success('文件上传成功～')
        currentFile.progress = 100
        emit('uploadCallback')
        break
      }
    } catch (error) {
      console.log(error)
      currentFile.status = STATUS.fail.value
    }
  }
}

// 暂停上传
function pauseUpload(uid) {
  getFileByUid(uid).pause = true
}
// 继续上传
function continueUpload(uid) {
  const currentFile = getFileByUid(uid)
  currentFile.pause = false
  fileUpload(currentFile.uid, currentFile.chunkIndex + 1)
}

// 取消上传
function cancelUpload(uid) {
  getFileByUid(uid).cancel = true
}

// 重试
function retryUpload(uid) {
  const currentFile = getFileByUid(uid)
  currentFile.cancel = false
  fileUpload(currentFile.uid)
}
</script>
<style lang="scss" scoped>
.uploader-panel {
  height: 408px;
  width: 560px;
  font-size: 12px;
  .uploader-header {
    font-size: 14px;
    font-weight: 600;
    border-bottom-width: 1px;
    border-color: #f4f4f4;
    border-bottom-style: solid;
    height: 40px;
    line-height: 24px;
    padding: 8px 16px;
  }
  .uploader-body {
    position: relative;
    overflow: hidden;
    overflow-y: auto;
    height: 349px;
    .file-list .file-item {
      position: relative;
      border-bottom: 1px solid #f2f6fd;
      height: 72px;
      line-height: 72px;
      .file-info {
        position: relative;
        overflow: hidden;
        width: 416px;
        height: 72px;
        white-space: nowrap;
        text-overflow: ellipsis;
        display: inline-block;
        .file-icon {
          position: absolute;
          top: 15px;
          left: 10px;
          width: 40px;
          height: 40px;
          img {
            width: 100%;
            height: 100%;
            position: absolute;
            right: 0;
            border: 0;
            vertical-align: middle;
          }
        }
        .file-progress {
          display: inline-block;
          position: absolute;
          left: 56px;
          line-height: 1;
          width: 360px;
          .name-text {
            display: block;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            font-size: 14px;
            color: #03081a;
            padding: 10px 0;
          }
          .name-text.no-progress {
            padding: 19px 0 0 0;
          }
          .status {
            padding-top: 10px;
            position: relative;
            .file-status {
              overflow: hidden;
              text-overflow: ellipsis;
              color: #ff436a;
              vertical-align: bottom;
              .error {
                display: inline-block;
                padding-right: 8px;
              }
            }
            .file-size {
              font-size: 12px;
              color: #878c9c;
              display: inline-block;
            }
            .speed {
              position: absolute;
              right: 8px;
              color: #06a7ff;
            }
          }
          .status.no-progress {
            padding-top: 6px;
          }
        }
      }
      .file-operate {
        display: inline-block;
        padding: 0;
        padding-left: 8px;
        width: calc(100% - 416px);
        display: inline-block;
        height: 72px;
        .common-icon-container {
          background: #f0faff;
          border-radius: 13.28px;
          display: inline-block;
          width: 28px;
          height: 28px;
          position: relative;
          color: #06a7ff;
          margin-left: 12px;
          cursor: pointer;
          .img {
            position: absolute;
            top: 0;
            left: 0;
          }
        }
      }
    }
    .always-tips {
      text-align: center;
      width: 100%;
      display: inline-block;
      color: #afb3bf;
      margin-bottom: -12px;
    }
  }
}
</style>
