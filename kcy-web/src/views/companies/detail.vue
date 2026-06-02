<template>
    <div class="comp-detail-container">
    <div class="detail-banner">
        <img src="https://picsum.photos/1200/200" class="banner-bg" />
        <div class="banner-content">
        <img src="https://picsum.photos/100/100?random=1" class="logo" />
        <div class="title-box">
            <h1>极客云创科技</h1>
            <p>让技术创造价值，用代码改变世界。</p>
        </div>
        </div>
    </div>

    <div class="detail-main">
        <div class="left-col">
        <div class="section-box">
            <h3>公司介绍</h3>
            <p>这里是详尽的公司描述，包含愿景、团队文化及技术栈...</p>
        </div>
        <div class="section-box">
            <h3>在招职位</h3>
            </div>
        </div>
        
        <div class="right-col">
        <div class="info-card">
            <h3>公司概览</h3>
            <p>行业：互联网/AI</p>
            <p>规模：1000人以上</p>
            <p>地址：合肥市高新区软件园</p>
            <el-button type="primary" plain style="width:100%">查看官网</el-button>
        </div>
        </div>
    </div>
    </div>
</template>

<style scoped>
.comp-detail-container { padding-top: 68px; background: #f6f8f9; min-height: 100vh; }
.detail-banner { height: 200px; position: relative; }
.banner-bg { width: 100%; height: 100%; object-fit: cover; }
.banner-content { position: absolute; bottom: -40px; left: 10%; display: flex; align-items: flex-end; gap: 20px; }
.logo { width: 100px; height: 100px; border-radius: 12px; border: 4px solid #fff; }
.detail-main { max-width: 1200px; margin: 60px auto 0; display: flex; gap: 30px; }
.left-col { flex: 1; }
.right-col { width: 320px; }
.section-box, .info-card { background: #fff; padding: 24px; border-radius: 12px; margin-bottom: 20px; }
</style>