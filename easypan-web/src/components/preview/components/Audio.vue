<template>
  <div class="audio-previewer">
    <div class="audio-previewer-container">
      <div class="audio-visualizer">
        <div class="cover">
          <Icon class="cover-blur" icon-name="audio_cover" :width="256" />
          <Icon class="cover" icon-name="audio_cover" :width="256" />
        </div>
      </div>
      <div class="audio-player">
        <audio
          ref="audio"
          @play="playFunc"
          @pause="pauseFunc"
          @timeupdate="timeupdateFunc"
          @canplay="canplayFunc"
          @error="errorFunc"
          :src="audioSrc"
        ></audio>
        <div class="audioControl" style="height: 68px">
          <div @mousedown="isDraging = true" @mouseup="isDraging = false" class="progress-bar">
            <span class="time"> {{ formatCurrentTime }} </span>
            <el-slider
              v-model="playProcess"
              class="progress"
              style="height: 14px"
              @change="setProcessFunc"
              :format-tooltip="formatTooltip"
            ></el-slider>
            <span class="time">{{ formatDuration }}</span>
          </div>
          <div class="action-bar">
            <div class="action left">
              <span class="audioSpan">
                <button
                  :class="['button', isPlaying ? 'paused' : 'playing']"
                  @click="clickFunc(isPlaying ? 'pause' : 'play')"
                ></button>
              </span>
              <div class="volume">
                <span class="icon-btn" @click="clickFunc('mute')">
                  <i v-if="playVolume == 0" class="iconfont icon-volume-off" aria-hidden="true"></i>
                  <i
                    v-else-if="playVolume <= 50"
                    class="iconfont icon-volume"
                    aria-hidden="true"
                  ></i>
                  <i
                    v-else="playVolume > 50"
                    class="iconfont icon-volume-high"
                    aria-hidden="true"
                  ></i>
                </span>
                <el-slider
                  v-model="playVolume"
                  class="progress"
                  :format-tooltip="formatTooltipVol"
                  @input="setVolumeFunc"
                ></el-slider>
              </div>
            </div>
            <div class="action right">
              <div class="speed">
                <el-dropdown trigger="click" @command="itemSelected">
                  <span class="icon-btn"
                    ><i class="iconfont icon-speed" aria-hidden="true"></i
                  ></span>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item
                        v-for="(item, index) in speedOption"
                        :key="index"
                        :command="item.value"
                        :class="{ selected: playSpeed == item.value }"
                        >{{ item.label }}</el-dropdown-item
                      >
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'PreviewAudio'
}
</script>
<script setup>
import { computed, onMounted, ref } from 'vue'
import { isNumber } from '@/utils/is'
import Icon from '@/components/Icon.vue'
import { WindowsOutlined } from '@vicons/antd'

const props = defineProps({
  url: {
    type: String
  }
})

const speedOption = [
  { value: 0.75, label: '0.75 倍' },
  { value: 1, label: '1 倍' },
  { value: 1.25, label: '1.25 倍' },
  { value: 1.5, label: '1.5 倍' },
  { value: 1.75, label: '1.75 倍' },
  { value: 2, label: '2 倍' }
]

const audio = ref()
const audioSrc = ref('')
const isPlaying = ref(false)
const isError = ref(false)
const playProcess = ref(0)
const playVolume = ref(50)
const tempVolume = ref(playVolume.value)
const playSpeed = ref(1)
const totalTimes = ref('00:00')
const currentTime = ref('00:00')
const isDraging = ref(false)

function toStringFunc(data) {
  if (!isNumber(data)) return '00:00'
  if (data < 10) {
    return `0${data}`
  }
  return data
}

const formatDuration = computed(() => {
  if (typeof totalTimes.value == 'number') {
    let fTotal = Math.floor(totalTimes.value)
    let tempHour = Math.floor(fTotal / 3600)
    let tempMin = Math.floor(fTotal / 60) % 60
    let tempSec = fTotal % 60
    return `${toStringFunc(tempHour)}:${toStringFunc(tempMin)}:${toStringFunc(tempSec)}`
  }
  return '00:00:00'
})

const formatCurrentTime = computed(() => {
  if (typeof currentTime.value == 'number') {
    let fTotal = Math.floor(currentTime.value)
    let tempHour = Math.floor(fTotal / 3600)
    let tempMin = Math.floor(fTotal / 60) % 60
    let tempSec = fTotal % 60
    return `${toStringFunc(tempHour)}:${toStringFunc(tempMin)}:${toStringFunc(tempSec)}`
  }
  return '00:00:00'
})

function itemSelected(speed) {
  playSpeed.value = speed
  audio.value.playbackRate = speed
}

function downloadAudioFile() {
  if (!props.url) return
  const xhr = new XMLHttpRequest()
  xhr.open('GET', `/api/${props.url}`)
  xhr.responseType = 'blob'
  xhr.onload = function (e) {
    if (this.status == 200) {
      let blob = new Blob([this.response], { type: 'audio/mpeg' })
      if (window.URL) {
        audioSrc.value = window.URL.createObjectURL(blob)
      } else if (WindowsOutlined.webkitURL) {
        audioSrc.value = window.webkitURL.createObjectURL(blob)
      } else {
        audioSrc.value = blob
      }
    }
  }
  xhr.send()
}

function mousedownFunc() {
  isDraging.value = true
}
function mouseupFunc() {
  isDraging.value = false
}
function formatTooltip(val) {
  let currTime = Math.floor((val / 100) * totalTimes.value)
  let tempMin = Math.floor(currTime / 60)
  let tempSec = currTime % 60
  let min, sec
  return `${toStringFunc(tempMin)}:${toStringFunc(tempSec)}`
}
function formatTooltipVol(vol) {
  return Math.round(vol) + '%'
}
function clickFunc(val) {
  if (val == 'play') {
    isPlaying.value = !isPlaying.value
    audio.value.play()
  } else if (val == 'pause') {
    isPlaying.value = !isPlaying.value
    audio.value.pause()
  } else if (val == 'refresh') {
    audio.value.playbackRate = 1
    playSpeed.value = 1
    audio.value.load()
    audio.value.play()
  } else if (val == 'mute') {
    if (playVolume.value != 0) {
      tempVolume.value = playVolume.value
      playVolume.value = 0
      setVolumeFunc(0)
    } else {
      playVolume.value = tempVolume.value
      setVolumeFunc(playVolume.value)
    }
  }
}
function setProcessFunc(val) {
  audio.value.currentTime = Math.round((val / 100) * totalTimes.value)
  audio.value.play()
}
function setVolumeFunc(vol) {
  //设置播放音量，audio音量范围[0,1]
  audio.value.volume = Math.round((vol / 100) * 10) / 10
}
function playFunc(e) {
  isPlaying.value = true
}
function pauseFunc(e) {
  isPlaying.value = false
}
function timeupdateFunc(e) {
  if (!isDraging.value) {
    playProcess.value = (e.target.currentTime / totalTimes) * 100
    currentTime.value = e.target.currentTime
  }
}
function canplayFunc(e) {
  isError.value = false
  audio.value.volume = 0.5
  totalTimes.value = e.target.duration // 无
}
function errorFunc(e) {
  isError.value = true
}

onMounted(() => {
  downloadAudioFile()
})
</script>
<style scoped lang="scss">
:deep(.el-dropdown-menu__item.selected) {
  background-color: #ecf5ff;
  color: #409eff;
}
.audio-previewer {
  padding: 80px 140px;
  width: 100%;
  height: 100%;
  -webkit-transition: padding 0.66s cubic-bezier(0.66, 0, 0.01, 1);
  -o-transition: padding 0.66s cubic-bezier(0.66, 0, 0.01, 1);
  transition: padding 0.66s cubic-bezier(0.66, 0, 0.01, 1);
  .audio-visualizer {
    position: relative;
    margin-bottom: 100px;
    .cover,
    .cover-blur {
      position: absolute;
      left: 50%;
      -webkit-transform: translate3d(-50%, -50%, 0);
      transform: translate3d(-50%, -50%, 0);
      width: 100%;
      height: 100%;
      background-size: cover;
      border-radius: 5px;
    }
    .cover {
      top: 50%;
      -webkit-box-shadow: 0 0 1px 1px rgba(0, 0, 0, 0.05), 0 2px 8px rgba(28, 28, 32, 0.06),
        0 16px 48px rgba(28, 28, 32, 0.2);
      box-shadow: 0 0 1px 1px rgba(0, 0, 0, 0.05), 0 2px 8px rgba(28, 28, 32, 0.06),
        0 16px 48px rgba(28, 28, 32, 0.2);
    }
    .cover-blur {
      top: calc(50% + 20px);
      -webkit-filter: blur(60px);
      filter: blur(60px);
      opacity: 0.4;
    }
  }
  .audio-previewer-container {
    position: relative;
    display: -ms-flexbox;
    display: flex;
    -ms-flex-direction: column;
    flex-direction: column;
    -ms-flex-pack: center;
    justify-content: center;
    -ms-flex-align: center;
    align-items: center;
    width: 100%;
    height: 100%;
    .audio-player {
      position: absolute;
      height: 68px;
      width: 480px;
      min-height: 68px;
      border-radius: 10px;
      opacity: 1;
      -webkit-transition: opacity 0.3s ease;
      -o-transition: opacity 0.3s ease;
      transition: opacity 0.3s ease;
      left: 50%;
      -webkit-transform: translate(-50%);
      transform: translate(-50%);
      bottom: -40px;
      .audioControl {
        overflow: hidden;
        border-radius: 10px 10px 14px 14px;
        -webkit-box-shadow: 0 0 1px 1px rgba(28, 28, 32, 0.05), 0 8px 24px rgba(28, 28, 32, 0.12);
        box-shadow: 0 0 1px 1px rgba(28, 28, 32, 0.05), 0 8px 24px rgba(28, 28, 32, 0.12);
        background-color: rgb(49, 49, 54);
        .progress-bar {
          display: flex;
          align-items: center;
          padding: 8px 12px;
          .time {
            font-size: 10px;
            line-height: 1.6;
            color: #fff;
            -ms-flex-negative: 0;
            flex-shrink: 0;
          }
          .progress {
            flex-grow: 1;
            margin-left: 12px;
            margin-right: 12px;
          }
        }
        .action-bar {
          display: flex;
          align-items: center;
          justify-content: space-between;
          height: 36px;
          padding: 2px 8px 6px 6px;
          .action {
            display: flex;
            align-items: center;
            justify-content: space-between;
            .audioSpan {
              height: 28px;
              width: 28px;
              display: flex;
              align-items: center;
              justify-content: center;
            }
            .icon-btn {
              margin-right: 6px;
              cursor: pointer;
              .iconfont {
                color: #fff;
                font-size: 20px;
              }
            }
          }
          .volume {
            display: flex;
            align-items: center;
            .progress {
              margin-top: 1px;
              height: 14px;
              min-width: 64px;
            }
          }
        }
      }
    }
  }
}

// 自定义滑块样式
:deep(.el-slider__runway) {
  height: 4px;
  .el-slider__bar {
    height: 4px;
  }
}
:deep(.el-slider__button-wrapper) {
  height: 20px;
  width: 20px;
  top: -10px;
  .el-slider__button {
    width: 12px;
    height: 12px;
  }
}
.button {
  border: 0;
  background: transparent;
  box-sizing: border-box;
  width: 0;
  height: 16px;

  border-color: transparent transparent transparent #ffffff;
  transition: 100ms all ease;
  cursor: pointer;

  // play state
  border-style: solid;
  border-width: 8px 0 8px 14px;

  &.paused {
    border-style: double;
    border-width: 0px 0 0px 14px;
  }

  &:hover {
    border-color: transparent transparent transparent #ffffff;
  }
}
</style>
