<template>
  <div class="order-list-container">
    <Header />

    <div class="order-content">
      <div class="page-header">
        <h1>我的订单</h1>
        <div class="order-tabs">
          <button 
            v-for="tab in tabs" 
            :key="tab.id"
            class="tab-btn"
            :class="{ active: activeTab === tab.id }"
            @click="activeTab = tab.id"
          >
            {{ tab.name }}
            <span v-if="tab.count > 0" class="tab-count">{{ tab.count }}</span>
          </button>
        </div>
      </div>

      <div v-if="orders.length === 0" class="empty-state">
        <span class="empty-icon">📦</span>
        <p>暂无订单</p>
        <button class="go-shopping-btn" @click="goToMall">去逛逛</button>
      </div>

      <div v-else class="orders-list">
        <div v-for="order in orders" :key="order.id" class="order-card">
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
</template>

<script>
import Header from '../components/Header.vue';

export default {
  name: 'OrderList',
  components: { Header },
  data() {
    return {
      activeTab: 'all',
      tabs: [
        { id: 'all', name: '全部', count: 0 },
        { id: 'pending', name: '待支付', count: 0 },
        { id: 'completed', name: '已完成', count: 0 },
        { id: 'refund', name: '退款/售后', count: 0 }
      ],
      orders: [
        {
          id: 1,
          orderNo: 'ORD202605020001',
          createTime: '2026-05-02 14:30:00',
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
          orderNo: 'ORD202605020002',
          createTime: '2026-05-02 10:20:00',
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
          orderNo: 'ORD202605020003',
          createTime: '2026-05-02 16:45:00',
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
    this.loadOrders();
  },
  methods: {
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
        this.updateTabCounts();
      } catch (error) {
        console.error('获取订单列表失败:', error);
      }
    },
    updateTabCounts() {
      this.tabs.forEach(tab => {
        if (tab.id === 'all') {
          tab.count = this.orders.length;
        } else {
          tab.count = this.orders.filter(o => o.status === tab.id).length;
        }
      });
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
    formatDate(dateStr) {
      if (!dateStr) return '未知';
      const date = new Date(dateStr);
      return `${date.getFullYear()}.${date.getMonth() + 1}.${date.getDate()}`;
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
    goToMall() {
      window.location.href = '/mall';
    }
  },
  computed: {
    filteredOrders() {
      if (this.activeTab === 'all') {
        return this.orders;
      }
      return this.orders.filter(order => order.status === this.activeTab);
    }
  }
};
</script>

<style scoped>
.order-list-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  zoom: 1.25;
}

.order-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 20px;
}

.order-tabs {
  display: flex;
  gap: 16px;
}

.tab-btn {
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

.tab-btn:hover {
  border-color: #ff4757;
  color: #ff4757;
}

.tab-btn.active {
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

.tab-btn.active .tab-count {
  background-color: rgba(255, 255, 255, 0.3);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.empty-state p {
  font-size: 18px;
  color: #999;
  margin-bottom: 20px;
}

.go-shopping-btn {
  background-color: #ff4757;
  color: white;
  border: none;
  padding: 12px 32px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
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
  color: white;
}

.review-btn {
  background-color: #ffd700;
  border-color: #ffd700;
  color: #8b4513;
}

.review-btn:hover {
  background-color: #ffc700;
  border-color: #ffc700;
  color: #8b4513;
}
</style>
