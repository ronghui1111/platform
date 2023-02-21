package com.cpirh.common.constants;


/**
 * @author ronghui
 * @Description
 * @date 2023/2/14 10:56
 */
public interface AuthConstants {
    String TOKEN_HEADER = "X-User-Info";
    String LOGIN_URL = "/**/user/login";

    String SA_TOKEN_ROLE_KEY="user:roleId";
    String SA_TOKEN_PERMISSION_KEY ="user:button";

    String SA_TOKEN_DETAIL_KEY ="user:detail";
}
