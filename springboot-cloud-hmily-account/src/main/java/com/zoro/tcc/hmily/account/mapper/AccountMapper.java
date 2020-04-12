
package com.zoro.tcc.hmily.account.mapper;

import com.zoro.tcc.hmily.account.dto.AccountDTO;
import com.zoro.tcc.hmily.account.entity.Account;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * The interface Account mapper.
 *
 *
 */
@Mapper
@SuppressWarnings("all")
public interface AccountMapper {

    @Select("select id from account a where a.user_id =#{userId} for update")
    int selectByUserId(AccountDTO accountDTO);

    /**
     * Update int.
     *
     * @param accountDTO the account dto
     * @return the int
     */
    @Update("update account set balance = balance - #{amount}," +
            " freeze_amount= freeze_amount + #{amount} ,update_time = now()" +
            " where user_id =#{userId}  and  balance > 0  ")
    int update(AccountDTO accountDTO);


    /**
     * Confirm int.
     *
     * @param accountDTO the account dto
     * @return the int
     */
    @Update("update account set " +
            " freeze_amount= freeze_amount - #{amount}" +
            " where user_id =#{userId}  and freeze_amount >0 ")
    int confirm(AccountDTO accountDTO);


    /**
     * Cancel int.
     *
     * @param accountDO the account do
     * @return the int
     */
    @Update("update account set balance = balance + #{amount}," +
            " freeze_amount= freeze_amount -  #{amount} " +
            " where user_id =#{userId}  and freeze_amount >0")
    int cancel(AccountDTO accountDTO);

    /**
     * Find by user id account do.
     *
     * @param userId the user id
     * @return the account do
     */
    @Select("select id,user_id,balance, freeze_amount from account where user_id =#{userId} limit 1")
    Account findByUserId(String userId);

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
