<template>
  <div class="upload-container">
    <header class="upload-header">
      <div class="header-left">
        <button class="back-btn" @click="goToHome"><img src="/src/recourse/icon-left.png" alt="返回" class="icon-img" /> 返回</button>
        <h1 class="title">投稿</h1>
      </div>
      <div class="publish-buttons">
        <button class="publish-btn" @click="publishVideo(false)" :disabled="!isFormValid">发布</button>
        <button class="big-video-btn" @click="publishVideo(true)" :disabled="!isFormValid">大视频发布</button>
      </div>
    </header>

    <div class="upload-content">
      <!-- 视频上传区域 -->
      <div class="upload-section">
        <h2 class="section-title">视频上传</h2>
        <div class="video-upload-area" @click="triggerFileInput">
          <input type="file" ref="fileInput" style="display: none" accept="video/*" @change="handleVideoUpload" />
          <div v-if="!videoFile" class="upload-placeholder">
            <img src="/src/recourse/icon-postWorkj.png" alt="上传" class="upload-icon" />
            <p>点击上传视频</p>
            <p class="hint">支持MP4、AVI、MKV等格式</p>
          </div>
          <div v-else class="video-preview">
            <video :src="videoUrl" controls class="preview-video"></video>
            <div class="video-info">
              <span class="file-name">{{ videoFile.name }}</span>
              <span class="file-size">{{ formatFileSize(videoFile.size) }}</span>
            </div>
          </div>
        </div>
        <div v-if="uploadProgress > 0" class="upload-progress">
          <div class="progress-bar" :style="{ width: uploadProgress + '%' }"></div>
          <span class="progress-text">{{ uploadProgress }}%</span>
        </div>
      </div>

      <!-- 封面上传区域 -->
      <div class="upload-section">
        <h2 class="section-title">视频封面</h2>
        <div class="cover-upload-area" @click="triggerCoverInput">
          <input type="file" ref="coverInput" style="display: none" accept="image/*" @change="handleCoverUpload" />
          <div v-if="!coverFile" class="upload-placeholder">
            <img src="/src/recourse/icon-postWorkj.png" alt="上传" class="upload-icon" />
            <p>点击上传封面</p>
            <p class="hint">支持JPG、PNG等格式，建议比例16:9</p>
          </div>
          <div v-else class="cover-preview">
            <img :src="coverUrl" alt="封面预览" class="preview-cover" />
            <button class="change-cover-btn" @click.stop="triggerCoverInput">更换封面</button>
          </div>
        </div>
      </div>

      <!-- 视频信息填写 -->
      <div class="form-section">
        <h2 class="section-title">视频信息</h2>
        <div class="form-group">
          <label>视频标题</label>
          <input type="text" v-model="videoForm.title" placeholder="请输入视频标题" maxlength="50" />
        </div>
        <div class="form-group">
          <label>视频简介</label>
          <textarea v-model="videoForm.description" placeholder="请输入视频简介" rows="4" maxlength="200"></textarea>
        </div>
        <div class="form-group">
          <label>选择分类</label>
          <select v-model="videoForm.categoryId">
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
          </select>
        </div>
        <div class="form-group">
          <label>添加标签</label>
          <input type="text" v-model="videoForm.tags" placeholder="请输入标签，用逗号分隔" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import videoApi from '../api/video';

export default {
  data() {
    return {
      videoFile: null,
      videoUrl: '',
      coverFile: null,
      coverUrl: '',
      uploadProgress: 0,
      videoForm: {
        title: '',
        description: '',
        categoryId: '',
        tags: ''
      }
    };
  },
  computed: {
    isFormValid() {
      return this.videoFile && this.coverFile && this.videoForm.title && this.videoForm.categoryId;
    }
  },
  methods: {
    goToHome() {
      this.$router.push('/');
    },
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    triggerCoverInput() {
      this.$refs.coverInput.click();
    },
    handleVideoUpload(e) {
      const file = e.target.files[0];
      if (file) {
        this.videoFile = file;
        this.videoUrl = URL.createObjectURL(file);
      }
    },
    handleCoverUpload(e) {
      const file = e.target.files[0];
      if (file) {
        this.coverFile = file;
        this.coverUrl = URL.createObjectURL(file);
      }
    },
    formatFileSize(size) {
      if (size < 1024) {
        return size + ' B';
      } else if (size < 1024 * 1024) {
        return (size / 1024).toFixed(2) + ' KB';
      } else {
        return (size / (1024 * 1024)).toFixed(2) + ' MB';
      }
    },
    async publishVideo(isBigVideo) {
      if (!this.isFormValid) {
        this.showToast('请填写完整视频信息');
        return;
      }

      try {
        const token = localStorage.getItem('token');
        if (!token) {
          this.showToast('请先登录');
          this.$router.push('/login');
          return;
        }

        // 准备表单数据
        const formData = new FormData();
        formData.append('file', this.videoFile);
        formData.append('coverFile', this.coverFile);
        formData.append('title', this.videoForm.title);
        formData.append('description', this.videoForm.description);
        formData.append('categoryId', this.videoForm.categoryId);
        formData.append('tags', this.videoForm.tags);
        formData.append('isBigVideo', isBigVideo ? 1 : 0);

        // 调用后端API发布视频
        const response = await videoApi.publishVideo(formData);
        if (response.code === 200) {
          this.showToast(isBigVideo ? '大视频发布成功' : '视频发布成功');
          // 跳转到首页
          this.$router.push('/');
        } else {
          this.showToast('视频发布失败：' + (response.message || '未知错误'));
        }
      } catch (error) {
        console.error('视频发布失败:', error);
        this.showToast('视频发布失败，请稍后重试');
      }
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
    }
  }
};
</script>

<style scoped>
.upload-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  zoom: 1.25;
}

.upload-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 14px;
  color: #333;
}

.icon-img {
  width: 20px;
  height: 20px;
  vertical-align: middle;
}

.title {
  font-size: 18px;
  font-weight: bold;
  margin: 0;
}

.publish-btn {
  background-color: #ff4757;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s ease;
}

.publish-btn:hover:not(:disabled) {
  background-color: #ff3742;
}

.publish-buttons {
  display: flex;
  gap: 10px;
}

.publish-btn:disabled {
  background-color: #d9d9d9;
  cursor: not-allowed;
}

.big-video-btn {
  background-color: #ff6b35;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s ease;
}

.big-video-btn:hover:not(:disabled) {
  background-color: #ff5200;
}

.big-video-btn:disabled {
  background-color: #d9d9d9;
  cursor: not-allowed;
}

.upload-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.upload-section,
.form-section {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 20px 0;
  color: #333;
}

.video-upload-area {
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  padding: 40px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.video-upload-area:hover {
  border-color: #ff4757;
  background-color: #f9f0f0;
}

.cover-upload-area {
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cover-upload-area:hover {
  border-color: #ff4757;
  background-color: #f9f0f0;
}

.cover-preview {
  position: relative;
  display: inline-block;
}

.preview-cover {
  max-width: 100%;
  max-height: 200px;
  border-radius: 4px;
}

.change-cover-btn {
  margin-top: 10px;
  padding: 6px 12px;
  background-color: #ff4757;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.upload-icon {
  width: 64px;
  height: 64px;
  opacity: 0.6;
}

.upload-placeholder p {
  margin: 0;
  color: #666;
}

.hint {
  font-size: 12px;
  color: #999;
}

.video-preview {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.preview-video {
  width: 100%;
  max-height: 400px;
  border-radius: 4px;
}

.video-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #666;
}

.upload-progress {
  margin-top: 16px;
  height: 8px;
  background-color: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}

.progress-bar {
  height: 100%;
  background-color: #ff4757;
  transition: width 0.3s ease;
}

.progress-text {
  position: absolute;
  top: -20px;
  right: 0;
  font-size: 12px;
  color: #666;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #ff4757;
  box-shadow: 0 0 0 2px rgba(255, 71, 87, 0.2);
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}
</style>