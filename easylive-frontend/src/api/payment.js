import request from './request';

const paymentApi = {
  // 创建充值订单
  createRechargeOrder: (data) => request.post('/api/payment/recharge/order', data),
  
  // 获取充值订单详情
  getRechargeOrder: (orderId) => request.get(`/api/payment/recharge/order/${orderId}`),
  
  // 获取充值记录
  getRechargeRecords: (page, size) => request.get(`/api/payment/recharge/records?page=${page}&size=${size}`),
  
  // 获取VIP套餐列表
  getVipPackages: () => request.get('/api/payment/vip/packages'),
  
  // 购买VIP套餐
  buyVipPackage: (packageId) => request.post(`/api/payment/vip/buy/${packageId}`),
  
  // 获取VIP订单详情
  getVipOrder: (orderId) => request.get(`/api/payment/vip/order/${orderId}`),
  
  // 创建VIP订单
  createVipOrder: (data) => request.post('/api/payment/createVipOrder', data),
  
  // 支付VIP订单
  payVipOrder: (data) => request.post('/api/payment/payVipOrder', data),
  
  // 获取用户优惠券
  getUserCoupons: () => request.get('/api/payment/coupons'),
  
  // 使用优惠券
  useCoupon: (couponId) => request.post(`/api/payment/coupons/use/${couponId}`),
  
  // 领取优惠券
  claimCoupon: (data) => request.post('/api/payment/claimCoupon', data),
  
  // 获取VIP状态
  getVipStatus: (userId) => request.get(`/api/payment/getVipStatus?userId=${userId}`)
};

export default paymentApi;