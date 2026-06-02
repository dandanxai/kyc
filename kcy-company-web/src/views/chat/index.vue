<template>
  <div class="chat-container">
    <!-- 1. 左侧：高密联系人通讯录列表 -->
    <div class="chat-sidebar">
      <div class="sidebar-search">
        <el-input
          v-model="searchUser"
          placeholder="搜索候选人 / 沟通职位"
          :prefix-icon="Search"
          size="default"
          clearable
        />
      </div>
      
      <div class="contact-list-scroller">
        <div 
          v-for="user in filteredContacts" 
          :key="user.id" 
          class="contact-item"
          :class="{ 'active-contact': activeUserId === user.id }"
          @click="switchActiveUser(user)"
        >
          <div class="avatar-wrapper">
            <el-avatar :size="40" :src="user.avatar" />
            <span v-if="user.unread > 0" class="unread-badge">{{ user.unread }}</span>
          </div>
          <div class="item-meta">
            <div class="meta-top">
              <span class="user-name">{{ user.name }}</span>
              <span class="msg-time">{{ user.time }}</span>
            </div>
            <div class="meta-job">{{ user.targetJob }}</div>
            <div class="meta-last-msg">{{ user.lastMsg }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 2. 中部：沉浸式核心聊天视口 -->
    <div class="chat-main-window">
      <!-- 视口头部：当前沟通对象身份 -->
      <div class="chat-window-header">
        <div class="active-user-info">
          <span class="active-name">{{ activeUser.name }}</span>
          <el-tag size="small" type="success" effect="plain">沟通中</el-tag>
          <span class="active-job">意向岗位：{{ activeUser.targetJob }}</span>
        </div>
        <div class="header-actions">
          <el-button size="small" type="primary" plain :icon="Phone">发起约聊</el-button>
        </div>
      </div>

      <!-- 核心：聊天消息流垂直滚动滚动舱 -->
      <div class="chat-messages-box" ref="messageBoxRef">
        <div 
          v-for="(msg, idx) in currentMessageList" 
          :key="idx" 
          class="message-wrapper"
          :class="msg.sender === 'hr' ? 'msg-right' : 'msg-left'"
        >
          <!-- 消息实体架构 -->
          <div class="message-body">
            <!-- 场景 A：标准文本/聊天气泡 -->
            <div v-if="msg.type === 'text'" class="bubble-text">
              {{ msg.content }}
            </div>

            <!-- 场景 B：特制高价值业务卡片（如：面试邀请卡、投递职位卡） -->
            <div v-else-if="msg.type === 'card'" class="bubble-card">
              <div class="card-title">📌 {{ msg.cardData.title }}</div>
              <div class="card-detail">
                <p><strong>面试时间：</strong>{{ msg.cardData.time }}</p>
                <p><strong>面试形式：</strong>{{ msg.cardData.type }}</p>
                <p><strong>地点/链接：</strong>{{ msg.cardData.address }}</p>
              </div>
              <div class="card-footer-status">
                <span class="status-lbl">系统状态：</span>
                <el-tag size="small" type="warning" effect="dark">等待候选人接受</el-tag>
              </div>
            </div>

            <span class="msg-stamp">{{ msg.time }}</span>
          </div>
        </div>
      </div>

      <!-- 视口尾部：多功能野蛮输入流 -->
      <div class="chat-input-area">
        <!-- 快捷工具栏 -->
        <div class="input-toolbar">
          <el-tooltip content="发送常用语" placement="top">
            <el-button link :icon="ChatDotSquare" @click="sendQuickPhrase" />
          </el-tooltip>
          <el-tooltip content="发送面试邀请卡" placement="top">
            <el-button link :icon="Calendar" @click="sendInterviewCard" />
          </el-tooltip>
          <el-tooltip content="附件传递" placement="top">
            <el-button link :icon="Paperclip" />
          </el-tooltip>
        </div>
        
        <!-- 输入文本框 -->
        <div class="input-box-wrapper">
          <el-input
            v-model="inputText"
            type="textarea"
            :rows="3"
            placeholder="野蛮生长，在线开撩！按下 Ctrl + Enter 快捷发送消息..."
            resize="none"
            @keydown.ctrl.enter="sendMessage"
          />
          <div class="send-btn-row">
            <span class="tip-text">Ctrl + Enter 发送</span>
            <el-button type="primary" size="default" :disabled="!inputText.trim()" @click="sendMessage">
              发送消息
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 3. 右侧：候选人全景动态极速画像栏 -->
    <div class="chat-right-panel">
      <div class="panel-section user-profile-center">
        <el-avatar :size="64" :src="activeUser.avatar" />
        <h3 class="p-name">{{ activeUser.name }}</h3>
        <p class="p-sub">{{ activeUser.gender }} ｜ {{ activeUser.age }}岁 ｜ {{ activeUser.city }}</p>
        <div class="p-tags">
          <el-tag size="small" effect="plain">Java</el-tag>
          <el-tag size="small" effect="plain" type="success">Vue3</el-tag>
          <el-tag size="small" effect="plain" type="warning">UniApp</el-tag>
        </div>
      </div>

      <el-divider style="margin: 12px 0" />

      <!-- 精简版资历看板 -->
      <div class="panel-section profile-details">
        <h4 class="section-title">教育与履历</h4>
        <div class="detail-item">
          <span class="lbl">毕业院校</span>
          <span class="val">{{ activeUser.school || '安徽大学' }} ({{ activeUser.edu || '本科' }})</span>
        </div>
        <div class="detail-item">
          <span class="lbl">工作资历</span>
          <span class="val">{{ activeUser.expYear || '2年经验' }}</span>
        </div>
      </div>

      <!-- 快捷业务处理闭环 -->
      <div class="panel-section quick-actions">
        <h4 class="section-title">人才快速流转</h4>
        <div class="action-buttons-grid">
          <el-button type="success" :icon="Check" plain @click="executeQuickAction('advance')">直接晋级</el-button>
          <el-button type="danger" :icon="Close" plain @click="executeQuickAction('reject')">不合适</el-button>
        </div>
        <el-button type="primary" style="width: 100%; margin-top: 12px;" :icon="Document">
          调阅完整电子简历底册
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted } from 'vue'
import { Search, Phone, ChatDotSquare, Calendar, Paperclip, Check, Close, Document } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchUser = ref('')
const inputText = ref('')
const activeUserId = ref(9001)
const messageBoxRef = ref<HTMLElement | null>(null)

// 模拟极其庞大标准的动态高真联系人列表
const contacts = ref([
  {
    id: 9001,
    name: '黄胜',
    avatar: 'https://picsum.photos/60/60?random=11',
    targetJob: 'Java 全栈开发工程师',
    gender: '男',
    age: 21,
    city: '合肥',
    school: '滁州职业技术学院',
    edu: '大专',
    expYear: '应届生（Java全栈/跨端）',
    lastMsg: '好的，期待跟您的面试！',
    time: '14:32',
    unread: 2
  },
  {
    id: 9002,
    name: '王小丫',
    avatar: 'https://picsum.photos/60/60?random=12',
    targetJob: 'Vue3 前端研发工程师',
    gender: '女',
    age: 24,
    city: '合肥',
    school: '安徽大学',
    edu: '本科',
    expYear: '2年经验',
    lastMsg: '请问咱们公司前端团队目前有多少人呢？',
    time: '11:15',
    unread: 0
  },
  {
    id: 9003,
    name: '张铁柱',
    avatar: 'https://picsum.photos/60/60?random=13',
    targetJob: '架构师/高级 Java 开发',
    gender: '男',
    age: 28,
    city: '上海/合肥',
    school: '中国科学技术大学',
    edu: '研究生',
    expYear: '5年经验',
    lastMsg: '薪资架构里包含年终绩效和项目奖金吗？',
    time: '昨天',
    unread: 0
  }
])

// 深度结构化隔离的消息流动池（按联系人 ID 隔离映射）
const messagesMap = ref<Record<number, any[]>>({
  9001: [
    { sender: 'candidate', type: 'text', content: '您好，我已经关注贵公司很久了，对 Java 全栈及跨端混合开发岗位很感兴趣！', time: '14:20' },
    { sender: 'hr', type: 'text', content: '同学你好！看到你获得了中国软件杯和金砖国家技能大赛的一等奖，底子非常棒。我们项目刚好有很多跨端和本地 AI 集成需求。', time: '14:25' },
    { sender: 'hr', type: 'card', content: '', time: '14:26', cardData: { title: '研发二组·初试技术面邀请卡', time: '明天下午 15:30', type: '线上视频面试 (腾讯会议)', address: 'https://meeting.tencent.com/dm/xxxx' } },
    { sender: 'candidate', type: 'text', content: '好的，期待跟您的面试！', time: '14:32' }
  ],
  9002: [
    { sender: 'candidate', type: 'text', content: '您好，我精通 Vue3、ECharts 大屏开发，可以看看我的作品集。', time: '11:10' },
    { sender: 'hr', type: 'text', content: '收到，作品集很不错，等下帮您递交到业务技术主管那边交叉评估。', time: '11:12' },
    { sender: 'candidate', type: 'text', content: '请问咱们公司前端团队目前有多少人呢？', time: '11:15' }
  ],
  9003: [
    { sender: 'hr', type: 'text', content: '张先生您好，我们合肥研发中心目前在大规模扩充高并发中间件方向的核心骨干，看了您的中科大背景非常匹配。', time: '昨天 16:00' },
    { sender: 'candidate', type: 'text', content: '薪资架构里包含年终绩效和项目奖金吗？', time: '昨天 16:45' }
  ]
})

// 计算属性：当前激活的唯一用户及对应消息流
const activeUser = computed(() => {
  return contacts.value.find(c => c.id === activeUserId.value) || contacts.value[0]
})

const currentMessageList = computed(() => {
  return messagesMap.value[activeUserId.value] || []
})

const filteredContacts = computed(() => {
  return contacts.value.filter(c => c.name.includes(searchUser.value) || c.targetJob.includes(searchUser.value))
})

// 自动触底滚动机制（高频极速打字体验）
const scrollToBottom = () => {
  nextTick(() => {
    if (messageBoxRef.value) {
      messageBoxRef.value.scrollTop = messageBoxRef.value.scrollHeight
    }
  })
}

// 切换当前聊天人并清空未读气泡
const switchActiveUser = (user: any) => {
  activeUserId.value = user.id
  user.unread = 0
  scrollToBottom()
}

// 野蛮核心函数：按下 Ctrl+Enter 或点击按钮瞬间追加消息
const sendMessage = () => {
  if (!inputText.value.trim()) return
  
  // 1. 压入对应的消息数组
  currentMessageList.value.push({
    sender: 'hr',
    type: 'text',
    content: inputText.value,
    time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  })

  // 2. 同步刷新左侧侧边栏的最末动态
  activeUser.value.lastMsg = inputText.value
  activeUser.value.time = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })

  inputText.value = ''
  scrollToBottom()

  // 模拟机器人野蛮秒回机制，产生极度舒适的互动感
  setTimeout(() => {
    currentMessageList.value.push({
      sender: 'candidate',
      type: 'text',
      content: `🔥 [AI 自动模拟候选人动态回复]：已收到您的同步信息，我会保持跟进！`,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    })
    activeUser.value.lastMsg = '🔥 [AI 自动模拟候选人动态回复]...'
    scrollToBottom()
  }, 1200)
}

// 发送常用快捷短语
const sendQuickPhrase = () => {
  inputText.value = '同学你好，你的简历和背景我们团队非常看好，请问这周内什么时间方便约一轮线上的初试技术面对聊？'
}

// 模拟发送定制化的结构化面试邀请卡片
const sendInterviewCard = () => {
  currentMessageList.value.push({
    sender: 'hr',
    type: 'card',
    content: '',
    time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    cardData: {
      title: '极客互娱·复试总监面邀请函',
      time: '本周五上午 10:00',
      type: '线下园区面对面沟通',
      address: '高新区创新产业园二期G3栋大堂'
    }
  })
  scrollToBottom()
  ElMessage.success('已向候选人成功追加投递高维结构化面试邀请卡')
}

// 右侧极速看板快捷流转
const executeQuickAction = (action: 'advance' | 'reject') => {
  if (action === 'advance') {
    ElMessage.success(`快捷流转：候选人【${activeUser.value.name}】已被直接越级推送至下一轮面试池！`)
  } else {
    ElMessageBox.confirm(`确定要在聊天中将候选人【${activeUser.value.name}】做不合适挂起吗？`, '淘汰挂起', {
      type: 'warning'
    }).then(() => {
      ElMessage.warning('已将当前沟通标识为不合适')
    }).catch(() => {})
  }
}

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
.chat-container {
  display: flex;
  height: calc(100vh - 64px);
  background-color: #f1f5f9;
  overflow: hidden;
}

/* ================= 左侧通讯录大盘 ================= */
.chat-sidebar {
  width: 320px;
  background-color: #ffffff;
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
}
.sidebar-search {
  padding: 16px;
  border-bottom: 1px solid #f1f5f9;
}
.contact-list-scroller {
  flex: 1;
  overflow-y: auto;
}
.contact-item {
  display: flex;
  padding: 14px 16px;
  gap: 12px;
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 1px solid #f8fafc;
}
.contact-item:hover {
  background-color: #f8fafc;
}
.active-contact {
  background-color: #eff6ff !important;
}
.avatar-wrapper {
  position: relative;
}
.unread-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background-color: #ef4444;
  color: #fff;
  font-size: 10px;
  border-radius: 10px;
  padding: 2px 6px;
  font-weight: 700;
  transform: scale(0.9);
}
.item-meta {
  flex: 1;
  overflow: hidden;
}
.meta-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.user-name {
  font-weight: 600;
  color: #1e293b;
  font-size: 14px;
}
.msg-time {
  font-size: 11px;
  color: #94a3b8;
}
.meta-job {
  font-size: 11px;
  color: #2563eb;
  margin-top: 2px;
  font-weight: 500;
}
.meta-last-msg {
  font-size: 12px;
  color: #64748b;
  margin-top: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ================= 中部核心消息舱 ================= */
.chat-main-window {
  flex: 1;
  background-color: #f8fafc;
  display: flex;
  flex-direction: column;
}
.chat-window-header {
  height: 64px;
  background: #ffffff;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}
.active-user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}
.active-name {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
}
.active-job {
  font-size: 13px;
  color: #64748b;
  margin-left: 12px;
}

/* 核心滚动流 */
.chat-messages-box {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.message-wrapper {
  display: flex;
  max-width: 75%;
}
.msg-left {
  align-self: flex-start;
}
.msg-right {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-body {
  display: flex;
  flex-direction: column;
  position: relative;
}
.msg-left .message-body { align-items: flex-start; }
.msg-right .message-body { align-items: flex-end; }

/* 气泡设计 */
.bubble-text {
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-all;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}
.msg-left .bubble-text {
  background-color: #ffffff;
  color: #1e293b;
  border-top-left-radius: 2px;
  border: 1px solid #e2e8f0;
}
.msg-right .bubble-text {
  background-color: #2563eb;
  color: #ffffff;
  border-top-right-radius: 2px;
}

/* 业务卡片专属造型 */
.bubble-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 14px;
  width: 320px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}
.card-title {
  font-weight: 700;
  color: #1e293b;
  font-size: 14px;
  border-bottom: 1px solid #f1f5f9;
  padding-bottom: 8px;
  margin-bottom: 8px;
}
.card-detail p {
  font-size: 12px;
  color: #475569;
  margin: 4px 0;
}
.card-footer-status {
  margin-top: 10px;
  padding-top: 8px;
  border-top: 1px dashed #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 11px;
}
.status-lbl { color: #94a3b8; }

.msg-stamp {
  font-size: 10px;
  color: #94a3b8;
  margin-top: 4px;
}

/* 多功能底部输入区 */
.chat-input-area {
  background-color: #ffffff;
  border-top: 1px solid #e2e8f0;
  padding: 8px 16px 16px 16px;
}
.input-toolbar {
  display: flex;
  gap: 12px;
  padding-bottom: 6px;
}
.input-toolbar :deep(.el-button) {
  font-size: 18px;
  color: #64748b;
}
.input-toolbar :deep(.el-button:hover) {
  color: #2563eb;
}
.input-box-wrapper :deep(.el-textarea__inner) {
  border: none !important;
  box-shadow: none !important;
  padding: 0;
  font-size: 14px;
  color: #1e293b;
}
.send-btn-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}
.tip-text {
  font-size: 11px;
  color: #94a3b8;
}

/* ================= 右侧極速画像栏 ================= */
.chat-right-panel {
  width: 280px;
  background-color: #ffffff;
  border-left: 1px solid #e2e8f0;
  padding: 24px 16px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.user-profile-center {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}
.p-name {
  margin: 12px 0 4px 0;
  color: #1e293b;
  font-size: 16px;
}
.p-sub {
  font-size: 12px;
  color: #64748b;
  margin-bottom: 12px;
}
.p-tags {
  display: flex;
  gap: 6px;
}
.section-title {
  font-size: 13px;
  font-weight: 700;
  color: #475569;
  margin-bottom: 12px;
}
.profile-details {
  display: flex;
  flex-direction: column;
}
.detail-item {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  margin-bottom: 8px;
}
.detail-item .lbl { color: #94a3b8; }
.detail-item .val { color: #334155; font-weight: 500; }

.action-buttons-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

/* 美化独立滚动条 */
.contact-list-scroller::-webkit-scrollbar,
.chat-messages-box::-webkit-scrollbar {
  width: 5px;
}
.contact-list-scroller::-webkit-scrollbar-thumb,
.chat-messages-box::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}
</style>