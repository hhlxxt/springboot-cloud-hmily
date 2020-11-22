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

package com.zoro.tcc.hmily.account.service.impl;


import com.zoro.tcc.hmily.account.dto.AccountDTO;
import com.zoro.tcc.hmily.account.entity.Account;
import com.zoro.tcc.hmily.account.mapper.AccountMapper;
import com.zoro.tcc.hmily.account.service.AccountService;
import com.zoro.tcc.hmily.account.service.InLineService;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.Hmily;
import org.dromara.hmily.common.exception.HmilyRuntimeException;
import org.dromara.hmily.core.concurrent.threadlocal.HmilyTransactionContextLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 账户逻辑实现类..
 *
 *
 */
@Service("accountService")
@Slf4j
public class AccountServiceImpl implements AccountService {


    private final AccountMapper accountMapper;

    @Autowired
    private InLineService inLineService;

    /**
     * Instantiates a new Account service.
     *
     * @param accountMapper the account mapper
     */
    @Autowired(required = false)
    public AccountServiceImpl(final AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Override
    @Hmily(confirmMethod = "confirm", cancelMethod = "cancel")
    @Transactional
    public boolean payment(final AccountDTO accountDTO) {

        //行锁控制并发
        int i = accountMapper.selectByUserId(accountDTO);

        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("账户服务开始执行try方法，全局事务Id为{}",transId);

        //幂等判断 判断local_try_log表中是否有try日志记录，如果有则不再执行
        if(accountMapper.isExistTry(transId)>0){
            log.info("账户微服务 try 已经执行，无需重复执行,xid:{}",transId);
            return Boolean.TRUE;
        }

        //try悬挂处理，如果cancel、confirm有一个已经执行了，try不再执行
        if(accountMapper.isExistConfirm(transId)>0 || accountMapper.isExistCancel(transId)>0){
            log.info("账户微服务  try悬挂处理  cancel或confirm已经执行，不允许执行try,xid:{}",transId);
            return Boolean.TRUE;
        }

        //插入try执行记录,用于幂等判断
        accountMapper.addTry(transId);

        //预留金额
        int result = accountMapper.update(accountDTO);
        if(result != 1){
            log.info("账务微服务预留账户金额失败,事务id:{},待扣减信息:{}",transId,accountDTO);
            throw new RuntimeException("账务微服务预留账户金额失败");
        }
        log.info("账户服务开始执行try结束，全局事务Id为{}",transId);

        return Boolean.TRUE;

    }

    @Override
    public Account findByUserId(final String userId) {
        return accountMapper.findByUserId(userId);
    }

    /**
     * Confirm boolean.
     *
     * @param accountDTO the account dto
     * @return the boolean
     */
    @Transactional
    public boolean confirm(final AccountDTO accountDTO) {

        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("账户服务开始执行confirm方法执行开始，全局事务Id为{},扣费信息{}",transId,accountDTO);

        if(accountMapper.isExistConfirm(transId) > 0){//执行过扣减  则不处理
            log.info("账户微服务已经处理过该扣费信息,事务id:{},扣费信息:{}",transId , accountDTO);
            return Boolean.TRUE;
        }

        //增加处理信息
        accountMapper.addConfirm(transId);

        final int rows = accountMapper.confirm(accountDTO);

        log.info("账户服务开始执行confirm方法执行结束，全局事务Id为{}",transId);

        return Boolean.TRUE;
    }


    /**
     * Cancel boolean.
     *
     * @param accountDTO the account dto
     * @return the boolean
     */
    @Transactional
    public boolean cancel(final AccountDTO accountDTO) {
        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("账户服务开始执行cancel方法执行开始，全局事务Id为{},扣费信息{}",transId,accountDTO);

        //幂等处理  检查是否已经处理过
        if(accountMapper.isExistCancel(transId) > 0){
            log.info("账户服务开已经执行过回滚操作,不再处理，全局事务Id为{},扣费信息{}",transId,accountDTO);
            return Boolean.TRUE;
        }

        //空回滚处理  如果没有try记录直接返回
        if(accountMapper.isExistTry(transId) == 0){
            log.info("账户服务try没有执行,空回滚，cancel方法不处理，全局事务Id为{},扣费信息{}",transId,accountDTO);
            accountMapper.addCancel(transId);
            return Boolean.TRUE;
        }
        //创建幂等判断条件
        int result = accountMapper.addCancel(transId);

        //回滚预留资源
        final int rows = accountMapper.cancel(accountDTO);
        if (rows != 1) {
            throw new HmilyRuntimeException("取消扣减账户异常！");
        }
        log.info("账户服务开始执行cancel方法执行结束，全局事务Id为{}",transId);
        return Boolean.TRUE;
    }
}
