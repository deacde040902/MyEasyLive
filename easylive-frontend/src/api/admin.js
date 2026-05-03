import request from './request';

const adminApi = {
  // 管理员登录
  login: (data) => request.post('/admin/login', data),

  // 管理员退出
  logout: () => request.post('/admin/logout'),

  // 获取验证码
  getCheckCode: () => request.get('/admin/checkCode'),

  // 获取系统设置
  getSystemSettings: () => request.get('/admin/settings'),

  // 保存系统设置
  saveSystemSettings: (data) => request.post('/admin/settings', data),

  // 获取统计信息
  getActualTimeStatistics: () => request.post('/admin/index/getActualTimeStatisticsInfo'),
  getWeekStatistics: () => request.post('/admin/index/getWeekStatisticsInfo'),

  // ==================== 视频管理 ====================
  // 获取视频列表
  getVideoList: (params) => request.post('/admin/video/list', null, { params }),

  // 审核视频
  auditVideo: (videoId, status, reason) => request.post('/admin/video/audit', null, {
    params: { videoId, status, reason }
  }),

  // 删除视频
  deleteVideo: (videoId) => request.post('/admin/video/delete', null, {
    params: { videoId }
  }),

  // 推荐视频
  recommendVideo: (videoId, recommendFlag) => request.post('/admin/video/recommend', null, {
    params: { videoId, recommendFlag }
  }),

  // 获取视频分P列表
  getVideoEpisodes: (videoId) => request.post('/admin/video/episodes', null, {
    params: { videoId }
  }),

  // ==================== 用户管理 ====================
  // 获取用户列表
  getUserList: (params) => request.post('/admin/user/list', null, { params }),

  // 修改用户状态
  changeUserStatus: (userId, status) => request.post('/admin/user/changeStatus', null, {
    params: { userId, status }
  }),

  // ==================== 分类管理 ====================
  // 获取分类列表
  getCategoryList: () => request.post('/admin/category/list'),

  // 保存分类（新增或修改）
  saveCategory: (data) => request.post('/admin/category/save', data),

  // 删除分类
  deleteCategory: (categoryId) => request.post('/admin/category/delete', null, {
    params: { categoryId }
  }),

  // 分类排序
  sortCategory: (categories) => request.post('/admin/category/sort', categories),

  // ==================== 弹幕管理 ====================
  // 获取弹幕列表
  getDanmuList: (params) => request.post('/admin/danmu/list', null, { params }),

  // 删除弹幕
  deleteDanmu: (danmuId) => request.post('/admin/danmu/delete', null, {
    params: { danmuId }
  }),

  // ==================== 评论管理 ====================
  // 获取评论列表
  getCommentList: (params) => request.post('/admin/comment/list', null, { params }),

  // 删除评论
  deleteComment: (commentId) => request.post('/admin/comment/delete', null, {
    params: { commentId }
  }),

  // ==================== 文件管理 ====================
  // 上传文件
  uploadFile: (file, userId) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('userId', userId);
    return request.post('/admin/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  },

  // 获取文件信息
  getFileInfo: (fileId) => request.get(`/admin/file/info/${fileId}`),

  // ==================== 系统设置 ====================
  // 保存列表设置
  saveListSettings: (module, settings) => request.post('/admin/saveListSettings', settings, {
    params: { module }
  })
};

export default adminApi;