<template>
  <span :class="customClass" :style="{ width: width + 'px', height: computedHeight }">
    <img :src="getImage()" :style="{ 'object-fit': fit }" />
  </span>
</template>

<script>
export default {
  name: 'Icon'
}
</script>
<script setup>
import { useGlobalSetting } from '@/hooks/setting'
import fileSetting from '@/settings/fileSetting'
import { isString } from '@/utils/is'
import { computed } from 'vue'

const props = defineProps({
  fileType: {
    type: Number
  },
  iconName: {
    type: String
  },
  cover: {
    type: String
  },
  width: {
    type: Number,
    default: 32
  },
  height: {
    type: [Number, String],
    default: null
  },
  fit: {
    type: String,
    default: 'cover'
  },
  customClass: {
    type: String,
    default: 'icon'
  }
})

const globalSetting = useGlobalSetting()
const imageUrl = globalSetting.imgUrl

const computedHeight = computed(() => {
  if (props.height) {
    if (isString(props.height)) {
      return props.height
    } else {
      return props.height + 'px'
    }
  } else {
    return props.width + 'px'
  }
})
const { fileTypeMap } = fileSetting
const getImage = () => {
  if (props.cover) {
    // 视频，图片封面
    return imageUrl + props.cover
  }
  let icon = 'unknow_icon'
  if (props.iconName) {
    icon = props.iconName
  } else {
    const iconMap = fileTypeMap[props.fileType]
    if (iconMap != undefined) {
      icon = iconMap['icon']
    }
  }
  return new URL(`/src/assets/icon-image/${icon}.png`, import.meta.url).href
}
</script>
<style lang="scss" scoped>
.icon {
  text-align: center;
  display: inline-block;
  border-radius: 3px;
  // overflow: hidden;
}
img {
  display: block;
  width: 100%;
  height: 100%;
}
</style>
