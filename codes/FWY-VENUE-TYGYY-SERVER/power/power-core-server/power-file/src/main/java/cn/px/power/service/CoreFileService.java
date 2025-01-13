package cn.px.power.service;


import cn.px.power.dbs.entity.TbCoreFile;
import cn.px.power.module.common.service.BaseService;

/**
 * @author 品讯科技
 */
public interface CoreFileService extends BaseService<TbCoreFile> {

    /**
     * @author 品讯科技
     * @Description //TODO 新增一个文件
     * @date 2024-08
     * @Param [coreFile]
     * @return java.lang.Boolean
     **/
    Boolean add(TbCoreFile coreFile);

    TbCoreFile getById(String id);

    /**
     * @author 品讯科技
     * @Description //TODO 获取文件路径
     * @date 2024-08
     * @Param [id]
     * @return cn.px.power.module.dbs.entity.core.file.TbCoreFile
     **/
    String getPathById(String id);
}
