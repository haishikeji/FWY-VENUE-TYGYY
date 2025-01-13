<template>
  <basic-container>
    <avue-crud v-bind="bindVal"
               v-on="onEvent"
               :page.sync="page"
               :search.sync="searchFrom"
               v-model="form">

      <template slot-scope="{disabled,size}" slot="businessTypeStrSearch">
        <avue-select v-model="searchFrom.businessType_eq" placeholder="请选择请求业务类型" :disabled="disabled" :size="size" type="tree" :props="{label: 'name',value: 'code'}" :dic="businessTypeList"></avue-select>
      </template>

      <template slot-scope="{disabled,size}" slot="clientCodeSearch">
        <el-input placeholder="请输入客户端编码" :disabled="disabled" :size="size" v-model="searchFrom.clientCode_eq"></el-input>
      </template>

      <template slot="createTime" slot-scope="scope">
        <el-tag>{{scope.row.createTime}}</el-tag>
      </template>

      <template slot="businessTypeStr" slot-scope="scope">
        <el-tag v-if="scope.row.businessTypeStr">{{scope.row.businessTypeStr}}</el-tag>
      </template>

      <template slot="statusStr" slot-scope="scope">
        <el-tag>{{scope.row.statusStr}}</el-tag>
      </template>

    </avue-crud>

  </basic-container>
</template>

<script>


import request from "@/router/axios";

export default window.$crudCommon({
  data () {
    return {
      isDisplayShow: false,
      businessTypeList: [],
    }
  },
  created () {
    request.get(window.urllist.dictUrl + 'BUSINESS_TYPE').then((res) => {
      this.businessTypeList = res.data.data;
    })
  },
  watch: {
    'form.status' (status) {
      this.isDisplayShow = status === 1;
    }
  },
  methods: {
  }
}, {
  name: '/log/operate',
  res: (data) => {
    return {
      total: data.data.total,
      data: data.data.records
    }
  }
})
</script>

<style lang="scss" scoped>

</style>
