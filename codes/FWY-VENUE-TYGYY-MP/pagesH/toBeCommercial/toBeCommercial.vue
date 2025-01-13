<template>
  <view class="issueSite">
    <u-form :model="form" ref="uForm" label-align="left" label-width="190">
      <u-form-item label="会员ID:" v-show="!form.memberId" prop="memberId">
        <u-input v-model="form.memberId" :disabled="true" />
      </u-form-item>
      <u-form-item label="用户名:" prop="loginId"  :required="true">
        <u-input v-model="form.loginId" />
      </u-form-item>
      <u-form-item label="昵称:" prop="nickName" :required="true">
        <u-input v-model="form.nickName" />
      </u-form-item>
      <u-form-item label="头像:" >
        <u-avatar :src="form.avatar" size="large" @click="upLoadAvatar('avatar',1)"></u-avatar>
      </u-form-item>
      <u-form-item label="电话:" prop="telephone" :required="true">
        <u-input v-model="form.telephone" />
      </u-form-item>
      <u-form-item label="出生日期:" prop="birthday"  :required="true">
        <u-input v-model="form.birthday" type="select" :select-open="birthdayShow" @click="birthdayShow = true" />
        <u-picker mode="time" v-model="birthdayShow" :safe-area-inset-bottom="true" :params="timeParams"
          @confirm="choicebirthday">
        </u-picker>
      </u-form-item>
      <u-form-item label="活动类型:">
        <u-input v-model="form.activityType.label" type="select" :select-open="activityTypeShow"
          @click="activityTypeShow = true" />
        <u-select v-model="activityTypeShow" :list="activeTypeList" @confirm="confirmActivityType"></u-select>
      </u-form-item>
      <u-form-item label="商家介绍:" prop="intrduce" >
        <u-input v-model="form.intrduce" />
      </u-form-item>
      <u-form-item label="详情描述:" prop="description" >
        <u-input v-model="form.description" />
      </u-form-item>
      <u-form-item label="联系人二维码:" >
        <u-image width="200rpx" height="200rpx" :src="form.QRCode" @click="upLoadAvatar('QRCode',1)"></u-image>
      </u-form-item>
      <u-form-item label="上传轮播图:" >
        <u-image v-for="(item,indedx) in form.swiperList" width="200rpx" height="200rpx" :src="item"
          @click="upLoadAvatar('swiper',5)"></u-image>
      </u-form-item>
      <u-form-item label="地址:"  :required="true">
        <u-input v-model="form.address" :readonly="form.address" @click.stop.prevent="getRegeo" />
      </u-form-item>
      <u-form-item label="位置经度:" :required="true">
        <u-input v-model="form.lon" :disabled="true" />
      </u-form-item>
      <u-form-item label="位置纬度:" :required="true">
        <u-input v-model="form.lat" :disabled="true" />
      </u-form-item>
      <u-form-item label="营业起始时间:" prop="startTime"  :required="true">
        <u-input v-model="form.startTime" type="select" :select-open="startDataShow" @click="startDataShow = true" />
        <u-picker mode="time" v-model="startDataShow" :safe-area-inset-bottom="true" :params="businessTimeParams"
          @confirm="choiceStartDataTime">
        </u-picker>
      </u-form-item>
      <u-form-item label="营业结束时间:" prop="endTime" :required="true">
        <u-input v-model="form.endTime" type="select" :select-open="endDataShow" @click="endDataShow = true" />
        <u-picker mode="time" v-model="endDataShow" :safe-area-inset-bottom="true" :params="businessTimeParams"
          @confirm="choiceEndDataTime">
        </u-picker>
      </u-form-item>
      <u-form-item label="营业执照:">
        <u-image width="200rpx" height="200rpx" :src="form.businessLicense" @click="upLoadAvatar('businessLicense',1)">
        </u-image>
      </u-form-item>
      <u-form-item label="银行卡号:" prop="cardNo" :required="true">
        <u-input v-model="form.cardNo" />
      </u-form-item>
      <u-form-item label="银行开户行:" prop="bank" :required="true">
        <u-input v-model="form.bank" />
      </u-form-item>
      <u-form-item label="持卡人姓名:" prop="cardholderName"  :required="true">
        <u-input v-model="form.cardholderName" />
      </u-form-item>

      <u-toast ref="uToast" />

      <view class="confirm-btn">
        <u-button type="primary" @click="confirmForm">申请</u-button>
        <u-button type="info" @click="cancelIssue">取消</u-button>
      </view>
    </u-form>


  </view>
</template>

<script>
  import utils from "@/utils/Base.js"
  export default {
    data() {
      return {
        form: {
          memberId: uni.getStorageSync('userInfo').user.userId, //会员ID
          loginId: '', //用户名
          nickName: '', //昵称
          telephone: '', // 电话
          address: '', //地址
          birthday: '', //出生日期
          activityType: '', // 活动类型
          activityTypeChoice: '', //活动类型选择
          intrduce: '', //商家介绍
          description: '', //详情描述
          lon: '', //位置经度
          lat: '', //位置纬度
          startTime: '', //营业开放时间
          endTime: '', //营业结束时间
          cardNo: '', //银行卡号
          bank: '', //银行开户行
          cardholderName: '', //持卡人姓名
          swiperList: [''],
          avatar: '', // 头像
          QRCode: '', //二维码
          businessLicense: '', // 营业执照
        },
        action: `${uni.$http.baseUrl}/core/file/upload`, //图片上传接口

        baseSwiperList: [''],
        baseAvatar: '', // 头像
        baseQRCode: '', //二维码
        baseBusinessLicense: '', // 营业执照

        birthdayShow: false, // 是否显示出生日期选择器
        timeParams: {
          year: true,
          month: true,
          day: true,
          hour: false,
          minute: false,
          second: false
        }, //生日期选项
        activityTypeShow: false, //是否显示活动类型
        activeTypeList: [], // 活动类型列表
        businessTimeParams: {
          year: false,
          month: false,
          day: false,
          hour: true,
          minute: true,
          second: true
        },
        startDataShow: false, // 是否显示营业起始时间
        endDataShow: false, // 是否显示营业结束时间

        placeholder: '开始输入...',
        rules: {
          // 登录用户名
          loginId: [
            // 对loginId字段进行必填验证
            {
              required: true,
              message: '请填写用户名',
              trigger: ['change', 'blur']
            },
          ],
          // 昵称
          nickName: [
            // 对nickName字段进行必填验证
            {
              required: true,
              message: '请填写昵称',
              trigger: ['change', 'blur']
            },
          ],
          // 电话
          telephone: [{
              required: true,
              message: '请输入手机号',
              trigger: ['change', 'blur'],
            },
            {
              // 自定义验证函数，见上说明
              validator: (rule, value, callback) => {
                // 上面有说，返回true表示校验通过，返回false表示不通过
                // this.$u.test.mobile()就是返回true或者false的
                return this.$u.test.mobile(value);
              },
              message: '手机号码不正确',
              // 触发器可以同时用blur和change
              trigger: ['change', 'blur'],
            }
          ],
          //出生日期
          birthday: [{
            required: true,
            message: '请选择出生日期',
            trigger: ['change', 'blur']
          }],
          // 活动类型
          activityType: [{
            required: true,
            message: '请选择活动类型',
            trigger: ['change', 'blur']
          }],
          // 开放时间
          startTime: [{
            required: true,
            message: '请选择开放日期',
            trigger: ['change', 'blur']
          }],
          endTime: [{
            required: true,
            message: '请选择结束日期',
            trigger: ['change', 'blur']
          }],
          // 地址
          address: [{
            required: true,
            message: '请填写地址',
            trigger: 'change'
          }, ],
          // 银行卡号
          cardNo: [{
            required: true,
            message: '请填写银行卡号',
            trigger: ['change', 'blur']
          }],
          // 银行开户行
          bank: [{
            required: true,
            message: '请填写该银行卡号开户行',
            trigger: ['change', 'blur']
          }, ],
          // 银行开户行
          cardholderName: [{
            required: true,
            message: '请填写持卡人姓名',
            trigger: ['change', 'blur']
          }, ],

        }, //表单规则

      };

    },
    onLoad(options) {
      this.getActiveType()

    },
    onReady() {
      this.$refs.uForm.setRules(this.rules);
    },
    methods: {
      // 头像上传
      upLoadAvatar(type, num) {
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
                url: that.action, //仅为示例，非真实的接口地址
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
                success(res) {
                  // 上传成功后相关操作

                  const base = JSON.parse(res.data)
                  // console.log(base.data);
                  // console.log(tempFilePaths);
                  if (type === 'avatar' && num === 1) {
                    that.form.avatar = tempFilePaths[0];
                    that.baseAvatar = base.data
                  } // 头像
                  if (type === 'QRCode' && num === 1) {
                    that.form.QRCode = tempFilePaths[0];
                    that.baseQRCode = base.data
                  } // 二维码
                  if (type === 'businessLicense' && num === 1) {
                    that.form.businessLicense = tempFilePaths[0];
                    that.baseBusinessLicense = base.data
                  } // 营业执照
                  if (type === 'swiper' && num === 5) {
                    that.form.swiperList = tempFilePaths;
                    that.baseSwiperList.push(base.data)
                  } // 轮播图

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




      // 获取地图
      getRegeo() {
        var _this = this;

        uni.chooseLocation({
          success(res) {
            _this.form.address = res.address
            _this.form.lat = res.latitude
            _this.form.lon = res.longitude
            // console.log(res)
          }
        })

      },

      // 获取活动类型列表
      async getActiveType() {
        const res = await uni.$http.get('/core/dict/getDictListByType', {
          dictTypeCode: 'ACTIVITY_TYPE'
        })
        const {
          code,
          data,
          message
        } = res.data
        if (code === 200) {
          this.activeTypeList = data

          let col = [];
          this.activeTypeList.forEach((item, index) => {
            col.push({
              label: item.name,
              value: Number(item.code),
            }, );
          });
          this.activeTypeList = col

          // console.log(this.activeTypeList)
        } else {
          // console.log(message)
          this.$refs.uToast.show({
            title: message,
            type: 'error',
          })
        }
      },

      // 选择活动类型
      confirmActivityType(e) {
        this.form.activityType = e[0]
        // console.log(this.form.activityType)
      },

      // 选择出生日期
      choicebirthday(e) {
        this.form.birthday = e.year + '-' + e.month + '-' + e.day
      },

      // 选择开放时间
      choiceStartDataTime(e) {
        this.form.startTime = e.hour + ':' + e.minute + ':' + e.second
      },

      // 选择关门时间
      choiceEndDataTime(e) {
        this.form.endTime = e.hour + ':' + e.minute + ':' + e.second
        console.log(this.form.endTime)
      },



      // 提交表单验证 
      confirmForm() {
        this.$refs.uForm.validate(valid => {
          if (valid) {
            if (!this.form.address) {
              return uni.showToast({
                title: '地址不能为空！',
                duration: 1500,
                icon: 'none',
              })
            }
            console.log('验证通过');
            this.toBeManage()
          } else {
            console.log('验证失败');
          }
        });
      },

      //提交信息成为商户
      async toBeManage() {
        const swiperLists = this.baseSwiperList.join(',')
        let data = {
          user: {
            loginId: this.form.loginId, //用户名
            nickName: this.form.nickName, // 昵称
            avatar: this.baseAvatar, //头像
            telephone: this.form.telephone, //电话
            address: this.form.address, //地址
            birthday: this.form.birthday //生日
          },
          memberId: this.form.memberId, //会员ID
          activityType: this.form.activityType.value, // 活动类型
          intrduce: this.form.intrduce, //商家介绍
          description: this.form.description, //详细描述
          contactImg: this.baseQRCode, //联系人二维码
          rotationChart: swiperLists, //轮播图
          lon: this.form.lon, //位置经度
          lat: this.form.lat, //位置纬度
          startTime: this.form.startTime, //营业开放时间
          endTime: this.form.endTime, //营业结束时间
          license: this.baseBusinessLicense, //营业执照
          cardNo: this.form.cardNo, // 银行卡号
          bank: this.form.bank, // 银行卡开户行
          cardholderName: this.form.cardholderName, // 持卡人姓名
        }
        console.log(this.form.activityType)
        const res = await uni.$http.post('/user/merchant/add', data)
        const {
          code,
          message
        } = res.data
        if (code === 200) {
          uni.showToast({
            title: '申请成功！', //弹框内容
            icon: 'success', //弹框模式
            duration: 1000 //弹框显示时间
          })
          uni.showToast({
            title: '申请提交成功，请等待审核！', //弹框内容
            icon: 'none', //弹框模式
            duration: 1500 //弹框显示时间
          })
          // setTimeout(()=>{
          //   uni.switchTab({
          //     url:'/pages/my/my'
          //   })
          // },1500)
        } else {
          uni.showToast({
            title: message, //弹框内容
            icon: 'none', //弹框模式
            duration: 1000 //弹框显示时间
          })
        }
      },

      // 活动报名人数
      valChange(e) {
        console.log('当前值为: ' + e.value)
      },


      // 取消发布
      cancelIssue() {
        console.log('取消')
      },
    }
  };
</script>

<style lang="scss">
  .issueSite {
    box-sizing: border-box;
    padding: 50rpx 50rpx;
  }

  .container {
    padding: 10px;
  }

  // 操作按钮
  .confirm-btn {
    display: flex;
    justify-content: space-between;
    box-sizing: border-box;
    padding: 50rpx 50rpx;
  }



  page {
    height: 100%;
  }

  .map-page {
    height: 100%;

    .map {
      width: 100%;
      height: 100%;
    }

    .btn-box {
      position: fixed;
      left: 0;
      bottom: 0;
      right: 0;
      padding: 15rpx;
      padding-bottom: calc(constant(safe-area-inset-bottom) + 15rpx);
      padding-bottom: calc(env(safe-area-inset-bottom) + 15rpx);

      .btn {
        height: 88rpx;
        line-height: 88rpx;
        text-align: center;
        border-radius: 10rpx;
        font-size: 32rpx;
        background-color: #ff6735;
        color: #fff;
      }
    }
  }
</style>
