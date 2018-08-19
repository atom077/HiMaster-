const app = getApp()
Page({
  data: {
    src: [],
    presrc: [],
    userSrc: [],
    openid: '',
    token: ''
  },
  //事件处理函数
  bindViewTap: function () {
    wx.navigateTo({
      url: '../logs/logs'
    })
  },
  onLoad(options) {
    var token = options.token
    this.setData({
      token: token
    })
    if (app.globalData.openid) {
      var openid = app.globalData.openid
      this.setData({
        openid: openid
      })
    } else {
      app.openidReadyCallback = function () {
        var openid = app.globalData.openid
        this.setData({
          openid: openid
        })
      }
    }
    wx.getSetting({
      success: function (res) {
        if (!res.authSetting['scope.camera']) {
          // 未授权系统相机，则请求授权
          wx.authorize({
            scope: 'scope.camera'
          })
        }
      }
    })
  },
  faceContrast: function () {
    wx.showLoading({
      title: '身份识别中',
      mask: true,
    })
    var that = this
    var ctx = wx.createCameraContext()
    ctx.takePhoto({
      quality: 'high',
      success: function(res){
        var tempImagePath = res.tempImagePath
        that.setData({
          userSrc: tempImagePath
        })
        var token = wx.getStorageSync('token')
        var openid = wx.getStorageSync('openid')
        wx.uploadFile({
          url: 'https://www.whohim.top/image/face_contrast/',
          filePath: tempImagePath,
          name: 'user_photo',
          header: {
            "content-type": 'application/x-www-form-urlencoded'
          },
          formData: {
            openid: openid,
            token: token
          },
          success: function (res) {
            wx.hideLoading()
            var response = JSON.parse(res.data)
            console.log('识别结果:',response)
            if (response.status == 0){
              setTimeout(function(){
                wx.showModal({
                  title: response.msg,
                  content: '是否执行开门',
                  success: function (res) {
                    if (res.confirm) {
                      // 请求开门接口
                      wx.request({
                        url: 'https://www.whohim.top/module/door',
                        data: {
                          token: token
                        },
                        method: 'POST',
                        header: {
                          "content-type": 'application/x-www-form-urlencoded'
                        },
                        success: function(res){
                          console.log(res.data)
                          if(res.data.status == 0){
                            wx.showToast({
                              title: res.data.msg,
                              icon: 'success',
                              duration: 2000
                            })
                          }else{
                            wx.showToast({
                              title: res.data.msg,
                              image: '../../images/icon/error.png',
                              duration: 2000
                            })
                          }
                          setTimeout(function(){
                            wx.reLaunch({
                              url: '../control/control',
                            })
                          },2000)
                        }
                      })
                    }
                  }
                })
              },1000)
            }else{
              if(response.status == 1){
                wx.showToast({
                  title: response.msg,
                  image: '../../images/icon/error.png',
                  duration: 2000
                }) 
              }else{
                if(response.status == 2){
                  wx.showToast({
                    title: response.msg,
                    image: '../../images/icon/error.png',
                    duration: 2000
                  })
                  setTimeout(function () {
                    wx.redirectTo({
                      url: 'input_img',
                    })
                  }, 2000) 
                }else{
                  wx.showToast({
                    title: '登陆过期',
                    image: '../../images/icon/error.png',
                    duration: 2000
                  })
                  setTimeout(function () {
                    wx.redirectTo({
                      url: '../login/login',
                    })
                  }, 2000) 
                }           
              }
            }
          }
        })
      }
    })
  }
})
