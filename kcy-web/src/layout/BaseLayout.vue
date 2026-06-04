<template>
    <div class="layout-container">
    <header :class="['app-header', isIndexHeader ? (isScrolled ? 'header-glass' : 'header-transparent') : 'header-glass']">
        <div class="header-content">
        
        <div class="left-section">
            <div class="logo" @click="$router.push('/')">闪聘</div>
            <nav class="nav-menu">
            <router-link to="/" class="nav-item" active-class="active">首页</router-link>
            <router-link to="/jobs" class="nav-item" active-class="active">职位</router-link>
            <router-link to="/companies" class="nav-item" active-class="active">企业</router-link>
            
            <el-dropdown v-if="isLogin" trigger="hover" @command="handleMenuCommand" class="nav-dropdown-wrapper">
                <span :class="['nav-item', 'nav-dropdown-trigger', { 'active': isMyRoute }]">
                我的 <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </span>
                <template #dropdown>
                <el-dropdown-menu class="geek-dropdown-menu">
                    <el-dropdown-item command="/resume"><el-icon><Document /></el-icon>我的简历</el-dropdown-item>
                    <el-dropdown-item command="/favorites"><el-icon><Star /></el-icon>我的收藏</el-dropdown-item>
                    <el-dropdown-item command="/deliveries"><el-icon><Position /></el-icon>我的投递</el-dropdown-item>
                </el-dropdown-menu>
                </template>
            </el-dropdown>
            </nav>
        </div>
        
        <div :class="['header-search-flex-node', { 'search-show': isSearchShow }]">
            <div class="header-search-capsule">
            <el-icon class="h-search-icon"><Search /></el-icon>
            <input 
                v-model="searchKeyword" 
                placeholder="搜索职位、大厂、技术方向..." 
                @keyup.enter="handleHeaderSearch"
            />
            <button class="h-search-btn" @click="handleHeaderSearch">
                <el-icon><Position /></el-icon>
            </button>
            </div>
        </div>
        
        <div class="right-section">
            <div class="identity-switch-btn" @click="switchToCompanyEnd">
            <span class="pulse-dot"></span>
            我要招人
            </div>

            <template v-if="isLogin">
            <div class="message-bell-box" @click="router.push('/deliveries')">
                <el-badge :value="3" :max="99" class="msg-badge" type="danger">
                <el-icon class="bell-icon"><Bell /></el-icon>
                </el-badge>
            </div>
            
            <el-dropdown trigger="hover" @command="handleUserCommand" placement="bottom-end">
                <div class="user-info-trigger">
                <el-avatar 
                    :size="32" 
                    :src="userInfo?.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" 
                    class="header-avatar" 
                />
                <span class="username">{{ userInfo?.nickname || userInfo?.mobile || '求职者' }}</span>
                </div>
                <template #dropdown>
                <el-dropdown-menu class="geek-dropdown-menu">
                    <el-dropdown-item command="/profile"><el-icon><User /></el-icon>个人中心</el-dropdown-item>
                    <el-dropdown-item command="logout" divided class="logout-item">
                    <el-icon><SwitchButton /></el-icon>退出登录
                    </el-dropdown-item>
                </el-dropdown-menu>
                </template>
            </el-dropdown>
            </template>

            <template v-else>
            <div class="login-action-btn" @click="handleGoLogin">
                登录 / 注册
            </div>
            </template>

        </div>
        </div>
    </header>

    <main class="app-main">
        <router-view />
    </main>

    <footer class="app-footer">
        <div class="footer-content">
        <p>© 2026 闪聘系统 求职端. All Rights Reserved.</p>
        <p>提供最高效、最便捷的 Java / Vue 全栈人才智慧对接 platform</p>
        </div>
    </footer>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
    Search, Position, ArrowDown, Document, 
    Star, Bell, User, SwitchButton 
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user' 

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isScrolled = ref(false)
const searchKeyword = ref('')

// 从真正的 userStore 中获取响应式的内存用户信息
const userInfo = computed(() => userStore.userInfo)

// 🌟【核心修复点一】精准对齐你的 userStore 字段，把先前写错的 accessToken 改成真实的 token
const isLogin = computed(() => {
    return !!userStore.token || !!userInfo.value?.id
})

// 判断当前是不是在首页
const isIndexHeader = computed(() => {
    return route.path === '/'
})

// 控制搜索框显隐
const isSearchShow = computed(() => {
    return !isIndexHeader.value || isScrolled.value
})

const isMyRoute = computed(() => {
    return route.path.startsWith('/user')
})

const handleScroll = () => {
    if (isIndexHeader.value) {
    isScrolled.value = window.scrollY > 180
    }
}

const handleHeaderSearch = () => {
    if (!searchKeyword.value.trim()) return
    router.push({ path: '/jobs', query: { keyword: searchKeyword.value.trim() } })
}

const handleMenuCommand = (targetPath: string) => { router.push(targetPath) }

const handleGoLogin = () => {
    router.push('/login')
}

// 🌟【核心优化点三】精简登出动作，把提示与路由重定向细节彻底托管给 store
const handleUserCommand = async (command: string) => {
    if (command === 'logout') {
    try {
        await ElMessageBox.confirm('确定要退出当前登录状态吗？', '提示', {
        confirmButtonText: '确定退出',
        cancelButtonText: '取消',
        type: 'warning'
        })
        
        // 触发你的 Store 里的安全登出逻辑，它内部会自动调用后端、清除 Token、提示成功并闪现登录页
        await userStore.logOutAction()
    } catch (error) {
        if (error !== 'cancel') {
        console.error('登出异常：', error)
        }
    }
    } else {
    router.push(command)
    }
}

const switchToCompanyEnd = () => { ElMessage.info('正在平滑切入企业端招聘协同工作台...') }

onMounted(async () => { 
    window.addEventListener('scroll', handleScroll, { passive: true }) 
    handleScroll()

    // 🌟【核心修复点二】完美根治网页刷新导致登录状态丢失的闭环逻辑
    // 当检测到客户端 Cookie/LocalStorage 里的 Token 依旧存在，但是 Pinia 里的内存状态由于刷页被置空时
    if (userStore.token && !userStore.isSetUser) {
    try {
        // 自动触发你的 Action 穿透后端网关重新同步 member_user 数据
        await userStore.setUserInfoAction()
    } catch (error) {
        console.error('网页刷新时自动捞取用户信息上下文失败:', error)
    }
    }
})

onUnmounted(() => { 
    window.removeEventListener('scroll', handleScroll) 
})
</script>

<style scoped>
.layout-container { display: flex; flex-direction: column; min-height: 100vh; background-color: #f7f9fb; }
.app-header { position: fixed; top: 0; left: 0; width: 100%; height: 68px; z-index: 1000; transition: all 0.4s cubic-bezier(0.25, 1, 0.5, 1); }
.header-transparent { background-color: transparent; box-shadow: none; border-bottom: 1px solid rgba(255, 255, 255, 0.08); }
.header-transparent .logo { color: #ffffff; }
.header-transparent .nav-item { color: rgba(255, 255, 255, 0.85); }
.header-transparent .nav-item:hover, .header-transparent .nav-item.active { color: #409eff; }
.header-transparent .username { color: #ffffff; }
.header-transparent .bell-icon { color: rgba(255, 255, 255, 0.85); }
.header-transparent .identity-switch-btn { background: rgba(255, 255, 255, 0.12); color: #ffffff; border: 1px solid rgba(255, 255, 255, 0.2); }
.header-transparent .login-action-btn { color: #ffffff; background: rgba(64, 158, 255, 0.8); }
.header-transparent .login-action-btn:hover { background: #409eff; box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3); }

.header-glass { background-color: rgba(255, 255, 255, 0.85); backdrop-filter: blur(20px) saturate(180%); -webkit-backdrop-filter: blur(20px) saturate(180%); box-shadow: 0 4px 24px rgba(10, 16, 32, 0.05); border-bottom: 1px solid rgba(235, 238, 245, 0.7); }
.header-glass .logo { color: #409eff; }
.header-glass .nav-item { color: #333d57; }
.header-glass .nav-item:hover, .header-glass .nav-item.active { color: #409eff; font-weight: 600; }
.header-glass .username { color: #333d57; }
.header-glass .bell-icon { color: #4c5a70; }
.header-glass .identity-switch-btn { background: #f0f4ff; color: #409eff; border: 1px solid rgba(64, 158, 255, 0.2); }
.header-glass .identity-switch-btn:hover { background: #409eff; color: #ffffff; }
.header-glass .login-action-btn { color: #ffffff; background: #409eff; }
.header-glass .login-action-btn:hover { background: #207eff; box-shadow: 0 4px 12px rgba(32, 126, 255, 0.3); }

.header-content { max-width: 1240px; margin: 0 auto; height: 100%; padding: 0 20px; display: flex; justify-content: space-between; align-items: center; }
.left-section { display: flex; align-items: center; flex-shrink: 0; }
.logo { font-size: 20px; font-weight: 700; cursor: pointer; margin-right: 24px; transition: color 0.3s; letter-spacing: 0.5px; }
.nav-menu { display: flex; gap: 20px; align-items: center; }
.nav-item { text-decoration: none; font-size: 14px; transition: all 0.25s ease; cursor: pointer; display: flex; align-items: center; }
.nav-dropdown-trigger { outline: none; user-select: none; }
.nav-dropdown-trigger .el-icon--right { font-size: 12px; margin-left: 2px; transition: transform 0.3s; }
.nav-dropdown-wrapper:hover .el-icon--right { transform: rotate(180deg); }
.header-search-flex-node { flex: 1; max-width: 0; opacity: 0; overflow: hidden; padding: 0; margin: 0; transition: all 0.35s cubic-bezier(0.25, 1, 0.5, 1); display: flex; justify-content: center; }
.header-search-flex-node.search-show { max-width: 280px; opacity: 1; margin: 0 20px; }
.header-search-capsule { display: flex; align-items: center; background: rgba(240, 242, 245, 0.8); border: 1px solid rgba(220, 223, 230, 0.7); border-radius: 100px; padding: 2px 2px 2px 12px; width: 100%; box-sizing: border-box; }
.header-search-capsule:focus-within { background: #ffffff; border-color: #409eff; box-shadow: 0 4px 10px rgba(64, 158, 255, 0.1); }
.h-search-icon { color: #909399; font-size: 14px; margin-right: 6px; }
.header-search-capsule input { flex: 1; border: none; background: transparent; outline: none; font-size: 12px; color: #303133; height: 26px; width: 100%; }
.header-search-capsule input::placeholder { color: #a8abb2; }
.h-search-btn { width: 26px; height: 26px; border-radius: 50%; border: none; background: #409eff; color: #ffffff; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: background 0.2s; }
.h-search-btn:hover { background: #66b1ff; }
.right-section { display: flex; align-items: center; gap: 16px; flex-shrink: 0; }
.identity-switch-btn { font-size: 12px; font-weight: 600; padding: 5px 12px; border-radius: 50px; cursor: pointer; display: flex; align-items: center; gap: 4px; transition: all 0.3s; }
.pulse-dot { width: 5px; height: 5px; background-color: #52c41a; border-radius: 50%; }
.message-bell-box { cursor: pointer; display: flex; align-items: center; }
.bell-icon { font-size: 18px; }
:deep(.msg-badge .el-badge__content.is-fixed) { top: 2px; right: 2px; scale: 0.8; }
.user-info-trigger { display: flex; align-items: center; gap: 6px; cursor: pointer; outline: none; }
.header-avatar { border: 1px solid rgba(255, 255, 255, 0.6); }
.username { font-size: 13px; }

.login-action-btn { font-size: 13px; font-weight: 600; padding: 6px 16px; border-radius: 50px; cursor: pointer; transition: all 0.3s cubic-bezier(0.25, 1, 0.5, 1); }

.geek-dropdown-menu { background: rgba(255, 255, 255, 0.95) !important; backdrop-filter: blur(16px) !important; -webkit-backdrop-filter: blur(16px) !important; border-radius: 10px !important; border: 1px solid rgba(232, 236, 243, 0.8) !important; padding: 4px !important; box-shadow: 0 8px 24px rgba(10, 16, 32, 0.06) !important; }
:deep(.el-dropdown-menu__item) { font-size: 12px !important; color: #4c5a70 !important; padding: 6px 14px !important; border-radius: 6px !important; margin: 1px 0 !important; display: flex !important; align-items: center !important; gap: 6px !important; }
:deep(.el-dropdown-menu__item:hover) { background-color: #f0f6ff !important; color: #409eff !important; }
.logout-item { color: #ff4d4f !important; }
:deep(.logout-item:hover) { background-color: #fff1f0 !important; color: #ff4d4f !important; }
.app-main { flex: 1; width: 100%; }
.app-footer { background-color: #1a233d; color: #a0aab5; padding: 32px 24px; text-align: center; font-size: 14px; border-top: 1px solid rgba(255,255,255,0.05); }
.footer-content p { margin: 6px 0; }
</style>