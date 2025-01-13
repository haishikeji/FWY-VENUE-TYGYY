package cn.px.dbs.dao.order.mapper;

import cn.px.dbs.entity.order.PfReserveOrder;
import cn.px.power.module.dbs.dao.mapper.base.JpowerBaseMapper;
import cn.px.vo.order.OrderStatusVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 预约订单表 Mapper 接口
 * </p>
 *
 * @author 品讯科技
 * @since 2022-07-04
 */
public interface PfReserveOrderMapper extends JpowerBaseMapper<PfReserveOrder> {
    List<OrderStatusVo> getStatus(@Param("type")int type,@Param("id")String id);
}
