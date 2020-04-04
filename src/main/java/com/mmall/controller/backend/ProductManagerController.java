package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/product")
public class ProductManagerController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;

    /**
     * 新增商品或修改商品信息
     * @param session
     * @param product
     * @return
     */
    @ResponseBody
    @RequestMapping("save.do")
    public ServerResponse productSave(HttpSession session, Product product){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，请先登陆");
        }
        if(iUserService.checkAdmin(user).isSuccess()){
            return iProductService.saveOrUpdateProduct(product);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，没有权限");
        }
    }

    /**
     * 修改商品销售状态
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @ResponseBody
    @RequestMapping("set_sale_status.do")
    public ServerResponse setSaleStatus(HttpSession session,Integer productId,Integer status){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，请先登陆");
        }
        if(iUserService.checkAdmin(user).isSuccess()){
            return iProductService.setSaleStatus(productId,status);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，没有权限");
        }
    }


    /**
     * 获取商品详情
     * @param session
     * @param productId
     * @return
     */
    @ResponseBody
    @RequestMapping("detail.do")
    public ServerResponse getDetail(HttpSession session,Integer productId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，请先登陆");
        }
        if(iUserService.checkAdmin(user).isSuccess()){
            return iProductService.manageProductDetail(productId);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，没有权限");
        }
    }


    /**
     * 后台查询产品列表
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ResponseBody
    @RequestMapping("getList.do")
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,@RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，请先登陆");
        }
        if(iUserService.checkAdmin(user).isSuccess()){
            return iProductService.getList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，没有权限");
        }
    }

    /**
     * 后台搜索产品
     * @param session
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ResponseBody
    @RequestMapping("search.do")
    public ServerResponse productSearch(HttpSession session,String productName,Integer productId,@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,@RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，请先登陆");
        }
        if(iUserService.checkAdmin(user).isSuccess()){
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，没有权限");
        }
    }

}
