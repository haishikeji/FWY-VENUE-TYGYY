package cn.px.dbs.entity.merchant;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import cn.px.power.module.base.annotation.Dict;
import cn.px.power.module.dbs.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 商户设置退款规则表
 * </p>
 *
 * @author 品讯科技
 * @since 2022-07-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="PfMerchantRefund对象", description="商户设置退款规则表")
public class PfMerchantRefund extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户id")
    private String merchantId;

    @ApiModelProperty(value = "活动类型 活动类型 字典： ACTIVITY_TYPE；1=场地 2=活动")
    @NotNull(message = "活动类型不可为空")
    @Dict(name = "ACTIVITY_TYPE",attributes = "activityTypeDict")
    private Integer activityType;

    @ApiModelProperty(value = "活动id")
    @NotBlank(message = "活动id不可为空")
    private String activityId;

    @ApiModelProperty(value = "排期id ")
    @NotBlank(message = "排期id不可为空")
    private String scheduleId;

    @ApiModelProperty(value = "退款时间")
    private Date refundDate;

    @ApiModelProperty(value = "退款比例（单位%）")
    @NotNull(message = "退款比例")
    private BigDecimal proportion;

    @ApiModelProperty(value = "退款规则 例如超过3小时 3 超过5小时 5")
    @NotBlank(message = "退款规则不可为空")
    private String rule;

    @ApiModelProperty("活动类型 字典翻译")
    @TableField(exist = false)
    private String activityTypeDict;
}
