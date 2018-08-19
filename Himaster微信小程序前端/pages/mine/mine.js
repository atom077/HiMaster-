// pages/mine/mine.js
const app = getApp()
Page({
  data: {
    userInfo: app.globalData.userInfo,
    avatarUrl: '',
    nickName: '',
    userAuth: ''
  },
  onLoad: function (options) {
    var that = this
    var userAuth = wx.getStorageSync('userAuth')
    that.setData({
      userAuth: userAuth
    })
    console.log('userAuth', userAuth)
    if (app.globalData.userInfo) {
      // 如果已经获取到数据
      that.setData({
        userInfo: app.globalData.userInfo,
        avatarUrl: app.globalData.userInfo.avatarUrl,
        nickName: app.globalData.userInfo.nickName,
      })
    } else {
      // 未获取到数据，执行回调函数，确保正确取值
      app.userInfoReadyCallback = function () {
        that.setData({
          userInfo: app.globalData.userInfo,
          avatarUrl: app.globalData.userInfo.avatarUrl,
          nickName: app.globalData.userInfo.nickName,
        })
      }
    }
  },
  authUserInfo: function(){
    wx.navigateTo({
      url: '../index/auth',
    })
  },
  //绑定设备
  bindMachine: function(){
    var userAuth = wx.getStorageSync('userAuth')
    if (!userAuth) {
      wx.showModal({
        title: '尚未登录，无法绑定',
        content: '前往登录',
        success: function (res) {
          if (res.confirm) {
            // wx.redirectTo({
            //   url: '../login/login',
            // })
            wx.navigateTo({
              url: '../login/login',
            })
          }
        }
      })
    }else{
      wx.navigateTo({
        url: 'bind'
      })
    }
  },
  //人脸录入
  inputFace: function(){
    var userAuth = wx.getStorageSync('userAuth')
    if (!userAuth) {
      wx.showModal({
        title: '尚未登录，无法录入人脸',
        content: '前往登录',
        success: function (res) {
          if (res.confirm) {
            wx.navigateTo({
              url: '../login/login',
            })
            // wx.redirectTo({
            //   url: '../login/login',
            // })
          }
        }
      })
    } else {
      wx.navigateTo({
        url: '../AI_face/input_img',
      })
    }
  },
  //登录入口
  login: function(){
    wx.navigateTo({
      url: '../login/login',
    })
  },
  //查看我的步数
  mineStep: function(){
    wx.navigateTo({
      url: '../bar/step',
    })
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