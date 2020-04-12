
package com.zoro.tcc.hmily.account.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 账户实体.
 *
 */
@Data
@ToString
@Getter
@Setter
public class Account implements Serializable {

    private static final long serialVersionUID = -81849676368907419L;
    private Integer id;

    private String userId;

    private BigDecimal balance;

    private BigDecimal freezeAmount;

    private Date createTime;

    private Date updateTime;

}
