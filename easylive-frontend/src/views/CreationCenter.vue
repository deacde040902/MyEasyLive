<template>
  <div class="creation-center">
    <Header />
    
    <div class="main-content">
      <div class="sidebar">
        <div class="nav-item" :class="{ active: activeTab === 'upload' }" @click="activeTab = 'upload'">
          <img src="/src/recourse/icon-sendVideo.png" alt="发布视频" />
          <span>发布视频</span>
        </div>
        <div class="nav-item" :class="{ active: activeTab === 'myVideos' }" @click="loadMyVideos">
          <img src="/src/recourse/icon-video.png" alt="我的视频" />
          <span>我的视频</span>
        </div>
        <div class="nav-item" :class="{ active: activeTab === 'data' }" @click="loadDataCenter">
          <img src="/src/recourse/icon-shuju.png" alt="数据中心" />
          <span>数据中心</span>
        </div>
        <div class="nav-item" :class="{ active: activeTab === 'settings' }" @click="activeTab = 'settings'">
          <img src="/src/recourse/icon-opition.png" alt="账号设置" />
          <span>账号设置</span>
        </div>
      </div>

      <div class="content-area">
        <div v-if="activeTab === 'upload'" class="upload-section">
          <h2>{{ isEditing ? '编辑视频' : '发布视频' }}</h2>
          <div class="upload-card">
            <div class="upload-area" @click="triggerUpload" @dragover.prevent @drop.prevent="handleDrop">
              <div class="upload-icon">
                <img src="/src/recourse/icon-postWorkj.png" alt="上传" />
              </div>
              <p>点击或拖拽视频到这里上传</p>
              <p class="hint">支持 MP4、AVI、MOV 等格式，最大 1GB</p>
            </div>
            <input type="file" ref="fileInput" style="display: none" accept="video/*" @change="handleFileSelect" />
            <input type="file" ref="coverInput" style="display: none" accept="image/*" @change="handleCoverSelect" />
            
            <div v-if="uploading" class="upload-progress">
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
              </div>
              <span>{{ uploadProgress }}%</span>
            </div>

            <div v-if="videoInfo" class="video-form">
              <div class="form-group">
                <label>视频封面</label>
                <div class="cover-preview">
                  <img v-if="coverPreview" :src="coverPreview" alt="封面预览" />
                  <div v-else class="cover-placeholder" @click="triggerCoverUpload">
                    <span>上传封面</span>
                  </div>
                </div>
              </div>
              <div class="form-group">
                <label>视频标题</label>
                <input type="text" v-model="videoForm.title" placeholder="请输入视频标题" />
              </div>
              <div class="form-group">
                <label>视频分类</label>
                <select v-model="videoForm.category">
                  <option value="">请选择分类</option>
                  <option value="1">编程</option>
                  <option value="2">人工智能</option>
                  <option value="3">运动</option>
                  <option value="4">美食</option>
                  <option value="5">汽车</option>
                  <option value="6">音乐</option>
                  <option value="7">动画</option>
                  <option value="8">电影</option>
                  <option value="9">知识</option>
                  <option value="10">游戏</option>
                  <option value="11">搞笑</option>
                  <option value="12">短剧</option>
                  <option value="13">户外</option>
                  <option value="14">资讯</option>
                  <option value="15">生活</option>
                  <option value="16">纪录片</option>
                  <option value="17">科技</option>
                  <option value="18">舞蹈</option>
                  <option value="19">VLOG</option>
                </select>
              </div>
              <div class="form-group">
                <label>视频标签</label>
                <input type="text" v-model="videoForm.tags" placeholder="多个标签用逗号分隔" />
              </div>
              <div class="form-group">
                <label>视频简介</label>
                <textarea v-model="videoForm.description" placeholder="请输入视频简介" rows="4"></textarea>
              </div>
              <div class="form-group">
                <label>是否设为大视频</label>
                <label class="checkbox-label">
                  <input type="checkbox" v-model="videoForm.isBigVideo" />
                  <span>推荐到首页轮播</span>
                </label>
              </div>
              <div class="form-actions">
                <button v-if="isEditing" class="cancel-btn" @click="cancelEdit">取消编辑</button>
                <button class="publish-btn" @click="submitVideo">{{ isEditing ? '保存修改' : '发布视频' }}</button>
              </div>
            </div>
          </div>
        </div>

        <div v-else-if="activeTab === 'myVideos'" class="my-videos-section">
          <div class="section-header">
            <h2>我的视频</h2>
            <button v-if="myVideos.length > 0" class="delete-all-btn" @click="deleteAllVideos">删除全部</button>
          </div>
          <div class="video-list">
            <div v-if="loading" class="loading-state">
              <span>加载中...</span>
            </div>
            <div v-else-if="myVideos.length === 0" class="empty-state">
              <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=empty%20video%20library%20illustration&image_size=square" alt="暂无视频" />
              <p>还没有发布任何视频</p>
              <button class="upload-btn" @click="activeTab = 'upload'">去发布</button>
            </div>
            <div v-else class="video-grid">
              <div class="video-item" v-for="video in myVideos" :key="video.videoId">
                <div class="video-cover" @click="goToVideoDetail(video.videoId)">
                  <img :src="getVideoCover(video)" alt="视频封面" />
                  <span class="duration">{{ formatDuration(video.duration) }}</span>
                </div>
                <div class="video-info">
                  <h4>{{ video.title }}</h4>
                  <div class="stats">
                    <span>{{ formatViews(video.playCount) }}播放</span>
                    <span>{{ video.commentCount || 0 }}评论</span>
                  </div>
                  <div class="actions">
                    <button class="action-btn" @click="editVideo(video)">编辑</button>
                    <button class="action-btn delete" @click="deleteVideo(video.videoId)">删除</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else-if="activeTab === 'data'" class="data-section">
          <h2>数据中心</h2>
          <div v-if="loading" class="loading-state">
            <span>加载中...</span>
          </div>
          <div v-else>
            <div class="data-cards">
              <div class="data-card">
                <div class="data-icon views">📊</div>
                <div class="data-info">
                  <p class="data-value">{{ formatViews(dataCenter.totalViews || 0) }}</p>
                  <p class="data-label">总播放量</p>
                </div>
              </div>
              <div class="data-card">
                <div class="data-icon likes">❤️</div>
                <div class="data-info">
                  <p class="data-value">{{ dataCenter.totalLikes || 0 }}</p>
                  <p class="data-label">总点赞数</p>
                </div>
              </div>
              <div class="data-card">
                <div class="data-icon comments">💬</div>
                <div class="data-info">
                  <p class="data-value">{{ dataCenter.totalComments || 0 }}</p>
                  <p class="data-label">总评论数</p>
                </div>
              </div>
              <div class="data-card">
                <div class="data-icon videos">📹</div>
                <div class="data-info">
                  <p class="data-value">{{ dataCenter.videoCount || 0 }}</p>
                  <p class="data-label">发布视频</p>
                </div>
              </div>
            </div>
            <div class="chart-section">
              <h3>近7天播放趋势</h3>
              <div class="chart">
                <div class="chart-bars">
                  <div v-for="(item, index) in dataCenter.weeklyData" :key="index" class="bar-wrapper">
                    <div class="bar" :style="{ height: (maxViews > 0 ? (item.views / maxViews * 100) : 0) + '%' }"></div>
                    <span class="bar-label">{{ item.day }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else-if="activeTab === 'settings'" class="settings-section">
          <h2>账号设置</h2>
          <div class="settings-card">
            <div class="setting-item">
              <label>用户名</label>
              <input type="text" v-model="userSettings.nickname" placeholder="请输入用户名" />
            </div>
            <div class="setting-item">
              <label>头像</label>
              <div class="avatar-upload">
                <img :src="userSettings.avatar" alt="头像" />
                <input type="file" ref="avatarInput" style="display: none" accept="image/*" @change="handleAvatarUpload" />
                <button class="upload-avatar-btn" @click="triggerAvatarUpload">更换头像</button>
              </div>
            </div>
            <div class="setting-item">
              <label>个人简介</label>
              <textarea v-model="userSettings.description" placeholder="介绍一下自己" rows="4"></textarea>
            </div>
            <div class="setting-item">
              <label>邮箱</label>
              <input type="email" v-model="userSettings.email" placeholder="请输入邮箱" />
            </div>
            <button class="save-btn" @click="saveSettings">保存设置</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import Header from '../components/Header.vue';
import videoApi from '../api/video';

export default {
  name: 'CreationCenter',
  components: {
    Header
  },
  setup() {
    const activeTab = ref('upload');
    const fileInput = ref(null);
    const coverInput = ref(null);
    const avatarInput = ref(null);
    const uploading = ref(false);
    const uploadProgress = ref(0);
    const videoInfo = ref(null);
    const coverPreview = ref(null);
    const isEditing = ref(false);
    const editingVideoId = ref(null);
    const loading = ref(false);

    const videoForm = ref({
      title: '',
      category: '',
      tags: '',
      description: '',
      isBigVideo: false
    });

    const myVideos = ref([]);

    const dataCenter = ref({
      totalViews: 0,
      totalLikes: 0,
      totalComments: 0,
      videoCount: 0,
      weeklyData: [
        { day: '周一', views: 0 },
        { day: '周二', views: 0 },
        { day: '周三', views: 0 },
        { day: '周四', views: 0 },
        { day: '周五', views: 0 },
        { day: '周六', views: 0 },
        { day: '周日', views: 0 }
      ]
    });

    const maxViews = computed(() => {
      return Math.max(...dataCenter.value.weeklyData.map(d => d.views), 1);
    });

    const userSettings = ref({
      nickname: '',
      avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20icon&image_size=square',
      description: '',
      email: ''
    });

    const triggerUpload = () => {
      fileInput.value?.click();
    };

    const triggerCoverUpload = () => {
      coverInput.value?.click();
    };

    const handleDrop = (event) => {
      const files = event.dataTransfer.files;
      if (files.length > 0) {
        processFile(files[0]);
      }
    };

    const handleFileSelect = (event) => {
      const files = event.target.files;
      if (files.length > 0) {
        processFile(files[0]);
      }
    };

    const handleCoverSelect = (event) => {
      const file = event.target.files?.[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          coverPreview.value = e.target.result;
        };
        reader.readAsDataURL(file);
      }
    };

    const processFile = (file) => {
      if (!file.type.startsWith('video/')) {
        showToast('请选择视频文件');
        return;
      }
      if (file.size > 1024 * 1024 * 1024) {
        showToast('文件大小不能超过1GB');
        return;
      }
      videoInfo.value = {
        name: file.name,
        size: formatFileSize(file.size),
        type: file.type,
        file: file
      };
    };

    const formatFileSize = (bytes) => {
      if (bytes < 1024) return bytes + ' B';
      if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
      return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
    };

    const submitVideo = async () => {
      if (!isEditing.value && !videoInfo.value) {
        showToast('请先上传视频');
        return;
      }
      if (!videoForm.value.title) {
        showToast('请输入视频标题');
        return;
      }
      if (!videoForm.value.category) {
        showToast('请选择视频分类');
        return;
      }

      uploading.value = true;
      uploadProgress.value = 0;

      try {
        let response;
        
        if (isEditing.value) {
          // 编辑模式：更新视频信息
          const formData = new FormData();
          formData.append('title', videoForm.value.title);
          formData.append('description', videoForm.value.description || '');
          formData.append('categoryId', videoForm.value.category);
          formData.append('tags', videoForm.value.tags || '');
          formData.append('isBigVideo', videoForm.value.isBigVideo ? '1' : '0');
          
          // 如果有新的封面，上传封面
          if (coverPreview.value && coverPreview.value.startsWith('data:')) {
            const blob = await fetch(coverPreview.value).then(res => res.blob());
            formData.append('coverFile', blob, 'cover.png');
          }
          
          response = await videoApi.updateVideo(editingVideoId.value, formData);
        } else {
          // 发布模式：发布新视频
          const formData = new FormData();
          formData.append('file', videoInfo.value.file);
          if (coverPreview.value) {
            const blob = await fetch(coverPreview.value).then(res => res.blob());
            formData.append('coverFile', blob, 'cover.png');
          }
          formData.append('title', videoForm.value.title);
          formData.append('description', videoForm.value.description || '');
          formData.append('categoryId', videoForm.value.category);
          formData.append('tags', videoForm.value.tags || '');
          formData.append('isBigVideo', videoForm.value.isBigVideo ? '1' : '0');

          const interval = setInterval(() => {
            uploadProgress.value += Math.random() * 10;
            if (uploadProgress.value >= 100) {
              uploadProgress.value = 100;
              clearInterval(interval);
            }
          }, 300);

          response = await videoApi.publishVideo(formData);
          clearInterval(interval);
          uploadProgress.value = 100;
        }

        setTimeout(() => {
          uploading.value = false;
          if (response.code === 200 || response.status === 200) {
            showToast(isEditing.value ? '修改成功' : '发布成功');
            resetForm();
            loadMyVideos();
          } else {
            showToast(response.message || response.msg || '操作失败');
          }
        }, 500);
      } catch (error) {
        uploading.value = false;
        showToast('操作失败：' + error.message);
      }
    };

    const resetForm = () => {
      videoInfo.value = null;
      coverPreview.value = null;
      videoForm.value = {
        title: '',
        category: '',
        tags: '',
        description: '',
        isBigVideo: false
      };
      isEditing.value = false;
      editingVideoId.value = null;
    };

    const cancelEdit = () => {
      resetForm();
      activeTab.value = 'myVideos';
    };

    const goToVideoDetail = (videoId) => {
      window.location.href = `/video/detail/${videoId}`;
    };

    const loadMyVideos = async () => {
      activeTab.value = 'myVideos';
      loading.value = true;
      try {
        const response = await fetch('/api/video/list', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ pageNo: 1, pageSize: 20 })
        });
        const data = await response.json();
        if (data.code === 200) {
          myVideos.value = data.data?.list || [];
        } else {
          myVideos.value = [];
        }
      } catch (error) {
        console.error('加载视频列表失败:', error);
        myVideos.value = [];
      } finally {
        loading.value = false;
      }
    };

    const editVideo = (video) => {
      isEditing.value = true;
      editingVideoId.value = video.videoId;
      videoForm.value = {
        title: video.title || '',
        category: video.categoryId?.toString() || '',
        tags: '',
        description: video.description || '',
        isBigVideo: video.isBigVideo === 1
      };
      coverPreview.value = video.cover ? getVideoCover(video) : null;
      videoInfo.value = { name: '视频文件', size: '--', type: '--' };
      activeTab.value = 'upload';
    };

    const deleteVideo = async (videoId) => {
      if (!confirm('确定要删除这个视频吗？')) {
        return;
      }
      try {
        const response = await videoApi.deleteVideo(videoId);
        if (response.code === 200) {
          myVideos.value = myVideos.value.filter(v => v.videoId !== videoId);
          showToast('删除成功');
        } else {
          showToast(response.message || '删除失败');
        }
      } catch (error) {
        showToast('删除失败：' + error.message);
      }
    };

    const deleteAllVideos = async () => {
      if (!confirm('确定要删除所有视频吗？此操作不可撤销！')) {
        return;
      }
      try {
        for (const video of myVideos.value) {
          await videoApi.deleteVideo(video.videoId);
        }
        myVideos.value = [];
        showToast('全部删除成功');
      } catch (error) {
        showToast('删除失败：' + error.message);
      }
    };

    const loadDataCenter = async () => {
      activeTab.value = 'data';
      loading.value = true;
      try {
        const response = await fetch('/api/video/list', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ pageNo: 1, pageSize: 20 })
        });
        const data = await response.json();
        if (data.code === 200) {
          const videos = data.data?.list || [];
          dataCenter.value = {
            totalViews: videos.reduce((sum, v) => sum + (v.playCount || 0), 0),
            totalLikes: videos.reduce((sum, v) => sum + (v.likeCount || 0), 0),
            totalComments: videos.reduce((sum, v) => sum + (v.commentCount || 0), 0),
            videoCount: videos.length,
            weeklyData: [
              { day: '周一', views: Math.floor(Math.random() * 10000) },
              { day: '周二', views: Math.floor(Math.random() * 10000) },
              { day: '周三', views: Math.floor(Math.random() * 10000) },
              { day: '周四', views: Math.floor(Math.random() * 10000) },
              { day: '周五', views: Math.floor(Math.random() * 15000) },
              { day: '周六', views: Math.floor(Math.random() * 20000) },
              { day: '周日', views: Math.floor(Math.random() * 18000) }
            ]
          };
        }
      } catch (error) {
        console.error('加载数据中心失败:', error);
      } finally {
        loading.value = false;
      }
    };

    const getVideoCover = (video) => {
      if (!video.cover && !video.coverFileId && !video.cover_file_id) {
        return 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=video%20thumbnail&image_size=landscape_16_9';
      }
      if (video.cover) {
        let cover = video.cover;
        if (cover.includes('localhost:7071')) {
          if (cover.includes('/resource/public/download')) {
            return cover.replace('http://localhost:7071/resource/public/download', '/api/file/public/download');
          }
          if (cover.includes('/resource/file')) {
            return cover.replace('http://localhost:7071/resource/file', '/api/file');
          }
          if (cover.includes('/resource/')) {
            return cover.replace('http://localhost:7071/resource', '/api/file');
          }
        }
        if (cover.startsWith('/resource/public/download/')) {
          return '/api/file/public/download' + cover.substring('/resource/public/download'.length);
        }
        if (cover.startsWith('/resource/file/')) {
          return '/api/file' + cover.substring('/resource/file'.length);
        }
        if (cover.startsWith('/resource/')) {
          return '/api/file' + cover.substring('/resource'.length);
        }
        if (cover.startsWith('http')) {
          return cover;
        }
        return `/api/file/public/download/${cover}`;
      }
      if (video.coverFileId) {
        return `/api/file/public/download/${video.coverFileId}`;
      }
      if (video.cover_file_id) {
        return `/api/file/public/download/${video.cover_file_id}`;
      }
      return 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=video%20thumbnail&image_size=landscape_16_9';
    };

    const triggerAvatarUpload = () => {
      avatarInput.value?.click();
    };

    const handleAvatarUpload = (event) => {
      const file = event.target.files?.[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          userSettings.value.avatar = e.target.result;
        };
        reader.readAsDataURL(file);
      }
    };

    const saveSettings = () => {
      localStorage.setItem('nickName', userSettings.value.nickname);
      localStorage.setItem('userAvatar', userSettings.value.avatar);
      showToast('保存成功');
    };

    const formatDuration = (duration) => {
      console.log('formatDuration 被调用，输入值：', duration, '类型：', typeof duration);
      
      if (duration === undefined || duration === null || duration === '') {
        return '00:00';
      }
      
      let seconds = parseInt(duration);
      if (isNaN(seconds)) {
        seconds = Number(duration);
        if (isNaN(seconds)) {
          return '00:00';
        }
      }
      
      seconds = Math.max(0, seconds);
      
      const minutes = Math.floor(seconds / 60);
      const secs = seconds % 60;
      const result = `${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
      console.log('formatDuration 输出：', result);
      return result;
    };

    const formatViews = (views) => {
      if (!views) return '0';
      if (views >= 10000) {
        return (views / 10000).toFixed(1) + '万';
      }
      return views.toString();
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

    onMounted(() => {
      const nickName = localStorage.getItem('nickName');
      const userAvatar = localStorage.getItem('userAvatar');
      if (nickName) userSettings.value.nickname = nickName;
      if (userAvatar) userSettings.value.avatar = userAvatar;
    });

    return {
      activeTab,
      fileInput,
      coverInput,
      avatarInput,
      uploading,
      uploadProgress,
      videoInfo,
      coverPreview,
      isEditing,
      videoForm,
      myVideos,
      dataCenter,
      maxViews,
      userSettings,
      loading,
      triggerUpload,
      triggerCoverUpload,
      handleDrop,
      handleFileSelect,
      handleCoverSelect,
      submitVideo,
      cancelEdit,
      goToVideoDetail,
      loadMyVideos,
      editVideo,
      deleteVideo,
      deleteAllVideos,
      loadDataCenter,
      getVideoCover,
      triggerAvatarUpload,
      handleAvatarUpload,
      saveSettings,
      formatDuration,
      formatViews
    };
  }
};
</script>

<style scoped>
.creation-center {
  min-height: 100vh;
  background-color: #f5f5f5;
  zoom: 1.25;
}

.main-content {
  display: flex;
  max-width: 1400px;
  margin: 20px auto;
  padding: 0 20px;
  gap: 20px;
}

.sidebar {
  width: 200px;
  background-color: white;
  border-radius: 8px;
  padding: 16px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.nav-item:hover {
  background-color: #f5f5f5;
}

.nav-item.active {
  background-color: #ffebee;
  color: #ff4757;
}

.nav-item img {
  width: 20px;
  height: 20px;
}

.nav-icon {
  font-size: 20px;
}

.content-area {
  flex: 1;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 20px;
  color: #333;
  margin: 0;
}

.delete-all-btn {
  background-color: #ff4757;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.delete-all-btn:hover {
  background-color: #ff3344;
}

.upload-section h2,
.my-videos-section h2,
.data-section h2,
.settings-section h2 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #333;
}

.upload-card {
  background-color: white;
  border-radius: 8px;
  padding: 24px;
}

.upload-area {
  border: 2px dashed #ddd;
  border-radius: 8px;
  padding: 40px;
  text-align: center;
  cursor: pointer;
  transition: border-color 0.2s;
}

.upload-area:hover {
  border-color: #ff4757;
}

.upload-icon img {
  width: 48px;
  height: 48px;
  margin-bottom: 16px;
}

.upload-area p {
  margin: 0 0 8px 0;
  color: #666;
}

.upload-area .hint {
  font-size: 12px;
  color: #999;
}

.upload-progress {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background-color: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background-color: #ff4757;
  transition: width 0.3s;
}

.video-form {
  margin-top: 24px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #333;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  border-color: #ff4757;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.form-actions {
  display: flex;
  gap: 12px;
}

.publish-btn {
  background-color: #ff4757;
  color: white;
  border: none;
  padding: 12px 32px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.cancel-btn {
  background-color: #f0f0f0;
  color: #666;
  border: none;
  padding: 12px 32px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.cover-preview {
  display: flex;
  gap: 12px;
}

.cover-preview img {
  width: 128px;
  height: 72px;
  object-fit: cover;
  border-radius: 4px;
}

.cover-placeholder {
  width: 128px;
  height: 72px;
  border: 2px dashed #ddd;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #999;
  font-size: 12px;
}

.cover-placeholder:hover {
  border-color: #ff4757;
  color: #ff4757;
}

.loading-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-state img {
  width: 100px;
  height: 100px;
  margin-bottom: 16px;
}

.empty-state p {
  color: #999;
  margin-bottom: 16px;
}

.empty-state .upload-btn {
  background-color: #ff4757;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 4px;
  cursor: pointer;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.video-item {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
}

.video-item .video-cover {
  position: relative;
  height: 180px;
  cursor: pointer;
}

.video-item .video-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-item .duration {
  position: absolute;
  bottom: 4px;
  right: 4px;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 2px;
}

.video-item .video-info {
  padding: 12px;
}

.video-item .video-info h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.video-item .stats {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #999;
  margin-bottom: 12px;
}

.video-item .actions {
  display: flex;
  gap: 8px;
}

.video-item .action-btn {
  flex: 1;
  padding: 6px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}

.video-item .action-btn:hover {
  border-color: #ff4757;
  color: #ff4757;
}

.video-item .action-btn.delete:hover {
  background-color: #ffebee;
}

.data-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.data-card {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.data-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.data-icon.views {
  background-color: #e3f2fd;
}

.data-icon.likes {
  background-color: #ffebee;
}

.data-icon.comments {
  background-color: #e8f5e9;
}

.data-icon.videos {
  background-color: #fff3e0;
}

.data-info .data-value {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}

.data-info .data-label {
  font-size: 12px;
  color: #999;
  margin: 4px 0 0 0;
}

.chart-section {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
}

.chart-section h3 {
  margin: 0 0 20px 0;
  font-size: 16px;
}

.chart-bars {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  height: 200px;
  padding: 20px 0;
}

.bar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.bar {
  width: 32px;
  background-color: #ff4757;
  border-radius: 4px 4px 0 0;
  transition: height 0.3s;
}

.bar-label {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

.settings-card {
  background-color: white;
  border-radius: 8px;
  padding: 24px;
}

.setting-item {
  margin-bottom: 20px;
}

.setting-item label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #333;
}

.setting-item input,
.setting-item textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
}

.setting-item input:focus,
.setting-item textarea:focus {
  border-color: #ff4757;
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-upload img {
  width: 64px;
  height: 64px;
  border-radius: 50%;
}

.upload-avatar-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
  cursor: pointer;
}

.save-btn {
  background-color: #ff4757;
  color: white;
  border: none;
  padding: 12px 32px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
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
  .main-content {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
  }
  
  .video-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .data-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .video-grid {
    grid-template-columns: 1fr;
  }
  
  .data-cards {
    grid-template-columns: 1fr;
  }
}
</style>