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
    </avue-form>
  </basic-container>
</template>

<script>
import {
  activeSelectInquire,
  activePhotoesAdd,
} from "@/api/activeRegulator/activeRegulator";
export default {
  data() {
    return {
      DIC: {
        acitveId: [],
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
        submitText: "确认上传",
        labelWidth: 130,
        column: [
          {
            label: "选择活动",
            prop: "activityId",
            display: true,
            type: "select",
            span: 8,
            allowCreate: true,
            filterable: true,
            dicData: this.DIC.acitveId,
            props: {
              label: "activityName",
              value: "id",
            },
            rules: [
              {
                required: true,
                message: "请选择要上传相册的活动",
                trigger: "blur",
              },
            ],
          },
          {
            label: "相册名称",
            prop: "name",
            type: "input",
            rules: [
              {
                required: true,
                message: "请输入相册名称",
                trigger: "blur",
              },
            ],
          },
          {
            label: "相册链接",
            prop: "picStr",
            type: "input",
            rules: [
              {
                required: true,
                message: "请输入相册链接",
                trigger: "blur",
              },
            ],
          },
          // {
          //     label: '活动图片上传',
          //     prop: 'imgUrl',
          //     type: 'upload',
          //     listType: 'picture-card',
          //     loadText: '图片上传中，请稍等',
          //     // limit: 5, // 最多上传5张
          //     span: 24,
          //     dataType:'array',
          //     propsHttp: {
          //       home: window.urllist.download,
          //       url: 'data'
          //     },
          //     accept:'image/png, image/jpeg',
          //     tip: '只能上传jpg/png文件，且不超过500kb',
          //     action: window.urllist.update,
          // },
        ],
      };
    },
  },

  created() {
    this.selectInquire();
  },

  methods: {
    // 选择活动下拉列表查询
    selectInquire() {
      activeSelectInquire().then((res) => {
        const { code, data, message } = res.data;
        if (code === 200) {
          this.DIC.acitveId = data;
        } else {
          console.log(message);
        }
      });
    },

    // 清空表单
    emptytChange() {
      this.$message.success("清空方法回调");
    },

    // 上传文件类型提示
    tip() {
      this.$message.success("自定义按钮");
    },

    //  查看图片放大
    uploadPreview(file, column, done) {
      this.$confirm("是否放大查看图片").then(() => {
        done(); //默认执行打开方法
      });
    },

    // 发布相册
    submit(form, done) {
      console.log(form);
      activePhotoesAdd({
        activityId: form.activityId,
        // pictures: form.imgUrl,
        name: form.name,
        picStr:form.picStr
      }).then((res) => {
        const { code, message } = res.data;
        if (code === 200) {
          this.$message.success("活动发布成功！");
        } else {
          console.log(message);
        }
        done(form);
      });
    },
  },
};
</script>

<style>
</style>