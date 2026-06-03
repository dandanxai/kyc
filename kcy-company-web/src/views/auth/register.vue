<template>
  <div class="auth-container">
    <div class="brand-section">
      <div class="brand-overlay"></div>
      <div class="brand-content">
        <div class="logo-area">
          <span class="logo-icon">🚀</span>
          <span class="logo-text">极客互娱</span>
        </div>
        <div class="slogan-area">
          <h1>加入我们，开启高效招聘新纪元</h1>
          <p>只需几步轻松入驻，即可开启海量精英人才的精准触达与智能管理。</p>
        </div>
        <div class="brand-footer">
          © 2026 Geek Mutual Entertainment. All Rights Reserved.
        </div>
      </div>
    </div>

    <div class="form-section">
      <div class="register-card">
        <div class="header">
          <h2>企业入驻申请</h2>
          <p>请填写以下真实信息完成企业资质认证</p>
        </div>
        
        <el-form :model="regForm" :rules="rules" ref="regFormRef" label-position="top" size="large">
          <el-divider content-position="left"><span class="divider-title">👤 账户管理信息</span></el-divider>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="真实姓名 (用户名)" prop="username">
                <el-input v-model="regForm.username" placeholder="请输入真实姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="用户昵称" prop="nickname">
                <el-input v-model="regForm.nickname" placeholder="请输入用户昵称" />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="手机号" prop="mobile">
                <el-input v-model="regForm.mobile" placeholder="请输入11位手机号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="regForm.email" placeholder="shanghai@example.com" />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item label="密码" prop="password">
            <el-input v-model="regForm.password" type="password" show-password placeholder="6-20位登录密码" />
          </el-form-item>
          
          <el-form-item label="性别" prop="sex">
            <el-radio-group v-model="regForm.sex">
              <el-radio :label="1">男</el-radio>
              <el-radio :label="2">女</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-divider content-position="left"><span class="divider-title">🏢 企业资质认证</span></el-divider>
          <el-form-item label="公司营业执照名称" prop="licenceName">
            <el-input v-model="regForm.licenceName" placeholder="如：极客互娱网络科技有限公司" />
          </el-form-item>
          <el-form-item label="纳税人识别号" prop="taxpayerCode">
            <el-input v-model="regForm.taxpayerCode" placeholder="91330000XXXXXXX" />
          </el-form-item>
          
          <el-row :gutter="10">
            <el-col :span="8">
              <el-form-item label="所在省" prop="province"><el-input v-model="regForm.province" placeholder="安徽省" /></el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="所在市" prop="city"><el-input v-model="regForm.city" placeholder="合肥市" /></el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="所在区/县" prop="county"><el-input v-model="regForm.county" placeholder="蜀山区" /></el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item label="详细地址" prop="addressDetail">
            <el-input v-model="regForm.addressDetail" placeholder="某某路某某产业园几号楼" />
          </el-form-item>
          <el-form-item label="经营范围" prop="businessScope">
            <el-input v-model="regForm.businessScope" type="textarea" :rows="3" placeholder="计算机软硬件开发、技术服务..." />
          </el-form-item>

          <el-divider content-position="left"><span class="divider-title">📸 影像材料上传</span></el-divider>
          <div class="upload-group">
            <el-form-item label="营业执照" prop="licenceFile">
              <el-upload
                action="#"
                list-type="picture-card"
                :auto-upload="true"
                :show-file-list="false"
                :http-request="(options) => handleRealTimeUpload(options, 'licenceFile')"
              >
                <img v-if="licenceFilePreview" :src="licenceFilePreview" class="preview-img" />
                <el-icon v-else><Plus /></el-icon>
              </el-upload>
            </el-form-item>

            <el-form-item label="身份证正面" prop="identityFront">
              <el-upload
                action="#"
                list-type="picture-card"
                :auto-upload="true"
                :show-file-list="false"
                :http-request="(options) => handleRealTimeUpload(options, 'identityFront')"
              >
                <img v-if="identityFrontPreview" :src="identityFrontPreview" class="preview-img" />
                <el-icon v-else><Plus /></el-icon>
              </el-upload>
            </el-form-item>

            <el-form-item label="身份证反面" prop="identityBack">
              <el-upload
                action="#"
                list-type="picture-card"
                :auto-upload="true"
                :show-file-list="false"
                :http-request="(options) => handleRealTimeUpload(options, 'identityBack')"
              >
                <img v-if="identityBackPreview" :src="identityBackPreview" class="preview-img" />
                <el-icon v-else><Plus /></el-icon>
              </el-upload>
            </el-form-item>
          </div>

          <el-button type="primary" class="submit-btn" :loading="loading" @click="handleRegister">
            确认提交入驻申请并登录
          </el-button>
        </el-form>

        <div class="footer-link">
          已有账号？<el-link type="primary" :underlined="false" @click="$router.push('/auth/login')">返回立即登录</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

import { EnterpriseAuthApi } from '@/api/enterprise'
import { InfraFileApi } from '@/api/file'
import { setToken } from '@/utils/auth'

import { useUserStore } from '@/store/modules/user'
const userStore = useUserStore()

const router = useRouter()
const loading = ref(false)
const regFormRef = ref()

const licenceFilePreview = ref('')
const identityFrontPreview = ref('')
const identityBackPreview = ref('')

const regForm = reactive({
  mobile: '', email: '', password: '', nickname: '', username: '', sex: 1,
  licenceName: '', province: '', city: '', county: '', addressDetail: '',
  taxpayerCode: '', businessScope: '', avatar: '', 
  licenceFile: '', 
  identityFront: '', 
  identityBack: ''  
})

const rules = {
  username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
  nickname: [{ required: true, message: '昵称不能为空', trigger: 'blur' }],
  mobile: [
    { required: true, message: '手机号不能为空', trigger: 'blur' },
    { pattern: /^[1][3-9][0-9]{9}$/, message: '手机号格式有误', trigger: 'blur' }
  ],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为 6-20 位', trigger: 'blur' }
  ],
  sex: [{ required: true, message: '性别不能为空', trigger: 'change' }],
  licenceName: [{ required: true, message: '公司营业执照名称不能为空', trigger: 'blur' }],
  province: [{ required: true, message: '所在省不能为空', trigger: 'blur' }],
  city: [{ required: true, message: '所在市不能为空', trigger: 'blur' }],
  county: [{ required: true, message: '所在区/县不能为空', trigger: 'blur' }],
  addressDetail: [{ required: true, message: '详细地址不能为空', trigger: 'blur' }],
  taxpayerCode: [{ required: true, message: '纳税人识别号不能为空', trigger: 'blur' }],
  businessScope: [{ required: true, message: '经营范围不能为空', trigger: 'blur' }],
  licenceFile: [{ required: true, message: '请上传公司营业执照影像材料', trigger: 'change' }],
  identityFront: [{ required: true, message: '请上传法人身份证正面照片', trigger: 'change' }],
  identityBack: [{ required: true, message: '请上传法人身份证反面照片', trigger: 'change' }]
}

const handleRealTimeUpload = async (options: any, fieldName: 'licenceFile' | 'identityFront' | 'identityBack') => {
  try {
    const res = await InfraFileApi.uploadFile(options.file)
    if (res && res.code === 0 && res.data) {
      const imageUrl = res.data 
      regForm[fieldName] = imageUrl
      
      if (fieldName === 'licenceFile') licenceFilePreview.value = imageUrl
      if (fieldName === 'identityFront') identityFrontPreview.value = imageUrl
      if (fieldName === 'identityBack') identityBackPreview.value = imageUrl
      
      ElMessage.success('材料图片上传成功！')
    } else {
      options.onError(new Error('上传失败，未获取到合法数据'))
    }
  } catch (error) {
    console.error('实时文件上传异常:', error)
    options.onError(error)
  }
}

/**
 * 🌟 核心修改：注册成功 -> 存Token -> 存Pinia -> 闪现跳转
 */
const handleRegister = async () => {
  if (!regFormRef.value) return
  await regFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    loading.value = true
    try {
      // 1. 发起注册
      const res = await EnterpriseAuthApi.registerEnterprise(regForm)
      
      if (res && res.code === 0 && res.data) {
        ElMessage.success('企业入驻成功，已为您自动登录！')
        
        // 2. 默默固定系统租户ID
        localStorage.setItem('TENANT_ID', '1')
        
        // 3. 将后端下发的 accessToken 和 refreshToken 塞入本地 Cookie/localStorage
        setToken({
          accessToken: res.data.accessToken,
          refreshToken: res.data.refreshToken
        })
        
        // 🌟 4. 核心：立刻调用 Pinia 的 Action 去异步调后端接口获取完整的 UserInfo（包含昵称、头像、权限等）
        // 这样跳进 /dashboard 时，Pinia 已经是满血状态，不需要重复查询
        if (typeof userStore.setUserInfoAction === 'function') {
          await userStore.setUserInfoAction() // 芋道标准 Action 名字
        } else if (typeof userStore.getUserInfo === 'function') {
          await userStore.getUserInfo()       // 或者是这种命名
        }

        // 5. 闪现到后台控制台首页 
        router.push('/dashboard')
      } else {
        ElMessage.error(res.msg || '入驻成功，但未能获取到合法登录凭证，请前往登录页')
        router.push('/auth/login')
      }
    } catch (error) {
      console.error('注册并快捷登录流程异常:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.auth-container { height: 100vh; display: flex; overflow: hidden; background-color: #ffffff; }
.brand-section { position: relative; flex: 1; display: flex; background-image: url('https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=1964&auto=format&fit=crop'); background-size: cover; background-position: center; }
.brand-overlay { position: absolute; inset: 0; background: linear-gradient(135deg, rgba(15, 23, 42, 0.88) 0%, rgba(30, 41, 59, 0.65) 100%); backdrop-filter: blur(2px); }
.brand-content { position: relative; z-index: 10; display: flex; flex-direction: column; justify-content: space-between; padding: 50px; color: #ffffff; width: 100%; }
.logo-area { display: flex; align-items: center; gap: 12px; }
.logo-icon { font-size: 28px; }
.logo-text { font-size: 24px; font-weight: 800; letter-spacing: 1px; background: linear-gradient(to right, #38bdf8, #6366f1); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
.slogan-area h1 { font-size: 34px; font-weight: 700; line-height: 1.3; margin-bottom: 20px; text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2); }
.slogan-area p { font-size: 15px; color: #cbd5e1; max-width: 480px; line-height: 1.6; }
.brand-footer { font-size: 13px; color: #94a3b8; }
.form-section { flex: 1.4; display: flex; background-color: #f8fafc; padding: 40px; overflow-y: auto; }
.register-card { width: 100%; max-width: 680px; margin: auto; background: #ffffff; padding: 45px; border-radius: 20px; box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05), 0 8px 10px -6px rgba(0, 0, 0, 0.05); border: 1px solid #e2e8f0; }
.header { margin-bottom: 30px; text-align: left; }
.header h2 { font-size: 26px; color: #0f172a; font-weight: 700; margin-bottom: 8px; }
.header p { font-size: 14px; color: #64748b; }
.divider-title { font-size: 14px; font-weight: 700; color: #4f46e5; letter-spacing: 0.5px; }
:deep(.el-form-item__label) { font-weight: 600; color: #334155; margin-bottom: 4px !important; }
:deep(.el-input__wrapper), :deep(.el-textarea__inner) { border-radius: 8px; box-shadow: 0 0 0 1px #e2e8f0 inset; }
:deep(.el-input__wrapper.is-focus), :deep(.el-textarea__inner:focus) { box-shadow: 0 0 0 1px #4f46e5 inset !important; }
.upload-group { display: flex; justify-content: flex-start; gap: 24px; margin-bottom: 15px; flex-wrap: wrap; }
:deep(.el-upload--picture-card) { width: 110px; height: 110px; border-radius: 10px; border: 1px dashed #cbd5e1; background-color: #f8fafc; overflow: hidden; position: relative; }
.preview-img { width: 100%; height: 100%; object-fit: cover; border-radius: 10px; }
.submit-btn { width: 100%; height: 48px; font-size: 16px; font-weight: 600; border-radius: 10px; background: linear-gradient(135deg, #4f46e5 0%, #3730a3 100%); border: none; box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3); margin-top: 25px; transition: all 0.3s; }
.submit-btn:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(79, 70, 229, 0.4); }
.footer-link { margin-top: 25px; text-align: center; font-size: 14px; color: #64748b; }
@media (max-width: 1024px) { .brand-section { display: none; } .form-section { padding: 20px; background-color: #ffffff; } .register-card { box-shadow: none; border: none; padding: 10px; } }
</style>