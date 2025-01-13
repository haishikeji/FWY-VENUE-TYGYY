<template>
  <basic-container>
    <avue-crud v-bind="bindVal"
               v-on="onEvent"
               :page.sync="page"
               v-permission="{addBtn: 'SYSTEM_TENANT_ADD',editBtn: 'SYSTEM_TENANT_UPDATE',delBtn: 'SYSTEM_TENANT_DELETE'}"
               v-model="form">

      <template slot="menuLeft" slot-scope="scope">
        <el-button type="danger" v-permission="'SYSTEM_TENANT_DELETE'" icon="el-icon-delete" size="small" @click.stop="deletes()">批量删除</el-button>
        <el-button v-permission="'SYSTEM_TENANT_SETTING'" icon="el-icon-setting" size="small" @click.stop="openSettiong">授权配置</el-button>
      </template>

      <template slot="accountNumber" slot-scope="scope">
        <el-tag v-if="scope.row.accountNumber === -1">不限制</el-tag>
        <el-tag v-else>{{scope.row.accountNumber}}</el-tag>
      </template>

      <template slot="expireTime" slot-scope="scope">
        <el-tag v-if="scope.row.expireTime">{{scope.row.expireTime}}</el-tag>
        <el-tag v-else>不限制</el-tag>
      </template>

    </avue-crud>

    <el-dialog class="avue-dialog" destroy-on-close append-to-body :show-close="true" :close-on-press-escape="true" :close-on-click-modal="true" title="授权配置"  :visible.sync="isSetting" :lock-scroll="false" width="30%"><!--359.39px-->
      <avue-form ref="setting" v-model="setting" :option="settingOption" @submit="handleSetting">
        <template slot-scope="scope" slot="menuForm">
          <el-button type="primary" @click="handleSetting">提交</el-button>
          <el-button @click.native="isSetting=false">取消</el-button>
        </template>
      </avue-form>

    </el-dialog>

  </basic-container>
</template>

<script>


export default window.$crudCommon({
  data () {
    return {
      isSetting: false,
      setting: {
        accountNumber: -1,
        expireTime: '',
      },
      settingOption: {
        emptyBtn: false,
        submitBtn:false,
        column:[
          {
            label: "帐号额度",
            prop: "accountNumber",
            type: 'number',
            span: 24,
            precision:0,
            // row:true
          },
          {
            label: "过期时间",
            prop: "expireTime",
            type: "date",
            format: 'yyyy-MM-dd',
            valueFormat:'yyyy-MM-dd 12:00:00',
            span: 24,
            // row:true
          }
        ]
      }
    }
  },
  created () {
  },
  watch: {
  },
  methods: {
   openSettiong (){
     if (this.selection.length > 0){
       if (this.selection.length === 1){
         this.setting.accountNumber = this.selection[0].accountNumber;
         this.setting.expireTime = this.selection[0].expireTime;
       }else{
         this.setting.accountNumber = -1;
         this.setting.expireTime = '';
       }
       this.isSetting = true;
     }else{
       this.$message.warning("至少选中一条数据");
     }
   },
    handleSetting(){
        this.$NProgress.start();
        this.api['setting']({
          ids: this.ids,
          accountNumber: this.setting.accountNumber,
          expireTime: this.setting.expireTime
        }).then((data) => {
          if (data.status){
            this.$NProgress.done();
            this.$message.success("设置成功");
            this.isSetting = false;
            this.getList();
          }else{
            this.error.success(data.message);
          }
        }).catch(() =>{
          this.$NProgress.done();
        })
      }
  }
}, {
  name: '/system/tenant',
  res: (data) => {
    return {
      total: data.data.total,
      data: data.data.list
    }
  }
})
</script>

<style lang="scss" scoped>

.avue-crud .avue-input-number, .avue-crud .el-cascader, .avue-crud .el-date-editor.el-input, .avue-crud .el-date-editor.el-input__inner, .avue-crud .el-select, .avue-form .avue-input-number, .avue-form .el-cascader, .avue-form .el-date-editor.el-input, .avue-form .el-date-editor.el-input__inner, .avue-form .el-select{
  width: 100%;
}

</style>
