<template>
  <div class="video-detail">
    <!-- 顶部导航栏 - 使用 Header 组件 -->
    <Header />

    <!-- 视频内容区域 -->
    <div class="video-content">
      <!-- 左侧视频区域 -->
      <div class="video-main">
        <!-- 视频信息（移到视频上方） -->
        <div class="video-info-header">
          <div class="video-info-left">
            <h1 class="video-title">{{ video.title || '视频标题' }}</h1>
            <div class="video-meta">
              <span class="views">{{ formatViews(video.playCount || 0) }}次播放</span>
              <span class="comments">{{ video.commentCount || 0 }}条评论</span>
              <span class="date">{{ formatDate(video.createTime || video.publishTime) }}</span>
            </div>
          </div>
        </div>

        <!-- 视频播放器 -->
        <div class="video-player">
          <video v-if="video.videoUrl" ref="videoPlayer" controls class="player"></video>
          <div v-else class="video-placeholder">
            <img :src="getVideoCover(video)" alt="视频封面" class="placeholder-image" @error="handleCoverError" />
            <div class="placeholder-content">
              <h3>视频加载中...</h3>
              <p>请稍后再试</p>
            </div>
          </div>
        </div>

        <!-- 视频信息 -->
        <div class="video-info">
          <!-- 视频互动区 -->
          <div class="video-actions">
            <button class="action-btn" @click="likeVideo">
              <img src="/src/recourse/icon-dianzhan.png" alt="点赞" class="action-icon" />
              <span>{{ video.likeCount || 0 }}</span>
            </button>
            <button class="action-btn" @click="dislikeVideo">
              <img src="/src/recourse/icon-dislike.png" alt="点踩" class="action-icon" />
              <span>{{ video.dislikeCount || 0 }}</span>
            </button>
            <button class="action-btn" @click="collectVideo">
              <img src="/src/recourse/icon-collection.png" alt="收藏" class="action-icon" />
              <span>{{ video.collectCount || 0 }}</span>
            </button>
            <button class="action-btn" @click="coinVideo">
              <img src="/src/recourse/icon-coin.png" alt="投币" class="action-icon" />
              <span>{{ video.coinCount || 0 }}</span>
            </button>
            <button class="action-btn" @click="shareVideo">
              <img src="/src/recourse/icon-share.png" alt="分享" class="action-icon" />
              <span>分享</span>
            </button>
          </div>

          <!-- 弹幕输入区 -->
          <div class="danmaku-input">
            <span class="danmaku-count">1人正在看，已装填50条弹幕</span>
            <div class="danmaku-controls">
              <button class="danmaku-toggle" :class="{ off: !danmakuEnabled }" @click="toggleDanmaku">
                <img :src="danmakuEnabled ? '/src/recourse/icon-danmuOn.png' : '/src/recourse/icon-danmuOff.png'" alt="弹幕开关" />
              </button>
              <button class="danmaku-settings" @click="showDanmakuSettings = !showDanmakuSettings">
                <img src="/src/recourse/icon-danmuSet.png" alt="弹幕设置" />
              </button>
              <div class="input-container">
                <input type="text" placeholder="发个友善的弹幕见证当下" v-model="danmakuText" :disabled="!danmakuEnabled" />
                <button class="send-btn" @click="sendDanmaku" :disabled="!danmakuEnabled">发送</button>
              </div>
            </div>
          </div>

          <!-- 评论区 -->
          <div class="comment-section">
            <h3 class="section-title">评论 ({{ video.commentCount || 0 }})</h3>
            <div class="comment-tabs">
              <button class="tab-btn active">最新</button>
              <button class="tab-btn">最热</button>
            </div>
            <div class="comment-input">
              <img :src="userAvatar" alt="用户头像" class="user-avatar" />
              <div class="input-area">
                <textarea placeholder="写下你的评论..." v-model="commentText" maxlength="500"></textarea>
                <div class="comment-actions">
                  <div class="emoji-btn">😀</div>
                  <div class="image-btn">🖼️</div>
                  <button class="publish-btn" @click="publishComment">发布</button>
                </div>
              </div>
            </div>
            <!-- 评论列表 -->
            <div class="comment-list">
              <div class="comment-item" v-for="(comment, index) in comments" :key="index">
                <img :src="comment.avatar" alt="评论者头像" class="comment-avatar" />
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="comment-author">{{ comment.author }}</span>
                    <span class="comment-time">{{ comment.time }}</span>
                  </div>
                  <div class="comment-text">{{ comment.content }}</div>
                  <div class="comment-actions">
                    <span class="like-btn">👍 {{ comment.likes }}</span>
                    <span class="reply-btn">回复</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧边栏 -->
      <div class="video-sidebar">
        <!-- 上传者信息 -->
        <div class="uploader-info">
          <div class="uploader-header">
            <img :src="getAuthorAvatar()" alt="作者头像" class="uploader-avatar" @error="handleAvatarError" @click="goToProfile" />
            <div class="uploader-details">
              <h3 class="uploader-name">{{ getAuthorName(video) }}</h3>
              <p class="uploader-fans">{{ authorFans || 0 }}粉丝</p>
            </div>
            <button class="follow-btn">关注</button>
          </div>
          <button class="visit-btn">访问主页</button>
        </div>

        <!-- 弹幕列表（预留） -->
        <div class="danmaku-list">
          <div class="danmaku-header">
            <h3>弹幕列表</h3>
            <button class="toggle-btn">▼</button>
          </div>
          <div class="danmaku-content">
            <div class="danmaku-item" v-for="(danmaku, index) in danmakus" :key="index">
              <span class="danmaku-time">{{ danmaku.time }}</span>
              <span class="danmaku-text">{{ danmaku.content }}</span>
              <span class="danmaku-date">{{ danmaku.date }}</span>
            </div>
          </div>
        </div>

        <!-- 推荐视频 -->
        <div class="recommend-videos">
          <h3 class="section-title">推荐视频</h3>
          <div class="video-card" v-for="(recommendVideo, index) in recommendVideos" :key="index" @click="goToVideoDetail(recommendVideo.videoId)">
            <div class="video-card-cover">
              <img :src="recommendVideo.cover" alt="视频封面" class="cover-image" />
              <span class="video-duration">{{ formatDuration(recommendVideo.duration) }}</span>
            </div>
            <div class="video-card-info">
              <h4 class="video-card-title">{{ recommendVideo.title }}</h4>
              <p class="video-card-author">{{ recommendVideo.authorName }}</p>
              <p class="video-card-stats">{{ formatViews(recommendVideo.playCount || 0) }}播放 · {{ formatDate(recommendVideo.createTime || recommendVideo.publishTime) }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import videoApi from '../api/video';
import userApi from '../api/user';
import Hls from 'hls.js'; // 需 npm install hls.js
import Header from '../components/Header.vue';

export default {
  components: { Header },
  data() {
    return {
      video: {},
      authorAvatar: '',
      authorFans: 0,
      hlsInstance: null, // 保存 HLS 实例以便销毁
      transcodingPollInterval: null, // 转码轮询定时器
      transcodingAttempts: 0, // 转码检查次数
      maxTranscodingAttempts: 30, // 最大转码检查次数（约5分钟）
      danmakuEnabled: true,
      showDanmakuSettings: false,
      isLiked: false,
      isCollected: false,
      danmakuText: '',
      commentText: '',
      isLoggedIn: false,
      userAvatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20icon&image_size=square',
      userNickname: '',
      userId: '',
      registerTime: '',
      comments: [
        {
          avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%201&image_size=square',
          author: '123珊珊',
          time: '2025-01-03 18:38:50',
          content: 'theSea 可以',
          likes: 11
        },
        {
          avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%202&image_size=square',
          author: 'theSea',
          time: '2025-012-10 10:48:45',
          content: '精彩片段',
          likes: 5
        }
      ],
      danmakus: [
        { time: '00:00', content: '胜衣装魔法', date: '2025-01-22 23:06:37' },
        { time: '00:00', content: 'dddxai', date: '2025-11-19 18:57:46' },
        { time: '00:00', content: '666', date: '2025-05-11 08:04:21' },
        { time: '00:01', content: '123', date: '2025-02-27 22:54:52' },
        { time: '00:01', content: '哈哈哈哈', date: '2025-09-17 23:02:23' },
        { time: '00:02', content: 'jikeji', date: '2024-12-24 23:33:05' },
        { time: '00:02', content: '111', date: '2025-01-19 14:35:47' },
        { time: '00:02', content: '路过', date: '2025-08-12 15:58:19' },
        { time: '00:03', content: '111', date: '2025-03-31 02:07:33' },
        { time: '00:03', content: 'hahahah', date: '2025-07-20 21:06:38' },
        { time: '00:03', content: '牛啊', date: '2025-09-25 10:08:22' },
        { time: '00:04', content: '00000', date: '2025-05-03 18:32:48' },
        { time: '00:04', content: '好好好', date: '2025-06-21 07:16:42' },
        { time: '00:05', content: '11111', date: '2025-03-19 17:14:33' }
      ],
      recommendVideos: [],
      showCollectionDropdown: false,
      collectionList: []
    };
  },
  mounted() {
    this.getVideoDetail();
    this.checkLoginStatus();
    this.getCollectionList();
    this.recordHistory();
    // 监听localStorage变化，实时更新头像
    window.addEventListener('storage', this.handleStorageChange);
  },
  beforeUnmount() {
    // 移除事件监听器
    window.removeEventListener('storage', this.handleStorageChange);
    // 组件销毁时释放 HLS 资源
    if (this.hlsInstance) {
      this.hlsInstance.destroy();
      this.hlsInstance = null;
    }
    // 清除转码轮询定时器
    if (this.transcodingPollInterval) {
      clearInterval(this.transcodingPollInterval);
      this.transcodingPollInterval = null;
    }
  },
  methods: {
    // 检查登录状态
    checkLoginStatus() {
      const token = localStorage.getItem('token');
      const nickName = localStorage.getItem('nickName');
      const userId = localStorage.getItem('userId');
      const userAvatar = localStorage.getItem('userAvatar');
      if (token && nickName && userId) {
        this.isLoggedIn = true;
        this.userNickname = nickName;
        this.userId = userId;
        this.userAvatar = userAvatar || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20icon&image_size=square';
        // 使用fetch请求获取头像，包含token
        this.fetchAvatar(token, userId);
        // 从后端获取用户信息（包括注册时间）
        this.getUserInfo(token, userId);
      }
    },
    // 处理localStorage变化
    handleStorageChange(event) {
      if (event.key === 'userAvatar') {
        this.userAvatar = event.newValue || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20icon&image_size=square';
      }
      if (event.key === 'nickName') {
        this.userNickname = event.newValue || '';
      }
      if (event.key === 'userId') {
        this.userId = event.newValue || '';
      }
      if (event.key === 'token') {
        if (event.newValue) {
          this.isLoggedIn = true;
          this.checkLoginStatus();
        } else {
          this.isLoggedIn = false;
          this.userNickname = '';
          this.userId = '';
          this.userAvatar = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20icon&image_size=square';
        }
      }
    },
    // 获取用户信息
    async getUserInfo(token, userId) {
      try {
        const response = await fetch(`/api/user/userInfo/getUserInfo/${userId}`, {
          headers: {
            'token': token
          }
        });
        if (response.ok) {
          const result = await response.json();
          if (result.code === 200 && result.data) {
            // 格式化注册时间
            if (result.data.createTime) {
              this.registerTime = this.formatDate(result.data.createTime);
            }
          }
        } else if (response.status === 401) {
          // token过期，跳转到登录页面
          this.showToast('登录已过期，请重新登录');
          localStorage.removeItem('token');
          localStorage.removeItem('nickName');
          localStorage.removeItem('userId');
          localStorage.removeItem('userAvatar');
          this.isLoggedIn = false;
          this.showUserInfo = false;
          this.userNickname = '';
          this.userId = '';
          this.userAvatar = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20icon&image_size=square';
          this.registerTime = '未知';
          this.$router.push('/login');
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
      }
    },
    // 格式化日期
    formatDate(timestamp) {
      if (!timestamp) return '';
      const date = new Date(timestamp);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
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
          this.isLoggedIn = false;
          this.showUserInfo = false;
          this.userNickname = '';
          this.userId = '';
          this.userAvatar = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20icon&image_size=square';
          this.$router.push('/login');
        } else {
          console.error('获取头像失败:', response.status);
        }
      } catch (error) {
        console.error('获取头像失败:', error);
      }
    },
    async getVideoDetail() {
      try {
        const videoId = this.$route.params.videoId;
        const response = await videoApi.getVideoDetail({ videoId });
        if (response.code === 200 && response.data) {
          this.video = response.data;

          // 视频播放：使用 hls.js 播放 m3u8
          if (this.video.videoUrl) {
            // 清除之前的轮询定时器（如果存在）
            if (this.transcodingPollInterval) {
              clearInterval(this.transcodingPollInterval);
              this.transcodingPollInterval = null;
            }
            this.$nextTick(() => {
              const processedUrl = this.processMediaUrl(this.video.videoUrl);
              this.initPlayer(processedUrl);
            });
          } else {
            console.warn('视频暂未转码完成（videoUrl 为空），开始轮询检查');
            this.showToast('视频转码中，请稍候...');
            this.startTranscodingPolling();
          }

          // 获取作者信息
          await this.getAuthorInfo();
          // 获取视频评论
          this.getVideoComments();
          // 获取弹幕列表
          this.getDanmakus();
          // 获取推荐视频
          this.getRecommendVideos();
        }
      } catch (error) {
        console.error('获取视频详情失败:', error);
        this.showToast('获取视频详情失败，请稍后再试');
      }
    },

    // 搜索功能
    search() {
      if (this.searchQuery.trim()) {
        this.$router.push(`/search?keyword=${encodeURIComponent(this.searchQuery)}`);
      }
    },

    // 切换用户信息下拉框
    toggleUserInfo() {
      this.showUserInfo = !this.showUserInfo;
    },

    // 切换弹幕开关
    toggleDanmaku() {
      this.danmakuEnabled = !this.danmakuEnabled;
      // 可以在这里添加弹幕显示/隐藏的逻辑
    },

    // 发送弹幕
    async sendDanmaku() {
      if (this.danmakuText.trim()) {
        try {
          const videoId = this.$route.params.videoId;
          const playTime = this.videoPlayer?.currentTime || 0;
          const response = await videoApi.addDanmaku(videoId, this.danmakuText, playTime);
          const isSuccess = response.code === 200 || response.status === 200;
          if (isSuccess) {
            this.danmakuText = '';
            this.showToast('弹幕发送成功');
            this.getDanmakus();
          } else {
            this.showToast(response.message || '弹幕发送失败，请稍后再试');
          }
        } catch (error) {
          console.error('发送弹幕失败:', error);
          if (error.response && error.response.status === 200) {
            this.danmakuText = '';
            this.showToast('弹幕发送成功');
            this.getDanmakus();
          } else {
            this.showToast('弹幕发送失败，请稍后再试');
          }
        }
      }
    },

    // 发布评论
    async publishComment() {
      if (this.commentText.trim()) {
        try {
          const videoId = this.$route.params.videoId;
          const response = await videoApi.addComment(videoId, {
            content: this.commentText
          });
          console.log('评论响应:', response);
          // 检查响应是否成功 - 支持多种响应格式
          const isSuccess = response.code === 200 || 
                           response.status === 200 || 
                           response.success === true ||
                           (response.data !== undefined && response.code !== undefined && response.code >= 200 && response.code < 300);
          if (isSuccess) {
            await this.getVideoComments();
            this.commentText = '';
            this.showToast('评论发布成功');
          } else {
            this.showToast(response.message || response.msg || '评论发布失败，请稍后再试');
          }
        } catch (error) {
          console.error('发布评论失败:', error);
          // 如果HTTP状态码是200但进入catch，可能是响应体解析问题
          if (error.response && error.response.status === 200) {
            await this.getVideoComments();
            this.commentText = '';
            this.showToast('评论发布成功');
          } else {
            this.showToast('评论发布失败，请稍后再试');
          }
        }
      }
    },
    // 点赞/取消点赞
    async likeVideo() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          this.showToast('请先登录');
          return;
        }

        const videoId = this.$route.params.videoId;
        let response;
        
        if (this.isLiked) {
          response = await videoApi.unlikeVideo(videoId);
          if (response.code === 200 || response.status === 200) {
            this.isLiked = false;
            this.video.likeCount = Math.max(0, (this.video.likeCount || 0) - 1);
            this.showToast('已取消点赞');
          } else {
            this.showToast(response.message || '取消点赞失败');
          }
        } else {
          response = await videoApi.likeVideo(videoId);
          if (response.code === 200 || response.status === 200) {
            this.isLiked = true;
            this.video.likeCount = (this.video.likeCount || 0) + 1;
            this.showToast('点赞成功');
          } else {
            this.showToast(response.message || '点赞失败');
          }
        }
      } catch (error) {
        console.error('点赞操作失败:', error);
        if (error.response && error.response.status === 200) {
          this.isLiked = !this.isLiked;
          if (this.isLiked) {
            this.video.likeCount = (this.video.likeCount || 0) + 1;
            this.showToast('点赞成功');
          } else {
            this.video.likeCount = Math.max(0, (this.video.likeCount || 0) - 1);
            this.showToast('已取消点赞');
          }
        } else {
          this.showToast('操作失败，请稍后再试');
        }
      }
    },
    // 点踩
    async dislikeVideo() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          this.showToast('请先登录');
          return;
        }

        const videoId = this.$route.params.videoId;
        // 假设后端有点踩API
        // const response = await videoApi.dislikeVideo(videoId);
        
        // 模拟成功
        this.video.dislikeCount = (this.video.dislikeCount || 0) + 1;
        this.showToast('点踩成功');
      } catch (error) {
        console.error('点踩失败:', error);
        this.showToast('点踩失败，请稍后再试');
      }
    },
    // 收藏/取消收藏
    async collectVideo() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          this.showToast('请先登录');
          return;
        }

        const videoId = this.$route.params.videoId;
        let response;
        
        if (this.isCollected) {
          response = await videoApi.uncollectVideo(videoId);
          if (response.code === 200 || response.status === 200) {
            this.isCollected = false;
            this.video.collectCount = Math.max(0, (this.video.collectCount || 0) - 1);
            this.showToast('已取消收藏');
            this.getCollectionList();
          } else {
            this.showToast(response.message || '取消收藏失败');
          }
        } else {
          response = await videoApi.collectVideo(videoId);
          if (response.code === 200 || response.status === 200) {
            this.isCollected = true;
            this.video.collectCount = (this.video.collectCount || 0) + 1;
            this.showToast('收藏成功');
            this.getCollectionList();
          } else {
            this.showToast(response.message || '收藏失败');
          }
        }
      } catch (error) {
        console.error('收藏操作失败:', error);
        if (error.response && error.response.status === 200) {
          this.isCollected = !this.isCollected;
          if (this.isCollected) {
            this.video.collectCount = (this.video.collectCount || 0) + 1;
            this.showToast('收藏成功');
          } else {
            this.video.collectCount = Math.max(0, (this.video.collectCount || 0) - 1);
            this.showToast('已取消收藏');
          }
          this.getCollectionList();
        } else {
          this.showToast('操作失败，请稍后再试');
        }
      }
    },
    // 投币
    async coinVideo() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          this.showToast('请先登录');
          return;
        }

        const videoId = this.$route.params.videoId;
        // 假设后端有投币API
        // const response = await videoApi.coinVideo(videoId, 1); // 投1个币
        
        // 模拟成功
        this.video.coinCount = (this.video.coinCount || 0) + 1;
        this.showToast('投币成功');
      } catch (error) {
        console.error('投币失败:', error);
        this.showToast('投币失败，请稍后再试');
      }
    },
    // 分享
    shareVideo() {
      try {
        const videoUrl = window.location.href;
        if (navigator.share) {
          // 使用Web Share API
          navigator.share({
            title: this.video.title,
            text: this.video.description,
            url: videoUrl
          });
        } else {
          // 复制链接到剪贴板
          navigator.clipboard.writeText(videoUrl).then(() => {
            this.showToast('链接已复制到剪贴板');
          }).catch(() => {
            this.showToast('复制失败，请手动复制链接');
          });
        }
      } catch (error) {
        console.error('分享失败:', error);
        this.showToast('分享失败，请稍后再试');
      }
    },

    // 跳转到视频详情页
    goToVideoDetail(videoId) {
      window.location.href = `/video/detail/${videoId}`;
    },

    // 跳转到收藏页面
    goToCollection() {
      window.location.href = '/collection';
    },

    // 跳转到历史记录页面
    goToHistory() {
      window.location.href = '/history';
    },

    // 跳转到消息页面
    goToMessage() {
      window.location.href = '/message';
    },

    // 跳转到个人主页
    goToProfile() {
      window.location.href = '/profile';
    },

    // 记录观看历史
    async recordHistory() {
      try {
        const token = localStorage.getItem('token');
        if (!token) return;
        
        const videoId = this.$route.params.videoId;
        await fetch('/history/addHistory', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'token': token
          },
          body: `videoId=${encodeURIComponent(videoId)}&watchTime=0`
        });
      } catch (error) {
        console.error('记录观看历史失败:', error);
      }
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
          body: JSON.stringify({ pageNo: 1, pageSize: 10 })
        });
        const result = await response.json();
        if (result.code === 200 && result.data) {
          // 获取每个视频的详细信息
          this.collectionList = await Promise.all(result.data.map(async item => {
            if (item.videoId) {
              try {
                const videoResponse = await videoApi.getVideoDetail(item.videoId);
                if (videoResponse.code === 200 && videoResponse.data) {
                  const video = videoResponse.data;
                  return {
                    videoId: video.videoId || video.id,
                    title: video.title,
                    coverFileId: video.coverFileId,
                    cover: video.cover,
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
    },

    // 处理下拉菜单滚动
    handleDropdownScroll(event) {
      const dropdownContent = this.$refs.dropdownContent;
      if (dropdownContent) {
        dropdownContent.scrollTop += event.deltaY;
      }
    },

    // 跳转到登录页
    goToLogin() {
      this.$router.push('/login');
    },

    // 跳转到上传页
    goToUpload() {
      this.$router.push('/upload');
    },

    // 跳转到创作中心
    goToCreationCenter() {
      window.location.href = '/creation-center';
    },

    // 格式化视频时长
    formatDuration(seconds) {
      if (!seconds) return '00:00';
      const minutes = Math.floor(seconds / 60);
      const remainingSeconds = seconds % 60;
      return `${String(minutes).padStart(2, '0')}:${String(remainingSeconds).padStart(2, '0')}`;
    },

    // 开始转码轮询
    startTranscodingPolling() {
      if (this.transcodingPollInterval) {
        clearInterval(this.transcodingPollInterval);
      }
      this.transcodingAttempts = 0;
      this.transcodingPollInterval = setInterval(async () => {
        try {
          this.transcodingAttempts++;
          const videoId = this.$route.params.videoId;
          const response = await videoApi.getVideoDetail({ videoId });
          if (response.code === 200 && response.data) {
            const newVideo = response.data;
            if (newVideo.videoUrl) {
              // 转码完成，停止轮询并初始化播放器
              clearInterval(this.transcodingPollInterval);
              this.transcodingPollInterval = null;
              this.video = newVideo;
              this.showToast('转码完成，正在播放...');
              this.$nextTick(() => {
                const processedUrl = this.processMediaUrl(this.video.videoUrl);
                this.initPlayer(processedUrl);
              });
            } else if (this.transcodingAttempts >= this.maxTranscodingAttempts) {
              // 达到最大尝试次数，停止轮询
              clearInterval(this.transcodingPollInterval);
              this.transcodingPollInterval = null;
              this.showToast('视频转码时间过长，请稍后再试');
            } else {
              console.log(`转码中，第 ${this.transcodingAttempts} 次检查`);
            }
          }
        } catch (error) {
          console.error('转码轮询失败:', error);
        }
      }, 10000); // 每10秒检查一次
    },

    // 处理媒体URL，确保在开发环境中能够正确访问
    processMediaUrl(url) {
      if (!url) return '';
      // 如果是localhost:7071的HLS地址，使用代理路径
      if (url.includes('localhost:7071') && url.includes('/resource/hls')) {
        return url.replace('http://localhost:7071/resource/hls', '/resource/hls');
      }
      // 如果是localhost:7071的文件地址，使用代理路径
      if (url.includes('localhost:7071')) {
        return url.replace('http://localhost:7071/resource/file', '/api/file');
      }
      // 如果是相对路径的resource/hls，使用正确的代理路径
      if (url.startsWith('/resource/hls/')) {
        return url;
      }
      // 如果是相对路径的resource/file，使用正确的代理路径
      if (url.startsWith('/resource/file/')) {
        return '/api/file' + url.substring('/resource/file'.length);
      }
      // 如果是相对路径的resource，使用正确的代理路径
      if (url.startsWith('/resource/')) {
        return '/api/file' + url.substring('/resource'.length);
      }
      // 如果是完整的URL，直接返回
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url;
      }
      // 如果已经是/api/file/路径，直接返回
      if (url.startsWith('/api/file/')) {
        return url;
      }
      // 如果是直接的资源文件名（如acdbca8f8f2d4b7_1777105168655.png），使用正确的代理路径
      if (url.match(/^[a-f0-9_]+\.(png|jpg|jpeg|gif|mp4|m3u8)$/i)) {
        return `/api/file/public/download/${url}`;
      }
      // 如果是文件ID格式（如file_1777711165456或2048658220070653954），构建完整的URL
      if (url.match(/^(file_)?[a-f0-9_]+$/i)) {
        return `/api/file/public/download/${url}`;
      }
      // 默认返回原URL
      return url;
    },

    // 使用 hls.js 初始化播放器
    initPlayer(url) {
      const video = this.$refs.videoPlayer;
      if (!video) return;

      video.crossOrigin = 'anonymous'; // 可选，解决跨域问题

      if (Hls.isSupported()) {
        const hls = new Hls();
        hls.loadSource(url);
        hls.attachMedia(video);
        hls.on(Hls.Events.MANIFEST_PARSED, () => {
          video.play().catch(e => console.warn('自动播放被阻止:', e));
        });
        hls.on(Hls.Events.ERROR, (event, data) => {
          console.error('HLS 播放错误:', data);
          if (data.fatal) {
            switch (data.type) {
              case Hls.ErrorTypes.NETWORK_ERROR:
                console.error('网络错误，尝试恢复...');
                hls.startLoad();
                break;
              case Hls.ErrorTypes.MEDIA_ERROR:
                console.error('媒体错误，尝试恢复...');
                hls.recoverMediaError();
                break;
              default:
                console.error('无法恢复的错误，销毁 HLS');
                hls.destroy();
                break;
            }
          }
        });
        this.hlsInstance = hls;
      } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
        // Safari 原生支持
        video.src = url;
        video.addEventListener('loadedmetadata', () => {
          video.play();
        });
      } else {
        console.error('浏览器不支持播放 HLS 流');
        this.showToast('您的浏览器不支持播放此视频，请使用最新版 Edge、Chrome 或 Safari');
      }
    },

    // 返回首页
    goToHome() {
      this.$router.push('/');
    },

    // 跳转到个人主页
    goToProfile() {
      const userId = this.video.userId || this.video.authorId;
      if (userId) {
        this.$router.push(`/profile/${userId}`);
      } else {
        this.showToast('无法获取用户信息');
      }
    },

    // 跳转到创作中心
    goToCreationCenter() {
      this.$router.push('/creation-center');
    },

    // 获取作者信息（补充粉丝数等）
    async getAuthorInfo() {
      if (this.video.authorFans != null) {
        this.authorFans = this.video.authorFans;
      }
      // 优先从后端获取完整的用户信息
      const userId = this.video.userId || this.video.authorId;
      if (userId) {
        const userInfo = await this.getUserInfo(userId);
        if (userInfo) {
          this.authorAvatar = userInfo.avatar ? this.processMediaUrl(userInfo.avatar) : `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture&image_size=square`;
          this.authorName = userInfo.nickName || userInfo.userName || this.authorName;
          return;
        }
      }
      // 如果获取不到用户信息，使用视频中的头像
      if (this.video.authorAvatar) {
        this.authorAvatar = this.processMediaUrl(this.video.authorAvatar);
      } else if (this.video.avatar) {
        this.authorAvatar = this.processMediaUrl(this.video.avatar);
      } else {
        this.authorAvatar = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture&image_size=square`;
      }
    },

    // 获取用户信息
    async getUserInfo(userId) {
      try {
        const response = await userApi.getUserInfo(userId);
        if (response.code === 200 && response.data) {
          return response.data;
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
      }
      return null;
    },

    // 获取视频封面
    getVideoCover(video) {
      // 如果有封面，使用封面
      if (video.cover) {
        return this.processMediaUrl(video.cover);
      }
      // 如果有coverUrl，使用coverUrl
      if (video.coverUrl) {
        return this.processMediaUrl(video.coverUrl);
      }
      // 如果有cover_file_id（下划线格式，来自数据库），使用直接下载路径
      if (video.cover_file_id) {
        return `/api/file/public/download/${video.cover_file_id}`;
      }
      // 如果有coverFileId（驼峰格式），使用直接下载路径
      if (video.coverFileId) {
        return `/api/file/public/download/${video.coverFileId}`;
      }
      // 如果有视频地址，可以尝试生成封面
      if (video.videoUrl) {
        // 暂时返回基于视频标题生成的封面
        return `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=video%20thumbnail%20${encodeURIComponent(video.title)}&image_size=landscape_16_9`;
      }
      // 默认封面
      return `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20video%20thumbnail&image_size=landscape_16_9`;
    },
    
    // 获取视频评论
    async getVideoComments() {
      try {
        const videoId = this.$route.params.videoId;
        const response = await videoApi.getVideoComments(videoId, {
          pageNo: 1,
          pageSize: 20
        });
        if (response.code === 200) {
          let commentList = [];
          if (response.data && response.data.list) {
            commentList = response.data.list;
          } else if (Array.isArray(response.data)) {
            commentList = response.data;
          }
          // 转换为前端期望的格式，并获取用户信息
          this.comments = await Promise.all(commentList.map(async comment => {
            const userInfo = comment.userId ? await this.getUserInfo(comment.userId) : null;
            return {
              id: comment.commentId || comment.id,
              avatar: userInfo?.avatar || comment.avatar || `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20${comment.userId}&image_size=square`,
              author: userInfo?.nickName || userInfo?.userName || comment.author || comment.nickName || '用户' + comment.userId?.substring(0, 6),
              time: this.formatDateTime(comment.createTime),
              content: comment.content,
              likes: comment.likeCount || comment.likes || 0
            };
          }));
          // 更新评论计数
          this.video.commentCount = this.comments.length;
        }
      } catch (error) {
        console.error('获取评论列表失败:', error);
        this.comments = [];
      }
    },

    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime) return '';
      const date = new Date(dateTime);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      const seconds = String(date.getSeconds()).padStart(2, '0');
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    },
    
    // 获取弹幕列表
    async getDanmakus() {
      try {
        const videoId = this.$route.params.videoId;
        const response = await videoApi.getDanmakus(videoId);
        if (response.code === 200 && response.data) {
          const danmakuList = Array.isArray(response.data) ? response.data : (response.data.list || []);
          this.danmakus = danmakuList.map(danmaku => ({
            id: danmaku.id || danmaku.danmakuId,
            time: this.formatDanmakuTime(danmaku.time || danmaku.playTime),
            content: danmaku.content,
            date: danmaku.createTime ? this.formatDanmakuDate(danmaku.createTime) : ''
          }));
        } else {
          this.danmakus = [];
        }
      } catch (error) {
        console.error('获取弹幕列表失败:', error);
        this.danmakus = [];
      }
    },

    // 格式化弹幕时间显示
    formatDanmakuTime(seconds) {
      if (!seconds) return '00:00';
      const mins = Math.floor(seconds / 60);
      const secs = Math.floor(seconds % 60);
      return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
    },

    // 格式化弹幕日期显示
    formatDanmakuDate(dateTime) {
      if (!dateTime) return '';
      const date = new Date(dateTime);
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      return `${month}-${day} ${hours}:${minutes}`;
    },

    // 获取作者头像（直接使用 data 中的 authorAvatar）
    getAuthorAvatar() {
      return this.authorAvatar || `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture&image_size=square`;
    },
    
    // 处理头像加载失败
    handleAvatarError(e) {
      // 使用默认头像
      e.target.src = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture&image_size=square`;
    },

    // 处理封面加载失败
    handleCoverError(e) {
      // 使用默认封面
      e.target.src = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20video%20thumbnail&image_size=landscape_16_9`;
    },

    // 获取作者名称
    getAuthorName(video) {
      return video.authorName || video.author || '未知作者';
    },

    // 获取推荐视频
    async getRecommendVideos() {
      try {
        const response = await videoApi.getVideoRecommend({ pageNo: 1, pageSize: 7 });
        if (response.code === 200 && response.data) {
          const list = Array.isArray(response.data) ? response.data : (response.data.list || []);
          this.recommendVideos = list.slice(0, 7).map(video => ({
            videoId: video.videoId || video.video_id || video.id,
            title: video.title,
            cover: this.getVideoCover(video),
            authorName: video.authorName || video.author_name || video.author,
            playCount: video.playCount || video.views || 0,
            createTime: video.createTime || video.publishTime,
            duration: video.duration || 0
          }));
        }
      } catch (error) {
        console.error('获取推荐视频失败:', error);
      }
    },

    // 格式化播放量
    formatViews(views) {
      if (!views) return '0';
      if (views >= 10000) {
        return (views / 10000).toFixed(1) + '万';
      }
      return views;
    },

    // 格式化日期
    formatDate(timestamp) {
      if (!timestamp) return '';
      const date = new Date(timestamp);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },

    // 轻提示
    showToast(message) {
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
    },
  }
};
</script>

<style scoped>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.video-detail {
  min-height: 100vh;
  background-color: #f5f5f5;
  zoom: 1.25;
}

/* 顶部导航栏 */
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

/* 视频内容区域 */
.video-content {
  display: flex;
  gap: 20px;
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

/* 视频信息头部（视频上方） */
.video-info-header {
  background-color: white;
  padding: 16px;
  border-radius: 8px 8px 0 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 0;
}

.video-info-left {
  flex: 1;
}

.video-info-header .video-title {
  font-size: 18px;
  margin-bottom: 8px;
  color: #333;
}

.video-info-header .video-meta {
  display: flex;
  gap: 16px;
  color: #666;
  font-size: 14px;
  margin: 0;
}

/* 左侧视频区域 */
.video-main {
  flex: 1;
  min-width: 0;
}

/* 视频播放器 */
.video-player {
  position: relative;
  margin-bottom: 0;
  border-radius: 0;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}

.player {
  width: 100%;
  height: 450px; /* 调整视频大小 */
  object-fit: cover;
}

.video-placeholder {
  width: 100%;
  height: 450px;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.placeholder-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: absolute;
  top: 0;
  left: 0;
  opacity: 0.5;
}

.placeholder-content {
  text-align: center;
  z-index: 1;
  color: #333;
}

/* 视频信息 */
.video-info {
  background-color: white;
  padding: 20px;
  border-radius: 0 0 8px 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-top: 0;
}

.video-title {
  font-size: 20px;
  margin-bottom: 12px;
  color: #333;
}

.video-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  color: #666;
  font-size: 14px;
}

.video-meta .comments {
  display: inline-flex;
  align-items: center;
}

.video-meta .comments::before {
  content: '💬';
  margin-right: 6px;
  font-size: 14px;
}

/* 视频互动区 */
.video-actions {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  background: transparent;
  border: none;
  cursor: pointer;
  color: #666;
  font-size: 14px;
  transition: color 0.3s ease;
}

.action-btn:hover {
  color: #ff4757;
}

.action-icon {
  width: 20px;
  height: 20px;
}

/* 弹幕输入区 */
.danmaku-input {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
  padding: 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.danmaku-count {
  font-size: 14px;
  color: #666;
}

.danmaku-controls {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
}

.danmaku-toggle,
.danmaku-settings {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.danmaku-toggle:hover,
.danmaku-settings:hover {
  background-color: #e8e8e8;
}

.danmaku-toggle img,
.danmaku-settings img {
  width: 20px;
  height: 20px;
}

.danmaku-toggle.off {
  opacity: 0.5;
}

.input-container {
  flex: 1;
  display: flex;
  gap: 10px;
}

.input-container input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  outline: none;
  font-size: 14px;
}

.send-btn {
  background-color: #ff4757;
  color: white;
  border: none;
  padding: 0 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

/* 评论区 */
.comment-section {
  margin-top: 30px;
}

.section-title {
  font-size: 18px;
  margin-bottom: 16px;
  color: #333;
}

.comment-tabs {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.tab-btn {
  background: transparent;
  border: none;
  padding: 0;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  position: relative;
}

.tab-btn.active {
  color: #ff4757;
  font-weight: bold;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #ff4757;
}

.comment-input {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.input-area {
  flex: 1;
  min-width: 0;
}

.input-area textarea {
  width: 100%;
  min-height: 80px;
  padding: 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  resize: vertical;
  outline: none;
  font-size: 14px;
  font-family: inherit;
}

.comment-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 10px;
}

.emoji-btn, .image-btn {
  font-size: 18px;
  cursor: pointer;
}

.publish-btn {
  background-color: #ff4757;
  color: white;
  border: none;
  padding: 6px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.comment-author {
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-text {
  font-size: 14px;
  line-height: 1.5;
  color: #333;
  margin-bottom: 8px;
}

.comment-actions {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #666;
}

.like-btn, .reply-btn {
  cursor: pointer;
}

/* 右侧边栏 */
.video-sidebar {
  width: 300px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 上传者信息 */
.uploader-info {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.uploader-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.uploader-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
}

.uploader-details {
  flex: 1;
  min-width: 0;
}

.uploader-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 4px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.uploader-fans {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.uploader-header .follow-btn {
  background-color: #ff4757;
  color: white;
  border: none;
  padding: 6px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.visit-btn {
  width: 100%;
  background-color: #f0f0f0;
  color: #333;
  border: none;
  padding: 8px 0;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s ease;
}

.visit-btn:hover {
  background-color: #e0e0e0;
}

/* 弹幕列表 */
.danmaku-list {
  background-color: white;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.danmaku-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.danmaku-header h3 {
  font-size: 16px;
  color: #333;
  margin: 0;
}

.toggle-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 12px;
  color: #666;
}

.danmaku-content {
  max-height: 300px;
  overflow-y: auto;
  font-size: 14px;
}

.danmaku-item {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.danmaku-time {
  width: 50px;
  color: #999;
  flex-shrink: 0;
}

.danmaku-text {
  flex: 1;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.danmaku-date {
  width: 120px;
  color: #999;
  flex-shrink: 0;
  text-align: right;
  font-size: 12px;
}

/* 推荐视频 */
.recommend-videos {
  background-color: white;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.video-card {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.video-card:hover {
  transform: translateY(-2px);
}

.video-card-cover {
  width: 120px;
  height: 68px;
  position: relative;
  flex-shrink: 0;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
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

.video-card-info {
  flex: 1;
  min-width: 0;
}

.video-card-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  white-space: normal;
}

.video-card-author {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.video-card-stats {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 轻提示样式 */
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

/* 响应式设计 */
@media (max-width: 1024px) {
  .video-content {
    flex-direction: column;
  }
  
  .video-sidebar {
    width: 100%;
    order: -1;
  }
  
  .player {
    height: 400px;
  }
  
  .video-placeholder {
    height: 400px;
  }
}

@media (max-width: 768px) {
  .header-center .search-box {
    width: 250px;
  }
  
  .header-right .user-actions {
    gap: 12px;
  }
  
  .player {
    height: 300px;
  }
  
  .video-placeholder {
    height: 300px;
  }
  
  .video-actions {
    gap: 20px;
  }
}

/* 收藏下拉菜单样式 */
.collection-item {
  position: relative;
}

.icon-text {
  font-size: 12px;
  margin-top: 2px;
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

.empty-collection p {
  margin: 0;
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
</style>