//register.js
//获取应用实例
var app = getApp();
Page({
  data: {
    currentTime: 60,
    time: '点击获取',
    disabled: false,
    passwdHide: false,    // 密码输入框隐藏
    msgHide: true,        // 短信输入框隐藏
    passwdLoginHide: true,     // 密码登录按钮切换隐藏
    msgLoginHide: false,       // 短信登录按钮切换隐藏
    phoneNumber: null,
    password: null,
    msgCode: null,
    openid: app.globalData.openid,
  },

  changeLogin: function(options){
    var that = this;
    var passwdHide = that.data.passwdHide
    var msgHide = that.data.msgHide
    var passwdLoginHide = that.data.passwdLoginHide
    var msgLoginHide = that.data.msgLoginHide
    that.setData({
      passwdHide: msgHide,
      msgHide: passwdHide,
      passwdLoginHide: msgLoginHide,
      msgLoginHide: passwdLoginHide
    })
  },
  sendMsg: function (options) {
    var that = this;
    var currentTime = that.data.currentTime
    var phoneNumber = that.data.phoneNumber
    if (phoneNumber != null){
      var interval = setInterval(function () {
        currentTime--;
        that.setData({
          time: currentTime + '秒',
          disabled: true,
        });
        if (currentTime == 0) {
          clearInterval(interval)
          that.setData({
            time: '重新获取',
            currentTime: 60,
            disabled: false
          })
        }
      }, 1000)
      wx.request({
        url: 'https://www.whohim.top/user/captcha',  // 请求短信验证码接口
        data:{
          phone: phoneNumber  
        },
        method: 'POST',
        header: {
          "content-type": 'application/x-www-form-urlencoded'
        },
        success: function(res){
          if (res.data.status == 0){
            console.log('获取验证码发送成功')
          } else {
            clearInterval(interval)
            that.setData({
              time: '重新获取',
              currentTime: 60,
              disabled: false
            })
            wx.showToast({
              title: res.data.msg,
              image: '../../images/icon/error.png',
              duration: 2000
            })
          }
        }
      })
    }else{
      wx.showToast({
        title: '请输入手机号',
        image: '../../images/icon/error.png',
        duration: 2000
      })
    }
  },
  phoneInput: function(event){
    this.setData({
      phoneNumber: event.detail.value
    })
  },
  passwdInput: function(event){
    this.setData({
      password: event.detail.value
    })
  },
  msgCodeInput: function(event){
    this.setData({
      msgCode: event.detail.value
    })
  },
  forgetPasswd: function(){
    wx.navigateTo({
      url: 'backPwd',
    })
  },
  register: function(){
    wx.redirectTo({
      url: '../register/register',
    })
  },
  confirmBtnClick: function(){
    var phoneNumber = this.data.phoneNumber
    var passwd = this.data.password
    var msgCode = this.data.msgCode
    var openid = this.data.openid
    if (this.data.passwdHide == false){
      // 选择密码登录
      if (phoneNumber == null || passwd == null){
        wx.showToast({
          title: '请输入密码和手机',
          image: '../../images/icon/error.png',
          duration:2000
        })
      }else{
        wx.request({
          url: 'https://www.whohim.top/user/login',
          method: 'POST',
          data: {
            phone: phoneNumber,
            password: passwd
          },
          header:{
            "content-type": 'application/x-www-form-urlencoded'
          },
          success: function(res){
            console.log(res.data)
            if (res.data.status == 0){
              // 登录成功
              console.log('登录成功返回',res.data)
              var token = res.data.data['token']
              wx.setStorageSync('token', token)
              wx.setStorageSync('userAuth', 1)
              wx.showToast({
                title: res.data.msg,
                icon: 'success',
                duration: 1000
              })
              setTimeout(function(){
                wx.navigateBack({
                  delta: 1
                })
                // wx.reLaunch({
                //   url: '../control/control',
                // })
                // wx.switchTab({
                //   url: '../control/control',
                // })
              },1000)
            }else{
              if (res.data.status == 1) {
                wx.showModal({
                  title: '前往注册',
                  content: res.data.msg,
                  showCancel: false,
                  success: function (res) {
                    if (res.confirm) {
                      wx.redirectTo({
                        url: '../register/register',
                      })
                    }
                  }
                })
              }
              else {
                // re.data.status == 2 "密码错误"
                wx.showToast({
                  title: res.data.msg,
                  image: '../../images/icon/error.png',
                  duration: 2000
                })
              } 
            }        
          }
        })
      }
    }else{
      // 密码框隐藏，选择短信登录
      if (msgCode == null){
        wx.showToast({
          title: '请输入验证码',
          image: '../../images/icon/error.png',
          duration: 2000
        })
      }else{ 
        wx.request({
          url: 'https://www.whohim.top/user/login_captcha',  //短信验证接口
          method: 'POST',
          data: {
            phone: phoneNumber,
            captcha: msgCode
          },
          header: {
            "content-type": 'application/x-www-form-urlencoded'
          },
          success: function (res) {
            console.log(res.data)
            if (res.data.status == 0) {
              var token = res.data.data['token']
              wx.setStorageSync('token', token)
              wx.setStorageSync('userAuth', 1)
              wx.showToast({
                title: '登录成功',
                icon: "success",
                duration: 1000
              })
              setTimeout(function () {
                wx.switchTab({
                  url: '../mine/mine',
                })
              }, 1000)
            }else{
              console.log(res.data.msg)
              wx.showToast({
                title: res.data.msg,
                image: '../../images/icon/error.png',
                duration: 2000
              })
            }
          }
        })
      }
    }
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var that = this;
    if (app.globalData.openid) {
      // 如果已经获取到数据
      that.setData({
        openid: app.globalData.openid
      })
    } else {
      // 未获取到数据，执行回调函数，确保正确取值
      app.openidReadyCallback = function () {
        that.setData({
          openid: app.globalData.openid
        })
      }
    }
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
    var _this = this;
    wx.onAccelerometerChange(function (res) {
      var angle = -(res.x * 30).toFixed(1);
      if (angle > 14) { angle = 14; }
      else if (angle < -14) { angle = -14; }
      if (_this.data.angle !== angle) {
        _this.setData({
          angle: angle
        });
      }
    })
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {
    
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {
    
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {
    
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {
    
  }
})