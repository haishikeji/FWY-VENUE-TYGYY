<template>
	<view>
		<view v-if="!showBind">
			<view class="title" v-if="merchantVerificationList.length">已绑定人员</view>
			<view class="bill-item" v-for="(item, index) in merchantVerificationList" :key="index">
				<view>
					{{item.memberName}}
					{{item.phone}}
				</view>
				<view @click="relieve(item)">解除绑定</view>
			</view>
			<view class="noMore" v-if="!merchantVerificationList.length">暂无已绑定的核销人员</view>
			<!-- <view class="btn" @click="showBind = true">
				绑定人员
			</view> -->
		</view>
		<view v-if="showBind">
			<scroll-view class="bill-list" :scroll-y="true" lower-threshold="30" :scroll-top="scrollTop"
				@scrolltolower="scrollLoad" @click="changeScrollView">
				<view v-for="(item, index) in billList" :key="index" class="flex-b" @click="changeCheck(item,index)">
					<view>{{item.name}}{{item.phone}}</view>
					<view v-if="item.check">
						<u-icon name="checkbox-mark" color="#2979ff" size="28"></u-icon>
					</view>
				</view>

				<view class="flex-a">
					<u-button type="primary" size="default" @click="sure()" v-if="billList.length">确认</u-button>
					<u-button type="info" size="default" @click="showBind=false">取消</u-button>
				</view>
			</scroll-view>

		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				showBind: false,
				billList: [], // 账单列表 
				page: 1, //页码
				isScrollLoad: true, //是否滚动加载
				scrollTop: 0, // 默认加载滚动条位置
				allStatus: false, // 是否加载所有数据
				total: Number, // 信息总条数
				userId: '',
				merchantVerificationList: [],
				checkList: []
			};
		},
		onLoad() {
			this.userId = JSON.parse(uni.getStorageSync('merchantVo')).userId
			this.getmerchantVerificationList()
			this.getBillList()
		},
		methods: {
			relieve(item) {
				uni.$http.delete('/user/merchantVerification/delete', {
					ids: item.id,
					requestFormType: true,
				}).then(res => {
					const {
						code,
						data,
						message
					} = res.data
					if (code == 200) {
						uni.showToast({
							title: '解除成功！',
							duration: 1000,
							icon: 'none',
						})
						this.getmerchantVerificationList()
					} else {
						uni.showToast({
							title: message,
							icon: 'error',
							duration: 1000,
						})
					}
				})
			},
			getmerchantVerificationList() {
				uni.$http.get('/user/merchantVerification/list', {
					merchantId_eq: this.userId,
				}).then(res => {
					const {
						code,
						data,
						message
					} = res.data
					if (code == 200) {
						this.merchantVerificationList = data.records

						this.checkList = this.merchantVerificationList.map(function(item) {
							return item.id;
						});
					}
				})
			},
			sure() {
				let arr = []
				this.billList.forEach(x => {
					if (x.check) {
						arr.push({
							memberId: x.id,
							merchantId: this.userId,
						})
					}
				})
				if (!arr.length) {
					return uni.showToast({
						title: '请选择要添加的人员！',
						duration: 1000,
						icon: 'none',
					})
				}
				uni.$http.post('/user/merchantVerification/add', arr).then(res => {
					const {
						code,
						data,
						message
					} = res.data
					if (code == 200) {
						uni.showToast({
							title: '添加成功！',
							duration: 1000,
							icon: 'none',
						})
						this.showBind = false
						this.getmerchantVerificationList()
					} else {
						uni.showToast({
							title: message,
							icon: 'error',
							duration: 1000,
						})
					}
				})
			},
			changeCheck(item, index) {
				item.check = !item.check
				this.$set(this.billList, index, item)
			},
			// 获取人员列表
			async getBillList() {
				const userId = JSON.parse(uni.getStorageSync('merchantVo')).userId
				const res = await uni.$http.get('/user/member/memberList', {
					pageNum: this.page,
					pageSize: 10,
				})
				const {
					code,
					data,
					message
				} = res.data

				// 数据请求成功
				if (code === 200) {
					this.total = data.total;
					if (this.billList.length < this.total) {
						if (this.page === 1) {
							this.billList = data.records
						} else {
							this.billList = this.billList.concat(data.records)
						}
						this.isScrollLoad = true;
						this.page++;
						// console.log('完成加载数据',this.isclick)
					} else if (this.billList.length >= this.total) {
						this.allStatus = true
						return;
					}
				}

				// 数据请求失败
				if (code !== 200) {
					return uni.showToast({
						title: '数据请求失败！',
						duration: 1500,
						icon: 'none',
					})
				}
				// console.log(res)
			},

			// 加载更多 
			scrollLoad() {
				if (this.isScrollLoad) {
					this.isScrollLoad = false
					setTimeout(() => {
						this.getBillList()
					}, 300)
				}
			},
			//切换scroll-view事件
			changeScrollView() {
				this.scrollTop = 0; //回到最顶部
			},

		}
	}
</script>

<style lang="scss">
	.title {
		margin: 10px;
		font-size: 14px
	}

	.flex-b,
	.bill-item {
		display: flex;
		justify-content: space-between;
		border-bottom: 1px solid #eee;
		padding: 10px;
	}

	.flex-a {
		display: flex;
		justify-content: space-around;
		margin-top: 30px;
	}

	.btn {
		width: 90%;
		margin: 20px auto;
		background: #3c9cff;
		height: 40px;
		line-height: 40px;
		text-align: center;
		color: #fff;
		border-radius: 10px;

	}

	.bill-list {
		width: 100%;
		height: 100vh;
		background-color: #fefefe;
		box-sizing: border-box;
		padding: 30rpx;
	}


	// 没有更多数据
	.noMore {
		text-align: center;
		line-height: 4;
	}
</style>
