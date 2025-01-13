<template>
  <view class="orderDetail">
    <!-- 轮播图 -->
    <swiper :indicator-dots="true" :autoplay="true" :interval="3000" :duration="1000" circular="true">
      <swiper-item v-for="(item,index) in swiperList" :key="index">
        <view class="swiper-item">
          <img :src="action+item">
        </view>
      </swiper-item>
    </swiper>
 
    <!-- 活动/场地信息 -->
    <view class="place-info">
      <text class="title">{{showInfo.activityName ? showInfo.activityName : showInfo.siteName}}</text>
      <view class="time" v-if="orderInfo.scheduleDate">预约日期：{{orderInfo.scheduleDate.split(' ')[0]}}</view>
      <view class="time">开始时间：{{orderInfo.startTime}}</view>
      <view class="time">结束时间：{{orderInfo.endTime}}</view>
      <view class="time">市场价：
        <text class="money">{{showInfo.marketPrice}}¥</text>
      </view>
      <view class="time">会员价：
        <text class="money">{{showInfo.memberPrice}}¥</text>
      </view>
      <view class="phone">
        <text>联系电话：{{showInfo.activityPhone ? showInfo.activityPhone : showInfo.telephone}}</text>
        <u-icon name="phone" color="#000" size="38"></u-icon>
      </view>
      <view class="address">
        <text>地址：{{ showInfo.activityAddr ? showInfo.activityAddr : showInfo.siteAddr }}</text>
        <u-icon name="map" color="#000" size="38"></u-icon>
      </view>
    </view>

    <!-- 预约人信息（商家可见） -->
    <u-card :title="title" title-color="#7b9bff" title-size="32" :sub-title="subTitle">
      <view class="order-man-info" slot="body">

        <view class="u-body-item u-flex u-border-bottom u-col-between u-p-t-0" v-for="(item,index) in userInfo"
          :key="index">
          <view class="u-body-item-title">
            <text class="user-info">用户名：{{item.memberName}}</text>
            <text class="user-info">联系方式：{{item.phone}}</text>
            <text class="user-info">订单状态：{{item.orderStatusDict}}</text>
            <text class="user-info">下单时间：{{item.orderDate}}</text>
          </view>
          <u-button v-if=" item.orderStatusDict === '已付款待参与' " type="primary" :plain="true" size="mini"
            @click="hEdit(item.id)">改派编辑</u-button>

          <u-popup v-model="editShow" mode="center" :closeable="true" :mask-close-able="false">
            <view class="edit-sty">
              <u-form :model="editForm" ref="editForm" label-width="150">
                <u-form-item label="用户名:">
                  <u-input v-model="item.memberName" :disabled="true" />
                </u-form-item>
                <u-form-item label="预约日期:" :required="true">
                  <u-input v-model="editForm.scheduleDate" type="select" :select-open="dateSelectShow"
                    @click="dateSelectShow = true" />
                  <u-select v-model="dateSelectShow" mode="single-column" :list="scheduleDateList"
                    safe-area-inset-bottom="true" @confirm="scheduleDateConfirm"></u-select>
                </u-form-item>
                <u-form-item label="预约场次:" :required="true">
                  <u-input v-model="editForm.schedulePlace" type="select" :select-open="placeSelectShow"
                    @click="placeSelectShow = true" />
                  <u-select v-model="placeSelectShow" mode="single-column" :list="schedulePlaceList"
                    safe-area-inset-bottom="true" @confirm="schedulePlaceConfirm"></u-select>
                </u-form-item>
                <u-form-item label="开始时间:">
                  <u-input v-model="editForm.startTime" :disabled="true" />
                </u-form-item>
                <u-form-item label="结束时间:">
                  <u-input v-model="editForm.endTime" :disabled="true" />
                </u-form-item>
              </u-form>
              <view class="edit-btns">
                <u-button size="default" type="info" @click="cancelEdit()">取消</u-button>
                <u-button size="default" type="success" @click="confirmEdit()">确认改派</u-button>
              </view>
            </view>
          </u-popup>
        </view>
			
		
		<view class="u-body-item u-flex u-border-bottom u-col-between u-p-t-0" v-for="(item,index) in records"
		  :key="index">
		  <view class="u-body-item-title">
		    <text class="user-info">用户名：{{item.name}}</text>
		    <text class="user-info">联系方式：{{item.phone}}</text>
		    <text class="user-info">是否已付费：{{item.isPayStr}}</text>
		    <text class="user-info" v-if="item.isPay==1">付费方式：{{item.payTypeStr}}</text>
		  </view>
		
		</view>
		
      </view>
    </u-card>
  </view>
</template>

<script>
  export default {
    data() {
      return {
        action: `${uni.$http.baseUrl}/core/file/download?base=`, // 图片下载预览地址
        identity: 1, //身份：1=参与者；2=商家
        scheduleId: '', // 预约场次ID
        swiperList: [], //轮播图列表
        orderInfo: {}, // 预约详情
        showInfo: {}, // 渲染信息（活动/场地）
        userInfo: [], // 预约用户信息
        title: '预约用户信息',
        subTitle: '',
        editShow: false, // 是否显示改派信息
        scheduleDateList: [], //预约日期列表
        dateSelectShow: false, // 日期显示
        PScheduleDateList: [], //预约日期传参列表
        placeSelectShow: false, //场地显示
        schedulePlaceList: [], //场地列表
        editForm: {
          name: '', // 用户名
          scheduleDate: '', // 预约日期
          schedulePlace: '', // 预约场次信息
          startTime: '', // 开始时间
          endTime: '', // 结束时间
        },
        orderId: '', // 改派订单ID 
        editPlaceId: '', // 改派场次ID
		records:[]
      };
    },
    onLoad(option) {
      // 身份认证
      this.identity = uni.getStorageSync('identity')

      // 预约场地ID
      const scheduleId = JSON.parse(decodeURIComponent(option.scheduleId))
      this.scheduleId = scheduleId // 预约信息
      // console.log(this.scheduleId)
    },
    onShow() {
      this.getScheduleInfo() // 加载预约信息 
      this.getScheduleRecord() // 加载线下预约信息 
    },
    methods: {
		async getScheduleRecord(){
			const res = await uni.$http.get('/merchant/scheduleRecord/list', {
			  scheduleId_eq: this.scheduleId,
				pageNum:1,
				pageSize:100
			})
			const {
			  code,
			  data,
			  message
			} = res.data
			if(code == 200){
				this.records = data.records
			}
		},
      // 加载预约信息
      async getScheduleInfo() {
        const res = await uni.$http.get('/merchant/schedule/getScheduleInfo', {
          requestFormType: true,
          id: this.scheduleId
        })
        const {
          code,
          data,
          message
        } = res.data
        // 数据请求成功
        if (code === 200) {
          console.log(data)
          this.orderInfo = data // 所有预约信息
          this.userInfo = data.orders // 预约用户信息
          this.subTitle = '预约人数：' + this.userInfo.length
          if (data.activityTypeDict === '活动') {
            this.showInfo = data.activity // 活动
          } else {
            this.showInfo = data.site // 场地
          }
          this.swiperList = this.showInfo.rotationChart ? this.showInfo.rotationChart.split(',') : '' // 轮播图
        } else {
          console.log(message)
        }
        // 数据请求失败
        if (code !== 200) {
          return uni.showToast({
            title: '数据请求失败！',
            duration: 1500,
            icon: 'none',
          })
        }
      },

      // 改派预约信息
      hEdit(id) {
        this.editShow = true
        this.orderId = id
        setTimeout(() => {
          this.getData() // 日期
        }, 500)
      },

      // 预约日期查询
      async getData() {
        const res = await uni.$http.get('/merchant/schedule/getDate')
        const {
          code,
          data,
          message
        } = res.data
        if (code === 200) {
          this.PScheduleDateList = data //预约日期传参列表
          this.scheduleDateList = data // 预约日期列表
          // 修改对象键
          this.scheduleDateList.forEach((item, index) => {
            if (item.day) {
              item.label = item.day;
              delete item.day;
            }
            if (item.weekDay) {
              item.value = index;
              delete item.weekDay;
            }
          });
        } else {
          console.log(message)
        }
      },

      // 选择日期
      scheduleDateConfirm(e) {
        this.editForm.scheduleDate = e[0].label
        setTimeout(() => {
          this.getSchedulingList() // 选择场次
        }, 100)
        // console.log(this.editForm.scheduleDate)
      },

      // 获取场次列表
      async getSchedulingList() {
        const res = await uni.$http.get('/merchant/schedule/getList', {
          requestFormType: true,
          activityId_eq: this.orderInfo.activityId,
          scheduleDate_eq: this.editForm.scheduleDate
        })
        const {
          code,
          data,
          message
        } = res.data
        if (code === 200) {
          this.schedulePlaceList = data
          // 修改对象键
          this.schedulePlaceList.forEach((item, index) => {
            if (item.schedules === null) {
              item.label = String(index + 1);
            }
            if (item.createOrg === null) {
              item.value = index + 1;
            }
          });
          // console.log(this.schedulePlaceList)
        } else {
          console.log(message)
        }
      },

      // 场次选择
      schedulePlaceConfirm(e) {
        this.editForm.schedulePlace = e[0].label
        this.schedulePlaceList.forEach(item => {
          if (e[0].value === item.value) {
            this.editForm.startTime = item.startTime
            this.editForm.endTime = item.endTime
            // this.activeId = item.activityId
            this.editPlaceId = item.id
          }
        })

        console.log(e)
        console.log(this.schedulePlaceList)
      },

      // 取消改派
      cancelEdit() {
        this.editShow = false
      },

      // 确认改派
      async confirmEdit() {
        if (!this.editForm.scheduleDate) {
          return uni.showToast({
            title: '请先选择预约日期！',
            duration: 1500,
            icon: 'none',
          })
        }
        if (!this.editForm.schedulePlace) {
          return uni.showToast({
            title: '请选择预约场次！',
            duration: 1500,
            icon: 'none',
          })
        }

        // console.log(this.activeId)
        // console.log(this.editPlaceId)
        const res = await uni.$http.put('/order/reserveOrder/update', {
          requestFormType: true,
          id: this.orderId,
          scheduleId: this.editPlaceId
        })
        // console.log(res)
        const {
          code,
          message
        } = res.data
        if (code === 200) {
          uni.showToast({
            title: '订单改派成功！',
            duration: 1000,
            icon: 'none',
          })
          this.editShow = false
          setTimeout(() => {
            // console.log('刷新')
            this.page = 1
            const pages = getCurrentPages()
            const curPage = pages[pages.length - 1]
            curPage.onLoad(curPage.options)
            curPage.onShow()
          }, 1000)
        }
        // 数据请求失败
        if (code !== 200) {
          return uni.showToast({
            title: '数据请求失败！',
            duration: 1500,
            icon: 'none',
          })
        }
      },

    }
  }
</script>

<style lang="scss">
  .orderDetail {
    padding-bottom: 100rpx;
  }

  // 轮播图
  swiper {
    width: 100%;
    height: 380rpx;

    .swiper-item,
    img {
      width: 100%;
      height: 100%;
    }
  }

  // 场地信息
  .place-info {
    width: 100%;
    // height: 300rpx;
    box-sizing: border-box;
    padding: 10rpx 50rpx;

    .title {
      font-size: 40rpx;
      font-weight: 600;
      margin: 20rpx 0 30rpx;
    }

    .time {
      margin-top: 30rpx;
    }

    .money {
      font-weight: 600;
      color: red;
    }

    .phone,
    .address {
      display: flex;
      justify-content: space-between;
      margin-top: 20rpx;
    }
  }

  // 预约人信息卡片 
  .u-card-wrap {
    background-color: $u-bg-color;
    padding: 1px;
  }

  .u-body-item {
    font-size: 26rpx;
    color: #333;
    padding: 20rpx 10rpx;
  }

  .u-flex {
    justify-content: space-between;
  }

  // 预约人信息
  .order-man-info {
    .user-info {
      display: block;
      line-height: 2;
    }

    // 改派弹框样式
    .edit-sty {
      width: 100%;
      height: 100%;
      padding: 80rpx 50rpx 50rpx;
      // background-color: aliceblue;
    }

    .edit-btns {
      margin-top: 30rpx;
      display: flex;
      justify-content: space-around;
    }
  }
</style>
