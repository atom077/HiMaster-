import * as echarts from '../../ec-canvas/echarts';

const app = getApp();

Page({
  data: {
    ec: {
      onInit: function (canvas, width, height) {
        var chart = echarts.init(canvas, null, {
          width: width,
          height: height
        });
        canvas.setChart(chart)

        var option = {
          title: {
            text: '您的每日步数统计折线图',
            left: 'center'
          },
          color: ["#2ecc71"],
          legend: {
            data: ['步数'],
            top: 50,
            left: 'center',
            backgroundColor: '#ecf0f1',
            z: 100
          },
          grid: {
            containLabel: true
          },

          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: wx.getStorageSync('dayInfo'),
            splitNumber: 6
            // show: false
          },
          yAxis: {
            x: 'center',
            type: 'value',
            splitLine: {
              lineStyle: {
                type: 'dashed'
              }
            },
            splitNumber: 4
            // show: false
          },
          series: [{
            name: '步数',
            type: 'line',
            smooth: true,
            data: wx.getStorageSync('stepInfo')
          }]
        };
        chart.setOption(option);
        return chart;
      }
    }
  },
  onLoad: function(){
    
  }
  // initChart: function(canvas, width, height){
  //   chart = echarts.init(canvas, null, {
  //     width: width,
  //     height: height
  //   });
  //   canvas.setChart(chart)

  //   var option = {
  //     title: {
  //       text: '测试下面legend的红色区域不应被裁剪',
  //       left: 'center'
  //     },
  //     color: ["#37A2DA", "#67E0E3", "#9FE6B8"],
  //     legend: {
  //       data: ['A', 'B', 'C'],
  //       top: 50,
  //       left: 'center',
  //       backgroundColor: 'red',
  //       z: 100
  //     },
  //     grid: {
  //       containLabel: true
  //     },

  //     xAxis: {
  //       type: 'category',
  //       boundaryGap: false,
  //       data: ['a', 'b', 'c', 'd']
  //       // show: false
  //     },
  //     yAxis: {
  //       x: 'center',
  //       type: 'value',
  //       splitLine: {
  //         lineStyle: {
  //           type: 'dashed'
  //         }
  //       }
  //       // show: false
  //     },
  //     series: [{
  //       name: 'A',
  //       type: 'line',
  //       smooth: true,
  //       data: ['1', '2', '3', '4']
  //     }]
  //   };
  //   chart.setOption(option);
  //   return chart;
  // },
})
