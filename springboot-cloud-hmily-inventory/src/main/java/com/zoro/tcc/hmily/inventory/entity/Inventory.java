
package com.zoro.tcc.hmily.inventory.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * InventoryDO.
 *
 *
 */
@Data
@ToString
@Setter
@Getter
public class Inventory implements Serializable {

    private static final long serialVersionUID = 6957734749389133832L;

    private Integer id;

    /**
     * 商品id.
     */
    private String productId;

    /**
     * 总库存.
     */
    private Integer totalInventory;

    /**
     * 锁定库存.
     */
    private Integer lockInventory;

}
