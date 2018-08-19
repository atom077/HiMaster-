//register.js
//获取应用实例
var app = getApp();
Page({
  data: {
    currentTime: 60,
    time: '点击获取',
    disabled: false,
    phoneNumber: null,
    password: null,
    rePassword: null,
    msgCode: null,
    openid: app.globalData.openid
  },
  phoneInput: function(event){
    this.setData({
      phoneNumber: event.detail.value
    })
  },
  passwdInput: function (event) {
    this.setData({
      password: event.detail.value
    })
  },
  rePasswdInput: function (event) {
    this.setData({
      rePassword: event.detail.value
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
    }, 100)
    if (phoneNumber != null) {
      wx.request({
        url: 'https://www.whohim.top/user/captcha',
        data: {
          phone: phoneNumber
        },
        method: 'POST',
        header: {
          "content-type": 'application/x-www-form-urlencoded'
        },
        success: function (res) {
          if (res.data.status == 0) {
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
              
              duration: 2000
            })
          }
        }
      })
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
    var passwd = this.data.password
    var rePasswd = this.data.rePassword
    var msgCode = this.data.msgCode
    var openid = this.data.openid
    if (phoneNumber != null && passwd != null && rePasswd != null && msgCode != null){
      if (passwd == rePasswd){
        wx.request({
          url: 'https://www.whohim.top/user/register',
          data: {
            phone: phoneNumber,
            password: passwd,
            captcha: msgCode,
            openid: openid
          },
          method: 'POST',
          header: {
            "content-type": 'application/x-www-form-urlencoded'
          },
          success: function (res) {
            if (res.data.status == 0) {
              wx.showToast({
                title: '注册成功！',
                icon: "success",
                duration: 2000
              }),
              wx.redirectTo({
                url: '../login/login',
              })
            } else {
              console.log(res.data.msg)
              wx.showToast({
                title: "错误",
                image: '../../images/icon/error.png',
                duration: 2000
              })
            }
          }
        })
      }else{
        wx.showToast({
          title: '密码不一致',
          image: '../../images/icon/error.png',
          duration: 2000
        })
      }
    }else{
      wx.showToast({
        title: '请输入完整信息',
        image: '../../images/icon/error.png',
        duration: 2000
      })
    }
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var that = this
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