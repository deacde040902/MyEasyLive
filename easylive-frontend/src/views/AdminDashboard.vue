<template>
  <div class="admin-dashboard">
    <!-- 顶部导航栏 -->
    <div class="admin-header">
      <div class="header-left">
        <h1>EasyLive 管理后台</h1>
      </div>
      <div class="header-right">
        <span class="admin-name">{{ adminName }}</span>
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </div>

    <!-- 侧边导航 -->
    <div class="admin-sidebar">
      <div class="nav-item" :class="{ active: activeMenu === 'dashboard' }" @click="activeMenu = 'dashboard'">
        <img src="/src/recourse/icon-controll.png" alt="控制台" /> 控制台
      </div>
      <div class="nav-item" :class="{ active: activeMenu === 'video' }" @click="activeMenu = 'video'">
        <span>🎬</span> 视频管理
      </div>
      <div class="nav-item" :class="{ active: activeMenu === 'user' }" @click="activeMenu = 'user'">
        <span>👥</span> 用户管理
      </div>
      <div class="nav-item" :class="{ active: activeMenu === 'category' }" @click="activeMenu = 'category'">
        <span>📁</span> 分类管理
      </div>
      <div class="nav-item" :class="{ active: activeMenu === 'danmu' }" @click="activeMenu = 'danmu'">
        <span>💬</span> 弹幕管理
      </div>
      <div class="nav-item" :class="{ active: activeMenu === 'comment' }" @click="activeMenu = 'comment'">
        <span>📝</span> 评论管理
      </div>
      <div class="nav-item" :class="{ active: activeMenu === 'settings' }" @click="activeMenu = 'settings'">
        <span>⚙️</span> 系统设置
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="admin-content">
      <!-- 控制台 -->
      <div v-if="activeMenu === 'dashboard'" class="content-section">
        <h2>数据概览</h2>
        <div class="stats-cards">
          <div class="stat-card">
            <div class="stat-icon">👥</div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.userCount || 0 }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">🎬</div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.videoCount || 0 }}</div>
              <div class="stat-label">视频总数</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">💬</div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.commentCount || 0 }}</div>
              <div class="stat-label">评论总数</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">📅</div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.todayVideoCount || 0 }}</div>
              <div class="stat-label">今日新增视频</div>
            </div>
          </div>
        </div>

        <h2>本周数据趋势</h2>
        <div class="chart-container">
          <div class="chart-placeholder">
            <table class="week-data-table">
              <thead>
                <tr>
                  <th>日期</th>
                  <th>新增用户</th>
                  <th>新增视频</th>
                  <th>新增评论</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="day in weekStatistics" :key="day.date">
                  <td>{{ day.date }}</td>
                  <td>{{ day.newUsers || 0 }}</td>
                  <td>{{ day.newVideos || 0 }}</td>
                  <td>{{ day.newComments || 0 }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- 视频管理 -->
      <div v-if="activeMenu === 'video'" class="content-section">
        <h2>视频管理</h2>
        <div class="filter-bar">
          <input type="text" v-model="videoSearchTitle" placeholder="搜索视频标题" class="search-input" />
          <select v-model="videoFilterStatus" class="status-select">
            <option value="">全部状态</option>
            <option value="0">已删除</option>
            <option value="1">正常</option>
            <option value="2">审核中</option>
          </select>
          <button class="filter-btn" @click="filterVideos">筛选</button>
          <button class="filter-btn" @click="resetVideoFilter">重置</button>
        </div>
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>视频ID</th>
                <th>标题</th>
                <th>作者</th>
                <th>播放量</th>
                <th>点赞数</th>
                <th>评论数</th>
                <th>状态</th>
                <th>发布时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="video in videoList" :key="video.videoId">
                <td>{{ video.videoId.substring(0, 8) }}...</td>
                <td>{{ video.title }}</td>
                <td>{{ video.authorName || '未知' }}</td>
                <td>{{ video.playCount || 0 }}</td>
                <td>{{ video.likeCount || 0 }}</td>
                <td>{{ video.commentCount || 0 }}</td>
                <td>
                  <span :class="['status-badge', getStatusClass(video.status)]">
                    {{ getStatusText(video.status) }}
                  </span>
                </td>
                <td>{{ formatDate(video.publishTime) }}</td>
                <td>
                  <button v-if="video.status === 2" class="action-btn audit" @click="showAuditDialog(video)">审核</button>
                  <button class="action-btn view" @click="viewVideoDetail(video)">查看</button>
                  <button class="action-btn delete" @click="deleteVideo(video.videoId)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="pagination">
          <button @click="changeVideoPage(-1)" :disabled="videoPage <= 1">上一页</button>
          <span>{{ videoPage }} / {{ videoTotalPages }}</span>
          <button @click="changeVideoPage(1)" :disabled="videoPage >= videoTotalPages">下一页</button>
        </div>
      </div>

      <!-- 用户管理 -->
      <div v-if="activeMenu === 'user'" class="content-section">
        <h2>用户管理</h2>
        <div class="filter-bar">
          <input type="text" v-model="userSearchName" placeholder="搜索用户名" class="search-input" />
          <select v-model="userFilterStatus" class="status-select">
            <option value="">全部状态</option>
            <option value="1">正常</option>
            <option value="0">禁用</option>
          </select>
          <button class="filter-btn" @click="filterUsers">筛选</button>
          <button class="filter-btn" @click="resetUserFilter">重置</button>
        </div>
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>用户ID</th>
                <th>昵称</th>
                <th>邮箱</th>
                <th>注册时间</th>
                <th>粉丝数</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in userList" :key="user.userId">
                <td>{{ user.userId.substring(0, 8) }}...</td>
                <td>{{ user.nickName || '未设置' }}</td>
                <td>{{ user.email || '未绑定' }}</td>
                <td>{{ formatDate(user.createTime) }}</td>
                <td>{{ user.fansCount || 0 }}</td>
                <td>
                  <span :class="['status-badge', user.status === 1 ? 'status-normal' : 'status-disabled']">
                    {{ user.status === 1 ? '正常' : '禁用' }}
                  </span>
                </td>
                <td>
                  <button class="action-btn" @click="toggleUserStatus(user)">
                    {{ user.status === 1 ? '禁用' : '启用' }}
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="pagination">
          <button @click="changeUserPage(-1)" :disabled="userPage <= 1">上一页</button>
          <span>{{ userPage }} / {{ userTotalPages }}</span>
          <button @click="changeUserPage(1)" :disabled="userPage >= userTotalPages">下一页</button>
        </div>
      </div>

      <!-- 分类管理 -->
      <div v-if="activeMenu === 'category'" class="content-section">
        <h2>分类管理</h2>
        <div class="category-actions">
          <button class="add-btn" @click="showAddCategoryDialog">添加分类</button>
        </div>
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>分类ID</th>
                <th>分类名称</th>
                <th>排序</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="category in categoryList" :key="category.categoryId">
                <td>{{ category.categoryId }}</td>
                <td>{{ category.categoryName }}</td>
                <td>{{ category.sortOrder || 0 }}</td>
                <td>
                  <button class="action-btn" @click="editCategory(category)">编辑</button>
                  <button class="action-btn delete" @click="deleteCategory(category.categoryId)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 弹幕管理 -->
      <div v-if="activeMenu === 'danmu'" class="content-section">
        <h2>弹幕管理</h2>
        <div class="filter-bar">
          <input type="text" v-model="danmuSearchVideoId" placeholder="搜索视频ID" class="search-input" />
          <button class="filter-btn" @click="filterDanmus">筛选</button>
          <button class="filter-btn" @click="resetDanmuFilter">重置</button>
        </div>
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>弹幕ID</th>
                <th>视频ID</th>
                <th>内容</th>
                <th>发送时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="danmu in danmuList" :key="danmu.id">
                <td>{{ danmu.id }}</td>
                <td>{{ danmu.videoId }}</td>
                <td>{{ danmu.content }}</td>
                <td>{{ formatDate(danmu.createTime) }}</td>
                <td>
                  <button class="action-btn delete" @click="deleteDanmu(danmu.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="pagination">
          <button @click="changeDanmuPage(-1)" :disabled="danmuPage <= 1">上一页</button>
          <span>{{ danmuPage }} / {{ danmuTotalPages }}</span>
          <button @click="changeDanmuPage(1)" :disabled="danmuPage >= danmuTotalPages">下一页</button>
        </div>
      </div>

      <!-- 评论管理 -->
      <div v-if="activeMenu === 'comment'" class="content-section">
        <h2>评论管理</h2>
        <div class="filter-bar">
          <input type="text" v-model="commentSearchVideoId" placeholder="搜索视频ID" class="search-input" />
          <button class="filter-btn" @click="filterComments">筛选</button>
          <button class="filter-btn" @click="resetCommentFilter">重置</button>
        </div>
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>评论ID</th>
                <th>视频ID</th>
                <th>用户</th>
                <th>内容</th>
                <th>时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="comment in commentList" :key="comment.commentId || comment.id">
                <td>{{ (comment.commentId || comment.id).toString().substring(0, 8) }}...</td>
                <td>{{ comment.videoId }}</td>
                <td>{{ comment.userId || '未知' }}</td>
                <td>{{ comment.content }}</td>
                <td>{{ formatDate(comment.createTime) }}</td>
                <td>
                  <button class="action-btn delete" @click="deleteComment(comment.commentId || comment.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="pagination">
          <button @click="changeCommentPage(-1)" :disabled="commentPage <= 1">上一页</button>
          <span>{{ commentPage }} / {{ commentTotalPages }}</span>
          <button @click="changeCommentPage(1)" :disabled="commentPage >= commentTotalPages">下一页</button>
        </div>
      </div>

      <!-- 系统设置 -->
      <div v-if="activeMenu === 'settings'" class="content-section">
        <h2>系统设置</h2>
        <div class="settings-form">
          <div class="form-group">
            <label>网站名称</label>
            <input type="text" v-model="settings.websiteName" placeholder="请输入网站名称" />
          </div>
          <div class="form-group">
            <label>网站描述</label>
            <textarea v-model="settings.description" placeholder="请输入网站描述" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label>关键字</label>
            <input type="text" v-model="settings.keywords" placeholder="请输入关键字，多个用逗号分隔" />
          </div>
          <div class="form-group">
            <label>联系邮箱</label>
            <input type="email" v-model="settings.contactEmail" placeholder="请输入联系邮箱" />
          </div>
          <div class="form-group">
            <label>ICP备案号</label>
            <input type="text" v-model="settings.icpNumber" placeholder="请输入ICP备案号" />
          </div>
          <button class="save-btn" @click="saveSettings">保存设置</button>
        </div>
      </div>
    </div>

    <!-- 审核弹窗 -->
    <div v-if="showAuditModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>审核视频</h3>
          <button class="close-btn" @click="showAuditModal = false">×</button>
        </div>
        <div class="modal-body">
          <p>视频标题：{{ currentAuditVideo?.title }}</p>
          <div class="form-group">
            <label>审核结果</label>
            <select v-model="auditStatus">
              <option :value="1">通过</option>
              <option :value="0">拒绝</option>
            </select>
          </div>
          <div v-if="auditStatus === 0" class="form-group">
            <label>拒绝原因</label>
            <textarea v-model="auditReason" placeholder="请输入拒绝原因" rows="3"></textarea>
          </div>
          <button class="submit-btn" @click="submitAudit">提交审核</button>
        </div>
      </div>
    </div>

    <!-- 添加/编辑分类弹窗 -->
    <div v-if="showCategoryModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ isEditCategory ? '编辑分类' : '添加分类' }}</h3>
          <button class="close-btn" @click="showCategoryModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>分类名称</label>
            <input type="text" v-model="categoryForm.categoryName" placeholder="请输入分类名称" />
          </div>
          <div class="form-group">
            <label>排序</label>
            <input type="number" v-model="categoryForm.sortOrder" placeholder="请输入排序序号" />
          </div>
          <button class="submit-btn" @click="submitCategory">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import adminApi from '../api/admin';

export default {
  data() {
    return {
      adminName: localStorage.getItem('nickName') || '管理员',
      activeMenu: 'dashboard',

      // 统计数据
      statistics: {},
      weekStatistics: [],

      // 视频管理
      videoList: [],
      videoSearchTitle: '',
      videoFilterStatus: '',
      videoPage: 1,
      videoPageSize: 10,
      videoTotalPages: 1,

      // 用户管理
      userList: [],
      userSearchName: '',
      userFilterStatus: '',
      userPage: 1,
      userPageSize: 10,
      userTotalPages: 1,

      // 分类管理
      categoryList: [],
      showCategoryModal: false,
      isEditCategory: false,
      categoryForm: {
        categoryId: null,
        categoryName: '',
        sortOrder: 0
      },

      // 弹幕管理
      danmuList: [],
      danmuSearchVideoId: '',
      danmuPage: 1,
      danmuPageSize: 10,
      danmuTotalPages: 1,

      // 评论管理
      commentList: [],
      commentSearchVideoId: '',
      commentPage: 1,
      commentPageSize: 10,
      commentTotalPages: 1,

      // 系统设置
      settings: {
        websiteName: '',
        description: '',
        keywords: '',
        contactEmail: '',
        icpNumber: ''
      },

      // 审核弹窗
      showAuditModal: false,
      currentAuditVideo: null,
      auditStatus: 1,
      auditReason: ''
    };
  },
  mounted() {
    this.checkLoginStatus();
    this.loadStatistics();
    this.loadWeekStatistics();
  },
  methods: {
    checkLoginStatus() {
      const token = localStorage.getItem('token');
      if (!token) {
        this.$router.push('/login');
      }
    },

    // 退出登录
    async handleLogout() {
      try {
        await adminApi.logout();
      } catch (error) {
        console.error('退出失败:', error);
      }
      localStorage.removeItem('token');
      localStorage.removeItem('nickName');
      localStorage.removeItem('userId');
      this.$router.push('/login');
    },

    // 加载统计数据
    async loadStatistics() {
      try {
        const response = await adminApi.getActualTimeStatistics();
        if (response.code === 200) {
          this.statistics = response.data || {};
        }
      } catch (error) {
        console.error('获取统计数据失败:', error);
      }
    },

    // 加载周统计数据
    async loadWeekStatistics() {
      try {
        const response = await adminApi.getWeekStatistics();
        if (response.code === 200) {
          this.weekStatistics = response.data || [];
        }
      } catch (error) {
        console.error('获取周统计数据失败:', error);
      }
    },

    // 视频管理
    async loadVideoList() {
      try {
        const params = {
          pageNo: this.videoPage,
          pageSize: this.videoPageSize,
          title: this.videoSearchTitle || null,
          status: this.videoFilterStatus ? parseInt(this.videoFilterStatus) : null
        };
        const response = await adminApi.getVideoList(params);
        if (response.code === 200) {
          this.videoList = response.data || [];
          this.videoTotalPages = Math.ceil((response.data?.length || 0) / this.videoPageSize) || 1;
        }
      } catch (error) {
        console.error('获取视频列表失败:', error);
      }
    },

    filterVideos() {
      this.videoPage = 1;
      this.loadVideoList();
    },

    resetVideoFilter() {
      this.videoSearchTitle = '';
      this.videoFilterStatus = '';
      this.videoPage = 1;
      this.loadVideoList();
    },

    changeVideoPage(delta) {
      this.videoPage += delta;
      this.loadVideoList();
    },

    getStatusClass(status) {
      const statusMap = {
        0: 'status-deleted',
        1: 'status-normal',
        2: 'status-audit'
      };
      return statusMap[status] || 'status-normal';
    },

    getStatusText(status) {
      const statusMap = {
        0: '已删除',
        1: '正常',
        2: '审核中'
      };
      return statusMap[status] || '未知';
    },

    showAuditDialog(video) {
      this.currentAuditVideo = video;
      this.auditStatus = 1;
      this.auditReason = '';
      this.showAuditModal = true;
    },

    async submitAudit() {
      try {
        const response = await adminApi.auditVideo(
          this.currentAuditVideo.videoId,
          this.auditStatus,
          this.auditReason
        );
        if (response.code === 200) {
          this.showToast('审核提交成功');
          this.showAuditModal = false;
          this.loadVideoList();
        }
      } catch (error) {
        console.error('审核失败:', error);
        this.showToast('审核失败');
      }
    },

    async deleteVideo(videoId) {
      if (!confirm('确定要删除这个视频吗？')) return;
      try {
        const response = await adminApi.deleteVideo(videoId);
        if (response.code === 200) {
          this.showToast('删除成功');
          this.loadVideoList();
        }
      } catch (error) {
        console.error('删除失败:', error);
        this.showToast('删除失败');
      }
    },

    viewVideoDetail(video) {
      window.open(`/video/detail/${video.videoId}`, '_blank');
    },

    // 用户管理
    async loadUserList() {
      try {
        const params = {
          pageNo: this.userPage,
          pageSize: this.userPageSize,
          nickName: this.userSearchName || null,
          status: this.userFilterStatus ? parseInt(this.userFilterStatus) : null
        };
        const response = await adminApi.getUserList(params);
        if (response.code === 200) {
          this.userList = response.data || [];
          this.userTotalPages = Math.ceil((response.data?.length || 0) / this.userPageSize) || 1;
        }
      } catch (error) {
        console.error('获取用户列表失败:', error);
      }
    },

    filterUsers() {
      this.userPage = 1;
      this.loadUserList();
    },

    resetUserFilter() {
      this.userSearchName = '';
      this.userFilterStatus = '';
      this.userPage = 1;
      this.loadUserList();
    },

    changeUserPage(delta) {
      this.userPage += delta;
      this.loadUserList();
    },

    async toggleUserStatus(user) {
      const newStatus = user.status === 1 ? 0 : 1;
      try {
        const response = await adminApi.changeUserStatus(user.userId, newStatus);
        if (response.code === 200) {
          this.showToast('状态修改成功');
          this.loadUserList();
        }
      } catch (error) {
        console.error('修改状态失败:', error);
        this.showToast('修改失败');
      }
    },

    // 分类管理
    async loadCategoryList() {
      try {
        const response = await adminApi.getCategoryList();
        if (response.code === 200) {
          this.categoryList = response.data || [];
        }
      } catch (error) {
        console.error('获取分类列表失败:', error);
      }
    },

    showAddCategoryDialog() {
      this.isEditCategory = false;
      this.categoryForm = {
        categoryId: null,
        categoryName: '',
        sortOrder: 0
      };
      this.showCategoryModal = true;
    },

    editCategory(category) {
      this.isEditCategory = true;
      this.categoryForm = {
        categoryId: category.categoryId,
        categoryName: category.categoryName,
        sortOrder: category.sortOrder || 0
      };
      this.showCategoryModal = true;
    },

    async submitCategory() {
      try {
        const response = await adminApi.saveCategory(this.categoryForm);
        if (response.code === 200) {
          this.showToast('保存成功');
          this.showCategoryModal = false;
          this.loadCategoryList();
        }
      } catch (error) {
        console.error('保存失败:', error);
        this.showToast('保存失败');
      }
    },

    async deleteCategory(categoryId) {
      if (!confirm('确定要删除这个分类吗？')) return;
      try {
        const response = await adminApi.deleteCategory(categoryId);
        if (response.code === 200) {
          this.showToast('删除成功');
          this.loadCategoryList();
        }
      } catch (error) {
        console.error('删除失败:', error);
        this.showToast('删除失败');
      }
    },

    // 弹幕管理
    async loadDanmuList() {
      try {
        const params = {
          pageNo: this.danmuPage,
          pageSize: this.danmuPageSize,
          videoId: this.danmuSearchVideoId || null
        };
        const response = await adminApi.getDanmuList(params);
        if (response.code === 200) {
          this.danmuList = response.data || [];
          this.danmuTotalPages = Math.ceil((response.data?.length || 0) / this.danmuPageSize) || 1;
        }
      } catch (error) {
        console.error('获取弹幕列表失败:', error);
      }
    },

    filterDanmus() {
      this.danmuPage = 1;
      this.loadDanmuList();
    },

    resetDanmuFilter() {
      this.danmuSearchVideoId = '';
      this.danmuPage = 1;
      this.loadDanmuList();
    },

    changeDanmuPage(delta) {
      this.danmuPage += delta;
      this.loadDanmuList();
    },

    async deleteDanmu(danmuId) {
      if (!confirm('确定要删除这条弹幕吗？')) return;
      try {
        const response = await adminApi.deleteDanmu(danmuId);
        if (response.code === 200) {
          this.showToast('删除成功');
          this.loadDanmuList();
        }
      } catch (error) {
        console.error('删除失败:', error);
        this.showToast('删除失败');
      }
    },

    // 评论管理
    async loadCommentList() {
      try {
        const params = {
          pageNo: this.commentPage,
          pageSize: this.commentPageSize,
          videoId: this.commentSearchVideoId || null
        };
        const response = await adminApi.getCommentList(params);
        if (response.code === 200) {
          this.commentList = response.data || [];
          this.commentTotalPages = Math.ceil((response.data?.length || 0) / this.commentPageSize) || 1;
        }
      } catch (error) {
        console.error('获取评论列表失败:', error);
      }
    },

    filterComments() {
      this.commentPage = 1;
      this.loadCommentList();
    },

    resetCommentFilter() {
      this.commentSearchVideoId = '';
      this.commentPage = 1;
      this.loadCommentList();
    },

    changeCommentPage(delta) {
      this.commentPage += delta;
      this.loadCommentList();
    },

    async deleteComment(commentId) {
      if (!confirm('确定要删除这条评论吗？')) return;
      try {
        const response = await adminApi.deleteComment(commentId);
        if (response.code === 200) {
          this.showToast('删除成功');
          this.loadCommentList();
        }
      } catch (error) {
        console.error('删除失败:', error);
        this.showToast('删除失败');
      }
    },

    // 系统设置
    async loadSettings() {
      try {
        const response = await adminApi.getSystemSettings();
        if (response.code === 200) {
          this.settings = response.data || this.settings;
        }
      } catch (error) {
        console.error('获取设置失败:', error);
      }
    },

    async saveSettings() {
      try {
        const response = await adminApi.saveSystemSettings(this.settings);
        if (response.code === 200) {
          this.showToast('设置保存成功');
        }
      } catch (error) {
        console.error('保存设置失败:', error);
        this.showToast('保存失败');
      }
    },

    // 工具方法
    formatDate(dateStr) {
      if (!dateStr) return '-';
      const date = new Date(dateStr);
      return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
    },

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
    }
  },
  watch: {
    activeMenu(newMenu) {
      if (newMenu === 'video' && this.videoList.length === 0) {
        this.loadVideoList();
      } else if (newMenu === 'user' && this.userList.length === 0) {
        this.loadUserList();
      } else if (newMenu === 'category' && this.categoryList.length === 0) {
        this.loadCategoryList();
      } else if (newMenu === 'danmu' && this.danmuList.length === 0) {
        this.loadDanmuList();
      } else if (newMenu === 'comment' && this.commentList.length === 0) {
        this.loadCommentList();
      } else if (newMenu === 'settings' && !this.settings.websiteName) {
        this.loadSettings();
      }
    }
  }
};
</script>

<style scoped>
.admin-dashboard {
  display: flex;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.admin-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background-color: #001529;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  z-index: 1000;
}

.header-left h1 {
  font-size: 20px;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.admin-name {
  font-size: 14px;
}

.logout-btn {
  padding: 6px 16px;
  background-color: #ff4d4f;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.admin-sidebar {
  position: fixed;
  top: 60px;
  left: 0;
  width: 200px;
  height: calc(100vh - 60px);
  background-color: white;
  border-right: 1px solid #e8e8e8;
  padding: 20px 0;
  overflow-y: auto;
}

.nav-item {
  padding: 12px 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
  transition: all 0.3s;
}

.nav-item img {
  width: 20px;
  height: 20px;
  object-fit: contain;
}

.nav-item:hover {
  background-color: #f0f0f0;
}

.nav-item.active {
  background-color: #e6f7ff;
  color: #1890ff;
  border-right: 3px solid #1890ff;
}

.admin-content {
  margin-left: 200px;
  margin-top: 60px;
  padding: 20px;
  flex: 1;
}

.content-section {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.content-section h2 {
  font-size: 18px;
  margin-bottom: 20px;
  color: #333;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  font-size: 36px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.chart-container {
  margin-top: 20px;
}

.week-data-table {
  width: 100%;
  border-collapse: collapse;
}

.week-data-table th,
.week-data-table td {
  padding: 12px;
  text-align: center;
  border-bottom: 1px solid #e8e8e8;
}

.week-data-table th {
  background-color: #fafafa;
  font-weight: bold;
  color: #333;
}

.filter-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input,
.status-select {
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}

.search-input {
  flex: 1;
  max-width: 300px;
}

.filter-btn {
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.filter-btn:hover {
  background-color: #40a9ff;
}

.table-container {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e8e8e8;
}

.data-table th {
  background-color: #fafafa;
  font-weight: bold;
  color: #333;
  white-space: nowrap;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-normal {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-disabled,
.status-deleted {
  background-color: #fff1f0;
  color: #ff4d4f;
}

.status-audit {
  background-color: #fffbe6;
  color: #faad14;
}

.action-btn {
  padding: 4px 12px;
  margin-right: 8px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  background-color: #1890ff;
  color: white;
}

.action-btn.audit {
  background-color: #faad14;
}

.action-btn.delete {
  background-color: #ff4d4f;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
}

.pagination button {
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  background-color: #d9d9d9;
  cursor: not-allowed;
}

.category-actions {
  margin-bottom: 20px;
}

.add-btn {
  padding: 10px 20px;
  background-color: #52c41a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.settings-form {
  max-width: 600px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
  color: #333;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}

.form-group textarea {
  resize: vertical;
}

.save-btn {
  padding: 10px 30px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 500px;
  max-width: 90%;
  overflow: hidden;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.modal-body {
  padding: 20px;
}

.modal-body p {
  margin-bottom: 15px;
  color: #666;
}

.submit-btn {
  width: 100%;
  padding: 12px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  margin-top: 10px;
}
</style>