<template>
  <div ref="chart" style="height:400px"></div>
</template>
<script setup lang="ts">
import * as echarts from "echarts";
import { onMounted, ref } from "vue";
onMounted(()=>{const chart=echarts.init(chartRef.value);chart.setOption({xAxis:{type:"category",data:["s1","s2"]},yAxis:{type:"value"},series:[{type:"bar",data:[80,90]}]});});const chartRef=ref();</script>