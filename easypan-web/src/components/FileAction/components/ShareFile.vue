<template>
  <Dialog
    :show="dialogConfig.show"
    :title="dialogConfig.title"
    :buttons="dialogConfig.buttons"
    :show-cancel="true"
    width="600px"
    @close="dialogConfig.show = false"
  >
    <div class="share-container" v-loading="loading">
      <el-form :model="shareForm" ref="shareFormRef" v-show="showType == 0">
        <el-form-item label="有效期">
          <el-radio-group v-model="shareForm.validType">
            <el-radio v-for="item in validOption" :label="item.label">{{ item.value }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="提取码">
          <div style="width: 400px">
            <el-radio-group v-model="shareForm.codType" @change="clearValidStatus">
              <el-radio label="0">系统随机生成验证码</el-radio>
              <el-radio label="1" style="margin-top: 10px">
                <el-input
                  v-model="shareForm.code"
                  placeholder="请输入4位提取码"
                  :disabled="shareForm.codType !== '1'"
                  style="width: 160px"
                  @change="validateCode"
                />
                <span :class="['tips', tips.type]">{{ tips.msg }}</span>
              </el-radio>
            </el-radio-group>
            <div>
              <el-checkbox
                v-model="shareForm.withCode"
                label="分享链接自动填充提取码"
                size="large"
              />
              <el-tooltip effect="dark" content="" placement="right">
                <template #content>
                  开启后分享链接会带上提取码，<br />
                  用户点击链接可自动填充提取码
                </template>
                <el-icon style="margin-left: 6px"><ExclamationCircleOutlined /></el-icon>
              </el-tooltip>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <div class="share-success" v-show="showType == 1">
        <div class="msg">
          <el-icon size="16"><CheckCircleFilled /> </el-icon>
          <span class="text">成功创建分享链接，访问者无需提取码可直接查看分享文件</span>
        </div>
        <div class="link">
          <el-input :value="shareUrl + resultInfo.id" readonly />
          <div class="mask"></div>
        </div>
        <div class="link-code">
          <span class="label">提取码</span>
          <div class="inline"><el-input :value="resultInfo.code" readonly /></div>
        </div>
        <div class="tip">
          链接<span>{{ validOption[shareForm.validType].value }}</span>
          <template v-if="shareForm.validType != 3">后失效</template>
        </div>
      </div>
    </div>
  </Dialog>
</template>

<script>
export default {
  name: 'ShareFile'
}
</script>
<script setup>
import { reactive, ref, nextTick } from 'vue'
import Dialog from '@/components/Dialog.vue'
import useClipboard from 'vue-clipboard3'
import { ExclamationCircleOutlined, CheckCircleFilled } from '@vicons/antd'
import { ElMessage } from 'element-plus'
import { isArray } from '@/utils/is'
import { shareFileApi } from '@/api/share'

const validOption = [
  { label: '0', value: '1天' },
  { label: '1', value: '7天' },
  { label: '2', value: '30天' },
  { label: '3', value: '永久有效' }
]

const { toClipboard } = useClipboard()
// 0:分享表单 1:分享结果
const showType = ref(0)
const currentFile = ref({})
const loading = ref(false)
const shareFormRef = ref()
const tips = reactive({
  type: 'info',
  msg: '仅支持数字及英文字母'
})
const shareUrl = ref(document.location.origin + '/share/')
const resultInfo = reactive({})
const shareForm = reactive({
  validType: '1',
  codType: '0',
  code: '',
  withCode: false
})
const dialogConfig = reactive({
  show: false,
  title: '分享文件: ',
  showCancel: true,
  buttons: [
    {
      type: 'primary',
      text: '创建链接',
      isShow: true,
      click: (e) => {
        createShareLink()
      }
    },
    {
      type: 'success',
      text: '复制链接及提取码',
      isShow: false,
      click: (e) => {
        copy2Clipboard()
      }
    }
  ]
})

function show(data) {
  if (isArray(data)) return
  showType.value = 0
  dialogConfig.show = true
  nextTick(() => {
    shareFormRef.value.resetFields()
    showType.value = 0
    dialogConfig.buttons[0].isShow = true
    dialogConfig.buttons[1].isShow = false
  })
  dialogConfig.title = '分享文件: ' + data.filename
  currentFile.value = data
}

function clearValidStatus() {
  tips.type = 'info'
  tips.value = '仅支持数字及英文字母'
  shareForm.code = ''
}

function validateCode() {
  if (/^[A-Za-z\d]{4}$/.test(shareForm.code)) {
    tips.type = 'info'
    tips.value = '仅支持数字及英文字母'
  } else {
    tips.type = 'error'
    tips.value = '仅可输入4位字母和数字'
  }
}

const createShareLink = async () => {
  loading.value = true
  const param = {
    fileId: currentFile.value.id,
    validType: shareForm.validType,
    code: shareForm.code
  }
  try {
    const res = await shareFileApi(param)
    if (!res) return
    Object.assign(resultInfo, res)
    showType.value = 1
    dialogConfig.buttons[0].isShow = false
    dialogConfig.buttons[1].isShow = true
    loading.value = false
  } catch (error) {
    // 分享失败
  }
}

const copy2Clipboard = async () => {
  // 链接: https://pan.baidu.com/s/1OtdlYyBNGL0NPpIiLgyG3A 提取码: 93vd
  await toClipboard(`链接: ${shareUrl.value}${resultInfo.id} 提取码: ${resultInfo.code}`)
  ElMessage.success('复制成功')
}

defineExpose({ show })
</script>
<style lang="scss" scoped>
.share-container {
  .tips {
    margin-left: 8px;
    font-size: 14px;
    font-weight: 500;
    &.info {
      color: #818999;
    }
    &.error {
      color: #ff436a;
    }
  }
  .share-success {
    .msg {
      color: #06a7ff;
      font-size: 16px;
      .text {
        font-size: 12px;
        margin-left: 4px;
      }
    }
    .link {
      margin-top: 19px;
      width: 295px;
      position: relative;
      .mask {
        position: absolute;
        bottom: 1px;
        right: 1px;
        height: 30px;
        width: 66px;
        border-radius: 4px;
        background: linear-gradient(90deg, rgba(245, 246, 250, 0.0001) -5.23%, #fff 82.68%);
      }
    }
    .link-code {
      margin-top: 16px;
      .label {
        font-size: 12px;
      }
      .inline {
        display: inline-block;
        margin-left: 8px;
        width: 94px;
      }
    }
    .tip {
      margin-top: 16px;
      font-size: 12px;
      color: #4f526c;
      line-height: 17px;
      span {
        color: #06a7ff;
        vertical-align: baseline;
      }
    }
  }
}
</style>
