<template>
  <div class="video-previewer" v-show="isReady">
    <div class="video-previewer-container">
      <div ref="player" id="player"></div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PreviewWindow'
}
</script>
<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import DPlayer from 'dplayer'
import HLS from 'hls.js'

const props = defineProps({
  url: {
    type: String
  }
})

const db = ref()
const player = ref()
const isReady = ref(false)

const initPlayer = () => {
  db.value = new DPlayer({
    element: player.value,
    theme: '#FADFA3',
    autoplay: true,
    video: {
      url: props.url,
      type: 'customHls',
      customType: {
        customHls: function (video, player) {
          const hls = new HLS()
          hls.loadSource(props.url)
          hls.attachMedia(video)
        }
      }
    }
  })
  db.value.on('canplay', () => {
    isReady.value = true
    db.value.pause()
  })
}

onMounted(() => {
  initPlayer()
})

onUnmounted(() => {
  db.value.destroy()
})
</script>
<style lang="scss" scoped>
.video-previewer {
  padding: 80px 140px;
  width: 100%;
  height: 100%;
  position: relative;
  -webkit-transition: padding 0.66s cubic-bezier(0.66, 0, 0.01, 1);
  -o-transition: padding 0.66s cubic-bezier(0.66, 0, 0.01, 1);
  transition: padding 0.66s cubic-bezier(0.66, 0, 0.01, 1);
  .video-previewer-container {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
  }
}
</style>
