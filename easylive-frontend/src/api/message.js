import request from './request'

const messageApi = {
  getMessageList: () => {
    return request.post('/api/chat/getMessageList')
  },
  
  getChatHistory: (userId) => {
    return request.post(`/api/chat/getChatHistory?userId=${userId}`)
  },
  
  sendMessage: (data) => {
    return request.post('/api/chat/sendMessage', data)
  },
  
  markRead: (userId) => {
    return request.post(`/api/chat/markRead?userId=${userId}`)
  },
  
  getUnreadCount: () => {
    return request.post('/api/chat/getUnreadCount')
  }
}

export default messageApi