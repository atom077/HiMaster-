var util = require('../../utils/util.js')
//获取应用实例
var app = getApp()
Page({
  data: {
    tempFilePath: null,
    tempData: [{
      id: '',
      msg: '',
      dateIme: '',
      toview: ''
    }],
    nickName: '',
    avatarUrl: '',
    voice_hidden: false,
    input_hidden: true ,
    toview: 'last',
    view_count: 0,
    inputValue: ''
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var userInfo = wx.getStorageSync('userInfo')
    var weatherInfo = wx.getStorageSync('weatherInfo')
    this.setData({
      weatherInfo: weatherInfo,
      nickName: userInfo.nickName,
      avatarUrl: userInfo.avatarUrl,
      dateTime: (util.formatTime(new Date)).split(' ')[1]
    })
  },


  change_show: function(res){
    var that = this
    var voice_hidden = that.data.voice_hidden
    var input_hidden = that.data.input_hidden
    that.setData({
      voice_hidden: input_hidden,
      input_hidden: voice_hidden
    })
  },
  /**
   * 监听语音按钮长按
   */
  voice_longtap: function(res){
    wx.showLoading({
      title: '录音中',
      mask: true,
    })
    var that = this
    var token = wx.getStorageSync('token')
    var tempData = that.data.tempData
    var i = that.data.view_count 
    wx.startRecord({
      success: function (res) {
        var tempFilePath = res.tempFilePath
        wx.uploadFile({
          url: 'https://www.whohim.top/voice/upload_audio',
          header: {
            "content-type": 'application/x-www-form-urlencoded'
          },
          filePath: res.tempFilePath,
          name: 'file',
          formData: {
            token: token
          }, 
          success: function(res){
            var response = JSON.parse(res.data)
            var last_i = i++
            console.log(response)
            tempData.push({
              id: '1',
              msg: response.msg,
              dateTime: (util.formatTime(new Date)).split(' ')[1],
              toview: 'last_id' + last_i
            })
            that.setData({
              tempData: tempData,
              view_count: last_i,
              toview: 'last_id' + last_i
            })
            var last_i = i++
            tempData.push({
              id: '2',
              msg: response.data,
              dateTime: (util.formatTime(new Date)).split(' ')[1],
              toview: 'last_id' + last_i
            })
            that.setData({
              tempData: tempData,
              view_count: last_i,
              toview: 'last_id' + last_i
            })
            wx.showToast({
              title: '语音识别中',
              icon: 'loading',
              duration: 2000,
              mask: true
            })
            wx.downloadFile({
              url: 'https://www.whohim.top/voice/download_audio',
              header: {
                "content-type": 'multipart/form-data'
              },
              success: function (res) {
                // 只要服务器有响应数据，就会把响应内容写入文件并进入 success 回调，业务需要自行判断是否下载到了想要的内容
                if (res.statusCode === 200) {
                  const innerAudioContext = wx.createInnerAudioContext()
                  innerAudioContext.autoplay = true
                  innerAudioContext.src = res.tempFilePath

                  innerAudioContext.onPlay(() => {
                    console.log('音频播放')
                  })

                  innerAudioContext.onStop(() => {
                    console.log('音频停止')
                    innerAudioContext.stop()
                    //播放停止，销毁该实例
                    innerAudioContext.destroy()
                  })

                  innerAudioContext.onEnded(() => {
                    console.log('音频自然播放结束')
                    innerAudioContext.destroy()
                  })

                  innerAudioContext.onError((res) => {
                    console.log('音频播放错误', res.errMsg)
                    innerAudioContext.destroy()
                  })
                }
              }
            })
          }
        })
      }
    })
  },

   /**
   * 监听语音按钮点击完成
   */
  voice_tap: function(res){
    wx.stopRecord()
    console.log('录音结束')
    setTimeout(function(res){
      wx.hideLoading()
    },2000)
  },

   /**
   * 监听输入框输入
   */
  text_input: function(e){
    this.setData({
      inputValue: e.detail.value
    })
  },

   /**
   * 监听发送按钮
   */
  send_btn: function(res){
    var that = this
    var info = that.data.inputValue
    that.setData({
      inputValue: ''
    })
    console.log(info)
    var tempData = that.data.tempData
    var i = that.data.view_count 
    var last_i = i++
    tempData.push({
      id: '1',
      msg: info,
      dateTime: (util.formatTime(new Date)).split(' ')[1],
      toview: 'last_id' + last_i
    })
    that.setData({
      tempData: tempData,
      view_count: last_i,
      toview: 'last_id' + last_i
    })
    wx.request({
      url: 'https://www.whohim.top/voice/chat?info=' + info,
      header: {
        'Content-Type': 'application/json'
      },
      success: function(res){
        console.log(res.data.data)
        var last_i = i++
        tempData.push({
          id: '2',
          msg: res.data.data,
          dateTime: (util.formatTime(new Date)).split(' ')[1],
          toview: 'last_id' + last_i
        })
        that.setData({
          tempData: tempData,
          view_count: last_i,
          toview: 'last_id' + last_i
        })
        wx.downloadFile({
          url: 'https://www.whohim.top/voice/download_audio',
          header: {
            "content-type": 'multipart/form-data'
          },
          success: function(res){
            if (res.statusCode === 200) {
              const innerAudioContext = wx.createInnerAudioContext()
              innerAudioContext.autoplay = true
              innerAudioContext.src = res.tempFilePath

              innerAudioContext.onPlay(() => {
                console.log('音频播放')
              })

              innerAudioContext.onStop(() => {
                console.log('音频停止')
                innerAudioContext.stop()
                //播放停止，销毁该实例
                innerAudioContext.destroy()
              })

              innerAudioContext.onEnded(() => {
                console.log('音频自然播放结束')
                innerAudioContext.destroy()
              })

              innerAudioContext.onError((res) => {
                console.log('音频播放错误', res.errMsg)
                innerAudioContext.destroy()
              })
            }
          }
        })
      }
    })
  },
})