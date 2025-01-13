import {validatenull,findMenuId} from "@/util/validate"; // 判断按钮显隐工具类

const mixin = {
    data() {
      return {
        msg: '按钮权限'
      }
    },

    methods: {
      // 获取菜单ID
      getMenuId () {
        if (this.$route.meta.id){
          return this.$route.meta.id;
        }
        return findMenuId(this.$route.meta.code,this.$store.getters.menu);
      },
        
      // 获取按钮权限
        getPermission(key) {
            if (key === 'editBtn') {
                return validatenull(this.permission.editCodeShow)?false:this.permission.editCodeShow;
            } if (key === 'addBtn') {
                return validatenull(this.permission.addCodeShow)?false:this.permission.addCodeShow;
            } else if (key === 'delBtn') {
                return validatenull(this.permission.delCodeShow)?false:this.permission.delCodeShow;
            } else if (key === 'viewBtn') {
                return validatenull(this.permission.viewCodeShow)?true:this.permission.viewCodeShow;
            } else if (key === 'excelBtn') {
                return validatenull(this.permission.excelCodeShow)?false:this.permission.excelCodeShow;
            } else {
                return true;
            }
        },
    }
  }
  
  export default mixin
