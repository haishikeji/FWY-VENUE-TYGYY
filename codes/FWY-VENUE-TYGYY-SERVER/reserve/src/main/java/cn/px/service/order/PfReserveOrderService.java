package cn.px.service.order;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.binarywang.wxpay.exception.WxPayException;
import cn.px.dbs.entity.order.PfReserveOrder;
import cn.px.dbs.vo.order.ReserveOrderVo;
import cn.px.dbs.vo.order.WxPaySignVo;
import cn.px.power.module.common.service.BaseService;
import cn.px.vo.order.OrderStatusVo;

import java.util.List;

/**
 * <p>
 * 预约订单表 服务类
 * </p>
 *
 * @author 品讯科技
 * @since 2022-07-04
 */
public interface PfReserveOrderService extends BaseService<PfReserveOrder> {
    /**
     * 分页查询类型转换
     * @param page
	 * @param wrapper
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<cn.px.power.dbs.vo.order.ReserveOrderVo>
     * @author 品讯科技
     * @date 2024-08
     */
    Page<ReserveOrderVo> pageCover(Page<PfReserveOrder> page, Wrapper<PfReserveOrder> wrapper);

    Page<ReserveOrderVo> miniCover(Page<PfReserveOrder> page, Wrapper<PfReserveOrder> wrapper);
    /**
     * 套餐首次下单
     * @param order
     * @return java.lang.Boolean
     * @author 品讯科技
     * @date 2024-08
     */
    Boolean seatMealOrder(PfReserveOrder order);
    /**
     * 判断是否已经下单套餐内活动
     * @param order
     * @return java.lang.Boolean
     * @author 品讯科技
     * @date 2024-08
     */
    Boolean seatMealOrderSchel(PfReserveOrder order);
    /**
     * 订单列表 类型转换
     * @param memberId
     * @return java.util.List<cn.px.power.dbs.vo.order.ReserveOrderVo>
     * @author 品讯科技
     * @date 2024-08
     */
    List<ReserveOrderVo> getAppointment(String memberId);

    PfReserveOrder coverOrder(PfReserveOrder bean);
    /**
     * 异步方法进行订单占位
     * @param bean
     * @return void
     * @author 品讯科技
     * @date 2024-08
     */
    void ticket(PfReserveOrder bean);
    void ticketTo(PfReserveOrder bean);
    /**
     * 生成订单号二维码
     * @param orderNo
     * @return java.lang.String
     * @author 品讯科技
     * @date 2024-08
     */
    String qrCodeBase64(String orderNo);

    /**
     * 微信下单业务处理
     * @param order
     * @return java.lang.String
     * @author 品讯科技
     * @date 2024-08
     */
    String wxPayOrder( PfReserveOrder order) throws WxPayException;
    /**
     * 微信支付成功回调处理
     * @param orderNo
     * @return java.lang.Boolean
     * @author 品讯科技
     * @date 2024-08
     */
    Boolean notify(String orderNo) throws WxPayException;
    /**
     * 微信支付退款
     * @param orderNo
     * @return java.lang.Boolean
     * @author 品讯科技
     * @date 2024-08
     */
    Boolean refund(String orderNo) throws WxPayException;
    /**
     * 退款回调函数
     * @param orderNo
     * @return java.lang.Boolean
     * @author 品讯科技
     * @date 2024-08
     */
    Boolean notifyRefund(String orderNo) throws WxPayException;

    WxPaySignVo getWxPaySign(String prepayId);

    /**
     * 核销之后加入商户加入金额
     * @param order
     * @return java.lang.Boolean
     * @author 品讯科技
     * @date 2024-08
     */
    Boolean accountReceivableBool(PfReserveOrder order);

    Boolean timeJudge(PfReserveOrder order);

    /**
     * 统计订单数量
     * @param type
	 * @param id
     * @return java.util.List<cn.px.power.vo.order.OrderStatusVo>
     * @author 品讯科技
     * @date 2024-08
     */
    List<OrderStatusVo> getStatus(Integer type,String id);


    ReserveOrderVo getByOrder(String id);
}
