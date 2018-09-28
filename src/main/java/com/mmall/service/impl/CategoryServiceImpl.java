package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Service("iCategoryServiceImpl")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;

    public ServiceResponse addCategory(String categoryName, Integer parentId){

        if(parentId==null|| StringUtils.isBlank(categoryName)){
            return ServiceResponse.createByErrorMessage("插入品类参数有误或不完整！");
        }
        Category category =new Category();
        category.setId(parentId);
        category.setName(categoryName);
        category.setStatus(true);

        int resultCount = categoryMapper.insert(category);

        if (resultCount>0){
            return ServiceResponse.createBySuccessMessqge("添加品类成功！");
        }
        return ServiceResponse.createByErrorMessage("添加品类失败！");
    }

    public ServiceResponse updateCategoryName(Integer categoryId,String categoryName){
        if(categoryId==null|| StringUtils.isBlank(categoryName)){
            return ServiceResponse.createByErrorMessage("更新品类参数有误或不完整！");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);

        if (rowCount>0){
            return ServiceResponse.createBySuccess("更新品类名称成功");
        }
        return ServiceResponse.createByErrorMessage("更新品类名称失败");

    }

    public ServiceResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> list = categoryMapper.selectChildrenIdByParentId(categoryId);
        if (CollectionUtils.isEmpty(list)){
            logger.info("未找到当前分类的子分类");
        }
        return ServiceResponse.createBySuccess(list);
    }

    /**
     * 递归查询当前节点和其字节点及一下节点的id
     * @param categoryId
     * @return
     */

    public ServiceResponse selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildrenCategory(categorySet,categoryId);

        List<Integer> categoryIdLists = Lists.newArrayList();

        if (categoryId!=null){
            for (Category category : categorySet) {
                categoryIdLists.add(category.getId());
            }
        }

        return ServiceResponse.createBySuccess(categoryIdLists);
    }

    private Set<Category> findChildrenCategory(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category!=null){
            categorySet.add(category);
        }
        List<Category> categoryList = categoryMapper.selectChildrenIdByParentId(categoryId);
        for (Category category1 : categoryList) {
            findChildrenCategory(categorySet,category1.getId());
        }
        return categorySet;
    }

}
