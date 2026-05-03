<template>
  <div class="collection-page">
    <!-- 顶部导航栏 -->
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
          <div class="icon-item active">
            <img class="icon-img" src="/src/recourse/icon-collect.png" alt="收藏" />
            <span class="icon-text">收藏</span>
          </div>
          <div class="icon-item" @click="goToHistory">
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

    <!-- 页面内容 -->
    <div class="collection-content">
      <div class="page-title">
        <h2>我的收藏</h2>
        <span class="count">{{ collections.length }}个视频</span>
      </div>

      <div v-if="collections.length === 0" class="empty-state">
        <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=empty%20collection%20cartoon%20illustration&image_size=square" alt="暂无收藏" />
        <p>还没有收藏任何视频</p>
        <button class="explore-btn" @click="goToHome">去发现</button>
      </div>

      <div v-else class="collection-grid">
        <div class="video-card" v-for="(video, index) in collections" :key="index" @click="goToVideoDetail(video.videoId)">
          <div class="video-card-cover">
            <img :src="video.cover" alt="视频封面" class="cover-image" @error="handleCoverError($event, video)" />
            <span class="video-duration">{{ formatDuration(video.duration) }}</span>
            <div class="remove-btn" @click.stop="removeCollection(video.videoId)">✕</div>
          </div>
          <div class="video-card-info">
            <h4 class="video-card-title">{{ video.title }}</h4>
            <p class="video-card-author">{{ video.authorName }}</p>
            <p class="video-card-stats">{{ formatViews(video.playCount || 0) }}播放 · {{ formatDate(video.createTime || video.publishTime) }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';

export default {
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
    const collections = ref([
      {
        videoId: '1',
        title: '乌鸦哥名场面师傅学张学友',
        cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=video%20thumbnail%201&image_size=landscape_16_9',
        authorName: '程序员老罗',
        playCount: 83,
        createTime: '2024-08-03',
        duration: 300
      },
      {
        videoId: '2',
        title: '盘点周星驰电影中的经典闽南语',
        cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=video%20thumbnail%202&image_size=landscape_16_9',
        authorName: '程序员老罗',
        playCount: 64,
        createTime: '2024-08-03',
        duration: 420
      },
      {
        videoId: '3',
        title: '人在囧途搞笑片段王宝强化徐峥',
        cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=video%20thumbnail%203&image_size=landscape_16_9',
        authorName: '程序员老罗',
        playCount: 27,
        createTime: '2024-08-03',
        duration: 240
      },
      {
        videoId: '4',
        title: '美人鱼经典搞笑片段',
        cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=video%20thumbnail%204&image_size=landscape_16_9',
        authorName: '程序员老罗',
        playCount: 22,
        createTime: '2024-08-03',
        duration: 180
      }
    ]);

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

    const handleCoverError = (e, video) => {
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

    const processCoverUrl = (url) => {
      if (!url) return '';
      if (url.includes('localhost:7071')) {
        const path = url.replace(/^https?:\/\/localhost:7071/, '');
        return processCoverUrl(path);
      }
      if (url.startsWith('/resource/file/')) {
        return '/api/file' + url.substring('/resource/file'.length);
      }
      if (url.startsWith('/resource/')) {
        return '/api/file' + url.substring('/resource'.length);
      }
      if (url.match(/^[a-f0-9]+_\d{10,}\.\w+$/i) || /^\d{10,}$/.test(url)) {
        return `/api/file/public/download/${url}`;
      }
      return url;
    };

    const getCollections = async () => {
      try {
        const token = localStorage.getItem('token');
        if (!token) return;
        
        const response = await fetch('/userCollection/list', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'token': token
          },
          body: JSON.stringify({
            pageNo: 1,
            pageSize: 50
          })
        });
        
        if (!response.ok) {
          console.warn('获取收藏列表失败，状态码:', response.status);
          return;
        }
        
        const result = await response.json();
        if (result && result.code === 200 && result.data) {
          collections.value = result.data.map(item => ({
            videoId: item.videoId,
            title: item.title || '未知视频',
            cover: processCoverUrl(item.cover) || `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=video%20thumbnail&image_size=landscape_16_9`,
            authorName: item.authorName || '未知作者',
            playCount: item.playCount || 0,
            createTime: item.createTime,
            duration: item.duration || 0
          }));
        } else if (result && result.code === 401) {
          console.warn('登录超时，请重新登录');
          localStorage.clear();
          isLoggedIn.value = false;
        }
      } catch (error) {
        console.error('获取收藏列表失败:', error);
      }
    };

    const removeCollection = async (videoId) => {
      if (!confirm('确定要取消收藏这个视频吗？')) {
        return;
      }

      try {
        const token = localStorage.getItem('token');
        if (!token) {
          showToast('请先登录');
          return;
        }

        const response = await fetch(`/userCollection/collect/${videoId}`, {
          method: 'DELETE',
          headers: {
            'token': token,
            'Content-Type': 'application/json'
          }
        });
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        if (result.code === 200) {
          const beforeLength = collections.value.length;
          collections.value = collections.value.filter(v => v.videoId !== videoId);
          const afterLength = collections.value.length;
          
          if (beforeLength !== afterLength) {
            showToast('取消收藏成功');
          } else {
            showToast('未找到该收藏记录');
          }
        } else {
          showToast(result.message || '取消收藏失败');
        }
      } catch (error) {
        console.error('取消收藏失败:', error);
        showToast('取消收藏失败，请稍后重试');
      }
    };

    const search = () => {
      if (searchQuery.value.trim()) {
        window.location.href = `/search?keyword=${encodeURIComponent(searchQuery.value)}`;
      }
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

    const goToHistory = () => {
      window.location.href = '/history';
    };

    const goToProfile = () => {
      window.location.href = '/profile';
    };

    const goToMessage = () => {
      window.location.href = '/message';
    };

    const goToCollection = () => {
      window.location.href = '/collection';
    };

    const goToVideoDetail = (videoId) => {
      window.location.href = `/video/detail/${videoId}`;
    };

    const formatDuration = (seconds) => {
      if (!seconds) return '00:00';
      const minutes = Math.floor(seconds / 60);
      const remainingSeconds = seconds % 60;
      return `${String(minutes).padStart(2, '0')}:${String(remainingSeconds).padStart(2, '0')}`;
    };

    const formatViews = (views) => {
      if (!views) return '0';
      if (views >= 10000) {
        return (views / 10000).toFixed(1) + '万';
      }
      return views;
    };

    const showToast = (message) => {
      const toast = document.createElement('div');
      toast.className = 'toast';
      toast.textContent = message;
      toast.style.cssText = `
        position: fixed; top: 50px; left: 50%; transform: translateX(-50%);
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

    const handleVipStatusUpdated = async (event) => {
      if (event.detail) {
        isBigVip.value = event.detail.isVip;
        vipLevel.value = event.detail.level || 0;
        vipExpireTime.value = event.detail.expireTime ? formatDate(event.detail.expireTime) : '';
      } else {
        await checkVipStatus();
      }
    };

    onMounted(() => {
      checkLoginStatus();
      getCollections();
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
      collections,
      search,
      goToHome,
      goToLogin,
      goToUpload,
      goToCreationCenter,
      goToHistory,
      goToProfile,
      goToMessage,
      goToCollection,
      goToVideoDetail,
      goToVIPCenter,
      goToMall,
      toggleUserInfo,
      triggerFileInput,
      handleAvatarError,
      handleAvatarUpload,
      logout,
      removeCollection,
      formatDuration,
      formatViews,
      formatDate
    };
  }
};
</script>

<style scoped>
.collection-page {
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

.collection-content {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.page-title h2 {
  font-size: 24px;
  color: #333;
  margin: 0;
}

.page-title .count {
  font-size: 14px;
  color: #999;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
}

.empty-state img {
  width: 150px;
  height: 150px;
  margin-bottom: 20px;
}

.empty-state p {
  font-size: 16px;
  color: #999;
  margin-bottom: 20px;
}

.explore-btn {
  background-color: #ff4757;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.collection-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.video-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.video-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.video-card-cover {
  position: relative;
  width: 100%;
  padding-top: 56.25%;
}

.cover-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-duration {
  position: absolute;
  bottom: 4px;
  right: 4px;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  font-size: 12px;
  padding: 2px 4px;
  border-radius: 2px;
}

.remove-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 24px;
  height: 24px;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  opacity: 0;
  transition: opacity 0.2s;
  cursor: pointer;
}

.video-card:hover .remove-btn {
  opacity: 1;
}

.video-card-info {
  padding: 12px;
}

.video-card-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0 0 8px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.video-card-author {
  font-size: 12px;
  color: #999;
  margin: 0 0 4px 0;
}

.video-card-stats {
  font-size: 12px;
  color: #999;
  margin: 0;
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

@media (max-width: 1024px) {
  .collection-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .header-center .search-box {
    width: 250px;
  }
  
  .collection-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .collection-grid {
    grid-template-columns: 1fr;
  }
}
</style>
