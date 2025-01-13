<template>
  <basic-container>
    <avue-crud v-bind="bindVal"
               v-on="onEvent"
               v-model="form"
               @tree-load="treeLoad"
               ref="crud">
      <template slot-scope="{row}"
                slot="menu">
        <el-button type="text"
                   icon="el-icon-setting"
                   size="small"
                   plain
                   v-permission="'SYSTEM_DATASCOPE_LISTPAGE'"
                   v-if="isShowRoleBth(row)"
                   style="border: 0;background-color: transparent !important;"
                   @click.stop="handleDataScope(row)">权限配置
        </el-button>
      </template>

      <template slot="menuRight" slot-scope="{size}">

        <avue-select v-model="clientId"
                     placeholder="请选择客户端"
                     :clearable="false"
                     style="margin-right: 10px;width: 50%!important;"
                     type="tree"
                     :props="{label: 'name',value: 'id'}"
                     :dic="clientList"
                     :size="size"></avue-select>

      </template>

    </avue-crud>
    <el-drawer :title="`[${scopeMenuName}] 数据权限配置`"
               :visible.sync="drawerVisible"
               :direction="direction"
               append-to-body
               :before-close="handleDrawerClose"
               size="1000px">
      <basic-container>
        <avue-crud :option="optionScope"
                   :data="dataScope"
                   :page="pageScope"
                   v-model="formScope"
                   :before-open="beforeOpen"
                   :table-loading="scopeLoading"
                   :search.sync="searchScope"
                   v-permission="{addBtn:'SYSTEM_DATASCOPE_ADD',editBtn:'SYSTEM_DATASCOPE_UPDATE',viewBtn: 'SYSTEM_DATASCOPE_DETAIL', delBtn: 'SYSTEM_DATASCOPE_DELETE'}"
                   ref="crudScope"
                   @row-del="rowDelScope"
                   @row-update="rowUpdateScope"
                   @row-save="rowSaveScope"
                   @search-change="searchChangeScope"
                   @search-reset="searchResetScope"
                   @selection-change="selectionChangeScope"
                   @current-change="currentChangeScope"
                   @size-change="sizeChangeScope">

          <template slot="scopeType" slot-scope="scope">
            <el-tag>{{scope.row.scopeTypeStr}}</el-tag>
          </template>

          <template slot="allRole" slot-scope="scope">
            <el-tag>{{scope.row.params.allRole}}</el-tag>
          </template>

        </avue-crud>
      </basic-container>
    </el-drawer>
  </basic-container>
</template>

<script>
import {
  addDataScope,
  removeDataScope,
  updateDataScope,
  getListDataScope,
} from "@/api/system/dataScope";
import func from "@/util/func";
import {validatenull} from "@/util/validate";

export default window.$crudCommon({
  data () {
    return {
      clientId: '',
      clientList: [],
      flag: false,
      drawerVisible: false,
      direction: 'rtl',
      scopeMenuId: 0,
      scopeMenuCode: '',
      scopeMenuName: "菜单",
      scopeLoading: false,
      menu: true,
      watchMode: true,
      data: [],
      formScope: {},
      searchScope: {},
      selectionListScope: [],
      pageScope: {
        pageSize: 10,
        currentPage: 1,
        total: 0
      },
      optionScope: {
        index: true,
        indexLabel: '序号',
        tip: false,
        searchShow: true,
        searchMenuSpan: 6,
        border: true,
        viewBtn: true,
        columnBtn: false,
        refreshBtn: false,
        align: 'center',
        menuWidth: 200,
        dialogWidth: 900,
        dialogClickModal: false,
        column: [
          {
            label: "权限名称",
            prop: "scopeName",
            search: true,
            value: "",
            rules: [{
              required: true,
              message: "请输入数据权限名称",
              trigger: "blur"
            }]
          },
          {
            label: "权限编号",
            prop: "scopeCode",
            search: true,
            width: 100,
            rules: [{
              required: true,
              message: "请输入数据权限编号",
              trigger: "blur"
            }]
          },
          {
            label: "权限类型",
            type: "select",
            prop: 'scopeType',
            search: true,
            slot: true,
            dicUrl: window.urllist.dictUrl + 'DATA_SCOPE_TYPE',
            props: {
              label: "name",
              value: "code"
            },
            dataType: "number",
            width: 140,
            control: (val) => {
              return {
                scopeValue: {
                  display: val === 5
                },
                scopeColumn: {
                  display: ![1, 5].includes(val)
                }
              }
            },
            rules: [{
              required: true,
              message: "请选择权限类型",
              trigger: "blur"
            }],
          },
          {
            label: "权限字段",
            prop: "scopeColumn",
            width: 130,
            hide: true,
            display: true,
            rules: [{
              required: true,
              message: "请输入数据权限字段",
              trigger: "blur"
            }]
          },
          {
            label: "Mapper方法",
            prop: "scopeClass",
            span: 24,
            hide: true,
            rules: [{
              required: true,
              message: "请输入MybatisMapper对应方法的完整类名路径",
              trigger: "blur"
            }],
          },
          {
            label: "权限值域",
            prop: "scopeValue",
            span: 24,
            minRows: 5,
            type: "textarea",
            tip: "输入正常sql的where条件后面得部分即可，例如：login_count > 1 and id = '{userId}' and address in ({roleIds});参数可以使用UserInfo类里属性",
            display: true,
            hide: true,
          },
          {
            label: "所有角色都执行",
            prop: "allRole",
            labelWidth: 130,
            value: 0,
            span: 24,
            slot: true,
            type: "switch",
            dicUrl: window.urllist.dictUrl + 'YN01',
            dataType: 'number',
            props: {
              label: "name",
              value: "code"
            },
            rules: [
              {
                required: true,
                message: "请选择是否所有角色都执行",
                trigger: "blur"
              }
            ]
          },
          {
            label: "备注",
            prop: "remark",
            span: 24,
            type: 'textarea',
            hide: true,
          }

        ]
      },
      dataScope: []
    };
  },
  created () {

    this.api['clientList']().then(res => {

      if (validatenull(res.data.data)){
        this.$message.error('请先新建客户端');
        return;
      }

      this.clientList = res.data.data;
      this.clientId = res.data.data[0].id;
    });

  },
  watch: {
    'formScope.scopeType' () {
      if (!this.flag) this.initScope();

    },
    'formScope.scopeColumn' () {
      if (!this.flag) this.initScopeValue();
    },
    'clientId'(){
      this.getList();
    }
  },
  computed: {

    scopeIds () {
      let ids = [];
      this.selectionListScope.forEach(ele => {
        ids.push(ele.id);
      });
      return ids.join(",");
    }
  },
  methods: {
    beforeOpen (done) {
      this.flag = true
      setTimeout(() => {
        this.flag = false
      }, 0)
      done()
    },
    listBefore(done){
      if (this.clientId){
        this.params.clientId_eq = this.clientId;
        done();
      }
    },
    treeLoad (tree, treeNode, resolve) {
      this.api[this.option.list || 'list']({ parentId_eq: tree.id,clientId_eq: this.clientId }).then(res => {
        resolve(res.data.data);
      });
    },
    initScopeValue () {
      let column = this.formScope.scopeColumn, scopeValue = '';
      const scopeType = func.toInt(this.formScope.scopeType);
      if (scopeType === 2) {
        scopeValue = column + ' = {userId}'
      } else if (scopeType === 3) {
        scopeValue = column + ' = {orgId}'

      } else if (scopeType === 4) {
        scopeValue = column + ' in {orgId}'
      }
      this.formScope.scopeValue = scopeValue;
    },
    initScope () {
      const scopeType = func.toInt(this.formScope.scopeType);
      let column = "-", name = "暂无", scopeValue = '';
      if (scopeType === 1) {
        column = "-";
        name = "全部可见";
      } else if (scopeType === 2) {
        column = "create_user";
        name = "本人可见";
        scopeValue = 'create_user = {userId}'
      } else if (scopeType === 3) {
        column = "create_org";
        name = "所在机构可见";
        scopeValue = 'create_org = {orgId}'

      } else if (scopeType === 4) {
        column = "create_org";
        name = "所在机构可见及子级可见";
        scopeValue = 'create_org in {orgId}'

      } else if (scopeType === 5) {
        column = "create_user";
        name = "自定义";
      }
      this.formScope.scopeValue = scopeValue;
      this.formScope.scopeColumn = column;
      this.formScope.scopeName = `${this.scopeMenuName} [${name}]`;
    },
    isShowRoleBth (row) {
      return !(row.url.startsWith("http") || row.hasChildren);

    },
    // 数据权限模块
    handleDataScope (row) {
      this.drawerVisible = true;
      this.scopeMenuId = row.id;
      this.scopeMenuCode = row.code;
      this.scopeMenuName = row.functionName;
      this.onLoadScope(this.pageScope)
    },
    handleDrawerClose (hide) {
      hide();
    },
    rowSaveScope (row, done, loading) {
      row = {
        ...row,
        menuId: this.scopeMenuId,
      };
      addDataScope(row).then(() => {
        this.onLoadScope(this.pageScope);
        this.$message({
          type: "success",
          message: "操作成功!"
        });
        done();
      }, error => {
        console.log(error);
        if (loading) {
          loading();
        } else {
          done();
        }
      });
    },
    rowUpdateScope (row, index, done, loading) {
      row = {
        ...row,
        menuId: this.scopeMenuId,
      };

      updateDataScope(row).then(() => {
        this.onLoadScope(this.pageScope);
        this.$message({
          type: "success",
          message: "操作成功!"
        });
        done();
      }, error => {
        console.log(error);
        if (loading) {
          loading();
        } else {
          done();
        }
      });
    },
    rowDelScope (row) {
      this.$confirm("确定将选择数据删除?", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
          .then(() => {
            return removeDataScope(row.id);
          })
          .then(() => {
            this.onLoadScope(this.pageScope);
            this.$message({
              type: "success",
              message: "操作成功!"
            });
          });
    },
    handleDeleteScope () {
      if (this.selectionListScope.length === 0) {
        this.$message.warning("请选择至少一条数据");
        return;
      }
      this.$confirm("确定将选择数据删除?", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
          .then(() => {
            return removeDataScope(this.scopeIds);
          })
          .then(() => {
            this.onLoadScope(this.pageScope);
            this.$message({
              type: "success",
              message: "操作成功!"
            });
            this.$refs.crudScope.toggleSelection();
          });
    },
    searchResetScope () {
      this.pageScope.currentPage = 1;
      this.onLoadScope(this.pageScope);
    },
    searchChangeScope (params, done) {
      this.pageScope.currentPage = 1;
      this.onLoadScope(this.pageScope, params);
      done();
    },
    selectionChangeScope (list) {
      this.selectionListScope = list;
    },
    currentChangeScope (currentPage) {
      this.pageScope.currentPage = currentPage;
      this.onLoadScope(this.pageScope, this.searchScope);
    },
    sizeChangeScope (pageSize) {
      this.pageScope.currentPage = 1;
      this.pageScope.pageSize = pageSize;
      this.onLoadScope(this.pageScope, this.searchScope);
    },
    onLoadScope (page, params = {}) {
      this.scopeLoading = true;
      const values = {
        ...params,
        menuId_eq: this.scopeMenuId,
        pageNum: page.currentPage,
        pageSize: page.pageSize
      }
      getListDataScope(Object.assign(values, this.query)).then(res => {
        const data = res.data.data;
        this.pageScope.total = data.total;
        this.dataScope = data.list;
        this.selectionListScope = [];
        this.scopeLoading = false;
      });
    }
  }
}, {
  onLoad: false,
  name: '/system/dataScope',
  res: (data) => {
    return {
      data: data.data
    }
  },
  crudPage: false
});
</script>

