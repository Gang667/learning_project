<template>
  <div style="font-size: 20px; font-weight: bold; color: cornflowerblue; margin-bottom: 20px">

    <div>
      <el-button type="primary" @click="router.back()">返回主页</el-button>
    </div>

    <div style="font-size: 30px">
      {{ a }}  {{ b }}
    </div>

    <div style="font-size: 20px; color: palegoldenrod; margin-bottom: 20px">
      {{ data.a }}
      {{ data.b }}
    </div>

    <div style="margin-bottom: 20px">
      <input v-model="data.a" />
    </div>

    <div style="margin-bottom: 20px">
      <input v-model="data.name" />
      <span style="color: darkred" v-if="data.name === '佩奇'">小猪佩奇</span>
      <span style="color: deepskyblue" v-if="data.name ==='乔治'">小猪乔治</span>
      <span style="color: hotpink" v-else>小猪朵朵</span>
    </div>

    <div style="margin-bottom: 20px">
      <div v-on:click="show(item)" style="width: 300px; height: 300px; text-align: center; line-height: 300px; font-size: 30px; margin-bottom: 10px; background-color: darkslategrey" v-for="item in data.arr">{{ item }}</div>

      <div>
        <select style="width: 200px">
          <option v-for="fruit in data.fruits">{{ fruit }}</option>
        </select>
      </div>
    </div>

    <div style="margin-bottom: 20px">
      <button @click="click">点我加好运</button>
    </div>

    <div style="margin-bottom: 20px">
      <div :style="data.box"></div>
      <div>
        <img :src="data.img" alt="">
      </div>
    </div>

    <div style="margin: 30px">
      <el-button type="primary" @click="clickData" :style="{color: 'yellow'}">新的加好运！</el-button>
      <el-button type="success" color="#626aef" :dark="isDark">啊哈哈</el-button>
      <el-button type="success">啊哈哈</el-button>
    </div>

    <div style="margin: 20px">
      <el-icon :size="20">
        <Edit />
      </el-icon>

      <span style="margin-left: 30px; color:#666">
        <el-icon :size="20" color="#666" style="top: 4px"><View /></el-icon> 100
      </span>

      <el-button type="danger" :icon="Delete" circle />

      <el-input
          v-model="data.a"
          style="width: 240px"
          placeholder="Pick a date"
          :suffix-icon="Calendar"
      />
    </div>

    <div>

    </div>


  </div>
</template>

<script setup>
import {reactive, ref, onMounted} from "vue";
import {Calendar, Delete} from "@element-plus/icons-vue";
import router from "@/router/index.js";

const a = ref(1)
const b = ref("真的很棒！")

const data = reactive({
  id: router.currentRoute.value.query.id,
  a: 123,
  b: "什么啊！！",
  name: "乔治",
  arr: [1,2,3],
  fruits: ['苹果','香蕉','橘子'],
  box:{
    width: '100px',
    height: '100px',
    backgroundColor: 'red'
  },
  img: 'https://www.runoob.com/wp-content/uploads/2017/01/vue.png'
})

console.log('获取到传递过来的id=' + data.id)

onMounted(() =>{
  console.log("页面加载完成")
})

const show = (value)=>{
  alert(value)
}

const click = () =>{
  alert("好运+1")
}

const clickData = () =>{
  alert("狠狠加好运！")
}
</script>

