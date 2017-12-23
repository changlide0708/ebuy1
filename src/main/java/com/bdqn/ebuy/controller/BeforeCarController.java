package com.bdqn.ebuy.controller;

import com.alibaba.fastjson.JSON;
import com.bdqn.ebuy.pojo.*;
import com.bdqn.ebuy.service.car.CarService;
import com.bdqn.ebuy.service.order.OrderService;
import com.bdqn.ebuy.service.orderDetailService.OrderDetailService;
import com.bdqn.ebuy.service.product.ProductService;
import com.bdqn.ebuy.service.user.UserService;
import com.bdqn.ebuy.service.userAddress.UserAddressService;
import com.bdqn.ebuy.utils.Comm;
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
 * Created by hp on 2017/12/20.
 */
@Controller
@RequestMapping("/car")
public class BeforeCarController {
    @Resource
    private CarService carService;
    @Resource
    private ProductService productService;
    @Resource
    private UserService userService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private OrderService orderService;
    @Resource
    private UserAddressService userAddressService;

    @RequestMapping(value = "/addCar",method = RequestMethod.POST)
    @ResponseBody
    public String addCar(Car car){
        Float price = productService.findProductByID(car.getProduct().getId()).getPrice();
        car.setSum((int) price.floatValue() * car.getCarProductQuatity());
        int result = carService.addCar(car);
        if(result>0){
            return JSON.toJSONString(Comm.success());
        }
        return JSON.toJSONString(Comm.failed());
    }

    @RequestMapping("/listCar")
    public String listCar(Integer userID, Model model){
        List<Car> cars = carService.findAll(userID);
        if(0==cars.size()){
            return "redirect:/index/shl";
        }
        model.addAttribute("cars", cars);
        int sum = carService.sumByUserID(userID);
        model.addAttribute("sum", sum);
        return "before/car";
    }

    @RequestMapping("/listCar2")
    public String listCar2(Integer userID, HttpSession session, Model model){
        List<Car> cars = carService.findAll(userID);
        if(0==cars.size()){
            return "redirect:/index/shl";
        }
        model.addAttribute("cars", cars);
        int sum = carService.sumByUserID(userID);
        model.addAttribute("sum", sum);
        User currentUser = userService.detailUser(userID);
        //User currentUser = userService.detailUser(10);
        session.setAttribute(Comm.CUR_USER, currentUser);
        //添加订单
        //1、添加订单
        Order order = new Order();
        order.setUserId(currentUser.getId());
        order.setLoginName(currentUser.getLoginName());
        order.setCost(sum);
        //查询客户默认地址
        UserAddress userAddress = userAddressService.findByUserIDDefault(userID);
        order.setUserAddress(userAddress.getAddress());
        Integer addOrderResult = orderService.addOrder(order);
        model.addAttribute("order", order);
        int orderID = order.getId();
        //2、添加订单详情
        if(null!=cars){
            for (Car car : cars) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderId(orderID);
                orderDetail.setProductId(car.getProduct().getId());
                orderDetail.setQuantity(car.getCarProductQuatity());
                orderDetail.setCost(car.getProduct().getPrice());
                orderDetailService.addOrderDetail(orderDetail);
            }
        }
        return "before/car2";
    }
    @RequestMapping("/listCar3")
    public String listCar3(Integer orderID,Integer userID, HttpSession session, Model model){
        List<Car> cars = carService.findAll(userID);
        if(0==cars.size()){
            return "redirect:/index/shl";
        }
        model.addAttribute("cars", cars);
        int sum = carService.sumByUserID(userID);
        model.addAttribute("sum", sum);
        User currentUser = userService.detailUser(userID);
        //User currentUser = userService.detailUser(10);
        session.setAttribute(Comm.CUR_USER, currentUser);
        //查询客户默认地址
        UserAddress userAddress = userAddressService.findByUserIDDefault(userID);
        model.addAttribute("userAddress", userAddress);
        //查询订单
        Order order = orderService.findOrderByID(orderID);
        model.addAttribute("order", order);
        return "before/car3";
    }

    @RequestMapping(value = "/updateCar",method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String updateCar(Car car){
        int result = carService.updateCar(car);
        int sum = carService.sumByUserID(car.getCarUserID());
        Map<String, Integer> map = new HashMap<>();
        map.put("sum", sum);
        return JSON.toJSONString(map);
    }

    @RequestMapping("/delCar")
    public String delCar(Integer userID,Integer carID, Model model){
        carService.deleteCarByID(carID);
        List<Car> cars = carService.findAll(userID);
        if(0==cars.size()){
            return "redirect:/index/shl";
        }
        model.addAttribute("cars", cars);
        int sum = carService.sumByUserID(userID);
        model.addAttribute("sum", sum);
        return "before/car";
    }




}
