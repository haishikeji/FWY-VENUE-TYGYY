package cn.px.power.cache;

import cn.px.power.dbs.entity.TbCoreFile;
import cn.px.power.feign.FileClient;
import cn.px.power.module.base.vo.ResponseData;
import cn.px.power.module.common.cache.CacheNames;
import cn.px.power.module.common.utils.CacheUtil;
import cn.px.power.module.common.utils.SpringUtil;


/**
 * @ClassName UserCache
 * @Description TODO 用户缓存
 * @author 品讯科技
 * @Version 1.0
 */
public class FileCache {

    private static FileClient fileClient;

    static {
        fileClient = SpringUtil.getBean(FileClient.class);
    }

    public static TbCoreFile getFileDetail(String base) {
        return CacheUtil.get(CacheNames.FILE_REDIS_CACHE,CacheNames.FILE_BASE_KEY, base,() -> {
            ResponseData<TbCoreFile> responseData = fileClient.getFileDetail(base);
            return responseData.getData();
        });
    }
}
