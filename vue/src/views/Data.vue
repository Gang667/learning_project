<template>
  <div>
    <div style="display: flex; grid-gap: 10px; margin-bottom: 20px">
      <div class="card" style="padding: 20px; flex: 1; height: 400px" id="bar"></div>
      <div class="card" style="padding: 20px; flex: 1; height: 400px" id="line"></div>
    </div>

    <div style="display: flex; grid-gap: 10px">
      <div class="card" style="padding: 20px; flex:1; height: 400px" id="pie"></div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from "vue";
import * as echarts from "echarts";
import request from "@/utils/request.js";

const barOption = {
  title: {
    text: '各部门员工数量'
  },
  tooltip: {},
  legend: {
    data: ['人数']
  },
  xAxis: {
    data: []
  },
  yAxis: {},
  series: [
    {
      name: '人数',
      type: 'bar',
      data: []
    }
  ]
};

const lineOption = {
  title: {
    text: '近七天发布文章数量'
  },
  tooltip: {},
  legend: {
    data: ['数量']
  },
  xAxis: {
    data: []
  },
  yAxis: {},
  series: [
    {
      name: '发布数量',
      type: 'line',
      data: [],
      smooth: true
    }
  ]
};

const pieOption = {
  title: {
    text: '各部门员工数量比例图',
    left: 'center'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: '员工数量',
      type: 'pie',
      radius: '50%',
      data: [],
      label: {
        formatter: '{b}: {@2012} ({d}%)'
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
};

const data = reactive({

})

// onMounted 表示页面的所有元素都初始化完成了
onMounted(() => {
  //基于准备好的dom， 初始化ecahrts实例
  const barChart = echarts.init(document.getElementById('bar'));

  request.get('/barData').then(res => {
    barOption.xAxis.data = res.data.department //横轴的数据
    barOption.series[0].data = res.data.count //纵轴的数据
    //使用刚指定的配置项和数据显示图标
    barChart.setOption(barOption)
  })

  //基于准备好的dom， 初始化ecahrts实例
  const lineChart = echarts.init(document.getElementById('line'));

  request.get('/lineData').then(res => {
    lineOption.xAxis.data = res.data.date //横轴的数据
    lineOption.series[0].data = res.data.count //纵轴的数据
    //使用刚指定的配置项和数据显示图标
    lineChart.setOption(lineOption)
  })

  //基于准备好的dom， 初始化ecahrts实例
  const pieChart = echarts.init(document.getElementById('pie'));

  request.get('/pieData').then(res => {
    pieOption.series[0].data = res.data
    //使用刚指定的配置项和数据显示图标
    pieChart.setOption(pieOption)
  })
})

</script>

<style scoped>

</style>