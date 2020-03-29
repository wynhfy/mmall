package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface IUserService {

    public ServerResponse<User> login(String username,String password);

    public ServerResponse<String> register(User user);

    public ServerResponse<String> checkValid(String str,String type);

}
