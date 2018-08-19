// pages/mine/bind.js
const app = getApp()
Page({
  data: {
    array: ['智能门锁', '智能灯具', '智能插座'],
    machineType: {
      '智能门锁': 'door',
      '智能灯具': 'light',
      '智能插座': 'socket'
    },
    machineID: '',
    phone: '',
    index: 0
  },
  onLoad: function (options) {
  },
  scanCode: function(){
    var that = this
    wx.scanCode({
      success: function(res){
        var result = res.result
        that.setData({
          machineID: result
        })
      }
    })
  },
  phoneInput: function(event){
    this.setData({
      phone: event.detail.value
    })
  },
  idInput: function(event){
    this.setData({
      machineID: event.detail.value
    })
    wx.setStorageSync('doorID', event.detail.value)
  },
  bindPickerChange: function (e) {
    this.setData({
      index: e.detail.value
    })
  },
  confirm: function(){
    var phone = this.data.phone
    var index = this.data.index
    var name = this.data.array[index]
    var machineType = this.data.machineType[name]
    console.log('设备类型:',machineType)
    var machineID = this.data.machineID
    wx.setStorageSync('userPhone', phone)
    wx.setStorageSync(machineType, machineID)
    // 请求绑定设备接口
    wx.request({
      url: 'https://www.whohim.top/user/bind_raspberryPie',
      data:{
        phone: phone,
        raspberrypie: machineID
      },
      header: {
        "content-type": 'application/x-www-form-urlencoded'
      },
      method: 'POST',
      success: function(res){
        console.log(res.data)
        if(res.data.status == 0){
          var token = res.data.data
          wx.setStorageSync('token',token)
          wx.showToast({
            title: res.data.msg,
            icon: "success",
            duration: 1500,
          })
          setTimeout(function () {
            wx.reLaunch({
              url: '../index/index',
            })
          }, 2500)
        }else{
          wx.showToast({
            title: '登录过期',
            image: '../../images/icon/error.png',
            duration: 1500,
          })
          setTimeout(function(){
            wx.redirectTo({
              url: '../login/login',
            })
          },1500)
        }
      }
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