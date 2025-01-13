package cn.px.power.handler;

import com.alibaba.fastjson.JSONObject;
import cn.px.power.dbs.entity.TbLogMonitorParam;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 品讯科技
 * @date 2024-08
 */
public class HttpInfoBuilder {
    /**
     * HttpInfoHandler缓存池
     */
    private static final Map<String, HttpInfoHandler> HANDLER_POOL = new ConcurrentHashMap<>();

    /**
     * 获取Handler
     *
     * @param  url 请求接口
     * @return HttpInfoHandler
     */
    public static HttpInfoHandler getHandler(String url) {
        HttpInfoHandler handler = HANDLER_POOL.get(url);
        if (handler == null) {
            throw new IllegalArgumentException("不存在的地址");
        }else {
            return handler;
        }
    }

    /**
     * 生成Handler
     *
     * @param  url 请求接口
     * @param paramList
     * @return HttpInfoHandler
     */
    public static HttpInfoHandler newHandler(String url, List<TbLogMonitorParam> paramList, JSONObject methodsInfo, JSONObject definitions) {
        HttpInfoHandler handler = HANDLER_POOL.get(url);
        if (handler == null) {
            handler = new HttpInfoHandler(paramList,methodsInfo,definitions);
            HANDLER_POOL.put(url,handler);
        }

        return handler;
    }

}
