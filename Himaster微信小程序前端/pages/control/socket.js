// pages/control/socket.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    array: ['开启','关闭'],
    time: '',
    hour: '',   //系统当前时间-小时
    minute: ''  //系统当前时间-分钟
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    
  },
  bindPickerChange: function (e) {
    this.setData({
      index: e.detail.value
    })
  },
  bindTimeChange: function (e) {
    var date = new Date
    var cur_timeStamp = date.getTime()
    var timeSelect = e.detail.value
    console.log('时间选择picker改变，携带值为', timeSelect)
    var timeSet = date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate() + ' ' + timeSelect
    timeSet = timeSet.substring(0, 19)
    timeSet = timeSet.replace(/-/g, '/')
    var set_timeStamp = new Date(timeSet).getTime()
    var time_count = set_timeStamp - cur_timeStamp
    this.setData({
      time: timeSelect,
      hour: date.getHours(),
      minutes: date.getMinutes(),
      cur_timeStamp: cur_timeStamp,
      set_timeStamp: set_timeStamp,
      time_count: time_count
    })
  },
  confirm: function(res){
    wx.showToast({
      title: '设置成功',
      icon: 'success',
      duration: 500
    })
    var token = wx.getStorageSync('token')
    var time_count = this.data.time_count
    console.log(time_count)
    setTimeout(function(){
      wx.request({
        url: 'https://www.whohim.top/module/smart_socket',
        data: {
          token: token
        },
        header: {
          "content-type": 'application/x-www-form-urlencoded'
        },
        method: 'POST',
        success: function (res) {
          console.log(res.data)
          if(res.data.status == 0){
            wx.showToast({
              title: '开关插座成功',
              icon: 'success',
              duration: 2000
            })
          }else{
            wx.showToast({
              title: '密钥过期',
              image: '../../images/icon/error.png',
              duration: 2000
            })
            setTimeout(function(){
              wx.redirectTo({
                url: '../login/login',
              })
            },2000)
          }
        }
      })
    },time_count)
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