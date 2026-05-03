<template>
  <div class="mall-container">
    <Header />

    <div class="mall-content">
      <!-- 页面导航标签 -->
      <div class="mall-tabs">
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'products' }"
          @click="activeTab = 'products'"
        >
          🛒 商品
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'orders' }"
          @click="activeTab = 'orders'"
        >
          📦 我的订单
          <span v-if="orders.length > 0" class="order-count">{{ orders.length }}</span>
        </button>
      </div>

      <!-- 商品页面 -->
      <div v-show="activeTab === 'products'">
        <div class="mall-banner">
          <div class="banner-content">
            <h1>会员购商城</h1>
            <p>会员专享八折优惠</p>
          </div>
        </div>

        <div class="coupon-section">
          <h2 class="section-title">会员专属优惠券</h2>
          <div class="coupon-list">
            <div 
              v-for="coupon in coupons" 
              :key="coupon.id" 
              class="coupon-card"
              :class="{ 'claimed': coupon.claimed, 'vip-only': coupon.vipOnly, 'disabled': !isBigVip && coupon.vipOnly }"
            >
              <div class="coupon-left">
                <span class="coupon-value">{{ coupon.value }}</span>
                <span class="coupon-condition">{{ coupon.condition }}</span>
              </div>
              <div class="coupon-right">
                <h4>{{ coupon.name }}</h4>
                <p class="coupon-desc">{{ coupon.description }}</p>
                <p class="coupon-expire">有效期至 {{ formatDate(coupon.expireTime) }}</p>
                <button 
                  class="claim-btn" 
                  :disabled="coupon.claimed || (!isBigVip && coupon.vipOnly)"
                  @click="claimCoupon(coupon)"
                >
                  {{ coupon.claimed ? '已领取' : (!isBigVip && coupon.vipOnly ? '非会员' : '立即领取') }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="products-section">
          <div class="section-header">
            <h2 class="section-title">热门商品</h2>
            <div class="filter-tabs">
              <button 
                v-for="tab in filterTabs" 
                :key="tab.id"
                class="filter-tab"
                :class="{ 'active': activeFilter === tab.id }"
                @click="activeFilter = tab.id"
              >
                {{ tab.name }}
              </button>
            </div>
          </div>

          <div class="products-grid">
            <div 
              v-for="product in products" 
              :key="product.id" 
              class="product-card"
            >
              <div class="product-image">
                <img :src="product.image" :alt="product.name" />
                <div class="product-badge" v-if="product.discount">{{ product.discount }}折</div>
              </div>
              <div class="product-info">
                <h3 class="product-name">{{ product.name }}</h3>
                <p class="product-desc">{{ product.description }}</p>
                <div class="product-price-row">
                  <span class="product-price">¥{{ isBigVip ? (product.price * 0.8).toFixed(2) : product.price }}</span>
                  <span class="product-original-price" v-if="isBigVip">¥{{ product.price }}</span>
                </div>
                <div class="product-tags">
                  <span class="tag" v-if="product.tag">{{ product.tag }}</span>
                  <span class="vip-tag" v-if="isBigVip">会员价</span>
                </div>
                <button class="buy-btn" @click="buyProduct(product)">
                  {{ isBigVip ? '立即购买' : '开通会员享八折' }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="my-coupons-section">
          <h2 class="section-title">我的优惠券</h2>
          <div v-if="myCoupons.length === 0" class="empty-state">
            <span class="empty-icon">🎫</span>
            <p>暂无优惠券</p>
            <button class="go-claim-btn" @click="scrollToCoupons">去领取</button>
          </div>
          <div class="coupon-list" v-else>
            <div 
              v-for="coupon in myCoupons" 
              :key="coupon.id" 
              class="coupon-card owned"
            >
              <div class="coupon-left">
                <span class="coupon-value">{{ coupon.value }}</span>
                <span class="coupon-condition">{{ coupon.condition }}</span>
              </div>
              <div class="coupon-right">
                <h4>{{ coupon.name }}</h4>
                <p class="coupon-desc">{{ coupon.description }}</p>
                <p class="coupon-expire">有效期至 {{ formatDate(coupon.expireTime) }}</p>
                <button class="use-btn" @click="useCoupon(coupon)">立即使用</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 订单页面 -->
      <div v-show="activeTab === 'orders'">
        <div class="orders-header">
          <h1>我的订单</h1>
          <div class="order-tabs">
            <button 
              v-for="tab in orderTabs" 
              :key="tab.id"
              class="order-tab-btn"
              :class="{ active: activeOrderTab === tab.id }"
              @click="activeOrderTab = tab.id"
            >
              {{ tab.name }}
              <span v-if="getOrderCount(tab.id) > 0" class="tab-count">{{ getOrderCount(tab.id) }}</span>
            </button>
          </div>
        </div>

        <div v-if="filteredOrders.length === 0" class="empty-state">
          <span class="empty-icon">📦</span>
          <p>暂无订单</p>
          <button class="go-shopping-btn" @click="activeTab = 'products'">去逛逛</button>
        </div>

        <div v-else class="orders-list">
          <div v-for="order in filteredOrders" :key="order.id" class="order-card">
            <div class="order-header">
              <div class="order-info">
                <span class="order-id">订单号: {{ order.orderNo }}</span>
                <span class="order-date">{{ formatDate(order.createTime) }}</span>
              </div>
              <span class="order-status" :class="getStatusClass(order.status)">
                {{ getStatusText(order.status) }}
              </span>
            </div>
            
            <div class="order-items">
              <div v-for="item in order.items" :key="item.id" class="order-item">
                <img :src="item.image" :alt="item.name" class="item-image" />
                <div class="item-info">
                  <h4 class="item-name">{{ item.name }}</h4>
                  <p class="item-desc">{{ item.description }}</p>
                  <div class="item-price-row">
                    <span class="item-price">¥{{ item.price }}</span>
                    <span class="item-quantity">x{{ item.quantity }}</span>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="order-footer">
              <div class="order-total">
                <span class="total-label">实付金额:</span>
                <span class="total-amount">¥{{ order.totalAmount }}</span>
              </div>
              <div class="order-actions">
                <button v-if="order.status === 'pending'" class="action-btn pay-btn" @click="payOrder(order)">
                  去支付
                </button>
                <button v-if="order.status === 'completed'" class="action-btn review-btn" @click="reviewOrder(order)">
                  评价
                </button>
                <button class="action-btn detail-btn" @click="viewDetail(order)">
                  查看详情
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <AlipayModal 
      v-if="showPayModal" 
      :order-info="currentOrder" 
      @close="showPayModal = false"
      @success="handlePaySuccess"
    />
  </div>
</template>

<script>
import Header from '../components/Header.vue';
import AlipayModal from '../components/AlipayModal.vue';

export default {
  name: 'Mall',
  components: {
    Header,
    AlipayModal
  },
  data() {
    return {
      activeTab: 'products',
      activeOrderTab: 'all',
      orderTabs: [
        { id: 'all', name: '全部' },
        { id: 'pending', name: '待支付' },
        { id: 'completed', name: '已完成' },
        { id: 'refund', name: '退款/售后' }
      ],
      isBigVip: false,
      showPayModal: false,
      currentOrder: null,
      activeFilter: 'all',
      filterTabs: [
        { id: 'all', name: '全部' },
        { id: 'vip', name: '会员专享' },
        { id: 'hot', name: '热门' }
      ],
      coupons: [
        {
          id: 1,
          name: '会员专属八折券',
          description: '全场商品八折优惠',
          value: '8折',
          condition: '满10元可用',
          expireTime: '2025-12-31',
          claimed: false,
          vipOnly: true
        },
        {
          id: 2,
          name: '新会员专属券',
          description: '新开通会员专享',
          value: '5元',
          condition: '满20元可用',
          expireTime: '2025-06-30',
          claimed: false,
          vipOnly: true
        },
        {
          id: 3,
          name: '限时特惠券',
          description: '限时优惠，先到先得',
          value: '3元',
          condition: '满15元可用',
          expireTime: '2025-03-31',
          claimed: false,
          vipOnly: false
        }
      ],
      myCoupons: [],
      products: [
        {
          id: 1,
          name: '大会员月度卡',
          description: '畅享30天大会员特权',
          price: 30,
          discount: 8,
          tag: '热销',
          image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=VIP%20membership%20card%20golden%20premium&image_size=square'
        },
        {
          id: 2,
          name: '大会员季度卡',
          description: '畅享90天大会员特权',
          price: 90,
          discount: 7.6,
          tag: '推荐',
          image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=VIP%20quarterly%20membership%20card%20silver&image_size=square'
        },
        {
          id: 3,
          name: '大会员年度卡',
          description: '畅享365天大会员特权',
          price: 360,
          discount: 5.5,
          tag: '超值',
          image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=VIP%20yearly%20membership%20card%20diamond&image_size=square'
        },
        {
          id: 4,
          name: '精美周边礼盒',
          description: '官方限定周边套装',
          price: 128,
          discount: null,
          tag: null,
          image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=anime%20merchandise%20gift%20box%20collection&image_size=square'
        },
        {
          id: 5,
          name: '数字头像挂件',
          description: '专属数字头像装饰',
          price: 19.9,
          discount: null,
          tag: '新品',
          image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20avatar%20accessories%20digital%20items&image_size=square'
        },
        {
          id: 6,
          name: '专属表情包',
          description: '大会员专属表情包',
          price: 9.9,
          discount: null,
          tag: null,
          image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20emoji%20pack%20collection&image_size=square'
        }
      ],
      orders: [
        {
          id: 1,
          orderNo: 'ORD202401150001',
          createTime: '2024-01-15 14:30:00',
          status: 'completed',
          totalAmount: 24.00,
          items: [
            {
              id: 1,
              name: '大会员月度卡',
              description: '畅享30天大会员特权',
              price: 24.00,
              quantity: 1,
              image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=VIP%20membership%20card%20golden%20premium&image_size=square'
            }
          ]
        },
        {
          id: 2,
          orderNo: 'ORD202401160002',
          createTime: '2024-01-16 10:20:00',
          status: 'completed',
          totalAmount: 72.00,
          items: [
            {
              id: 2,
              name: '大会员季度卡',
              description: '畅享90天大会员特权',
              price: 72.00,
              quantity: 1,
              image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=VIP%20quarterly%20membership%20card%20silver&image_size=square'
            }
          ]
        },
        {
          id: 3,
          orderNo: 'ORD202401170003',
          createTime: '2024-01-17 16:45:00',
          status: 'pending',
          totalAmount: 128.00,
          items: [
            {
              id: 3,
              name: '精美周边礼盒',
              description: '官方限定周边套装',
              price: 128.00,
              quantity: 1,
              image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=anime%20merchandise%20gift%20box%20collection&image_size=square'
            }
          ]
        }
      ]
    };
  },
  mounted() {
    this.checkVipStatus();
    this.loadMyCoupons();
    this.loadOrders();
  },
  computed: {
    filteredOrders() {
      if (this.activeOrderTab === 'all') {
        return this.orders;
      }
      return this.orders.filter(order => order.status === this.activeOrderTab);
    }
  },
  methods: {
    checkVipStatus() {
      this.isBigVip = localStorage.getItem('isBigVip') === '1';
    },
    loadMyCoupons() {
      const savedCoupons = localStorage.getItem('myCoupons');
      if (savedCoupons) {
        this.myCoupons = JSON.parse(savedCoupons);
      }
    },
    async loadOrders() {
      const token = localStorage.getItem('token');
      const userId = localStorage.getItem('userId');
      if (!token || !userId) {
        return;
      }

      try {
        const response = await fetch('/api/order/getOrders', {
          method: 'POST',
          headers: {
            'token': token,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ userId, pageNo: 1, pageSize: 50 })
        });
        const result = await response.json();
        if (result.code === 200 && result.data) {
          this.orders = result.data;
        }
      } catch (error) {
        console.error('获取订单列表失败:', error);
      }
    },
    getOrderCount(status) {
      if (status === 'all') {
        return this.orders.length;
      }
      return this.orders.filter(o => o.status === status).length;
    },
    getStatusText(status) {
      const statusMap = {
        pending: '待支付',
        completed: '已完成',
        refund: '退款中',
        cancelled: '已取消'
      };
      return statusMap[status] || status;
    },
    getStatusClass(status) {
      const classMap = {
        pending: 'status-pending',
        completed: 'status-completed',
        refund: 'status-refund',
        cancelled: 'status-cancelled'
      };
      return classMap[status] || '';
    },
    scrollToCoupons() {
      document.querySelector('.coupon-section').scrollIntoView({ behavior: 'smooth' });
    },
    async claimCoupon(coupon) {
      if (coupon.claimed || (!this.isBigVip && coupon.vipOnly)) {
        return;
      }

      const token = localStorage.getItem('token');
      const userId = localStorage.getItem('userId');
      if (!token || !userId) {
        this.$router.push('/login');
        return;
      }

      try {
        const response = await fetch('/api/payment/claimCoupon', {
          method: 'POST',
          headers: { 
            'token': token,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            userId: userId,
            couponId: coupon.id
          })
        });
        const result = await response.json();
        if (result.code === 200) {
          coupon.claimed = true;
          this.myCoupons.push({ ...coupon });
          localStorage.setItem('myCoupons', JSON.stringify(this.myCoupons));
          alert('领取成功！');
        } else {
          alert(result.message || '领取失败');
        }
      } catch (error) {
        console.error('领取优惠券失败:', error);
        alert('领取失败，请稍后重试');
      }
    },
    useCoupon(coupon) {
      alert(`使用优惠券: ${coupon.name}`);
    },
    async buyProduct(product) {
      if (!this.isBigVip) {
        if (confirm('开通大会员可享受八折优惠，是否立即开通？')) {
          window.location.href = '/vip-center';
        }
        return;
      }

      const token = localStorage.getItem('token');
      const userId = localStorage.getItem('userId');
      if (!token || !userId) {
        this.$router.push('/login');
        return;
      }

      try {
        const response = await fetch('/api/payment/createVipOrder', {
          method: 'POST',
          headers: { 
            'token': token,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            userId: userId,
            packageId: product.id,
            packageName: product.name,
            price: this.isBigVip ? (product.price * 0.8).toFixed(2) : product.price,
            originalPrice: product.price,
            payType: 1
          })
        });
        const result = await response.json();
        if (result.code === 200) {
          this.currentOrder = result.data;
          this.showPayModal = true;
        } else {
          alert(result.message || '创建订单失败');
        }
      } catch (error) {
        console.error('创建订单失败:', error);
        alert('创建订单失败，请稍后重试');
      }
    },
    async handlePaySuccess(orderId) {
      this.showPayModal = false;
      await this.loadOrders();
      alert('支付成功！');
    },
    payOrder(order) {
      alert(`正在支付订单 ${order.orderNo}...`);
    },
    reviewOrder(order) {
      alert(`评价订单 ${order.orderNo}`);
    },
    viewDetail(order) {
      alert(`查看订单 ${order.orderNo} 详情`);
    },
    formatDate(dateStr) {
      if (!dateStr) return '未知';
      const date = new Date(dateStr);
      return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
    }
  }
};
</script>

<style scoped>
.mall-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  zoom: 1.25;
}

.mall-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.mall-tabs {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.tab-btn {
  padding: 12px 32px;
  background-color: white;
  border: 2px solid #e0e0e0;
  border-radius: 30px;
  cursor: pointer;
  font-size: 16px;
  color: #666;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tab-btn:hover {
  border-color: #ff4757;
  color: #ff4757;
}

.tab-btn.active {
  background-color: #ff4757;
  border-color: #ff4757;
  color: white;
}

.order-count {
  background-color: #fff;
  color: #ff4757;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: bold;
}

.tab-btn.active .order-count {
  background-color: rgba(255, 255, 255, 0.3);
  color: white;
}

.mall-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 60px 40px;
  text-align: center;
  color: white;
  margin-bottom: 30px;
}

.banner-content h1 {
  font-size: 36px;
  margin-bottom: 10px;
}

.banner-content p {
  font-size: 18px;
  opacity: 0.9;
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.coupon-section {
  margin-bottom: 40px;
}

.coupon-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.coupon-card {
  display: flex;
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.2s;
}

.coupon-card:hover:not(.disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.coupon-card.disabled {
  opacity: 0.6;
}

.coupon-card.vip-only .coupon-left {
  background: linear-gradient(135deg, #ffd700 0%, #ffb700 100%);
}

.coupon-card.owned {
  opacity: 0.8;
}

.coupon-left {
  flex: 1;
  background: linear-gradient(135deg, #ff4757 0%, #ff6b7a 100%);
  padding: 24px;
  text-align: center;
  color: white;
}

.coupon-value {
  display: block;
  font-size: 36px;
  font-weight: bold;
}

.coupon-condition {
  font-size: 14px;
  opacity: 0.9;
}

.coupon-right {
  flex: 2;
  padding: 20px;
}

.coupon-right h4 {
  font-size: 16px;
  margin-bottom: 8px;
  color: #333;
}

.coupon-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.coupon-expire {
  font-size: 12px;
  color: #999;
  margin-bottom: 12px;
}

.claim-btn, .use-btn {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.claim-btn {
  background-color: #ff4757;
  color: white;
}

.claim-btn:hover:not(:disabled) {
  background-color: #ff3344;
}

.claim-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.use-btn {
  background-color: #ffd700;
  color: #8b4513;
}

.use-btn:hover {
  background-color: #ffc700;
}

.products-section {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filter-tabs {
  display: flex;
  gap: 8px;
}

.filter-tab {
  padding: 8px 20px;
  background-color: white;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: all 0.2s;
}

.filter-tab:hover {
  border-color: #ff4757;
}

.filter-tab.active {
  background-color: #ff4757;
  border-color: #ff4757;
  color: white;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.product-card {
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.2s;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.product-image {
  position: relative;
  width: 100%;
  padding-top: 56.25%;
}

.product-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  background-color: #ff4757;
  color: white;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
}

.product-info {
  padding: 16px;
}

.product-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #333;
}

.product-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-price-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 8px;
}

.product-price {
  font-size: 20px;
  font-weight: bold;
  color: #ff4757;
}

.product-original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.product-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.tag {
  font-size: 12px;
  color: #ff6b7a;
  background-color: #ffeef0;
  padding: 4px 8px;
  border-radius: 4px;
}

.vip-tag {
  font-size: 12px;
  color: #8b4513;
  background-color: #fff8dc;
  padding: 4px 8px;
  border-radius: 4px;
}

.buy-btn {
  width: 100%;
  padding: 12px;
  background-color: #ff4757;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.2s;
}

.buy-btn:hover {
  background-color: #ff3344;
}

.my-coupons-section {
  margin-bottom: 40px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
  color: #999;
  margin-bottom: 16px;
}

.go-claim-btn, .go-shopping-btn {
  background-color: #ff4757;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

/* 订单页面样式 */
.orders-header {
  margin-bottom: 30px;
}

.orders-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 20px;
}

.order-tabs {
  display: flex;
  gap: 16px;
}

.order-tab-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 10px 20px;
  background-color: white;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: all 0.2s;
}

.order-tab-btn:hover {
  border-color: #ff4757;
  color: #ff4757;
}

.order-tab-btn.active {
  background-color: #ff4757;
  border-color: #ff4757;
  color: white;
}

.tab-count {
  background-color: #ff4757;
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
}

.order-tab-btn.active .tab-count {
  background-color: rgba(255, 255, 255, 0.3);
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.order-info {
  display: flex;
  gap: 16px;
}

.order-id {
  font-size: 14px;
  color: #333;
}

.order-date {
  font-size: 14px;
  color: #999;
}

.order-status {
  font-size: 14px;
  font-weight: 500;
}

.status-pending {
  color: #ff9500;
}

.status-completed {
  color: #07c160;
}

.status-refund {
  color: #ff4757;
}

.status-cancelled {
  color: #999;
}

.order-items {
  padding: 16px 20px;
}

.order-item {
  display: flex;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px dashed #f0f0f0;
}

.order-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.item-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}

.item-desc {
  font-size: 14px;
  color: #999;
  margin-bottom: 12px;
}

.item-price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-price {
  font-size: 16px;
  font-weight: 500;
  color: #ff4757;
}

.item-quantity {
  font-size: 14px;
  color: #999;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background-color: #fafafa;
}

.order-total {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.total-label {
  font-size: 14px;
  color: #666;
}

.total-amount {
  font-size: 20px;
  font-weight: bold;
  color: #ff4757;
}

.order-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  padding: 8px 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.action-btn:hover {
  border-color: #ff4757;
  color: #ff4757;
}

.pay-btn {
  background-color: #ff4757;
  border-color: #ff4757;
  color: white;
}

.pay-btn:hover {
  background-color: #ff3344;
  border-color: #ff3344;
}

.review-btn {
  background-color: #ffd700;
  border-color: #ffd700;
  color: #8b4513;
}

.review-btn:hover {
  background-color: #ffc700;
  border-color: #ffc700;
}
</style>
