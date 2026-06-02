<template>
    <div class="register-container">
    <el-card class="register-card">
        <div class="header">
        <h2>企业入驻申请</h2>
        <p>完善企业信息，开启极客互娱招聘智控之旅</p>
        </div>

        <el-form :model="regForm" :rules="rules" ref="regFormRef" label-position="top">
        <el-row :gutter="20">
            <el-col :span="12">
            <el-form-item label="企业简称 (昵称)" prop="nickname">
                <el-input v-model="regForm.nickname" placeholder="例如：极客互娱" />
            </el-form-item>
            </el-col>
            <el-col :span="12">
            <el-form-item label="管理员手机号" prop="mobile">
                <el-input v-model="regForm.mobile" placeholder="用于登录及接收通知" />
            </el-form-item>
            </el-col>
        </el-row>

        <el-form-item label="密码" prop="password">
            <el-input v-model="regForm.password" type="password" show-password />
        </el-form-item>

        <el-divider>企业资质验证</el-divider>
        <el-form-item label="营业执照名称" prop="licence_name">
            <el-input v-model="regForm.licence_name" placeholder="与营业执照保持一致" />
        </el-form-item>

        <el-row :gutter="20">
            <el-col :span="8">
            <el-form-item label="省份" prop="province"><el-input v-model="regForm.province" /></el-form-item>
            </el-col>
            <el-col :span="8">
            <el-form-item label="城市" prop="city"><el-input v-model="regForm.city" /></el-form-item>
            </el-col>
            <el-col :span="8">
            <el-form-item label="详细地址" prop="address_detail"><el-input v-model="regForm.address_detail" /></el-form-item>
            </el-col>
        </el-row>

        <el-form-item label="纳税人识别号" prop="taxpayer_code">
            <el-input v-model="regForm.taxpayer_code" />
        </el-form-item>

        <el-form-item label="营业执照照片">
            <el-upload action="#" list-type="picture-card" :limit="1">
            <el-icon><Plus /></el-icon>
            </el-upload>
        </el-form-item>

        <el-button type="primary" class="w-full" :loading="loading" @click="handleRegister">
            提交入驻申请
        </el-button>
        </el-form>

        <div class="footer-link">
        已有账号？<el-link type="primary" @click="$router.push('/auth/login')">直接登录</el-link>
        </div>
    </el-card>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const regFormRef = ref()

// 字段映射自 kyc_enterprise_info 表
const regForm = reactive({
    nickname: '',
    mobile: '',
    password: '',
    licence_name: '',
    province: '',
    city: '',
    address_detail: '',
    taxpayer_code: ''
})

const rules = {
    nickname: [{ required: true, message: '请输入企业简称', trigger: 'blur' }],
    mobile: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
    password: [{ required: true, message: '请设置密码', trigger: 'blur' }],
    licence_name: [{ required: true, message: '请输入执照名称', trigger: 'blur' }]
}

const handleRegister = () => {
    regFormRef.value.validate((valid: boolean) => {
    if (valid) {
        loading.value = true
        // 这里调用后端 API
        console.log('提交的注册数据:', regForm)
        setTimeout(() => {
        loading.value = false
        ElMessage.success('注册申请已提交，等待平台审核')
        }, 1000)
    }
    })
}
</script>

<style scoped>
.register-container { min-height: 100vh; padding: 40px 0; background: #f1f5f9; display: flex; justify-content: center; }
.register-card { width: 600px; padding: 30px; }
.header { text-align: center; margin-bottom: 30px; }
.w-full { width: 100%; margin-top: 20px; }
.footer-link { margin-top: 20px; text-align: center; font-size: 14px; }
</style>