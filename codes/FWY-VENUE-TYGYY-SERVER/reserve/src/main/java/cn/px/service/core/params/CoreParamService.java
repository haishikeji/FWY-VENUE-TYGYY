package cn.px.service.core.params;

import cn.px.dbs.entity.core.params.TbCoreParam;
import cn.px.power.module.common.service.BaseService;

/**
 * @author 品讯科技
 */
public interface CoreParamService extends BaseService<TbCoreParam> {

    /**
     * @author 品讯科技
     * @Description //TODO 通过code查询参数值
     * @date 2024-08
     * @Param [key]
     * @return java.lang.String
     **/
    String selectByCode(String code);

    /**
     * @author 品讯科技
     * @Description //TODO 删除系统参数
     * @date 2024-08
     * @Param [id]
     * @return java.lang.Integer
     **/
    Boolean deletes(String id);

    /**
     * @author 品讯科技
     * @Description //TODO 更新系统参数
     * @date 2024-08
     * @Param [coreParam]
     * @return java.lang.Integer
     **/
    Boolean update(TbCoreParam coreParam);

    /**
     * @author 品讯科技
     * @Description //TODO 参数生效
     * @date 2024-08
     * @Param
     * @return
     **/
    void effectAll();

}
