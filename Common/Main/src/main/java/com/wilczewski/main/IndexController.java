package com.wilczewski.main;

import com.wilczewski.main.category.CategoryService;
import com.wilczewski.shared.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {

    private CategoryService categoryService;

    @Autowired
    public IndexController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public String main(Model model){

        List<Category> listCategories = categoryService.listNoChildrenCategories();
        model.addAttribute("listCategories", listCategories);

        return "index";
    }
}
