const app = getApp()
Page({
  data: {
    userInfo: app.globalData.userInfo,
    stepInfo: app.globalData.stepInfo,
    dateInfo: app.globalData.dateInfo,
    userStepInfo: app.globalData.userStepInfo,
    dayInfo: '',
    currentDate: '',
    currenStep: '',
    count: 0,
    forCount: 0,
    backCount: 0,
    forPoint: '',
    backPoint: '',
    temperature: '',
    weather: '天气',
    HM: '',   // 湿度
    LIT: '',  // 光照
    TM: ''    // 温度
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var that = this
    that.wxLogin()

    // 获取用户所在地天气信息
    that.get_Location_Info()

    // 获取用户绑定的设备模块的状态信息
    that.get_module_Info()
  },

  wxLogin: function(res){
    var that = this
    wx.login({
      success: function (res) {
        if (res.code) {
          wx.request({
            url: 'https://www.whohim.top/user/get_sessionkey/',
            data: {
              code: res.code
            },
            header: {
              "content-type": 'application/x-www-form-urlencoded'
            },
            method: 'POST',
            success: function (res) {
              var session_key = res.data.session_key;
              var openid = res.data.openid
              console.log('这是openid', openid)
              console.log('这是session_key', session_key)
              wx.setStorageSync('openid', openid)
              app.globalData.openid = openid
              if (app.openidReadyCallback) {
                app.openidReadyCallback(openid)
              }
              that.setData({
                openid: openid
              })
              wx.getSetting({
                success: res => {
                  if (res.authSetting['scope.userInfo']) {
                    // 已经授权，可以直接调用 getUserInfo 获取头像昵称，不会弹框
                    wx.getUserInfo({
                      success: res => {
                        console.log(res.userInfo)
                        // 可以将 res 发送给后台解码出 unionId
                        app.globalData.userInfo = res.userInfo
                        // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
                        // 所以此处加入 callback 以防止这种情况
                        if (app.userInfoReadyCallback) {
                          app.userInfoReadyCallback(res.userInfo)
                        }
                        that.setData({
                          userInfo: res.userInfo
                        })
                        wx.setStorageSync('userInfo', res.userInfo)
                        var appid = 'wx1ce56b7e907ee346';
                        that.getData(appid, session_key, openid, res.userInfo)
                      }
                    })
                  } else {
                    wx.showModal({
                      title: '智能管家想要获取您的公开信息(头像昵称)',
                      content: '前往授权',
                      success: function (res) {
                        if (res.confirm) {
                          wx.navigateTo({
                            url: '../index/auth',
                          })
                        } else if (res.cancel) {
                          wx.showToast({
                            title: '您已取消授权，可能会影响您的用户体验，可在个人中心>微信授权重新授权',
                            icon: "none",
                            duration: 3000
                          })
                        }
                      }
                    })
                  }
                }
              })
            }
          })
        }
      }
    })
  },

  // 请求业务数据=》微信运动
  getData: function (appid, session_key, openid, userInfo) {
    var that = this;
    wx.getSetting({
      success: function (res) {
        if (!res.authSetting['scope.werun']) {
          // 未授权微信运动，则请求授权
          wx.authorize({
            scope: 'scope.werun',
            success() {
              that.getWerun(appid, session_key, openid, userInfo)
            }
          })
        } else {
          // 已经授权，可直接调用getWerunData
          that.getWerun(appid, session_key, openid, userInfo)
        }
      }
    })
  },

  // 开发者服务器用appid,session_key获取微信运动数据
  getWerun: function (appid, session_key, openid, userInfo) {
    var that = this
    wx.getWeRunData({
      success: function (res) {
        var encryptedData = res.encryptedData;
        var iv = res.iv;
        wx.request({
          url: 'https://www.whohim.top/user/get_werun/',
          data: {
            appid: appid,
            session_key: session_key,
            encryptedData: encryptedData,
            iv: iv
          },
          method: 'POST',
          header: {
            "content-type": 'application/x-www-form-urlencoded'
          },
          success: function (res) {
            var stepInfo = res.data.data
            console.log(stepInfo)
            var stepArray = []
            var dateArray = []
            var dayArray = []
            for(var i in stepInfo){
              dateArray.push(i)
              stepArray.push(stepInfo[i]) 
              dayArray.push(i.slice(5))
            }
            that.setData({
              stepInfo: stepArray,
              dayInfo: dayArray,
              dateInfo: dateArray,
              currentDate: dateArray[0],
              currentStep: stepArray[0]
            })
            var stepLine = stepArray.reverse()
            var dayLine = dayArray.reverse()
            wx.setStorageSync('stepInfo', stepLine)
            wx.setStorageSync('dayInfo', dayLine)
            // 更新用户当前步数
            wx.request({
              url: 'https://www.whohim.top/user/update_userStep',
              header: {
                "content-type": 'application/x-www-form-urlencoded'
              },
              data: {
                openid: openid,
                stepinfo: stepArray[30],
                nickname: userInfo.nickName,
                avatarurl: userInfo.avatarUrl,
              },
              method: 'POST',
              success: function (res) {
                console.log(res.data)
                // 查询步数排行
                wx.request({
                  url: 'https://www.whohim.top/user/get_userStep/',
                  header: {
                    'content-type': 'application/json'
                  },
                  success: function (res) {
                    console.log('步数排行', res.data.data)
                    that.setData({
                      userStepInfo: res.data.data
                    })
                  }
                })
              }
            }) 
          }
        })
      }
    })
  },

  // 获取用户所在地理位置
  get_Location_Info: function(res){
    var that = this
    wx.getSetting({
      success: function (res) {
        if (!res.authSetting['scope.userLocation']) {
          wx.authorize({
            scope: 'scope.userLocation',
            success() {
              wx.getLocation({
                type: 'wgs84',
                success: function (res) {
                  var lat = res.latitude
                  var long = res.longitude
                  wx.request({
                    url: 'https://www.whohim.top/module/get_weather',
                    data: {
                      lat: lat,
                      lon: long
                    },
                    header: {
                      "content-type": 'application/x-www-form-urlencoded'
                    },
                    method: 'POST',
                    success: function (res) {
                      var weatherInfo = JSON.parse(res.data.data['future'])
                      app.globalData.weatherInfo = weatherInfo
                      if (app.weatherInfoReadyCallback) {
                        app.weatherInfoReadyCallback(weatherInfo)
                      }
                      var dat_sort_arr = []   // 提取key进行排序
                      for (var i in weatherInfo) {
                        dat_sort_arr.push(i)
                      }
                      var weatherArray = []   // 将排序后的天气数据存入新的数组
                      var wea_day_arr = []
                      var temperature_arr = []
                      for (var i in dat_sort_arr.sort()) {
                        weatherArray.push(weatherInfo[dat_sort_arr[i]])
                        wea_day_arr.push(weatherInfo[dat_sort_arr[i]]['date'].slice(4))
                        temperature_arr.push(weatherInfo[dat_sort_arr[i]]['temperature'].slice(0, 2))
                      }
                      console.log(weatherArray)
                      wx.setStorageSync('weatherInfo', weatherArray)
                      wx.setStorageSync('wea_day_arr', wea_day_arr)
                      wx.setStorageSync('temperature_arr', temperature_arr)
                      var temperature = weatherArray[0]['temperature']
                      var weather = weatherArray[0]['weather']
                      that.setData({
                        weatherArray: weatherArray,
                        temperature: temperature,
                        weather: weather
                      })
                    }
                  })
                },
              })
            }
          })
        } else {
          wx.getLocation({
            type: 'wgs84',
            success: function (res) {
              var lat = res.latitude
              var long = res.longitude
              wx.request({
                url: 'https://www.whohim.top/module/get_weather',
                data: {
                  lat: lat,
                  lon: long
                },
                header: {
                  "content-type": 'application/x-www-form-urlencoded'
                },
                method: 'POST',
                success: function (res) {
                  var weatherInfo = JSON.parse(res.data.data['future'])
                  var dat_sort_arr = []   // 提取key进行排序
                  for(var i in weatherInfo){
                    dat_sort_arr.push(i)
                  }
                  var weatherArray = []   // 将排序后的天气数据存入新的数组
                  var wea_day_arr = []
                  var temperature_arr = []
                  for (var i in dat_sort_arr.sort()){
                    weatherArray.push(weatherInfo[dat_sort_arr[i]]) 
                    wea_day_arr.push(weatherInfo[dat_sort_arr[i]]['date'].slice(4))
                    temperature_arr.push(weatherInfo[dat_sort_arr[i]]['temperature'].slice(0,2))
                  }
                  console.log(weatherArray)
                  wx.setStorageSync('weatherInfo', weatherArray)
                  wx.setStorageSync('wea_day_arr', wea_day_arr)
                  wx.setStorageSync('temperature_arr', temperature_arr)
                  var temperature = weatherArray[0]['temperature']
                  var weather = weatherArray[0]['weather']
                  that.setData({
                    weatherArray: weatherArray,
                    temperature: temperature,
                    weather: weather
                  })
                }
              })
            },
          })
        }
      }
    })
  },

  // 获取用户的绑定设备的状态
  get_module_Info: function(res){
    var that = this
    var token = wx.getStorageSync('token')
    wx.request({
      url: 'https://www.whohim.top/module/get_module_status_and_data',
      data: {
        token: token
      },
      header: {
        "content-type": 'application/x-www-form-urlencoded'
      },
      method: 'POST',
      success: function (res) {
        var machineInfo = res.data
        console.log(machineInfo)
        var HM = machineInfo.data.HM
        var LIT = machineInfo.data.LIT
        var TM = machineInfo.data.TM
        that.setData({
          HM: HM + '%',
          LIT: LIT + 'lx',
          TM: TM + '℃'
        })
      }
    })
  },

  // 获取小程序所有用户的步数数据
  get_userStepInfo: function(res){
    var that = this
    wx.request({
      url: 'https://www.whohim.top/user/get_userStep/',
      header: {
        'content-type': 'application/json'
      },
      success: function (res) {
        console.log('步数排行', res.data.data)
        that.setData({
          userStepInfo: res.data.data
        })
      }
    })
  },

  weather_forecast: function(res){
    wx.navigateTo({
      url: '../bar/weather',
    })
  },
  forward: function(res){
    var that = this
    var count = that.data.count + 1
    if(count > 30){
      that.setData({
        forPoint: 'none'
      })
    }else{
      that.setData({
        backPoint: ''
      })
      var stepInfo = that.data.stepInfo
      var dateInfo = that.data.dateInfo
      var currentStep = stepInfo[count]
      var currentDate = dateInfo[count]
      that.setData({
        count: count,
        currentDate: currentDate,
        currentStep: currentStep
      })
    }
    
  },
  backward: function(res){
    var that = this
    var count = that.data.count - 1
    if(count < 0){
      that.setData({
        backPoint: 'none'
      })
    }else{
      that.setData({
        forPoint: ''
      })
      var stepInfo = that.data.stepInfo
      var dateInfo = that.data.dateInfo
      var currentStep = stepInfo[count]
      var currentDate = dateInfo[count]
      that.setData({
        count: count,
        currentDate: currentDate,
        currentStep: currentStep
      })
    }
  },
  stepView: function(res){
    wx.navigateTo({
      url: '../bar/step',
    })
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {
    console.log('-------下拉刷新-------')
    wx.showNavigationBarLoading()
    this.get_Location_Info()
    console.log('刷新地理信息')
    this.get_module_Info()
    console.log('刷新设备信息')
    this.get_userStepInfo()
    console.log('刷新步数排行')
    wx.hideNavigationBarLoading()
    wx.stopPullDownRefresh()
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function (res) {
    if (res.from === 'button') {
      // 来自页面内转发按钮
      console.log(res.target)
    }
    return {
      title: '快来和我一起体验智能管家吧',
      path: '/pages/index/index'
    }
  }
})