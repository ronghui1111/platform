package com.cpirh.common.bo;

import lombok.Data;

import java.util.List;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/20 17:33
 */
@Data
public class LoginDetailBo {
    private Integer userId;
    private String account;
    private String name;
    private String email;
    private String mobile;
    private String userFlag;
    private List<String> roles;
    private List<String> buttons;
    private List<UserCompanyInfoBo> companyInfos;
}
