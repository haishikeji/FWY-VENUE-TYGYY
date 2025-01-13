package cn.px.power.feign;

import cn.px.power.module.base.vo.ResponseData;
import cn.px.power.module.common.utils.constants.AppConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */
@FeignClient(value = AppConstant.JPOWER_SYSTEM, fallback = DictClientFallback.class, path = "/core/dict")
public interface DictClient {

    /**
     * @author 品讯科技
     * @Description //TODO 查询dictType
     * @date 2024-08
     */
    @GetMapping("/queryDictByType")
    ResponseData<List<Map<String, Object>>> queryDictByType(@RequestParam String dictTypeCode);
}
