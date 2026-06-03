<template>
    <div class="auth-container">
        <div class="aurora-bg">
            <div class="blob blob-blue"></div>
            <div class="blob blob-purple"></div>
            <div class="blob blob-cyan"></div>
        </div>

        <div class="glass-card-wrapper">
            <div class="brand-showcase">
                <div class="brand-header" @click="router.push('/')">
                    <div class="brand-icon-box">⚡</div>
                    <span class="brand-name">闪聘</span>
                </div>
                
                <div class="showcase-content">
                    <h1 class="main-slogan">
                        让每一个才华<br />
                        <span class="highlight-text">精准闪耀。</span>
                    </h1>
                    <p class="sub-slogan">为 Java / Vue 全栈极客量身打造的智慧双端对接平台</p>
                </div>

                <div class="brand-footer">
                    <div class="status-tag">
                        <span class="dot"></span> 2026 智慧互联版
                    </div>
                </div>
            </div>

            <div class="form-showcase">
                <div class="form-header">
                    <h2>欢迎回来</h2>
                    <p class="form-subtitle">
                        新用户？<span class="link-btn" @click="router.push('/register')">创建新账号</span>
                    </p>
                </div>

                <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" size="large" label-position="top">
                    <el-form-item label="手机号码" prop="mobile" class="custom-form-item">
                        <el-input 
                            v-model="loginForm.mobile" 
                            placeholder="请输入11位手机号" 
                            :prefix-icon="Phone"
                            clearable 
                        />
                    </el-form-item>

                    <el-form-item label="登录密码" prop="password" class="custom-form-item">
                        <el-input 
                            v-model="loginForm.password" 
                            type="password" 
                            placeholder="请输入您的密码" 
                            :prefix-icon="Lock"
                            show-password 
                            @keyup.enter="handleLogin"
                        />
                    </el-form-item>

                    <div class="form-actions-row">
                        <el-checkbox v-model="rememberMe">记住密码</el-checkbox>
                        <span class="forget-link">忘记密码？</span>
                    </div>

                    <el-button type="primary" class="gradient-submit-btn" :loading="loading" @click="handleLogin">
                        立即安全登录
                    </el-button>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { Phone, Lock } from '@element-plus/icons-vue'
import { UserAuthApi } from '@/api/user'
import { useUserStore } from '@/store/modules/user' // 引入完美的 Store 依赖
import { setToken } from '@/utils/auth'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore() // 实例化
const loading = ref(false)
const rememberMe = ref(false)
const loginFormRef = ref<FormInstance>()

const loginForm = reactive({ mobile: '', password: '' })

onMounted(() => {
    if (route.query.mobile) {
        loginForm.mobile = route.query.mobile as string
    }
})

const validateMobile = (rule: any, value: string, callback: any) => {
    if (!value) {
        callback(new Error('请输入手机号'))
    } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的11位手机号'))
    } else {
        callback()
    }
}

const loginRules = {
    mobile: [{ validator: validateMobile, trigger: 'change' }],
    password: [{ required: true, message: '密码不能为空', trigger: 'blur' }]
}

const handleLogin = async () => {
    if (!loginFormRef.value) return
    await loginFormRef.value.validate(async (valid) => {
        if (!valid) return
        loading.value = true
        try {
            // 1. 发起登录请求
            const res = await UserAuthApi.loginUser(loginForm)
            console.log('登录成功返回结构:', res);
            
            // 2. 数据捞取兼容
            const tokenData = res.data ? res.data : res
            
            // 3. 写入安全持久化，并激活内存 Token 标识
            setToken(tokenData)
            userStore.token = tokenData.accessToken
            
            // 4. 穿透拉取核心用户信息
            await userStore.setUserInfoAction()
            
            ElMessage({ type: 'success', message: '登录成功，欢迎光临闪聘！', duration: 2000 })
            
            // 5. 优雅重定向
            const redirect = route.query.redirect as string || '/'
            router.push(redirect)
        } catch (e) {
            console.error(e)
        } finally {
            loading.value = false
        }
    })
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&family=Noto+Sans+SC:wght@400;500;700&display=swap');

/* 保持你原本非常好看的样式不变 */
.auth-container { position: relative; width: 100vw; height: 100vh; display: flex; justify-content: center; align-items: center; background-color: #0b0f19; font-family: 'Inter', 'Noto Sans SC', sans-serif; overflow: hidden; }
.aurora-bg { position: absolute; width: 100%; height: 100%; top: 0; left: 0; z-index: 1; }
.blob { position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.4; mix-blend-mode: screen; animation: floatAround 12s infinite alternate ease-in-out; }
.blob-blue { width: 450px; height: 450px; background: #2563eb; top: -10%; left: 10%; }
.blob-purple { width: 500px; height: 500px; background: #7c3aed; bottom: -10%; right: 15%; animation-delay: -3s; }
.blob-cyan { width: 350px; height: 350px; background: #06b6d4; top: 40%; left: 50%; animation-delay: -6s; }
@keyframes floatAround { 0% { transform: translate(0px, 0px) scale(1); } 100% { transform: translate(40px, 60px) scale(1.15); } }
.glass-card-wrapper { position: relative; z-index: 10; display: flex; width: 960px; height: 580px; background: rgba(255, 255, 255, 0.03); backdrop-filter: blur(25px); -webkit-backdrop-filter: blur(25px); border: 1px solid rgba(255, 255, 255, 0.08); border-radius: 24px; box-shadow: 0 24px 70px rgba(0, 0, 0, 0.4); overflow: hidden; }
@media (max-width: 900px) { .glass-card-wrapper { width: 400px; height: auto; flex-direction: column; } .brand-showcase { display: none !important; } }
.brand-showcase { flex: 1.1; background: linear-gradient(145deg, rgba(30, 41, 59, 0.5) 0%, rgba(15, 23, 42, 0.8) 100%); border-right: 1px solid rgba(255, 255, 255, 0.05); padding: 40px; display: flex; flex-direction: column; justify-content: space-between; color: #ffffff; }
.brand-header { display: flex; align-items: center; gap: 10px; cursor: pointer; }
.brand-icon-box { background: linear-gradient(135deg, #3b82f6, #2563eb); width: 36px; height: 36px; display: flex; align-items: center; justify-content: center; border-radius: 10px; font-size: 18px; font-weight: bold; box-shadow: 0 4px 14px rgba(37, 99, 235, 0.4); }
.brand-name { font-size: 20px; font-weight: 700; letter-spacing: 0.5px; }
.main-slogan { font-size: 38px; font-weight: 700; line-height: 1.3; letter-spacing: -0.5px; margin-bottom: 16px; color: #f8fafc; }
.highlight-text { background: linear-gradient(to right, #3b82f6, #06b6d4); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
.sub-slogan { font-size: 14px; color: #94a3b8; line-height: 1.6; max-width: 340px; }
.status-tag { display: inline-flex; align-items: center; gap: 6px; font-size: 12px; color: #64748b; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.05); padding: 4px 12px; border-radius: 50px; }
.status-tag .dot { width: 6px; height: 6px; background: #10b981; border-radius: 50%; box-shadow: 0 0 8px #10b981; }
.form-showcase { flex: 1; background: #ffffff; padding: 45px 50px; display: flex; flex-direction: column; justify-content: center; }
.form-header { margin-bottom: 32px; }
.form-header h2 { font-size: 26px; color: #0f172a; font-weight: 700; margin-bottom: 6px; }
.form-subtitle { font-size: 14px; color: #64748b; }
.link-btn { color: #2563eb; font-weight: 600; cursor: pointer; margin-left: 4px; transition: color 0.2s; }
.link-btn:hover { color: #1d4ed8; text-decoration: underline; }
:deep(.custom-form-item .el-form-item__label) { font-weight: 600 !important; color: #334155 !important; padding-bottom: 6px !important; font-size: 13px; }
:deep(.el-input__wrapper) { background-color: #f8fafc !important; border: 1px solid #e2e8f0 !important; box-shadow: none !important; border-radius: 12px !important; padding: 8px 14px !important; transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1) !important; }
:deep(.el-input__wrapper.is-focus) { background-color: #ffffff !important; border-color: #2563eb !important; box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.08) !important; }
:deep(.el-input__inner) { font-size: 14px !important; color: #0f172a !important; }
:deep(.el-input__inner::placeholder) { color: #94a3b8 !important; }
.form-actions-row { display: flex; justify-content: space-between; align-items: center; margin: -4px 0 24px; }
:deep(.el-checkbox__label) { color: #64748b !important; font-size: 13px; }
:deep(.el-checkbox__input.is-checked + .el-checkbox__label) { color: #2563eb !important; }
.forget-link { font-size: 13px; color: #64748b; cursor: pointer; transition: color 0.2s; }
.forget-link:hover { color: #2563eb; }
.gradient-submit-btn { width: 100%; padding: 14px 0 !important; font-size: 15px !important; font-weight: 600 !important; border-radius: 12px !important; background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%) !important; border: none !important; box-shadow: 0 4px 15px rgba(37, 99, 235, 0.25) !important; transition: all 0.25s ease !important; color: #ffffff !important; cursor: pointer; }
.gradient-submit-btn:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(37, 99, 235, 0.35) !important; filter: brightness(1.05); }
.gradient-submit-btn:active { transform: translateY(1px); }
</style>