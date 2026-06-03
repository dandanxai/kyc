<template>
    <div class="auth-container">
    <!-- 左侧：品牌视觉与标语区 -->
    <div class="brand-section">
        <div class="brand-overlay"></div>
        <div class="brand-content">
        <div class="logo-area">
            <span class="logo-icon">🚀</span>
            <span class="logo-text">极客互娱</span>
        </div>
        <div class="slogan-area">
            <h1>用智慧连接人才，用科技驱动未来</h1>
            <p>全新一代企业级智能招聘管理后台系统，助力企业高效构建精英团队。</p>
        </div>
        <div class="brand-footer">
            © 2026 Geek Mutual Entertainment. All Rights Reserved.
        </div>
        </div>
    </div>

    <!-- 右侧：中间居着的登录卡片区 -->
    <div class="form-section">
        <div class="login-card">
        <div class="header">
            <h2>欢迎回来</h2>
            <p>请登录您的企业招聘管理账号</p>
        </div>

        <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-position="top" size="large">
            
            <el-form-item label="手机号 / 账号" prop="mobile">
            <el-input v-model="loginForm.mobile" placeholder="请输入手机号或账号" clearable>
                <template #prefix><span class="input-icon">👤</span></template>
            </el-input>
            </el-form-item>
            
            <el-form-item label="密码" prop="password">
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password>
                <template #prefix><span class="input-icon">🔒</span></template>
            </el-input>
            </el-form-item>

            <div class="form-actions">
            <el-checkbox>记住密码</el-checkbox>
            <el-link type="info" :underlined="false">忘记密码？</el-link>
            </div>

            <el-button type="primary" class="submit-btn" :loading="loading" @click="handleLogin">
            登 录
            </el-button>
        </el-form>

        <div class="footer-link">
            没有账号？<el-link type="primary" :underlined="false" @click="$router.push('/auth/register')">立即注册企业</el-link>
        </div>
        </div>
    </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { EnterpriseAuthApi } from '@/api/enterprise' 
import { setToken } from '@/utils/auth'
import { useUserStore } from '@/store/modules/user'

const router = useRouter()
const userStore = useUserStore() // 🌟 2. 实例化 Store
const loginFormRef = ref()
const loading = ref(false)

// 默默给一个默认系统租户值（通常是 '1'）
const loginForm = reactive({ 
    tenantId: '1', 
    mobile: '', 
    password: '' 
})

// 表单校验规则
const rules = {
    mobile: [{ required: true, message: '账号不能为空', trigger: 'blur' }],
    password: [{ required: true, message: '密码不能为空', trigger: 'blur' }]
}

/**
 * 核心登录处理
 */
const handleLogin = async () => {
    if (!loginFormRef.value) return

    try {
        // 🌟 3. 现代化表单校验：使用 Promise 风格，校验失败会自动进 catch，代码更清爽
        await loginFormRef.value.validate()
        
        loading.value = true
        // 默默固定系统租户
        localStorage.setItem('TENANT_ID', loginForm.tenantId)
        
        // 调用芋道企业侧登录接口
        const res = await EnterpriseAuthApi.loginEnterprise({
            mobile: loginForm.mobile,
            password: loginForm.password
        })
        
        if (res && res.accessToken) {
            // 🌟 4. 持久化 Token 写入本地 Cookie 或 LocalStorage
            setToken({
                accessToken: res.accessToken,
                refreshToken: res.refreshToken
            })
            
            // 🌟 5. 核心闭环：趁热打铁，立刻同步加载用户信息写入 Pinia 内存
            // 这样能确保跳进 /dashboard 的瞬间，Navbar、Header 就能立刻拿到“头像、昵称”，防止页面闪烁
            await userStore.setUserInfoAction()
            
            ElMessage.success('登录成功，欢迎回来！')
            router.push('/dashboard') 
        } else {
            ElMessage.error('登录失败，未获取到有效凭证')
        }
    } catch (error: any) {
        // 🌟 6. 异常兜底：如果是表单校验未通过，不弹提示；如果是后端业务报错，给出友好反馈
        console.error('登录流程异常:', error)
        if (error !== false) {
            // 假设你的 request.ts 拦截器没有统一弹窗，这里作为兜底提示
            ElMessage.error(error.message || error.msg || '登录失败，请检查账号密码')
        }
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
/* 全屏容器，采用弹性左右分栏 */
.auth-container {
    height: 100vh;
    display: flex;
    overflow: hidden;
    background-color: #ffffff;
}

/* ================= 左侧视觉区 ================= */
.brand-section {
    position: relative;
    flex: 1.2;
    display: flex;
    background-image: url('https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=1964&auto=format&fit=crop');
    background-size: cover;
    background-position: center;
}

.brand-overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, rgba(15, 23, 42, 0.85) 0%, rgba(30, 41, 59, 0.6) 100%);
    backdrop-filter: blur(2px);
}

.brand-content {
    position: relative;
    z-index: 10;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 50px;
    color: #ffffff;
    width: 100%;
}

.logo-area {
    display: flex;
    align-items: center;
    gap: 12px;
}

.logo-icon {
    font-size: 28px;
}

.logo-text {
    font-size: 24px;
    font-weight: 800;
    letter-spacing: 1px;
    background: linear-gradient(to right, #38bdf8, #6366f1);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
}

.slogan-area h1 {
    font-size: 36px;
    font-weight: 700;
    line-height: 1.3;
    margin-bottom: 20px;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.slogan-area p {
    font-size: 16px;
    color: #cbd5e1;
    max-width: 540px;
    line-height: 1.6;
}

.brand-footer {
    font-size: 13px;
    color: #94a3b8;
}

/* ================= 右侧登录区 ================= */
.form-section {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f8fafc;
    padding: 40px;
}

.login-card {
    width: 100%;
    max-width: 420px;
    background: #ffffff;
    padding: 40px;
    border-radius: 20px;
    box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05), 0 8px 10px -6px rgba(0, 0, 0, 0.05);
    border: 1px solid #e2e8f0;
}

.header {
    margin-bottom: 32px;
}

.header h2 {
    font-size: 26px;
    color: #0f172a;
    font-weight: 700;
    margin-bottom: 10px;
}

.header p {
    font-size: 14px;
    color: #64748b;
}

/* 表单细节优化 */
:deep(.el-form-item__label) {
    font-weight: 600;
    color: #334155;
    margin-bottom: 6px !important;
}

:deep(.el-input__wrapper) {
    border-radius: 10px;
    box-shadow: 0 0 0 1px #e2e8f0 inset;
    padding: 4px 12px;
}

:deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 0 0 1px #4f46e5 inset !important;
}

.input-icon {
    font-size: 16px;
    margin-right: 4px;
}

.form-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 5px;
    margin-bottom: 24px;
}

:deep(.el-checkbox__label) {
    color: #64748b;
}

.submit-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 10px;
    background: linear-gradient(135deg, #4f46e5 0%, #3730a3 100%);
    border: none;
    box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
    transition: all 0.3s;
}

.submit-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 20px rgba(79, 70, 229, 0.4);
}

.footer-link {
    margin-top: 28px;
    text-align: center;
    font-size: 14px;
    color: #64748b;
}

/* 响应式适配：在窄屏设备上隐藏左侧视觉区 */
@media (max-width: 900px) {
    .brand-section {
    display: none;
    }
    .form-section {
    background-color: #ffffff;
    }
    .login-card {
    box-shadow: none;
    border: none;
    padding: 20px;
    }
}
</style>