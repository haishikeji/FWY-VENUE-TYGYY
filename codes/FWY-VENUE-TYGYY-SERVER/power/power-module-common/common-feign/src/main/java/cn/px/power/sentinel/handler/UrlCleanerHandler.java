package cn.px.power.sentinel.handler;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.UrlCleaner;
import cn.px.power.module.common.utils.Fc;
import cn.px.power.module.common.utils.constants.StringPool;

/**
 * @ClassName UrlCleanerHandler
 * @Description TODO 不做流控的URL去除
 * @author 品讯科技
 * @date 2024-08
 * @Version 1.0
 */
public class UrlCleanerHandler implements UrlCleaner {

    @Override
    public String clean(String url) {
        if (Fc.equals(url,"/**")){
            return StringPool.EMPTY;
        }
        if (Fc.equals(url,"/error")){
            return StringPool.EMPTY;
        }
        return url;
    }

}
