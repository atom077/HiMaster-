//backPwd.js
//获取应用实例
var app = getApp();
Page({
  data: {
    phoneNumber: null,
    msgCode: null,
    time: '点击获取',
    currentTime: 60,
    captcha: null
  },
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
    });
  },
  phoneInput: function (event) {
    this.setData({
      phoneNumber: event.detail.value
    })
  },
  msgCodeInput: function (event) {
    this.setData({
      msgCode: event.detail.value
    })
  },
  sendMsg: function (options) {
    var that = this;
    var currentTime = that.data.currentTime
    var phoneNumber = that.data.phoneNumber
    if (phoneNumber != null) {
      wx.request({
        url: 'https://www.whohim.top/user/forget_get_captcha',  // 请求找回短信验证码接口
        data: {
          phone: phoneNumber
        },
        method: 'POST',
        header: {
          "content-type": 'application/x-www-form-urlencoded'
        },
        success: function (res) {
          if (res.data.status == 0) {
            that.setData({
              captcha: res.data.data
            })
            console.log('获取验证码发送成功')
          }
        }
      })
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
    } else {
      wx.showToast({
        title: '请输入手机号',
        image: '../../images/icon/error.png',
        duration: 2000
      })
    }
  },
  confirmBtnClick: function(){
    var phoneNumber = this.data.phoneNumber
    var msgCode = this.data.msgCode
    var captcha = this.data.captcha
    if (msgCode != null){
      if (msgCode == captcha){
        wx.redirectTo({
          url: 'setPwd?phone='+phoneNumber +'captcha='+captcha,
        })
      }else{
        wx.showToast({
          title: '验证码错误',
          image: '../../images/icon/error.png',
          duration: 2000
        })
      } 
    }else{
      wx.showToast({
        title: '请输入验证码',
        image: '../../images/icon/error.png',
        duration: 2000
      })
    }
  }
})