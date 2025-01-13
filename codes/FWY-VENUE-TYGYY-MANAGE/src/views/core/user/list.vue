<template>

  <el-row>
    <el-col :span="5">
      <div class="box">
        <el-scrollbar>
          <basic-container>
            <avue-tree :option="treeOption"
                       :data="treeData"
                       @node-click="nodeClick">
              <span class="el-tree-node__label"
                    slot-scope="{ node }">
                <span>
                  <i class="el-icon-user-solid"></i>
                  {{ (node || {}).label }}
                </span>
              </span>
            </avue-tree>
          </basic-container>
        </el-scrollbar>
      </div>
    </el-col>
    <el-col :span="19">
      <basic-container>
        <avue-crud v-bind="bindVal"
                   v-on="onEvent"
                   :page.sync="page"
                   v-model="form"
                   :before-open="beforeOpen"
                   :before-close="beforeClose">
          <template slot="menuLeft" slot-scope="{size}">
            <el-button type="danger"
                       v-if="permission.delCodeShow===undefined?false:permission.delCodeShow"
                       icon="el-icon-delete"
                       :size="size"
                       @click.stop="deletes()">批量删除
            </el-button>

            <el-button :size="size"
                       plain
                       icon="el-icon-s-custom"
                       v-if="permission.updateRoleShow===undefined?false:permission.updateRoleShow"
                       @click="handleGrant">角色配置
            </el-button>
            <el-button :size="size"
                       plain
                       icon="el-icon-refresh"
                       v-if="permission.resetPasswordShow===undefined?false:permission.resetPasswordShow"
                       @click="handleReset">密码重置
            </el-button>

            <el-button  :size="size"
                       type="success"
                       plain
                       icon="el-icon-download"
                       v-if="permission.importUserShow===undefined?false:permission.importUserShow"
                       @click.stop="handleUpload">导入</el-button>

            <el-button type="warning"
                       :size="size"
                       plain
                       v-if="permission.excelCodeShow===undefined?false:permission.excelCodeShow"
                       icon="el-icon-upload2"
                       @click="handleExport">导出
            </el-button>

          </template>
        </avue-crud>
      </basic-container>
    </el-col>
    <el-dialog title="用户角色配置"
               append-to-body
               :visible.sync="roleBox"
               width="345px">
      <el-tree :data="roleGrantList"
               show-checkbox
               check-strictly
               default-expand-all
               node-key="id"
               ref="treeRole"
               :default-checked-keys="roleTreeObj"
               :props="roleProps">
      </el-tree>

      <span slot="footer"
            class="dialog-footer">
        <el-button @click="roleBox = false">取 消</el-button>
        <el-button type="primary"
                   @click="submitRole">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog title="导入用户数据"
               append-to-body
               :visible.sync="isUpload"
               width="555px">
      <avue-form :option="excelOption" ref="updateFrom" v-model="excelForm" :upload-after="uploadAfter">
        <template slot="excelTemplate">
          <el-button type="primary" size="small" icon="el-icon-download" @click="handleTemplate">
            点击下载
          </el-button>
        </template>
      </avue-form>
    </el-dialog>
  </el-row>
</template>

<script>
import {getRoleList, resetPassword, addRoles, getOrgTree} from '@/api/system/user'
import { jsonToParams } from '@/util/util'
import { validatenull } from '@/util/validate'
import {mapGetters} from "vuex";
export default window.$crudCommon({
  data () {
    return {
      edit: false,
      del: false,
      isResetPassword: false,
      isImportUser: false,
      excel: false,
      isAddRole: false,
      roleProps: {
        label: "name",
        value: "id"
      },
      roleTreeObj: {},
      roleGrantList: [],
      roleBox: false,
      isUpload: false,
      count: 0,
      excelBox: false,
      treeData: [],
      treeOption: {
        nodeKey: 'key',
        addBtn: false,
        menu: false,
        props: {
          label: 'name',
          value: 'id'
        }
      },
      excelForm: {
        isCover: 0
      },
      excelOption: {
        submitBtn: false,
        emptyBtn: false,
        column: [
          {
            label: '上传文件',
            prop: 'file',
            type: 'upload',
            drag: true,
            loadText: '模板上传中，请稍等',
            span: 24,
            accept: '.xls,.xlsx',
            propsHttp: {
              res: 'data'
            },
            data: {"isCover": 0},
            tip: '请上传 .xls,.xlsx 模板格式文件',
            action: window.home_url+"/jpower-user/core/user/importUser"
          },
          {
            label: "覆盖数据",
            prop: "isCover",
            type: "switch",
            dicUrl: window.urllist.dictUrl + 'YN01',
            props: {
              label: "name",
              value: "code"
            },
            hide: true,
            row:true,
          },
          {
            label: '模板下载',
            prop: 'excelTemplate',
            formslot: true,
            span: 24,
          }
        ]
      },
      isStart: true,
      selectOrgId:  ''
    }
  },
  created () {
    getOrgTree().then(res => {
      this.treeData = res.data.data;
    });
  },
  watch: {
    'form.tenantCode'(tenantCode) {
      if (!validatenull(tenantCode)) {
        if (!this.isStart){
          this.form.roleIds="";
          this.form.orgId="";
        }else {
          this.isStart = false;
        }

        getRoleList({"tenantCode": tenantCode}).then(res => {
          this.findObject(this.$refs.crud.option.group[0].column, "roleIds").dicData = res.data.data;
        });

        getOrgTree({"tenantCode": tenantCode}).then(res => {
          this.findObject(this.$refs.crud.option.group[0].column, "orgId").dicData = res.data.data;
        });
      }else {
        this.form.roleIds="";
        this.form.orgId="";
        this.findObject(this.$refs.crud.option.group[0].column, "roleIds").dicData = [];
        this.findObject(this.$refs.crud.option.group[0].column, "orgId").dicData = [];
      }
    },
    'excelForm.isCover' () {
      this.findObject(this.$refs.updateFrom.option.column, 'file').data = {"isCover" : this.excelForm.isCover};
    }
  },
  computed: {
    ...mapGetters(["website"]),
  },
  methods: {
    customizePermission (key,row) {

      if (key === 'editBtn') {
        return validatenull(this.permission.editCodeShow)?false:this.permission.editCodeShow;
      } if (key === 'addBtn') {
        return validatenull(this.permission.addCodeShow)?false:this.permission.addCodeShow;
      } else if (key === 'delBtn') {
        if (row){
          //超级用户和匿名用户不可删除
          if (row.id === this.website.rootRoleId || row.id === this.website.anonymousRoleId){
            return false;
          }
        }
        return validatenull(this.permission.delCodeShow)?false:this.permission.delCodeShow;
      } else if (key === 'viewBtn') {
        return validatenull(this.permission.viewCodeShow)?false:this.permission.viewCodeShow;
      } else if (key === 'excelBtn') {
        return validatenull(this.permission.excelCodeShow)?false:this.permission.excelCodeShow;
      } else {
        return true;
      }
    },
    beforeClose(done){
      this.isStart = true;
      done();
    },
    submitRole () {
      const roleList = this.$refs.treeRole.getCheckedKeys().join(",");
      addRoles({
        userIds: this.ids,
        roleIds: roleList
      }).then(() => {
        this.roleBox = false;
        this.$message({
          type: "success",
          message: "操作成功!"
        });
        this.getList();
      });
    },
    handleUpload () {
      this.excelForm = {}
      this.isUpload = true
    },
    handleTemplate() {
      window.open( window.urllist.user+'/core/user/downloadTemplate');
    },
    uploadAfter(res, done) {
      done();
      this.isUpload = false;
      this.refreshChange();
    },
    handleGrant () {
      if (this.selection.length <= 0) {
        this.$message.warning("请至少选择一条数据");
        return;
      }
      this.roleTreeObj = [];
      if (this.selection.length === 1) {
        this.roleTreeObj = (this.selection[0].roleIds || '').split(",");
      }


      let tenantCode = this.selection[0].tenantCode;
      let is = true;
      Object.keys(this.selection).forEach(ele => {
        if (tenantCode !== this.selection[ele].tenantCode){
          is = false;
        }
      });
      if (is){
        let param = {};
        if (this.$store.getters.isShowTenantCode){
          param = {"tenantCode": tenantCode};
        }
        getRoleList(param).then(res => {
          this.roleGrantList = res.data.data;
          this.roleBox = true;
        });
      }else {
        this.$message.warning("只能选择同一租户的用户设置角色");
      }
    },
    handleExport(){

      this.$confirm("是否导出当前查询用户数据?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        window.open( window.urllist.user+'/core/user/exportUser?'+jsonToParams(this.$refs.crud.$refs.headerSearch.searchForm));
      });
    },
    handleReset () {
      if (this.selection.length === 0) {
        this.$message.warning("请至少选择一条数据");
        return;
      }
      this.$confirm("确定将选择账号密码重置?", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          return resetPassword(this.ids);
        })
        .then(() => {
          this.$message({
            type: "success",
            message: "操作成功!"
          });
          this.$refs.crud.toggleSelection();
        });
    },
    nodeClick (data) {
      this.selectOrgId = data.id;
      this.params.orgId = data.id;
      this.page.currentPage = 1;
      this.getList();
    },
    searchBefore(){
      if (!this.validatenull(this.selectOrgId)) {
        this.params.orgId = this.selectOrgId;
      }
    },
    searchResetAfter(){
      this.selectOrgId = '';
    },
    beforeOpen(done,type){
      if (["add"].includes(type)) {
        this.findObject(this.$refs.crud.option.group[0].column, 'tenantCode').value=this.$store.getters.tenantCode;
      }
      done();
    }
  }
}, {
  name: '/system/user',
  data: 'list'
})
</script>

<style lang="scss" scoped>
</style>