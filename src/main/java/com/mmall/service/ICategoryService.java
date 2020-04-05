package com.mmall.service;

import com.mmall.common.ServerResponse;
import java.util.List;

public interface ICategoryService {

    public ServerResponse addCategory(String categoryName,Integer parentId);

    public ServerResponse updateCategoryName(String categoryName,Integer categoryId);

    public ServerResponse getChildrenCategoryByParentId(Integer parentId);

    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);

}
