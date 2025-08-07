<template>
  <div>
    <div class="card" style="margin-bottom: 20px; line-height: 30px; font-size: 25px">
      <strong>Springboot3 + Vue3 的学习项目</strong>
    </div>
    <div style="display: flex; grid-gap: 10px">
      <el-card style="max-width: 800px; flex:1">
        <template #header>
          <div class="card-header" style="font-size: 25px">
            <span>前端技术栈</span>
          </div>
        </template>
        <p v-for="f_item in data.f_list" :key="f_item" class="text item" style="font-size: 23px">{{ f_item }}</p>
      </el-card>

      <el-card style="max-width: 800px; flex:1">
        <template #header>
          <div class="card-header" style="font-size: 25px">
            <span>后端技术栈</span>
          </div>
        </template>
        <p v-for="b_item in data.b_list" :key="b_item" class="text item" style="font-size: 23px">{{ b_item }}</p>
      </el-card>
    </div>


  </div>
</template>

<script setup>
import { reactive } from "vue";

const data = reactive({
  f_list: ['Vue3', 'ElementPlus', 'Vue-Router', 'Axios', 'wangEditor', 'Echarts'],
  b_list: ['SpringBoot3', 'Mybatis', 'Hutool']
})
</script>