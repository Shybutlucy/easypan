<template>
  <div>
    <Dialog
      :show="dialogConfig.show"
      :title="dialogConfig.title"
      :buttons="dialogConfig.buttons"
      width="400px"
      :showCancel="false"
      @close="dialogConfig.show = false"
    >
      <el-form ref="formDataRef" :model="formData" label-width="80px" :rules="rules">
        <el-form-item label="新密码" prop="password">
          <el-input
            show-password
            type="password"
            placeholder="请输入密码"
            v-model.trim="formData.password"
            :prefix-icon="LockOutlined"
            maxLength="20"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="rePassword">
          <el-input
            show-password
            type="password"
            placeholder="请再次输入密码"
            v-model.trim="formData.rePassword"
            :prefix-icon="LockOutlined"
            maxLength="20"
          />
        </el-form-item>
      </el-form>
    </Dialog>
  </div>
</template>

<script>
export default {
  name: 'UploadPassword'
}
</script>
<script setup>
import { nextTick, reactive, ref } from 'vue'
import Verify from '@/utils/Verify'
import Dialog from './Dialog.vue'
import { updatePassword } from '@/api/user'
import { LockOutlined } from '@vicons/antd'

const checkRePassword = (rule, value, callback) => {
  if (value !== formData.value.password) {
    callback(new Error(rule.message))
  } else {
    callback()
  }
}
const rules = reactive({
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { validator: Verify.password, message: '密码只能是数字, 字母, 特殊字符8-18位', trigger: 'blur' }
  ],
  rePassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: checkRePassword, message: '两次密码不一致', trigger: 'blur' }
  ]
})

const formData = ref({})
const formDataRef = ref()
const dialogConfig = reactive({
  show: false,
  title: '修改密码',
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

const show = () => {
  dialogConfig.show = true
  nextTick(() => {
    formDataRef.value.resetFields()
    formData.value = {}
  })
}
defineExpose({ show })

const submitForm = () => {
  formDataRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }
    await updatePassword({ password: formData.value.password })
    dialogConfig.show = false
  })
}
</script>
<style lang="scss" scoped></style>
