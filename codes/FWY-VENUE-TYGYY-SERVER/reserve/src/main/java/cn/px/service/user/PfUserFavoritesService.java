package cn.px.service.user;

import cn.px.dbs.entity.user.PfUserFavorites;
import cn.px.dbs.vo.user.PfUserFavoritesVo;
import cn.px.power.module.common.service.BaseService;

import java.util.List;

/**
 * <p>
 * 用户收藏表 服务类
 * </p>
 *
 * @author 品讯科技
 * @since 2022-08-01
 */
public interface PfUserFavoritesService extends BaseService<PfUserFavorites> {
    /**
     * TODO 我的收藏
     * @param userId
     * @return java.util.List<cn.px.power.dbs.vo.user.PfUserFavoritesVo>
     * @author 品讯科技
     * @date 2024-08
     */
    List<PfUserFavoritesVo> getLists(String userId);
    /**
     * TODO 判断是否收藏
     * @param userId
	 * @param activityId
     * @return java.lang.Boolean
     * @author 品讯科技
     * @date 2024-08
     */
    Boolean isFavorite(String userId,String activityId);
}
