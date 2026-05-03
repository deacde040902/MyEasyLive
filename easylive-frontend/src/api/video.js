import request from './request'

const videoApi = {
  // 获取视频列表
  getVideoList: (params) => {
    return request.post('/api/video/list', params)
  },
  // 获取视频详情
  getVideoDetail: (params) => {
    const videoId = params.videoId || params.video_id || params;
    return request.post(`/api/video/detail?videoId=${videoId}`)
  },
  // 获取视频分P列表
  getVideoEpisodes: (params) => {
    return request.post(`/api/video/episodes?videoId=${params.videoId}`)
  },
  // 获取视频的m3u8播放地址
  getM3u8Url: (params) => {
    return request.post(`/api/video/m3u8Url?episodeId=${params.episodeId}`)
  },
  // 搜索视频
  search: (params) => {
    return request.post('/api/video/search', params)
  },
  // 热词
  getSearchKeywordTop: () => {
    return request.post('/api/video/getSearchKeywordTop')
  },
  // 推荐视频
  getVideoRecommend: (params) => {
    return request.post('/api/video/getVideoRecommend', params)
  },
  // 热门视频
  loadHotVideoList: (params) => {
    return request.post('/api/video/loadHotVideoList', params)
  },
  // 发布视频
  publishVideo: (data) => {
    return request.post('/api/video/publish', data, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },
  // 更新视频
  updateVideo: (videoId, data) => {
    const config = {};
    if (data instanceof FormData) {
      config.headers = {
        'Content-Type': 'multipart/form-data'
      };
    }
    return request.put(`/api/video/update/${videoId}`, data, config)
  },
  // 删除视频
  deleteVideo: (videoId) => {
    return request.delete(`/api/video/delete/${videoId}`)
  },
likeVideo: (videoId) => {
    return request.post(`/userAction/like/${videoId}`)
},
unlikeVideo: (videoId) => {
    return request.delete(`/userAction/like/${videoId}`)
},
collectVideo: (videoId) => {
    return request.post(`/userCollection/collect/${videoId}`)
},
uncollectVideo: (videoId) => {
    return request.delete(`/userCollection/collect/${videoId}`)
},
  // 获取视频评论
  getVideoComments: (videoId, params) => {
    return request.post(`/comment/loadComment?videoId=${videoId}&pageNo=${params.pageNo}&pageSize=${params.pageSize}&orderType=${params.orderType || 'create_time'}`)
  },
  // 发表视频评论
  addComment: (videoId, content) => {
    return request.post(`/comment/postComment?videoId=${videoId}&content=${encodeURIComponent(content.content)}`)
  },
  // 获取大视频列表
  getBigVideos: () => {
    return request.post('/api/video/big/list')
  },
  // 获取弹幕列表
  getDanmakus: (videoId) => {
    return request.post(`/api/video/danmaku/list?videoId=${videoId}`)
  },
  // 添加弹幕
  addDanmaku: (videoId, content, playTime) => {
    return request.post(`/api/video/danmaku/add?videoId=${videoId}&content=${encodeURIComponent(content)}&playTime=${playTime}`)
  }
}

export default videoApi