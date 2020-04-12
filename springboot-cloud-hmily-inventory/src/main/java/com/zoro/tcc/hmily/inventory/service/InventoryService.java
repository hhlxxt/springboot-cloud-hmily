
package com.zoro.tcc.hmily.inventory.service;


import com.zoro.tcc.hmily.inventory.dto.InventoryDTO;
import com.zoro.tcc.hmily.inventory.entity.Inventory;
import org.dromara.hmily.annotation.Hmily;


/**
 *
 */
@SuppressWarnings("all")
public interface InventoryService {

    /**
     * 扣减库存操作.
     * 这一个tcc接口
     *
     * @param inventoryDTO 库存DTO对象
     * @return true
     */
    @Hmily
    Boolean decrease(InventoryDTO inventoryDTO);

    /**
     * 获取商品库存信息.
     * @param productId 商品id
     * @return InventoryDO
     */
    Inventory findByProductId(String productId);

    /**
     * mock 库存扣减try阶段异常.
     *
     * @param inventoryDTO dto
     * @return true
     */
    @Hmily
    Boolean mockWithTryException(InventoryDTO inventoryDTO);

    /**
     * mock 库存扣减try阶段超时.
     *
     * @param inventoryDTO dto
     * @return true
     */
    @Hmily
    Boolean mockWithTryTimeout(InventoryDTO inventoryDTO);

}
