<template>
	<view class="case">
		<!-- 选项卡 -->
		<view class="tab-card">
			<!-- <u-tabs :list="tabList" :is-scroll="true" :current="current" height="130" gutter="35" active-color="#fd8378"
        @change="change">
      </u-tabs> -->
			<u-button type="primary" plain @click="calendarShow = true">选择日期</u-button>
			<u-calendar v-model="calendarShow" mode="date" :min-date="nowDate" max-date="2050-12-31" @change="change">
			</u-calendar>
		</view>


		<!-- 可预约列表 -->
		<scroll-view class="orderList" scroll-y="true">
			<view class="order-card" v-for="(item,index) in orderList" :key="index">
				<view class="titles">
					<text class="time">{{item.scheduleDate}} {{item.startTime}}-{{item.endTime}}</text>
					<text>{{item.activity ? item.activity.activityName : item.site.siteName}}</text>
				</view>
				<view class="order-info">
					<img v-if="(item.activity && item.activity.rotationChart) || (item.site && item.site.rotationChart)"
						:src="item.activity ? action + item.activity.rotationChart.split(',')[0] : action + item.site.rotationChart.split(',')[0]">
					<img v-else src="../../static/no-img.png" alt="">
					<view class="word-info">
						<text class="address">{{item.activity ? item.activity.activityAddr : item.site.siteAddr}}</text>
						<text class="money">可预约人数：{{item.activityNum === null ? 0 : item.activityNum }}</text>
						<!--            <text class="money">￥{{item.activity ? item.activity.memberPrice : item.site.memberPrice}}</text> -->
					</view>
					<view class="btn" v-if="item.status === 1 && !item.orderStatus" @click="appointment(item)">预 约
					</view>
					<view class="btn-belong" v-if="item.orderStatus === 3">已预约</view>
					<view class="btn-done" v-if="item.status !== 1 &&item.orderStatus != 3">已约满</view>
				</view>
			</view>
			<view class="saveBox"></view>
			<view class="none" v-if="orderList.length === 0">该日期暂无排期、请重新选择日期</view>
		</scroll-view>

	</view>
</template>

<script>
	import dayjs from 'dayjs'
	export default {
		data() {
			return {
				action: `${uni.$http.baseUrl}/core/file/download?base=`, // 图片下载预览地址
				tabList: [], // 数据格式化后的tab列表
				current: 0,
				ordTabList: [],
				activeId: '', //活动id
				orderList: [], // 预约列表
				calendarShow: true, // 是否显示日历
				nowDate: '', //当前时间
			};
		},

		onLoad(options) {
			// console.log(options.id)
			this.activeId = options.id // 更新活动id

			this.getData()

			// setTimeout(() => {  
			//   this.getActiveList(this.activeId, this.tabList[0].count)
			// }, 500)

		},
		onShow() {
			// this.nowDate = dayjs().format('YYYY-MM-DD')
			// setTimeout(() => {
			//   this.getActiveList(this.activeId, this.tabList[this.current].count)
			// }, 500)
		},

		methods: {
			// 近七天日期查询
			async getData() {
				const res = await uni.$http.get('/merchant/schedule/getDate')
				const {
					code,
					data,
					message
				} = res.data
				if (code === 200) {
					this.ordTabList = data
					this.tabList = data
					// 修改对象键
					this.tabList.forEach((item) => {
						if (item.day) {
							item.count = item.day;
						}
						if (item.weekDay) {
							item.name = item.weekDay;
							delete item.weekDay;
						}
					});
					// 修改对象值
					this.tabList.forEach((item, index) => {
						item.count = item.count.split('-').slice(1, 3).join('-')
						if (index === 0) {
							item.name = '今天'
						} else if (index === 1) {
							item.name = '明天'
						} else {
							item.name = item.name.replace('星期', '周')
						}
					})

				} else {
					console.log(message)
				}
			},

			// 获取活动场地列表
			async getActiveList(id, date) {
				// // 格式化时间
				// var time = date.split('-').concat();
				// time.unshift('2022')
				// time = time.join('-')
				// console.log(time)
				// 数据请求
				const res = await uni.$http.get('/merchant/schedule/getList', {
					requestFormType: true,
					activityId_eq: id,
					scheduleDate_eq: date
				})
				const {
					code,
					data,
					message
				} = res.data
				if (code === 200) {
					this.orderList = data
				} else {
					console.log(message)
				}
			},

			// // tab切换查询不同活动
			// change(index) {
			//   this.current = index;
			//   this.getActiveList(this.activeId, this.tabList[index].count)
			//   // console.log(this.tabList[index].count)
			// },

			// 日历选择
			change(e) {
				// this.current = index;
				// this.getActiveList(this.activeId, this.tabList[index].count)
				console.log(e.result)
				this.getActiveList(this.activeId, e.result)
			},

			// 跳转预约详情页
			appointment(item) {
				uni.navigateTo({
					url: '/pagesH/appointmentDetail/appointmentDetail?item=' + encodeURIComponent(JSON.stringify(
						item))
				})
			}
		},
	}
</script>

<style lang="scss">
	.case {
		width: 100%;
		height: 98vh;
		background-color: #fafafa;
		overflow: hidden;
	}

	.tab-card {
		width: 100%;
		height: 150rpx;
		box-sizing: border-box;
		padding-top: 20rpx;
	}

	.orderList {
		width: 100%;
		height: 90vh;
		background-color: #fff;
		margin: 20rpx 0;

		.none {
			width: 100%;
			display: flex;
			justify-content: center;
			align-items: center;
			font-size: 40rpx;
			font-weight: 600;
			color: #999;
		}

		.saveBox {
			width: 100%;
			height: 100rpx;
			background-color: #fff;
		}

		.order-card {
			box-sizing: border-box;
			padding: 30rpx;
			border-bottom: 1px solid #ccc;

			.titles {
				font-size: 26rpx;
				font-weight: 600;
				margin-bottom: 20rpx;

				.time {
					margin-right: 20rpx;
				}
			}
		}

		.order-info {
			position: relative;
			display: flex;

			// align-items: center;
			img {
				width: 250rpx;
				height: 150rpx;
			}

			.word-info {
				display: flex;
				flex-direction: column;
				justify-content: space-around;
				margin-left: 20rpx;
				font-weight: 600;

				.address {
					font-size: 28rpx;
					width: 400rpx;
					overflow: hidden;
					text-overflow: ellipsis;
					display: -webkit-box;
					-webkit-box-orient: vertical;
					-webkit-line-clamp: 2;
				}

				.money {
					font-size: 28rpx;
					color: #e44741;
				}
			}

			.btn,
			.btn-done,
			.btn-belong {
				position: absolute;
				bottom: 10rpx;
				right: 0;
				height: 60rpx;
				line-height: 60rpx;
				border-radius: 50rpx;
				padding: 0 40rpx;
				color: #fff;
			}

			.btn {
				background-color: #fd8378;
			}

			.btn-done {
				background-color: #cccccc;
			}

			.btn-belong {
				background-color: #1e61ba;
			}
		}
	}
</style>
