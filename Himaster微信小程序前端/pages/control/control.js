const app = getApp()
Page({
  data: {
    userInfo: app.globalData.userInfo,
    avatarUrl: '',
    nickName: '',
    phone: '',
    token: '',
    on_hidden: false,
    off_hidden: true
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var that = this
    var phone = wx.getStorageSync('userPhone')
    var token = wx.get
    if(app.globalData.userInfo){
      // 如果已经获取到数据   
      that.setData({
        userInfo: app.globalData.userInfo,
        avatarUrl: app.globalData.userInfo.avatarUrl,
        nickName: app.globalData.userInfo.nickName,
      })
    }else{
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
  open_door: function(){
    var phone = wx.getStorageSync('userPhone')
    var phone = this.data.phone
    var token = wx.getStorageSync('token')
    if (!token) {
      wx.showModal({
        title: '登录过期',
        content: '请前往登录',
        success: function (res) {
          if (res.confirm) {
            wx.navigateTo({
              url: '../login/login',
            })
          }
        }
      })
    } else {
      wx.navigateTo({
        url: '../AI_face/AI_face?token=' + token
      })
    }  
  },
  open_light: function () {
    var that = this
    var on_hidden = that.data.on_hidden
    var off_hidden = that.data.off_hidden
    that.setData({
      on_hidden: off_hidden,
      off_hidden: on_hidden
    })
    var token = wx.getStorageSync('token')
    if (!token) {
      wx.showModal({
        title: '登录过期',
        content: '请前往登录', 
        success: function (res) {
          if (res.confirm) {
            wx.navigateTo({
              url: '../login/login',
            })
          }
        }
      })
    } else {
      wx.request({
        url: 'https://www.whohim.top/module/led',
        data: {
          token: token
        },
        method: 'POST',
        header: {
          "content-type": 'application/x-www-form-urlencoded'
        },
        success: function (res) {
          console.log(res.data)
          if(res.data.status == 0){
            wx.showToast({
              title: res.data.msg,
              icon: 'success',
              duration: 2000
            })
          }else{
            if(res.data.status){
              wx.showToast({
                title: res.data.msg,
                image: '../../images/icon/error.png',
                duration: 2000
              })
            }else{
              wx.showToast({
                title: '登录过期',
                image: '../../images/icon/error.png',
                duration: 2000
              })
              setTimeout(function(){
                wx.navigateTo({
                  url: '../login/login',
                })
              },2000)
            }
          }
        }
      })
    }
  },
  AI_socket: function () {
    var token = wx.getStorageSync('token')
    if (!token) {
      wx.showModal({
        title: '未绑定设备',
        content: '请前往绑定',
        success: function (res) {
          if (res.confirm) {
            wx.navigateTo({
              url: '../mine/bind',
            })
          }
        }
      })
    } else {
      wx.navigateTo({
        url: 'socket',
      })
    }
  },
  hi_chat: function () {
    wx.navigateTo({
      url: '../chat/chat',
    })
    // var token = wx.getStorageSync('token')
    // if (!token) {
    //   wx.showModal({
    //     title: '未绑定设备',
    //     content: '请前往绑定',
    //     success: function (res) {
    //       if (res.confirm) {
    //         wx.navigateTo({
    //           url: '../mine/bind',
    //         })
    //       }
    //     }
    //   })
    // } else {
    //   wx.navigateTo({
    //     url: '../chat/chat',
    //   })
    // }
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