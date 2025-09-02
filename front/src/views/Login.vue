<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-title">
        <span class="system-name">学生选课管理系统</span>
      </div>
      <el-form :model="data.form" ref="formRef" :rules="data.rules" class="login-form">
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
        <el-form-item prop="role">
          <el-select 
            size="large" 
            style="width: 100%" 
            v-model="data.form.role" 
            placeholder="请选择您的身份"
            class="custom-select"
          >
            <el-option value="ADMIN" label="管理员"></el-option>
            <el-option value="TEACHER" label="教师"></el-option>
            <el-option value="STUDENT" label="学生"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button 
            size="large" 
            type="primary" 
            class="login-button" 
            @click="login"
          >登 录</el-button>
        </el-form-item>
      </el-form>
      <div class="register-link">
        还没有账号？请 <router-link to="/register" class="register-text">注册</router-link>
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

  const data = reactive({
    form: {},
    rules: {
      username: [
        { required: true, message: '请输入账号', trigger: 'blur' },
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
      ],
      role: [
        { required: true, message: '请选择身份', trigger: 'change' },
      ]
    }
  })

  const formRef = ref()

  // 点击登录按钮的时候会触发这个方法
  const login = () => {
    formRef.value.validate((valid => {
      if (valid) {
        // 调用后台的接口
        request.post('/login', data.form).then(res => {
          if (res.code === '200') {
            // 只有当返回的数据不为空时才算登录成功
            if (res.data) {
              ElMessage.success("登录成功")
              router.push('/')
              localStorage.setItem('system-user', JSON.stringify(res.data))
            } else {
              ElMessage.error("用户名、密码或身份类型错误")
            }
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
.login-container {
  height: 100vh;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url("@/assets/imgs/2.png");
  background-size: cover;
  position: relative;
}

.login-box {
  position: relative;
  width: 420px;
  padding: 40px 35px;
  border-radius: 1px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  background-color: rgba(255, 255, 255, 1);
  backdrop-filter: blur(8px);
  z-index: 2;
  animation: fadeIn 0.6s ease-out;
  transition: all 0.3s ease;
}

.login-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
}

.login-title {
  text-align: center;
  margin-bottom: 35px;
}

.system-name {
  display: block;
  font-size: 30px;
  font-weight: bold;
  color: #000000;
  margin-bottom: 8px;
  letter-spacing: 1px;
}

.welcome-text {
  font-size: 16px;
  color: #666;
  letter-spacing: 1px;
}

.login-form {
  margin-bottom: 20px;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  padding: 2px 12px;
  transition: all 0.3s;
}

.custom-input :deep(.el-input__wrapper:hover),
.custom-select :deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 12px rgba(20, 80, 170, 0.1);
}

.custom-input :deep(.el-input__wrapper.is-focus),
.custom-select :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #1450aa inset;
}

.custom-select :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.login-button {
  width: 100%;
  border-radius: 8px;
  font-size: 16px;
  background: linear-gradient(to right, #1450aa, #4481cc);
  border: none;
  height: 44px;
  transition: all 0.3s;
  letter-spacing: 2px;
}

.login-button:hover {
  background: linear-gradient(to right, #0d3d8a, #3470b8);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(20, 80, 170, 0.3);
}

.login-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(20, 80, 170, 0.2);
}

.register-link {
  text-align: right;
  color: #666;
  font-size: 14px;
}

.register-text {
  color: #1450aa;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.3s;
}

.register-text:hover {
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
  .login-box {
    width: 90%;
    max-width: 400px;
    padding: 30px 25px;
  }
}
</style>