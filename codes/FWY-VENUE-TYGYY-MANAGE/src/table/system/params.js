export default ()=> {
  //safe => vue的this对象
  return {
    menuWidth: 300,
    labelWidth: 120,
    selection: true,
    indexLabel: '序号',
    tip: false,
    index: true,
    align: 'center',
    headerAlign: 'center',
    border: true,
    stripe: true,
    viewBtn: true,
    permission: {
      addCode: 'SYSTEM_PARAMS_ADD',
      editCode: 'SYSTEM_PARAMS_UPDATE',
      delCode: 'SYSTEM_PARAMS_DELETE',
      viewCode: 'SYSTEM_PARAMS_DETAIL',
      effectAllCode: 'SYSTEM_PARAMS_EFFECTALL',
      takeEffectCode: 'SYSTEM_PARAMS_TAKEEFFECT'
    },
    column: [{
        label: "参数名称",
        prop: "name",
        search: true,
        rules: [{
          required: true,
          message: "请输入参数名称",
          trigger: "blur"
        }]
      },
      {
        label: "参数编码",
        prop: "code",
        search: true,
        rules: [{
          required: true,
          message: "请输入参数编码",
          trigger: "blur"
        }]
      },
      {
        label: "参数值",
        prop: "value",
        search: true,
        rules: [{
          required: true,
          message: "请输入参数值",
          trigger: "blur"
        }]
      },
      {
        label: "支持立即生效",
        prop: "isEffect",
        value: 1,
        type: 'select',
        dataType: 'number',
        dicUrl: window.urllist.dictUrl + 'YN01',
        props: {
          label: "name",
          value: "code"
        },
        rules: [{
          required: true,
          message: "请选择 是否支持立即生效",
          trigger: "blur"
        }]
      },
      {
        label: "备注",
        prop: "note",
        showColumn: false,
        hide: true,
        type: "textarea",
        span: 24,
        minRows: 6
      }],
  }
}