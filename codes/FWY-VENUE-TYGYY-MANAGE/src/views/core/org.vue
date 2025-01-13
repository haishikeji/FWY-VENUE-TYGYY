<template>
  <basic-container>
    <avue-crud v-bind="bindVal"
               v-on="onEvent"
               :page.sync="page"
               v-model="form"
               v-permission="{delBtn: 'SYSTEM_ORG_DELETE',addBtn: 'SYSTEM_ORG_ADD',editBtn: 'SYSTEM_ORG_UPDATE'}"
               :before-open="beforeOpen"
               :before-close="beforeClose"
               @tree-load="treeLoad">

      <template slot="menuLeft" slot-scope="scope">
        <el-button type="danger" v-permission="'SYSTEM_ORG_DELETE'" icon="el-icon-delete" size="small" @click.stop="deletes()">批量删除</el-button>
      </template>

      <template slot="menu"
                slot-scope="scope">
        <el-button type="text"
                   v-permission="'ORG_CHILD_ADD'"
                   icon="el-icon-plus"
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
import {addItemById, delItemById, treeAddDisabled} from "@/util/util";
import func from "@/util/func";

export default window.$crudCommon({
  data () {
    return {
      oldEditForm: {},
      tenantCodeThis: ""
    }
  },
  created () {
  },
  watch: {
    "form.tenantCode"(tenantCode){
      if (!this.validatenull(tenantCode)){
        this.api['selectTree']({"tenantCode": tenantCode}).then((res) => {
          this.findObject(this.$refs.crud.option.column, 'parentId').dicData = treeAddDisabled(res.data.data || [], this.form.id);
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
        let list = res.data.data;
        list.forEach(ele => {
          ele.children = [];
        });
        resolve(list);
      });
    },
    addAfter (row, done, data) {
      done(Object.assign(row, {
        id: data.data,
        children: []
      }));
      this.$message.success('新增成功')
    },
    updateAfter (row, done) {
      done(row);
      this.$message.success('修改成功');

      //动态改变结构
      if (this.oldEditForm.parentId !== row.parentId){
        if (row.parentId === undefined){
          row.parentId = '-1';
        }
        const data = delItemById(this.$refs.crud.data,row.id);

        if (data){
          //avue更新后会丢失箭头，所以我这里不管改没改上级ID，都去修改下；
          const index = this.oldEditForm.parentId !== row.parentId ? 0 : data.index;
          addItemById(this.$refs.crud.data,Object.assign(data.data, row),index);
        }
      }

    },
    delAfter (row, done) {
      done(row);
      this.$message.success('删除成功');
    },
    delsAfter (ids) {
      func.split(ids).forEach(id=> {
        delItemById(this.$refs.crud.data,id);
      })
      this.$message.success('删除成功');
    },
    handleAdd (row) {
      const parentId = this.findObject(this.$refs.crud.option.column, 'parentId')
      parentId.value = row.id;
      parentId.addDisabled = true;
      this.tenantCodeThis = row.tenantCode;
      this.$refs.crud.rowAdd();
    },
    beforeClose(done){
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
      if (["edit"].includes(type)) {
        this.oldEditForm = this.form;
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
