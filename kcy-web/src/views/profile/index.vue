<template>
    <div class="profile-settings-page">
    <div class="settings-layout">
        
        <div class="left-side-pane">
        <div class="avatar-card">
            <div class="avatar-uploader-wrapper">
                <el-avatar :size="100" :src="form.avatar || defaultAvatar" class="profile-avatar" />
                <div class="avatar-mask" @click="triggerAvatarUpload">
                    <el-icon><Camera /></el-icon>
                    <span>更换头像</span>
                </div>
                
                <input 
                    type="file" 
                    ref="avatarFileRef" 
                    style="display: none" 
                    accept="image/*" 
                    @change="handleAvatarFileChange" 
                />
            </div>
            <h3>{{ form.nickname || form.username || '未设置昵称' }}</h3>
            <p class="account-identity">UID: {{ form.id || '暂无ID' }}</p>
        </div>

        <div class="security-action-box">
            <div class="sec-item" @click="openPasswordModal">
            <div class="sec-left">
                <div class="icon-pulse-wrapper purple">
                <el-icon class="sec-icon"><Lock /></el-icon>
                </div>
                <div class="sec-info">
                <h5>账户密码安全</h5>
                <p>建议定期修改密码确保安全</p>
                </div>
            </div>
            <el-icon class="arrow-right"><ArrowRight /></el-icon>
            </div>
        </div>
        </div>

        <div class="main-form-pane">
        <div class="pane-header">
            <div class="header-title-row">
            <h3 class="pane-title">基本档案信息</h3>
            <el-tag type="primary" effect="plain" round size="small">数据双向动态同步</el-tag>
            </div>
            <p class="pane-subtitle">管理您的个人档案，数据将直接同步到后端的 member_user 表</p>
        </div>

        <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="geek-form">
            
            <h4 class="form-section-title"><span class="section-dot blue"></span> 基础识别信息</h4>
            <el-row :gutter="24">
            <el-col :span="12">
                <el-form-item label="用户昵称 (nickname)" prop="nickname">
                <el-input v-model="form.nickname" placeholder="请输入个性用户昵称" clearable />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="真实姓名 (name)" prop="name">
                <el-input v-model="form.name" placeholder="请输入真实姓名（用于简历/实名）" clearable />
                </el-form-item>
            </el-col>
            </el-row>

            <el-row :gutter="24">
            <el-col :span="12">
                <el-form-item label="性别 (sex)" prop="sex">
                <el-radio-group v-model="form.sex" class="geek-radio-group">
                    <el-radio-button :value="1">男生</el-radio-button>
                    <el-radio-button :value="2">女生</el-radio-button>
                    <el-radio-button :value="0">保密</el-radio-button>
                </el-radio-group>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="出生日期 (birthday)" prop="birthday">
                <el-date-picker
                    v-model="form.birthday"
                    type="date"
                    placeholder="选择出生日期"
                    style="width: 100%;"
                    value-format="YYYY-MM-DD HH:mm:ss"
                />
                </el-form-item>
            </el-col>
            </el-row>

            <h4 class="form-section-title"><span class="section-dot green"></span> 联系方式绑定</h4>
            <el-row :gutter="24">
            <el-col :span="12">
                <el-form-item label="手机号码 (mobile)" prop="mobile">
                <el-input v-model="form.mobile" disabled placeholder="暂无绑定手机号">
                    <template #append>
                    <span class="disabled-append-btn" @click="handleModifyContact('手机号')">更换绑定</span>
                    </template>
                </el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="电子邮箱 (email)" prop="email">
                <el-input v-model="form.email" placeholder="请输入您的电子邮箱" clearable />
                </el-form-item>
            </el-col>
            </el-row>

            <h4 class="form-section-title"><span class="section-dot orange"></span> 所在常驻地区</h4>
            <el-row :gutter="24">
            <el-col :span="24">
                <el-form-item label="常驻地区 (province / city / county)">
                <el-cascader
                    v-model="selectedRegion"
                    :options="regionData"
                    placeholder="请选择 省 / 市 / 区县"
                    style="width: 100%;"
                    clearable
                    @change="handleRegionChange"
                />
                </el-form-item>
            </el-col>
            </el-row>

            <div class="form-submit-bar">
            <el-button type="primary" size="large" class="save-profile-btn" :loading="isSaving" @click="submitProfile">
                保存更改并同步落库
            </el-button>
            <el-button size="large" class="reset-profile-btn" @click="syncDataFromStore">回滚重置</el-button>
            </div>
        </el-form>
        </div>

    </div>

    <el-dialog v-model="passwordModalVisible" title="安全修改账户密码" width="440px" append-to-body class="geek-dialog">
        <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-position="top">
        <el-form-item label="原密码" prop="oldPassword">
            <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入当前旧密码" />
        </el-form-item>
        <el-form-item label="设置新密码" prop="newPassword">
            <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入复杂新密码 (6-20位)" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码核对" />
        </el-form-item>
        </el-form>
        <template #footer>
        <div class="dialog-footer">
            <el-button @click="passwordModalVisible = false">取消</el-button>
            <el-button type="primary" class="dialog-confirm-btn" :loading="isChangingPwd" @click="submitPasswordChange">确定修改</el-button>
        </div>
        </template>
    </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Camera, Lock, ArrowRight } from '@element-plus/icons-vue'
import { ElMessage, ElLoading, type FormInstance } from 'element-plus'
import { useUserStore } from '@/store/modules/user'

// 🌟【核心修复点一】只导入绝对稳定存在的 regionData 纯数据源，干掉所有不稳定的转换工具导入，从源头解决 SyntaxError 编译挂机
import { regionData } from 'element-china-area-data'
import { UserApi } from '@/api/member'
import { InfraFileApi } from '@/api/file'
const isUploadingAvatar = ref(false)

const userStore = useUserStore()
const formRef = ref<FormInstance>()
const pwdFormRef = ref<FormInstance>()

const avatarFileRef = ref<HTMLInputElement | null>(null)

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const isSaving = ref(false)
const isChangingPwd = ref(false)
const passwordModalVisible = ref(false)

// 1. 本地动态表单副本
const form = reactive({
    id: null as number | string | null,
    username: '',
    mobile: '',
    email: '',
    nickname: '',
    avatar: '',
    name: '',
    sex: 0,
    birthday: '',
    province: '',
    city: '',
    county: ''
})

// 2. 绑定的地区级联数组模型（存储国标 Code 数组）
const selectedRegion = ref<string[]>([])

const rules = reactive({
    nickname: [{ required: true, message: '用户昵称不能为空', trigger: 'blur' }],
    name: [{ required: true, message: '请填写真实姓名，便于建立互信', trigger: 'blur' }]
})

// 前端绑定的表单模型
const pwdForm = reactive({ 
    oldPassword: '', // 传给后端的旧密码
    newPassword: '', // 对应后端的 password
    confirmPassword: '' // 仅前端校验用
})
const pwdRules = reactive({
    oldPassword: [{ required: true, message: '请输入旧密码验证身份', trigger: 'blur' }],
    newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度需在 6 到 20 位之间', trigger: 'blur' }
    ],
    confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
        validator: (rule: any, value: any, callback: any) => {
        if (value !== pwdForm.newPassword) {
            callback(new Error('两次输入的新密码不一致'))
        } else {
            callback()
        }
        },
        trigger: 'blur'
    }
    ]
})

// 🌟【深度解析回显机制】从全局 Store 获取汉字，通过数据树直接逆向查出 Code
const syncDataFromStore = async () => {
    try {
        if (!userStore.userInfo || !userStore.isSetUser) {
            await userStore.setUserInfoAction()
        }
        
        const storeInfo = userStore.userInfo
        console.log(storeInfo);
        
        if (storeInfo) {
            Object.assign(form, storeInfo)
            
            // 🌟 纯前端数据树匹配：将后端的汉字转成对应的国标 Code 赋予级联选择器
            if (form.province && form.city && form.county) {
                findCodeByText(form.province, form.city, form.county)
            } else {
                selectedRegion.value = []
            }
        }
    } catch (error) {
        ElMessage.error('未能成功同步并获取到有效的登录用户上下文')
    }
}

// 🌟【全版本通用】深度遍历搜索器：通过汉字字符串模糊/精准碰撞出 Code 链路进行回显
const findCodeByText = (provinceText: string, cityText: string, countyText: string) => {
    let pCode = '', cCode = '', aCode = ''
    if (!regionData || !Array.isArray(regionData)) return
    
    // 1. 检索省
    const pNode = regionData.find(p => p.label.includes(provinceText) || provinceText.includes(p.label))
    if (pNode) {
        pCode = pNode.value
        // 2. 检索市
        const cNode = pNode.children?.find(c => c.label.includes(cityText) || cityText.includes(c.label))
        if (cNode) {
            cCode = cNode.value
            // 3. 检索区县
            const aNode = cNode.children?.find(a => a.label.includes(countyText) || countyText.includes(a.label))
            if (aNode) aCode = aNode.value
        }
    }
    
    if (pCode && cCode && aCode) {
        selectedRegion.value = [pCode, cCode, aCode]
    } else {
        selectedRegion.value = []
    }
}

// 🌟【全版本通用变动拦截】级联改变时，直接从被选中的国标 Code 链路节点中拉取 label 汉字存入 form
const handleRegionChange = (value: any) => {
    if (value && value.length === 3) {
        let pText = '', cText = '', aText = ''
        
        const pNode = regionData.find(p => p.value === value[0])
        if (pNode) {
            pText = pNode.label
            const cNode = pNode.children?.find(c => c.value === value[1])
            if (cNode) {
                cText = cNode.label
                const aNode = cNode.children?.find(a => a.value === value[2])
                if (aNode) aText = aNode.label
            }
        }
        
        form.province = pText
        form.city = cText
        form.county = aText
    } else {
        form.province = ''
        form.city = ''
        form.county = ''
    }
}

// 1. 点击“更换头像”遮罩层，暗中拉起 input 选择文件
const triggerAvatarUpload = () => {
    if (avatarFileRef.value) {
        avatarFileRef.value.click()
    }
}

// 参照实时材料上传逻辑完善的头像上传函数
const handleAvatarFileChange = async (event: Event) => {
    const target = event.target as HTMLInputElement
    if (!target.files || target.files.length === 0) return
    
    // 1. 获取原生的 File 对象
    const file = target.files[0]
    
    // 限制图片大小
    if (file.size / 1024 / 1024 > 2) {
        ElMessage.error('头像图片大小不能超过 2MB!')
        return
    }

    // 开启头像组件的局部 Loading 遮罩
    isUploadingAvatar.value = true

    try {
        console.log('正在开始通过封装好的 uploadFile 接口上传头像...')
        
        // 2. 参照你提供的规范，直接传入原生 file 对象调用上传接口
        const res = await InfraFileApi.uploadFile(file)
        
        // 3. 严格参照材料上传的 res.code === 0 && res.data 结构进行校验
        if (res && res.code === 0 && res.data) {
            // 在芋道底层，res.data 如果直接是图片 URL 字符串则直接赋值；如果是对象则取 res.data.url
            const imageUrl = res.data.url || res.data
            
            // 4. 同步更新表单的物理模型与预览状态
            form.avatar = imageUrl
            
            // 5. 联动修改全局 Pinia 内存状态，让顶部导航栏等挂件实时刷新
            if (userStore.userInfo) {
                userStore.userInfo.avatar = imageUrl
            }

            // 6. 真正调用修改头像的 API 写入后端 member_user 表进行持久化
            await UserApi.updateUserAvatar({ avatar: imageUrl })

            ElMessage.success('头像更换成功！')
        } else {
            ElMessage.error('头像上传失败，未获取到合法数据')
        }
    } catch (error) {
        console.error('更换头像异常:', error)
        ElMessage.error('头像上传或落库失败，请检查后端网络')
    } finally {
        // 关闭局部 Loading，并清空 input 的值防止连续选同一张图不触发 change
        isUploadingAvatar.value = false
        if (target) target.value = ''
    }
}

const handleModifyContact = (type: string) => {
    ElMessage.warning(`正在拉起【${type}】安全风控验证流...`)
}

const openPasswordModal = () => {
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
    passwordModalVisible.value = true
}

const submitPasswordChange = async () => {
    if (!pwdFormRef.value) return
    await pwdFormRef.value.validate(async (valid) => {
        if (valid) {
            // 前端先做一次两遍密码一样的初筛
            if (pwdForm.oldPassword === pwdForm.newPassword) {
                ElMessage.error('新密码不能与旧密码相同')
                return
            }
            
            isChangingPwd.value = true
            try {
                // 🌟 调用包装好的 Axios 接口，映射 VO 字段扔给后端 Java
                await UserApi.updateUserPassword({
                    oldPassword: pwdForm.oldPassword,
                    password: pwdForm.newPassword // 对应后端的 password 属性
                })
                
                ElMessage.success('账户密码修改成功！')
                passwordModalVisible.value = false
            } catch (e: any) {
                // 后端抛出的 “旧密码不对” 会在这里被统一拦截并提示
                console.error(e)
            } finally {
                isChangingPwd.value = false
            }
        }
    })
}

// 保存资料到后端并将状态同步写回全局 Store
const submitProfile = async () => {
    if (!formRef.value) return
    
    await formRef.value.validate(async (valid) => {
        if (valid) {
            isSaving.value = true
            try {
                // 1. 精确构建符合后端 AppMemberUserUpdateReqVO 的有效载荷
                const updatePayload = {
                    nickname: form.nickname,
                    // avatar: form.avatar,
                    name: form.name,
                    sex: form.sex,
                    birthday: form.birthday,
                    email: form.email,
                    province: form.province,
                    city: form.city,
                    county: form.county
                }
                
                console.log('正在向后端发送基本信息更新请求，载荷为:', updatePayload)
                
                // 2. 真正调用 Axios 封装的 Api 接口发送 PUT 请求
                await UserApi.updateUser(updatePayload)
                
                // 3. 接口调用成功后，反向同步写入全局 Pinia 内存中，保持页面全局数据一致
                if (userStore.userInfo) {
                    userStore.userInfo = { 
                        ...userStore.userInfo, 
                        ...updatePayload // 覆盖更新被修改的字段
                    }
                }

                ElMessage.success('个人档案保存成功，全局状态已完美同步！')
            } catch (error) {
                // 请求失败时，Axios 拦截器若没有统一抛出，此处做兜底提示
                console.error('修改基本信息失败:', error)
            } finally {
                isSaving.value = false
            }
        } else {
            ElMessage.error('表单核验失败，请检查必填字段')
        }
    })
}

onMounted(() => {
    syncDataFromStore()
})
</script>

<style scoped>
/* 精致 UI 样式保持完全一致 */
.profile-settings-page { max-width: 1140px; margin: 100px auto; padding: 0 24px; font-family: system-ui, -apple-system, sans-serif; }
.settings-layout { display: grid; grid-template-columns: 300px 1fr; gap: 28px; align-items: start; }

.left-side-pane { display: flex; flex-direction: column; gap: 24px; }
.avatar-card { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 16px; padding: 36px 24px; text-align: center; box-shadow: 0 4px 20px rgba(148, 163, 184, 0.05); }
.avatar-uploader-wrapper { position: relative; width: 102px; height: 102px; margin: 0 auto 20px; border-radius: 50%; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
.profile-avatar { border: 2px solid #ffffff; transition: transform 0.4s ease; }
.avatar-mask { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: rgba(15, 23, 42, 0.65); color: #ffffff; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 6px; font-size: 12px; cursor: pointer; opacity: 0; transition: all 0.3s ease; backdrop-filter: blur(4px); }
.avatar-uploader-wrapper:hover .avatar-mask { opacity: 1; }
.avatar-uploader-wrapper:hover .profile-avatar { transform: scale(1.05); }
.avatar-card h3 { margin: 0 0 8px; font-size: 18px; color: #0f172a; font-weight: 700; }
.account-identity { margin: 0; font-size: 12px; color: #64748b; background: #f1f5f9; display: inline-block; padding: 2px 10px; border-radius: 20px; }

.security-action-box { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 16px; padding: 16px; box-shadow: 0 4px 20px rgba(148, 163, 184, 0.05); }
.sec-item { display: flex; align-items: center; justify-content: space-between; cursor: pointer; padding: 12px; border-radius: 12px; transition: all 0.25s ease; }
.sec-item:hover { background: #f8fafc; }
.sec-item:hover .icon-pulse-wrapper.purple { transform: scale(1.05); box-shadow: 0 0 0 4px rgba(124, 58, 237, 0.15); }
.sec-left { display: flex; align-items: center; gap: 14px; }
.icon-pulse-wrapper { display: flex; align-items: center; justify-content: center; width: 36px; height: 36px; border-radius: 10px; transition: all 0.3s ease; }
.icon-pulse-wrapper.purple { background: #f5f3ff; color: #7c3aed; }
.sec-icon { font-size: 16px; }
.sec-info h5 { margin: 0 0 2px; font-size: 13px; color: #1e293b; font-weight: 600; }
.sec-info p { margin: 0; font-size: 11px; color: #94a3b8; }
.arrow-right { font-size: 14px; color: #94a3b8; transition: transform 0.2s; }
.sec-item:hover .arrow-right { transform: translateX(2px); color: #64748b; }

.main-form-pane { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 16px; padding: 36px; box-shadow: 0 4px 24px rgba(148, 163, 184, 0.04); }
.pane-header { border-bottom: 1px solid #f1f5f9; padding-bottom: 22px; margin-bottom: 32px; }
.header-title-row { display: flex; align-items: center; gap: 12px; margin-bottom: 6px; }
.pane-title { margin: 0; font-size: 18px; color: #0f172a; font-weight: 700; }
.pane-subtitle { margin: 0; font-size: 13px; color: #64748b; }

.form-section-title { font-size: 14px; color: #334155; font-weight: 700; display: flex; align-items: center; gap: 10px; margin: 28px 0 20px; }
.section-dot { width: 4px; height: 14px; border-radius: 4px; }
.section-dot.blue { background: #3b82f6; box-shadow: 0 2px 6px rgba(59,130,246,0.4); }
.section-dot.green { background: #10b981; box-shadow: 0 2px 6px rgba(16,185,129,0.4); }
.section-dot.orange { background: #f59e0b; box-shadow: 0 2px 6px rgba(245,158,11,0.4); }

.geek-radio-group { width: 100%; display: flex; }
:deep(.geek-radio-group .el-radio-button) { flex: 1; }
:deep(.geek-radio-group .el-radio-button__inner) { width: 100%; padding: 10px 0; font-size: 13px; font-weight: 500; transition: all 0.2s; border-radius: 0; }
:deep(.geek-radio-group .el-radio-button:first-child .el-radio-button__inner) { border-radius: 8px 0 0 8px; }
:deep(.geek-radio-group .el-radio-button:last-child .el-radio-button__inner) { border-radius: 0 8px 8px 0; }

.disabled-append-btn { color: #3b82f6; font-weight: 600; cursor: pointer; font-size: 12px; transition: color 0.2s; padding: 0 8px; user-select: none; }
.disabled-append-btn:hover { color: #2563eb; }

.form-submit-bar { border-top: 1px solid #f1f5f9; margin-top: 36px; padding-top: 24px; display: flex; gap: 14px; }
.save-profile-btn { background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%) !important; border: none !important; font-weight: 600; padding: 0 24px; border-radius: 8px; box-shadow: 0 4px 14px rgba(59,130,246,0.3); transition: all 0.25s ease; }
.save-profile-btn:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(59,130,246,0.4); }
.reset-profile-btn { border-radius: 8px; transition: all 0.2s; }
.reset-profile-btn:hover { background: #f8fafc; border-color: #cbd5e1; color: #475569; }

:deep(.el-form-item__label) { font-size: 13px !important; font-weight: 600 !important; color: #475569 !important; padding-bottom: 8px !important; }
:deep(.el-input__wrapper), :deep(.el-cascader .el-input__wrapper) { border-radius: 8px !important; padding: 4px 12px !important; box-shadow: 0 0 0 1px #e2e8f0 inset !important; background-color: #fafbfc; transition: all 0.2s; }
:deep(.el-input__wrapper:hover) { box-shadow: 0 0 0 1px #cbd5e1 inset !important; background-color: #ffffff; }
:deep(.el-input__wrapper.is-focus) { box-shadow: 0 0 0 1px #3b82f6 inset, 0 0 0 3px rgba(59,130,246,0.1) !important; background-color: #ffffff; }

.geek-dialog { border-radius: 16px !important; overflow: hidden; }
.dialog-confirm-btn { background: #3b82f6 !important; border-color: #3b82f6 !important; }
</style>