<template>
  <Dialog
    :show="dialogConfig.show"
    :title="dialogConfig.title"
    :buttons="dialogConfig.buttons"
    width="500px"
    :showCancel="false"
    @close="dialogConfig.show = false"
  >
    <el-form ref="formDataRef" :model="formData" label-width="80px">
      <el-form-item label="昵称">
        {{ formData.nickname }}
      </el-form-item>
      <el-form-item label="头像上传">
        <AvatarUpload v-model="formData.avatar" />
      </el-form-item>
    </el-form>
  </Dialog>
</template>
<script>
export default {
  name: 'UploadAvatar'
}
</script>
<script setup>
import { reactive, ref } from 'vue'
import Dialog from './Dialog.vue'
import AvatarUpload from './AvatarUpload.vue'
import { updateUserAvatar } from '@/api/user'

const emit = defineEmits()

const dialogConfig = reactive({
  show: false,
  title: '修改头像',
  buttons: [
    {
      type: 'primary',
      text: '确定',
      click: (e) => {
        submitForm()
      }
    }
  ]
})
const formDataRef = ref()
const formData = ref({})

const show = (data) => {
  formData.value = Object.assign({}, data)
  formData.value.avatar = { userId: data.id, qqAvatar: data.avatar }
  dialogConfig.show = true
}
defineExpose({ show })

const submitForm = async () => {
  if (!(formData.value.avatar instanceof File)) {
    dialogConfig.show = false
    return
  }
  const params = new FormData()
  params.append('avatar', formData.value.avatar)
  await updateUserAvatar(params)
  dialogConfig.show = false
  emit('upload-success')
}
</script>
