package com.cpirh.gateway.handler;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/21 10:52
 */
@Service
public class AuthorityRouterHandler {
    public List<String> getIncludeList() {
        return Lists.newArrayList("/**");
    }

    public List<String> getExcludeList() {
        return Lists.newArrayList("/favicon.ico", "/**/webjars/**", "/**/doc.html", "/**/login", "/**/user/exclude",  "/**/v3/api-docs/**");
    }
}
