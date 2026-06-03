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
                        探索无限可能，<br />
                        <span class="highlight-text">就在今日。</span>
                    </h1>
                    <p class="sub-slogan">建立独一无二的数字化简历，一键直通知名企业技术骨干</p>
                </div>

                <div class="brand-footer">
                    <div class="status-tag">
                        <span class="dot"></span> 2026 智慧互联版
                    </div>
                </div>
            </div>

            <div class="form-showcase">
                <div class="form-header">
                    <h2>求职者注册</h2>
                    <p class="form-subtitle">
                        已有闪聘账号？<span class="link-btn" @click="router.push('/login')">直接登录</span>
                    </p>
                </div>

                <div class="form-body-content">
                    <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" size="large" label-position="top">
                    
                        <div class="form-row-flex">
                            <el-form-item label="真实姓名" prop="username" class="custom-form-item flex-2">
                                <el-input v-model="registerForm.username" placeholder="请输入真实姓名" :prefix-icon="User" clearable />
                            </el-form-item>

                            <el-form-item label="性别" prop="sex" class="custom-form-item flex-1">
                                <div class="sex-selector">
                                    <div 
                                        :class="['sex-btn', { active: registerForm.sex === 1 }]" 
                                        @click="registerForm.sex = 1"
                                    >👦 男</div>
                                    <div 
                                        :class="['sex-btn', { active: registerForm.sex === 2 }]" 
                                        @click="registerForm.sex = 2"
                                    >👧 女</div>
                                </div>
                            </el-form-item>
                        </div>

                        <el-form-item label="手机号码" prop="mobile" class="custom-form-item">
                            <el-input v-model="registerForm.mobile" placeholder="请输入11位手机号" :prefix-icon="Phone" clearable />
                        </el-form-item>

                        <el-form-item label="电子邮箱 (选填)" prop="email" class="custom-form-item">
                            <el-input v-model="registerForm.email" placeholder="example@qq.com" :prefix-icon="Message" clearable />
                        </el-form-item>

                        <el-form-item label="设置密码" prop="password" class="custom-form-item">
                            <el-input 
                                v-model="registerForm.password" 
                                type="password" 
                                placeholder="密码长度为 6-20 位" 
                                :prefix-icon="Lock" 
                                show-password 
                            />
                        </el-form-item>

                        <div class="protocol-clause-box">
                            <el-checkbox v-model="isAgreed">
                                我已仔细阅读并完全同意 <span class="accent">《服务协议》</span> 与 <span class="accent">《隐私政策》</span>
                            </el-checkbox>
                        </div>

                        <el-button type="success" class="gradient-submit-btn register-theme-btn" :loading="loading" @click="handleRegister">
                            立即注册并登录
                        </el-button>
                    </el-form>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { Phone, Lock, User, Message } from '@element-plus/icons-vue'
import { UserAuthApi } from '@/api/user'
import { useUserStore } from '@/store/modules/user' // 引入你完美的 Store
import { setToken } from '@/utils/auth'

const router = useRouter()
const userStore = useUserStore() // 实例化
const loading = ref(false)
const isAgreed = ref(false)
const registerFormRef = ref<FormInstance>()

// userType=1 代表求职者
const registerForm = reactive({ 
    username: '', 
    sex: 1, 
    mobile: '', 
    email: '', 
    password: '', 
    userType: 1 
})

// 手机号正则
const validateMobile = (rule: any, value: string, callback: any) => {
    if (!value) {
        callback(new Error('手机号不能为空'))
    } else if (!/^[1][3-9][0-9]{9}$/.test(value)) {
        callback(new Error('手机号格式有误'))
    } else {
        callback()
    }
}

// 邮箱格式校验
const validateEmail = (rule: any, value: string, callback: any) => {
    if (value && !/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(value)) {
        callback(new Error('邮箱格式不正确'))
    } else {
        callback()
    }
}

const registerRules = {
    username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
    sex: [{ required: true, message: '性别不能为空', trigger: 'change' }],
    mobile: [{ validator: validateMobile, trigger: 'change' }],
    email: [{ validator: validateEmail, trigger: 'blur' }],
    password: [
        { required: true, message: '密码不能为空', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度为 6-20 位', trigger: 'blur' }
    ]
}

const handleRegister = async () => {
    if (!registerFormRef.value) return
    if (!isAgreed.value) {
        ElMessage.warning('需同意服务协议和隐私政策后方能注册')
        return
    }
    await registerFormRef.value.validate(async (valid) => {
        if (!valid) return
        loading.value = true
        try {
            // 1. 发送注册请求
            const res = await UserAuthApi.registerUser(registerForm)
            console.log('注册成功返回结构:', res)
            
            // 2. 兼容芋道框架可能包裹的 data 域，提取真正的 token 对象
            const tokenData = res.data ? res.data : res
            
            // 3. 执行本地持久化与内存同步
            setToken(tokenData) 
            userStore.token = tokenData.accessToken // 同步至你优秀的全局 Store 状态
            
            // 4. 链式穿透获取用户信息进行高亮锁死，防止页面级数据闪现
            await userStore.setUserInfoAction()
            
            ElMessage.success('账号创建成功，已为您自动安全登录！')
            
            // 5. 注册成功一键直通主页
            router.push('/')
        } catch (e) {
            console.error(e)
        } finally {
            loading.value = false
        }
    })
}
</script>

<style scoped>
/* 保持你原本非常好看的样式不变 */
.auth-container { position: relative; width: 100vw; height: 100vh; display: flex; justify-content: center; align-items: center; background-color: #0b0f19; font-family: 'Inter', 'Noto Sans SC', sans-serif; overflow: hidden; }
.aurora-bg { position: absolute; width: 100%; height: 100%; top: 0; left: 0; z-index: 1; }
.blob { position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.4; mix-blend-mode: screen; animation: floatAround 12s infinite alternate ease-in-out; }
.blob-blue { width: 450px; height: 450px; background: #2563eb; top: -10%; left: 10%; }
.blob-purple { width: 500px; height: 500px; background: #7c3aed; bottom: -10%; right: 15%; animation-delay: -3s; }
.blob-cyan { width: 350px; height: 350px; background: #06b6d4; top: 40%; left: 50%; animation-delay: -6s; }
@keyframes floatAround { 0% { transform: translate(0px, 0px) scale(1); } 100% { transform: translate(40px, 60px) scale(1.15); } }
.glass-card-wrapper { position: relative; z-index: 10; display: flex; width: 960px; height: 560px; background: rgba(255, 255, 255, 0.03); backdrop-filter: blur(25px); -webkit-backdrop-filter: blur(25px); border: 1px solid rgba(255, 255, 255, 0.08); border-radius: 24px; box-shadow: 0 24px 70px rgba(0, 0, 0, 0.4); overflow: hidden; }
@media (max-width: 900px) { .glass-card-wrapper { width: 400px; height: auto; flex-direction: column; } .brand-showcase { display: none !important; } }
.brand-showcase { flex: 1.1; background: linear-gradient(145deg, rgba(30, 41, 59, 0.5) 0%, rgba(15, 23, 42, 0.8) 100%); border-right: 1px solid rgba(255, 255, 255, 0.05); padding: 40px; display: flex; flex-direction: column; justify-content: space-between; color: #ffffff; }
.brand-header { display: flex; align-items: center; gap: 10px; cursor: pointer; }
.brand-icon-box { background: linear-gradient(135deg, #3b82f6, #2563eb); width: 36px; height: 36px; display: flex; align-items: center; justify-content: center; border-radius: 10px; font-size: 18px; font-weight: bold; box-shadow: 0 4px 14px rgba(37, 99, 235, 0.4); }
.brand-name { font-size: 20px; font-weight: 700; letter-spacing: 0.5px; }
.main-slogan { font-size: 38px; font-weight: 700; line-height: 1.3; letter-spacing: -0.5px; margin-bottom: 16px; color: #f8fafc; }
.highlight-text { background: linear-gradient(to right, #10b981, #06b6d4); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
.sub-slogan { font-size: 14px; color: #94a3b8; line-height: 1.6; max-width: 340px; }
.status-tag { display: inline-flex; align-items: center; gap: 6px; font-size: 12px; color: #64748b; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.05); padding: 4px 12px; border-radius: 50px; }
.status-tag .dot { width: 6px; height: 6px; background: #10b981; border-radius: 50%; box-shadow: 0 0 8px #10b981; }
.form-showcase { flex: 1; background: #ffffff; padding: 40px 50px; display: flex; flex-direction: column; justify-content: center; }
.form-header { margin-bottom: 20px; }
.form-header h2 { font-size: 24px; color: #0f172a; font-weight: 700; margin-bottom: 4px; }
.form-subtitle { font-size: 13px; color: #64748b; }
.link-btn { color: #2563eb; font-weight: 600; cursor: pointer; margin-left: 4px; }
.link-btn:hover { color: #1d4ed8; text-decoration: underline; }
.form-body-content { text-align: left; }
.form-row-flex { display: flex; gap: 16px; width: 100%; }
.flex-2 { flex: 1.8; }
.flex-1 { flex: 1.2; }
.sex-selector { display: flex; gap: 8px; width: 100%; height: 40px; }
.sex-btn { flex: 1; border: 1px solid #e2e8f0; background: #f8fafc; border-radius: 12px; font-size: 13px; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: all 0.2s; color: #475569; }
.sex-btn:hover { background: #f1f5f9; }
.sex-btn.active { border-color: #2563eb; color: #2563eb; background: #f0f6ff; font-weight: 600; box-shadow: 0 2px 8px rgba(37, 99, 235, 0.06); }
:deep(.custom-form-item) { margin-bottom: 14px !important; }
:deep(.custom-form-item .el-form-item__label) { font-weight: 600 !important; color: #334155 !important; padding-bottom: 2px !important; font-size: 12px; margin-bottom: 1px; line-height: 1.5 !important;}
:deep(.el-input__wrapper) { background-color: #f8fafc !important; border: 1px solid #e2e8f0 !important; box-shadow: none !important; border-radius: 12px !important; padding: 4px 12px !important; height: 40px; transition: all 0.25s ease !important; }
:deep(.el-input__wrapper.is-focus) { background-color: #ffffff !important; border-color: #2563eb !important; box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.08) !important; }
.protocol-clause-box { margin: 12px 0 16px; font-size: 12px; }
.accent { color: #2563eb; font-weight: 500; cursor: pointer; }
.accent:hover { text-decoration: underline; }
.gradient-submit-btn { width: 100%; padding: 12px 0 !important; font-size: 14px !important; font-weight: 600 !important; border-radius: 12px !important; border: none !important; color: #ffffff !important; cursor: pointer; transition: all 0.25s !important; }
.register-theme-btn { background: linear-gradient(135deg, #10b981 0%, #059669 100%) !important; box-shadow: 0 4px 15px rgba(16, 185, 129, 0.25) !important; }
.register-theme-btn:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(16, 185, 129, 0.35) !important; filter: brightness(1.05); }
</style>