
package com.zoro.tcc.hmily.account.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *  账户Dto.
 *
 */
@Data
@ToString
@Setter
@Getter
public class AccountDTO implements Serializable {

    private static final long serialVersionUID = 7223470850578998427L;

    /**
     * 用户id.
     */
    private String userId;

    /**
     * 扣款金额.
     */
    private BigDecimal amount;


}
