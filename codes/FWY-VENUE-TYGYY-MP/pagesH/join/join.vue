<template>
	<view class="scanCode">
		<u-icon class="code-icon" name="scan" color="#707070" size="310" @click="scan"></u-icon>
		<u-input class="code-input" v-model="codeValue" :type="text" :border="true" :disabled="true"
			placeholder="请点击扫码框扫码" />
		<u-button :custom-style="customStyle" :plain="false" @click="sureScan">确定</u-button>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				codeValue: '',
				customStyle: {
					width: '160rpx',
					height: '65rpx',
					marginTop: '20px', // 注意驼峰命名，并且值必须用引号包括，因为这是对象
					color: '#ffffff',
					fontSize: '28rpx',
					backgroundColor: "#ff8275",
				}
			};
		},
		methods: {
			scan() {
				let that = this
				// 允许从相机和相册扫码
				uni.scanCode({
					success: function(res) {
						console.log('条码类型：' + res.scanType);
						console.log('条码内容：' + res.result);
						that.codeValue = res.result
					}
				});
			},
			// 确定核销
			sureScan() {
				uni.$http.get('/user/merchantVerification/scanCode', {
					code: this.codeValue
				}).then(res => {
					const {
						code,
						message
					} = res.data
					if (code === 200) {
						uni.showToast({
							title: '加入成功！', //弹框内容
							icon: 'success', //弹框模式
							duration: 1500 //弹框显示时间
						})
						let user = uni.getStorageSync('userInfo')
						user.user.info.verification = true
						uni.setStorageSync('userInfo', user)
						setTimeout(() => {
							uni.reLaunch({
								url: '/pages/my/my'
							})
						}, 1500)
					} else { // 核销失败
						uni.showToast({
							title: message, //弹框内容
							icon: 'none', //弹框模式
							duration: 1500 //弹框显示时间
						})
					}
				})
			},

		},
	}
</script>

<style lang="scss">
	.scanCode {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-top: 80rpx;

		.code-tit {
			font-size: 32rpx;
			font-weight: 600;
		}

		.code-icon {
			margin-top: 80rpx;
		}

		.code-input {
			width: 500rpx;
			text-align: center;
			margin-top: 60rpx;
			padding: 0 50rpx;
		}
	}
</style>
