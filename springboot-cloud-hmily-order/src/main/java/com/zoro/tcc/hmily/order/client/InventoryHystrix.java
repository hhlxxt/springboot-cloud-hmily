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

package com.zoro.tcc.hmily.order.client;

import com.zoro.tcc.hmily.order.dto.InventoryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 订单服务调用库存微服务异常降级类
 */
@Component
@Slf4j
public class InventoryHystrix implements InventoryClient {


    @Override
    public Boolean decrease(InventoryDTO inventoryDTO) {
        log.info("执行订单服务调用库存微服务断路器,方法:{},参数{}","decrease",inventoryDTO);
        return false;
    }

    @Override
    public Integer findByProductId(String productId) {
        log.info("执行订单服务调用库存微服务断路器,方法:{},参数{}","findByProductId",productId);
        return 0;
    }

    @Override
    public Boolean mockWithTryException(InventoryDTO inventoryDTO) {
        log.info("执行订单服务调用库存微服务断路器,方法:{},参数{}","mockWithTryException",inventoryDTO);
        return false;
    }

    @Override
    public Boolean mockWithTryTimeout(InventoryDTO inventoryDTO) {
        log.info("执行订单服务调用库存微服务断路器,方法:{},参数{}","mockWithTryTimeout",inventoryDTO);
        return false;
    }
}
