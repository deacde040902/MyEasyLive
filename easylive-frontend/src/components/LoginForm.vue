<template>
  <div class="login-form-container">
    <!-- 返回首页按钮 -->
    <button class="back-home-btn" @click="goToHome">返回首页</button>
    <div class="login-form-content">
      <!-- 左侧图片 -->
      <div class="form-left">
        <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=anime%20girl%20with%20pink%20hair%20holding%20microphone%20cute%20manga%20style&image_size=portrait_4_3" alt="登录插图" />
      </div>
      <!-- 右侧表单 -->
      <div class="form-right">
        <div class="form-header">
          <h2>{{ isLogin ? (isAdmin ? '管理员登录' : '登录') : '注册' }}</h2>
          <div class="form-tabs">
            <span :class="{ active: isLogin && !isAdmin }" @click="isLogin = true; isAdmin = false">用户登录</span>
            <span :class="{ active: isLogin && isAdmin }" @click="isLogin = true; isAdmin = true">管理员登录</span>
            <span :class="{ active: !isLogin }" @click="isLogin = false">注册</span>
          </div>
        </div>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <input type="email" v-model="form.email" placeholder="请输入邮箱" required />
          </div>
          <div v-if="!isLogin" class="form-group">
            <input type="text" v-model="form.nickName" placeholder="请输入昵称" required />
          </div>
          <div class="form-group">
            <input type="password" v-model="form.password" placeholder="请输入密码" required />
          </div>
          <div v-if="!isLogin" class="form-group">
            <input type="password" v-model="form.confirmPassword" placeholder="请确认密码" required />
          </div>
          <div class="form-group">
            <input type="text" v-model="form.checkCode" placeholder="请输入验证码" required />
            <img :src="captchaImage" alt="验证码" class="captcha-image" @click="refreshCaptcha" />
          </div>
          <div v-if="!isLogin" class="form-group">
            <input type="text" v-model="form.emailCode" placeholder="请输入邮箱验证码" required />
            <button type="button" class="send-code-btn" @click="sendEmailCode" :disabled="isSendingCode">
              {{ isSendingCode ? `${countdown}秒后重发` : '发送验证码' }}
            </button>
          </div>
          <div v-if="isLogin && !isAdmin" class="form-actions">
            <span class="forgot-password" @click="showForgotPassword = true">忘记密码？</span>
          </div>
          <button type="submit" class="submit-btn">{{ isLogin ? (isAdmin ? '管理员登录' : '登录') : '注册' }}</button>
          <div v-if="isLogin && !isAdmin" class="third-party-login">
            <div class="divider">或</div>
            <button type="button" class="qq-login-btn" @click="qqLogin">
              <img src="/src/recourse/icon-QQ.png" alt="QQ" class="qq-icon" /> QQ登录
            </button>
          </div>
        </form>
      </div>
    </div>
    
    <!-- 忘记密码弹窗 -->
    <div v-if="showForgotPassword" class="forgot-password-modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>忘记密码</h3>
          <button class="close-btn" @click="showForgotPassword = false">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="handleForgotPassword">
            <div class="form-group">
              <input type="email" v-model="forgotForm.email" placeholder="请输入邮箱" required />
            </div>
            <div class="form-group">
              <input type="text" v-model="forgotForm.checkCode" placeholder="请输入验证码" required />
              <img :src="forgotCaptchaImage" alt="验证码" class="captcha-image" @click="refreshForgotCaptcha" />
            </div>
            <div class="form-group">
              <input type="text" v-model="forgotForm.emailCode" placeholder="请输入邮箱验证码" required />
              <button type="button" class="send-code-btn" @click="sendForgotEmailCode" :disabled="isSendingForgotCode">
                {{ isSendingForgotCode ? `${forgotCountdown}秒后重发` : '发送验证码' }}
              </button>
            </div>
            <div class="form-group">
              <input type="password" v-model="forgotForm.newPassword" placeholder="请输入新密码" required />
            </div>
            <div class="form-group">
              <input type="password" v-model="forgotForm.confirmNewPassword" placeholder="请确认新密码" required />
            </div>
            <button type="submit" class="submit-btn">重置密码</button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      isLogin: true,
      isAdmin: false,
      showForgotPassword: false,
      form: {
        email: '',
        nickName: '',
        password: '',
        confirmPassword: '',
        checkCode: '',
        emailCode: '',
        checkCodeKey: ''
      },
      forgotForm: {
        email: '',
        checkCode: '',
        emailCode: '',
        newPassword: '',
        confirmNewPassword: '',
        checkCodeKey: ''
      },
      captchaImage: '',
      forgotCaptchaImage: '',
      isSendingCode: false,
      isSendingForgotCode: false,
      countdown: 60,
      forgotCountdown: 60,
      countdownTimer: null,
      forgotCountdownTimer: null
    }
  },
  mounted() {
    this.refreshCaptcha();
  },
  methods: {
    // 返回首页
    goToHome() {
      this.$router.push('/');
    },
    
    // 刷新图片验证码
    async refreshCaptcha() {
      try {
        const url = this.isAdmin ? '/admin/checkCode' : '/api/userInfo/checkCode';
        const response = await fetch(url, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json'
          }
        });
        
        if (!response.ok) {
          throw new Error('获取验证码失败');
        }
        
        const result = await response.json();
        
        if (result.code === 200 && result.data) {
          this.captchaImage = result.data.checkCode;
          this.form.checkCodeKey = result.data.checkCodeKey;
        } else {
          throw new Error(result.message || '获取验证码失败');
        }
      } catch (error) {
        console.error('获取验证码失败:', error);
        this.captchaImage = '';
        this.form.checkCodeKey = '';
      }
    },
    
    // 刷新忘记密码的图片验证码
    async refreshForgotCaptcha() {
      try {
        const response = await fetch('/api/userInfo/checkCode', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json'
          }
        });
        
        if (!response.ok) {
          throw new Error('获取验证码失败');
        }
        
        const result = await response.json();
        
        if (result.code === 200 && result.data) {
          this.forgotCaptchaImage = result.data.checkCode;
          this.forgotForm.checkCodeKey = result.data.checkCodeKey;
        } else {
          throw new Error(result.message || '获取验证码失败');
        }
      } catch (error) {
        console.error('获取验证码失败:', error);
        this.forgotCaptchaImage = '';
        this.forgotForm.checkCodeKey = '';
      }
    },
    
    // 发送邮箱验证码
    async sendEmailCode() {
      if (!this.form.email.includes('@')) {
        this.showToast('请输入有效的邮箱地址');
        return;
      }
      
      if (!this.form.checkCode) {
        this.showToast('请输入图片验证码');
        return;
      }
      
      this.isSendingCode = true;
      
      try {
        const response = await fetch('/api/user/userInfo/sendEmailCode', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            email: this.form.email,
            type: '0', // 注册验证码类型
            checkCodeKey: this.form.checkCodeKey,
            checkCode: this.form.checkCode
          })
        });
        
        if (!response.ok) {
          throw new Error('发送验证码失败');
        }
        
        const result = await response.json();
        
        if (result.code === 200) {
          this.showToast('验证码发送成功，请注意查收');
          
          // 开始倒计时
          this.countdown = 60;
          this.countdownTimer = setInterval(() => {
            this.countdown--;
            if (this.countdown <= 0) {
              clearInterval(this.countdownTimer);
              this.isSendingCode = false;
            }
          }, 1000);
        } else {
          throw new Error(result.message || '发送验证码失败');
        }
      } catch (error) {
        console.error('发送验证码失败:', error);
        this.showToast('发送验证码失败，请稍后重试');
        this.isSendingCode = false;
      }
    },
    
    // 处理表单提交
    async handleSubmit() {
      if (!this.isLogin && this.form.password !== this.form.confirmPassword) {
        this.showToast('两次输入的密码不一致');
        return;
      }
      
      if (!this.form.checkCode) {
        this.showToast('请输入验证码');
        return;
      }
      
      if (!this.isLogin && !this.form.emailCode) {
        this.showToast('请输入邮箱验证码');
        return;
      }
      
      try {
        if (this.isLogin) {
          if (this.isAdmin) {
            // 管理员登录
            const response = await fetch('/admin/login', {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json'
              },
              body: JSON.stringify({
                checkCodeKey: this.form.checkCodeKey,
                checkCode: this.form.checkCode,
                userName: this.form.email,
                password: this.form.password
              })
            });
            
            if (!response.ok) {
              throw new Error('管理员登录失败');
            }
            
            const result = await response.json();
            
            if (result.code === 200 && result.data) {
              // 保存token和用户信息
              localStorage.setItem('token', result.data.token);
              localStorage.setItem('nickName', result.data.nickName);
              localStorage.setItem('userId', result.data.userId);
              this.showToast('管理员登录成功');
              // 登录成功后跳转到管理员首页
              this.$router.push('/admin');
            } else {
              throw new Error(result.message || '管理员登录失败');
            }
          } else {
            // 用户登录
            const response = await fetch('/api/user/userInfo/login', {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json'
              },
              body: JSON.stringify({
                checkCodeKey: this.form.checkCodeKey,
                checkCode: this.form.checkCode,
                email: this.form.email,
                password: this.form.password
              })
            });
            
            if (!response.ok) {
              throw new Error('登录失败');
            }
            
            const result = await response.json();
            
            if (result.code === 200 && result.data) {
              // 保存token和用户信息
              localStorage.setItem('token', result.data.token);
              localStorage.setItem('nickName', result.data.nickName);
              localStorage.setItem('userId', result.data.userId);
              this.showToast('登录成功');
              // 登录成功后跳转到首页
              this.$router.push('/');
            } else {
              throw new Error(result.message || '登录失败');
            }
          }
        } else {
          // 注册
          const response = await fetch('/api/user/userInfo/register', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify({
              checkCodeKey: this.form.checkCodeKey,
              checkCode: this.form.checkCode,
              email: this.form.email,
              nickName: this.form.nickName,
              password: this.form.password,
              emailCode: this.form.emailCode
            })
          });
          
          if (!response.ok) {
            throw new Error('注册失败');
          }
          
          const result = await response.json();
          
          if (result.code === 200) {
            this.showToast('注册成功');
            // 注册成功后切换到登录
            this.isLogin = true;
          } else {
            throw new Error(result.message || '注册失败');
          }
        }
      } catch (error) {
        console.error(this.isLogin ? (this.isAdmin ? '管理员登录失败:' : '登录失败:') : '注册失败:', error);
        this.showToast(this.isLogin ? (this.isAdmin ? '管理员登录失败，请稍后重试' : '登录失败，请稍后重试') : '注册失败，请稍后重试');
      }
    },
    
    // 发送忘记密码邮箱验证码
    async sendForgotEmailCode() {
      if (!this.forgotForm.email.includes('@')) {
        this.showToast('请输入有效的邮箱地址');
        return;
      }
      
      if (!this.forgotForm.checkCode) {
        this.showToast('请输入图片验证码');
        return;
      }
      
      this.isSendingForgotCode = true;
      
      try {
        const response = await fetch('/api/user/userInfo/sendEmailCode', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            email: this.forgotForm.email,
            type: '1', // 忘记密码验证码类型
            checkCodeKey: this.forgotForm.checkCodeKey,
            checkCode: this.forgotForm.checkCode
          })
        });
        
        if (!response.ok) {
          throw new Error('发送验证码失败');
        }
        
        const result = await response.json();
        
        if (result.code === 200) {
          this.showToast('验证码发送成功，请注意查收');
          
          // 开始倒计时
          this.forgotCountdown = 60;
          this.forgotCountdownTimer = setInterval(() => {
            this.forgotCountdown--;
            if (this.forgotCountdown <= 0) {
              clearInterval(this.forgotCountdownTimer);
              this.isSendingForgotCode = false;
            }
          }, 1000);
        } else {
          throw new Error(result.message || '发送验证码失败');
        }
      } catch (error) {
        console.error('发送验证码失败:', error);
        this.showToast('发送验证码失败，请稍后重试');
        this.isSendingForgotCode = false;
      }
    },
    
    // 处理忘记密码
    async handleForgotPassword() {
      if (this.forgotForm.newPassword !== this.forgotForm.confirmNewPassword) {
        this.showToast('两次输入的密码不一致');
        return;
      }
      
      if (!this.forgotForm.checkCode) {
        this.showToast('请输入验证码');
        return;
      }
      
      if (!this.forgotForm.emailCode) {
        this.showToast('请输入邮箱验证码');
        return;
      }
      
      try {
        const response = await fetch('/api/user/userInfo/resetPwd', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            checkCodeKey: this.forgotForm.checkCodeKey,
            checkCode: this.forgotForm.checkCode,
            email: this.forgotForm.email,
            emailCode: this.forgotForm.emailCode,
            newPwd: this.forgotForm.newPassword,
            confirmPwd: this.forgotForm.confirmNewPassword
          })
        });
        
        if (!response.ok) {
          throw new Error('重置密码失败');
        }
        
        const result = await response.json();
        
        if (result.code === 200) {
          this.showToast('密码重置成功，请使用新密码登录');
          this.showForgotPassword = false;
          // 重置表单
          this.forgotForm = {
            email: '',
            checkCode: '',
            emailCode: '',
            newPassword: '',
            confirmNewPassword: '',
            checkCodeKey: ''
          };
        } else {
          throw new Error(result.message || '重置密码失败');
        }
      } catch (error) {
        console.error('重置密码失败:', error);
        this.showToast('重置密码失败，请稍后重试');
      }
    },
    
    // QQ登录
    qqLogin() {
      // 跳转到QQ登录授权页面
      window.location.href = '/api/user/userInfo/qqLogin';
    },
    // 显示友好的提示
    showToast(message) {
      // 创建一个toast元素
      const toast = document.createElement('div');
      toast.className = 'toast';
      toast.textContent = message;
      
      // 添加样式
      toast.style.position = 'fixed';
      toast.style.top = '50px';
      toast.style.left = '50%';
      toast.style.transform = 'translateX(-50%) translateY(-20px)';
      toast.style.backgroundColor = 'white';
      toast.style.color = 'black';
      toast.style.padding = '12px 20px';
      toast.style.borderRadius = '4px';
      toast.style.boxShadow = '0 2px 12px 0 rgba(0, 0, 0, 0.1)';
      toast.style.zIndex = '9999';
      toast.style.transition = 'all 0.3s ease';
      toast.style.opacity = '0';
      
      // 添加到页面
      document.body.appendChild(toast);
      
      // 显示动画
      setTimeout(() => {
        toast.style.opacity = '1';
        toast.style.transform = 'translateX(-50%) translateY(0)';
      }, 10);
      
      // 3秒后隐藏
      setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(-50%) translateY(-20px)';
        setTimeout(() => {
          document.body.removeChild(toast);
        }, 300);
      }, 3000);
    }
  },
  watch: {
    isAdmin(newVal) {
      // 切换登录模式时刷新验证码
      this.refreshCaptcha();
    }
  },
  beforeUnmount() {
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer);
    }
    if (this.forgotCountdownTimer) {
      clearInterval(this.forgotCountdownTimer);
    }
  }
}
</script>

<style scoped>
.login-form-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
}

/* 返回首页按钮 */
.back-home-btn {
  position: absolute;
  top: 20px;
  left: 20px;
  padding: 8px 16px;
  background-color: rgba(255, 255, 255, 0.9);
  color: #333;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  z-index: 1001;
}

.back-home-btn:hover {
  background-color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.login-form-content {
  display: flex;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  width: 900px;
  max-width: 90%;
  height: 600px;
}

.form-left {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 0;
}

.form-left img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.form-right {
  flex: 1;
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header {
  margin-bottom: 40px;
}

.form-header h2 {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.form-tabs {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 30px;
}

.form-tabs span {
  padding: 8px 20px;
  cursor: pointer;
  font-size: 16px;
  color: #666;
  border-bottom: 2px solid transparent;
  transition: all 0.3s ease;
}

.form-tabs span.active {
  color: #1890ff;
  border-bottom-color: #1890ff;
}

.form-group {
  margin-bottom: 20px;
  position: relative;
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.captcha-image {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  width: 100px;
  height: 36px;
  cursor: pointer;
  border-radius: 4px;
}

.send-code-btn {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  padding: 6px 12px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s ease;
}

.send-code-btn:hover {
  background-color: #40a9ff;
}

.send-code-btn:disabled {
  background-color: #d9d9d9;
  cursor: not-allowed;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 20px;
}

.submit-btn:hover {
  background-color: #40a9ff;
}

/* 表单操作 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 10px;
}

.forgot-password {
  color: #1890ff;
  cursor: pointer;
  font-size: 14px;
  transition: color 0.3s ease;
}

.forgot-password:hover {
  color: #40a9ff;
  text-decoration: underline;
}

/* 第三方登录 */
.third-party-login {
  margin-top: 30px;
}

.divider {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  color: #999;
  font-size: 14px;
}

.divider::before {
  content: '';
  flex: 1;
  height: 1px;
  background-color: #f0f0f0;
  margin-right: 10px;
}

.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background-color: #f0f0f0;
  margin-left: 10px;
}

.qq-login-btn {
  width: 100%;
  padding: 12px;
  background-color: #f0f0f0;
  color: #333;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.qq-login-btn:hover {
  background-color: #e6f7ff;
  border-color: #1890ff;
  color: #1890ff;
}

.qq-icon {
  width: 18px;
  height: 18px;
  object-fit: contain;
}

/* 忘记密码弹窗 */
.forgot-password-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1002;
}

.modal-content {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  width: 400px;
  max-width: 90%;
  overflow: hidden;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
  transition: color 0.3s ease;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-form-content {
    flex-direction: column;
    height: auto;
  }
  
  .form-left {
    padding: 30px;
  }
  
  .form-right {
    padding: 40px 30px;
  }
}
</style>