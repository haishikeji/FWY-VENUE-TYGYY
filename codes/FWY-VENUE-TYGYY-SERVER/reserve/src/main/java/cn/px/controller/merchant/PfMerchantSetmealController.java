package cn.px.controller.merchant;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import cn.px.dbs.entity.merchant.PfMerchantSetmeal;
import cn.px.dbs.entity.merchant.PfSetmealActivity;
import cn.px.dbs.vo.merchant.PfMerchantSetmealVo;
import cn.px.power.module.base.enums.JpowerError;
import cn.px.power.module.base.exception.JpowerAssert;
import cn.px.power.module.base.vo.ResponseData;
import cn.px.power.module.common.controller.BaseController;
import cn.px.power.module.common.page.PaginationContext;
import cn.px.power.module.common.utils.Fc;
import cn.px.power.module.common.utils.ReturnJsonUtil;
import cn.px.power.module.mp.support.Condition;
import cn.px.service.merchant.PfMerchantSetmealService;
import cn.px.service.merchant.PfSetmealActivityService;
import cn.px.utils.constants.BusConstantsEnum;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 套餐表 前端控制器
 * </p>
 *
 * @author 品讯科技
 * @since 2022-07-04
 */
@Api(tags = "套餐模块")
@RestController
@RequestMapping("/merchant/setmeal")
@AllArgsConstructor
public class PfMerchantSetmealController extends BaseController {

    private PfMerchantSetmealService service;

    private PfSetmealActivityService pfSetmealActivityService;

    @ApiOperation("分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", defaultValue = "1", value = "页码", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", defaultValue = "10", value = "条数", paramType = "query", required = true),
            @ApiImplicitParam(name = "setmealName", value = "套餐名称", paramType = "query", required = false),

    })
    @GetMapping(value = "list", produces = "application/json")
    public ResponseData<Page<PfMerchantSetmealVo>> list(@ApiIgnore @RequestParam Map<String, Object> map){
        LambdaQueryWrapper<PfMerchantSetmeal> queryWrapper = Condition.getQueryWrapper(map,PfMerchantSetmeal.class).lambda();
        Page<PfMerchantSetmeal> page = service.page(PaginationContext.getMpPage(), queryWrapper);
        return ReturnJsonUtil.ok("查询成功",service.pageCover(page));
    }

    @ApiOperation("更新")
    @PutMapping(value = "/update", produces = "application/json")
    public ResponseData update(PfMerchantSetmeal bean){
        JpowerAssert.notEmpty(bean.getId(), JpowerError.Arg,"主键不可为空");
        JpowerAssert.notEmpty(bean.getActivityids(),JpowerError.Arg,"活动id集不可为空");
        return ReturnJsonUtil.status(service.linkedData(bean));
    }

    @ApiOperation("删除")
    @DeleteMapping(value = "/delete", produces = "application/json")
    public ResponseData delete(@ApiParam("主键，多个逗号分隔") @RequestParam String ids) {
        JpowerAssert.notEmpty(ids, JpowerError.Arg,"主键不可为空");
        if (!service.removeByIds(Fc.toStrList(ids))) {
            return ReturnJsonUtil.fail("删除失败");
        }
        //判断该套餐下是否关联活动
        if (Fc.isEmpty(pfSetmealActivityService.list(Condition.<PfSetmealActivity>getQueryWrapper().lambda().in(PfSetmealActivity::getSetmealId,Fc.toStrList(ids))))) {
            return ReturnJsonUtil.ok("操作成功");
        }
        return ReturnJsonUtil.status(pfSetmealActivityService.remove(Condition.<PfSetmealActivity>getQueryWrapper()
                .lambda().in(PfSetmealActivity::getSetmealId,Fc.toStrList(ids))));
    }

    @ApiOperation("新增")
    @PostMapping(value = "/add", produces = "application/json")
    public ResponseData add(@RequestBody @Valid PfMerchantSetmeal bean){
        bean.setId(null);
        JpowerAssert.notEmpty(bean.getActivityids(),JpowerError.Arg,"活动id集不可为空");
        List<PfSetmealActivity> list = Lists.newArrayList();
        bean.setTotalPrice(service.getTotalPrice(bean.getActivityids()));
        if (service.save(bean)) {
            bean.getActivityids().forEach(item -> {
                PfSetmealActivity setmealActivity = new PfSetmealActivity();
                setmealActivity.setActivityId(item);
                setmealActivity.setSetmealId(bean.getId());
                setmealActivity.setActivityType(BusConstantsEnum.ACTIVITY_TYPE.ACTIVITY.getValue());
                list.add(setmealActivity);
            });
        } else {
            return ReturnJsonUtil.fail("保存套餐失败");
        }
        return ReturnJsonUtil.status(pfSetmealActivityService.saveBatch(list));
    }

}
