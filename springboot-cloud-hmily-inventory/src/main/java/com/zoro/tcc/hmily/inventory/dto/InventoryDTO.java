
package com.zoro.tcc.hmily.inventory.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * InventoryDTO.
 *
 *
 */
@Data
@ToString
@Setter
@Getter
public class InventoryDTO implements Serializable {

    private static final long serialVersionUID = 8229355519336565493L;

    /**
     * 商品id.
     */
    private String productId;


    /**
     * 数量.
     */
    private Integer count;

}
