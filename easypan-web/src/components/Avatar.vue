<template>
  <span class="avatar" :style="{ width: width + 'px', height: width + 'px' }">
    <img
      :src="avatar && avatar != '' ? avatar : `${avatarUrl}${userId}?${timestamp}`"
      alt=""
      srcset=""
      v-if="userId"
    />
  </span>
</template>
<script>
export default {
  name: 'Avatar'
}
</script>
<script setup>
import { useGlobalSetting } from '@/hooks/setting'

const globalSetting = useGlobalSetting()
const avatarUrl = globalSetting.avatarUrl
const props = defineProps({
  userId: {
    type: String
  },
  avatar: {
    type: String
  },
  timestamp: {
    type: Number,
    default: 0
  },
  width: {
    type: Number,
    default: 32
  }
})
</script>

<style lang="scss" scoped>
.avatar {
  display: flex;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  img {
    width: 100%;
    object-fit: cover;
  }
}
</style>
