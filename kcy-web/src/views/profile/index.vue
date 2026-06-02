<template>
    <div class="user-profile-page">
    <!-- 1. 个人全息高光名片 -->
    <div class="profile-hero-card">
        <div class="hero-left">
        <el-avatar :size="80" :src="userInfo.avatar" class="user-avatar" />
        <div class="user-meta">
            <div class="name-row">
            <h2>{{ userInfo.name }}</h2>
            <el-tag size="small" type="success" effect="light">已认证</el-tag>
            </div>
            <p class="sub-txt">{{ userInfo.gender }} · {{ userInfo.age }}岁 · {{ userInfo.education }} · {{ userInfo.experience }}工作经验</p>
            <div class="contact-tags">
            <span>📞 {{ userInfo.phone }}</span>
            <span>✉️ {{ userInfo.email }}</span>
            </div>
        </div>
        </div>
        
        <!-- 右侧求职状态切换 -->
        <div class="hero-right">
        <span class="status-label">当前求职状态：</span>
        <el-select v-model="userInfo.jobStatus" placeholder="请选择" style="width: 160px;" @change="handleStatusChange">
            <el-option label="积极找工作" value="积极找工作" />
            <el-option label="在职看机会" value="在职看机会" />
            <el-option label="暂无跳槽打算" value="暂无跳槽打算" />
        </el-select>
        </div>
    </div>

    <!-- 2. 数据快报看板（与底层业务动态联动） -->
    <div class="profile-stats-grid">
        <div class="stat-card" @click="navTo('/resume')">
        <div class="stat-info">
            <span class="count">{{ stats.resumeCount }}</span>
            <span class="label">在线简历库</span>
        </div>
        <el-icon class="stat-icon blue"><Document /></el-icon>
        </div>
        <div class="stat-card" @click="navTo('/deliveries')">
        <div class="stat-info">
            <span class="count">{{ stats.deliveryCount }}</span>
            <span class="label">投递动态/沟通</span>
        </div>
        <el-icon class="stat-icon green"><ChatDotRound /></el-icon>
        </div>
        <div class="stat-card" @click="navTo('/favorites')">
        <div class="stat-info">
            <span class="count">{{ stats.favoriteCount }}</span>
            <span class="label">我的收藏夹</span>
        </div>
        <el-icon class="stat-icon orange"><Star /></el-icon>
        </div>
    </div>

    <!-- 3. 功能中心金刚区布局 -->
    <div class="profile-content-layout">
        <!-- 左侧：功能卡片矩阵 -->
        <div class="main-menu-box">
        <h3 class="box-title">核心功能服务</h3>
        <div class="menu-grid">
            <div class="menu-item" @click="navTo('/resume')">
            <div class="icon-wrapper r-blue"><DocumentChecked /></div>
            <div class="menu-text">
                <h4>简历管理</h4>
                <p>个性化多方向在线简历打磨</p>
            </div>
            </div>
            <div class="menu-item" @click="navTo('/deliveries')">
            <div class="icon-wrapper r-green"><ChatLineRound /></div>
            <div class="menu-text">
                <h4>投递与沟通</h4>
                <p>实时跟踪投递进度与HR线上沟通</p>
            </div>
            </div>
            <div class="menu-item" @click="navTo('/favorites')">
            <div class="icon-wrapper r-orange"><CollectionTag /></div>
            <div class="menu-text">
                <h4>心仪收藏</h4>
                <p>聚合查阅收藏的岗位、企业与干货</p>
            </div>
            </div>
            <div class="menu-item" @click="mockAction('隐私设置')">
            <div class="icon-wrapper r-purple"><Lock /></div>
            <div class="menu-text">
                <h4>隐私设置</h4>
                <p>屏蔽企业、隐藏简历、匿名求职</p>
            </div>
            </div>
        </div>

        <!-- 退出登录区 -->
        <div class="danger-zone">
            <el-button type="danger" plain class="logout-btn" :icon="SwitchButton" @click="handleLogout">
            退出登录账号
            </el-button>
        </div>
        </div>

        <!-- 右侧：求职助手/小贴士 -->
        <div class="side-tips-box">
        <h4 class="side-title">💡 求职小助手</h4>
        <div class="tip-item">
            <h5>充实简历亮点</h5>
            <p>附带真实全栈研发背景的简历，大厂面试通过率可提升约 35%。</p>
        </div>
        <div class="tip-item">
            <h5>保持积极沟通</h5>
            <p>在“投递与沟通”中主动向 HR 询问进度，能大幅规避简历沉没。</p>
        </div>
        </div>
    </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { 
    Document, ChatDotRound, Star, DocumentChecked, 
    ChatLineRound, CollectionTag, Lock, SwitchButton 
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

// 模拟登录用户基本档案数据
const userInfo = reactive({
    name: '黄胜',
    avatar: 'https://picsum.photos/100/100?random=10',
    gender: '男',
    age: 21,
    education: '大专',
    experience: '应届生',
    phone: '138 **** 8888',
    email: 'huangsheng@example.com',
    jobStatus: '积极找工作'
})

// 各大业务模块的统计总数（与之前写好的页面组件完美暗合）
const stats = reactive({
    resumeCount: 2,
    deliveryCount: 3,
    favoriteCount: 6
})

const navTo = (path: string) => {
    router.push(path)
}

const handleStatusChange = (val: string) => {
    ElMessage.success(`求职状态已成功变更为：${val}`)
}

const mockAction = (menuName: string) => {
    ElMessage.info(`[${menuName}] 模块正在接入安全沙箱，敬请期待...`)
}

// 退出登录
const handleLogout = () => {
    ElMessageBox.confirm('确定要退出当前求职者登录状态吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
    }).then(() => {
    ElMessage.success('已安全退出，正在清除 Token 并跳转登录页...')
    // router.push('/login')
    }).catch(() => {})
}
</script>

<style scoped>
.user-profile-page { max-width: 1000px; margin: 88px auto 40px; padding: 0 20px; }

/* 1. 顶部全息英雄卡片 */
.profile-hero-card { background: #fff; border: 1px solid #eef0f5; border-radius: 12px; padding: 24px; display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.hero-left { display: flex; align-items: center; gap: 20px; }
.user-avatar { border: 3px solid #eff6ff; }
.user-meta .name-row { display: flex; align-items: center; gap: 10px; margin-bottom: 6px; }
.user-meta h2 { margin: 0; font-size: 22px; color: #1e293b; }
.sub-txt { margin: 0 0 8px; font-size: 13px; color: #64748b; }
.contact-tags { display: flex; gap: 16px; font-size: 12px; color: #94a3b8; }

.hero-right { display: flex; flex-direction: column; align-items: flex-end; gap: 6px; }
.status-label { font-size: 12px; color: #94a3b8; }

/* 2. 数据快报看板 */
.profile-stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 24px; }
.stat-card { background: #fff; border: 1px solid #eef0f5; padding: 16px 20px; border-radius: 10px; display: flex; justify-content: space-between; align-items: center; cursor: pointer; transition: 0.2s; }
.stat-card:hover { border-color: #2563eb; transform: translateY(-1px); box-shadow: 0 4px 12px rgba(37,99,235,0.03); }
.stat-info { display: flex; flex-direction: column; }
.stat-info .count { font-size: 22px; font-weight: 700; color: #1e293b; }
.stat-info .label { font-size: 12px; color: #64748b; margin-top: 4px; }
.stat-icon { font-size: 24px; padding: 10px; border-radius: 8px; }
.stat-icon.blue { background: #eff6ff; color: #2563eb; }
.stat-icon.green { background: #f0fdf4; color: #16a34a; }
.stat-icon.orange { background: #fff7ed; color: #ea580c; }

/* 3. 功能中心大版块 */
.profile-content-layout { display: grid; grid-template-columns: 1fr 280px; gap: 20px; align-items: start; }
.main-menu-box { background: #fff; border: 1px solid #eef0f5; border-radius: 12px; padding: 24px; }
.box-title { margin: 0 0 20px; font-size: 16px; color: #1e293b; border-left: 4px solid #2563eb; padding-left: 10px; }

/* 金刚区网格微调 */
.menu-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.menu-item { border: 1px solid #f1f5f9; background: #fafbfc; border-radius: 8px; padding: 16px; display: flex; align-items: center; gap: 14px; cursor: pointer; transition: 0.2s; }
.menu-item:hover { background: #fff; border-color: #2563eb; box-shadow: 0 4px 12px rgba(0,0,0,0.02); }

.icon-wrapper { padding: 10px; border-radius: 6px; font-size: 20px; display: flex; align-items: center; justify-content: center; }
.icon-wrapper.r-blue { background: #eff6ff; color: #2563eb; }
.icon-wrapper.r-green { background: #f0fdf4; color: #16a34a; }
.icon-wrapper.r-orange { background: #fff7ed; color: #ea580c; }
.icon-wrapper.r-purple { background: #f5f3ff; color: #7c3aed; }

.menu-text h4 { margin: 0 0 4px; font-size: 14px; color: #1e293b; }
.menu-text p { margin: 0; font-size: 11px; color: #94a3b8; line-height: 1.4; }

/* 危险区（退出登录） */
.danger-zone { border-top: 1px dashed #e2e8f0; margin-top: 24px; padding-top: 20px; text-align: center; }
.logout-btn { width: 100%; max-width: 200px; }

/* 右侧侧边栏提示卡 */
.side-tips-box { background: #f8fafc; border: 1px dashed #e2e8f0; border-radius: 12px; padding: 20px; }
.side-title { margin: 0 0 16px; font-size: 14px; color: #1e293b; }
.tip-item { margin-bottom: 16px; }
.tip-item:last-child { margin-bottom: 0; }
.tip-item h5 { margin: 0 0 4px; font-size: 13px; color: #334155; }
.tip-item p { margin: 0; font-size: 12px; color: #64748b; line-height: 1.6; }
</style>