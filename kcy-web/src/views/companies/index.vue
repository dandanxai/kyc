<template>
    <div class="comp-list-page">
    <div class="comp-filter-panel">
        <div class="filter-row">
        <label>公司地点：</label>
        <div class="filter-tags">
            <span class="tag" :class="{ active: activeFilters.city === '全国' }" @click="activeFilters.city = '全国'">全国</span>
            <span class="tag more-city" @click="showCityDialog = true">
            {{ activeFilters.city !== '全国' && !cities.includes(activeFilters.city) ? activeFilters.city : '全部城市' }}
            <el-icon><ArrowDown /></el-icon>
            </span>
        </div>
        </div>

        <div class="filter-row">
        <label>行业类型：</label>
        <div class="filter-tags">
            <span v-for="ind in industries" :key="ind" 
                :class="['tag', { active: activeFilters.industry === ind }]"
                @click="activeFilters.industry = ind">{{ ind }}</span>
        </div>
        </div>

        <div class="filter-row">
        <label>融资阶段：</label>
        <div class="filter-tags">
            <span v-for="stage in stages" :key="stage" 
                :class="['tag', { active: activeFilters.stage === stage }]"
                @click="activeFilters.stage = stage">{{ stage }}</span>
        </div>
        </div>

        <div class="filter-row">
        <label>公司规模：</label>
        <div class="filter-tags">
            <span v-for="scale in scales" :key="scale" 
                :class="['tag', { active: activeFilters.scale === scale }]"
                @click="activeFilters.scale = scale">{{ scale }}</span>
        </div>
        </div>

        <div class="filter-actions">
        <el-button link type="primary" @click="resetFilters">一键清空筛选</el-button>
        </div>
    </div>

    <div class="comp-grid">
        <div class="comp-card" v-for="comp in filteredCompanies" :key="comp.id" @click="toDetail(comp.id)">
        <div class="card-header">
            <img :src="comp.logo" class="logo" />
            <div class="info">
            <h4>{{ comp.name }}</h4>
            <p>{{ comp.industry }} · {{ comp.stage }} · {{ comp.scale }}</p>
            </div>
        </div>
        <div class="card-footer">
            <span class="job-link">在招职位 {{ comp.jobCount }} 个</span>
            <el-button size="small" type="primary" link>查看详情</el-button>
        </div>
        </div>
    </div>

    <el-dialog v-model="showCityDialog" title="选择城市" width="600px" align-center>
        <div class="city-selector">
        <span v-for="city in cityList" :key="city" 
                class="city-item" 
                :class="{ active: activeFilters.city === city }"
                @click="selectCity(city)">
            {{ city }}
        </span>
        </div>
    </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const showCityDialog = ref(false)

// 基础数据
const cities = ['滁州', '全国', '全部城市']
const cityList = ['滁州', '北京', '上海', '广州', '深圳', '杭州', '天津', '西安', '苏州', '武汉', '厦门', '长沙', '成都', '郑州', '重庆']
const industries = ['不限', '电子商务', '游戏', '社交网络与媒体', '广告营销', '大数据', '医疗健康', '生活服务(O2O)', 'O2O', '旅游', '分类信息', '音乐/视频/阅读', '在线教育', '社交网络', '人力资源服务', '企业服务', '信息安全', '智能硬件', '移动互联网', '互联网', '计算机软件', '通信/网络设备', '广告/公关/会展', '互联网金融', '物流/仓储', '进出口贸易', '咨询', '工程施工', '汽车研发/制造', '其他行业']
const stages = ['不限', '未融资', '天使轮', 'A轮', 'B轮', 'C轮', 'D轮及以上', '已上市', '不需要融资']
const scales = ['不限', '0-20人', '20-99人', '100-499人', '500-999人', '1000-9999人', '10000人以上']

const activeFilters = reactive({ city: '全国', industry: '不限', stage: '不限', scale: '不限' })

const companies = ref([
    { id: 1, name: '字节跳动', logo: 'https://picsum.photos/100/100?random=1', industry: '互联网', stage: '已上市', scale: '10000人以上', jobCount: 120 },
    { id: 2, name: '极客互娱', logo: 'https://picsum.photos/100/100?random=2', industry: '游戏', stage: 'B轮', scale: '100-499人', jobCount: 15 }
])

// 过滤逻辑
const filteredCompanies = computed(() => {
    return companies.value.filter(c => {
    // 这里可根据 activeFilters 扩展过滤逻辑
    return true 
    })
})

const selectCity = (city: string) => {
    activeFilters.city = city
    showCityDialog.value = false
}

const resetFilters = () => {
    activeFilters.city = '全国'
    activeFilters.industry = '不限'
    activeFilters.stage = '不限'
    activeFilters.scale = '不限'
}

const toDetail = (id: number) => router.push(`/companies/${id}`)
</script>

<style scoped>
.comp-list-page { max-width: 1200px; margin: 88px auto 40px; padding: 0 20px; }
.comp-filter-panel { background: #fff; padding: 20px; border-radius: 12px; margin-bottom: 20px; border: 1px solid #eef0f5; }
.filter-row { display: flex; margin-bottom: 15px; }
.filter-row label { width: 90px; color: #64748b; font-size: 14px; font-weight: 600; padding-top: 5px; }
.filter-tags { display: flex; flex-wrap: wrap; gap: 10px; flex: 1; }
.tag { padding: 5px 12px; border-radius: 6px; cursor: pointer; font-size: 13px; color: #475569; transition: 0.2s; }
.tag:hover { color: #2563eb; }
.tag.active { background: #eff6ff; color: #2563eb; font-weight: 600; }
.more-city { border: 1px dashed #cbd5e1; }
.filter-actions { margin-top: 10px; padding-top: 10px; border-top: 1px dashed #e2e8f0; }

.comp-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.comp-card { background: #fff; padding: 20px; border-radius: 12px; border: 1px solid #eef0f5; transition: 0.3s; cursor: pointer; }
.comp-card:hover { border-color: #2563eb; box-shadow: 0 8px 16px rgba(37,99,235,0.1); }
.card-header { display: flex; gap: 12px; margin-bottom: 20px; }
.logo { width: 50px; height: 50px; border-radius: 8px; object-fit: cover; }
.info h4 { margin: 0 0 5px; font-size: 15px; }
.info p { margin: 0; font-size: 12px; color: #94a3b8; }
.card-footer { display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #f8fafc; padding-top: 15px; font-size: 13px; }
.job-link { color: #2563eb; }

.city-selector { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; }
.city-item { padding: 10px; border: 1px solid #eef0f5; text-align: center; cursor: pointer; border-radius: 6px; font-size: 13px; }
.city-item:hover, .city-item.active { border-color: #2563eb; color: #2563eb; background: #f0f7ff; }
</style>