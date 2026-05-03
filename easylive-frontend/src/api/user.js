import request from './request';

const userApi = {
  // 用户登录
  login: (data) => request.post('/api/user/userInfo/login', data),
  
  // 用户注册
  register: (data) => request.post('/api/user/userInfo/register', data),
  
  // 重置密码
  resetPwd: (data) => request.post('/api/user/userInfo/resetPwd', data),
  
  // 发送邮箱验证码
  sendEmailCode: (data) => request.post('/api/user/userInfo/sendEmailCode', data),
  
  // 获取用户信息
  getUserInfo: (userId) => request.get(`/api/user/userInfo/getUserInfo/${userId}`),
  
  // 更新用户信息
  updateUserInfo: (data) => request.put('/api/user/userInfo/update', data),
  
  // 上传头像
  uploadAvatar: (data) => request.post('/api/user/userInfo/uploadAvatar', data),
  
  // QQ登录
  qqLogin: (code) => request.get(`/api/user/userInfo/qq/login?code=${code}`),
  
  // 获取用户关注列表
  getFollowList: (userId, page, size) => request.get(`/api/user/userInfo/follow/list?userId=${userId}&page=${page}&size=${size}`),
  
  // 关注用户
  followUser: (userId) => request.post(`/api/user/userInfo/follow/${userId}`),
  
  // 取消关注
  unfollowUser: (userId) => request.delete(`/api/user/userInfo/follow/${userId}`)
};

export default userApi;