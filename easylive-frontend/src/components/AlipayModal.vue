<template>
  <div class="alipay-modal-overlay" @click="handleOverlayClick">
    <div class="alipay-modal" @click.stop>
      <div class="modal-header">
        <div class="alipay-logo">
          <img src="/src/recourse/icon-aliPay.png" alt="支付宝" class="alipay-icon" />
          <span class="alipay-title">支付宝支付</span>
        </div>
        <button class="close-btn" @click="handleClose">×</button>
      </div>

      <div class="modal-body">
        <div class="order-info">
          <div class="order-item">
            <span class="label">商品名称</span>
            <span class="value">{{ orderInfo?.packageName || '大会员套餐' }}</span>
          </div>
          <div class="order-item">
            <span class="label">订单编号</span>
            <span class="value order-no">{{ orderInfo?.orderId || '---' }}</span>
          </div>
          <div class="order-item">
            <span class="label">支付金额</span>
            <span class="value price">¥{{ orderInfo?.price || '0.00' }}</span>
          </div>
        </div>

        <div class="qr-section">
          <div class="qr-container">
            <div class="qr-code">
              <div class="qr-pattern">
                <div class="qr-corner top-left"></div>
                <div class="qr-corner top-right"></div>
                <div class="qr-corner bottom-left"></div>
                <div class="qr-corner bottom-right"></div>
                <div class="qr-grid">
                  <div v-for="i in 225" :key="i" class="qr-cell" :class="{ 'filled': getCellState(i) }"></div>
                </div>
              </div>
              <div class="qr-mask" v-if="!isPaying && !isPaid">
            <div class="scan-hint">
              <img src="/src/recourse/icon-QR.png" alt="二维码" class="scan-icon" />
            </div>
          </div>
              <div class="pay-success" v-if="isPaid">
                <div class="success-icon">✓</div>
                <p>支付成功</p>
              </div>
              <div class="pay-processing" v-if="isPaying">
                <div class="loading-spinner"></div>
                <p>支付处理中...</p>
              </div>
            </div>
            <p class="qr-tip">请使用支付宝APP扫码</p>
          </div>
        </div>

        <div class="quick-pay">
          <button class="quick-pay-btn" @click="handleQuickPay" :disabled="isPaying || isPaid">
            <img src="/src/recourse/icon-aliPay.png" alt="支付宝" class="btn-icon" />
            <span>{{ isPaid ? '支付成功' : '点击快速支付' }}</span>
          </button>
        </div>
      </div>

      <div class="modal-footer">
        <p class="footer-tip">支付安全由支付宝保障</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AlipayModal',
  props: {
    orderInfo: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      isPaying: false,
      isPaid: false,
      qrSeed: Math.random()
    };
  },
  created() {
    this.qrSeed = Math.random();
  },
  methods: {
    getCellState(index) {
      const row = Math.floor(index / 15);
      const col = index % 15;
      
      const cornerSize = 7;
      const isInCorner = 
        (row < cornerSize && col < cornerSize) ||
        (row < cornerSize && col >= 15 - cornerSize) ||
        (row >= 15 - cornerSize && col < cornerSize) ||
        (row >= 15 - cornerSize && col >= 15 - cornerSize);
      
      if (isInCorner) {
        if ((row < 6 && col < 6) || (row < 6 && col >= 9) || (row >= 9 && col < 6) || (row >= 9 && col >= 9)) {
          const innerRow = row < 6 ? row : row - 9;
          const innerCol = col < 6 ? col : col - 9;
          if (innerRow === 0 || innerRow === 5 || innerCol === 0 || innerCol === 5) {
            return true;
          }
          if (innerRow > 1 && innerRow < 4 && innerCol > 1 && innerCol < 4) {
            return true;
          }
          return false;
        }
      }
      
      const center = 7;
      if (Math.abs(row - center) <= 1 && Math.abs(col - center) <= 1) {
        if (Math.abs(row - center) === 1 || Math.abs(col - center) === 1) {
          return true;
        }
        return false;
      }
      
      const hash = (row * 15 + col + this.qrSeed * 1000) % 7;
      return hash < 3;
    },
    handleOverlayClick() {
      if (!this.isPaying && !this.isPaid) {
        this.handleClose();
      }
    },
    handleClose() {
      this.$emit('close');
    },
    async handleQuickPay() {
      if (this.isPaying || this.isPaid) return;

      this.isPaying = true;

      await new Promise(resolve => setTimeout(resolve, 2000));

      const token = localStorage.getItem('token');
      try {
        const response = await fetch('/api/payment/payVipOrder', {
          method: 'POST',
          headers: {
            'token': token,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            orderId: this.orderInfo.orderId,
            payType: 1
          })
        });
        const result = await response.json();
        if (result.code === 200) {
          this.isPaying = false;
          this.isPaid = true;
          localStorage.setItem('isBigVip', '1');
          setTimeout(() => {
            this.$emit('success', this.orderInfo.orderId);
          }, 1500);
        } else {
          this.isPaying = false;
          alert(result.message || '支付失败');
        }
      } catch (error) {
        this.isPaying = false;
        console.error('支付失败:', error);
        alert('支付失败，请稍后重试');
      }
    }
  }
};
</script>

<style scoped>
.alipay-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.alipay-modal {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 420px;
  overflow: hidden;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.modal-header {
  background: linear-gradient(135deg, #1677ff 0%, #0958d9 100%);
  padding: 20px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.alipay-logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.alipay-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
}

.alipay-title {
  font-size: 18px;
  font-weight: bold;
  color: white;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  font-size: 24px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s ease;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.modal-body {
  padding: 24px;
}

.order-info {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #e9ecef;
}

.order-item:last-child {
  border-bottom: none;
}

.order-item .label {
  font-size: 14px;
  color: #666;
}

.order-item .value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.order-item .value.price {
  font-size: 20px;
  font-weight: bold;
  color: #ff4757;
}

.order-item .value.order-no {
  font-size: 12px;
  color: #999;
}

.qr-section {
  margin-bottom: 20px;
}

.qr-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.qr-code {
  width: 200px;
  height: 200px;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.qr-pattern {
  width: 100%;
  height: 100%;
  padding: 8px;
  box-sizing: border-box;
  display: flex;
  justify-content: center;
  align-items: center;
}

.qr-corner {
  width: 28px;
  height: 28px;
  border: 3px solid #000;
  position: absolute;
  background: #fff;
}

.qr-corner.top-left {
  top: 8px;
  left: 8px;
}

.qr-corner.top-right {
  top: 8px;
  right: 8px;
}

.qr-corner.bottom-left {
  bottom: 8px;
  left: 8px;
}

.qr-corner.bottom-right {
  bottom: 8px;
  right: 8px;
}

.qr-grid {
  display: grid;
  grid-template-columns: repeat(15, 1fr);
  grid-template-rows: repeat(15, 1fr);
  gap: 0.5px;
  width: 100%;
  height: 100%;
  background: #fff;
  padding: 4px;
  box-sizing: border-box;
}

.qr-cell {
  background: #fff;
}

.qr-cell.filled {
  background: #000;
}

.qr-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  display: flex;
  align-items: center;
  justify-content: center;
}

.scan-hint {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.scan-icon {
  width: 200px;
  height: 200px;
  object-fit: cover;
  border-radius: 12px;
}

.scan-hint p {
  font-size: 14px;
  color: #666;
}

.pay-success {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
}

.success-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: white;
  color: #52c41a;
  font-size: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  animation: scaleIn 0.3s ease;
}

@keyframes scaleIn {
  from { transform: scale(0); }
  to { transform: scale(1); }
}

.pay-success p {
  font-size: 18px;
  font-weight: bold;
}

.pay-processing {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #1677ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.pay-processing p {
  font-size: 14px;
  color: #666;
}

.qr-tip {
  font-size: 12px;
  color: #999;
  margin-top: 12px;
}

.quick-pay {
  margin-bottom: 10px;
}

.quick-pay-btn {
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, #1677ff 0%, #0958d9 100%);
  color: white;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: all 0.3s ease;
}

.quick-pay-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(22, 119, 255, 0.4);
}

.quick-pay-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.btn-icon {
  width: 24px;
  height: 24px;
  border-radius: 6px;
}

.modal-footer {
  padding: 16px 24px;
  background: #f8f9fa;
  text-align: center;
}

.footer-tip {
  font-size: 12px;
  color: #999;
}
</style>