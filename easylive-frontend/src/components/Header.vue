<template>
  <header class="header">
    <div class="header-left">
      <h1 class="logo" @click="goToHome">首页</h1>
    </div>
    <div class="header-center">
      <div class="search-box">
        <input type="text" placeholder="搜索" v-model="searchQuery" @keyup.enter="search" />
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
        <div class="icon-item collection-item" @mouseenter="showCollectionDropdown = true" @mouseleave="showCollectionDropdown = false" @click="goToCollection">
          <img class="icon-img" src="/src/recourse/icon-collect.png" alt="收藏" />
          <span class="icon-text">收藏</span>
          <div v-show="showCollectionDropdown" class="collection-dropdown" @wheel.prevent="handleDropdownScroll">
            <div class="dropdown-header">
              <span>我的收藏</span>
              <button class="view-all-btn" @click="goToCollection">查看全部</button>
            </div>
            <div class="dropdown-content" ref="dropdownContent">
              <div v-if="collectionList.length === 0" class="empty-collection">
                <p>暂无收藏</p>
              </div>
              <div v-else class="collection-item-list">
                <div class="dropdown-video-item" v-for="(item, index) in collectionList" :key="index" @click="goToVideoDetail(item.videoId)">
                  <img :src="item.cover" alt="视频封面" class="dropdown-video-cover" />
                  <div class="dropdown-video-info">
                    <p class="dropdown-video-title">{{ item.title }}</p>
                    <p class="dropdown-video-author">{{ item.authorName }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="icon-item" @click="goToHistory">
          <img class="icon-img" src="/src/recourse/icon-history.png" alt="历史" />
          <span class="icon-text">历史</span>
        </div>
        <div class="icon-item" @click="goToCreationCenter">
          <img class="icon-img" src="/src/recourse/icon-creadCenter.png" alt="创作中心" />
          <span class="icon-text">创作中心</span>
        </div>
        <button class="upload-btn" @click="goToUpload">
          <img class="upload-btn-icon" src="/src/recourse/icon-postWorkj.png" alt="投稿" />
          投稿
        </button>
      </div>
    </div>
  </header>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue';

export default {
  name: 'Header',
  setup() {
    const searchQuery = ref('');
    const isLoggedIn = ref(false);
    const userAvatar = ref('https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20icon&image_size=square');
    const userNickname = ref('');
    const userId = ref('');
    const registerTime = ref('未知');
    const showUserInfo = ref(false);
    const showCollectionDropdown = ref(false);
    const collectionList = ref([]);
    const fileInput = ref(null);
    const isBigVip = ref(false);
    const vipLevel = ref(0);
    const vipExpireTime = ref('');

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

    const handleStorageChange = (event) => {
      if (event.key === 'userAvatar') {
        userAvatar.value = event.newValue || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20icon&image_size=square';
      }
      if (event.key === 'nickName') {
        userNickname.value = event.newValue || '';
      }
      if (event.key === 'userId') {
        userId.value = event.newValue || '';
      }
      if (event.key === 'token') {
        if (event.newValue) {
          isLoggedIn.value = true;
          checkLoginStatus();
        } else {
          isLoggedIn.value = false;
          userNickname.value = '';
          userId.value = '';
          userAvatar.value = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20icon&image_size=square';
          registerTime.value = '未知';
        }
      }
    };

    const toggleUserInfo = () => {
      showUserInfo.value = !showUserInfo.value;
    };

    const triggerFileInput = () => {
      fileInput.value?.click();
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

    const goToProfile = () => {
      showUserInfo.value = false;
      window.location.href = '/profile';
    };

    const goToUpload = () => {
      window.location.href = '/upload';
    };

    const goToCreationCenter = () => {
      window.location.href = '/creation-center';
    };

    const goToMessage = () => {
      window.location.href = '/message';
    };

    const goToHistory = () => {
      window.location.href = '/history';
    };

    const goToCollection = () => {
      showCollectionDropdown.value = false;
      window.location.href = '/collection';
    };

    const goToVideoDetail = (videoId) => {
      showCollectionDropdown.value = false;
      window.location.href = `/video/detail/${videoId}`;
    };

    const goToVIPCenter = () => {
      showUserInfo.value = false;
      window.location.href = '/vip-center';
    };

    const goToMall = () => {
      window.location.href = '/mall';
    };

    const getCollectionList = async () => {
      try {
        const token = localStorage.getItem('token');
        if (!token) return;

        const response = await fetch('/userCollection/list', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'token': token
          },
          body: JSON.stringify({ pageNo: 1, pageSize: 10 })
        });
        const result = await response.json();
        if (result.code === 200 && result.data) {
          collectionList.value = await Promise.all(result.data.map(async item => {
            if (item.videoId) {
              try {
                const videoResponse = await fetch(`/api/video/detail`, {
                  method: 'POST',
                  headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                  },
                  body: `videoId=${item.videoId}`
                });
                const videoResult = await videoResponse.json();
                if (videoResult.code === 200 && videoResult.data) {
                  const video = videoResult.data;
                  return {
                    videoId: video.videoId || video.id,
                    title: video.title,
                    cover: (video.coverFileId || video.cover_file_id) ? `/api/file/public/download/${video.coverFileId || video.cover_file_id}` : (video.cover || `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=video%20thumbnail%20${encodeURIComponent(video.title)}&image_size=landscape_16_9`),
                    authorName: video.authorName || video.author
                  };
                }
              } catch (error) {
                console.error('获取收藏视频详情失败:', error);
              }
            }
            return item;
          }));
        }
      } catch (error) {
        console.error('获取收藏列表失败:', error);
      }
    };

    const handleDropdownScroll = (event) => {
      const dropdownContent = document.querySelector('.dropdown-content');
      if (dropdownContent) {
        dropdownContent.scrollTop += event.deltaY;
      }
    };

    const handleAvatarError = (e) => {
      e.target.src = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20icon&image_size=square';
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
    getCollectionList();
    window.addEventListener('storage', handleStorageChange);
    window.addEventListener('vipStatusUpdated', handleVipStatusUpdated);
  });

  onUnmounted(() => {
    window.removeEventListener('storage', handleStorageChange);
    window.removeEventListener('vipStatusUpdated', handleVipStatusUpdated);
  });

    return {
      searchQuery,
      isLoggedIn,
      userAvatar,
      userNickname,
      userId,
      registerTime,
      showUserInfo,
      showCollectionDropdown,
      collectionList,
      fileInput,
      isBigVip,
      toggleUserInfo,
      logout,
      search,
      goToHome,
      goToLogin,
      goToProfile,
      goToUpload,
      goToCreationCenter,
      goToMessage,
      goToHistory,
      goToCollection,
      goToVideoDetail,
      goToVIPCenter,
      goToMall,
      handleAvatarError,
      handleAvatarUpload,
      triggerFileInput
    };
  }
};
</script>

<style scoped>
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

.user-id, .register-time {
  font-size: 12px;
  color: #999;
  margin: 0 0 4px 0;
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

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  color: #666;
  transition: color 0.2s;
}

.icon-item:hover {
  color: #ff4757;
}

.icon-img {
  width: 20px;
  height: 20px;
}

.icon-text {
  font-size: 12px;
  margin-top: 2px;
}

.upload-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background-color: #ff4757;
  color: white;
  border: none;
  padding: 6px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.upload-btn-icon {
  width: 16px;
  height: 16px;
}

/* 收藏下拉菜单 */
.collection-item {
  position: relative;
}

.collection-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  width: 360px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  overflow: hidden;
  margin-top: 8px;
}

.dropdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.dropdown-header span {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.view-all-btn {
  background-color: transparent;
  border: none;
  color: #ff4757;
  font-size: 12px;
  cursor: pointer;
  padding: 0;
}

.dropdown-content {
  max-height: 300px;
  overflow-y: auto;
}

.empty-collection {
  padding: 40px 20px;
  text-align: center;
  color: #999;
}

.collection-item-list {
  padding: 8px;
}

.dropdown-video-item {
  display: flex;
  gap: 12px;
  padding: 8px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.dropdown-video-item:hover {
  background-color: #f5f5f5;
}

.dropdown-video-cover {
  width: 80px;
  height: 45px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.dropdown-video-info {
  flex: 1;
  min-width: 0;
}

.dropdown-video-title {
  font-size: 13px;
  color: #333;
  margin: 0 0 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dropdown-video-author {
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

@media (max-width: 768px) {
  .header-center .search-box {
    width: 250px;
  }
  
  .header-right .user-actions {
    gap: 12px;
  }
  
  .nickname {
    display: none;
  }
  
  .user-info-dropdown {
    right: 10px;
  }
}
</style>
