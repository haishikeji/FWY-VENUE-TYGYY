<template>
  <basic-container>
    <avue-crud v-bind="bindVal"
               v-on="onEvent"
               :page.sync="page"
               v-model="form"
               :before-open="beforeOpen"
               :before-close="beforeClose"
               @tree-load="treeLoad">

      <template slot="menuLeft" slot-scope="scope">
        <el-button type="danger" v-if="validatenull(permission.delCodeShow)?false:permission.delCodeShow" icon="el-icon-delete" size="small" @click.stop="deletes()">批量删除</el-button>
      </template>

      <template slot="menu"
                slot-scope="scope">
        <el-button type="text"
                   v-if="validatenull(permission.addCodeShow)?false:permission.addCodeShow"
                   icon="el-icon-circle-plus-outline"
                   @click.stop="handleAdd(scope.row,scope.index)"
                   size="small">新增子项
        </el-button>
      </template>

      <template slot="isVirtual" slot-scope="scope">
        <el-tag>{{scope.row.$isVirtual}}</el-tag>
      </template>

    </avue-crud>
  </basic-container>
</template>

<script>
export default window.$crudCommon({
  data () {
    return {
      tenantCodeThis: ""
    }
  },
  created () {
  },
  watch: {
    "form.tenantCode"(tenantCode){
      if (!this.validatenull(tenantCode)){
        this.api['selectTree']({"tenantCode": tenantCode}).then((res) => {
          this.findObject(this.$refs.crud.option.column, 'parentId').dicData=res.data.data;
        });
      }
    }
  },
  methods: {
    treeLoad(tree, treeNode, resolve){
      this.api['listByparent']({
        ...this.params,
        parentId: tree.id
      }).then(res => {
        resolve(res.data.data);
      });
    },
    handleAdd (row) {
      const parentId = this.findObject(this.$refs.crud.option.column, 'parentId')
      parentId.value = row.id;
      parentId.addDisabled = true;
      this.tenantCodeThis = row.tenantCode;
      this.$refs.crud.rowAdd();
    }
    ,beforeClose(done){
      const parentId = this.findObject(this.$refs.crud.option.column, 'parentId');
      parentId.addDisabled = false;
      parentId.value="";
      this.tenantCodeThis = "";
      done();
    },
    beforeOpen(done,type){
      if (["add"].includes(type)) {
        const tenantCode = this.findObject(this.$refs.crud.option.column, 'tenantCode');
        if (this.validatenull(this.tenantCodeThis)){
          tenantCode.value=this.$store.getters.tenantCode;
        }else{
          tenantCode.value=this.tenantCodeThis;
        }
      }

      done();
    }
  }
}, {
  name: '/system/org',
  res: (data) => {
    return {
      total: data.data.total,
      data: data.data.list
    }
  }
});
</script>

<style lang="scss" scoped>
</style>
