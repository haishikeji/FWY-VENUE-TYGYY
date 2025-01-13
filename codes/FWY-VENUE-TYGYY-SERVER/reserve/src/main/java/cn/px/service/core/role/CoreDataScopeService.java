package cn.px.service.core.role;

import cn.px.dbs.entity.core.function.TbCoreDataScope;
import cn.px.power.module.common.service.BaseService;

import java.util.List;

/**
 * @author 品讯科技
 * @description 数据权限业务
 * @date 2024-08
 */
public interface CoreDataScopeService extends BaseService<TbCoreDataScope> {

    /**
     * 保存数据权限
     * @author 品讯科技
     * @date 2024-08
     * @param dataScope 数据权限bean
     * @return boolean
     **/
    @Override
    boolean save(TbCoreDataScope dataScope);

    /**
     * 给角色设置数据权限
     * @author 品讯科技
     * @date 2024-08
     * @param roleId
     * @param dataIds
     * @return boolean
     **/
    boolean roleDataScope(String roleId, String dataIds);

    /**
     * 查询所有角色都可执行得数据权限
     *
     * @author 品讯科技
     * @date 2024-08
     */
    List<TbCoreDataScope> getAllRoleDataScope();

    /**
     * 根据角色ID查询数据权限
     *
     * @author 品讯科技
     * @date 2024-08
     * @param roleIds 角色ID
     * @return java.util.List<cn.px.power.dbs.entity.function.TbCoreDataScope>
     */
    List<TbCoreDataScope> getDataScopeByRole(List<String> roleIds);
}
