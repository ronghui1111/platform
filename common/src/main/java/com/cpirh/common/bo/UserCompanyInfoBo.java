package com.cpirh.common.bo;

import lombok.Data;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/20 17:35
 */
@Data
public class UserCompanyInfoBo {
    private Integer companyId;
    private String companyName;
    private Integer orgId;
    private String orgName;
    private Integer groupId;
    private String groupName;
    private Integer positionId;
    private String positionName;
}
