

package com.zoro.tcc.hmily.order.entity.mapper;

import com.zoro.tcc.hmily.order.entity.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * OrderMapper.
 *
 *
 */
@Mapper
public interface OrderMapper {

    /**
     * 保存订单.
     *
     * @param order 订单对象
     * @return rows
     */
    @Insert(" insert into `order` (create_time,number,status,product_id,total_amount,count,user_id) "
            + " values ( #{createTime},#{number},#{status},#{productId},#{totalAmount},#{count},#{userId})")
    int save(Order order);

    /**
     * 更新订单.
     *
     * @param order 订单对象
     * @return rows
     */
    @Update("update `order` set status = #{status} , total_amount=#{totalAmount} where number=#{number}")
    int update(Order order);

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
