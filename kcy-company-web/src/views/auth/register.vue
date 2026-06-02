<template>
  <div class="register-page">
    <el-card class="register-box" shadow="hover">
      <div class="title">企业入驻申请</div>
      
      <el-form :model="regForm" :rules="rules" ref="regFormRef" label-position="top">
        <el-divider content-position="left">账户信息</el-divider>
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
              <el-input v-model="regForm.email" placeholder="zhangsan@example.com" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="密码" prop="password">
          <el-input v-model="regForm.password" type="password" show-password placeholder="6-20位密码" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="regForm.sex">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-divider content-position="left">企业资质</el-divider>
        <el-form-item label="公司营业执照名称" prop="licenceName">
          <el-input v-model="regForm.licenceName" placeholder="如：阿里云计算有限公司" />
        </el-form-item>
        <el-form-item label="纳税人识别号" prop="taxpayerCode">
          <el-input v-model="regForm.taxpayerCode" placeholder="91330000XXXXXXX" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="所在省" prop="province"><el-input v-model="regForm.province" placeholder="浙江省" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="所在市" prop="city"><el-input v-model="regForm.city" placeholder="杭州市" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="所在区/县" prop="county"><el-input v-model="regForm.county" placeholder="西湖区" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址" prop="addressDetail">
          <el-input v-model="regForm.addressDetail" placeholder="某某街道某某号" />
        </el-form-item>
        <el-form-item label="经营范围" prop="businessScope">
          <el-input v-model="regForm.businessScope" type="textarea" :rows="3" placeholder="计算机软硬件..." />
        </el-form-item>

        <el-divider content-position="left">影像材料上传</el-divider>
        <div class="upload-group">
          <el-form-item label="营业执照" required>
            <el-upload
              action="#"
              list-type="picture-card"
              :auto-upload="false"
              :limit="1"
              :on-change="(file) => handleFileChange(file, 'licenceFile')"
              :on-remove="() => handleFileRemove('licenceFile')"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </el-form-item>
          <el-form-item label="身份证正面" required>
            <el-upload
              action="#"
              list-type="picture-card"
              :auto-upload="false"
              :limit="1"
              :on-change="(file) => handleFileChange(file, 'identityFront')"
              :on-remove="() => handleFileRemove('identityFront')"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </el-form-item>
          <el-form-item label="身份证反面" required>
            <el-upload
              action="#"
              list-type="picture-card"
              :auto-upload="false"
              :limit="1"
              :on-change="(file) => handleFileChange(file, 'identityBack')"
              :on-remove="() => handleFileRemove('identityBack')"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </el-form-item>
        </div>

        <el-button type="primary" class="submit-btn" :loading="loading" @click="handleRegister">确认入驻</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { uploadFileApi, registerEnterpriseApi } from '../../api/enterprise'

const router = useRouter()
const loading = ref(false)
const regFormRef = ref()

// 暂存区：存储 el-upload 选中的原生 File 对象
const fileFiles = reactive<Record<string, File | null>>({
  licenceFile: null,
  identityFront: null,
  identityBack: null
})

const regForm = reactive({
  mobile: '',
  email: '',
  password: '',
  nickname: '',
  username: '', 
  sex: 1,
  licenceName: '',
  province: '',
  city: '',
  county: '', 
  addressDetail: '',
  taxpayerCode: '',
  businessScope: '',
  avatar: '', 
  licenceFile: '',
  identityFront: '',
  identityBack: ''
})

// 完全对齐后端校验注解 @NotBlank, @Pattern, @Length
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
  businessScope: [{ required: true, message: '经营范围不能为空', trigger: 'blur' }]
}

// 监听文件选中状态，存入暂存区
const handleFileChange = (uploadFile: any, fieldName: string) => {
  if (uploadFile.raw) {
    fileFiles[fieldName] = uploadFile.raw
  }
}

// 监听文件移除
const handleFileRemove = (fieldName: string) => {
  fileFiles[fieldName] = null
  regForm[fieldName] = ''
}

// 核心提交逻辑
const handleRegister = async () => {
  if (!regFormRef.value) return
  
  await regFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    // 影像材料的前端强校验
    // if (!fileFiles.licenceFile || !fileFiles.identityFront || !fileFiles.identityBack) {
    //   ElMessage.warning('请上传完整的营业执照及身份证正反面照片！')
    //   return
    // }

    loading.value = true
    try {
      // 1. 异步并行上传三张图片，拿回真实的 URL
      const uploadTasks = [
        uploadFileApi(fileFiles.licenceFile!).then(res => regForm.licenceFile = res.data.url),
        uploadFileApi(fileFiles.identityFront!).then(res => regForm.identityFront = res.data.url),
        uploadFileApi(fileFiles.identityBack!).then(res => regForm.identityBack = res.data.url)
      ]
      
      await Promise.all(uploadTasks)

      // 2. 影像上传成功，组合完表单数据后，正式提交入驻
      const response = await registerEnterpriseApi(regForm)
      
      // 基于你的 index.ts 拦截器，非0错误已经被拦截弹窗，能走到这里的 response 必然 code === 0
      ElMessage.success('企业入驻申请提交成功，请等待审核！')
      router.push('/auth/login')
      
    } catch (error) {
      console.error('注册流程异常:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.register-page { min-height: 100vh; padding: 40px 20px; background: #f1f5f9; display: flex; justify-content: center; }
.register-box { width: 800px; padding: 20px; }
.title { font-size: 22px; font-weight: bold; text-align: center; margin-bottom: 20px; color: #1e293b; }
.upload-group { display: flex; justify-content: space-between; gap: 10px; margin-bottom: 10px; }
.submit-btn { width: 100%; margin-top: 30px; height: 45px; font-size: 16px; }
:deep(.el-form-item__label) { font-weight: 600; color: #475569; }
</style>