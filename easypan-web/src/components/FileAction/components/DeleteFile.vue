<template>
  <Dialog
    :show="dialogConfig.show"
    :title="dialogConfig.title"
    :buttons="dialogConfig.buttons"
    :show-cancel="true"
    width="400px"
    @close="dialogConfig.show = false"
  >
    <div class="delete-info">
      <Icon icon-name="warning" :width="40" />
      <div class="info">
        <span class="text">确定删除所选的文件吗？</span>
        <span class="text">删除的文件可在 10天 内通过回收站还原</span>
      </div>
    </div>
  </Dialog>
</template>

<script>
export default {
  name: 'DeleteFileDialog'
}
</script>
<script setup>
import Dialog from '@/components/Dialog.vue'
import Icon from '@/components/Icon.vue'
import { ElMessage } from 'element-plus'
import { isArray } from '@/utils/is'
import { reactive } from 'vue'
import { delFileApi } from '@/api/file'

const emit = defineEmits(['delete-success'])
let delIds = ''
const dialogConfig = reactive({
  show: false,
  title: '确定删除',
  showCancel: true,
  buttons: [
    {
      type: 'primary',
      text: '删除',
      click: (e) => {
        deleteFile()
      }
    }
  ]
})

function show(data) {
  if (isArray(data)) {
    delIds = data.map((item) => item.id).join(',')
  } else {
    delIds = data.id
  }
  dialogConfig.show = true
}

const deleteFile = async () => {
  try {
    await delFileApi(delIds)
    emit('delete-success')
  } catch (error) {
    ElMessage.success('失败成功')
  }
  dialogConfig.show = false
}
defineExpose({
  show
})
</script>
<style lang="scss" scoped>
.delete-info {
  display: flex;
  align-items: center;
  .info {
    display: flex;
    flex-direction: column;
    margin-left: 16px;
  }
}
</style>
