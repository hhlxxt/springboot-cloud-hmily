/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zoro.tcc.hmily.order.service.impl;

import com.zoro.tcc.hmily.order.client.AccountClient;
import com.zoro.tcc.hmily.order.client.InventoryClient;
import com.zoro.tcc.hmily.order.dto.AccountDTO;
import com.zoro.tcc.hmily.order.dto.InventoryDTO;
import com.zoro.tcc.hmily.order.entity.Order;
import com.zoro.tcc.hmily.order.entity.mapper.OrderMapper;
import com.zoro.tcc.hmily.order.enums.OrderStatusEnum;
import com.zoro.tcc.hmily.order.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.Hmily;
import org.dromara.hmily.common.exception.HmilyRuntimeException;
import org.dromara.hmily.core.concurrent.threadlocal.HmilyTransactionContextLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * PaymentServiceImpl.
 *
 *
 */
@Service
@Slf4j
@SuppressWarnings("all")
public class PaymentServiceImpl implements PaymentService {

   
    private final OrderMapper orderMapper;

    private final AccountClient accountClient;

    private final InventoryClient inventoryClient;

    @Autowired(required = false)
    public PaymentServiceImpl(OrderMapper orderMapper,
                              AccountClient accountClient,
                              InventoryClient inventoryClient) {
        this.orderMapper = orderMapper;
        this.accountClient = accountClient;
        this.inventoryClient = inventoryClient;
    }

    @Override
    @Hmily(confirmMethod = "confirmOrderStatus", cancelMethod = "cancelOrderStatus")
    public void makePayment(Order order) {
        //获取全局事务id
        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("订单服务开始执行try方法,全局事务Id为{}",transId);

        //幂等判断 判断local_try_log表中是否有try日志记录，如果有则不再执行
        if(orderMapper.isExistTry(transId)>0){
            log.info("订单服务 try 已经执行，无需重复执行,xid:{}",transId);
            return ;
        }

        //try悬挂处理，如果cancel、confirm有一个已经执行了，try不再执行
        if(orderMapper.isExistConfirm(transId)>0 || orderMapper.isExistCancel(transId)>0){
            log.info("订单服务  try悬挂处理  cancel或confirm已经执行，不允许执行try,xid:{}",transId);
            return ;
        }

        //插入try执行记录,用于幂等判断
        orderMapper.addTry(transId);


        order.setStatus(OrderStatusEnum.PAYING.getCode());
        orderMapper.update(order);

        //检查数据
        final BigDecimal accountInfo = accountClient.findByUserId(order.getUserId());

        final Integer inventoryInfo = inventoryClient.findByProductId(order.getProductId());

        if (accountInfo.compareTo(order.getTotalAmount()) < 0) {
            throw new HmilyRuntimeException("余额不足！");
        }

        if (inventoryInfo < order.getCount()) {
            throw new HmilyRuntimeException("库存不足！");
        }



        //扣除用户余额
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAmount(order.getTotalAmount());
        accountDTO.setUserId(order.getUserId());
        Boolean payment = accountClient.payment(accountDTO);
        if (payment == false ){
            throw new RuntimeException("调用账户微服务失败");
        }

        //减库存
        log.debug("===========执行springcloud  mockPaymentInventoryWithTryTimeout 扣减库存接口==========");
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setCount(order.getCount());
        inventoryDTO.setProductId(order.getProductId());
        Boolean aBoolean = inventoryClient.decrease(inventoryDTO);
        if(aBoolean == false ){
            throw new RuntimeException("调用库存微服务失败");
        }

        log.info("订单服务执行try方法结束,全局事务Id为{}",transId);
    }

    @Override
    @Hmily(confirmMethod = "confirmOrderStatus", cancelMethod = "cancelOrderStatus")
    public String mockPaymentInventoryWithTryException(Order order) {
        log.debug("===========执行springcloud  mockPaymentInventoryWithTryTimeout 扣减资金接口==========");

        //获取全局事务id
        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("订单服务开始执行try方法,全局事务Id为{}",transId);

        //幂等判断 判断local_try_log表中是否有try日志记录，如果有则不再执行
        if(orderMapper.isExistTry(transId)>0){
            log.info("订单服务 try 已经执行，无需重复执行,xid:{}",transId);
            return "";
        }

        //try悬挂处理，如果cancel、confirm有一个已经执行了，try不再执行
        if(orderMapper.isExistConfirm(transId)>0 || orderMapper.isExistCancel(transId)>0){
            log.info("订单服务  try悬挂处理  cancel或confirm已经执行，不允许执行try,xid:{}",transId);
            return "";
        }

        //插入try执行记录,用于幂等判断
        orderMapper.addTry(transId);

        order.setStatus(OrderStatusEnum.PAYING.getCode());
        orderMapper.update(order);
        //扣除用户余额
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAmount(order.getTotalAmount());
        accountDTO.setUserId(order.getUserId());
        Boolean payment = accountClient.payment(accountDTO);
        if (payment == false ){
            throw new RuntimeException("调用账户微服务失败");
        }

        log.debug("===========执行springcloud  mockPaymentInventoryWithTryTimeout 扣减库存接口==========");
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setCount(order.getCount());
        inventoryDTO.setProductId(order.getProductId());
        Boolean aBoolean = inventoryClient.mockWithTryException(inventoryDTO);
        if(aBoolean == false ){
            throw new RuntimeException("调用库存微服务失败");
        }

        return "success";
    }

    @Override
    @Hmily(confirmMethod = "confirmOrderStatus", cancelMethod = "cancelOrderStatus")
    public String mockPaymentInventoryWithTryTimeout(Order order) {
        log.debug("===========执行springcloud  mockPaymentInventoryWithTryTimeout 扣减资金接口==========");

        //获取全局事务id
        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("订单服务开始执行try方法,全局事务Id为{}",transId);

        //幂等判断 判断local_try_log表中是否有try日志记录，如果有则不再执行
        if(orderMapper.isExistTry(transId)>0){
            log.info("订单服务 try 已经执行，无需重复执行,xid:{}",transId);
            return "";
        }

        //try悬挂处理，如果cancel、confirm有一个已经执行了，try不再执行
        if(orderMapper.isExistConfirm(transId)>0 || orderMapper.isExistCancel(transId)>0){
            log.info("订单服务  try悬挂处理  cancel或confirm已经执行，不允许执行try,xid:{}",transId);
            return "";
        }

        //插入try执行记录,用于幂等判断
        orderMapper.addTry(transId);

        order.setStatus(OrderStatusEnum.PAYING.getCode());
        orderMapper.update(order);
        //扣除用户余额
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAmount(order.getTotalAmount());
        accountDTO.setUserId(order.getUserId());
        Boolean payment = accountClient.payment(accountDTO);
        if (payment == false ){
            throw new RuntimeException("调用账户微服务失败");
        }

        log.debug("===========执行springcloud  mockPaymentInventoryWithTryTimeout 扣减库存接口==========");
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setCount(order.getCount());
        inventoryDTO.setProductId(order.getProductId());
        Boolean aBoolean = inventoryClient.mockWithTryTimeout(inventoryDTO);
        if(aBoolean == false ){
            throw new RuntimeException("调用库存微服务失败");
        }

        return "success";
    }

    public void confirmOrderStatus(Order order) {
        //获取全局事务id 此方法不用做空回滚以及幂等判断 订单状态本来就是幂等的  怎么执行都是成功状态
        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("订单微服务 confirm begin 开始执行...xid:{},orderNo:{}",transId,order.getId());
        order.setStatus(OrderStatusEnum.PAY_SUCCESS.getCode());
        orderMapper.update(order);
        log.info("订单微服务 confirm end 开始执行...xid:{},orderNo:{}",transId,order.getId());
    }

    public void cancelOrderStatus(Order order) {
        //获取全局事务id  此方法不用做空回滚以及幂等判断 订单状态本来就是幂等的  怎么执行都是失败状态
        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("订单微服务 cancel begin 开始执行...xid:{},orderNo:{}",transId,order.getId());
        order.setStatus(OrderStatusEnum.PAY_FAIL.getCode());
        orderMapper.update(order);
        log.info("订单微服务 cancel end 开始执行...xid:{},orderNo:{}",transId,order.getId());
    }

}
