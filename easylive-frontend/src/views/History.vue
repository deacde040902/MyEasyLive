<template>
  <div class="history-container">
    <header class="header">
      <div class="header-left">
        <h1 class="logo" @click="goToHome">首页</h1>
      </div>
      <div class="header-center">
        <div class="search-box">
          <input type="text" placeholder="搜索" v-model="searchQuery" />
          <button class="search-btn" @click="search"><img src="/src/recourse/icon-search.png" alt="搜索" class="search-icon" /></button>
        </div>
      </div>
      <div class="header-right">
        <div class="user-actions">
          <!-- 未登录状态 -->
          <div v-if="!isLoggedIn" class="avatar-container">
            <img class="avatar" src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20icon&image_size=square" alt="默认头像" @click="goToLogin" />
          </div>
          <!-- 已登录状态 -->
          <div v-else class="user-info-container" @click="toggleUserInfo">
            <div class="avatar-container">
              <img class="avatar" :src="userAvatar" alt="用户头像" @error="handleAvatarError" />
              <img v-if="isBigVip" class="vip-badge" src="/src/recourse/icon-bigVIP.png" alt="大会员" />
            </div>
            <span class="nickname">{{ userNickname }}</span>
            <!-- 用户信息下拉框 -->
            <div v-if="showUserInfo" class="user-info-dropdown">
              <div class="user-info-header">
                <div class="avatar-container-large">
                  <img class="avatar-large" :src="userAvatar" alt="用户头像" @click="triggerFileInput" @error="handleAvatarError" />
                  <img v-if="isBigVip" class="vip-badge-large" src="/src/recourse/icon-bigVIP.png" alt="大会员" />
                </div>
                <div class="user-details">
                  <div class="user-name-row">
                    <h3>{{ userNickname }}</h3>
                    <img v-if="isBigVip" class="vip-tag-icon" src="/src/recourse/icon-bigVIP.png" alt="大会员" />
                  </div>
                  <p class="user-id">ID: {{ userId }}</p>
                  <p class="register-time">注册时间: {{ registerTime }}</p>
                  <div v-if="isBigVip" class="vip-info">
                    <span class="vip-level">Lv.{{ vipLevel }}大会员</span>
                    <span class="vip-expire">到期时间: {{ vipExpireTime }}</span>
                  </div>
                </div>
              </div>
              <div class="user-info-actions">
                <button class="action-btn" @click.stop="goToProfile">
                  <img class="action-btn-icon" src="/src/recourse/icon-personal.png" alt="个人主页" />
                  个人主页
                </button>
                <button class="action-btn vip-btn" @click.stop="goToVIPCenter">
                  <img class="action-btn-icon" src="/src/recourse/icon-bigVIP.png" alt="大会员中心" />
                  {{ isBigVip ? '续费大会员' : '开通大会员' }}
                </button>
                <button class="action-btn" @click.stop="logout">
                  <img class="action-btn-icon" src="/src/recourse/icon-out.png" alt="退出登录" />
                  退出登录
                </button>
              </div>
              <input type="file" ref="fileInput" style="display: none" accept="image/*" @change="handleAvatarUpload" />
            </div>
          </div>
          <div class="icon-item" @click="goToVIPCenter">
            <img class="icon-img" src="/src/recourse/icon-bigVIP.png" alt="大会员中心" />
            <span class="icon-text">大会员</span>
          </div>
          <div class="icon-item" @click="goToMall">
            <img class="icon-img" src="/src/recourse/icon-vipMall.png" alt="会员购商城" />
            <span class="icon-text">会员购</span>
          </div>
          <div class="icon-item" @click="goToMessage">
            <img class="icon-img" src="/src/recourse/icon-message.png" alt="消息" />
            <span class="icon-text">消息</span>
          </div>
          <div class="icon-item" @click="goToCollection">
            <img class="icon-img" src="/src/recourse/icon-collect.png" alt="收藏" />
            <span class="icon-text">收藏</span>
          </div>
          <div class="icon-item active">
            <img class="icon-img" src="/src/recourse/icon-history.png" alt="历史" />
            <span class="icon-text">历史</span>
          </div>
          <div class="icon-item" @click="goToCreationCenter">
            <img class="icon-img" src="/src/recourse/icon-creadCenter.png" alt="创作中心" />
            <span class="icon-text">创作中心</span>
          </div>
          <button class="upload-btn" @click="goToUpload">投稿</button>
        </div>
      </div>
    </header>
    <div class="history-content">
      <div class="page-header">
        <h2>观看历史</h2>
        <button v-if="historyList.length > 0" class="clear-btn" @click="clearHistory">清空历史</button>
      </div>
      
      <div v-if="historyList.length === 0" class="empty-history">
        <img src="/src/recourse/icon-history.png" alt="历史记录" />
        <p>暂无观看历史</p>
        <button class="go-home-btn" @click="goToHome">去看看</button>
      </div>
      
      <div v-else class="history-list">
        <div class="history-item" v-for="(item, index) in historyList" :key="index" @click="goToVideoDetail(item.videoId)">
          <img :src="getVideoCover(item)" alt="视频封面" class="video-cover" @error="handleCoverError($event, item)" />
          <div class="video-info">
            <h3 class="video-title">{{ item.title }}</h3>
            <p class="video-author">{{ item.authorName }}</p>
            <p class="view-time">观看于 {{ formatHistoryDate(item.viewTime) }}</p>
          </div>
          <button class="delete-btn" @click.stop="deleteHistory(item.historyId)">删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';

export default {
  name: 'History',
  setup() {
    const searchQuery = ref('');
    const isLoggedIn = ref(false);
    const userAvatar = ref('https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20icon&image_size=square');
    const userNickname = ref('');
    const userId = ref('');
    const registerTime = ref('未知');
    const showUserInfo = ref(false);
    const isBigVip = ref(false);
    const vipLevel = ref(0);
    const vipExpireTime = ref('');
    const fileInput = ref(null);
    const historyList = ref([]);

    const checkLoginStatus = () => {
      const token = localStorage.getItem('token');
      const nickName = localStorage.getItem('nickName');
      const userIdStorage = localStorage.getItem('userId');
      const userAvatarStorage = localStorage.getItem('userAvatar');
      if (token && nickName && userIdStorage) {
        isLoggedIn.value = true;
        userNickname.value = nickName;
        userId.value = userIdStorage;
        userAvatar.value = userAvatarStorage || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20icon&image_size=square';
        fetchAvatar(token, userIdStorage);
        getUserInfo(token, userIdStorage);
        checkVipStatus();
      }
    };

    const checkVipStatus = async () => {
      const token = localStorage.getItem('token');
      const userIdStorage = localStorage.getItem('userId');
      if (!token || !userIdStorage) {
        isBigVip.value = false;
        vipLevel.value = 0;
        vipExpireTime.value = '';
        return;
      }
      try {
        const response = await fetch(`/api/payment/getVipStatus?userId=${userIdStorage}`, {
          headers: { 'token': token }
        });
        const result = await response.json();
        if (result.code === 200 && result.data) {
          isBigVip.value = result.data.isVip;
          vipLevel.value = result.data.vipLevel || 0;
          vipExpireTime.value = result.data.expireTime ? formatDate(result.data.expireTime) : '';
          localStorage.setItem('isBigVip', result.data.isVip ? '1' : '0');
        } else {
          isBigVip.value = false;
          vipLevel.value = 0;
          vipExpireTime.value = '';
        }
      } catch (error) {
        console.error('获取VIP状态失败:', error);
        isBigVip.value = false;
        vipLevel.value = 0;
        vipExpireTime.value = '';
      }
    };

    const fetchAvatar = async (token, userId) => {
      try {
        const response = await fetch(`/api/user/userInfo/getAvatar?userId=${userId}&t=${Date.now()}`, {
          headers: { 'token': token }
        });
        if (response.ok) {
          const blob = await response.blob();
          const reader = new FileReader();
          reader.onloadend = () => {
            userAvatar.value = reader.result;
            localStorage.setItem('userAvatar', userAvatar.value);
          };
          reader.readAsDataURL(blob);
        }
      } catch (error) {
        console.error('获取头像失败:', error);
      }
    };

    const getUserInfo = async (token, userId) => {
      try {
        const response = await fetch(`/api/user/userInfo/getUserInfo/${userId}`, {
          headers: { 'token': token }
        });
        if (response.ok) {
          const result = await response.json();
          if (result.code === 200 && result.data && result.data.createTime) {
            registerTime.value = formatDate(result.data.createTime);
          }
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
      }
    };

    const formatDate = (timestamp) => {
      if (!timestamp) return '未知';
      const date = new Date(timestamp);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    };

    const toggleUserInfo = () => {
      showUserInfo.value = !showUserInfo.value;
    };

    const triggerFileInput = () => {
      fileInput.value?.click();
    };

    const handleAvatarError = (e) => {
      e.target.src = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20icon&image_size=square';
    };

    const handleCoverError = (e, item) => {
      e.target.src = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=video%20thumbnail&image_size=landscape_16_9`;
    };

    const handleAvatarUpload = async (event) => {
      const file = event.target.files?.[0];
      if (!file) return;

      const token = localStorage.getItem('token');
      if (!token) return;

      const formData = new FormData();
      formData.append('file', file);
      formData.append('userId', userId.value);

      try {
        const response = await fetch('/api/user/userInfo/uploadAvatar', {
          method: 'POST',
          headers: { 'token': token },
          body: formData
        });
        const result = await response.json();
        if (result.code === 200) {
          fetchAvatar(token, userId.value);
          showToast('头像上传成功');
        } else {
          showToast('头像上传失败');
        }
      } catch (error) {
        console.error('头像上传失败:', error);
        showToast('头像上传失败');
      }
    };

    const logout = () => {
      localStorage.removeItem('token');
      localStorage.removeItem('nickName');
      localStorage.removeItem('userId');
      localStorage.removeItem('userAvatar');
      isLoggedIn.value = false;
      userNickname.value = '';
      userId.value = '';
      userAvatar.value = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20icon&image_size=square';
      registerTime.value = '未知';
      showUserInfo.value = false;
      showToast('已退出登录');
      window.location.href = '/';
    };

    const goToVIPCenter = () => {
      showUserInfo.value = false;
      window.location.href = '/vip-center';
    };

    const goToMall = () => {
      showUserInfo.value = false;
      window.location.href = '/mall';
    };

    const getHistoryList = async () => {
      try {
        const token = localStorage.getItem('token');
        
        if (!token) {
          console.warn('未登录，无法获取历史记录');
          return;
        }

        console.log('开始获取历史记录...');
        
        const response = await fetch('/history/loadHistory', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'token': token
          },
          body: 'pageNo=1&pageSize=50'
        });
        
        console.log('历史记录API响应状态:', response.status);
        
        if (!response.ok) {
          console.error('历史记录API请求失败，状态码:', response.status);
          return;
        }
        
        const result = await response.json();
        console.log('历史记录API响应数据:', result);
        
        if (result.code === 200 && result.data) {
          if (Array.isArray(result.data) && result.data.length > 0) {
            // 获取视频详情以补充标题、封面和作者信息
            const historyRecords = result.data;
            const detailedHistory = await Promise.all(
              historyRecords.map(async (item) => {
                let title = '未知视频';
                let cover = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=video%20thumbnail&image_size=landscape_16_9`;
                let authorName = '未知作者';
                
                try {
                  const videoResponse = await fetch(`/api/video/detail?videoId=${item.videoId}`, {
                    method: 'POST',
                    headers: {
                      'token': token
                    }
                  });
                  const videoResult = await videoResponse.json();
                  if (videoResult.code === 200 && videoResult.data) {
                    const video = videoResult.data;
                    title = video.title || '未知视频';
                    authorName = video.authorName || video.nickName || '未知作者';
                    
                    // 处理封面
                    if (video.cover) {
                      cover = video.cover;
                      if (cover.includes('localhost:7071')) {
                        // 处理网关地址，转换为前端代理路径
                        if (cover.includes('/resource/file')) {
                          cover = cover.replace('http://localhost:7071/resource/file', '/api/file');
                        } else if (cover.includes('/resource/public/download')) {
                          cover = cover.replace('http://localhost:7071/resource/public/download', '/api/file/public/download');
                        } else if (cover.includes('/resource/')) {
                          cover = cover.replace('http://localhost:7071/resource', '/api/file');
                        }
                      } else if (cover.startsWith('/resource/file/')) {
                        cover = '/api/file' + cover.substring('/resource/file'.length);
                      } else if (cover.startsWith('/resource/public/download/')) {
                        cover = '/api/file/public/download' + cover.substring('/resource/public/download'.length);
                      } else if (cover.startsWith('/resource/')) {
                        cover = '/api/file' + cover.substring('/resource'.length);
                      } else if (cover.match(/^[a-f0-9_]+\.(png|jpg|jpeg|gif)$/i)) {
                        cover = `/api/file/public/download/${cover}`;
                      } else if (!cover.startsWith('http')) {
                        cover = `/api/file/public/download/${cover}`;
                      }
                    } else if (video.coverFileId) {
                      cover = `/api/file/public/download/${video.coverFileId}`;
                    } else if (video.cover_file_id) {
                      cover = `/api/file/public/download/${video.cover_file_id}`;
                    }
                  }
                } catch (videoError) {
                  console.warn(`获取视频 ${item.videoId} 详情失败:`, videoError);
                }
                
                return {
                  historyId: item.historyId || item.id || Date.now() + Math.random(),
                  videoId: item.videoId,
                  title: title,
                  cover: cover,
                  authorName: authorName,
                  viewTime: item.updateTime || item.createTime || item.viewTime
                };
              })
            );
            
            historyList.value = detailedHistory;
            console.log('历史记录加载成功，共', historyList.value.length, '条');
          } else {
            console.log('历史记录为空');
            historyList.value = [];
          }
        } else if (result.code === 401) {
          console.warn('登录超时，请重新登录');
          localStorage.clear();
          isLoggedIn.value = false;
        } else {
          console.warn('获取历史记录失败:', result.message || '未知错误');
        }
      } catch (error) {
        console.error('获取历史记录失败:', error);
        showToast('获取历史记录失败，请稍后重试');
      }
    };

    const getVideoCover = (video) => {
      if (video.cover) {
        let cover = video.cover;
        if (cover.includes('localhost:7071')) {
          cover = cover.replace('http://localhost:7071/resource/file', '/api/file');
        }
        if (cover.startsWith('/resource/file/')) {
          cover = '/api/file' + cover.substring('/resource/file'.length);
        } else if (cover.startsWith('/resource/')) {
          cover = '/api/file' + cover.substring('/resource'.length);
        }
        if (cover.match(/^[a-f0-9]+_\d{10,}\.\w+$/i)) {
          cover = `/api/file/public/download/${cover}`;
        }
        return cover;
      }
      if (video.coverFileId) {
        return `/api/file/public/download/${video.coverFileId}`;
      }
      return `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=video%20thumbnail%20${encodeURIComponent(video.title)}&image_size=landscape_16_9`;
    };

    const formatHistoryDate = (timestamp) => {
      if (!timestamp) return '未知';
      const date = new Date(timestamp);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      return `${year}-${month}-${day} ${hours}:${minutes}`;
    };

    const deleteHistory = async (historyId) => {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          showToast('请先登录');
          return;
        }

        console.log('删除历史记录，historyId:', historyId);

        const response = await fetch('/history/delHistory', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'token': token
          },
          body: `historyId=${historyId}`
        });
        
        console.log('删除历史记录响应状态:', response.status);
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        console.log('删除历史记录响应:', result);
        
        if (result.code === 200) {
          historyList.value = historyList.value.filter(item => item.historyId !== historyId);
          showToast('删除成功');
        } else {
          showToast(result.message || '删除失败');
        }
      } catch (error) {
        console.error('删除历史记录失败:', error);
        showToast('删除失败，请稍后重试');
      }
    };

    const clearHistory = async () => {
      if (!confirm('确定要清空所有观看历史吗？')) return;
      
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          showToast('请先登录');
          return;
        }

        console.log('清空所有历史记录');

        const response = await fetch('/history/clearHistory', {
          method: 'POST',
          headers: {
            'token': token
          }
        });
        
        console.log('清空历史记录响应状态:', response.status);
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        console.log('清空历史记录响应:', result);
        
        if (result.code === 200) {
          historyList.value = [];
          showToast('已清空所有历史');
        } else {
          showToast(result.message || '清空失败');
        }
      } catch (error) {
        console.error('清空历史记录失败:', error);
        showToast('清空失败，请稍后重试');
      }
    };

    const goToVideoDetail = (videoId) => {
      window.location.href = `/video/detail/${videoId}`;
    };

    const goToHome = () => {
      window.location.href = '/';
    };

    const goToLogin = () => {
      window.location.href = '/login';
    };

    const goToUpload = () => {
      window.location.href = '/upload';
    };

    const goToCreationCenter = () => {
      window.location.href = '/creation-center';
    };

    const goToCollection = () => {
      window.location.href = '/collection';
    };

    const goToProfile = () => {
      window.location.href = '/profile';
    };

    const goToMessage = () => {
      window.location.href = '/message';
    };

    const search = () => {
      if (searchQuery.value.trim()) {
        window.location.href = `/search?keyword=${encodeURIComponent(searchQuery.value)}`;
      }
    };

    const showToast = (message) => {
      const toast = document.createElement('div');
      toast.className = 'toast';
      toast.textContent = message;
      toast.style.cssText = `
        position: fixed; top: 70px; left: 50%; transform: translateX(-50%);
        background: white; color: black; padding: 12px 20px; border-radius: 4px;
        box-shadow: 0 2px 12px rgba(0,0,0,0.1); z-index: 9999; transition: 0.3s; opacity:0;
      `;
      document.body.appendChild(toast);
      setTimeout(() => { toast.style.opacity = '1'; }, 10);
      setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => document.body.removeChild(toast), 300);
      }, 3000);
    };

    // VIP状态更新事件处理
    const handleVipStatusUpdated = async (event) => {
      if (event.detail) {
        isBigVip.value = event.detail.isVip;
        vipLevel.value = event.detail.level || 0;
        vipExpireTime.value = event.detail.expireTime ? formatDate(event.detail.expireTime) : '';
      } else {
        // 如果没有detail数据，重新获取VIP状态
        await checkVipStatus();
      }
    };

    onMounted(() => {
      checkLoginStatus();
      getHistoryList();
      window.addEventListener('vipStatusUpdated', handleVipStatusUpdated);
    });
    
    return {
      searchQuery,
      isLoggedIn,
      userAvatar,
      userNickname,
      userId,
      registerTime,
      showUserInfo,
      isBigVip,
      vipLevel,
      vipExpireTime,
      fileInput,
      historyList,
      getVideoCover,
      formatDate,
      formatHistoryDate,
      deleteHistory,
      clearHistory,
      goToVideoDetail,
      goToHome,
      goToLogin,
      goToUpload,
      goToCreationCenter,
      goToCollection,
      goToProfile,
      goToMessage,
      goToVIPCenter,
      goToMall,
      toggleUserInfo,
      triggerFileInput,
      handleAvatarError,
      handleAvatarUpload,
      logout,
      search
    };
  }
};
</script>

<style scoped>
.history-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  zoom: 1.25;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 20px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left .logo {
  font-size: 20px;
  font-weight: bold;
  color: #ff4757;
  cursor: pointer;
}

.header-center .search-box {
  display: flex;
  align-items: center;
  width: 400px;
  height: 36px;
  background-color: #f0f0f0;
  border-radius: 18px;
  padding: 0 16px;
}

.search-box input {
  flex: 1;
  border: none;
  background: transparent;
  outline: none;
  font-size: 14px;
}

.search-btn {
  border: none;
  background: transparent;
  cursor: pointer;
  padding: 0;
}

.search-icon {
  width: 16px;
  height: 16px;
}

.header-right .user-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  color: #666;
  transition: color 0.3s;
}

.icon-item:hover, .icon-item.active {
  color: #ff4757;
}

.icon-img {
  width: 20px;
  height: 20px;
}

.upload-btn {
  background-color: #ff4757;
  color: white;
  border: none;
  padding: 6px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.avatar-container {
  position: relative;
  display: flex;
  align-items: center;
}

.vip-badge {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  border: 2px solid white;
  object-fit: cover;
}

.avatar-container-large {
  position: relative;
}

.vip-badge-large {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid white;
  object-fit: cover;
}

.vip-tag-icon {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  object-fit: cover;
}

.user-info-container {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.nickname {
  font-size: 14px;
  color: #333;
}

.user-info-dropdown {
  position: absolute;
  top: 56px;
  right: 20px;
  width: 300px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  overflow: hidden;
}

.user-info-header {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.avatar-large {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  cursor: pointer;
}

.user-details h3 {
  font-size: 16px;
  margin: 0 0 4px 0;
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-id, .register-time {
  font-size: 12px;
  color: #999;
  margin: 0 0 4px 0;
}

.vip-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.vip-level, .vip-expire {
  font-size: 12px;
  color: #999;
}

.user-info-actions {
  padding: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  padding: 10px;
  border: none;
  background-color: #f5f5f5;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  margin-bottom: 8px;
  transition: background-color 0.2s;
}

.action-btn:hover {
  background-color: #e8e8e8;
}

.action-btn:last-child {
  margin-bottom: 0;
}

.action-btn-icon {
  width: 18px;
  height: 18px;
}

.vip-btn {
  background: linear-gradient(135deg, #ffd700 0%, #ffb700 100%);
  color: #8b4513;
}

.vip-btn:hover {
  background: linear-gradient(135deg, #ffb700 0%, #ffa500 100%);
}

.icon-text {
  font-size: 12px;
  margin-top: 2px;
  text-align: center;
}

.history-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 24px;
  margin: 0;
}

.clear-btn {
  background-color: #ff4757;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.empty-history {
  text-align: center;
  padding: 60px 20px;
  background-color: white;
  border-radius: 8px;
}

.empty-history img {
  width: 80px;
  height: 80px;
  margin-bottom: 16px;
}

.empty-history p {
  font-size: 16px;
  color: #999;
  margin: 0 0 20px 0;
}

.go-home-btn {
  background-color: #ff4757;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.history-list {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.history-item:last-child {
  border-bottom: none;
}

.history-item:hover {
  background-color: #f9f9f9;
}

.video-cover {
  width: 120px;
  height: 68px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.video-info {
  flex: 1;
  min-width: 0;
}

.video-title {
  font-size: 15px;
  color: #333;
  margin: 0 0 6px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.video-author {
  font-size: 13px;
  color: #999;
  margin: 0 0 4px 0;
}

.view-time {
  font-size: 12px;
  color: #bbb;
  margin: 0;
}

.delete-btn {
  width: 60px;
  height: 32px;
  background-color: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s;
}

.delete-btn:hover {
  background-color: #ffebee;
  color: #ff4757;
}

.toast {
  position: fixed;
  top: 70px;
  left: 50%;
  transform: translateX(-50%);
  background: white;
  color: black;
  padding: 12px 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  z-index: 9999;
  transition: all 0.3s ease;
  opacity: 0;
}
</style>