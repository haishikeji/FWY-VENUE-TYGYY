<template>

  <div class="home">
    <avue-data-tabs :option="option"></avue-data-tabs>
  </div>

</template>

<script>

import { homeData } from '@/api/home/home'
export default {
  data () {
    return {
        option: {
          data: [
            {
              count: 0,
              text: '商家入驻数',
              color: 'rgb(27, 201, 142)',
              key: '商'
            },
            {
              count: 0,
              text: '注册会员数',
              color: 'rgb(230, 71, 88)',
              key: '人'
            },
            {
              count: 0,
              text: '发布活动数',
              color: 'rgb(178, 159, 255)',
              key: '场'
            },
            {
              count: 0,
              text: '发布场地数',
              color: 'rgb(240, 137, 40)',
              key: '场'
              },
            {
              count: 0,
              text: '平台累计收入',
              color: 'rgb(242, 134, 196)',
              key: '元'
            },
            
          ]
        }
    }
  },

  created () {
    this.getHomeData()
  },
  methods: {
    getHomeData() {
      homeData().then(res => {
        const {code, data, message} = res.data 
        if (code === 200) {
          this.option.data[0].count = data.merchants //商家入驻数
          this.option.data[1].count = data.members //注册会员数
          this.option.data[2].count = data.activityNum //发布活动数
          this.option.data[3].count = data.siteNum //发布场地数
          this.option.data[4].count = data.platformTotalRevenue //平台累计收入数
        } else {
          console.log(message);
        }
        
      })
    }
  }
}
</script>

<style lang="scss">

.home {
  padding: 10px;
  box-sizing: border-box;
}

.avue-data-tabs .item{
  position: relative;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avue-data-tabs .item-body .h2 {
  font-size: 50px;
}

.avue-data-tabs .item-header>span{
   padding: 0;
}
.avue-data-tabs .item-footer>p{
  font-size: 16px;
}



</style>
