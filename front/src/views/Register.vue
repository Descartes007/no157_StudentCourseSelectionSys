<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-title">
        <span class="system-name">学生选课管理系统</span>
      </div>
      <el-form :model="data.form" ref="formRef" :rules="data.rules" class="register-form">
        <el-form-item prop="username">
          <el-input 
            :prefix-icon="User" 
            size="large" 
            v-model="data.form.username" 
            placeholder="请输入账号" 
            class="custom-input"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            :prefix-icon="Lock" 
            size="large" 
            v-model="data.form.password" 
            placeholder="请输入密码" 
            show-password 
            class="custom-input"
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input 
            :prefix-icon="Lock" 
            size="large" 
            v-model="data.form.confirmPassword" 
            placeholder="请确认密码" 
            show-password 
            class="custom-input"
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            size="large" 
            type="primary" 
            class="register-button" 
            @click="register"
          >注 册</el-button>
        </el-form-item>
      </el-form>
      <div class="login-link">
        已有账号？请 <router-link to="/login" class="login-text">登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { reactive, ref } from "vue";
  import { User, Lock } from "@element-plus/icons-vue";
  import request from "@/utils/request";
  import {ElMessage} from "element-plus";
  import router from "@/router";

  const validatePass = (rule, value, callback) => {
    if (!value) {
      callback(new Error('请确认密码'))
    } else if (value !== data.form.password) {
      callback(new Error('两次输入密码不一致'))
    } else {
      callback()
    }
  }

  const data = reactive({
    form: { role: 'USER' },
    rules: {
      username: [
        { required: true, message: '请输入账号', trigger: 'blur' },
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
      ],
      confirmPassword: [
        { validator: validatePass, trigger: 'blur' },
      ],
    }
  })


  const formRef = ref()

  // 点击注册按钮的时候会触发这个方法
  const register = () => {
    formRef.value.validate((valid => {
      if (valid) {
        // 调用后台的接口
        request.post('/register', data.form).then(res => {
          if (res.code === '200') {
            ElMessage.success("注册成功")
            router.push('/login')
          } else {
            ElMessage.error(res.msg)
          }
        })
      }
    })).catch(error => {
      console.error(error)
    })
  }

</script>

<style scoped>
.register-container {
  height: 100vh;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url("@/assets/imgs/5.png");
  background-size: cover;
  position: relative;
}

.register-box {
  position: relative;
  width: 420px;
  padding: 40px 35px;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  background-color: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  z-index: 2;
  animation: fadeIn 0.6s ease-out;
  transition: all 0.3s ease;
}

.register-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
}

.register-title {
  text-align: center;
  margin-bottom: 35px;
}

.system-name {
  display: block;
  font-weight: bold;
  font-size: 26px;
  color: #1450aa;
  margin-bottom: 8px;
  letter-spacing: 1px;
}

.welcome-text {
  font-size: 16px;
  color: #666;
  letter-spacing: 1px;
}

.register-form {
  margin-bottom: 20px;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  padding: 2px 12px;
  transition: all 0.3s;
}

.custom-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 12px rgba(20, 80, 170, 0.1);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #1450aa inset;
}

.register-button {
  width: 100%;
  border-radius: 8px;
  font-size: 16px;
  background: linear-gradient(to right, #1450aa, #4481cc);
  border: none;
  height: 44px;
  transition: all 0.3s;
  letter-spacing: 2px;
}

.register-button:hover {
  background: linear-gradient(to right, #0d3d8a, #3470b8);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(20, 80, 170, 0.3);
}

.register-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(20, 80, 170, 0.2);
}

.login-link {
  text-align: right;
  color: #666;
  font-size: 14px;
}

.login-text {
  color: #1450aa;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.3s;
}

.login-text:hover {
  color: #0d3d8a;
  text-decoration: underline;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 适配不同屏幕尺寸 */
@media (max-width: 768px) {
  .register-box {
    width: 90%;
    max-width: 400px;
    padding: 30px 25px;
  }
}
</style>