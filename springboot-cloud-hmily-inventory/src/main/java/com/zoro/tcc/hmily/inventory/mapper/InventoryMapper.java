
package com.zoro.tcc.hmily.inventory.mapper;

import com.zoro.tcc.hmily.inventory.dto.InventoryDTO;
import com.zoro.tcc.hmily.inventory.entity.Inventory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 *
 */
@SuppressWarnings("all")
@Mapper
public interface InventoryMapper {


    @Select("select id from inventory a where a.product_id=#{productId}  for update")
    int selectByProductId(InventoryDTO inventoryDTO);
    /**
     * Decrease int.
     *
     * @param inventoryDTO the inventory dto
     * @return the int
     */
    @Update("update inventory set total_inventory = total_inventory - #{count}," +
            " lock_inventory= lock_inventory + #{count} " +
            " where product_id =#{productId}  and  total_inventory >0  ")
    int decrease(InventoryDTO inventoryDTO);


    /**
     * Confirm int.
     *
     * @param inventoryDTO the inventory dto
     * @return the int
     */
    @Update("update inventory set " +
            " lock_inventory=  lock_inventory - #{count} " +
            " where product_id =#{productId}  and lock_inventory >0 ")
    int confirm(InventoryDTO inventoryDTO);


    /**
     * Cancel int.
     *
     * @param inventoryDTO the inventory dto
     * @return the int
     */
    @Update("update inventory set total_inventory = total_inventory + #{count}," +
            " lock_inventory= lock_inventory - #{count} " +
            " where product_id =#{productId}  and lock_inventory >0 ")
    int cancel(InventoryDTO inventoryDTO);

    /**
     * Find by product id inventory do.
     *
     * @param productId the product id
     * @return the inventory do
     */
    @Select("select id,product_id,total_inventory ,lock_inventory from inventory where product_id =#{productId}")
    Inventory findByProductId(String productId);

    /**
     * 增加某分支事务try执行记录
     * @param localTradeNo 本地事务编号
     * @return
     */
    @Insert("insert into local_try_log values(#{txNo},now());")
    int addTry(String localTradeNo);

    /**
     * 增加某分支事务confirm执行记录
     * @param localTradeNo 本地事务编号
     * @return
     */
    @Insert("insert into local_confirm_log values(#{txNo},now());")
    int addConfirm(String localTradeNo);

    /**
     * 增加某分支事务cancel执行记录
     * @param localTradeNo 本地事务编号
     * @return
     */
    @Insert("insert into local_cancel_log values(#{txNo},now());")
    int addCancel(String localTradeNo);


    /**
     * 查询分支事务try是否已执行
     * @param localTradeNo 本地事务编号
     * @return 记录数
     */
    @Select("select count(1) from local_try_log where tx_no = #{txNo} ")
    int isExistTry(String localTradeNo);
    /**
     * 查询分支事务confirm是否已执行
     * @param localTradeNo 本地事务编号
     * @return 记录数
     */
    @Select("select count(1) from local_confirm_log where tx_no = #{txNo} ")
    int isExistConfirm(String localTradeNo);

    /**
     * 查询分支事务cancel是否已执行
     * @param localTradeNo 本地事务编号
     * @return 记录数
     */
    @Select("select count(1) from local_cancel_log where tx_no = #{txNo} ")
    int isExistCancel(String localTradeNo);

}
