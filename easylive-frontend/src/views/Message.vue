<template>
  <div class="message-page">
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
          <div class="icon-item active">
            <img class="icon-img" src="/src/recourse/icon-message.png" alt="消息" />
            <span class="icon-text">消息</span>
            <span class="unread-badge" v-if="unreadCount > 0">{{ unreadCount }}</span>
          </div>
          <div class="icon-item" @click="goToCollection">
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

    <div v-if="!isLoggedIn" class="login-required">
      <div class="login-content">
        <img src="/src/recourse/icon-message.png" alt="消息" />
        <p>请先登录查看消息</p>
        <button class="login-btn" @click="goToLogin">立即登录</button>
      </div>
    </div>

    <div v-else class="message-page-container">
      <div class="sidebar">
        <div class="sidebar-menu">
          <div 
            v-for="item in menuItems" 
            :key="item.id" 
            class="menu-item"
            :class="{ active: currentMenu === item.id }"
            @click="selectMenu(item.id)"
          >
            <img :src="item.icon" alt="图标" class="menu-icon" />
            <span class="menu-text">{{ item.label }}</span>
            <span class="badge" v-if="item.count > 0">{{ item.count }}</span>
          </div>
        </div>
        
        <div class="sidebar-contacts" v-if="currentMenu === 'message'">
          <div class="contacts-header">
            <span>好友</span>
            <button class="add-btn" @click="showAddFriendModal = true">+</button>
          </div>
          <div class="contact-search-box">
            <input type="text" placeholder="搜索好友..." v-model="contactSearchQuery" />
          </div>
          <div 
            v-for="contact in filteredContacts" 
            :key="contact.userId" 
            class="contact-item"
            :class="{ active: currentContact?.userId === contact.userId }"
            @click="selectContact(contact)"
          >
            <img :src="contact.avatar || defaultAvatar" alt="头像" class="contact-avatar" />
            <div class="contact-info">
              <div class="contact-name">{{ contact.nickName || contact.userName }}</div>
            </div>
            <span class="online-dot" :class="{ online: contact.online }"></span>
          </div>
          <div v-if="filteredContacts.length === 0" class="empty-contacts">
            <p>暂无好友</p>
            <button class="add-friend-btn" @click="showAddFriendModal = true">添加好友</button>
          </div>
        </div>
      </div>
      
      <div class="main-content">
        <div v-if="currentMenu === 'message' && currentContact" class="chat-area">
          <div class="chat-header">
            <img :src="currentContact.avatar || defaultAvatar" alt="头像" class="chat-avatar" />
            <div class="chat-info">
              <div class="chat-name">{{ currentContact.nickName || currentContact.userName }}</div>
              <span class="online-status" :class="{ online: currentContact.online }">
                {{ currentContact.online ? '在线' : '离线' }}
              </span>
            </div>
          </div>
          
          <div class="chat-messages" ref="chatMessagesContainer">
            <div 
              v-for="msg in chatMessages" 
              :key="msg.id" 
              class="message-item"
              :class="{ mine: msg.isMine }"
            >
              <!-- 对方消息：头像在左 -->
              <template v-if="!msg.isMine">
                <img 
                  :src="currentContact.avatar || defaultAvatar" 
                  alt="头像" 
                  class="message-avatar" 
                />
                <div class="message-bubble">
                  <div class="message-text" @contextmenu.prevent="showContextMenu($event, msg)">{{ msg.content }}</div>
                  <div class="message-time">{{ msg.time }}</div>
                </div>
              </template>
              
              <!-- 我的消息：头像在右 -->
              <template v-else>
                <div class="message-bubble">
                  <div class="message-text" @contextmenu.prevent="showContextMenu($event, msg)">{{ msg.content }}</div>
                  <div class="message-time">{{ msg.time }}</div>
                </div>
                <img 
                  :src="userAvatar" 
                  alt="头像" 
                  class="message-avatar" 
                />
              </template>
            </div>
            
            <div v-if="chatMessages.length === 0" class="empty-chat">
              <div class="empty-chat-icon">
                <img src="/src/recourse/icon-love.png" alt="开始聊天" />
              </div>
              <p>和 {{ currentContact.nickName || currentContact.userName }} 打个招呼吧</p>
            </div>
          </div>
          
          <!-- 右键菜单 -->
          <div 
            v-if="contextMenu.visible" 
            class="context-menu"
            :style="{ left: contextMenu.x + 'px', top: contextMenu.y + 'px' }"
          >
            <div v-if="contextMenu.message?.isMine" class="menu-item" @click="recallMessage">撤回</div>
            <div class="menu-item" @click="deleteMessage">删除</div>
          </div>
          
          <div class="chat-input">
            <input 
              v-model="inputMessage" 
              type="text" 
              placeholder="输入消息..." 
              class="message-input"
              @keyup.enter="sendMessage"
            />
            <button class="send-btn" @click="sendMessage">发送</button>
          </div>
        </div>
        
        <div v-else-if="currentMenu === 'message'" class="empty-message-state">
          <div class="empty-icon-wrapper">
            <img src="/src/recourse/icon-message.png" alt="消息" class="empty-icon" />
          </div>
          <p>选择一个好友开始聊天</p>
        </div>
        
        <div v-else class="notification-panel">
          <div class="notification-header">
            <h2>{{ currentMenuLabel }}</h2>
          </div>
          
          <div v-if="currentMenu === 'system'" class="notification-list">
            <div v-for="(item, index) in systemNotifications" :key="index" class="notification-item">
              <div class="notification-icon">📢</div>
              <div class="notification-content">
                <div class="notification-title">{{ item.title }}</div>
                <div class="notification-desc">{{ item.desc }}</div>
              </div>
              <div class="notification-time">{{ item.time }}</div>
            </div>
            <div v-if="systemNotifications.length === 0" class="empty-notification">
              <p>暂无系统通知</p>
            </div>
          </div>
          
          <div v-if="currentMenu === 'likes'" class="notification-list">
            <div v-for="(item, index) in likeNotifications" :key="index" class="notification-item">
              <div class="notification-icon">👍</div>
              <div class="notification-content">
                <div class="notification-title">{{ item.title }}</div>
                <div class="notification-desc">{{ item.desc }}</div>
              </div>
              <div class="notification-time">{{ item.time }}</div>
            </div>
            <div v-if="likeNotifications.length === 0" class="empty-notification">
              <p>暂无点赞通知</p>
            </div>
          </div>
          
          <div v-if="currentMenu === 'collections'" class="notification-list">
            <div v-for="(item, index) in collectionNotifications" :key="index" class="notification-item">
              <div class="notification-icon">⭐</div>
              <div class="notification-content">
                <div class="notification-title">{{ item.title }}</div>
                <div class="notification-desc">{{ item.desc }}</div>
              </div>
              <div class="notification-time">{{ item.time }}</div>
            </div>
            <div v-if="collectionNotifications.length === 0" class="empty-notification">
              <p>暂无收藏通知</p>
            </div>
          </div>
          
          <div v-if="currentMenu === 'comments'" class="notification-list">
            <div v-for="(item, index) in commentNotifications" :key="index" class="notification-item">
              <div class="notification-icon">💬</div>
              <div class="notification-content">
                <div class="notification-title">{{ item.title }}</div>
                <div class="notification-desc">{{ item.desc }}</div>
              </div>
              <div class="notification-time">{{ item.time }}</div>
            </div>
            <div v-if="commentNotifications.length === 0" class="empty-notification">
              <p>暂无评论和@通知</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加好友弹窗 -->
    <div v-if="showAddFriendModal" class="modal-overlay" @click.self="showAddFriendModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>添加好友</h3>
          <button class="close-btn" @click="showAddFriendModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="search-section">
            <input 
              type="text" 
              placeholder="输入用户名或ID搜索" 
              v-model="friendSearchQuery" 
              class="friend-search-input"
              @keyup.enter="searchFriends"
            />
            <button class="search-friend-btn" @click="searchFriends">搜索</button>
          </div>
          
          <div v-if="searchResults.length > 0" class="search-results">
            <div 
              v-for="user in searchResults" 
              :key="user.userId" 
              class="search-result-item"
            >
              <img :src="user.avatar || defaultAvatar" alt="头像" class="result-avatar" />
              <div class="result-info">
                <div class="result-name">{{ user.nickName || user.userName }}</div>
                <div class="result-id">ID: {{ user.userId }}</div>
              </div>
              <button 
                class="add-btn" 
                :class="{ added: isFriend(user.userId) }"
                @click="isFriend(user.userId) ? showToast('已是好友') : addFriend(user.userId)"
              >
                {{ isFriend(user.userId) ? '已添加' : '添加' }}
              </button>
            </div>
          </div>
          
          <div v-if="!friendSearchQuery" class="search-tips">
            <p>搜索用户名或用户ID来添加好友</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, nextTick } from 'vue';

export default {
  name: 'Message',
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
    
    const currentMenu = ref('message');
    const contacts = ref([]);
    const currentContact = ref(null);
    const chatMessages = ref([]);
    const chatMessagesContainer = ref(null); // 改个名字
    const inputMessage = ref('');
    const unreadCount = ref(0);
    const defaultAvatar = ref('https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture&image_size=square');
    
    const contactSearchQuery = ref('');
    const showAddFriendModal = ref(false);
    const friendSearchQuery = ref('');
    const searchResults = ref([]);
    
    // 右键菜单状态
    const contextMenu = ref({
      visible: false,
      x: 0,
      y: 0,
      message: null
    });
    
    const menuItems = ref([
      { id: 'message', label: '消息中心', icon: '/src/recourse/icon-message.png', count: 2 },
      { id: 'system', label: '系统通知', icon: '/src/recourse/icon-sysMessage2.png', count: 0 },
      { id: 'likes', label: '收到的赞', icon: '/src/recourse/icon-pinkLove.png', count: 5 },
      { id: 'collections', label: '收到收藏', icon: '/src/recourse/icon-collect.png', count: 3 },
      { id: 'comments', label: '评论和@', icon: '/src/recourse/icon-comment.png', count: 1 }
    ]);
    
    const systemNotifications = ref([
      { title: '系统维护通知', desc: '系统将于今晚22:00-00:00进行维护升级', time: '10分钟前' },
      { title: '新功能上线', desc: '弹幕功能已正式上线，快来体验吧！', time: '1小时前' }
    ]);
    
    const likeNotifications = ref([
      { title: '用户xxx赞了你的视频', desc: '点击查看', time: '5分钟前' },
      { title: '用户yyy赞了你的评论', desc: '点击查看', time: '30分钟前' }
    ]);
    
    const collectionNotifications = ref([
      { title: '用户aaa收藏了你的视频', desc: '点击查看', time: '2小时前' }
    ]);
    
    const commentNotifications = ref([
      { title: '用户bbb评论了你的视频', desc: '评论内容：这个视频太棒了！', time: '3小时前' }
    ]);
    
    const currentMenuLabel = computed(() => {
      const item = menuItems.value.find(m => m.id === currentMenu.value);
      return item ? item.label : '';
    });
    
    const filteredContacts = computed(() => {
      if (!contactSearchQuery.value) return contacts.value;
      const query = contactSearchQuery.value.toLowerCase();
      return contacts.value.filter(contact => 
        (contact.nickName || '').toLowerCase().includes(query) ||
        (contact.userName || '').toLowerCase().includes(query)
      );
    });
    
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
    
    const fetchContacts = async () => {
      try {
        const token = localStorage.getItem('token');
        if (!token) return;
        
        const response = await fetch('/api/userInfo/getFriendList', {
          method: 'POST',
          headers: { 'token': token }
        });
        const result = await response.json();
        if (result.code === 200 && result.data) {
          contacts.value = result.data.map(user => ({
            userId: user.userId,
            nickName: user.nickName,
            avatar: user.avatarPath ? `/api/userInfo/getAvatar?userId=${user.userId}` : null,
            online: false
          }));
        }
      } catch (error) {
        console.error('获取好友列表失败:', error);
      }
    };
    
    const fetchNotificationCounts = async () => {
      try {
        const token = localStorage.getItem('token');
        if (!token) return;
        
        const response = await fetch('/api/notification/getNotificationCounts', {
          method: 'POST',
          headers: { 'token': token }
        });
        const result = await response.json();
        if (result.code === 200 && result.data) {
          const counts = result.data;
          const messageIndex = menuItems.value.findIndex(item => item.id === 'message');
          const systemIndex = menuItems.value.findIndex(item => item.id === 'system');
          const likesIndex = menuItems.value.findIndex(item => item.id === 'likes');
          const collectionsIndex = menuItems.value.findIndex(item => item.id === 'collections');
          const commentsIndex = menuItems.value.findIndex(item => item.id === 'comments');
          
          if (messageIndex !== -1) menuItems.value[messageIndex].count = counts.unreadMessageCount || 0;
          if (systemIndex !== -1) menuItems.value[systemIndex].count = counts.systemNotificationCount || 0;
          if (likesIndex !== -1) menuItems.value[likesIndex].count = counts.likeCount || 0;
          if (collectionsIndex !== -1) menuItems.value[collectionsIndex].count = counts.collectionCount || 0;
          if (commentsIndex !== -1) menuItems.value[commentsIndex].count = counts.commentCount || 0;
        }
      } catch (error) {
        console.error('获取通知计数失败:', error);
      }
    };
    
    const fetchChatHistory = async (userId) => {
      try {
        const token = localStorage.getItem('token');
        if (!token) return;
        
        const response = await fetch(`/api/chat/getChatHistory?userId=${userId}`, {
          method: 'POST',
          headers: { 'token': token }
        });
        
        const result = await response.json();
        if (result.code === 200 && result.data) {
          chatMessages.value = result.data.map(msg => ({
            id: msg.messageId,
            content: msg.content,
            time: new Date(msg.createTime).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
            isMine: msg.fromUserId === getCurrentUserId(),
            fromUserId: msg.fromUserId,
            toUserId: msg.toUserId,
            messageType: msg.messageType,
            isRead: msg.isRead,
            createTime: msg.createTime
          })).sort((a, b) => new Date(a.createTime) - new Date(b.createTime));
          
          await nextTick();
          setTimeout(() => {
            if (chatMessagesContainer.value) {
              chatMessagesContainer.value.scrollTop = chatMessagesContainer.value.scrollHeight;
            }
          }, 100);
        }
      } catch (error) {
        console.error('获取聊天记录失败:', error);
      }
    };
    
    const selectMenu = (menuId) => {
      currentMenu.value = menuId;
      if (menuId !== 'message') {
        currentContact.value = null;
      }
    };
    
    const selectContact = (contact) => {
      currentContact.value = contact;
      chatMessages.value = [];
      fetchChatHistory(contact.userId);
    };
    
    const sendMessage = async () => {
      if (!inputMessage.value.trim() || !currentContact.value) return;
      
      const token = localStorage.getItem('token');
      if (!token) {
        showToast('请先登录');
        return;
      }
      
      try {
        const response = await fetch('/api/chat/sendMessage', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', 'token': token },
          body: JSON.stringify({
            receiverId: currentContact.value.userId,
            content: inputMessage.value.trim(),
            messageType: 1
          })
        });
        
        const responseText = await response.text();
        let result;
        try {
          result = JSON.parse(responseText);
        } catch (e) {
          showToast('服务器响应格式错误');
          return;
        }
        
        if (result.code === 200) {
          await fetchChatHistory(currentContact.value.userId);
          inputMessage.value = '';
        } else {
          showToast(result.message || '发送失败');
        }
      } catch (error) {
        showToast('发送失败，请稍后再试');
      }
    };
    
    const searchFriends = async () => {
      if (!friendSearchQuery.value.trim()) return;
      
      try {
        const token = localStorage.getItem('token');
        const response = await fetch(`/api/userInfo/searchUser`, {
          method: 'POST',
          headers: { 'token': token, 'Content-Type': 'application/json' },
          body: JSON.stringify({ keyword: friendSearchQuery.value })
        });
        const result = await response.json();
        if (result.code === 200 && result.data) {
          searchResults.value = result.data;
        }
      } catch (error) {
        console.error('搜索用户失败:', error);
      }
    };
    
    const addFriend = async (userId) => {
      try {
        const token = localStorage.getItem('token');
        const response = await fetch('/api/userInfo/addFriend', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', 'token': token },
          body: JSON.stringify({ friendUserId: userId })
        });
        const result = await response.json();
        if (result.code === 200) {
          showToast('添加好友成功');
          showAddFriendModal.value = false;
          fetchContacts();
        } else {
          showToast(result.message || '添加失败');
        }
      } catch (error) {
        showToast('添加失败，请稍后再试');
      }
    };
    
    const isFriend = (userId) => {
      return contacts.value.some(c => c.userId === userId);
    };
    
    const getCurrentUserId = () => localStorage.getItem('userId');
    
    const goToHome = () => window.location.href = '/';
    const goToLogin = () => window.location.href = '/login';
    const goToUpload = () => window.location.href = '/upload';
    const goToCreationCenter = () => window.location.href = '/creation-center';
    const goToCollection = () => window.location.href = '/collection';
    const goToProfile = () => window.location.href = '/profile';
    const goToHistory = () => window.location.href = '/history';
    
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
        box-shadow: 0 2px 12px rgba(0,0,0,0.15); z-index: 9999; transition: 0.3s; opacity:0;
      `;
      document.body.appendChild(toast);
      setTimeout(() => { toast.style.opacity = '1'; }, 10);
      setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => document.body.removeChild(toast), 300);
      }, 3000);
    };

    // 显示右键菜单
    const showContextMenu = (event, message) => {
      const menuHeight = 100; // 预估菜单高度
      const windowHeight = window.innerHeight;
      const clientY = event.clientY;
      
      // 如果菜单会超出屏幕底部，向上偏移
      let menuY = clientY;
      if (clientY + menuHeight > windowHeight) {
        menuY = windowHeight - menuHeight - 10;
      }
      
      contextMenu.value = {
        visible: true,
        x: event.clientX,
        y: menuY,
        message: message
      };
      
      // 点击其他地方关闭菜单
      document.addEventListener('click', closeContextMenu);
    };

    // 关闭右键菜单
    const closeContextMenu = () => {
      contextMenu.value.visible = false;
      document.removeEventListener('click', closeContextMenu);
    };

    // 撤回消息
    const recallMessage = async () => {
      const message = contextMenu.value.message;
      if (!message) return;
      
      closeContextMenu();
      
      const token = localStorage.getItem('token');
      if (!token) {
        showToast('请先登录');
        return;
      }
      
      try {
        const params = new URLSearchParams();
        params.append('messageId', message.id);
        const response = await fetch('/api/message/recall', {
          method: 'POST',
          headers: { 
            'token': token,
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          body: params.toString()
        });
        const result = await response.json();
        if (result.code === 200) {
          showToast('撤回成功');
          // 更新消息内容
          const index = chatMessages.value.findIndex(m => m.id === message.id);
          if (index !== -1) {
            chatMessages.value[index].content = '[消息已撤回]';
          }
        } else {
          showToast(result.info || '撤回失败');
        }
      } catch (error) {
        console.error('撤回消息失败:', error);
        showToast('撤回失败');
      }
    };

    // 删除消息
    const deleteMessage = async () => {
      const message = contextMenu.value.message;
      if (!message) return;
      
      closeContextMenu();
      
      const token = localStorage.getItem('token');
      if (!token) {
        showToast('请先登录');
        return;
      }
      
      try {
        const params = new URLSearchParams();
        params.append('messageId', message.id);
        const response = await fetch('/api/message/delete', {
          method: 'POST',
          headers: { 
            'token': token,
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          body: params.toString()
        });
        const result = await response.json();
        if (result.code === 200) {
          showToast('删除成功');
          // 从列表中移除消息
          chatMessages.value = chatMessages.value.filter(m => m.id !== message.id);
        } else {
          showToast(result.info || '删除失败');
        }
      } catch (error) {
        console.error('删除消息失败:', error);
        showToast('删除失败');
      }
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
      if (isLoggedIn.value) {
        fetchContacts();
        fetchNotificationCounts();
      }
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
      currentMenu,
      menuItems,
      contacts,
      currentContact,
      chatMessages,
      chatMessagesContainer,
      inputMessage,
      unreadCount,
      defaultAvatar,
      contactSearchQuery,
      showAddFriendModal,
      friendSearchQuery,
      searchResults,
      contextMenu,
      systemNotifications,
      likeNotifications,
      collectionNotifications,
      commentNotifications,
      currentMenuLabel,
      filteredContacts,
      selectMenu,
      selectContact,
      sendMessage,
      searchFriends,
      addFriend,
      isFriend,
      goToHome,
      goToLogin,
      goToUpload,
      goToCreationCenter,
      goToCollection,
      goToProfile,
      goToHistory,
      goToVIPCenter,
      goToMall,
      search,
      showContextMenu,
      recallMessage,
      deleteMessage,
      toggleUserInfo,
      triggerFileInput,
      handleAvatarError,
      handleAvatarUpload,
      logout,
      formatDate
    };
  }
};
</script>

<style scoped>
.message-page {
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
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
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
  background-color: #f5f5f5;
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
  object-fit: cover;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  color: #666;
  transition: color 0.3s;
  position: relative;
}

.icon-item:hover, .icon-item.active {
  color: #ff4757;
}

.icon-img {
  width: 20px;
  height: 20px;
}

.unread-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #ff4757;
  color: white;
  font-size: 10px;
  padding: 1px 5px;
  border-radius: 8px;
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

.login-required {
  display: flex;
  align-items: center;
  justify-content: center;
  height: calc(100vh - 60px);
}

.login-content {
  text-align: center;
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.message-page-container {
  height: calc(100vh - 60px);
  display: flex;
  padding: 20px;
  gap: 20px;
}

.sidebar {
  width: 280px;
  height: 100%;              /* 新增：撑满父容器高度 */
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-menu {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.menu-item:hover {
  background: #f5f5f5;
}

.menu-item.active {
  background: #fff3f3;
  color: #ff4757;
}

.menu-icon {
  width: 18px;
  height: 18px;
  margin-right: 12px;
}

.menu-text {
  flex: 1;
  font-size: 14px;
}

.badge {
  background: #ff4757;
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
}

.sidebar-contacts {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.contacts-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  font-weight: 500;
  color: #333;
  flex-shrink: 0;
}

.add-btn {
  background: #ff4757;
  color: white;
  border: none;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  font-size: 16px;
  cursor: pointer;
}

.contact-search-box {
  padding: 0 12px 12px;
  flex-shrink: 0;
}

.contact-search-box input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  font-size: 13px;
  outline: none;
}

.contact-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  border-bottom: 1px solid #f5f5f5;
}

.contact-item:hover {
  background: #fafafa;
}

.contact-item.active {
  background: #fff3f3;
}

.contact-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
}

.contact-info {
  flex: 1;
}

.contact-name {
  font-size: 14px;
  color: #333;
}

.online-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ccc;
}

.online-dot.online {
  background: #2ed573;
}

.empty-contacts {
  padding: 24px;
  text-align: center;
  color: #999;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.empty-contacts p {
  margin: 0 0 12px 0;
}

.add-friend-btn {
  background: #ff4757;
  color: white;
  border: none;
  padding: 6px 16px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
}

.main-content {
  flex: 1;
  height: 100%;              /* 新增：撑满父容器高度 */
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  overflow: hidden;
  display: flex;
}

.chat-area {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.chat-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.chat-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
}

.chat-info {
  flex: 1;
}

.chat-name {
  font-weight: 600;
  font-size: 15px;
}

.online-status {
  font-size: 12px;
  color: #999;
}

.online-status.online {
  color: #2ed573;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.empty-chat {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: #999;
  height: 100%;
}

.empty-chat-icon {
  width: 80px;
  height: 80px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.message-item {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  gap: 10px;
}

.message-item.mine {
  justify-content: flex-end;
}

.message-bubble {
  max-width: 60%;
  display: flex;
  flex-direction: column;
  min-width: 60px;
  min-height: 40px;
}

.message-item.mine .message-bubble {
  align-items: flex-end;
}

.message-text {
  background: #f0f0f0;
  padding: 12px 16px;
  border-radius: 16px 16px 16px 4px;
  font-size: 14px;
  word-wrap: break-word;
  user-select: text;
  min-width: 30px;
  min-height: 24px;
}

.message-item.mine .message-text {
  background: #ff4757;
  color: white;
  border-radius: 16px 16px 4px 16px;
  user-select: text;
  min-width: 30px;
  min-height: 24px;
}

.message-time {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
  padding: 0 4px;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.chat-input {
  display: flex;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.message-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 25px;
  font-size: 14px;
  outline: none;
}

.send-btn {
  background: #ff4757;
  color: white;
  border: none;
  padding: 12px 28px;
  border-radius: 25px;
  cursor: pointer;
}

.empty-message-state {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
}

.empty-icon-wrapper {
  width: 120px;
  height: 120px;
  background: #f5f5f5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.empty-icon {
  width: 60px;
  height: 60px;
  opacity: 0.5;
}

.notification-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.notification-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.notification-header h2 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.notification-list {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 16px;
  border-bottom: 1px solid #f5f5f5;
}

.notification-icon {
  font-size: 24px;
  margin-right: 12px;
}

.notification-content {
  flex: 1;
}

.notification-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.notification-desc {
  font-size: 13px;
  color: #999;
}

.notification-time {
  font-size: 12px;
  color: #ccc;
}

.empty-notification {
  padding: 40px;
  text-align: center;
  color: #999;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 480px;
  max-width: 90%;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  margin: 0;
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

.search-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.friend-search-input {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}

.search-friend-btn {
  background: #ff4757;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
}

.search-results {
  max-height: 300px;
  overflow-y: auto;
}

.search-result-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid #f5f5f5;
}

.result-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
}

.result-info {
  flex: 1;
}

.result-name {
  font-weight: 500;
}

.result-id {
  font-size: 12px;
  color: #999;
}

.search-tips {
  text-align: center;
  padding: 20px;
  color: #999;
}

/* 右键菜单 */
.context-menu {
  position: fixed;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  padding: 8px 0;
  min-width: 120px;
  z-index: 10000;
  border: 1px solid #f0f0f0;
}

.context-menu .menu-item {
  padding: 10px 20px;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  transition: background-color 0.2s;
}

.context-menu .menu-item:hover {
  background-color: #f5f5f5;
}

.context-menu .menu-item:first-child {
  border-bottom: 1px solid #f0f0f0;
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
  box-shadow: 0 2px 12px rgba(0,0,0,0.15);
  z-index: 9999;
  transition: all 0.3s ease;
  opacity: 0;
}
</style>