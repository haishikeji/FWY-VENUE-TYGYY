<template>
  <view class="userSetID">
    <!-- 基础信息 -->
    <view class="base-info">
      <!-- 头像 -->
      <view class="avata">
        <text>头像</text>
        <!-- <img src="../../static/app-logo.png"> -->
        <!-- <u-upload :custom-btn="true">
        	<view slot="addBtn" class="slot-btn" hover-class="slot-btn__hover" hover-stay-time="150">
        		<u-icon name="account-fill" size="50" :color="$u.color['lightColor']"></u-icon>
        	</view>
        </u-upload> -->
        <u-avatar :src=" baseAvatar ? ShowAction+baseAvatar : avatar " size="default" @click="upLoadAvatar('avatar',1)"></u-avatar>
      </view>

      <!-- 其他信息 -->
      <view class="single-fun avata" v-for="(item,index) in otherFunctionList" :key="index"
        @click="morePages(item.tit)">
        <text class="star" v-if="item.name !== '地址'  && item.name !== '出生日期' ">*</text>
        <text>{{item.name}}</text>
        <view>
          <text class="info" v-if="!inputShow">{{item.tit}}</text>
          
          <u-input v-if="inputShow && item.name=== '昵称' " v-model="item.tit" type="text" input-align="right" :border="true" />
          <u-input style="line-height: 70rpx;" v-if="inputShow && item.name=== '性别' " v-model="item.tit" type="select" input-align="right" :border="true"  @click="sexChoice" />
          <u-input style="line-height: 70rpx;" label-width="120rpx" v-if="inputShow && item.name=== '出生日期' " v-model="item.tit" type="select" input-align="right" :border="true" @click="isBirthday = true" />
          <u-input v-if="inputShow && item.name=== '手机号' " v-model="item.tit" type="number" input-align="right" :border="true" />
          <u-input v-if="inputShow && item.name=== '地址' " v-model="item.tit" type="text" input-align="right" :border="true" />
          <u-icon v-if="!inputShow" name="arrow-right" color="#000" size="28"></u-icon>

          <u-select v-if="inputShow && item.name=== '性别' " v-model="sexSelectShow" :list="sexSelectList" @confirm="sexSelect"></u-select>
          <u-picker mode="time" v-model="isBirthday" :safe-area-inset-bottom="true" :params="timeParams" @confirm="choiceBirthday"></u-picker>
          
        </view>
      </view>

      <view class="check-ID" v-if="inputShow" type="default" @click="saveInfo">保存</view>
    </view>

  </view>


</template>

<script>
  import utils from "@/utils/Base.js"
  export default {
    data() {
      return {
        inputShow: false,
        sexSelectShow: false,
        sexSelectList: [{
            value: '1',
            label: '男'
          },
          {
            value: '2',
        		label: '女'
          }
        ],// 性别选项卡
        sexValue:{}, // 性别值
        isBirthday:false, // 生日选择器
        timeParams: {
          year: true,
          month: true,
          day: true,
          hour: false,
          minute: false,
          second: false
        }, //时间选择器
        action: `${uni.$http.baseUrl}/core/file/upload`, //图片上传接口
        ShowAction: `${uni.$http.baseUrl}/core/file/download?base=`, // 图片下载预览地址, // 图片显示接口
        avatar:'',// 显示头像
        baseAvatar: '', //文件头像
        otherFunctionList: [{
            name: '昵称',
            tit: ''
          },
          {
            name: '性别',
            tit:'',
          },
          {
            name: '出生日期',
            tit: ''
          },
          {
            name: '手机号',
            tit: ''
          },
          {
            name: '地址',
            tit: ''
          },
        ], // 基础信息
      };
    },

    onLoad() {
      this.getUserInfo()
    },

    methods: {
      //获取最新用户信息
      getUserInfo() {
         uni.$http.get('/user/member/getUserInfo', {
           openid: uni.getStorageSync('openid')
         }).then(res=>{
           const {
             code,
             message,
             data
           } = res.data
           const userInfo = data.member
           this.baseAvatar = userInfo.avatar // 自定义头像文件流
           this.avatar = uni.getStorageSync('WXUserInfo').avatarUrl // 微信头像
           this.otherFunctionList[0].tit = userInfo.name ? userInfo.name : ''
           this.otherFunctionList[1].tit = userInfo.gender=== 1 ? '男' : '女'
           this.otherFunctionList[2].tit = userInfo.birthday ? userInfo.birthday : ''
           this.otherFunctionList[3].tit = userInfo.phone ? userInfo.phone : ''
           this.otherFunctionList[4].tit = userInfo.address ? userInfo.address : ''
         })
         // const userInfo = JSON.parse(uni.getStorageSync('member')) 
      },
      
      // 头像上传
      upLoadAvatar(type, num) {
        this.inputShow = true
        const that = this;
        uni.chooseImage({
          count: num,
          sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
          sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
          success(res) {
            // tempFilePath可以作为img标签的src属性显示图片
            const tempFilePaths = res.tempFilePaths
            for (let i = 0; i < tempFilePaths.length; i += 1) {
              uni.uploadFile({
                url: that.action, //接口地址
                filePath: tempFilePaths[i],
                name: 'file',
                header: { // 我的 HTTP 请求中需要token，视情况而定是否需要header
                  'Content-Type': 'application/x-www-form-urlencoded',
                  Authorization: 'Basic ' + utils.base64_encode('wxapp:QdRsGE'),
                  'User-Type': 'wx',
                  'jpower-auth': uni.getStorageSync('userInfo').tokenType + ' ' + uni.getStorageSync('userInfo').accessToken ||
                    '',
                  'Menu-Code': 'wxapp'
                },
                formData: { // HTTP 请求中其他额外的 form data
                  file: tempFilePaths[i],
                },
                // 上传成功后相关操作
                success(res) {
                  const base = JSON.parse(res.data)
                  if (type === 'avatar' && num === 1) {
                    that.avatar = tempFilePaths[0];
                    that.baseAvatar = base.data
                  } // 头像
                  // console.log(that.form.swiperList)
                },
                fail() {
                  uni.showToast({
                    title: '上传失败，请重试', //弹框内容
                    icon: 'fail', //弹框模式
                    duration: 2000 //弹框显示时间
                  })
                },
              })
            }
          },
        })
      },

      // 编辑
      morePages(item) {
        console.log(item)
        this.inputShow = true
      },

      // 性别选择
      sexChoice() {
        this.sexSelectShow = true
      },
      // 性别选择
      sexSelect(e) {
        this.sexValue = e[0]
        this.otherFunctionList[1].tit = this.sexValue.label
      },
      
      // 选择生日
      choiceBirthday(e) {
        this.otherFunctionList[2].tit = e.year + '-' + e.month + '-' + e.day 
        console.log(e)
      },

      // 保存更新个人信息
      async saveInfo() {
        this.inputShow = false
        // console.log(this.otherFunctionList)
       const res = await uni.$http.put('/user/member/update', {
          requestFormType: true,
          id:JSON.parse(uni.getStorageSync('member')).id ,
          avatar: this.baseAvatar ? this.baseAvatar : '' ,//头像
          name: this.otherFunctionList[0].tit ? this.otherFunctionList[0].tit : '',
          gender: this.otherFunctionList[1].tit === '男'? 1 : 2,
          birthday: this.otherFunctionList[2].tit ? this.otherFunctionList[2].tit : '',
          phone: this.otherFunctionList[3].tit ? this.otherFunctionList[3].tit : '',
          address: this.otherFunctionList[4].tit ? this.otherFunctionList[4].tit : ''
        })
        const {code,data,message} = res.data
        console.log(code)
        if (code === 200) {
          uni.showToast({
            title: '保存成功！', //弹框内容
            icon: 'success', //弹框模式
            duration: 1500 //弹框显示时间
          })
          // setTimeout(()=>{
          //   uni.switchTab({
          //     url:'/pages/my/my'
          //   })
          // },1500)
        } 
        if(code !== 200){
          uni.showToast({
            title: message, //弹框内容
            icon: 'none', //弹框模式
            duration: 2000 //弹框显示时间
          })
          this.inputShow = true
        }
       
      },

    }
  }
</script>

<style lang="scss">
  page {
    background-color: #f4f5f4;
  }

  // 基础信息
  .base-info {
    width: 100%;
    // height: 800rpx;
    background-color: #fff;
    margin-top: 20rpx;
    box-sizing: border-box;
    padding: 0 30rpx;

    .avata {
      width: 100%;
      height: 160rpx;
      // line-height: 160rpx;
      font-size: 30rpx;
      font-weight: 600;
      box-sizing: border-box;
      padding: 0 30rpx;
      border-bottom: 1px solid #f3f3f3;
      display: flex;
      align-items: center;
      justify-content: space-between;
      // .slot-btn {
      // 	width: 120rpx;
      // 	height: 120rpx;
      // 	display: flex;
      // 	justify-content: center;
      // 	align-items: center;
      // 	background: rgb(244, 245, 246);
      // 	border-radius: 50%;
      //   border: 1px solid #999;
      // }
      
      // .slot-btn__hover {
      // 	background-color: rgb(235, 236, 238);
      // }

      .info {
        font-weight: 500;
        font-size: 28rpx;
      }
    }

    .single-fun {
      position: relative;
      width: 100%;
      height: 110rpx;
      box-sizing: border-box;
      border-bottom: 1px solid #f3f3f3;
      line-height: 110rpx;
      padding: 0 30rpx;
      font-size: 30rpx;
      .star{
        position: absolute;
        left: 0;
        color: red;
      }
    }
  }
  .u-input__input{
    width: 300rpx;
  }
  // 退出编辑
  .check-ID {
    width: 100%;
    height: 100rpx;
    line-height: 100rpx;
    background-color: #fff;
    margin-top: 50rpx;
    font-size: 32rpx;
    font-weight: 600;
    text-align: center;
  }
</style>
