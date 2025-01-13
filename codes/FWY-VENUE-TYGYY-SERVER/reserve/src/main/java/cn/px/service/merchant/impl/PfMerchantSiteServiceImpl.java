package cn.px.service.merchant.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.Lists;
import cn.px.cache.param.ParamConfig;
import cn.px.dbs.dao.merchant.mapper.PfMerchantSiteMapper;
import cn.px.dbs.entity.merchant.PfMerchantSite;
import cn.px.power.module.common.service.impl.BaseServiceImpl;
import cn.px.power.module.common.utils.Fc;
import cn.px.power.module.mp.support.Condition;
import cn.px.service.merchant.PfMerchantSiteService;
import cn.px.utils.TencentMapsUtils;
import cn.px.utils.constants.BusConstants;
import cn.px.utils.constants.BusConstantsEnum;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 场地表 服务实现类
 * </p>
 *
 * @author 品讯科技
 * @since 2022-07-04
 */
@Service
public class PfMerchantSiteServiceImpl extends BaseServiceImpl<PfMerchantSiteMapper, PfMerchantSite> implements PfMerchantSiteService {

    @Override
    public List<PfMerchantSite> listNearSite(String lon, String lat) {
        List<PfMerchantSite> resultList = Lists.newArrayList();
        List<PfMerchantSite> list = this.list(Condition.<PfMerchantSite>getQueryWrapper().lambda().eq(PfMerchantSite::getReleaseStatus, BusConstantsEnum.RELEASE_STATUS.APPROVED.getValue()));
        for (PfMerchantSite site : list) {
            if (Fc.isNoneBlank(site.getLon(),site.getLat())) {
                Double algorithm = TencentMapsUtils.algorithm(Double.valueOf(lon), Double.valueOf(lat), Double.valueOf(site.getLon()), Double.valueOf(site.getLat()));
                if (algorithm <= ParamConfig.getInt(BusConstants.DISTANCE)) {
                    site.setDistance(NumberUtil.round(algorithm, 2));
                    resultList.add(site);
                }
            }
        }
        return resultList.stream().sorted(Comparator.comparing(PfMerchantSite::getDistance)).collect(Collectors.toList());
    }

    @Override
    public void gxCode(Integer code, Integer newCode) {
        Runnable runnable = () -> {
            List<PfMerchantSite> list = this.list(Condition.<PfMerchantSite>getQueryWrapper().lambda().eq(PfMerchantSite::getClassification, code));
            if (Fc.notNull(list)) {
                for (PfMerchantSite site : list) {
                    site.setClassification(newCode);
                }
            }
            this.updateBatchById(list);
        };
        ThreadUtil.execAsync(runnable);
    }
}
