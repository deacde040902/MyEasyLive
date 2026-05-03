import request from './request';

const chatApi = {
  // 获取会话列表
  getConversationList: (page, size) => request.get(`/api/chat/conversations?page=${page}&size=${size}`),
  
  // 获取聊天消息
  getChatMessages: (conversationId, page, size) => request.get(`/api/chat/messages/${conversationId}?page=${page}&size=${size}`),
  
  // 发送消息
  sendMessage: (data) => request.post('/api/chat/messages', data),
  
  // 创建会话
  createConversation: (userId) => request.post(`/api/chat/conversations/${userId}`),
  
  // 标记消息已读
  markAsRead: (conversationId) => request.put(`/api/chat/conversations/${conversationId}/read`)
};

export default chatApi;