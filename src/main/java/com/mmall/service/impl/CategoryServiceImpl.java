package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger=LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if(parentId==null|| StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category=new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        int rowCount=categoryMapper.insert(category);
        if(rowCount>0){
            return ServerResponse.createBySuccessMessage("添加产品分类成功");
        }
        return ServerResponse.createByErrorMessage("添加产品分类失败");
    }


    @Override
    public ServerResponse updateCategoryName(String categoryName, Integer categoryId) {
        if(categoryId==null||StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category=new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int rowCount=categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount>0){
            return ServerResponse.createBySuccessMessage("更新产品分类名字成功");
        }
        return ServerResponse.createByErrorMessage("更新产品分类名字失败");
    }

    @Override
    public ServerResponse getChildrenCategoryByParentId(Integer parentId) {
        List<Category> categoryList=categoryMapper.getChildrenCategoryByParentId(parentId);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }


    /**
     * 递归查询本节点的id和孩子节点的id
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet= Sets.newHashSet();
        findChildCategory(categorySet,categoryId);
        List<Integer> categoryIdList= Lists.newArrayList();
        if(categoryId!=null){
            for(Category category:categorySet){
                categoryIdList.add(category.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if(category!=null){
            categorySet.add(category);
        }
        //mybatis返回的List不会是null的，如果查询不到返回的只是size为0的List
        List<Category> categoryList=categoryMapper.getChildrenCategoryByParentId(categoryId);
        for(Category categoryItem:categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }
}
