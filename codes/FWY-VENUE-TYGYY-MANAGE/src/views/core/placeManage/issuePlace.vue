<template>
  <basic-container>
    <avue-form
      ref="form"
      v-model="obj"
      :option="option"
      @reset-change="emptytChange"
      :upload-preview="uploadPreview"
      @submit="submit"
    >
      <template slot-scope="{ disabled, size }" slot="text">
        <!-- <el-input :disabled="disabled" :size="size" v-model="obj.text" placeholder="这里是自定的表单"></el-input> -->
        <avue-ueditor :disabled="disabled" :size="size" v-model="obj.text">
        </avue-ueditor>
      </template>
    </avue-form>
  </basic-container>
</template>

<script>
import {
  placeAdd,
  classifySelectInquire
} from "@/api/placeManage/placeList";
export default {
  data() {
    return {
      DIC: {
        classification: [],
      },
      obj: {
        imgUrl: [],
        text: "",
      },
      sizeValue: "medium",
    };
  },
  computed: {
    option() {
      return {
        size: this.sizeValue,
        submitText: "确认发布",
        labelWidth: 130,
        column: [
          {
            label: "场地名称",
            prop: "siteName",
            span: 8,
            maxlength: 20,
            minlength: 2,
            rules: [
              {
                required: true,
                message: "请输入场地名称",
                trigger: "blur",
              },
            ],
          },
          {
            label: "场地开放时间",
            span: 8,
            type: "datetime",
            prop: "openTime",
            format: "yyyy-MM-dd HH:mm:ss",
            valueFormat: "yyyy-MM-dd HH:mm:ss",
            mock: {
              type: "datetime",
              format: "yyyy-MM-dd hh:mm:ss",
              now: true,
            },
            rules: [
              {
                required: true,
                message: "请选择场地开始时间",
                trigger: "blur",
              },
            ],
          },
          {
            label: "场地关门时间",
            prop: "endTime",
            type: "datetime",
            span: 8,
            format: "yyyy-MM-dd HH:mm:ss",
            valueFormat: "yyyy-MM-dd HH:mm:ss",
            mock: {
              type: "datetime",
              format: "yyyy-MM-dd HH:mm:ss",
              now: true,
            },
            rules: [
              {
                required: true,
                message: "请选择活动结束时间",
                trigger: "blur",
              },
            ],
          },
          {
            label: "场地负责人",
            prop: "header",
            span: 8,
            maxlength: 10,
            minlength: 2,
            rules: [
              {
                required: true,
                message: "请输入负责人姓名",
                trigger: "blur",
              },
            ],
          },
          {
            label: "负责人联系方式",
            prop: "telephone",
            span: 8,
            maxlength: 20,
            minlength: 2,
            rules: [
              {
                required: true,
                message: "请输入负责人联系方式",
                trigger: "blur",
              },
            ],
          },
          {
            label: "市场价格",
            prop: "marketPrice",
            type: "number",
            span: 8,
            maxlength: 20,
            minlength: 2,
            min: 0,
            rules: [
              {
                required: true,
                message: "请输入市场价格",
                trigger: "blur",
              },
            ],
          },
          {
            label: "会员价格",
            prop: "memberPrice",
            type: "number",
            span: 8,
            maxlength: 20,
            minlength: 2,
            min: 0,
            rules: [
              {
                required: true,
                message: "请输入会员价格",
                trigger: "blur",
              },
            ],
          },
          {
            label: "关联的分类",
            prop: "classification",
            display: true,
            type: "select",
            span: 8,
            // allowCreate:true,
            // filterable:true,
            dicData: this.DIC.classification,
            props: {
              label: "name",
              value: "code",
            },
          },
          {
            label: "轮播图上传",
            prop: "imgUrl",
            type: "upload",
            listType: "picture-card",
            loadText: "图片上传中，请稍等",
            limit: 5,
            span: 24,
            dataType: "array",
            propsHttp: {
              home: window.urllist.download,
              url: "data",
            },
            accept: "image/png, image/jpeg",
            tip: "只能上传jpg/png文件，且不超过500kb",
            action: window.urllist.update,
          },
          {
            label: "场地位置",
            prop: "siteAddr",
            type: "map",
            display: true,
            //高德初始化参数
            params: {
              zoom: 10,
              // zoomEnable: false,
              // dragEnable: false,
            },
          },
          {
            label: "详情设置",
            prop: "text",
            customConfig: {}, //wangEditor编辑的配置
            span: 24,
            formslot: true,
            rules: [
              {
                required: true,
                message: "请输入场地详情",
                trigger: "blur",
              },
            ],
          },
        ],
      };
    },
  },

  created() {
    this.classifyInquire()
  },
  mounted() {},
  methods: {
     // 关联分类下拉列表查询
    classifyInquire() {
      classifySelectInquire().then(res => {
        const {code,data,message} = res.data
        if (code === 200) {
          this.DIC.classification = data
          console.log(data);
        } else {
            console.log(message);
        }
      })
    },

    // 发布活动
    submit(form, done) {
      // console.log('当前数据='+JSON.stringify(this.obj));
      console.log(this.obj);

      const data = {
        siteName: this.obj.siteName,
        classification: this.obj.classification,
        openTime: this.obj.openTime,
        endTime: this.obj.endTime,
        lon: this.obj.siteAddr[0],
        lat: this.obj.siteAddr[1],
        siteAddr: this.obj.siteAddr[2],
        header: this.obj.header,
        telephone: this.obj.telephone,
        marketPrice: this.obj.marketPrice,
        memberPrice: this.obj.memberPrice,
        rotationChart: this.obj.imgUrl.join(","),
        siteDetail: this.obj.text,
      };

      placeAdd(data).then((res) => {
        const { code, message } = res.data;
        if (code === 200) {
          this.$message.success("场地新增成功");
        } else {
          console.log(message);
        }
        done(form);
      });
    },

    emptytChange() {
      this.$message.success("清空方法回调");
    },

    tip() {
      this.$message.success("自定义按钮");
    },
    uploadPreview(file, column, done) {
      this.$confirm("是否放大查看图片").then(() => {
        done(); //默认执行打开方法
      });
    },
  },
};
</script>

<style lang="scss" scoped>
</style>