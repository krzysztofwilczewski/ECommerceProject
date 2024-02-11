package com.wilczewski.admin.category;

import com.wilczewski.shared.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CategoryController {

    private CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public String listAll(Model model){
        List<Category> listCategories = categoryService.listAll();
        model.addAttribute("listCategories", listCategories);

        return "categories";
    }
}
