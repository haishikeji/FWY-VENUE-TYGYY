<template>
  <basic-container>
    <avue-crud v-bind="bindVal"
               v-on="onEvent"
               :page.sync="page"
               :search.sync="search"
               v-model="form">

      <template slot-scope="{disabled,size}" slot="nameSearch">
        <avue-select v-model="search.name_eq" placeholder="请选择服务名称" :disabled="disabled" :size="size" type="tree" :props="{label: 'name',value: 'name'}" :dic="serverList"></avue-select>
      </template>

      <template slot="menuLeft" slot-scope="scope">
        <el-button type="warning"
                   size="small"
                   plain
                   v-if="validatenull(permission.excelCodeShow)?false:permission.excelCodeShow"
                   icon="el-icon-upload2"
                   @click="handleExport">导出
        </el-button>
      </template>

      <template slot="method" slot-scope="scope">
        <el-tag>{{scope.row.method}}</el-tag>
      </template>

      <template slot="isSuccess" slot-scope="scope">
        <el-tag>{{scope.row.isSuccessStr}}</el-tag>
      </template>

      <template slot="responseTime" slot-scope="scope">
        <el-tag v-if="scope.row.responseTime">{{scope.row.responseTime}}毫秒</el-tag>
      </template>
    </avue-crud>
  </basic-container>
</template>

<script>

import {jsonToParams} from "@/util/util";

export default window.$crudCommon({
  data () {
    return {
      serverList: [],
      search: {
      }
    }
  },
  created () {
    this.api['server']().then((res) => {
      this.serverList = res.data.data.map(item => {
        return {...item, desc: item.location}
      });
    })
  },
  methods: {
    handleExport(){
      this.$confirm("是否导出当前查询数据?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        let params = jsonToParams(this.$refs.crud.$refs.headerSearch.searchForm);
        if (params){
          params = params + '&';
        }
        params = params + jsonToParams(this.params);
        window.open( window.urllist.log+'/monitor/log/export?'+params);
      });
    }
  }
}, {
  name: '/log/monitor/result',
  data: 'records'
})
</script>

<style lang="scss" scoped>
</style>