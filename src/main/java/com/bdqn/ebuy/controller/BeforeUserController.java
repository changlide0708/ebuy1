package com.bdqn.ebuy.controller;

import com.bdqn.ebuy.pojo.*;
import com.bdqn.ebuy.service.car.CarService;
import com.bdqn.ebuy.service.news.NewsService;
import com.bdqn.ebuy.service.product.ProductService;
import com.bdqn.ebuy.service.productCategory.ProductCategoryService;
import com.bdqn.ebuy.service.user.UserService;
import com.bdqn.ebuy.service.userAddress.UserAddressService;
import com.bdqn.ebuy.utils.Comm;
import com.bdqn.ebuy.utils.MD5;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2017/12/21.
 */
@Controller
@RequestMapping("/user")
public class BeforeUserController {
    @Resource
    private ProductCategoryService productCategoryService;
    @Resource
    private ProductService productService;
    @Resource
    private NewsService newsService;
    @Resource
    private UserService userService;
    @Resource
    private CarService carService;
    @Resource
    private UserAddressService userAddressService;

    @RequestMapping("/center")
    private String center(HttpSession session, Model model){

        return "before/user_center";
    }
    @RequestMapping("/address")
    private String address(HttpSession session, Model model){
        User currentUser = (User) session.getAttribute(Comm.CUR_USER);
        if(currentUser==null){
            return "redirect:/login.html";
        }
        model.addAttribute("addresses", userAddressService.queryAllByUserID(currentUser.getId()));
        return "before/address";
    }

    @RequestMapping(value = "beforeLogin",method = RequestMethod.POST)
    public String beforeLogin(String loginName, String password, HttpSession session, Model model) throws Exception {
        //总分类
        List<ProductCategory> l1Categories = productCategoryService.findAllByType(1);
        model.addAttribute("l1Categories", l1Categories);
        Map<String, List<ProductCategory>> l2cateMap = new HashMap<>();
        Map<String, List<ProductCategory>> l3cateMap = new HashMap<>();
        for (ProductCategory l1Category : l1Categories) {
            List<ProductCategory> l2Categories = productCategoryService.findAllByParentID(l1Category.getId());
            l2cateMap.put(l1Category.getName(), l2Categories);
            for (ProductCategory l2Category : l2Categories) {
                List<ProductCategory> l3Categories = productCategoryService.findAllByParentID(l2Category.getId());
                l3cateMap.put(l2Category.getName(), l3Categories);
            }
        }
        model.addAttribute("l2cateMap",l2cateMap);
        model.addAttribute("l3cateMap",l3cateMap);


        //首页新闻资讯
        PageInfo<News> newsPageInfo = newsService.queryNewsAllByVisible(1, 9);
        model.addAttribute("newsPageInfo", newsPageInfo);

        //首页下部分类
        List<Product> list628 = productService.queryProductBycategoryLevel1Id(628);//家居用品
        List<Product> list548 = productService.queryProductBycategoryLevel1Id(548);//化妆品
        List<Product> list676 = productService.queryProductBycategoryLevel1Id(676);//保健食品
        List<Product> list660 = productService.queryProductBycategoryLevel1Id(660);//休闲零食
        List<Product> list670 = productService.queryProductBycategoryLevel1Id(670);//数码家电
        List<Product> list681 = productService.queryProductBycategoryLevel1Id(681);//母婴
        model.addAttribute("productList", list628);
        model.addAttribute("productList1", list548);
        model.addAttribute("productList2", list676);
        model.addAttribute("productList3", list660);
        model.addAttribute("productList4", list670);
        model.addAttribute("productList5", list681);

        User user = userService.beforeLogin(loginName, MD5.getMD5(password) );
        if (user != null) {
            session.setAttribute(Comm.CUR_USER,user);

            //首页购物车
            //User currentUser = (User) session.getAttribute("cur_user");
            List<Car> cars = null;
            Integer sum = null;
            if(null!=user){
                cars = carService.findAll(user.getId());
                sum = carService.sumByUserID(user.getId());
            }
            //cars = carService.findAll(10);
            //sum = carService.sumByUserID(10);
            model.addAttribute("cars", cars);
            if(null!=cars){
                model.addAttribute("carsSize", cars.size());
            }else{
                model.addAttribute("carsSize", 0);
            }
            if(null!= sum){
                model.addAttribute("sum", sum);
            }else{
                model.addAttribute("sum", 0);
            }

            model.addAttribute("message", "用户名错误或密码错误");


            return "before/index";
        }
        return "before/login";
    }

    @RequestMapping(value = "beforeRegister",method = RequestMethod.POST)
    public String beforeRegister(User user,String password) throws Exception {
        user.setPassword(MD5.getMD5(password));
        int count = userService.beforeRegister(user);
        if (count > 0) {
            return "before/login";
        }
        return "before/Regist";
    }

    @ResponseBody
    @RequestMapping(value = "deExists",method = RequestMethod.GET,
            produces = {"application/json;charset=UTF-8"})
    public String deExists(String loginName)  {
        User user = userService.queryUserByLoginName(loginName);

        if (user!=null){
            return "success";
        }

        return "error";
    }
}
