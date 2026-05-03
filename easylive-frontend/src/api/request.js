import axios from 'axios';

// 创建axios实例
const service = axios.create({
  baseURL: '',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 添加token等认证信息
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['token'] = token;
    }
    // 如果是FormData类型的请求，删除Content-Type，让浏览器自动设置
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type'];
    }
    return config;
  },
  error => {
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data;
    return res;
  },
  error => {
    console.error('响应错误:', error);
    return Promise.reject(error);
  }
);

export default service;