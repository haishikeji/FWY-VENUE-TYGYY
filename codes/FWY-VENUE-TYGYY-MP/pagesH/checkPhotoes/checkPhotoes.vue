<template>
  <scroll-view class="active-phone" :scroll-y="true" :scroll-top="scrollTop" @scrolltolower="scrollLoad"
    @click="changeScrollView">
    <view class="flex">
      <view class="phone-item" v-for="(item,index) in activePhotoes" :key="index">
        <img :src="item" @click.stop="clickImg(index)">
      </view>
    </view>
     <view class="noMore" v-if="allStatus">没有更多了……</view>
  </scroll-view>
</template>

<script>
  export default {
    data() {
      return {
        photosId: '', //相册ID
        page: 1, //页码
        isScrollLoad: true, //是否滚动加载
        allStatus: false, // 是否加载所有数据
        total: Number, // 信息总条数
        scrollTop: 0, // 默认加载滚动条位置
        activePhotoes: [], // 活动相册
      };
    },

    onLoad(options) {
      console.log(options)
      this.photosId = options.id
      this.getPhotos()
    },

    onShow() {
      
    },

    methods: {
      // 获取相册图片
      async getPhotos() {
        uni.showLoading({
          title: "数据加载中..."
        })

        const res = await uni.$http.get('/merchant/pfActivityPicture/listPic', {
          requestFormType: true,
          pageNum: this.page,
          pageSize: 20,
          id: this.photosId
        })
        const {
          code,
          data,
          message
        } = res.data

        // 数据请求成功
        if (code === 200) {
          this.total = data.total;
          if (this.activePhotoes.length < this.total) {
            if (this.page === 1) {
              this.activePhotoes = data.records
            } else {
              this.activePhotoes = this.activePhotoes.concat(data.records)
            }
            this.isScrollLoad = true;
            this.page++;
            // console.log('完成加载数据',this.isclick)
          } else if (this.activePhotoes.length >= this.total) {
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
      },

      // 加载更多
      scrollLoad() {
        if (this.isScrollLoad) {
          this.isScrollLoad = false
          setTimeout(() => {
            this.getPhotos()
          }, 300)
        }
        // console.log('加载更多列表', this.activePhotoes)
      },

      // 查看大图
      clickImg(index) {
        const that = this
        uni.previewImage({
          urls: that.activePhotoes, //需要预览的图片http链接列表，多张的时候，url直接写在后面就行了
          current: that.activePhotoes[index], // 当前显示图片的http链接，默认是第一个
          success: function(res) {},
          fail: function(res) {},
          complete: function(res) {},
        })
      },

    },
  }
</script>

<style lang="scss">
  .active-phone {
    width: 100%;
    height: 100vh;
    padding: 30rpx;
    box-sizing: border-box;
    .flex{
      display: flex;
      flex-wrap: wrap;
      justify-content: space-between;
      
      .phone-item {
        width: 48%;
        // padding: 10rpx;
      
        img {
          width: 100%;
          height: 250rpx;
        }
      }
    } 
  }

  // 没有更多数据
  .noMore {
    flex: 1;
    text-align: center;
    line-height: 4;
    padding-bottom: 50rpx;
  }
</style>
