package com.mmall.controller.back;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
@Controller
@RequestMapping("/manage/category")
public class CategoryManagerController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    /**
     *  新增分类
     * @param session
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping("add_category.do")
    @ResponseBody
    public ServiceResponse addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") int parentId){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆,请登录！");
        }
        //校验是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员 插入分类
            return iCategoryService.addCategory(categoryName,parentId);
        }else {
            return ServiceResponse.createByErrorMessage("无操作权限，您不是管理员！");
        }
    }

    /**
     * 修改分类名称
     * @param session
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServiceResponse setCategoryName (HttpSession session,Integer categoryId, String categoryName){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆,请登录！");
        }
        //校验是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //更新categoryName
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }else {
            return ServiceResponse.createByErrorMessage("无操作权限，您不是管理员！");
        }
    }

    @RequestMapping("get_category.do")
    @ResponseBody
    public ServiceResponse getChildrenParallelCategory(HttpSession session ,@RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆,请登录！");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询子节点的 category 不递归 平级信息
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else {
            return ServiceResponse.createByErrorMessage("无操作权限，您不是管理员！");
        }
    }

    /**
     * 递归查询当前节点和其字节点及一下节点的id
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServiceResponse getCategoryAndDeepChildrenCategory(HttpSession session ,@RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆,请登录！");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询当前节点的 和其子节点id 递归
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }else {
            return ServiceResponse.createByErrorMessage("无操作权限，您不是管理员！");
        }
    }



}
