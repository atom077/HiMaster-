// pages/AI_face/input_img.js
const app = getApp()
Page({
  data: {
    presrc_arr: [],
    adminNickName: '',
    face_list_hide: true,
    openid: ''
  },
  onLoad: function (options) {
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
  },
  takePhoto: function(){
    var ctx = wx.createCameraContext()
    var that = this
    var token = wx.getStorageSync('token')
    var openid = wx.getStorageSync('openid')
    ctx.takePhoto({
      quality: 'high',
      success: (res) => {
        var tempImagePath = res.tempImagePath
        that.setData({
          presrc_arr: that.data.presrc_arr.concat(tempImagePath),
          face_list_hide: false 
        })
        wx.uploadFile({
          url: 'https://www.whohim.top/image/admin_upload',
          filePath: tempImagePath,
          name: 'user_face',
          formData: {
            token: token,
            openid: openid
          },
          header: {
            "content-type": 'application/x-www-form-urlencoded'
          },
          success: function (res) {
            var data = res.data
            console.log(data)
          }
        })
      }
    })
  },
  adminNameInput: function(event){
    this.setData({
      adminNickName: event.detail.value
    })
  },
  save_face: function(){
    var adminNickName = this.data.adminNickName
    var openid = wx.getStorageSync('openid')
    wx.request({
      url: 'https://www.whohim.top/image/save_adminFace',
      header: {
        "content-type": 'application/x-www-form-urlencoded'
      },
      data: {
        openid: openid
      },
      method: 'POST',
      success: function (res) {
        console.log(res.data)
        if(res.data.status == 0){
          wx.showToast({
            title: res.data.msg,
            icon: 'success',
            duration: 2000
          })
          setTimeout(function () {
            wx.navigateBack({
              delta: '1'
            })
          }, 2000)
        }else{
          wx.showToast({
            title: '录入失败',
            image: '../../images/icon/error.png',
            duration: 2000
          })
        }
      }
    })
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