package cn.px.builder.auth.granter;

import cn.px.builder.auth.AuthUserInfo;
import cn.px.builder.auth.TokenGranter;
import cn.px.builder.auth.TokenParameter;
import cn.px.power.module.base.exception.BusinessException;
import cn.px.power.module.common.auth.UserInfo;
import cn.px.power.module.common.utils.Fc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static cn.px.builder.auth.granter.OtherCodeTokenGranter.GRANT_TYPE;


/**
 * @author 品讯科技
 * @Description //TODO 第三方Code登录 默认实现类
 * @date 2024-08
 **/
@Slf4j
@Component(GRANT_TYPE)
public class OtherCodeTokenGranter implements TokenGranter {
    public static final String GRANT_TYPE = "otherCode";

    @Autowired(required = false)
    private AuthUserInfo authUserInfo;

    @Override
    public UserInfo grant(TokenParameter tokenParameter) {
        String otherCode = tokenParameter.getOtherCode();
        String tenantCode = tokenParameter.getTenantCode();
        if (Fc.isNotBlank(otherCode)) {

            if (!Fc.isNull(authUserInfo)){
                return authUserInfo.getOtherCodeUserInfo(tokenParameter);
            }else {
//                TbCoreUser result = UserCache.getUserByCode(otherCode,tenantCode);
//                return AuthUtil.toUserInfo(result);
                throw new BusinessException("暂不支持第三方验证码登录");
            }

        }
        return null;
    }

}
