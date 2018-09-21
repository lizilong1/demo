package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    //校验用户名是否存在
    int checkUsername(String username);
    //校验email 是否使用
    int checkEmail(String email);

    User selectLogin(@Param("username") String username,@Param("password") String password);

    String selectQuestionByUsername(String username);

    int checkAnswer(@Param("username") String username,@Param("question") String question,@Param("answer") String answer);

    int updatePasswordByUsername(@Param("username")String username, @Param("password")String md5Password);

    int checkPassword(@Param("password")String passwordOld,@Param("userId") Integer id);

    int checkEmailByUserId(@Param("email")String email,@Param("userId") Integer id);
}