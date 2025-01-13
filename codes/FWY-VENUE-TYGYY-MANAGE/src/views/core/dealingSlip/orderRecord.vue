<template>
 <basic-container>
    <!-- <p>审核记录</p> -->
    <avue-crud :data="dataList" 
               :option="option" 
               :page.sync="page"
               :permission="getPermission"
               @refresh-change="refreshChange"
               @size-change="sizeChange"
               @current-change="currentChange">        
      <template slot-scope="{type,size,row,index}" slot="menu">
          <!-- 已付款待参与的显示退款/驳回按钮 -->
          <el-button :size="size" :type="type" v-if="(row.orderStatus === 3) && (validatenull(permission.refundShow)?false:permission.refundShow)" @click="hRefund(row,index)" >退款</el-button>
          <el-button :size="size" :type="type" v-if="(row.orderStatus === 3) && (validatenull(permission.turnDownShow)?false:permission.turnDownShow)" @click="turnDown(row)" >驳回</el-button>
      </template>    
    </avue-crud>  
  </basic-container>
</template>

<script>
import { orderInquire, payRefund, payturnDown} from '@/api/dealingSlip/dealingSlip'
// import {validatenull,findMenuId} from "@/util/validate"; // 判断按钮显隐工具类
import {isShowButton} from "@/api/showButton"; // 按钮显隐
import mixin from "../../../mixins/jurisdiction";
export default {
    mixins: [mixin],
    data() {
    return {
        page: {
          currentPage: 1, 
          pageSize: 10,
          pageSizes:[1,10,20],
          total: 0, 
        },
        dataList: [],
        permission:{},
        option:{
          border:true,
          align:'center',
          menuAlign: 'center',
          stripe: true,
          menuHeaderAlign: 'center',
          labelWidth: 110,
          addBtn:false,
          // menu:false,
          menuWidth:100,
          editBtn:false,
          delBtn: false,
          index: true,
          indexLabel:'序号',
          permission: {
            refund: 'ORDER_REFUND',
            turnDown:"ORDER_RESERVE_UPDATE"
          },
          column:[
            {
              label: '订单号',
              prop:'orderNo',
              width:200,
            }, {
              label: '会员名称',
              prop:'memberName',
              width:150,
              
            }, {
              label: '订单类型',
              prop:'type',
              width: 100,
              type: 'select',
              filters: true,
              dataType:'number',
              dicUrl: window.urllist.dictUrl + 'ORDER_TYPE', 
              props: {
                label: "name",
                value: "code"
               },
              filterMethod:function(value, row) {
                return row.orderStatus === value;
              }
            }, {
              label: '订单状态',
              prop:'orderStatus',
              width: 150,
              type: 'select',
              filters: true,
              dataType:'number',
              dicUrl: window.urllist.dictUrl + 'ORDER_STATUS', 
              props: {
                label: "name",
                value: "code"
               },
              filterMethod:function(value, row) {
                return row.orderStatus === value;
              }
            },{
              label: '所属场地',
              prop:'activityName',
              width:300,
              
            }, {
              label: '所属商户名称',
              prop:'merchantName',
              width:200,
              
            },{
              label: '费用价格',
              prop:'price',
              width:80,
              
            },{
              label: '下单时间',
              prop:'orderDate',
              width:300,
            },
            {
              label: '驳回原因',
              prop:'reason',
              width: 150,
              display: true,
              hide:true
            },
          ]
        },
      };
  },
  created() {
    const menuId = this.getMenuId();

      if(this.option.permission && menuId){
        Object.keys(this.option.permission).forEach(ele => {
          isShowButton(menuId, this.option.permission[ele]).then(res => {
            this.$set(this.permission,`${ele}Show`,res);
          });
        })
      } // 按钮权限方法

    this.getTableData()
  },
    
  methods: {
    // 获取表格信息
    getTableData() {
      orderInquire({
        pageSize:this.page.pageSize,
        pageNum: this.page.currentPage,
      }).then(res => {
        const {code, data, message} = res.data
        if (code === 200) {
          // console.log(data);
          this.dataList = data.records
          this.page.total = data.total
        } else {
          console.log(message);
        }
      })
    },

    // 退款
    hRefund(row) {
      payRefund({
        orderNo:row.orderNo
      }).then(res => {
        const { code, message } = res.data
        if (code === 200) {
          this.$message.success('退款成功！');
        } else {
          this.$message.error(message);
        }
      })
    },

    // 驳回
    turnDown(row) {
      // console.log(row,index);
      this.$prompt('请输入驳回原因', '驳回原因', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
      }).then(({ value }) => {

          row.releaseStatus = 3
          row.reason = value

          payturnDown({
            id:row.id,
            orderStatus:2,
            reason:row.reason
          }).then(res => {
            console.log(res)
            const {code,message} = res.data
            if (code === 200) {
              this.$message.success('驳回成功')
              this.getTableData()
            } else {
              this.$message.error(message)
            }
          })

        }).catch(() => {
          this.$message({
            type: 'info',
            message: '取消驳回'
          });       
        });
    },

    // 分页切换
    currentChange(val) {
      this.page.currentPage = val
      this.getTableData()
    },
    sizeChange(val) {
      this.page.pageSize = val
      this.getTableData()
    },

    // 刷新
    refreshChange(){
      this.getTableData()
    },
  }

}
</script>

<style lang="scss" scoped>
.element.style{
  width: 100px;
}
</style>