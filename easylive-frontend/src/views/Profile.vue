<template>
  <div class="profile-container">
    <!-- 顶部导航栏 - 使用 Header 组件 -->
    <Header />

    <!-- 个人信息区域 -->
    <div class="profile-info">
      <div class="profile-banner">
        <div class="banner-content">
          <div class="user-profile">
            <div class="avatar-wrapper">
              <img
                class="profile-avatar"
                :src="userAvatar"
                alt="用户头像"
                @click="triggerFileInput"
              />
              <img v-if="isBigVip" class="vip-badge" src="/src/recourse/icon-bigVIP.png" alt="大会员" />
            </div>
            <div class="user-details">
              <div class="username-row">
                <h2 class="username">{{ userNickname }}</h2>
                <span v-if="isBigVip" class="vip-tag">
                  <img src="/src/recourse/icon-bigVIP.png" alt="大会员" class="vip-tag-icon" />
                  Lv.{{ vipLevel }}大会员
                </span>
              </div>
              <p class="user-id">ID: {{ userId }}</p>
              <p v-if="isBigVip" class="vip-expire">到期时间: {{ vipExpireTime }}</p>
              <div class="user-stats">
                <div class="stat-item">
                  <span class="stat-value">210</span>
                  <span class="stat-label">关注</span>
                </div>
                <div class="stat-item">
                  <span class="stat-value">4</span>
                  <span class="stat-label">粉丝</span>
                </div>
                <div class="stat-item">
                  <span class="stat-value">4</span>
                  <span class="stat-label">获赞</span>
                </div>
                <div class="stat-item">
                  <span class="stat-value">0</span>
                  <span class="stat-label">播放数</span>
                </div>
              </div>
            </div>
            <div class="profile-actions">
              <button class="action-btn edit-btn" @click="openEditProfileModal">编辑资料</button>
              <button class="action-btn settings-btn" @click="showChangePasswordModal = true">重置密码</button>
              <button v-if="isBigVip" class="action-btn vip-renew-btn" @click="goToVIPCenter">续费大会员</button>
              <button v-else class="action-btn vip-buy-btn" @click="goToVIPCenter">开通大会员</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 导航标签 -->
      <div class="profile-nav">
        <div class="nav-tabs">
          <div class="tab active">
            <img src="/src/recourse/icon-home.png" alt="主页" class="tab-icon" />
            <span>主页</span>
          </div>
          <div class="tab">
            <img src="/src/recourse/icon-dongtai.png" alt="动态" class="tab-icon" />
            <span>动态</span>
          </div>
          <div class="tab">
            <img src="/src/recourse/icon-postWorkj.png" alt="投稿" class="tab-icon" />
            <span>投稿</span>
          </div>
          <div class="tab">
            <img src="/src/recourse/icon-collect&series.png" alt="合集和系列" class="tab-icon" />
            <span>合集和系列</span>
          </div>
          <div class="tab">
            <img src="/src/recourse/icon-collect.png" alt="收藏" class="tab-icon" />
            <span>收藏</span>
          </div>
          <div class="tab">
            <img src="/src/recourse/icon-love.png" alt="追番追剧" class="tab-icon" />
            <span>追番追剧</span>
          </div>
          <div class="tab">
            <img src="/src/recourse/icon-opition.png" alt="设置" class="tab-icon" />
            <span>设置</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="profile-content">
      <!-- 公告 -->
      <div class="content-section collapsible-section">
        <div class="section-header" @click="toggleSection('announcement')">
          <h3>公告</h3>
          <div class="header-actions">
            <button class="edit-announcement-btn" @click.stop="openEditAnnouncementModal">编辑</button>
            <span class="expand-icon">{{ expandedSections.announcement ? '▼' : '▶' }}</span>
          </div>
        </div>
        <div v-if="expandedSections.announcement" class="section-content">
          <div class="announcement">
            <p>{{ announcementContent || '编辑我的公告' }}</p>
          </div>
        </div>
      </div>

      <!-- 粉丝视角 -->
      <div class="content-section collapsible-section">
        <div class="section-header" @click="toggleSection('fanView')">
          <h3>粉丝视角</h3>
          <span class="expand-icon">{{ expandedSections.fanView ? '▼' : '▶' }}</span>
        </div>
        <div v-if="expandedSections.fanView" class="section-content">
          <div class="section-tabs">
            <span class="section-tab active">粉丝视角</span>
            <span class="section-tab">新访客视角</span>
          </div>
          <div class="featured-video">
            <div class="featured-content">
              <img
                src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=anime%20style%20cute%20characters&image_size=landscape_4_3"
                alt="置顶视频"
              />
              <div class="featured-info">
                <h4>置顶视频</h4>
                <p>
                  置顶视频是粉丝们看到的第一个视频，<br />
                  请选择你最喜爱的作品，让粉丝们一眼就爱上吧！
                </p>
                <button class="featured-btn">+ 置顶视频</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 收藏夹 -->
      <div class="content-section collapsible-section">
        <div class="section-header" @click="toggleSection('collection')">
          <h3>收藏夹</h3>
          <div class="header-actions">
            <span class="more-link" @click.stop="goToCollection">查看更多 ></span>
            <span class="expand-icon">{{ expandedSections.collection ? '▼' : '▶' }}</span>
          </div>
        </div>
        <div v-if="expandedSections.collection" class="section-content">
          <div class="collection-grid">
            <div
              class="collection-item"
              v-for="(collection, index) in collections"
              :key="collection.videoId || index"
              @click="goToVideoDetail(collection.videoId)"
            >
              <div class="collection-cover">
                <img :src="collection.cover" alt="收藏视频封面" />
                <div class="collection-info">
                  <h4>{{ collection.title }}</h4>
                  <p>{{ collection.count }}个视频</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>

    <!-- 头像上传输入框 -->
    <input
      type="file"
      ref="fileInput"
      style="display: none"
      accept="image/*"
      @change="handleAvatarUpload"
    />

    <!-- 重置密码弹窗 -->
    <div v-if="showChangePasswordModal" class="change-password-modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>重置密码</h3>
          <button class="close-btn" @click="showChangePasswordModal = false">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="handleChangePassword">
            <div class="form-group">
              <input type="password" v-model="changePasswordForm.oldPassword" placeholder="请输入旧密码" required />
            </div>
            <div class="form-group">
              <input type="password" v-model="changePasswordForm.newPassword" placeholder="请输入新密码" required />
            </div>
            <div class="form-group">
              <input type="password" v-model="changePasswordForm.confirmNewPassword" placeholder="请确认新密码" required />
            </div>
            <button type="submit" class="submit-btn">重置密码</button>
          </form>
        </div>
      </div>
    </div>
    
    <!-- 编辑资料弹窗 -->
    <div v-if="showEditProfileModal" class="edit-profile-modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>编辑资料</h3>
          <button class="close-btn" @click="showEditProfileModal = false">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="handleEditProfile">
            <div class="form-group">
              <label>昵称</label>
              <input type="text" v-model="editProfileForm.nickName" placeholder="请输入昵称" required />
            </div>
            <div class="form-group">
              <label>个人简介</label>
              <textarea v-model="editProfileForm.bio" placeholder="请输入个人简介" rows="3"></textarea>
            </div>
            <button type="submit" class="submit-btn">保存修改</button>
          </form>
        </div>
      </div>
    </div>
    
    <!-- 编辑公告弹窗 -->
    <div v-if="showEditAnnouncementModal" class="edit-announcement-modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>编辑公告</h3>
          <button class="close-btn" @click="showEditAnnouncementModal = false">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="handleEditAnnouncement">
            <div class="form-group">
              <label>公告内容</label>
              <textarea v-model="editAnnouncementForm.content" placeholder="请输入公告内容" rows="5"></textarea>
            </div>
            <button type="submit" class="submit-btn">保存修改</button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Header from '../components/Header.vue';
import videoApi from '../api/video';

export default {
  components: { Header },
  data() {
    return {
      userAvatar: localStorage.getItem('userAvatar') || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20icon&image_size=square',
      userNickname: localStorage.getItem('nickName') || '',
      userId: localStorage.getItem('userId') || '',
      isBigVip: localStorage.getItem('isBigVip') === '1',
      vipLevel: 0,
      vipExpireTime: '',
      showChangePasswordModal: false,
      showEditProfileModal: false,
      showEditAnnouncementModal: false,
      changePasswordForm: {
        oldPassword: '',
        newPassword: '',
        confirmNewPassword: ''
      },
      editProfileForm: {
        nickName: '',
        bio: ''
      },
      editAnnouncementForm: {
        content: ''
      },
      announcementContent: localStorage.getItem('announcementContent') || '',
      collections: [],
      expandedSections: {
        fanView: true,
        collection: true,
        announcement: true
      }
    };
  },
  mounted() {
    this.checkLoginStatus();
    // 监听VIP状态更新事件
    window.addEventListener('vipStatusUpdated', this.handleVipStatusUpdated);
    // 获取收藏列表
    this.getCollectionList();
  },
  beforeDestroy() {
    // 移除事件监听
    window.removeEventListener('vipStatusUpdated', this.handleVipStatusUpdated);
  },
  methods: {
    // VIP状态更新事件处理
    handleVipStatusUpdated(event) {
      if (event.detail) {
        this.isBigVip = event.detail.isVip;
        this.vipLevel = event.detail.level || 0;
        this.vipExpireTime = event.detail.expireTime ? this.formatDate(event.detail.expireTime) : '';
      } else {
        // 如果没有detail数据，重新获取VIP状态
        const token = localStorage.getItem('token');
        const userId = localStorage.getItem('userId');
        if (token && userId) {
          this.checkVipStatus(token, userId);
        }
      }
    },
    checkLoginStatus() {
      const token = localStorage.getItem('token');
      const nickName = localStorage.getItem('nickName');
      const userId = localStorage.getItem('userId');
      if (token && nickName && userId) {
        this.userNickname = nickName;
        this.userId = userId;
        this.fetchAvatar(token, userId);
        this.checkVipStatus(token, userId);
      } else {
        this.userNickname = '';
        this.userId = '';
        this.userAvatar = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20icon&image_size=square';
        this.isBigVip = false;
        this.vipLevel = 0;
        this.vipExpireTime = '';
      }
    },
    async checkVipStatus(token, userId) {
      try {
        const response = await fetch(`/api/payment/getVipStatus?userId=${userId}`, {
          headers: { 'token': token }
        });
        const result = await response.json();
        if (result.code === 200 && result.data) {
          this.isBigVip = result.data.isVip;
          this.vipLevel = result.data.vipLevel || 0;
          this.vipExpireTime = result.data.expireTime ? this.formatDate(result.data.expireTime) : '';
          localStorage.setItem('isBigVip', result.data.isVip ? '1' : '0');
        } else {
          this.isBigVip = false;
          this.vipLevel = 0;
          this.vipExpireTime = '';
        }
      } catch (error) {
        console.error('获取VIP状态失败:', error);
        this.isBigVip = false;
        this.vipLevel = 0;
        this.vipExpireTime = '';
      }
    },
    formatDate(timestamp) {
      if (!timestamp) return '未知';
      const date = new Date(timestamp);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    goToVIPCenter() {
      window.location.href = '/vip-center';
    },
    // 获取头像
    async fetchAvatar(token, userId) {
      try {
        const response = await fetch(`/api/user/userInfo/getAvatar?userId=${userId}&t=${Date.now()}`, {
          headers: {
            'token': token
          }
        });
        if (response.ok) {
          const blob = await response.blob();
          // 将blob转换为base64格式
          const reader = new FileReader();
          reader.onloadend = () => {
            this.userAvatar = reader.result;
            // 保存头像base64到本地存储
            localStorage.setItem('userAvatar', this.userAvatar);
          };
          reader.readAsDataURL(blob);
        } else if (response.status === 401) {
          // token过期，跳转到登录页面
          this.showToast('登录已过期，请重新登录');
          localStorage.removeItem('token');
          localStorage.removeItem('nickName');
          localStorage.removeItem('userId');
          localStorage.removeItem('userAvatar');
          this.$router.push('/login');
        } else {
          console.error('获取头像失败:', response.status);
        }
      } catch (error) {
        console.error('获取头像失败:', error);
      }
    },
    async handleAvatarUpload(e) {
      const file = e.target.files[0];
      if (!file) return;

      try {
        const token = localStorage.getItem('token');
        if (!token) {
          this.showToast('登录已过期，请重新登录');
          this.$router.push('/login');
          return;
        }

        const formData = new FormData();
        formData.append('file', file);
        formData.append('userId', this.userId);

        const response = await fetch('/api/user/userInfo/uploadAvatar', {
          method: 'POST',
          headers: {
            token: token,
          },
          body: formData,
        });

        if (!response.ok) {
          if (response.status === 401) {
            // token过期，跳转到登录页面
            this.showToast('登录已过期，请重新登录');
            localStorage.removeItem('token');
            localStorage.removeItem('nickName');
            localStorage.removeItem('userId');
            localStorage.removeItem('userAvatar');
            this.$router.push('/login');
            return;
          }
          throw new Error('头像上传失败');
        }

        const result = await response.json();
        if (result.code === 200 && result.data) {
          // 上传成功后，重新获取头像
          this.fetchAvatar(token, this.userId);
          this.showToast('头像上传成功');
        } else {
          throw new Error(result.message || '头像上传失败');
        }
      } catch (error) {
        console.error('头像上传失败:', error);
        this.showToast('头像上传失败，请稍后重试');
      }
    },
    toggleUserInfo() {
      // 这里可以实现用户信息下拉框
    },
    goToHome() {
      this.$router.push('/');
    },
    showToast(message) {
      const toast = document.createElement('div');
      toast.className = 'toast';
      toast.textContent = message;
      toast.style.position = 'fixed';
      toast.style.top = '50px';
      toast.style.left = '50%';
      toast.style.transform = 'translateX(-50%) translateY(-20px)';
      toast.style.backgroundColor = 'white';
      toast.style.color = 'black';
      toast.style.padding = '12px 20px';
      toast.style.borderRadius = '4px';
      toast.style.boxShadow = '0 2px 12px 0 rgba(0, 0, 0, 0.1)';
      toast.style.zIndex = '9999';
      toast.style.transition = 'all 0.3s ease';
      toast.style.opacity = '0';
      document.body.appendChild(toast);
      setTimeout(() => {
        toast.style.opacity = '1';
        toast.style.transform = 'translateX(-50%) translateY(0)';
      }, 10);
      setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(-50%) translateY(-20px)';
        setTimeout(() => {
          document.body.removeChild(toast);
        }, 300);
      }, 3000);
    },
    // 处理重置密码
    async handleChangePassword() {
      if (this.changePasswordForm.newPassword !== this.changePasswordForm.confirmNewPassword) {
        this.showToast('两次输入的密码不一致');
        return;
      }
      
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          this.showToast('登录已过期，请重新登录');
          this.$router.push('/login');
          return;
        }
        
        const response = await fetch('/api/user/userInfo/changePwd', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'token': token
          },
          body: JSON.stringify({
            oldPassword: this.changePasswordForm.oldPassword,
            newPassword: this.changePasswordForm.newPassword
          })
        });
        
        if (!response.ok) {
          throw new Error('重置密码失败');
        }
        
        const result = await response.json();
        
        if (result.code === 200) {
          this.showToast('密码修改成功，请重新登录');
          this.showChangePasswordModal = false;
          // 重置表单
          this.changePasswordForm = {
            oldPassword: '',
            newPassword: '',
            confirmNewPassword: ''
          };
          // 退出登录
          localStorage.removeItem('token');
          localStorage.removeItem('nickName');
          localStorage.removeItem('userId');
          localStorage.removeItem('userAvatar');
          this.$router.push('/login');
        } else {
          throw new Error(result.message || '重置密码失败');
        }
      } catch (error) {
        console.error('重置密码失败:', error);
        this.showToast('重置密码失败，请稍后重试');
      }
    },
    // 打开编辑资料弹窗
    openEditProfileModal() {
      this.editProfileForm.nickName = this.userNickname;
      this.showEditProfileModal = true;
    },
    // 处理编辑资料
    handleEditProfile() {
      if (!this.editProfileForm.nickName) {
        this.showToast('昵称不能为空');
        return;
      }
      
      // 这里可以添加调用后端API更新用户资料的逻辑
      localStorage.setItem('nickName', this.editProfileForm.nickName);
      this.userNickname = this.editProfileForm.nickName;
      this.showToast('资料修改成功');
      this.showEditProfileModal = false;
    },
    // 切换展开/收起状态
    toggleSection(section) {
      this.expandedSections[section] = !this.expandedSections[section];
    },
    // 获取收藏列表
    async getCollectionList() {
      try {
        const token = localStorage.getItem('token');
        if (!token) return;

        const response = await fetch('/userCollection/list', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'token': token
          },
          body: JSON.stringify({ pageNo: 1, pageSize: 6 })
        });
        const result = await response.json();
        if (result.code === 200 && result.data) {
          this.collections = await Promise.all(result.data.map(async item => {
            if (item.videoId) {
              try {
                const videoResponse = await videoApi.getVideoDetail({ videoId: item.videoId });
                if (videoResponse.code === 200 && videoResponse.data) {
                  const video = videoResponse.data;
                  return {
                    title: video.title,
                    cover: video.coverFileId ? `/api/file/public/download/${video.coverFileId}` : (video.cover || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=video%20thumbnail&image_size=square'),
                    count: 1,
                    videoId: video.videoId || video.id
                  };
                }
              } catch (error) {
                console.error('获取收藏视频详情失败:', error);
              }
            }
            return null;
          }).filter(item => item !== null));
        }
      } catch (error) {
        console.error('获取收藏列表失败:', error);
      }
    },
    // 跳转到收藏页面
    goToCollection() {
      this.$router.push('/collection');
    },
    // 跳转到视频详情页面
    goToVideoDetail(videoId) {
      if (videoId) {
        this.$router.push(`/video/detail/${videoId}`);
      }
    },
    // 打开编辑公告弹窗
    openEditAnnouncementModal() {
      this.editAnnouncementForm.content = this.announcementContent || '';
      this.showEditAnnouncementModal = true;
    },
    // 处理编辑公告
    handleEditAnnouncement() {
      // 这里可以添加调用后端API更新公告的逻辑
      this.announcementContent = this.editAnnouncementForm.content;
      // 保存公告内容到localStorage
      localStorage.setItem('announcementContent', this.announcementContent);
      this.showToast('公告修改成功');
      this.showEditAnnouncementModal = false;
    },
  },
};
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  background-color: rgba(255, 255, 255, 0.9);
  background-image: url('/src/recourse/background.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
  zoom: 1.25;
}

.profile-info {
  background-color: white;
  margin-bottom: 20px;
}

.profile-banner {
  background-image: url('/src/recourse/background2.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  padding: 40px 0;
}

.banner-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.user-profile {
  display: flex;
  align-items: flex-start;
  gap: 30px;
}

.avatar-wrapper {
  position: relative;
}

.profile-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 4px solid white;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.profile-avatar:hover {
  transform: scale(1.05);
}

.vip-badge {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 3px solid white;
  object-fit: cover;
}

.vip-tag-icon {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  object-fit: cover;
}

.user-details {
  flex: 1;
}

.username-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  font-size: 24px;
  font-weight: bold;
  color: white;
  margin-bottom: 8px;
}

.vip-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  background: linear-gradient(135deg, #ffd700 0%, #ffb700 100%);
  color: #8b4513;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
}

.vip-tag-icon {
  width: 16px;
  height: 16px;
  border-radius: 50%;
}

.user-id {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 16px;
}

.user-stats {
  display: flex;
  gap: 30px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: white;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
}

.stat-label {
  font-size: 12px;
  opacity: 0.8;
}

.profile-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 8px 20px;
  border: 1px solid white;
  border-radius: 4px;
  background-color: transparent;
  color: white;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.vip-buy-btn {
  background: linear-gradient(135deg, #ffd700 0%, #ffb700 100%);
  color: #8b4513 !important;
  border-color: #ffd700 !important;
}

.vip-buy-btn:hover {
  background: linear-gradient(135deg, #ffb700 0%, #ffa500 100%) !important;
}

.vip-renew-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff4757 100%);
  color: white !important;
  border-color: #ff6b6b !important;
}

.vip-renew-btn:hover {
  background: linear-gradient(135deg, #ff4757 0%, #e63946 100%) !important;
}

.profile-nav {
  border-bottom: 1px solid #f0f0f0;
}

.nav-tabs {
  display: flex;
  gap: 30px;
  padding: 0 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 16px 0;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  border-bottom: 2px solid transparent;
  transition: all 0.3s ease;
}

.tab:hover {
  color: #333;
}

.tab.active {
  color: #ff4757;
  border-bottom-color: #ff4757;
}

.tab-icon {
  width: 16px;
  height: 16px;
  object-fit: contain;
}

.icon-img {
  width: 20px;
  height: 20px;
  margin-right: 4px;
  vertical-align: middle;
}

.profile-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.content-section {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.collapsible-section {
  transition: all 0.3s ease;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background-color: #f9f9f9;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.section-header:hover {
  background-color: #f0f0f0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.expand-icon {
  font-size: 12px;
  transition: transform 0.3s ease;
}

.section-content {
  padding: 20px;
  animation: slideDown 0.3s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.section-tabs {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.section-tab {
  padding: 4px 12px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  border-radius: 16px;
  transition: all 0.3s ease;
}

.section-tab.active {
  background-color: #ff4757;
  color: white;
}

.featured-video {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 30px;
  text-align: center;
}

.featured-content {
  display: flex;
  align-items: center;
  gap: 30px;
  justify-content: center;
}

.featured-content img {
  width: 200px;
  height: 150px;
  border-radius: 8px;
  object-fit: cover;
}

.featured-info {
  text-align: left;
  max-width: 400px;
}

.featured-info h4 {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.featured-info p {
  font-size: 14px;
  color: #666;
  margin-bottom: 16px;
  line-height: 1.5;
}

.featured-btn {
  padding: 8px 20px;
  background-color: #ff4757;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.featured-btn:hover {
  background-color: #ff7875;
}

.collection-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.collection-item {
  background-color: #f9f9f9;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s ease;
}

.collection-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.collection-cover {
  position: relative;
  height: 120px;
}

.collection-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.collection-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  color: white;
  padding: 12px;
}

.collection-info h4 {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.collection-info p {
  font-size: 12px;
  opacity: 0.8;
}

.more-link {
  font-size: 14px;
  color: #1890ff;
  cursor: pointer;
  transition: color 0.3s ease;
}

.more-link:hover {
  color: #40a9ff;
  text-decoration: underline;
}

.create-btn {
  width: 100%;
  padding: 12px;
  background-color: #ff4757;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.create-btn:hover {
  background-color: #ff7875;
}

.announcement {
  background-color: #f9f9f9;
  border-radius: 4px;
  padding: 16px;
  text-align: center;
}

.announcement p {
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: color 0.3s ease;
}

.announcement p:hover {
  color: #333;
}

@media (max-width: 768px) {
  .profile-content {
    flex-direction: column;
  }

  .user-profile {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .user-stats {
    justify-content: center;
  }

  .nav-tabs {
    overflow-x: auto;
    white-space: nowrap;
    padding-bottom: 10px;
  }

  .featured-content {
    flex-direction: column;
    text-align: center;
  }

  .featured-info {
    text-align: center;
  }
}

.edit-announcement-btn {
  padding: 6px 12px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: background-color 0.3s ease;
}

.edit-announcement-btn:hover {
  background-color: #40a9ff;
}

/* 弹窗样式 */
.change-password-modal,
.edit-profile-modal,
.edit-announcement-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  width: 400px;
  max-width: 90%;
  overflow: hidden;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
  transition: color 0.3s ease;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
}

.modal-body .form-group {
  margin-bottom: 20px;
  position: relative;
}

.modal-body .form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.modal-body .form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
  resize: vertical;
}

.modal-body .form-group input:focus,
.modal-body .form-group textarea:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.modal-body .submit-btn {
  width: 100%;
  padding: 14px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 20px;
}

.modal-body .submit-btn:hover {
  background-color: #40a9ff;
}

/* 图标样式 */
.search-icon {
  width: 20px;
  height: 20px;
  object-fit: contain;
}
</style>