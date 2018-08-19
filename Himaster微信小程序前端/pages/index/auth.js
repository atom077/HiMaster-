const app = getApp()
Page({
  data: {
    stepInfo: app.globalData.stepInfo,
    userInfo: app.globalData.userInfo,
  },
  onLoad: function () {

  },
  onGotUserInfo: function (e) {
    console.log(e.detail.userInfo)
    app.globalData.userInfo = e.detail.userInfo
    if (this.userInfoReadyCallback) {
      this.userInfoReadyCallback(e.detail.userInfo)
    }
    setTimeout(function () {
      wx.reLaunch({
        url: '../index/index'
      })
    }, 1000)
  },
  onReady: function(){
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
  }
})