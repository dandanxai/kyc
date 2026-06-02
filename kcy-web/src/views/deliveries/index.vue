<template>
    <div class="delivery-im-page">
    <div class="im-container">
        
        <!-- ================= 左侧：投递过的简历/岗位列表 ================= -->
        <div class="im-sidebar">
        <div class="sidebar-header">
            <h3>投递动态 ({{ deliveryList.length }})</h3>
            <p>点击卡片直接与招聘官发起沟通</p>
        </div>
        
        <div class="delivery-list-scroll">
            <div 
            v-for="item in deliveryList" 
            :key="item.id"
            :class="['delivery-card', { active: activeDeliveryId === item.id }]"
            @click="switchChat(item.id)"
            >
            <!-- 岗位与薪资 -->
            <div class="card-row">
                <h4 class="job-title">{{ item.jobTitle }}</h4>
                <span class="salary">{{ item.salary }}</span>
            </div>
            <!-- 公司名 -->
            <p class="company-name">{{ item.companyName }}</p>
            <!-- 关联的简历名称 -->
            <p class="resume-ref">📄 投递简历：{{ item.resumeTitle }}</p>
            
            <!-- 底部状态与时间 -->
            <div class="card-footer">
                <span class="time">{{ item.deliveryTime }}</span>
                <el-tag :type="statusTagType(item.status)" size="small" effect="light">
                {{ item.status }}
                </el-tag>
            </div>
            </div>
        </div>
        </div>

        <!-- ================= 右侧：与该岗位的聊天对话框 ================= -->
        <div class="im-main" v-if="currentChat">
        <!-- 聊天框头部：显示HR及岗位信息 -->
        <div class="chat-header">
            <div class="hr-info">
            <img :src="currentChat.hrAvatar" class="hr-avatar" />
            <div class="hr-meta">
                <h4>{{ currentChat.hrName }}</h4>
                <p>{{ currentChat.companyName }} · {{ currentChat.hrRole }}</p>
            </div>
            </div>
            <!-- 快捷去往岗位详情 -->
            <el-button size="small" type="primary" plain @click="toJobDetail(currentChat.jobId)">
            查看岗位详情
            </el-button>
        </div>

        <!-- 聊天内容气泡流 -->
        <div class="chat-body-scroll" ref="chatBodyRef">
            <!-- 顶部投递卡片提示 -->
            <div class="system-tip">
            <span>您于 {{ currentChat.deliveryTime }} 投递了该岗位，HR 正在火速评估中</span>
            </div>

            <div 
            v-for="(msg, index) in currentChat.messages" 
            :key="index"
            :class="['msg-wrapper', msg.sender === 'me' ? 'msg-me' : 'msg-other']"
            >
            <img :src="msg.sender === 'me' ? myAvatar : currentChat.hrAvatar" class="chat-user-avatar" />
            <div class="msg-bubble">
                <p class="msg-text">{{ msg.text }}</p>
                <span class="msg-time">{{ msg.time }}</span>
            </div>
            </div>
        </div>

        <!-- 底部输入框区域 -->
        <div class="chat-footer-input">
            <div class="toolbar">
            <!-- 模拟快捷短语 -->
            <span class="quick-phrase" @click="sendQuickPhrase('您好，请问方便约个面试吗？')">💬 约面试</span>
            <span class="quick-phrase" @click="sendQuickPhrase('这是我的最新全栈项目案例，请您过目。')">📂 发作品</span>
            </div>
            <div class="input-row">
            <el-input
                v-model="inputMsg"
                type="textarea"
                :rows="2"
                placeholder="跟招聘官打个招呼吧，优秀的沟通能带来更多面试机会..."
                @keyup.enter="sendMessage"
            />
            <el-button type="primary" :icon="Promotion" class="send-btn" @click="sendMessage">
                发送
            </el-button>
            </div>
        </div>
        </div>

        <!-- 空状态 -->
        <div class="im-main-empty" v-else>
        <el-empty description="请在左侧选择一份投递记录开始沟通" />
        </div>

    </div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Promotion } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const inputMsg = ref('')
const chatBodyRef = ref<HTMLDivElement | null>(null)

// 我的头像
const myAvatar = 'https://picsum.photos/100/100?random=10'

// 激活的聊天ID（默认选中第一条）
const activeDeliveryId = ref(1)

// 模拟极其详尽的“投递 + 聊天消息联合模型”
const deliveryList = ref([
    {
    id: 1,
    jobId: 101,
    jobTitle: 'Java 全栈开发工程师',
    salary: '8K - 13K',
    companyName: '科大讯飞股份有限公司',
    resumeTitle: '黄胜-Java全栈简历.pdf',
    deliveryTime: '2026-06-01 14:30',
    status: '约面试',
    hrName: '王经理',
    hrRole: '高级技术初筛官',
    hrAvatar: 'https://picsum.photos/100/100?random=50',
    messages: [
        { sender: 'other', text: '您好黄胜，看到您独立开发过UniApp跨端旅游小程序和Spring Boot物联网大屏，感觉跟我们目前的岗位契合度挺高。', time: '14:35' },
        { sender: 'me', text: '王经理您好！是的，我对Java后端持久层重构和前端ECharts、Vue3组件封装都比较熟悉。', time: '14:38' },
        { sender: 'other', text: '很好，这周五下午3点方便组织一轮线上视频面试吗？', time: '14:40' }
    ]
    },
    {
    id: 2,
    jobId: 102,
    jobTitle: 'Vue3 前端研发工程师',
    salary: '7K - 11K',
    companyName: '极客互娱科技有限公司',
    resumeTitle: '黄胜-前端高级开发简历.pdf',
    deliveryTime: '2026-05-30 10:15',
    status: '被查看',
    hrName: '刘女士',
    hrRole: 'HRBP 负责人',
    hrAvatar: 'https://picsum.photos/100/100?random=51',
    messages: [
        { sender: 'me', text: '您好，非常期待加入贵公司，请问我的前端简历通过初筛了吗？', time: '10:16' },
        { sender: 'other', text: '您好，简历已经转发给前端技术主管评估了，有后续进展我会第一时间在这个聊天框内通知您。', time: '11:00' }
    ]
    },
    {
    id: 3,
    jobId: 103,
    jobTitle: 'Python 自动化脚本助理',
    salary: '6K - 8K',
    companyName: '智脑网络矩阵',
    resumeTitle: '黄胜-Java全栈简历.pdf',
    deliveryTime: '2026-05-20',
    status: '不合适',
    hrName: '张主管',
    hrRole: '团队负责人',
    hrAvatar: 'https://picsum.photos/100/100?random=52',
    messages: [
        { sender: 'other', text: '感谢投递，经过评估您的全栈开发能力很强，但与我们纯粹做自动化爬虫脚本的契合度有些偏差，祝您早日找到心仪的 Offer。', time: '16:00' }
    ]
    }
])

// 获取当前选中的聊天上下文
const currentChat = computed(() => {
    return deliveryList.value.find(item => item.id === activeDeliveryId.value)
})

// 根据投递状态返回对应的 Element Plus Tag 颜色标签
const statusTagType = (status: string) => {
    switch (status) {
    case '约面试': return 'success'
    case '被查看': return 'warning'
    case '已投递': return 'info'
    case '不合适': return 'danger'
    default: return ''
    }
}

// 切换聊天事件
const switchChat = (id: number) => {
    activeDeliveryId.value = id
    scrollToBottom()
}

// 丝滑滚动到聊天最底部
const scrollToBottom = () => {
    nextTick(() => {
    if (chatBodyRef.value) {
        chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight
    }
    })
}

// 发送消息
const sendMessage = () => {
    if (!inputMsg.value.trim()) return
    
    if (currentChat.value) {
    currentChat.value.messages.push({
        sender: 'me',
        text: inputMsg.value,
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    })
    inputMsg.value = ''
    scrollToBottom()

    // 模拟 1.5 秒后 HR 智能自动回复（增强互动感）
    setTimeout(() => {
        currentChat.value?.messages.push({
        sender: 'other',
        text: '收到您的回复，我会立刻记录并跟进。',
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        })
        scrollToBottom()
    }, 1500)
    }
}

// 点击常用快捷语发送
const sendQuickPhrase = (phrase: string) => {
    inputMsg.value = phrase
    sendMessage()
}

// 跳转岗位详情
const toJobDetail = (jobId: number) => {
    router.push(`/jobs/${jobId}`)
}
</script>

<style scoped>
.delivery-im-page { max-width: 1200px; margin: 88px auto 40px; padding: 0 20px; }

/* IM 容器核心：采用弹性视口等高设计，彻底杜绝外层页面滚动错位 */
.im-container { display: flex; background: #fff; border-radius: 12px; border: 1px solid #eef0f5; height: 680px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.02); }

/* ================= 左侧列表样式 ================= */
.im-sidebar { width: 340px; border-right: 1px solid #eef0f5; display: flex; flex-direction: column; background: #fafbfc; }
.sidebar-header { padding: 20px; border-bottom: 1px solid #eef0f5; background: #fff; }
.sidebar-header h3 { margin: 0 0 4px; font-size: 16px; color: #1e293b; }
.sidebar-header p { margin: 0; font-size: 12px; color: #94a3b8; }
.delivery-list-scroll { flex: 1; overflow-y: auto; padding: 12px; display: flex; flex-direction: column; gap: 10px; }

/* 投递卡片微调 */
.delivery-card { background: #fff; border: 1px solid #eef0f5; padding: 14px; border-radius: 8px; cursor: pointer; transition: 0.2s; }
.delivery-card:hover { border-color: #2563eb; transform: translateY(-1px); }
.delivery-card.active { border-color: #2563eb; background: #eff6ff; box-shadow: 0 4px 12px rgba(37,99,235,0.04); }
.card-row { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 4px; gap: 10px; }
.job-title { margin: 0; font-size: 14px; color: #1e293b; line-height: 1.4; }
.delivery-card.active .job-title { color: #2563eb; }
.salary { font-size: 13px; color: #f43f5e; font-weight: 600; white-space: nowrap; }
.company-name { margin: 0 0 8px; font-size: 12px; color: #64748b; }
.resume-ref { margin: 0 0 10px; font-size: 11px; color: #94a3b8; background: #f8fafc; padding: 3px 6px; border-radius: 4px; display: inline-block; }
.card-footer { display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #fcfdfe; padding-top: 8px; }
.time { font-size: 11px; color: #94a3b8; }

/* ================= 右侧聊天区域样式 ================= */
.im-main { flex: 1; display: flex; flex-direction: column; background: #fff; }
.chat-header { padding: 16px 24px; border-bottom: 1px solid #eef0f5; display: flex; justify-content: space-between; align-items: center; background: #fff; }
.hr-info { display: flex; align-items: center; gap: 12px; }
.hr-avatar { width: 42px; height: 42px; border-radius: 50%; object-fit: cover; }
.hr-meta h4 { margin: 0 0 2px; font-size: 15px; color: #1e293b; }
.hr-meta p { margin: 0; font-size: 12px; color: #64748b; }

/* 聊天气泡滚动层 */
.chat-body-scroll { flex: 1; overflow-y: auto; padding: 24px; background: #f8fafc; display: flex; flex-direction: column; gap: 20px; }
.system-tip { text-align: center; margin: 10px 0; }
.system-tip span { font-size: 11px; background: #e2e8f0; color: #64748b; padding: 4px 12px; border-radius: 20px; }

/* 气泡流差异化架构（核心） */
.msg-wrapper { display: flex; gap: 12px; max-width: 75%; }
.chat-user-avatar { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; flex-shrink: 0; }
.msg-bubble { padding: 10px 14px; border-radius: 10px; position: relative; display: flex; flex-direction: column; gap: 4px; }
.msg-text { margin: 0; font-size: 13px; line-height: 1.5; word-break: break-all; }
.msg-time { font-size: 10px; color: #94a3b8; align-self: flex-end; }

/* 他人发送的（居左，白色底） */
.msg-other { align-self: flex-start; }
.msg-other .msg-bubble { background: #fff; border: 1px solid #eef0f5; border-top-left-radius: 2px; }

/* 我发送的（居右，蓝色底，反转轴向） */
.msg-me { align-self: flex-end; flex-direction: row-reverse; }
.msg-me .msg-bubble { background: #2563eb; color: #fff; border-top-right-radius: 2px; }
.msg-me .msg-time { color: rgba(255,255,255,0.7); }

/* 底部输入框区 */
.chat-footer-input { border-top: 1px solid #eef0f5; padding: 16px 24px; background: #fff; }
.toolbar { display: flex; gap: 10px; margin-bottom: 10px; }
.quick-phrase { font-size: 12px; color: #2563eb; background: #eff6ff; padding: 3px 10px; border-radius: 4px; cursor: pointer; transition: 0.2s; }
.quick-phrase:hover { background: #2563eb; color: #fff; }
.input-row { display: flex; gap: 14px; align-items: flex-end; }
.send-btn { height: 54px; padding: 0 20px; }

/* 空状态 */
.im-main-empty { flex: 1; display: flex; align-items: center; justify-content: center; background: #fafbfc; }
</style>