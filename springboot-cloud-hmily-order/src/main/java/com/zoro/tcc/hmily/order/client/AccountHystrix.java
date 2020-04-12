
package com.zoro.tcc.hmily.order.client;

import com.zoro.tcc.hmily.order.dto.AccountDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 订单调用账户微服务异常降级类
 *
 *
 */
@Component
@Slf4j
public class AccountHystrix implements AccountClient {

    @Override
    public Boolean payment(AccountDTO accountDO) {
        log.info("执行订单服务调用账户微服务断路器,方法:{},参数{}","payment",accountDO);
        return false;
    }

    @Override
    public BigDecimal findByUserId(String userId) {
        log.info("执行订单服务调用账户微服务断路器,方法:{},参数{}","findByUserId",userId);
        return BigDecimal.ZERO;
    }
}
