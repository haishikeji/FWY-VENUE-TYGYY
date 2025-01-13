package cn.px.vo.auth;

import cn.px.power.module.common.auth.UserInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName AuthInfo
 * @Description TODO 鉴权信息
 * @author 品讯科技
 * @date 2024-08
 * @Version 1.0
 */
@Data
public class AuthInfo {

    @ApiModelProperty("用户信息")
    private UserInfo user;
    @ApiModelProperty("令牌")
    private String accessToken;
    @ApiModelProperty("令牌类型")
    private String tokenType;
    @ApiModelProperty("刷新令牌")
    private String refreshToken;
    @ApiModelProperty("过期时间")
    private long expiresIn;

}
