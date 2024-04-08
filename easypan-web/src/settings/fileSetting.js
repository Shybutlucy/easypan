export default {
  fileTypeMap: {
    0: { desc: '目录', icon: 'folder' },
    1: { desc: '视频', icon: 'video' },
    2: { desc: '音频', icon: 'music' },
    3: { desc: '图片', icon: 'image' },
    4: { desc: 'pdf', icon: 'pdf' },
    5: { desc: 'doc', icon: 'word' },
    6: { desc: 'excel', icon: 'excel' },
    7: { desc: '纯文本', icon: 'txt' },
    8: { desc: '代码', icon: 'code' },
    9: { desc: '压缩包', icon: 'zip' },
    10: { desc: '其他文件', icon: 'others' }
  },
  fileUrlMap: {
    0: {
      fileUrl: '/file/getFile',
      videoUrl: '/api/file/ts/getVideoInfo',
      createDownloadUrl: '/file/createDownloadUrl',
      downloadUrl: '/api/file/download'
    },
    1: {
      fileUrl: '/file/getFile',
      videoUrl: '/api/file/ts/getVideoInfo',
      createDownloadUrl: '/file/createDownloadUrl',
      downloadUrl: '/api/file/download'
    }
  }
}
