package com.wilczewski.admin.product;

import com.wilczewski.shared.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String listAll(Model model){
        List<Product> listProducts = productService.listAll();

        model.addAttribute("listProducts", listProducts);

        return "products";
    }

}
