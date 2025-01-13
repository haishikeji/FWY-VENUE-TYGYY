package cn.px.power.service.city.impl;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import cn.px.power.dbs.dao.city.TbCoreCityDao;
import cn.px.power.dbs.dao.city.mapper.TbCoreCityMapper;
import cn.px.power.dbs.entity.city.TbCoreCity;
import cn.px.power.module.base.enums.JpowerError;
import cn.px.power.module.base.exception.JpowerAssert;
import cn.px.power.module.common.cache.CacheNames;
import cn.px.power.module.common.service.impl.BaseServiceImpl;
import cn.px.power.module.common.utils.CacheUtil;
import cn.px.power.module.common.utils.Cm;
import cn.px.power.module.common.utils.Fc;
import cn.px.power.module.common.utils.StringUtil;
import cn.px.power.module.common.utils.constants.JpowerConstants;
import cn.px.power.module.mp.support.Condition;
import cn.px.power.service.city.CoreCityService;
import cn.px.power.vo.CityVo;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */
@Service("coreCityService")
@AllArgsConstructor
public class CoreCityServiceImpl extends BaseServiceImpl<TbCoreCityMapper, TbCoreCity> implements CoreCityService {

    private TbCoreCityDao coreCityDao;

    @Override
    public List<Map<String, Object>> listChild(Map<String, Object> city) {
        return coreCityDao.listMaps(Condition.getQueryWrapper(city,TbCoreCity.class).lambda()
                .select(TbCoreCity::getCode,TbCoreCity::getName)
                .orderByAsc(TbCoreCity::getSortNum));
    }

    @Cacheable(value = CacheNames.CITY_PARENT_LIST_REDIS_KEY,key = "#coreCity.toString()" +
            ".concat(T(cn.px.power.module.common.page.PaginationContext).pageNum)" +
            ".concat(T(cn.px.power.module.common.page.PaginationContext).pageSize)" +
            ".concat(T(cn.px.power.module.common.page.PaginationContext).asc)" +
            ".concat(T(cn.px.power.module.common.page.PaginationContext).desc)")
    @Override
    public List<TbCoreCity> list(TbCoreCity coreCity) {
        return coreCityDao.list(Condition.getQueryWrapper(coreCity).lambda().orderByAsc(TbCoreCity::getSortNum));
    }

    @Override
    @Caching(evict = {@CacheEvict(value= CacheNames.CITY_PARENT_REDIS_KEY, key = "#coreCity.pcode ?: '-1'"),
            @CacheEvict(value= {CacheNames.CITY_PARENT_LIST_REDIS_KEY,CacheNames.CITY_PARENT_CODE_REDIS_KEY}, allEntries = true)})
    public boolean add(TbCoreCity coreCity) {
        coreCity.setFullname(Fc.isNotBlank(coreCity.getFullname())?coreCity.getFullname():coreCity.getName());
        coreCity.setCountryCode(Fc.isNotBlank(coreCity.getCountryCode())?coreCity.getCountryCode():JpowerConstants.COUNTRY_CODE);
        coreCity.setPcode(Fc.isNotBlank(coreCity.getPcode())?coreCity.getPcode():JpowerConstants.TOP_CODE);

        JpowerAssert.notTrue(queryByCode(coreCity.getCode()) != null, JpowerError.BUSINESS,"该编号已存在");

        // 新增的下级如果是第一个，则需要删除上级的缓存
        try {
            if (coreCityDao.count(Condition.<TbCoreCity>getQueryWrapper().lambda().eq(TbCoreCity::getPcode,coreCity.getPcode())) <= 0){
                TbCoreCity city = queryByCode(coreCity.getPcode());
                if (Fc.notNull(city)){
                    Fc.requireNotNull(Cm.getInstance().getCache(CacheNames.CITY_PARENT_REDIS_KEY),"缓存不存在").evict(city.getPcode());
                }
            }
        }catch (Exception e){
            log.warn("("+CacheNames.CITY_PARENT_REDIS_KEY+")缓存删除失败："+e.getMessage());
        }

        CacheUtil.clear(CacheNames.SYSTEM_REDIS_CACHE,Boolean.FALSE);
        return coreCityDao.save(coreCity);
    }

    @Override
    public TbCoreCity queryByCode(String cityCode) {
        return coreCityDao.getOne(Condition.<TbCoreCity>getQueryWrapper().lambda().eq(TbCoreCity::getCode,cityCode));
    }

    @Override
    @CacheEvict(value = {CacheNames.CITY_PARENT_REDIS_KEY,CacheNames.CITY_PARENT_LIST_REDIS_KEY,CacheNames.CITY_PARENT_CODE_REDIS_KEY},allEntries = true)
    public Boolean deleteBatch(List<String> ids) {

        List<Object> listCode = coreCityDao.listObjs(Condition.<TbCoreCity>getQueryWrapper().lambda().select(TbCoreCity::getCode).in(TbCoreCity::getId,ids));
        if(listCode.size()>0){
            long count = coreCityDao.count(Condition.<TbCoreCity>getQueryWrapper().lambda().in(TbCoreCity::getPcode,listCode));
            JpowerAssert.geZero(count,JpowerError.BUSINESS,"请先删除子区域");
        }

        CacheUtil.clear(CacheNames.SYSTEM_REDIS_CACHE,Boolean.FALSE);
        return coreCityDao.removeRealByIds(ids);
    }

    @Override
    @Cacheable(value = CacheNames.CITY_PARENT_REDIS_KEY,key = "#pcode ?: '-1'")
    public List<Tree<String>> lazyTree(String pcode) {
        return coreCityDao.tree(Condition.getLambdaTreeWrapper(TbCoreCity.class,TbCoreCity::getCode,TbCoreCity::getPcode)
                .lazy(pcode).unLambda().select("sort_num AS sort","name","id AS `key`"));
    }

    @Override
    @Caching(evict = {@CacheEvict(value= CacheNames.CITY_PARENT_REDIS_KEY, key = "#coreCity.pcode ?: '-1'"),
            @CacheEvict(value= {CacheNames.CITY_PARENT_LIST_REDIS_KEY,CacheNames.CITY_PARENT_CODE_REDIS_KEY}, allEntries = true)})
    public Boolean update(TbCoreCity coreCity) {
        TbCoreCity city = coreCityDao.getById(coreCity.getId());
        if (!StringUtil.equals(city.getCode(),coreCity.getCode())){
            JpowerAssert.isNull(queryByCode(coreCity.getCode()),JpowerError.BUSINESS,"编号已存在");
        }

        boolean is = coreCityDao.updateById(coreCity);
        if (is && Fc.isNotBlank(coreCity.getCode()) && !Fc.isNull(city) && !StringUtil.equals(city.getCode(),coreCity.getCode())){
            coreCityDao.update(new UpdateWrapper<TbCoreCity>().lambda()
                    .set(TbCoreCity::getPcode,coreCity.getCode())
                    .eq(TbCoreCity::getPcode,city.getCode()));
        }

        CacheUtil.clear(CacheNames.SYSTEM_REDIS_CACHE,Boolean.FALSE);
        return is;
    }

    @Override
    public CityVo getById(String id) {
        return coreCityDao.conver(super.getById(id));
    }

}
