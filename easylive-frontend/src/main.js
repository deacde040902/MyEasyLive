import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import Home from './views/Home.vue'
import Login from './views/Login.vue'
import Profile from './views/Profile.vue'
import Upload from './views/Upload.vue'
import CreationCenter from './views/CreationCenter.vue'
import VideoDetail from './views/VideoDetail.vue'
import Collection from './views/Collection.vue'
import Search from './views/Search.vue'
import History from './views/History.vue'
import Message from './views/Message.vue'
import VIPCenter from './views/VIPCenter.vue'
import Mall from './views/Mall.vue'
import OrderList from './views/OrderList.vue'
import AdminDashboard from './views/AdminDashboard.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile
  },
  {
    path: '/upload',
    name: 'Upload',
    component: Upload
  },
  {
    path: '/creation-center',
    name: 'CreationCenter',
    component: CreationCenter
  },
  {
    path: '/video/detail/:videoId',
    name: 'VideoDetail',
    component: VideoDetail
  },
  {
    path: '/collection',
    name: 'Collection',
    component: Collection
  },
  {
    path: '/search',
    name: 'Search',
    component: Search
  },
  {
    path: '/history',
    name: 'History',
    component: History
  },
  {
    path: '/message',
    name: 'Message',
    component: Message
  },
  {
    path: '/vip-center',
    name: 'VIPCenter',
    component: VIPCenter
  },
  {
    path: '/mall',
    name: 'Mall',
    component: Mall
  },
  {
    path: '/orders',
    name: 'OrderList',
    component: OrderList
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: AdminDashboard
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const app = createApp(App)
app.use(router)

const lazyLoad = {
  mounted(el, binding) {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          const img = entry.target;
          const src = binding.value;
          if (src) {
            img.src = src;
          }
          observer.unobserve(img);
        }
      });
    }, {
      rootMargin: '100px',
      threshold: 0.1
    });
    observer.observe(el);
    el.__lazyObserver__ = observer;
  },
  unmounted(el) {
    if (el.__lazyObserver__) {
      el.__lazyObserver__.unobserve(el);
    }
  }
};

app.directive('lazy-load', lazyLoad)
app.mount('#app')