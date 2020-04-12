
package com.zoro.tcc.hmily.inventory.service.impl;

import com.zoro.tcc.hmily.inventory.dto.InventoryDTO;
import com.zoro.tcc.hmily.inventory.entity.Inventory;
import com.zoro.tcc.hmily.inventory.mapper.InventoryMapper;
import com.zoro.tcc.hmily.inventory.service.InventoryService;
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
 * InventoryServiceImpl.
 *
 *
 */
@Service("inventoryService")
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    
    private final InventoryMapper inventoryMapper;

    @Autowired(required = false)
    public InventoryServiceImpl(InventoryMapper inventoryMapper) {
        this.inventoryMapper = inventoryMapper;
    }

    /**
     * 扣减库存操作.
     * 这一个tcc接口
     *
     * @param inventoryDTO 库存DTO对象
     * @return true
     */
    @Override
    @Hmily(confirmMethod = "confirmMethod", cancelMethod = "cancelMethod")
    @Transactional
    public Boolean decrease(InventoryDTO inventoryDTO) {

        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("库存服务开始执行decrease try方法，全局事务Id为{},库存信息:{}",transId,inventoryDTO);

        //添加数据库行锁 防止同时执行出错
        inventoryMapper.selectByProductId(inventoryDTO);

        //幂等判断 判断local_try_log表中是否有try日志记录，如果有则不再执行
        if(inventoryMapper.isExistTry(transId)>0){
            log.info("库存微服务decrease try 已经执行，无需重复执行,xid:{}",transId);
            return Boolean.TRUE;
        }

        //try悬挂处理，如果cancel、confirm有一个已经执行了，try不再执行
        if(inventoryMapper.isExistConfirm(transId)>0 || inventoryMapper.isExistCancel(transId)>0){
            log.info("库存微服务decrease  try悬挂处理  cancel或confirm已经执行，不允许执行try,xid:{}",transId);
            return Boolean.TRUE;
        }

        //插入try执行记录,用于幂等判断
        inventoryMapper.addTry(transId);


        inventoryMapper.decrease(inventoryDTO);

        log.info("库存服务执行decrease try方法结束，全局事务Id为{}",transId);

        return true;

    }

    /**
     * 获取商品库存信息.
     *
     * @param productId 商品id
     * @return InventoryDO
     */
    @Override
    public Inventory findByProductId(String productId) {
        return inventoryMapper.findByProductId(productId);
    }

    @Override
    @Hmily(confirmMethod = "confirmMethod", cancelMethod = "cancelMethod")
    @Transactional
    public Boolean mockWithTryException(InventoryDTO inventoryDTO) {
        //这里是模拟异常所以就直接抛出异常了
        throw new HmilyRuntimeException("库存扣减异常！");
    }

    @Override
    @Hmily(confirmMethod = "confirmMethod", cancelMethod = "cancelMethod")
    @Transactional(rollbackFor = Exception.class)
    public Boolean mockWithTryTimeout(InventoryDTO inventoryDTO) {
        try {
            //模拟延迟 当前线程暂停10秒
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //添加数据库行锁 防止同时执行出错
        inventoryMapper.selectByProductId(inventoryDTO);

        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("库存服务开始执行 mockWithTryTimeout try方法开始，全局事务Id为{},库存信息:{}",transId,inventoryDTO);

        //幂等判断 判断local_try_log表中是否有try日志记录，如果有则不再执行
        if(inventoryMapper.isExistTry(transId)>0){
            log.info("库存微服务 mockWithTryTimeout try 已经执行，无需重复执行,xid:{}",transId);
            return Boolean.TRUE;
        }

        //try悬挂处理，如果cancel、confirm有一个已经执行了，try不再执行
        if(inventoryMapper.isExistConfirm(transId)>0 || inventoryMapper.isExistCancel(transId)>0){
            log.info("库存微服务 mockWithTryTimeout  try悬挂处理  cancel或confirm已经执行，不允许执行try,xid:{}",transId);
            return Boolean.TRUE;
        }

        //插入try执行记录,用于幂等判断
        inventoryMapper.addTry(transId);

        log.info("==========springcloud调用扣减库存mockWithTryTimeout===========");
        final int decrease = inventoryMapper.decrease(inventoryDTO);
        if (decrease != 1) {
            throw new HmilyRuntimeException("库存不足");
        }

        log.info("库存服务开始执行 mockWithTryTimeout try方法结束，全局事务Id为{},库存信息:{}",transId,inventoryDTO);

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmMethodTimeout(InventoryDTO inventoryDTO) {
        try {
            //模拟延迟 当前线程暂停11秒
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("库存服务开始执行confirmMethodTimeout方法执行开始，全局事务Id为{},库存信息{}",transId,inventoryDTO);

        //添加数据库行锁 防止同时执行出错
        inventoryMapper.selectByProductId(inventoryDTO);

        //幂等判断
        if(inventoryMapper.isExistConfirm(transId) > 0){//执行过扣减  则不处理
            log.info("库存微服务已经处理过该库存信息,事务id:{}",transId);
            return Boolean.TRUE;
        }

        //增加处理信息
        inventoryMapper.addConfirm(transId);

        log.info("==========Springcloud调用扣减库存确认方法===========");
        inventoryMapper.decrease(inventoryDTO);

        log.info("库存服务开始执行confirmMethodTimeout方法执行结束，全局事务Id为{}",transId);

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmMethodException(InventoryDTO inventoryDTO) {
        log.info("==========Springcloud调用扣减库存确认方法===========");

        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("库存服务开始执行confirmMethodException方法执行开始，全局事务Id为{},库存信息{}",transId,inventoryDTO);

        //添加数据库行锁 防止同时执行出错
        inventoryMapper.selectByProductId(inventoryDTO);

        //幂等判断
        if(inventoryMapper.isExistConfirm(transId) > 0){//执行过扣减  则不处理
            log.info("库存微服务已经处理过该库存信息,事务id:{}",transId);
            return Boolean.TRUE;
        }

        //增加处理信息
        inventoryMapper.addConfirm(transId);


        final int decrease = inventoryMapper.decrease(inventoryDTO);
        if (decrease != 1) {
            throw new HmilyRuntimeException("库存不足");
        }
        log.info("库存服务开始执行confirmMethodException方法执行结束，全局事务Id为{}",transId);
        return true;
    }

    @Transactional
    public Boolean confirmMethod(InventoryDTO inventoryDTO) {
        log.info("==========Springcloud调用扣减库存确认方法===========");

        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("库存服务开始执行confirmMethod方法执行开始，全局事务Id为{},库存信息{}",transId,inventoryDTO);

        //添加数据库行锁 防止同时执行出错
        inventoryMapper.selectByProductId(inventoryDTO);

        //幂等判断
        if(inventoryMapper.isExistConfirm(transId) > 0){//执行过扣减  则不处理
            log.info("库存微服务已经处理过该库存信息,事务id:{}",transId);
            return Boolean.TRUE;
        }

        //增加处理信息
        inventoryMapper.addConfirm(transId);

        final int rows = inventoryMapper.confirm(inventoryDTO);
        log.info("库存服务开始执行confirmMethod方法执行结束，全局事务Id为{}",transId);
        return true;
    }

    @Transactional
    public Boolean cancelMethod(InventoryDTO inventoryDTO) {
        log.info("==========Springcloud调用扣减库存取消方法===========");
        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("账户服务开始执行cancel方法执行开始，全局事务Id为{},扣费信息{}",transId,inventoryDTO);

        //添加数据库行锁 防止同时执行出错
        inventoryMapper.selectByProductId(inventoryDTO);

        //幂等处理  检查是否已经处理过
        if(inventoryMapper.isExistCancel(transId) > 0){
            log.info("账户服务开已经执行过回滚操作,不再处理，全局事务Id为{}",transId);
            return Boolean.TRUE;
        }

        //空回滚处理  如果没有try记录直接返回
        if(inventoryMapper.isExistTry(transId) == 0){
            log.info("账户服务try没有执行,空回滚，cancel方法不处理，全局事务Id为{}",transId);
            return Boolean.TRUE;
        }
        //创建幂等判断条件
        int result = inventoryMapper.addCancel(transId);

        int rows = inventoryMapper.cancel(inventoryDTO);
        if(rows != 1){
            log.info("回滚库存信息失败，全局事务Id为{}",transId);
            throw new HmilyRuntimeException("回滚库存信息失败");
        }
        log.info("账户服务开始执行cancel方法执行结束，全局事务Id为{},扣费信息{}",transId,inventoryDTO);
        return true;
    }

}
