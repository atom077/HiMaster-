//app.js
App({
  globalData: {
    openid: "",
    session_key: ''
  },

  onLaunch: function () {
    // 展示本地存储能力
    // var logs = wx.getStorageSync('logs') || []
    // logs.unshift(Date.now())
    // wx.setStorageSync('logs', logs)

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
            success: function(res){
              var session_key = res.data.session_key;
              var openid = res.data.openid
              wx.setStorageSync('openid', openid)
              that.globalData.openid = openid
              if (that.openidReadyCallback) {
                that.openidReadyCallback(openid)
              }
              that.globalData.session_key = session_key
              if (that.session_keyReadyCallback) {
                that.openidReadyCallback(session_key)
              }
            }
          })
        }
      }
    })
  }
})