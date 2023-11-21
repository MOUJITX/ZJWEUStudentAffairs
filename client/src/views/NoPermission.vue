<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <h1>无权限</h1>
      <p>该页面你无权限访问</p>
      <router-link to="/">返回首页</router-link>
    </div>
  </div>
</template>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f0f0;
}

.not-found-content {
  text-align: center;
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

h1 {
  font-size: 60px;
  color: #f56c6c;
}

p {
  font-size: 18px;
  color: #333;
  margin-top: 10px;
}

router-link {
  display: inline-block;
  margin-top: 20px;
  font-size: 16px;
}
</style>
<script setup>
</script>