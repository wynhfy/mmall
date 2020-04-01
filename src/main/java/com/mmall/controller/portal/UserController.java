package com.mmall.controller.portal;


import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

     @Autowired
     private IUserService iUserService;

    /**
     * 用户登陆
     * @param username
     * @param password
     * @param session
     * @return
     */
     @ResponseBody
     @RequestMapping(value = "login.do",method = RequestMethod.POST)
     public ServerResponse<User> login(String username, String password, HttpSession session){
         ServerResponse<User> response=iUserService.login(username,password);
         if(response.isSuccess()){
             session.setAttribute(Const.CURRENT_USER,response.getData());
         }
         return response;
     }

    /**
     * 用户退出登陆
     * @param session
     * @return
     */
     @ResponseBody
     @RequestMapping(value = "logout.do",method = RequestMethod.GET)
     public ServerResponse<String> logout(HttpSession session){
         session.removeAttribute(Const.CURRENT_USER);
         return ServerResponse.createBySuccessMessage("退出成功");
     }


    /**
     * 用户注册
     * @param user
     * @return
     */
     @ResponseBody
     @RequestMapping(value = "register.do",method = RequestMethod.POST)
     public ServerResponse<String> register(User user){
        return iUserService.register(user);
     }

    /**
     * 校验
     * @param str 具体的value值
     * @param type  类型:用户名或者邮箱
     * @return
     */
     @ResponseBody
     @RequestMapping(value = "check_valid" ,method = RequestMethod.GET)
     public ServerResponse<String> checkValid(String str,String type){
          return iUserService.checkValid(str,type);
     }


    /**
     * 获取用户信息
     * @param session
     * @return
     */
     @ResponseBody
     @RequestMapping(value = "get_user_info.do",method = RequestMethod.GET)
     public ServerResponse<User> getUserInfo(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户还未登陆,无法获取当前用户信息");
     }


    /**
     * 得到找回密码时的问题
     * @param username
     * @return
     */
     @RequestMapping(value = "forget_get_question.do",method = RequestMethod.GET)
     @ResponseBody
     public ServerResponse<String> forgetGetQuestion(String username){
         return iUserService.selectQuestion(username);
     }

    /**
     * 检查找回密码问题的答案对不对
     * @param username
     * @param question
     * @param answer
     * @return
     */
     @ResponseBody
     @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.GET)
     public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
         return iUserService.checkAnswer(username,question,answer);
     }

     @ResponseBody
     @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.GET)
     public ServerResponse<String> forgetResetPassword(String username,String newpassword,String token){
         return iUserService.forgetResetPassword(username,newpassword,token);
     }

}
