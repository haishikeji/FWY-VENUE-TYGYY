package cn.px.dbs.entity.user;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import cn.px.power.module.base.annotation.Dict;
import cn.px.power.module.dbs.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 商家信息表
 * </p>
 *
 * @author 品讯科技
 * @since 2022-07-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="PfUserMerchant对象", description="商家信息表")
public class PfUserMerchant extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id 关联tb_core_user")
    private String userId;

    @ApiModelProperty(value = "活动类型 字典： ACTIVITY_TYPE；1=场地主办方 2=活动主办方")
//    @NotNull(message = "活动类型不可为空")
    @Dict(name = "ACTIVITY_TYPE",attributes = "activityTypeDict")
    private Integer activityType;

    @ApiModelProperty(value = "预约币")
    private BigDecimal reservationCurrency;

    @ApiModelProperty(value = "商家介绍")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String intrduce;

    @ApiModelProperty(value = "详情描述")
    private String description;

    @ApiModelProperty(value = "轮播图最大限制5张")
    private String rotationChart;

    @ApiModelProperty(value = "等级")
    private String level;

    @ApiModelProperty(value = "联系人二维码")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String contactImg;

    @ApiModelProperty(value = "发布状态RELEASE_STATUS 1发布待审核 2审核通过 3审核不通过")
    @Dict(name = "RELEASE_STATUS",attributes = "releaseStatusDict")
    private Integer releaseStatus;

    @ApiModelProperty(value = "驳回原因")
    private String reason;

    @ApiModelProperty(value = "位置经度")
    private String lon;

    @ApiModelProperty(value = "位置纬度")
    private String lat;

    /** 营业起始时间 */
    @ApiModelProperty(value = "营业起始时间")
    private String startTime;
    /** 营业结束时间 */
    @ApiModelProperty(value = "营业结束时间")
    private String closeTime;
    /** 营业执照 */
    @ApiModelProperty(value = "营业执照")
    private String license;
    /** 银行卡号 */
    @ApiModelProperty(value = "银行卡号")
    private String cardNo;
    /** 银行卡开户行 */
    @ApiModelProperty(value = "银行卡开户行")
    private String bank;
    /** 持卡人姓名 */
    @ApiModelProperty(value = "持卡人姓名")
    private String cardholderName;
    @ApiModelProperty(value = "二维码")
    private String qrcode;

    @ApiModelProperty(value = "发布状态 字典翻译")
    @TableField(exist = false)
    private String releaseStatusDict;

    @ApiModelProperty("活动类型 字典翻译")
    @TableField(exist = false)
    private String activityTypeDict;


}
