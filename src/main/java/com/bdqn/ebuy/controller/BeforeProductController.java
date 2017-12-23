package com.bdqn.ebuy.controller;

import com.bdqn.ebuy.pojo.EasybuyProduct;
import com.bdqn.ebuy.pojo.Product;
import com.bdqn.ebuy.pojo.ProductCategory;
import com.bdqn.ebuy.service.product.ProductService;
import com.bdqn.ebuy.service.productCategory.ProductCategoryService;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2017/12/19.
 */
@Controller
@RequestMapping("/product")
public class BeforeProductController {
    @Resource
    private ProductService productService;
    @Resource
    private ProductCategoryService productCategoryService;

    @RequestMapping("/listByType")
    public String listByType(Product product, Model model) {
        List<Product> products = productService.findProductByTypeID(product);
        model.addAttribute("products", products);
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
        return "before/productList";
    }

    @RequestMapping("/findByID")
    public String findByID(Integer id, Model model) {
        Product product = productService.findProductByID(id);
        model.addAttribute("product", product);
        return "before/product";
    }

    @RequestMapping("/queryAllProduct")
    public String queryAllProduct(Integer pageNum, Integer pageSize, Model model) {
        PageInfo<EasybuyProduct> pageInfo = productService.queryAllProduct(pageNum, pageSize);
        model.addAttribute("pageInfo", pageInfo);
        return "after/after_product";
    }


}
