<template>
	<view>
		<view class="title">
			邀请用户扫二维码成为核销人员
		</view>
		<image :src="'data:image/png;base64,'+img" mode="" class='img'></image>
	</view>

</template>

<script>
	export default {
		data() {
			return {
				img: ''
			};
		},
		onLoad() {
			this.getImg()
		},
		methods: {
			async getImg() {
				const res = await uni.$http.get('/user/merchant/getQrCode', {
					userId: uni.getStorageSync('userInfo').user.info.coreUserId,
				})
				this.img = res.data.data
				console.log(res.data)
			}

		}
	}
</script>

<style lang="scss">
	.title {
		padding: 10px;
		text-align: center;
	}

	.img {
		width: 80%;
		margin: 10px auto;
		display: block;
	}
</style>
