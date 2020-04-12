
package com.zoro.tcc.hmily.account.service;

import com.zoro.tcc.hmily.account.dto.AccountDTO;
import com.zoro.tcc.hmily.account.entity.Account;
import org.dromara.hmily.annotation.Hmily;

/**
 * 账户逻辑接口类.
 *
 *
 */
public interface AccountService {

    /**
     * 扣款支付.
     *
     * @param accountDTO 参数dto
     * @return true
     */
    @Hmily
    boolean payment(AccountDTO accountDTO);

    /**
     * 获取用户账户信息.
     *
     * @param userId 用户id
     * @return AccountDO
     */
    Account findByUserId(String userId);
}
