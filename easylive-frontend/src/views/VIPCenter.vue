<template>
  <div class="vip-center">
    <Header />

    <div class="vip-container">
      <div class="vip-banner">
        <div class="banner-content">
          <h1>大会员中心</h1>
          <p>解锁专属特权，畅享精彩内容</p>
        </div>
      </div>

      <div class="vip-status" v-if="userVipInfo">
        <div class="status-card" :class="{ 'is-vip': userVipInfo.isVip }">
          <div class="status-header">
            <div class="vip-badge" v-if="userVipInfo.isVip">
              <img src="/src/recourse/icon-bigVIP.png" alt="大会员" class="vip-icon" />
              <span class="vip-text">大会员</span>
            </div>
            <span class="status-text" v-else>普通用户</span>
          </div>
          <div class="status-info" v-if="userVipInfo.isVip">
            <p class="expire-info">有效期至 {{ formatDate(userVipInfo.expireTime) }}</p>
            <p class="level-info">{{ getVipLevelText(userVipInfo.level) }}</p>
          </div>
          <div class="status-action" v-if="userVipInfo.isVip">
            <button class="renew-btn" @click="showRenewModal = true">立即续费</button>
          </div>
          <div class="status-action" v-else>
            <button class="buy-btn" @click="scrollToPackages">开通大会员</button>
          </div>
        </div>
      </div>

      <div class="vip-packages" id="vip-packages">
        <h2 class="section-title">选择套餐</h2>
        <div class="packages-grid">
          <div
            v-for="pkg in vipPackages"
            :key="pkg.id"
            class="package-card"
            :class="{ 'selected': selectedPackage === pkg.id, 'recommend': pkg.recommend }"
            @click="selectPackage(pkg.id)"
          >
            <div class="package-badge" v-if="pkg.recommend">推荐</div>
            <div class="package-header">
              <h3>{{ pkg.name }}</h3>
              <p class="package-desc">{{ pkg.description }}</p>
            </div>
            <div class="package-price">
              <span class="current-price">¥{{ pkg.price }}</span>
              <span class="original-price" v-if="pkg.originalPrice">¥{{ pkg.originalPrice }}</span>
              <span class="price-unit">{{ pkg.priceUnit }}</span>
            </div>
            <div class="package-benefits">
              <div class="benefit-item" v-for="benefit in pkg.benefits" :key="benefit">
                <span class="check-icon">✓</span>
                <span>{{ benefit }}</span>
              </div>
            </div>
            <button class="buy-package-btn" @click.stop="buyPackage(pkg)">
              {{ userVipInfo?.isVip ? '续费' : '立即开通' }}
            </button>
          </div>
        </div>
      </div>

      <div class="vip-benefits">
        <h2 class="section-title">大会员专属权益</h2>
        <div class="benefits-grid">
          <div class="benefit-card">
            <div class="benefit-icon">
              <span>🎟️</span>
            </div>
            <h3>专属折扣</h3>
            <p>享受会员购商城八折优惠</p>
          </div>
          <div class="benefit-card">
            <div class="benefit-icon">
              <span>🚫</span>
            </div>
            <h3>免广告</h3>
            <p>观看视频无广告打扰</p>
          </div>
          <div class="benefit-card">
            <div class="benefit-icon">
              <span>🎬</span>
            </div>
            <h3>高清画质</h3>
            <p>畅享4K超清画质体验</p>
          </div>
          <div class="benefit-card">
            <div class="benefit-icon">
              <span>🎁</span>
            </div>
            <h3>专属礼包</h3>
            <p>每月领取专属优惠券</p>
          </div>
        </div>
      </div>

      <div class="coupon-section">
        <h2 class="section-title">会员专属优惠券</h2>
        <div class="coupon-list">
          <div
            v-for="coupon in coupons"
            :key="coupon.id"
            class="coupon-card"
            :class="{ 'claimed': coupon.claimed, 'vip-only': coupon.vipOnly }"
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
  name: 'VIPCenter',
  components: {
    Header,
    AlipayModal
  },
  data() {
    return {
      userVipInfo: null,
      isBigVip: false,
      selectedPackage: 1,
      showPayModal: false,
      showRenewModal: false,
      currentOrder: null,
      vipPackages: [
        {
          id: 1,
          name: '月度大会员',
          description: '体验大会员专属特权',
          price: 25,
          originalPrice: 30,
          priceUnit: '/月',
          level: 1,
          recommend: false,
          benefits: ['1个月会员时长', '专属八折券', '高清画质', '免广告']
        },
        {
          id: 2,
          name: '季度大会员',
          description: '更优惠的季度套餐',
          price: 68,
          originalPrice: 90,
          priceUnit: '/季',
          level: 2,
          recommend: true,
          benefits: ['3个月会员时长', '专属八折券x3', '高清画质', '免广告', '专属客服']
        },
        {
          id: 3,
          name: '年度大会员',
          description: '最优惠的年度套餐',
          price: 198,
          originalPrice: 360,
          priceUnit: '/年',
          level: 3,
          recommend: false,
          benefits: ['12个月会员时长', '专属八折券x12', '4K超清画质', '免广告', '专属客服', '年度礼包']
        }
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
        }
      ]
    };
  },
  mounted() {
    this.loadUserVipInfo();
  },
  methods: {
    async loadUserVipInfo() {
      const token = localStorage.getItem('token');
      const userId = localStorage.getItem('userId');
      if (!token || !userId) {
        this.userVipInfo = { isVip: false };
        this.isBigVip = false;
        return;
      }

      try {
        const response = await fetch(`/api/payment/getVipStatus?userId=${userId}`, {
          headers: { 'token': token }
        });
        const result = await response.json();
        if (result.code === 200) {
          this.userVipInfo = result.data;
          this.isBigVip = result.data.isVip;
          localStorage.setItem('isBigVip', result.data.isVip ? '1' : '0');
        } else {
          this.userVipInfo = { isVip: false };
          this.isBigVip = false;
        }
      } catch (error) {
        console.error('获取VIP状态失败:', error);
        this.userVipInfo = { isVip: false };
        this.isBigVip = false;
      }
    },
    selectPackage(packageId) {
      this.selectedPackage = packageId;
    },
    scrollToPackages() {
      document.getElementById('vip-packages').scrollIntoView({ behavior: 'smooth' });
    },
    async buyPackage(vipPackage) {
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
            packageId: String(vipPackage.id),
            packageName: vipPackage.name,
            price: vipPackage.price,
            originalPrice: vipPackage.originalPrice,
            payType: 1
          })
        });
        const result = await response.json();
        if (result && (result.code === 200 || result.success)) {
          const orderData = result.data || result;
          if (!orderData.price && vipPackage.price) {
            orderData.price = vipPackage.price;
          }
          if (!orderData.packageName && vipPackage.name) {
            orderData.packageName = vipPackage.name;
          }
          this.currentOrder = orderData;
          this.showPayModal = true;
        } else {
          alert(result?.message || result?.msg || '创建订单失败');
        }
      } catch (error) {
        console.error('创建订单失败:', error);
        alert('创建订单失败，请稍后重试');
      }
    },
    async handlePaySuccess(orderId) {
      this.showPayModal = false;
      await this.loadUserVipInfo();
      // 触发VIP状态更新事件
      const vipUpdatedEvent = new CustomEvent('vipStatusUpdated', {
        detail: {
          isVip: this.userVipInfo.isVip,
          level: this.userVipInfo.level,
          expireTime: this.userVipInfo.expireTime
        }
      });
      window.dispatchEvent(vipUpdatedEvent);
      alert('支付成功！已开通大会员');
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
          alert('领取成功！');
        } else {
          alert(result.message || '领取失败');
        }
      } catch (error) {
        console.error('领取优惠券失败:', error);
        alert('领取失败，请稍后重试');
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return '未知';
      const date = new Date(dateStr);
      return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
    },
    getVipLevelText(level) {
      const levelMap = {
        1: '月卡会员',
        2: '季卡会员',
        3: '年卡会员'
      };
      return levelMap[level] || '普通会员';
    }
  }
};
</script>

<style scoped>
.vip-center {
  min-height: 100vh;
  background-color: #f5f5f5;
  zoom: 1.25;
}

.vip-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.vip-banner {
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

.vip-status {
  margin-bottom: 30px;
}

.status-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.status-card.is-vip {
  border: 2px solid #ffd700;
}

.status-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.vip-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #ffd700 0%, #ffb700 100%);
  padding: 8px 16px;
  border-radius: 20px;
}

.vip-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
}

.vip-text {
  color: #8b4513;
  font-weight: bold;
}

.status-text {
  font-size: 18px;
  color: #666;
}

.status-info {
  flex: 1;
}

.expire-info {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.level-info {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.status-action {
  margin-left: auto;
}

.renew-btn, .buy-btn {
  padding: 10px 24px;
  border: none;
  border-radius: 25px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.buy-btn {
  background: linear-gradient(135deg, #ffd700 0%, #ffb700 100%);
  color: #8b4513;
}

.buy-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 183, 0, 0.4);
}

.renew-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff4757 100%);
  color: white;
}

.renew-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 71, 87, 0.4);
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.vip-packages {
  margin-bottom: 40px;
}

.packages-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.package-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  position: relative;
  display: flex;
  flex-direction: column;
  min-height: 420px;
}

.package-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.package-card.selected {
  border-color: #ffd700;
  background: linear-gradient(135deg, #fffbf0 0%, #fff8e0 100%);
}

.package-card.recommend {
  border-color: #ff6b6b;
  background: linear-gradient(135deg, #fff5f5 0%, #ffebee 100%);
}

.package-badge {
  position: absolute;
  top: -10px;
  right: 20px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff4757 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: bold;
}

.package-header h3 {
  font-size: 20px;
  margin-bottom: 8px;
  color: #333;
}

.package-desc {
  font-size: 14px;
  color: #999;
  margin-bottom: 16px;
}

.package-price {
  margin-bottom: 16px;
}

.current-price {
  font-size: 32px;
  font-weight: bold;
  color: #ff4757;
}

.original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
  margin-left: 8px;
}

.price-unit {
  font-size: 14px;
  color: #999;
  margin-left: 4px;
}

.package-benefits {
  margin-bottom: 20px;
}

.benefit-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.check-icon {
  color: #52c41a;
  font-weight: bold;
  font-size: 16px;
}

.buy-package-btn {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #ffd700 0%, #ffb700 100%);
  color: #8b4513;
  margin-top: auto;
}

.buy-package-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 183, 0, 0.4);
}

.vip-benefits {
  margin-bottom: 40px;
}

.benefits-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.benefit-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  transition: all 0.3s ease;
}

.benefit-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.benefit-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.benefit-icon img {
  width: 32px;
  height: 32px;
}

.benefit-card h3 {
  font-size: 16px;
  margin-bottom: 8px;
  color: #333;
}

.benefit-card p {
  font-size: 14px;
  color: #999;
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
  background: white;
  border-radius: 12px;
  padding: 0;
  display: flex;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.coupon-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.coupon-card.vip-only {
  border: 2px solid #ffd700;
}

.coupon-left {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 24px 32px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 120px;
}

.coupon-value {
  font-size: 36px;
  font-weight: bold;
  color: white;
}

.coupon-condition {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 4px;
}

.coupon-right {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.coupon-right h4 {
  font-size: 16px;
  margin-bottom: 4px;
  color: #333;
}

.coupon-desc {
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.coupon-expire {
  font-size: 12px;
  color: #999;
}

.claim-btn {
  align-self: flex-start;
  padding: 8px 20px;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.claim-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.claim-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  color: #999;
}

@media (max-width: 768px) {
  .packages-grid {
    grid-template-columns: 1fr;
  }

  .benefits-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .coupon-card {
    flex-direction: column;
  }

  .coupon-left {
    min-width: 100%;
    padding: 16px;
  }
}
</style>
