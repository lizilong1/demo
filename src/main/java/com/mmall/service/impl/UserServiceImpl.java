package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public ServiceResponse<User> login(String username, String password) {

        int i = userMapper.checkUsername(username);

        if (i==0){
            return ServiceResponse.createByErrorMessage("用户名不存在");
        }
        //密码登陆 MD5加密
        String md5password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5password);

        if(user==null){
            return ServiceResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess("登陆成功！",user);
    }

    public ServiceResponse<String> register (User user){
        ServiceResponse<String> validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int i = userMapper.insert(user);
        if (i==0){
            return ServiceResponse.createByErrorMessage("注册失败");
        }
        return ServiceResponse.createBySuccessMessqge("注册成功");
    }

    /**
     *  校验接口
     * @param str 校验value
     * @param type value 类型 用户名 or email
     * @return
     */
    public ServiceResponse<String> checkValid(String str,String type){

        if (StringUtils.isNotBlank(type)){
            if(Const.USERNAME.equals(type)){
                if(StringUtils.isBlank(str)){
                    return ServiceResponse.createByErrorMessage("用户名不能为空");
                }
                int i = userMapper.checkUsername(str);
                if (i>0){
                    return ServiceResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int i =userMapper.checkEmail(str);
                if (i>0){
                    return ServiceResponse.createByErrorMessage("Email已存在");
                }
            }
        }else {
            return ServiceResponse.createByErrorMessage("参数错误！");
        }
        return ServiceResponse.createBySuccessMessqge("校验成功");
    }


    public ServiceResponse<String> selectQuestion(String username){

        ServiceResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
        if(validResponse.isSuccess()){
            return ServiceResponse.createByErrorMessage("用户不存在！");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServiceResponse.createBySuccess(question);
        }
        return ServiceResponse.createByErrorMessage("找回密码的问题为空！");
    }

    public ServiceResponse<String> checkAnswer(String username ,String question ,String answer){

        int resultCount = userMapper.checkAnswer(username,question,answer);
        if (resultCount>0){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServiceResponse.createBySuccess(forgetToken);
        }
        return ServiceResponse.createByErrorMessage("问题的答案错误！");
    }

    public ServiceResponse<String> forgetResetPassword(String username ,String passwordNew ,String forgetToken){

        if (StringUtils.isBlank(forgetToken)) {
            return ServiceResponse.createByErrorMessage("参数错误！forgetToken参数缺失。");
        }
        ServiceResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
        if(validResponse.isSuccess()){
            return ServiceResponse.createByErrorMessage("用户不存在！");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)){
            return ServiceResponse.createByErrorMessage("token无效或者已过期！");
        }
        if(StringUtils.equals(forgetToken,token)){
            String md5Password =MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount = userMapper.updatePasswordByUsername(username,md5Password);
            if(resultCount>0){
                return ServiceResponse.createBySuccessMessqge("修改密码成功！");
            }
        }else{
            return ServiceResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
        return ServiceResponse.createByErrorMessage("修改密码失败！");
    }

    public ServiceResponse<String> onlineResetPassword(String passwordOld,String passwordNew,User user){

        //防止横向越权 要校验密码是不是这个用户的密码
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());

        if(resultCount==0){
            return ServiceResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));

        int updateCount =userMapper.updateByPrimaryKeySelective(user);

        if (updateCount>0){
            return ServiceResponse.createBySuccessMessqge("密码更新成功!");
        }
        return ServiceResponse.createByErrorMessage("密码更新失败!");

    }

    public ServiceResponse<User> updateInformation(User user){
        //用户名不更新
        //email 校验非当前用户是否使用了此email
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());

        if(resultCount>0){
            ServiceResponse.createByErrorMessage("此email已存在,请更换email!");
        }
        User updateUser = new User();

        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setQuestion(user.getQuestion());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);

        if(updateCount>0){
            return ServiceResponse.createBySuccess("更新个人信息成功!",updateUser);
        }
        return ServiceResponse.createBySuccessMessqge("更新个人信息失败!");
    }

    public ServiceResponse<User> getInformation(Integer userId ){

        User user = userMapper.selectByPrimaryKey(userId);
        if(user ==null){
            return ServiceResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess(user);
    }
}
