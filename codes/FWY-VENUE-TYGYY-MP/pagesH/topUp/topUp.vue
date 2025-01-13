<template>
  <view class="top-up">
    <!-- 充值金额 -->
    <u-form :model="form" ref="uForm" label-align="left">
      <u-form-item label="充值金额:" prop="topMoney" label-width="130">
        <u-input v-model="form.topMoney" :border="true" placeholder="请输入/选择充值金额" type="number" />
      </u-form-item>
    </u-form>

    <!-- 金额选择 -->
    <view class="grid-sty">
      <u-grid :col="3">
        <u-grid-item v-for="(item,index) in gridList" :key="index" @click="choiceMoney(index,item)">
          <view class="grid-text">{{item.num}} 元</view>
        </u-grid-item>
      </u-grid>
    </view>

    <!-- 操作按钮 -->
    <view class="top-btn">
      <u-button type="primary" size="medium" :plain="true" @click="cancel">取 消</u-button>
      <u-button type="success" size="medium" :plain="true" @click="confirmForm">确 认</u-button>
    </view>

  </view>
</template>

<script>
  export default {
    data() {
      return {
        form: {
          topMoney: '', //市场价格
        },
        activeFlag: false,
        gridList: [{
          num: 10
        }, {
          num: 20
        }, {
          num: 30
        }, {
          num: 50
        }, {
          num: 100
        }, {
          num: 200
        }],

        //表单规则
        rules: {
          // 市场价格
          topMoney: [{
            pattern: /^[+]{0,1}(\d+)$|^[+]{0,1}(\d+\.\d+)$/,
            message: '只能是正数',
            trigger: 'change',
          }, ],
        },

      };

    },
    onLoad(options) {



      // console.log(options)

    },
    onReady() {
      this.$refs.uForm.setRules(this.rules);
    },

    methods: {
      // 选择充值金额
      choiceMoney(index, item) {
        this.form.topMoney = item.num
      },

      // 取消
      cancel() {
        uni.switchTab({
          url: '/pages/my/my'
        })
      },

      // 提交表单验证
      confirmForm() {
        this.$refs.uForm.validate(valid => {
          if (valid) {
            if (!this.form.topMoney) {
              return uni.showToast({
                title: '充值金额不能为空！',
                duration: 1500,
                icon: 'none',
              })
            }
            console.log('验证通过');
            this.confirm()
          } else {
            console.log('验证失败');
          }
        });
      },

      // 确认充值
      async confirm() {
        // console.log('充值')
        const memberID = JSON.parse(uni.getStorageSync('member')).id
        const res = await uni.$http.post('/order/recharge/add', {
          requestFormType: true,
          memberId: memberID,
          currencyNum: this.form.topMoney
        })
        const {
          code,
          data,
          message
        } = res.data

        if (code === 200) {
          console.log(data)
          uni.requestPayment({
            provider: 'wxpay', //支付类型
            appId: data.appId, //小程序Appid
            timeStamp: data.timeStamp, // 时间戳
            nonceStr: data.nonceStr, // 随机字符串
            package: data.pkg, // prepay_id参数
            signType: 'MD5', // 签名算法
            paySign: data.paySign, // 签名
            success(res) {
              console.log('充值成功', res);
              uni.$http.post('/order/recharge/notifySuccess', {
                requestFormType: true,
                orderNo: data.orderNo,
              }).then(res => {
                const {
                  code,
                  message
                } = res.data
                if (code === 200) {
                  uni.showToast({
                    title: '充值成功！',
                    duration: 1500,
                    icon: 'none',
                  })
                  setTimeout(() => {
                    uni.reLaunch({
                      url: '/pages/my/my'
                    })
                  }, 2000)
                }
              })
            },
            fail(err) {
              uni.showToast({
                title: '交易失败！',
                duration: 1500,
                icon: 'none',
              })
              console.log(err);
            }
          })
        }

        // 数据请求失败
        if (code !== 200) {
          return uni.showToast({
            title: message,
            duration: 1500,
            icon: 'none',
          })
        }
      }

    }
  };
</script>

<style lang="scss">
  .top-up {
    width: 100%;
    height: 100vh;
    background-color: #fafafa;
    padding: 50rpx;
    box-sizing: border-box;
  }

  .top-money {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .grid-sty {
    margin-top: 80rpx;

    .grid-text {
      font-size: 36rpx;
      font-weight: 600;
      color: red;
      padding: 30rpx 0;
    }
  }

  .top-btn {
    margin-top: 80rpx;
    display: flex;
    justify-content: space-around;
  }
</style>


.top-btn {
margin-top: 80rpx;
display: flex;
justify-content: space-around;
}
</style>
