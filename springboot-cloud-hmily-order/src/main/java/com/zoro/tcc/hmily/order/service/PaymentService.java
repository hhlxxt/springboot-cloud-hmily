
package com.zoro.tcc.hmily.order.service;


import com.zoro.tcc.hmily.order.entity.Order;

/**
 * 订单支付逻辑处理接口类.
 *
 *
 */
public interface PaymentService {

    /**
     * 订单支付.
     *
     * @param order 订单实体
     */
    void makePayment(Order order);

    /**
     * mock订单支付的时候库存异常.
     *
     * @param order 订单实体
     * @return String
     */
    String mockPaymentInventoryWithTryException(Order order);

    /**
     * mock订单支付的时候库存超时.
     *
     * @param order 订单实体
     * @return String
     */
    String mockPaymentInventoryWithTryTimeout(Order order);


}
