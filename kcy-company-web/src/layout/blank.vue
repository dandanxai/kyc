<template>
    <div class="blank-layout">
    <!-- 仅仅是一个路由出口，用于渲染 auth 文件夹下的 login 或 register -->
    <router-view />
    </div>
</template>

<style scoped>
.blank-layout {
    width: 100vw;
    height: 100vh;
    overflow: hidden;
    /* 确保背景色能覆盖全屏，避免闪烁 */
    background-color: #f1f5f9;
}
</style>