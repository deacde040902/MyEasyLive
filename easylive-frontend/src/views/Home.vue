<template>
  <div class="home">
    <Header />

    <div class="carousel">
      <div class="carousel-content">
        <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=anime%20style%20campus%20scene%20with%20students%20walking%20in%20modern%20building&image_size=landscape_16_9" alt="轮播图" />
      </div>
    </div>

    <div class="category-tabs">
      <div class="tab" :class="{ active: currentCategory === '热门' }" @click="switchCategory('热门')">
        <img class="tab-icon" src="/src/recourse/icon-hot.png" alt="热门" />
        <span>热门</span>
      </div>
      <div class="tab" :class="{ active: currentCategory === '编程' }" @click="switchCategory('编程')">编程</div>
      <div class="tab" :class="{ active: currentCategory === '人工智能' }" @click="switchCategory('人工智能')">人工智能</div>
      <div class="tab" :class="{ active: currentCategory === '运动' }" @click="switchCategory('运动')">运动</div>
      <div class="tab" :class="{ active: currentCategory === '美食' }" @click="switchCategory('美食')">美食</div>
      <div class="tab" :class="{ active: currentCategory === '汽车' }" @click="switchCategory('汽车')">汽车</div>
      <div class="tab" :class="{ active: currentCategory === '音乐' }" @click="switchCategory('音乐')">音乐</div>
      <div class="tab" :class="{ active: currentCategory === '动画' }" @click="switchCategory('动画')">动画</div>
      <div class="tab" :class="{ active: currentCategory === '电影' }" @click="switchCategory('电影')">电影</div>
      <div class="tab" :class="{ active: currentCategory === '知识' }" @click="switchCategory('知识')">知识</div>
      <div class="tab" :class="{ active: currentCategory === '游戏' }" @click="switchCategory('游戏')">游戏</div>
      <div class="tab" :class="{ active: currentCategory === '搞笑' }" @click="switchCategory('搞笑')">搞笑</div>
      <div class="tab" :class="{ active: currentCategory === '短剧' }" @click="switchCategory('短剧')">短剧</div>
      <div class="tab" :class="{ active: currentCategory === '户外' }" @click="switchCategory('户外')">户外</div>
      <div class="tab" :class="{ active: currentCategory === '资讯' }" @click="switchCategory('资讯')">资讯</div>
      <div class="tab" :class="{ active: currentCategory === '生活' }" @click="switchCategory('生活')">生活</div>
      <div class="tab" :class="{ active: currentCategory === '纪录片' }" @click="switchCategory('纪录片')">纪录片</div>
      <div class="tab" :class="{ active: currentCategory === '科技' }" @click="switchCategory('科技')">科技</div>
      <div class="tab" :class="{ active: currentCategory === '舞蹈' }" @click="switchCategory('舞蹈')">舞蹈</div>
      <div class="tab" :class="{ active: currentCategory === 'VLOG' }" @click="switchCategory('VLOG')">VLOG</div>
    </div>

    <div class="video-content">
      <div class="featured-video">
        <div v-if="bigVideos.length > 0" class="video-card large carousel-container" @click="goToVideoDetail(this.getVideoId(bigVideos[currentBigVideoIndex]))">
          <div class="carousel-slider" :style="{ transform: `translateX(-${currentBigVideoIndex * 100}%)` }">
            <div v-for="(video, index) in bigVideos" :key="index" class="carousel-slide">
              <div class="video-thumbnail">
                <img v-lazy-load="getVideoCover(video)" src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 128 72'%3E%3Crect fill='%23f0f0f0' width='128' height='72'/%3E%3Ctext fill='%23ccc' font-family='sans-serif' font-size='12' x='50%25' y='50%25' text-anchor='middle' dominant-baseline='middle'%3E加载中...%3C/text%3E%3C/svg%3E" alt="视频封面" />
                <div class="video-stats">
                  <span>{{ video.title }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="carousel-indicators">
            <span v-for="(video, index) in bigVideos" :key="index" class="indicator" :class="{ active: index === currentBigVideoIndex }"></span>
          </div>
          <div class="carousel-controls">
            <button class="control-btn left" @click.stop="prevBigVideo"><img src="/src/recourse/icon-left.png" alt="左" class="arrow-icon" /></button>
            <button class="control-btn right" @click.stop="nextBigVideo"><img src="/src/recourse/icon-right.png" alt="右" class="arrow-icon" /></button>
          </div>
        </div>
        <div v-else class="video-card large">
          <div class="video-thumbnail">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20video%20thumbnail&image_size=landscape_16_9" alt="默认封面" />
            <div class="video-stats">
              <span>暂无大视频</span>
            </div>
          </div>
        </div>
      </div>

      <div class="video-grid">
        <div class="video-card" v-for="(video, index) in videos" :key="index" @click="goToVideoDetail(this.getVideoId(video))" style="cursor: pointer; user-select: none;">
          <div class="video-thumbnail">
            <img v-lazy-load="getVideoCover(video)" src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 128 72'%3E%3Crect fill='%23f0f0f0' width='128' height='72'/%3E%3Ctext fill='%23ccc' font-family='sans-serif' font-size='12' x='50%25' y='50%25' text-anchor='middle' dominant-baseline='middle'%3E加载中...%3C/text%3E%3C/svg%3E" alt="视频封面" style="pointer-events: none;" />
            <span class="video-duration">{{ formatDuration(video.duration) }}</span>
          </div>
          <div class="video-info">
            <h3 class="video-title">{{ video.title }}</h3>
            <div class="video-meta">
              <span class="views">{{ formatViews(video.playCount || video.views) }}</span>
              <span class="comments">{{ video.commentCount || video.comments || 0 }}评论</span>
              <span class="author">{{ getVideoAuthor(video) }}</span>
              <span class="date">{{ formatDate(video.createTime || video.date) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="more-videos">
      <div class="video-card" v-for="(video, index) in moreVideos" :key="index + 10" @click="goToVideoDetail(this.getVideoId(video))" style="cursor: pointer; user-select: none;">
          <div class="video-thumbnail">
            <img v-lazy-load="getVideoCover(video)" src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 128 72'%3E%3Crect fill='%23f0f0f0' width='128' height='72'/%3E%3Ctext fill='%23ccc' font-family='sans-serif' font-size='12' x='50%25' y='50%25' text-anchor='middle' dominant-baseline='middle'%3E加载中...%3C/text%3E%3C/svg%3E" alt="视频封面" style="pointer-events: none;" />
            <span class="video-duration">{{ formatDuration(video.duration) }}</span>
          </div>
        <div class="video-info">
          <h3 class="video-title">{{ video.title }}</h3>
          <div class="video-meta">
            <span class="views">{{ formatViews(video.playCount || video.views) }}</span>
            <span class="comments">{{ video.commentCount || video.comments || 0 }}评论</span>
            <span class="author">{{ getVideoAuthor(video) }}</span>
            <span class="date">{{ formatDate(video.createTime || video.date) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import videoApi from '../api/video';
import Header from '../components/Header.vue';

export default {
  components: {
    Header
  },
  mounted() {
    this.getVideoList();
    this.getBigVideos();
    this.startCarouselTimer();
  },
  beforeUnmount() {
    if (this.carouselTimer) {
      clearInterval(this.carouselTimer);
    }
  },
  data() {
    return {
      videos: [],
      moreVideos: [],
      bigVideos: [],
      currentBigVideoIndex: 0,
      currentCategory: '热门',
      categoryMap: {
        '热门': null,
        '编程': 1,
        '人工智能': 2,
        '运动': 3,
        '美食': 4,
        '汽车': 5,
        '音乐': 6,
        '动画': 7,
        '电影': 8,
        '知识': 9,
        '游戏': 10,
        '搞笑': 11,
        '短剧': 12,
        '户外': 13,
        '资讯': 14,
        '生活': 15,
        '纪录片': 16,
        '科技': 17,
        '舞蹈': 18
      }
    };
  },
  methods: {
    async getVideoList() {
      try {
        const categoryId = this.categoryMap[this.currentCategory];
        const params = {
          pageNo: 1,
          pageSize: 10,
          categoryId: categoryId,
          tag: this.currentCategory,
          orderBy: this.currentCategory === '热门' ? 'play_count' : 'create_time'
        };
        const response = await videoApi.getVideoList(params);
        if (response.code === 200 && response.data) {
           let videoList = Array.isArray(response.data) ? response.data : (response.data.list || []);
            videoList = videoList.filter(video => video.isBigVideo !== 1);
            if (this.currentCategory === '热门') {
              videoList.sort((a, b) => (b.playCount || b.views || 0) - (a.playCount || a.views || 0));
            }
            this.videos = videoList.slice(0, 3);
            this.moreVideos = videoList.slice(3);
          }
      } catch (error) {
        console.error('获取视频列表失败:', error);
        this.videos = [];
        this.moreVideos = [];
      }
    },
    switchCategory(category) {
      this.currentCategory = category;
      this.getVideoList();
    },
    goToVideoDetail(videoId) {
      if (videoId !== undefined && videoId !== null && videoId !== '') {
        this.$router.push(`/video/detail/${videoId}`);
      }
    },
    getVideoId(video) {
      return video.videoId || video.video_id || video.id;
    },
    getVideoAuthor(video) {
      if (video.authorName) {
        return video.authorName;
      }
      if (video.author) {
        return video.author;
      }
      return '未知作者';
    },
    processMediaUrl(url) {
    if (!url) return '';
    // 完整 URL 包含 localhost:7071
    if (url.includes('localhost:7071')) {
        // 统一去掉域名和端口，保留 /resource 起头的路径
        const path = url.replace(/^https?:\/\/localhost:7071/, '');
        return this.processMediaUrl(path); // 递归处理剩下的相对路径
    }
    // 如果是 /resource/file/ 开头，说明是后端静态资源，映射到前端代理 /api/file/
    if (url.startsWith('/resource/file/')) {
        return '/api/file' + url.substring('/resource/file'.length);
    }
    // 如果是 /resource/ 开头，说明是后端静态资源，映射到前端代理 /api/file/
    if (url.startsWith('/resource/')) {
        return '/api/file' + url.substring('/resource'.length);
    }
    // 已经是 /api/file/ 或 http 的直接返回
    if (url.startsWith('/api/file/') || url.startsWith('http')) {
        return url;
    }
    // 如果是纯文件 ID，构建标准路径
    if (url.match(/^[a-f0-9]+_\d{10,}\.\w+$/i) || url.match(/^\d+[a-z]?$/)) {
        return `/api/file/public/download/${url}`;
    }
    return url;
},
    getVideoCover(video) {
      if (video.cover) {
        return this.processMediaUrl(video.cover);
      }
      const coverFileId = video.coverFileId || video.cover_file_id;
      if (coverFileId) {
        return `/api/file/public/download/${coverFileId}`;
      }
      if (video.videoUrl) {
        return `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=video%20thumbnail%20${encodeURIComponent(video.title)}&image_size=landscape_16_9`;
      }
      return `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20video%20thumbnail&image_size=landscape_16_9`;
    },
    formatViews(views) {
      if (!views) return '0';
      if (views >= 10000) {
        return (views / 10000).toFixed(1) + '万';
      }
      return views;
    },
    formatDuration(duration) {
        if (duration === undefined || duration === null || duration === '') {
            return '00:01';
        }
        
        let seconds = parseInt(duration);
        if (isNaN(seconds)) {
            seconds = Number(duration);
            if (isNaN(seconds)) {
                return '00:01';
            }
        }
        
        seconds = Math.max(1, seconds);
        
        const minutes = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
    },
    formatDate(timestamp) {
      if (!timestamp) return '';
      const date = new Date(timestamp);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    async getBigVideos() {
      try {
        const response = await videoApi.getBigVideos();
        if (response.code === 200 && response.data) {
          this.bigVideos = Array.isArray(response.data) ? response.data : [];
        }
      } catch (error) {
        console.error('获取大视频列表失败:', error);
        this.bigVideos = [];
      }
    },
    startCarouselTimer() {
      this.carouselTimer = setInterval(() => {
        this.nextBigVideo();
      }, 5000);
    },
    nextBigVideo() {
      if (this.bigVideos.length > 0) {
        if (this.currentBigVideoIndex < this.bigVideos.length - 1) {
          this.currentBigVideoIndex++;
        } else {
          if (this.carouselTimer) {
            clearInterval(this.carouselTimer);
          }
        }
      }
    },
    prevBigVideo() {
      if (this.bigVideos.length > 0) {
        if (this.currentBigVideoIndex > 0) {
          this.currentBigVideoIndex--;
        }
      }
    }
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background-color: #fff;
  zoom: 1.25;
}

.carousel {
  width: 100%;
  height: 200px;
  overflow: hidden;
  margin-bottom: 20px;
}

.carousel-content img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.category-tabs {
  display: flex;
  gap: 16px;
  padding: 0 40px 20px;
  overflow-x: auto;
  border-bottom: 1px solid #f0f0f0;
  max-width: 1400px;
  margin: 0 auto;
}

.tab {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  white-space: nowrap;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  border-radius: 16px;
  transition: all 0.3s ease;
}

.tab:hover {
  background-color: #f0f0f0;
}

.tab-icon {
  width: 16px;
  height: 16px;
  object-fit: contain;
}

.tab.active {
  background-color: #ff4757;
  color: white;
}

.video-content {
  display: flex;
  gap: 20px;
  padding: 20px 40px;
  max-width: 1400px;
  margin: 0 auto;
}

.featured-video {
  flex: 1.2;
}

.carousel-container {
  position: relative;
  overflow: hidden;
}

.carousel-slider {
  display: flex;
  transition: transform 0.5s ease-in-out;
  height: 100%;
}

.carousel-slide {
  flex: 0 0 100%;
  height: 100%;
}

.carousel-slide .video-thumbnail {
  height: 100%;
}

.video-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.video-card {
  background-color: #f9f9f9;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s ease;
  cursor: pointer;
}

.video-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.video-card.large {
  height: 240px;
  cursor: pointer;
}

.video-thumbnail {
  position: relative;
  width: 100%;
  height: 120px;
  overflow: hidden;
}

.video-duration {
  position: absolute;
  bottom: 4px;
  right: 4px;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  font-size: 10px;
  padding: 2px 4px;
  border-radius: 2px;
}

.video-card.large .video-thumbnail {
  height: 100%;
}

.video-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-stats {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 8px 12px;
  font-size: 14px;
}

.carousel-indicators {
  position: absolute;
  bottom: 40px;
  left: 12px;
  display: flex;
  gap: 4px;
}

.indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
}

.indicator.active {
  background-color: white;
}

.carousel-controls {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  transform: translateY(-50%);
  padding: 0 12px;
}

.control-btn {
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.video-info {
  padding: 12px;
}

.video-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 8px;
  line-height: 1.4;
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.video-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 12px;
  color: #999;
}

.more-videos {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  padding: 0 40px 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.search-icon {
  width: 20px;
  height: 20px;
  object-fit: contain;
}

.arrow-icon {
  width: 20px;
  height: 20px;
  object-fit: contain;
}

@media (max-width: 768px) {
  .video-content {
    flex-direction: column;
  }
  
  .video-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .more-videos {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}

@media (max-width: 480px) {
  .video-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
