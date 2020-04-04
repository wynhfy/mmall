package com.mmall.service;

import com.mmall.common.ServerResponse;

public interface ICategoryService {

    public ServerResponse addCategory(String categoryName,Integer parentId);

    public ServerResponse updateCategoryName(String categoryName,Integer categoryId);

    public ServerResponse getChildrenCategoryByParentId(Integer parentId);

    public ServerResponse selectCategoryAndChildrenById(Integer categoryId);

}
