<template>
  <view>
    
    <view class="searchResult-list-item" v-for="(item,index) in resultInfo" :key="index" @click="goDetail(item)">
      <img v-if="item.rotationChart" :src="action+item.rotationChart.split(',')[0]" @click="goDetail(item)">
      <img v-else src="../../static/no-img.png" alt="">
      <!-- <view class="place-introduce"> -->
        <!-- <text class="name">{{item.activityName}}</text> -->
        <view class="place-introduce">
          <text class="name">{{item.activityName ? item.activityName : item.siteName}}</text>
          <text class="introduce">{{item.activityAddr ? item.activityAddr : item.siteAddr}}</text>
          <view class="money">
            <text>市场价:{{item.marketPrice}}</text>
            <text class="place-status">{{item.releaseStatusDict}}</text>
          </view>
        </view>
      <!-- </view> -->
    </view>
    
  </view>
</template>

<script>
  export default {
    data() {
      return {
        action: `${uni.$http.baseUrl}/core/file/download?base=`, // 图片下载预览地址
        resultInfo: [ ]
      };
    },
    onLoad(options) {
      const item = JSON.parse(decodeURIComponent(options.info))
      this.resultInfo = item
      console.log(this.resultInfo)
    },
    
    onShow() {
      
    },
    
    methods:{
      // 跳转详情页
      goDetail(item) {
        if (item.activityName) {
          uni.navigateTo({
            url: '/pagesH/siteDetail/siteDetail?type=1&detailType=1&id=' + encodeURIComponent(JSON.stringify(item
              .id))
          })
        } else {
          uni.navigateTo({
            url: '/pagesH/siteDetail/siteDetail?type=1&detailType=2&id=' + encodeURIComponent(JSON.stringify(item
              .id))
          })
        }
        // console.log(item)
      },
    }
  
  }
</script>

<style lang="scss">
  .searchResult-list-item {
    background-color: #fff;
    box-sizing: border-box;
    padding: 20rpx 0;
    margin: 0 40rpx;
    border-bottom: 1px solid #f0f0f0;
    display: flex;
  
    img {
      width: 380rpx;
      height: 200rpx;
      border-radius: 10rpx;
      margin-right: 50rpx;
    }
  
    .place-introduce {
      display: flex;
      flex-direction: column;
    
      .name {
        font-weight: 600;
      }
    
      .introduce {
        width: 220rpx;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2;
        font-size: 24rpx;
        margin: 20rpx 0;
      }
    
      .money {
        display: flex;
        justify-content: space-between;
        font-size: 24rpx;
    
        .place-status {
          font-size: 21rpx;
          display: inline-block;
          padding: 5rpx;
          border: 1px solid #999;
          border-radius: 10rpx;
          // margin-left: 20rpx;
        }
      }
    }
  }
</style>
