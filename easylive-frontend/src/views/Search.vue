<template>
  <div class="search-page">
    <Header />
    
    <div class="search-content">
      <div class="search-header">
        <div class="search-box-container">
          <input 
            type="text" 
            class="search-input" 
            v-model="keyword" 
            placeholder="搜索"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">
            <img src="/src/recourse/icon-search.png" alt="搜索" />
          </button>
        </div>
      </div>

      <div class="search-tabs">
        <div 
          class="tab" 
          :class="{ active: activeTab === 'comprehensive' }" 
          @click="activeTab = 'comprehensive'; handleSearch()"
        >综合排序</div>
        <div 
          class="tab" 
          :class="{ active: activeTab === 'playCount' }" 
          @click="activeTab = 'playCount'; handleSearch()"
        >最多播放</div>
        <div 
          class="tab" 
          :class="{ active: activeTab === 'publishTime' }" 
          @click="activeTab = 'publishTime'; handleSearch()"
        >最新发布</div>
        <div 
          class="tab" 
          :class="{ active: activeTab === 'danmaku' }" 
          @click="activeTab = 'danmaku'; handleSearch()"
        >最多弹幕</div>
        <div 
          class="tab" 
          :class="{ active: activeTab === 'favorites' }" 
          @click="activeTab = 'favorites'; handleSearch()"
        >最多收藏</div>
      </div>

      <div v-if="loading" class="loading">
        <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=loading%20spinner&image_size=square" alt="加载中" class="loading-icon" />
        <p>搜索中...</p>
      </div>

      <div v-else-if="searchResults.length === 0 && hasSearched" class="empty-results">
        <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=empty%20search%20results%20illustration&image_size=square" alt="暂无结果" />
        <p>没有找到相关视频</p>
      </div>

      <div v-else-if="searchResults.length > 0" class="search-results">
        <div class="video-card" v-for="(video, index) in searchResults" :key="index" @click="goToVideoDetail(video.videoId)">
          <div class="video-thumbnail">
            <img v-lazy-load="getVideoCover(video)" src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 128 72'%3E%3Crect fill='%23f0f0f0' width='128' height='72'/%3E%3Ctext fill='%23ccc' font-family='sans-serif' font-size='12' x='50%25' y='50%25' text-anchor='middle' dominant-baseline='middle'%3E加载中...%3C/text%3E%3C/svg%3E" alt="视频封面" />
            <span class="video-duration">{{ formatDuration(video.duration) }}</span>
          </div>
          <div class="video-info">
            <h3 class="video-title">{{ video.title }}</h3>
            <p class="video-author">{{ video.authorName || video.author || '未知作者' }}</p>
            <div class="video-meta">
              <span>{{ formatViews(video.playCount || video.views) }}播放</span>
              <span>{{ video.commentCount || 0 }}评论</span>
              <span>{{ formatDate(video.createTime || video.publishTime) }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="search-history-section">
        <div class="section-header">
          <h3>搜索历史</h3>
          <button class="clear-history" @click="clearSearchHistory">清空</button>
        </div>
        <div class="history-tags">
          <span 
            v-for="(item, index) in searchHistory" 
            :key="index" 
            class="history-tag"
            @click="searchKeyword(item)"
          >
            {{ item }}
            <span class="remove-tag" @click.stop="removeHistory(item)">×</span>
          </span>
          <div v-if="searchHistory.length === 0" class="empty-history">
            <p>暂无搜索历史</p>
          </div>
        </div>

        <div class="hot-search-section">
          <div class="section-header">
            <h3>热搜</h3>
          </div>
          <div class="hot-search-list">
            <div 
              v-for="(item, index) in hotSearches" 
              :key="index" 
              class="hot-search-item"
              @click="searchKeyword(item.keyword)"
            >
              <span class="hot-rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
              <span class="hot-keyword">{{ item.keyword }}</span>
              <span class="hot-count">{{ item.count }}热</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import videoApi from '../api/video';
import Header from '../components/Header.vue';

export default {
  name: 'Search',
  components: {
    Header
  },
  setup() {
    const keyword = ref('');
    const activeTab = ref('comprehensive');
    const searchResults = ref([]);
    const loading = ref(false);
    const hasSearched = ref(false);
    const searchHistory = ref([]);
    const hotSearches = ref([]);

    const getSearchHistory = () => {
      const history = localStorage.getItem('searchHistory');
      if (history) {
        searchHistory.value = JSON.parse(history);
      }
    };

    const saveSearchHistory = (keyword) => {
      if (!keyword.trim()) return;
      searchHistory.value = searchHistory.value.filter(k => k !== keyword);
      searchHistory.value.unshift(keyword);
      if (searchHistory.value.length > 10) {
        searchHistory.value = searchHistory.value.slice(0, 10);
      }
      localStorage.setItem('searchHistory', JSON.stringify(searchHistory.value));
    };

    const clearSearchHistory = () => {
      searchHistory.value = [];
      localStorage.removeItem('searchHistory');
    };

    const removeHistory = (item) => {
      searchHistory.value = searchHistory.value.filter(k => k !== item);
      localStorage.setItem('searchHistory', JSON.stringify(searchHistory.value));
    };

    const searchKeyword = (kw) => {
      keyword.value = kw;
      handleSearch();
    };

    const handleSearch = async () => {
      if (!keyword.value.trim()) return;
      
      loading.value = true;
      hasSearched.value = true;
      saveSearchHistory(keyword.value);

      try {
        const response = await videoApi.search({
          keyword: keyword.value,
          pageNo: 1,
          pageSize: 20,
          sortType: activeTab.value
        });

        if (response.code === 200 && response.data) {
          searchResults.value = Array.isArray(response.data) ? response.data : (response.data.list || []);
        } else {
          searchResults.value = [];
        }
      } catch (error) {
        console.error('搜索失败:', error);
        searchResults.value = [];
      } finally {
        loading.value = false;
      }
    };

    const goToVideoDetail = (videoId) => {
      window.location.href = `/video/detail/${videoId}`;
    };

    const getVideoCover = (video) => {
      if (video.cover) {
        if (video.cover.includes('localhost:7071')) {
          return video.cover.replace('http://localhost:7071/resource/file', '/api/file');
        }
        if (video.cover.startsWith('/resource/file/')) {
          return '/api/file' + video.cover.substring('/resource/file'.length);
        }
        if (video.cover.startsWith('/resource/')) {
          return '/api/file' + video.cover.substring('/resource'.length);
        }
        if (video.cover.startsWith('http://') || video.cover.startsWith('https://')) {
          return video.cover;
        }
        if (video.cover.match(/^[a-f0-9]+_\d{10,}\.\w+$/i)) {
          return `/api/file/public/download/${video.cover}`;
        }
        return video.cover;
      }
      if (video.coverFileId) {
        return `/api/file/public/download/${video.coverFileId}`;
      }
      if (video.videoUrl) {
        return `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=video%20thumbnail%20${encodeURIComponent(video.title)}&image_size=landscape_16_9`;
      }
      return `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20video%20thumbnail&image_size=landscape_16_9`;
    };

    const formatDuration = (seconds) => {
      if (!seconds) return '00:00';
      const minutes = Math.floor(seconds / 60);
      const secs = seconds % 60;
      return `${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
    };

    const formatViews = (views) => {
      if (!views) return '0';
      if (views >= 10000) {
        return (views / 10000).toFixed(1) + '万';
      }
      return views;
    };

    const formatDate = (timestamp) => {
      if (!timestamp) return '';
      const date = new Date(timestamp);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    };

    const getHotSearches = async () => {
      try {
        const response = await videoApi.getSearchKeywordTop();
        if (response.code === 200 && response.data) {
          hotSearches.value = response.data;
        }
      } catch (error) {
        console.error('获取热搜失败:', error);
      }
    };

    onMounted(() => {
      getSearchHistory();
      getHotSearches();
      const urlParams = new URLSearchParams(window.location.search);
      const kw = urlParams.get('keyword');
      if (kw) {
        keyword.value = decodeURIComponent(kw);
        handleSearch();
      }
    });

    return {
      keyword,
      activeTab,
      searchResults,
      loading,
      hasSearched,
      searchHistory,
      hotSearches,
      handleSearch,
      goToVideoDetail,
      getVideoCover,
      formatDuration,
      formatViews,
      formatDate,
      clearSearchHistory,
      removeHistory,
      searchKeyword
    };
  }
};
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.search-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.search-header {
  margin-bottom: 20px;
}

.search-box-container {
  display: flex;
  max-width: 600px;
  margin: 0 auto;
}

.search-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 4px 0 0 4px;
  font-size: 14px;
  outline: none;
}

.search-input:focus {
  border-color: #ff4757;
}

.search-btn {
  padding: 0 24px;
  background-color: #ff4757;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
}

.search-btn img {
  width: 20px;
  height: 20px;
}

.search-tabs {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.tab {
  padding: 8px 16px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
}

.tab:hover {
  background-color: #f5f5f5;
}

.tab.active {
  background-color: #ff4757;
  color: white;
}

.loading {
  text-align: center;
  padding: 60px 20px;
}

.loading-icon {
  width: 48px;
  height: 48px;
  margin-bottom: 16px;
}

.loading p {
  color: #999;
}

.empty-results {
  text-align: center;
  padding: 60px 20px;
}

.empty-results img {
  width: 100px;
  height: 100px;
  margin-bottom: 16px;
}

.empty-results p {
  color: #999;
}

.search-results {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.video-card {
  display: flex;
  gap: 16px;
  background-color: white;
  border-radius: 8px;
  padding: 12px;
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.video-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.video-thumbnail {
  position: relative;
  width: 180px;
  height: 100px;
  flex-shrink: 0;
}

.video-thumbnail img {
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

.video-info {
  flex: 1;
  min-width: 0;
}

.video-title {
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.video-author {
  font-size: 14px;
  color: #999;
  margin: 0 0 8px 0;
}

.video-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #999;
}

.search-history-section {
  margin-top: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  font-size: 16px;
  color: #333;
  margin: 0;
}

.clear-history {
  background-color: transparent;
  border: none;
  color: #999;
  font-size: 14px;
  cursor: pointer;
}

.history-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 32px;
}

.history-tag {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background-color: white;
  border-radius: 4px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}

.remove-tag {
  color: #999;
  font-size: 16px;
  cursor: pointer;
}

.remove-tag:hover {
  color: #ff4757;
}

.empty-history p {
  color: #999;
}

.hot-search-section {
  background-color: white;
  border-radius: 8px;
  padding: 16px;
}

.hot-search-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.hot-search-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.hot-search-item:hover {
  background-color: #f5f5f5;
}

.hot-rank {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f0f0;
  border-radius: 4px;
  font-size: 14px;
  font-weight: bold;
  color: #666;
}

.hot-rank.top {
  background-color: #ff4757;
  color: white;
}

.hot-keyword {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.hot-count {
  font-size: 12px;
  color: #999;
}

@media (max-width: 768px) {
  .video-card {
    flex-direction: column;
  }
  
  .video-thumbnail {
    width: 100%;
    height: auto;
  }
  
  .hot-search-list {
    grid-template-columns: 1fr;
  }
}
</style>
