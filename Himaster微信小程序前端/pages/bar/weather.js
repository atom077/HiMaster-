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
            text: '未来七天温度变化趋势',
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
            data: wx.getStorageSync('wea_day_arr'),
            splitNumber: 3
            // show: false
          },
          yAxis: {
            x: 'center',
            name: '温度/℃',
            type: 'value',
            splitLine: {
              lineStyle: {
                type: 'dashed'
              }
            },
            min: 22,
            max: 32,
            // show: false
          },
          minInterval: 2,
          series: [{
            name: '步数',
            type: 'line',
            smooth: true,
            data: wx.getStorageSync('temperature_arr')
          }]
        };
        chart.setOption(option);
        return chart;
      }
    },
    weatherInfo: app.globalData.weatherInfo
  },
  onLoad: function () {
    var that = this
    var weatherInfo = wx.getStorageSync('weatherInfo')
    that.setData({
      weatherInfo: weatherInfo
    })
    // if (app.globalData.weatherInfo) {
    //   // 如果已经获取到数据   
    //   that.setData({
    //     weatherInfo: app.globalData.weatherInfo
    //   })
    // } else {
    //   // 未获取到数据，执行回调函数，确保正确取值
    //   app.weatherInfoReadyCallback = function () {
    //     that.setData({
    //       weatherInfo: app.globalData.weatherInfo
    //     })
    //   }
    // }
  }
})
