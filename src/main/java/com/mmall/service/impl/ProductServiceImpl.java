package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加商品或更新商品信息
     * @param product
     * @return
     */
    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        if(product!=null){
            if(StringUtils.isNotBlank(product.getSubImages())){
                String[] subImages=product.getSubImages().split(",");
                product.setMainImage(subImages[0]);
                if(product.getId()!=null){
                    //productId不为空，则是更新产品
                    int rowCount=productMapper.updateByPrimaryKey(product);
                    if(rowCount>0){
                        return ServerResponse.createBySuccessMessage("更新商品信息成功");
                    }
                    return ServerResponse.createByErrorMessage("更新商品信息失败");
                }else{
                    int rowCount=productMapper.insert(product);
                    if(rowCount>0){
                        return ServerResponse.createBySuccessMessage("添加商品成功");
                    }
                    return ServerResponse.createByErrorMessage("添加商品失败");
                }
            }
        }
        return ServerResponse.createByErrorMessage("参数为空");
    }

    /**
     * 上下架
     * @param productId
     * @param status
     * @return
     */
    @Override
    public ServerResponse setSaleStatus(Integer productId, Integer status) {
        if(productId==null||status==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount=productMapper.insertSelective(product);
        if(rowCount>0){
            return ServerResponse.createBySuccessMessage("修改商品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改商品销售状态失败");
    }

    /**
     * 获取商品详细信息
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if(productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createByErrorMessage("产品已经下架或者删除");
        }
        ProductDetailVo productDetailVo=assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    /**
     * pojo->vo
     * @param product
     * @return
     */
    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo=new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setName(product.getName());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setCategoryId(product.getCategoryId());

        productDetailVo.setIamgeHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.wyn.com/"));

        Category category=categoryMapper.selectByPrimaryKey(product.getId());
        if(category==null){
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }


    /**
     * 后台查询商品列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> getList(Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList=productMapper.getProductList();
        List<ProductListVo> productListVoList= Lists.newArrayList();
        for(Product product:productList){
            ProductListVo productListVo=assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * pojo->vo
     * @param product
     * @return
     */
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo=new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        productListVo.setPrice(product.getPrice());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.wyn.com/"));
        return productListVo;
    }

    /**
     * 根据名称或者id查询商品
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList=productMapper.searchByProductNameAndProductId(productName,productId);
        List<ProductListVo> productListVoList=Lists.newArrayList();
        for(Product product:productList){
            ProductListVo productListVo=assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }
}
