package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
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
    public ServerResponse<User> login(String username, String password) {
        int resultCount=userMapper.checkUserName(username);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        // todo MD5加密
        String md5password=MD5Util.MD5EncodeUtf8(password);

        User user=userMapper.selectLogin(username,md5password);
        if(user==null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功",user);
    }


    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse response=checkValid(user.getUsername(),Const.USERNAME);
        if(!response.isSuccess()){
            return response;
        }
        response=checkValid(user.getEmail(),Const.EMAIL);
        if(!response.isSuccess()){
            return response;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount=userMapper.insert(user);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 校验
     * 不存在返回成功，存在返回失败
     * @param str
     * @param type
     * @return
     */
    @Override
    public ServerResponse checkValid(String str, String type) {
        if(StringUtils.isNotBlank(type)){
            if(Const.USERNAME.equals(type)){
                int resultCount=userMapper.checkUserName(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount=userMapper.checkEmail(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("检验成功");
    }


    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse checkValid=checkValid(username,Const.USERNAME);
        if(checkValid.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question=userMapper.getQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题为空");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount=userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            //说明问题的答案正确，并且是该用户的
            String forgetToken= UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String newpassword, String forgetToken) {
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，需要传递token过来");
        }
        ServerResponse checkValid=checkValid(username,Const.USERNAME);
        if(checkValid.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token=TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        if(StringUtils.equals(token,forgetToken)){
            String md5password=MD5Util.MD5EncodeUtf8(newpassword);
            int resultCount=userMapper.updatePasswordByUsername(username,md5password);
            if(resultCount>0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误，请重新获取重制密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }


    @Override
    public ServerResponse<String> resetPassword(String oldPassword, String newPassword, User user) {
        int rowCount=userMapper.checkPassword(MD5Util.MD5EncodeUtf8(oldPassword),user.getId());
        if(rowCount==0){
            return ServerResponse.createByErrorMessage("密码输入错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        int resultCount=userMapper.updateByPrimaryKeySelective(user);
        if(resultCount>0){
            return ServerResponse.createBySuccessMessage("修改密码成功");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        //username不能修改
        int rowCount=userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(rowCount>0){
            return ServerResponse.createByErrorMessage("该email已被其他用户使用，请更换email后再更新");
        }
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount=userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount>0){
            return ServerResponse.createBySuccess("更新用户信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新用户信息失败");
    }

    @Override
    public ServerResponse<User> getInformatino(Integer userId) {
        User user=userMapper.selectByPrimaryKey(userId);
        user.setPassword("");
        if(user==null){
            return ServerResponse.createByErrorMessage("找不到用户");
        }
        return ServerResponse.createBySuccess(user);
    }


    //backend


    @Override
    public ServerResponse checkAdmin(User user) {
        if(user!=null&&user.getRole().intValue()==Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
