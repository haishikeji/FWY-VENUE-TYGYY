<template>
  <basic-container>
    <avue-crud v-bind="bindVal"
               v-on="onEvent"
               v-model="form"
               v-permission="{addBtn: 'SYSTEM_ROLE_ADD',editBtn: 'SYSTEM_ROLE_UPDATE',delBtn: 'SYSTEM_ROLE_DELETE'}"
               :before-open="beforeOpen"
               ref="crud">

      <template slot="menuLeft" slot-scope="{size}">
        <el-button type="danger"
                   v-permission="'SYSTEM_ROLE_DELETE'"
                   icon="el-icon-delete"
                   :size="size"
                   @click.stop="deletes()">批量删除
        </el-button>
      </template>

      <template slot="menuBtn"
                slot-scope="scope">

        <el-dropdown-item divided
                          icon="el-icon-user"
                          v-permission="'SYSTEM_ROLE_USER'"
                          @click.native="handleUser(scope.row,scope.index)"
                          size="small">角色成员
        </el-dropdown-item>

        <el-dropdown-item
                          size="small"
                          v-permission="'ROLE_TOPMENU_ID'"
                          icon="el-icon-guide"
                          @click.native="handleMenu(scope.row,scope.index)">顶级菜单
        </el-dropdown-item>

        <el-dropdown-item
                          size="small"
                          v-permission="'SYSTEM_ROLE_SELECT_URL'"
                          icon="el-icon-paperclip"
                          @click.native="handleRole(scope.row,scope.index)">功能权限
        </el-dropdown-item>

        <el-dropdown-item
                          size="small"
                          v-permission="'SYSTEM_DATASCOPE_LIST'"
                          icon="el-icon-s-data"
                          @click.native="handleData(scope.row,scope.index)">数据权限
        </el-dropdown-item>

      </template>

    </avue-crud>

    <el-drawer
        :title="`[${roleUserName}] 角色成员`"
        append-to-body
        size="1000px"
        :visible.sync="userBox">

      <basic-container>
        <avue-crud :option="optionUser"
                   :data="dataUser"
                   :page.sync="pageUser"
                   :table-loading="userLoading"
                   :search.sync="searchUser"
                   ref="tableUser"
                   @search-change="searchChangeUser"
                   @search-reset="searchResetUser"
                   @current-change="currentChangeUser"
                   @size-change="sizeChangeUser">

          <template slot="menuLeft" slot-scope="{size}">
            <el-button v-permission="'SYSTEM_ROLE_ADDUSER'" icon="el-icon-plus" type="primary" :size="size" @click.stop="roleUser()">添加</el-button>
            <el-button v-permission="'SYSTEM_ROLE_DELUSER'" icon="el-icon-delete" type="danger" :size="size" @click.stop="deleteUser()" :loading="deleteUserLoading">去除</el-button>
          </template>

          <template slot-scope="{disabled,size}" slot="userTypeStrSearch">
            <avue-select v-model="searchUser.userType_eq" placeholder="请选择用户类型" :disabled="disabled" :size="size" type="tree" :props="{label: 'name',value: 'code'}" :dic="userTypeList"></avue-select>
          </template>
          <template slot-scope="{disabled,size}" slot="orgNameSearch">
            <avue-input-tree v-model="searchUser.orgId_eq" placeholder="请选择部门" :disabled="disabled" :size="size" type="tree" :props="{label: 'name',value: 'id'}" :dic="orgList"></avue-input-tree>
          </template>

        </avue-crud>
      </basic-container>

    </el-drawer>

    <el-dialog class="avue-dialog"
               title="添加成员"
               top="20px"
               append-to-body
               :visible.sync="roleUserBox">

      <avue-crud :option="optionRoleUser"
                 :data="dataRoleUser"
                 :page="pageRoleUser"
                 :table-loading="roleUserLoading"
                 :search.sync="searchRoleUser"
                 ref="tableRoleUser"
                 @search-change="searchChangeRoleUser"
                 @search-reset="searchResetRoleUser"
                 @current-change="currentChangeRoleUser"
                 @size-change="sizeChangeRoleUser">

        <template slot-scope="{disabled,size}" slot="userTypeStrSearch">
          <avue-select v-model="searchRoleUser.userType_eq" placeholder="请选择用户类型" :disabled="disabled" :size="size" type="tree" :props="{label: 'name',value: 'code'}" :dic="userTypeList"></avue-select>
        </template>
        <template slot-scope="{disabled,size}" slot="orgNameSearch">
          <avue-input-tree v-model="searchRoleUser.orgId_eq" placeholder="请选择部门" :disabled="disabled" :size="size" type="tree" :props="{label: 'name',value: 'id'}" :dic="orgList"></avue-input-tree>
        </template>

      </avue-crud>

      <span slot="footer"
            class="dialog-footer">
        <el-button @click="roleUserBox = false">取 消</el-button>
        <el-button type="primary" :loading="addUserLoading" @click="addUser">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog class="avue-dialog" :title="this.menuTitle"
               append-to-body
               :visible.sync="menuBox">
      <el-tree :data="this.allTopMenu"
               show-checkbox
               check-on-click-node
               default-expand-all
               node-key="id"
               ref="topMenu"
               :default-checked-keys="this.topMenuId"
               :props="{label: 'name',value: 'id'}">
      </el-tree>

      <span slot="footer" class="dialog-footer">
            <el-button @click="menuBox = false">取 消</el-button>
            <el-button v-permission="'ROLE_TOPMENU_SAVE'" type="primary" @click="submitTopMenu">确 定</el-button>
        </span>
    </el-dialog>

    <el-dialog class="avue-dialog" title="配置功能权限"
               append-to-body
               :visible.sync="box">

      <div class="block">
          <span class="demonstration">当前客户端:&nbsp;&nbsp;</span>
          <avue-select v-model="clientId"
                       label="当前客户端"
                       placeholder="请选择客户端"
                       :clearable="false"
                       class="el-input--small el-input--suffix"
                       type="tree"
                       :props="{label: 'name',value: 'id'}"
                       :dic="clientList"></avue-select>
      </div>

      <el-container >
        <el-aside width="200px">
          <p class="el-dialog__title">菜单</p>
          <el-tree :data="menuGrantList"
                   node-key="id"
                   ref="treeMenu"
                   style="max-height: 450px;"
                   :show-checkbox="!boxDisabled"
                   defaultExpandAll
                   :expandOnClickNode="false"
                   :default-checked-keys="defaultFunctionIds"
                   :props="{label: 'functionName',value: 'id'}"
                   @check="checkChange"
                   @node-click="listFunction">
          </el-tree>
        </el-aside>
        <el-main>
          <p class="el-dialog__title">接口、按钮</p>
          <avue-crud ref="functionTable"
                     :data="functionList"
                     :option="optionFunction"
                     :table-loading="boxLoading"
                     @selection-change="functionSelectionChange">

            <template slot="functionType" slot-scope="scope">
              <el-tag :type="scope.row.functionType===0?'warning':'success'">{{scope.row.params.functionType}}</el-tag>
            </template>

          </avue-crud>
        </el-main>
      </el-container>

      <span slot="footer"
            class="dialog-footer">
        <el-button v-loading="boxDisabled" @click="box = false">取 消</el-button>
        <el-button v-loading="boxDisabled" v-permission="'SYSTEM_ROLE_UPDATEFUNCTION'" type="primary" @click="submit">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog class="avue-dialog" title="配置数据权限"
               append-to-body
               :visible.sync="databox">

      <div class="block">
        <span class="demonstration">当前客户端:&nbsp;&nbsp;</span>
        <avue-select v-model="clientId"
                     label="当前客户端"
                     placeholder="请选择客户端"
                     :clearable="false"
                     class="el-input--small el-input--suffix"
                     type="tree"
                     :props="{label: 'name',value: 'id'}"
                     :dic="clientList"></avue-select>
      </div>

      <el-container>
        <el-aside width="200px">
          <p class="el-dialog__title">菜单</p>
          <el-tree :data="menuData"
                   node-key="id"
                   ref="treeData"
                   style="max-height: 450px;"
                   defaultExpandAll
                   :expandOnClickNode="false"
                   :props="{label: 'functionName',value: 'id'}"
                   @node-click="listDataScope">
          </el-tree>
        </el-aside>
        <el-main>
          <p class="el-dialog__title">数据权限</p>
          <avue-crud ref="dataScopeTable"
                     :data="scopedList"
                     :option="optionScope"
                     :table-loading="boxDataLoading"
                     @selection-change="currentRowChange">

            <template slot="allRole" slot-scope="scope">
              <el-tag>{{scope.row.params.allRole}}</el-tag>
            </template>

          </avue-crud>
        </el-main>
      </el-container>

      <span slot="footer"
            class="dialog-footer">
        <el-button v-loading="dataBoxDisabled" @click="databox = false">取 消</el-button>
        <el-button v-loading="dataBoxDisabled" v-permission="'SYSTEM_DATASCOPE_ROLE'" type="primary" @click="submitDataScope">确 定</el-button>
      </span>
    </el-dialog>
  </basic-container>
</template>

<script>
import {menuTree} from '@/api/system/menuList';
import {
  addFunction,
  addRoleUser,
  deleteRoleUser,
  listButByMenu,
  listByMenuId,
  listByRole,
  listIdByRoleId,
  queryUrlIdByRole,
  roleDataScope
} from '@/api/system/roleTree'
import request from '@/router/axios';
import {treeAddDisabled , delItemById , addItemById} from '@/util/util'
import {validatenull} from "@/util/validate";
import func from "@/util/func";

export default window.$crudCommon({
  data () {
    return {
      clientId: '',
      clientList: [],
      oldEditForm: {},
      form: {},
      box: false,
      boxLoading: false,
      boxDataLoading: false,
      boxDisabled: false,
      dataBoxDisabled: false,
      databox: false,
      userBox: false,
      roleUserBox: false,
      roleUserName: '',
      dataIds: [],
      list: [],
      scopedList: [],
      menuGrantList: [],
      functionIds: [],
      defaultFunctionIds: [],
      menuData: [],
      userTypeList: [],
      orgList: [],
      functionList: [],
      optionFunction: {
        index: true,
        indexFixed: false,
        header: false,
        indexLabel: '序号',
        selection: true,
        selectionFixed: false,
        tip: true,
        searchShow: false,
        menu: false,
        border: true,
        columnBtn: false,
        addBtn: false,
        refreshBtn: false,
        align: 'center',
        maxHeight: 450,
        column: [
          {
            label: "功能名称",
            prop: "functionName"
          },
          {
            label: "别名",
            prop: "alias"
          },
          {
            label: "编码",
            prop: "code"
          },
          {
            label: "路由",
            prop: "url"
          },
          {
            label: "类型",
            prop: "functionType",
            slot: true
          }
        ]
      },
      optionScope: {
        index: true,
        indexFixed: false,
        header: false,
        indexLabel: '序号',
        selection: true,
        selectionFixed: false,
        tip: true,
        searchShow: false,
        menu: false,
        border: true,
        columnBtn: false,
        addBtn: false,
        refreshBtn: false,
        align: 'center',
        maxHeight: 450,
        rowKey: 'id',
        column: [
          {
            label: "权限编号",
            prop: "scopeCode"
          },
          {
            label: "数据权限名称",
            prop: "scopeName"
          },
          {
            label: "是否所有角色都执行",
            prop: "allRole",
            type: "switch",
            slot: true,
            dicUrl: window.urllist.dictUrl + 'YN01',
            dataType: 'number',
            props: {
              label: "name",
              value: "code"
            }
          }
        ]
      },
      optionUser: {
        index: true,
        indexLabel: '序号',
        selection: true,
        tip: true,
        searchShow: true,
        menu: false,
        border: true,
        addBtnText: "添加",
        columnBtn: false,
        addBtn: false,
        refreshBtn: false,
        searchIcon: true,
        align: 'center',
        maxHeight: 800,
        rowKey: 'id',
        reserveSelection: true,
        column: [
          {
            label: "用户名",
            prop: "loginId",
            search: true
          },
          {
            label: '用户昵称',
            prop: 'nickName',
            search: true
          },
          {
            label: "部门",
            prop: "orgName",
            overHidden: true,
            searchslot:true,
            search: true
          },
          {
            label: "用户类型",
            prop: "userTypeStr",
            searchslot:true,
            search: true
          },
          {
            label: "用户姓名",
            prop: "userName",
            overHidden: true,
            search: true
          },
          {
            label: "电话",
            prop: "telephone",
            overHidden: true
          }
        ]
      },
      dataUser: [],
      pageUser: {
        pageSize: 10,
        currentPage: 1,
        total: 0
      },
      searchUser: {},
      userLoading: true,
      selectTenantCode: '',
      roleId: '',
      optionRoleUser: {
        index: true,
        indexLabel: '序号',
        selection: true,
        tip: true,
        searchShow: true,
        menu: false,
        border: true,
        addBtnText: "添加",
        columnBtn: false,
        addBtn: false,
        refreshBtn: false,
        searchIcon: true,
        align: 'center',
        maxHeight: 450,
        rowKey: 'id',
        reserveSelection: true,
        column: [
          {
            label: "用户名",
            prop: "loginId",
            search: true
          },
          {
            label: '用户昵称',
            prop: 'nickName',
            search: true
          },
          {
            label: "部门",
            prop: "orgName",
            overHidden: true,
            searchslot:true,
            search: true
          },
          {
            label: "用户类型",
            prop: "userTypeStr",
            searchslot:true,
            search: true
          },
          {
            label: "用户姓名",
            prop: "userName",
            overHidden: true,
            search: true
          },
          {
            label: "电话",
            prop: "telephone",
            overHidden: true
          }
        ]
      },
      dataRoleUser: [],
      pageRoleUser: {
        pageSize: 10,
        currentPage: 1,
        total: 0
      },
      searchRoleUser: {},
      roleUserLoading: true,
      addUserLoading: false,
      deleteUserLoading: false,
      isChange: false,
      isDataChange: true,
      allTopMenu: [],
      topMenuId: [],
      menuBox: false,
      menuTitle: '配置顶级菜单',
      menuRoleId: ''
    }
  },
  created () {
    request.get(window.urllist.dictUrl + 'USER_TYPE').then((res) => {
      this.userTypeList = res.data.data;
    });


    this.api['listAllMenu']().then((res) => {
      this.allTopMenu = res.data.data;
    });

  },
  watch: {
    "form.tenantCode"(tenantCode){
      if (!this.validatenull(tenantCode)){
        this.api['list']({"tenantCode": tenantCode}).then((res) => {
          this.findObject(this.$refs.crud.option.column, 'parentId').dicData = treeAddDisabled(res.data.data || [], this.form.id);
        });
      }
    },
    'clientId'(){
      if (this.box === true){
        menuTree({clientId: this.clientId}).then(res => {
          this.menuGrantList = res.data.data;
        });
        this.listFunction();
      }

      if (this.databox === true){
        //查询菜单
        menuTree({clientId: this.clientId}).then(res => {
          this.menuData = res.data.data;
        });

        this.scopedList = [];
      }
    }
  },
  methods: {
    currentRowChange(selection){
      if (this.isDataChange){
        this.scopedList.forEach(e => {
          const index = this.dataIds.indexOf(e.id);
          if (index >= 0){
            this.dataIds.splice(index,1);
          }
        });

        selection.forEach(e => {
          if (!this.dataIds.includes(e.id)){
            this.dataIds.push(e.id)
          }
        });
      }
    },
    listDataScope (data) {
      this.boxDataLoading = true;
      listByMenuId({
        menuId: data.id
      }).then(res => {
        this.boxDataLoading = false;
        this.scopedList = res.data.data;
        this.isDataChange = false;

        this.$nextTick(() => {
          const selectDataIds = [];
          this.dataIds.forEach(ele => {
            this.scopedList.forEach(e => {
              if (ele === e.id) {
                selectDataIds.push(e)
              }
            })
          });

          this.$refs.dataScopeTable.toggleSelection(selectDataIds);
          this.isDataChange = true;
        });

      });

    },
    submitDataScope () {
      this.dataBoxDisabled = true;
      roleDataScope({
        roleId: this.roleId,
        dataIds: this.dataIds.join(',')
      }).then(() => {
        this.dataBoxDisabled = false;
        this.databox = false;
        this.$message({
          type: "success",
          message: "操作成功"
        });
      }).catch(() => {
        this.dataBoxDisabled = false;
      });
    },
    checkChange(data,select){
      this.delAllFunctions(this.menuGrantList);
      this.functionIds.push(...select.checkedKeys,...select.halfCheckedKeys);
    },
    delAllFunctions(list){
      list.forEach(e=>{

        if(e.hasChildren){
          this.delAllFunctions(e.children);
        }

        const index = this.functionIds.indexOf(e.id);
        if (index >= 0) {
          this.functionIds.splice(index,1);
        }

      });
    },
    functionSelectionChange(selection){
      if (this.isChange){
        this.functionList.forEach(e => {
          const index = this.functionIds.indexOf(e.id);
          if (index >= 0){
            this.functionIds.splice(index,1);
          }
        });

        selection.forEach(e => {
          if (!this.functionIds.includes(e.id)){
            this.functionIds.push(e.id)
          }
        });
      }
    },
    listFunction(data = {}){
      this.boxLoading = true
      listButByMenu({ id: data.id, clientId: this.clientId }).then((res)=>{
        this.boxLoading = false
        this.isChange=false;// 标记这时不要去更新选中的数据
        this.functionList = res.data.data;

        this.$nextTick(() => {

          const selectFunction = [];
          this.functionIds.forEach(ele => {
            this.functionList.forEach(e => {
              if (ele === e.id) {
                selectFunction.push(e);
              }
            })
          });

          this.$refs.functionTable.toggleSelection(selectFunction);
          this.isChange=true;
        });
      });
    },
    submit () {
      this.boxDisabled = true
      addFunction({
        roleId: this.roleId,
        functionIds: this.functionIds
      }).then(() => {
        this.boxDisabled = false;
        this.box = false;
        this.$message({
          type: "success",
          message: "操作成功"
        });
      }).catch(() => {
        this.boxDisabled = false;
      });
    },
    handleData (row) {
      this.api['clientList']().then(clientRes => {

        if (validatenull(clientRes.data.data)) {
          this.$message.error('请先新建客户端');
          return;
        }

        this.clientList = clientRes.data.data;
        this.clientId = clientRes.data.data[0].id;

        //加载数据
        this.roleId = row.id;
        this.scopedList = [];

        //查询菜单
        menuTree({clientId: this.clientId}).then(res => {
          this.menuData = res.data.data;
          this.databox = true;
        });

        //查询该角色所拥有的数据权限
        listIdByRoleId({ roleIds: row.id }).then(result => {
          this.dataIds = result.data.data;
        });
      });
    },
    handleRole (row) {
      this.api['clientList']().then(clientRes => {

        if (validatenull(clientRes.data.data)){
          this.$message.error('请先新建客户端');
          return;
        }

        this.clientList = clientRes.data.data;
        this.clientId = clientRes.data.data[0].id;

        //加载数据
        this.roleId = row.id;
        this.functionIds=[];
        this.defaultFunctionIds = [];
        this.functionList = []
        this.scopedList = []
        //查询菜单
        menuTree({clientId: this.clientId}).then(res => {
          this.menuGrantList = res.data.data;
          this.box = true;

          queryUrlIdByRole({
            roleIds: row.id
          }).then(res => {

            res.data.data.forEach(id=>{
              if (this.$refs.treeMenu.getNode(id) && this.$refs.treeMenu.getNode(id).isLeaf){
                this.defaultFunctionIds = [
                  id,
                  ...this.defaultFunctionIds
                ];
              }
            });

            this.functionIds = res.data.data;
            this.listFunction();

          });


        });
      });
    },
    searchChangeUser (params, done) {
      this.pageUser.currentPage = 1;
      this.onLoadUser(this.pageUser, params);
      done();
    },
    searchResetUser () {
      this.pageUser.currentPage = 1;
      this.onLoadUser(this.pageUser);
    },
    currentChangeUser(currentPage) {
      this.pageUser.currentPage = currentPage;
      this.onLoadUser(this.pageUser,this.searchUser);
    },
    sizeChangeUser (pageSize) {
      this.pageUser.pageSize = pageSize;
      this.pageUser.currentPage = 1;
      this.onLoadUser(this.pageUser,this.searchUser);
    },
    handleUser (row) {
      this.selectTenantCode = row.tenantCode;
      this.roleId = row.id;
      this.roleUserName = row.name
      this.pageUser ={
        pageSize: 10,
        currentPage: 1,
        total: 0
      };
      this.searchUser = {};
      this.onLoadUser(this.pageUser);

      if (this.$refs.tableUser){
        this.$nextTick(()=>{
          this.$refs.tableUser.searchReset();
          this.$refs.tableUser.selectClear();
        })
      }
      this.userBox = true;

      let url = window.home_url + '/jpower-system/core/org/tree';
      if (this.$store.getters.isShowTenantCode){
        url = url + '?tenantCode='+row.tenantCode;
      }

      request.get(url).then((res) => {
        this.orgList = res.data.data;
      });
    },
    onLoadUser (page, params = {}) {
      this.userLoading = true;
      const values = {
        ...params,
        isEq: 'Y',
        pageNum: page.currentPage,
        pageSize: page.pageSize
      }
      this.listRole(values).then(res => {
        const data = res.data.data;
        this.pageUser.total = data.total;
        this.dataUser = data.list;
        this.userLoading = false;
      });
    },
    listRole(params = {}){
      const values = {
        ...params,
        roleId: this.roleId
      }
      if (this.$store.getters.isShowTenantCode){
        values.tenantCode_eq = this.selectTenantCode;
      }
      return listByRole(values);
    },
    roleUser(){
      this.pageRoleUser ={
        pageSize: 10,
        currentPage: 1,
        total: 0
      };
      this.searchRoleUser = {};
      this.onLoadRoleUser(this.pageRoleUser);

      if (this.$refs.tableRoleUser){
        this.$nextTick(()=>{
          this.$refs.tableRoleUser.searchReset();
          this.$refs.tableRoleUser.selectClear();
        })
      }

      this.roleUserBox = true;
    },
    onLoadRoleUser (page, params = {}) {
      this.roleUserLoading = true;

      const values = {
        ...params,
        isEq: 'N',
        pageNum: page.currentPage,
        pageSize: page.pageSize
      }
      this.listRole(values).then(res => {
        const data = res.data.data;
        this.pageRoleUser.total = data.total;
        this.dataRoleUser = data.list;
        this.roleUserLoading = false;
      });
    },
    searchChangeRoleUser (params, done) {
      this.pageRoleUser.currentPage = 1;
      this.onLoadRoleUser(this.pageRoleUser, params);
      done();
    },
    searchResetRoleUser () {
      this.pageRoleUser.currentPage = 1;
      this.onLoadRoleUser(this.pageRoleUser);
    },
    currentChangeRoleUser(currentPage) {
      this.pageRoleUser.currentPage = currentPage;
      this.onLoadRoleUser(this.pageRoleUser, this.searchRoleUser);
    },
    sizeChangeRoleUser (pageSize) {
      this.pageRoleUser.pageSize = pageSize;
      this.pageRoleUser.currentPage = 1;
      this.onLoadRoleUser(this.pageRoleUser, this.searchRoleUser);
    },
    addUser(){

      let ids = [];
      this.$refs.tableRoleUser.tableSelect.forEach(ele => {
        ids.push(ele.id);
      });

      if (ids.length <= 0){
        this.$message.warning('至少选择一条数据');
        return;
      }

      this.addUserLoading = true;

      addRoleUser({ roleId: this.roleId, userIds: ids.join(",") }).then((res)=>{
        if (res.data.status){
          this.onLoadUser(this.pageUser,this.searchUser);
          this.roleUserBox = false;
          this.$message.success('添加成功');
        }else {
          this.$message.error(res.data.message);
        }
        this.addUserLoading = false;
      }).catch(()=>{
        this.addUserLoading = false;
      });
    },
    deleteUser(){
      let ids = [];
      this.$refs.tableUser.tableSelect.forEach(ele => {
        ids.push(ele.id);
      });

      if (ids.length <= 0){
        this.$message.warning('至少选择一条数据');
        return;
      }

      this.$confirm(`确定去除所选用户吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {

        this.deleteUserLoading = true;

        deleteRoleUser({ roleId: this.roleId, userIds: ids.join(",") }).then((res)=>{
          if (res.data.status){
            this.pageUser.currentPage = 1;
            this.onLoadUser(this.pageUser,this.searchUser);
            this.$refs.tableUser.selectClear();
            this.$message.success('去除成功');
          }else {
            this.$message.error(res.data.message);
          }
          this.deleteUserLoading = false;
        }).catch(()=>{
          this.deleteUserLoading = false;
        });

      })

    },
    beforeOpen(done,type){
      if (["add"].includes(type)) {
        this.findObject(this.$refs.crud.option.column, 'tenantCode').value=this.$store.getters.tenantCode;
      }
      if (["edit"].includes(type)) {
        this.oldEditForm = this.form;
      }
      done();
    },
    addAfter (row, done, data) {
      done(Object.assign(row, {
        id: data.data,
        children: []
      }));
      this.$message.success('新增成功');
      this.$refs.crud.updateDic('parentId');
    },
    updateAfter (row, done) {
      if (row.parentId === undefined){
        row.parentId = '-1';
      }
      const data = delItemById(this.$refs.crud.data,row.id);

      done(row);
      this.$message.success('修改成功');
      this.$refs.crud.updateDic('parentId');

      if (data){
        //avue更新后会丢失箭头，所以我这里不管改没改上级ID，都去修改下；
        const index = this.oldEditForm.parentId !== row.parentId ? 0 : data.index;
        addItemById(this.$refs.crud.data,Object.assign(data.data, row),index);
      }
    },
    delAfter (row, done) {
      done(row);
      this.$message.success('删除成功');
      this.$refs.crud.updateDic('parentId');
    },
    delsAfter (ids) {
      func.split(ids).forEach(id=> {
        delItemById(this.$refs.crud.data,id);
      })
      this.$message.success('删除成功');
      this.$refs.crud.updateDic('parentId');
    },
    handleMenu(row){
      this.menuRoleId = row.id;
      this.api['topMenuId'](row.id).then(res => {
          if (this.$refs.topMenu){
            this.$refs.topMenu.setCheckedKeys(res.data.data)
          }else {
            this.topMenuId = res.data.data;
          }
          this.menuBox = true;
          this.menuTitle = '['+row.name+']配置顶级菜单';
      });
    },
    submitTopMenu(){

      let menuIds = this.$refs.topMenu.getCheckedKeys();

      this.allTopMenu.forEach(m=>{
        if (menuIds.includes(m.id)){
          menuIds.splice(menuIds.indexOf(m.id),1)
        }
      })
      menuIds = menuIds.join(",");

      this.api['saveTopMenu']({
        menuIds: menuIds,
        roleId: this.menuRoleId
      }).then(() => {
        this.menuBox = false;
        this.$message({
          type: "success",
          message: "操作成功!"
        });
      });
    }
  }
}, {
  name: '/system/roleTree',
  res: (data) => {
    return {
      data: data.data
    }
  },
  crudPage: false
})
</script>

<style lang="scss">
.chilentTable .avue-crud > .avue-crud__menu {
  display: none!important;
}
</style>
