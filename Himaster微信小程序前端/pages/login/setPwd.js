//backPwd.js
//获取应用实例
var app = getApp();
Page({
  data: {
    passwd: null,
    rePasswd: null
  },
  onLoad: function(options){
    console.log(options)
    this.setData({
      phone: options.phone,
      captcha: options.captcha
    })
  },
  onReady: function (option) {
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
  passwdInput: function (event) {
    this.setData({
      passwd: event.detail.value
    })
  },
  rePasswdInput: function (event) {
    this.setData({
      rePasswd: event.detail.value
    })
  },
  confirmBtnClick: function () {
    var phone = this.data.phone
    var captcha = this.data.captcha
    var passwd = this.data.passwd
    var rePasswd = this.data.rePasswd
    if (passwd != null && rePasswd != null) {
      if(passwd == rePasswd){
        wx.request({
          url: 'https://www.whohim.top/user/forget_reset_password',
          data: {
            phone: phone,
            passwordNew: passwd,
            captcha: captcha
          },
          method: 'POST',
          header: {
            "content-type": 'application/x-www-form-urlencoded'
          },
          success: function(res){
            if (res.data.status == 0){
              wx.showToast({
                title: res.data.msg,
                icon: "success",
                duration: 2000
              })
              wx.redirectTo({
                url: 'login',
              })
            }else{
              wx.showToast({
                title: res.data.msg,
                images: '../../images/icon/error.png',
                duration: 2000
              })
            }
          }
        })
      } else {
        wx.showToast({
          title: '密码不一致',
          image: '../../images/icon/error.png',
          duration: 2000
        })
      }
    }else{
      wx.showToast({
        title: '请输入新密码',
        image: '../../images/icon/error.png',
        duration: 2000
      })
    } 
  }
})