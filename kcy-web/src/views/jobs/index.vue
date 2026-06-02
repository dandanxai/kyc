<template>
    <div class="jobs-list-page-container">
    <div class="jobs-page-inner">
        
        <!-- ================== 左侧：独立滚动的全维度高阶筛选器 ================== -->
        <aside class="filter-sidebar">
        <!-- 包裹一层内部滚动器，防止 sticky 本身高度溢出切断内容 -->
        <div class="sidebar-scroll-content">
            <div class="filter-card-header">
            <span class="filter-title">🧭 多维条件全息筛选</span>
            <span class="clear-all-btn" @click="resetFilters">一键重置</span>
            </div>

            <!-- 1. 求职类型 -->
            <div class="filter-group">
            <label class="filter-label">求职类型</label>
            <div class="filter-options">
                <span 
                v-for="t in typeOptions" :key="t"
                :class="['opt-tag', { active: activeFilters.jobType === t }]"
                @click="activeFilters.jobType = t"
                >
                {{ t }}
                </span>
            </div>
            </div>

            <!-- 2. 工作地址 -->
            <div class="filter-group">
            <label class="filter-label">工作地址</label>
            <div class="address-cascader-box">
                <el-cascader
                v-model="activeFilters.address"
                :options="addressOptions"
                placeholder="请选择城市/区域"
                size="small"
                clearable
                class="geek-cascader"
                />
            </div>
            </div>

            <!-- 3. 薪资待遇 -->
            <div class="filter-group">
            <label class="filter-label">薪资待遇</label>
            <div class="filter-options">
                <span 
                v-for="s in salaryOptions" :key="s"
                :class="['opt-tag', { active: activeFilters.salary === s }]"
                @click="activeFilters.salary = s"
                >
                {{ s }}
                </span>
            </div>
            </div>

            <!-- 4. 工作经验 -->
            <div class="filter-group">
            <label class="filter-label">工作经验</label>
            <div class="filter-options">
                <span 
                v-for="e in expOptions" :key="e"
                :class="['opt-tag', { active: activeFilters.exp === e }]"
                @click="activeFilters.exp = e"
                >
                {{ e }}
                </span>
            </div>
            </div>

            <!-- 5. 学历要求 -->
            <div class="filter-group">
            <label class="filter-label">学历要求</label>
            <div class="filter-options">
                <span 
                v-for="edu in eduOptions" :key="edu"
                :class="['opt-tag', { active: activeFilters.edu === edu }]"
                @click="activeFilters.edu = edu"
                >
                {{ edu }}
                </span>
            </div>
            </div>

            <!-- 6. 公司规模 -->
            <div class="filter-group">
            <label class="filter-label">公司规模</label>
            <div class="filter-options">
                <span 
                v-for="size in scaleOptions" :key="size"
                :class="['opt-tag', { active: activeFilters.scale === size }]"
                @click="activeFilters.scale = size"
                >
                {{ size }}
                </span>
            </div>
            </div>

            <!-- 7. 公司行业 -->
            <div class="filter-group">
            <label class="filter-label">公司行业 (当前: {{ activeFilters.industrySub || '不限' }})</label>
            <div class="industry-accordion">
                <div 
                v-for="mainInd in industryData" 
                :key="mainInd.name" 
                class="ind-accordion-item"
                >
                <div class="ind-main-row" @click="toggleIndustryMain(mainInd.name)">
                    <span>{{ mainInd.name }}</span>
                    <el-icon :class="{ 'is-rotate': openedIndustry === mainInd.name }"><ArrowDown /></el-icon>
                </div>
                <div v-show="openedIndustry === mainInd.name" class="ind-sub-panel">
                    <span 
                    class="sub-ind-tag" 
                    :class="{ active: activeFilters.industrySub === '不限' && activeFilters.industryMain === mainInd.name }"
                    @click="selectIndustry(mainInd.name, '不限')"
                    >
                    全部子类
                    </span>
                    <span 
                    v-for="subInd in mainInd.subs" 
                    :key="subInd"
                    :class="['sub-ind-tag', { active: activeFilters.industrySub === subInd }]"
                    @click="selectIndustry(mainInd.name, subInd)"
                    >
                    {{ subInd }}
                    </span>
                </div>
                </div>
            </div>
            </div>
        </div>
        </aside>

        <!-- ================== 右侧：动态职位列表与搜索看板 ================== -->
        <main class="jobs-main-content">
        <!-- 顶部实时检索动态反馈条 -->
        <div class="list-control-panel">
            <div class="result-count-tip">
            为你智能匹配到 <span class="highlight-num">{{ filteredJobs.length }}</span> 个硬核全栈岗位
            </div>
            <div class="sort-triggers">
            <span class="sort-item active">综合排序</span>
            <span class="sort-item">最新发布</span>
            <span class="sort-item">薪资最高</span>
            </div>
        </div>

        <!-- 核心职位卡片流 -->
        <div class="jobs-cards-stack">
            <transition-group name="list-fade">
            <div 
                v-for="job in filteredJobs" 
                :key="job.id" 
                class="job-stream-card"
                @click="goToDetail(job.id)"
            >
                <!-- 卡片上部分 -->
                <div class="card-top-floor">
                <div class="job-primary-info">
                    <h3 class="job-card-title">
                    {{ job.title }}
                    <span v-if="job.isHot" class="hot-fire-badge">HOT</span>
                    <span class="type-badge">{{ job.jobType }}</span>
                    </h3>
                    <div class="job-require-meta">
                    <span><el-icon><Location /></el-icon>{{ job.city }} · {{ job.district }}</span>
                    <span class="v-line">/</span>
                    <span>{{ job.experience }}</span>
                    <span class="v-line">/</span>
                    <span>{{ job.education }}</span>
                    </div>
                </div>
                <div class="job-salary-box">
                    {{ job.salary }}
                </div>
                </div>

                <!-- 卡片中部分 -->
                <div class="card-tags-floor">
                <span v-for="tag in job.tags" :key="tag" class="job-card-tag tech-tag">
                    {{ tag }}
                </span>
                <span class="job-card-tag industry-tag">
                    {{ job.industrySub }}
                </span>
                </div>

                <!-- 卡片底部分 -->
                <div class="card-footer-floor">
                <div class="company-mini-profile">
                    <img :src="job.companyLogo" class="mini-logo" />
                    <div class="mini-info">
                    <h4 class="mini-comp-name">{{ job.companyName }}</h4>
                    <p class="mini-comp-meta">{{ job.industryMain }} · {{ job.scale }}</p>
                    </div>
                </div>
                <button class="card-action-btn" @click.stop="quickApply(job.title)">
                    立即沟通
                </button>
                </div>
            </div>
            </transition-group>

            <!-- 缺省状态 -->
            <div v-if="filteredJobs.length === 0" class="empty-jobs-placeholder">
            <el-empty description="没有找到符合当前海量筛选条件的岗位，建议您减少一些勾选项再试试~" />
            </div>
        </div>
        </main>

    </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowDown, Location } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const typeOptions = ['不限', '全职', '兼职', '实习']
const salaryOptions = ['不限', '3K以下', '3-5K', '5-10K', '10-20K', '20-50K', '50K以上']
const expOptions = ['不限', '在校生', '应届生', '经验不限', '1年以内', '1-3年', '3-5年', '5-10年', '10年以上']
const eduOptions = ['不限', '初中及以下', '中专/中技', '高中', '大专', '本科', '硕士', '博士']
const scaleOptions = ['不限', '0-20人', '20-99人', '100-499人', '500-999人', '1000-9999人', '10000人以上']

const addressOptions = [
    { value: '不限', label: '不限' },
    {
    value: '合肥', label: '合肥市',
    children: [
        { value: '全部', label: '全部区域' },
        { value: '蜀山区', label: '蜀山区' },
        { value: '政务区', label: '政务区' },
        { value: '高新区', label: '高新区' },
        { value: '经开区', label: '经开区' }
    ]
    },
    {
    value: '南京', label: '南京市',
    children: [
        { value: '全部', label: '全部区域' },
        { value: '玄武区', label: '玄武区' },
        { value: '鼓楼区', label: '鼓楼区' },
        { value: '江宁区', label: '江宁区' }
    ]
    }
]

const industryData = [
    { name: '不限', subs: [] },
    { name: '互联网/AI', subs: ['互联网', '电子商务', '计算机软件', '生活服务(O2O)', '企业服务', '医疗健康', '游戏', '社交网络与媒体', '人工智能', '云计算', '在线教育', '计算机服务', '大数据', '广告营销', '物联网', '新零售', '信息安全'] },
    { name: '电子/通信/半导体', subs: ['半导体/芯片', '电子/硬件开发', '通信/网络设备', '智能硬件/消费电子', '运营商/增值服务', '计算机硬件', '电子/半导体/集成电路'] },
    { name: '服务业', subs: ['餐饮', '美容', '美发', '酒店/民宿', '休闲/娱乐', '运动/健身', '保健/养生', '家政服务', '旅游/景区', '婚庆/摄影', '宠物服务', '回收/维修', '其他生活服务'] },
    { name: '消费品/批发/零售', subs: ['批发/零售', '进出口贸易', '食品/饮料/烟酒', '服装/纺织', '家具/家居', '家用电器', '日化', '珠宝/首饰', '家具/家电/家居', '其他消费品'] },
    { name: '房地产/建筑', subs: ['装修装饰', '房屋建筑工程', '土木工程', '机电工程', '物业管理', '房地产中介/租赁', '建筑材料', '房地产开发经营', '建筑设计', '建筑工程咨询服务', '土地与公共设施管理', '工程施工'] },
    { name: '教育培训', subs: ['培训/辅导机构', '职业培训', '学前教育', '学校/学历教育', '学术/科研'] },
    { name: '广告/传媒/文化/体育', subs: ['文化艺术/娱乐', '体育', '广告/公关/会展', '广播/影视', '新闻/出版'] },
    { name: '制造业', subs: ['通用设备', '专用设备', '电气机械/器材', '金属制品', '非金属矿物制品', '橡胶/塑料制品', '化学原料/化学制品', '仪器仪表', '自动化设备', '印刷/包装/造纸', '铁路/船舶/航空/航天制造', '计算机/通信/其他电子设备', '新材料', '机械设备/机电/重工', '仪器仪表/工业自动化', '原材料及加工/模具', '其他制造业'] },
    { name: '专业服务', subs: ['咨询', '财务/审计/税务', '人力资源服务', '法律', '检测/认证/知识产权', '翻译', '其他专业服务'] },
    { name: '制药/医疗', subs: ['医疗服务', '医美服务', '医疗器械', 'IVD', '生物/制药', '医药批发零售', '医疗研发外包'] },
    { name: '汽车', subs: ['新能源汽车', '汽车智能网联', '汽车经销商', '汽车后市场', '汽车研发/制造', '汽车零部件', '摩托车/自行车制造', '4S店/后市场'] },
    { name: '交通运输/物流', subs: ['即时配送', '快递', '公路物流', '同城货运', '跨境物流', '装卸搬运和仓储业', '客运服务', '港口/铁路/公路/机场', '交通/运输', '物流/仓储'] },
    { name: '能源/化工/环保', subs: ['光伏', '储能', '动力电池', '风电', '其他新能源', '环保', '化工', '电力/热力/燃气/水利', '石油/石化', '矿产/地质', '采掘/冶炼', '新能源'] },
    { name: '金融', subs: ['互联网金融', '银行', '投资/融资', '证券/期货', '基金', '保险', '租赁/拍卖/典当/担保', '信托', '财富管理', '其他金融业'] },
    { name: '政府/非营利组织/其他', subs: ['农/林/牧/渔', '非营利组织', '政府/公共事业', '其他行业'] }
]

const openedIndustry = ref('')
const activeFilters = reactive({
    jobType: '不限',
    address: ['不限'],
    salary: '不限',
    exp: '不限',
    edu: '不限',
    scale: '不限',
    industryMain: '不限',
    industrySub: '',
    keyword: ''
})

watch(() => route.query.keyword, (newK) => { activeFilters.keyword = (newK as string) || '' }, { immediate: true })

const allJobs = ref([
    { id: 1, title: 'Java 后端架构开发专家', salary: '15K-25K', city: '合肥', district: '蜀山区', experience: '1-3年', education: '大专', scale: '500-999人', jobType: '全职', industryMain: '互联网/AI', industrySub: '计算机软件', companyName: '科大智讯数字软件', companyLogo: 'https://picsum.photos/60/60?random=11', tags: ['Java', 'Spring Boot'], isHot: true },
    { id: 2, title: 'Vue3 前端低代码系统研发', salary: '10K-20K', city: '南京', district: '鼓楼区', experience: '1-3年', education: '本科', scale: '100-499人', jobType: '全职', industryMain: '互联网/AI', industrySub: '企业服务', companyName: '极客云创科技', companyLogo: 'https://picsum.photos/60/60?random=12', tags: ['Vue3', 'TypeScript'], isHot: false },
    { id: 3, title: 'Java 实习生(有导师带薪培训)', salary: '3-5K', city: '合肥', district: '高新区', experience: '在校生', education: '本科', scale: '20-99人', jobType: '实习', industryMain: '互联网/AI', industrySub: '互联网', companyName: '闪聘互娱研创中心', companyLogo: 'https://picsum.photos/60/60?random=13', tags: ['Java', 'Servlet'], isHot: true },
    { id: 4, title: '智慧供应链物联网负责人', salary: '20-50K', city: '上海', district: '浦东新区', experience: '5-10年', education: '硕士', scale: '1000-9999人', jobType: '全职', industryMain: '电子/通信/半导体', industrySub: '智能硬件/消费电子', companyName: '大疆新物联智造', companyLogo: 'https://picsum.photos/60/60?random=14', tags: ['物联网', 'Go'], isHot: true }
])

const filteredJobs = computed(() => {
    return allJobs.value.filter(job => {
    if (activeFilters.jobType !== '不限' && job.jobType !== activeFilters.jobType) return false
    if (activeFilters.address && activeFilters.address.length > 0 && activeFilters.address[0] !== '不限') {
        if (job.city !== activeFilters.address[0]) return false
        if (activeFilters.address[1] && activeFilters.address[1] !== '全部' && job.district !== activeFilters.address[1]) return false
    }
    if (activeFilters.salary !== '不限' && job.salary !== activeFilters.salary) return false
    if (activeFilters.exp !== '不限' && job.experience !== activeFilters.exp) return false
    if (activeFilters.edu !== '不限' && job.education !== activeFilters.edu) return false
    if (activeFilters.scale !== '不限' && job.scale !== activeFilters.scale) return false
    if (activeFilters.industryMain !== '不限') {
        if (job.industryMain !== activeFilters.industryMain) return false
        if (activeFilters.industrySub && activeFilters.industrySub !== '不限' && job.industrySub !== activeFilters.industrySub) return false
    }
    if (activeFilters.keyword) {
        const k = activeFilters.keyword.toLowerCase()
        return job.title.toLowerCase().includes(k) || job.companyName.toLowerCase().includes(k) || job.tags.some(t => t.toLowerCase().includes(k))
    }
    return true
    })
})

const toggleIndustryMain = (name: string) => {
    if (name === '不限') {
    activeFilters.industryMain = '不限'
    activeFilters.industrySub = ''
    openedIndustry.value = ''
    return
    }
    openedIndustry.value = openedIndustry.value === name ? '' : name
}

const selectIndustry = (main: string, sub: string) => {
    activeFilters.industryMain = main
    activeFilters.industrySub = sub
}

const resetFilters = () => {
    activeFilters.jobType = '不限'
    activeFilters.address = ['不限']
    activeFilters.salary = '不限'
    activeFilters.exp = '不限'
    activeFilters.edu = '不限'
    activeFilters.scale = '不限'
    activeFilters.industryMain = '不限'
    activeFilters.industrySub = ''
    activeFilters.keyword = ''
    openedIndustry.value = ''
    router.push({ path: '/jobs' })
    ElMessage.success('多维高级筛选控制台已全面初始化')
}

const goToDetail = (id: number) => { router.push({ path: `/jobs/${id}` }) }
const quickApply = (title: string) => { ElMessage.success(`已连线【${title}】面试官！`) }

onMounted(() => { window.scrollTo(0, 0) })
</script>

<style scoped>
/* 全局容器锁定高度思维：整个页面容器高度最小充满屏幕 */
.jobs-list-page-container {
    width: 100%; 
    background-color: #f6f8fa; 
    padding: 88px 20px 20px 20px; /* 留出顶部 Header 的高度 */
    height: 100vh; 
    box-sizing: border-box;
}

/* 核心改动：把外部大网格高度锁死到 100% 内部自适应空间 */
.jobs-page-inner {
    max-width: 1200px; 
    margin: 0 auto; 
    display: grid; 
    grid-template-columns: 320px 1fr; 
    gap: 20px; 
    height: 100%;
    align-items: stretch; /* 让左右两边在非滚动状态下等高 */
}

/* ================== 【关键】左侧筛选区：独立滚动区架构 ================== */
.filter-sidebar {
    position: sticky;
    top: 88px; /* 完美的吸顶安全高度 */
    height: calc(100vh - 108px); /* 视口高度减去上下边距，精确计算可支配高度 */
    background: #ffffff; 
    border-radius: 14px; 
    border: 1px solid #eef1f6;
    box-shadow: 0 4px 24px rgba(148, 163, 184, 0.05); 
    box-sizing: border-box;
    overflow: hidden; /* 切断侧边栏的外层溢出，把滚轮交给内层 */
}

/* 筛选区内层滚动容器：当鼠标在左侧时，单独在这里触发滚动 */
.sidebar-scroll-content {
    width: 100%;
    height: 100%;
    padding: 22px;
    box-sizing: border-box;
    overflow-y: auto; /* 开启左侧垂直方向独立滚动轴 */
    scrollbar-width: none; /* 针对 Firefox 隐藏原生粗糙滚动条 */
}
/* 隐藏 Chrome 和 Safari 下的左侧滚动条，让视觉极为干净 */
.sidebar-scroll-content::-webkit-scrollbar {
    display: none;
}

/* ================== 【关键】右侧职位流：独立滚动区架构 ================== */
.jobs-main-content {
    height: calc(100vh - 108px); /* 高度与左侧看齐 */
    overflow-y: auto; /* 开启右侧垂直方向独立滚动轴 */
    display: flex; 
    flex-direction: column; 
    gap: 14px;
    padding-right: 4px; /* 给右侧滚动条腾出呼吸空间 */
}

/* 优雅美化右侧核心职位流的滚动条（大厂轻量化 UI 条） */
.jobs-main-content::-webkit-scrollbar {
    width: 6px;
}
.jobs-main-content::-webkit-scrollbar-thumb {
    background: #cbd5e1;
    border-radius: 10px;
}
.jobs-main-content::-webkit-scrollbar-track {
    background: transparent;
}

/* ================== 以下维持原本优秀的 UI 样式组件 ================== */
.filter-card-header { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #f1f5f9; padding-bottom: 14px; margin-bottom: 20px; }
.filter-title { font-size: 15px; font-weight: 700; color: #0f172a; }
.clear-all-btn { font-size: 12px; color: #3b82f6; font-weight: 600; cursor: pointer; }
.filter-group { margin-bottom: 22px; border-bottom: 1px solid #f8fafc; padding-bottom: 16px; text-align: left;}
.filter-label { display: block; font-size: 13px; font-weight: 700; color: #475569; margin-bottom: 12px; }
.filter-options { display: flex; flex-wrap: wrap; gap: 6px; }
.opt-tag { font-size: 11px; color: #334155; padding: 4px 10px; background: #f1f5f9; border-radius: 5px; cursor: pointer; transition: all 0.15s ease; }
.opt-tag:hover { background: #dbeafe; color: #2563eb; }
.opt-tag.active { background: #2563eb; color: #ffffff; font-weight: 600; box-shadow: 0 2px 6px rgba(37, 99, 235, 0.2); }
.address-cascader-box, .geek-cascader { width: 100%; }
:deep(.el-input__wrapper) { background-color: #f1f5f9 !important; box-shadow: none !important; border-radius: 6px; }
.industry-accordion { border: 1px solid #e2e8f0; border-radius: 8px; overflow: hidden; max-height: 260px; overflow-y: auto; }
.ind-accordion-item { border-bottom: 1px solid #f1f5f9; }
.ind-main-row { display: flex; justify-content: space-between; align-items: center; padding: 10px 14px; font-size: 12px; color: #1e293b; background: #f8fafc; cursor: pointer; font-weight: 600; }
.ind-main-row:hover { background: #f1f5f9; color: #2563eb; }
.ind-main-row .el-icon { transition: transform 0.25s; font-size: 10px; color: #94a3b8; }
.ind-main-row .el-icon.is-rotate { transform: rotate(180deg); color: #2563eb; }
.ind-sub-panel { background: #ffffff; padding: 10px; display: flex; flex-wrap: wrap; gap: 6px; border-top: 1px solid #edf2f7; }
.sub-ind-tag { font-size: 11px; color: #64748b; padding: 3px 8px; background: #f8fafc; border-radius: 4px; cursor: pointer; }
.sub-ind-tag:hover, .sub-ind-tag.active { background: #eff6ff; color: #2563eb; font-weight: 600; }

.list-control-panel { background: #ffffff; border-radius: 12px; padding: 16px 24px; border: 1px solid #eef1f6; display: flex; justify-content: space-between; align-items: center; }
.result-count-tip { font-size: 13px; color: #475569; }
.highlight-num { font-weight: 700; color: #2563eb; margin: 0 3px; font-size: 16px; }
.sort-triggers { display: flex; gap: 20px; }
.sort-item { font-size: 13px; color: #64748b; cursor: pointer; }
.sort-item.active { color: #2563eb; font-weight: 600; }
.jobs-cards-stack { display: flex; flex-direction: column; gap: 14px; }
.job-stream-card { background: #ffffff; border-radius: 14px; border: 1px solid #eef1f6; padding: 22px 26px; cursor: pointer; transition: all 0.25s; text-align: left;}
.job-stream-card:hover { transform: translateY(-2px); border-color: #bfdbfe; box-shadow: 0 12px 30px rgba(37, 99, 235, 0.05); }
.card-top-floor { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
.job-card-title { font-size: 18px; font-weight: 600; color: #0f172a; margin: 0 0 8px 0; display: flex; align-items: center; gap: 8px; }
.hot-fire-badge { background: #fef2f2; color: #ef4444; font-size: 10px; font-weight: 700; padding: 2px 6px; border-radius: 4px; }
.type-badge { background: #f0fdf4; color: #16a34a; font-size: 10px; font-weight: 600; padding: 2px 6px; border-radius: 4px; }
.job-require-meta { font-size: 13px; color: #64748b; display: flex; align-items: center; gap: 6px; }
.job-salary-box { font-size: 20px; font-weight: 700; color: #f43f5e; }
.card-tags-floor { display: flex; flex-wrap: wrap; gap: 6px; margin-bottom: 18px; }
.job-card-tag { font-size: 11px; padding: 3px 8px; border-radius: 4px; }
.tech-tag { color: #2563eb; background: #eff6ff; }
.industry-tag { color: #475569; background: #f1f5f9; }
.card-footer-floor { border-top: 1px solid #f8fafc; padding-top: 16px; display: flex; justify-content: space-between; align-items: center; }
.company-mini-profile { display: flex; align-items: center; gap: 12px; }
.mini-logo { width: 38px; height: 38px; border-radius: 8px; object-fit: cover; border: 1px solid #edf2f7; }
.mini-comp-name { font-size: 13px; font-weight: 600; color: #1e293b; margin: 0 0 3px 0; }
.mini-comp-meta { font-size: 11px; color: #94a3b8; margin: 0; }
.card-action-btn { height: 32px; padding: 0 16px; background: #2563eb; color: #ffffff; border: none; font-size: 12px; font-weight: 600; border-radius: 6px; cursor: pointer; }
.card-action-btn:hover { background: #1d4ed8; }
.list-fade-enter-active, .list-fade-leave-active { transition: all 0.2s ease; }
.list-fade-enter-from, .list-fade-leave-to { opacity: 0; transform: translateY(10px); }
.empty-jobs-placeholder { background: #ffffff; padding: 50px; border-radius: 14px; border: 1px solid #eef1f6; }
</style>