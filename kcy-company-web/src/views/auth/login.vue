<template>
    <div class="auth-wrapper">
    <div class="auth-card">
        <div class="header">
        <h2>登录极客互娱系统</h2>
        <p>欢迎回到企业招聘管理后台</p>
        </div>
        <el-form :model="loginForm" label-position="top">
        <el-form-item label="企业租户 ID">
            <el-input v-model="loginForm.tenantId" placeholder="请输入企业ID" />
        </el-form-item>
        <el-form-item label="账号">
            <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
            <el-input v-model="loginForm.password" type="password" show-password />
        </el-form-item>
        <el-button type="primary" class="w-full" @click="handleLogin">登录</el-button>
        </el-form>
        <div class="footer-link">
        没有账号？<el-link type="primary" @click="$router.push('/auth/register')">立即注册企业</el-link>
        </div>
    </div>
    </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
const loginForm = reactive({ tenantId: '', username: '', password: '' })
const handleLogin = () => { /* 登录逻辑 */ }
</script>

<style scoped>
.auth-wrapper { height: 100vh; display: flex; align-items: center; justify-content: center; background: #f1f5f9; }
.auth-card { width: 400px; padding: 40px; background: #fff; border-radius: 16px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.header { margin-bottom: 24px; text-align: center; }
.w-full { width: 100%; margin-top: 10px; }
.footer-link { margin-top: 20px; text-align: center; font-size: 14px; }
</style>