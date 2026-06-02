<template>
<div class="auth-wrapper">
    <div class="auth-card">
    <div class="header">
        <h2>登录极客互娱系统</h2>
        <p>欢迎回到企业招聘管理后台</p>
    </div>
    
    <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-position="top">
        <el-form-item label="企业租户 ID" prop="tenantId">
        <el-input v-model="loginForm.tenantId" placeholder="请输入企业租户 ID" clearable />
        </el-form-item>
        <el-form-item label="手机号 / 账号" prop="username">
        <el-input v-model="loginForm.username" placeholder="请输入手机号或账号" clearable />
        </el-form-item>
        <el-form-item label="密码" prop="password">
        <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-button type="primary" class="w-full" :loading="loading" @click="handleLogin">登录</el-button>
    </el-form>
    
    <div class="footer-link">
        没有账号？<el-link type="primary" @click="$router.push('/auth/register')">立即注册企业</el-link>
    </div>
    </div>
</div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { loginEnterpriseApi } from '@/api/enterprise' // 刚刚封装的统一登录 API
import { setToken } from '@/utils/auth' // 导入你的 auth.ts 处理类

const router = useRouter()
const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({ 
tenantId: '', 
username: '', 
password: '' 
})

// 表单校验规则
const rules = {
tenantId: [{ required: true, message: '企业租户 ID 不能为空', trigger: 'blur' }],
username: [{ required: true, message: '账号不能为空', trigger: 'blur' }],
password: [{ required: true, message: '密码不能为空', trigger: 'blur' }]
}

const handleLogin = async () => {
if (!loginFormRef.value) return

await loginFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    
    loading.value = true
    try {
    // 1. 【核心枢纽】先将租户 ID 缓存到本地。
    // 因为你的 service.ts 请求拦截器会立刻读取它并拼装到 headers['tenant-id']
    localStorage.setItem('TENANT_ID', loginForm.tenantId)
    
    // 2. 发起登录请求
    // 注意：这里传给后端的参数通常是 username 和 password，后端根据租户隔离
    const res = await loginEnterpriseApi({
        username: loginForm.username,
        password: loginForm.password
    })
    
    // 3. 基于你 index.ts 的封装，非 0 状态已被拦截，走到这里的 res 必定是成功返回的数据块
    if (res && res.accessToken) {
        // 4. 将 Token 写入持久化缓存，供后面的业务请求使用
        setToken({
        accessToken: res.accessToken,
        refreshToken: res.refreshToken
        })
        
        ElMessage.success('登录成功，欢迎回来！')
        
        // 5. 跳转至系统主页（通常是 Dashboard 可视化工作台）
        router.push('/dashboard') 
    } else {
        ElMessage.error('返回数据异常，未获取到访问令牌')
    }
    } catch (error) {
    console.error('登录流程捕获异常:', error)
    // 如果登录失败，可以考虑清除刚才设置的临时租户 ID 防止影响其他操作
    // localStorage.removeItem('TENANT_ID')
    } finally {
    loading.value = false
    }
})
}
</script>

<style scoped>
.auth-wrapper { height: 100vh; display: flex; align-items: center; justify-content: center; background: #f1f5f9; }
.auth-card { width: 400px; padding: 40px; background: #fff; border-radius: 16px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.header { margin-bottom: 24px; text-align: center; }
.header h2 { font-size: 22px; color: #1e293b; margin-bottom: 8px; font-weight: bold; }
.header p { font-size: 14px; color: #64748b; }
.w-full { width: 100%; margin-top: 15px; height: 40px; font-size: 15px; }
.footer-link { margin-top: 20px; text-align: center; font-size: 14px; color: #64748b; }
:deep(.el-form-item__label) { font-weight: 600; color: #475569; }
</style>