package com.wilczewski.admin.product;

import com.wilczewski.admin.brand.BrandService;
import com.wilczewski.admin.car.CarService;
import com.wilczewski.shared.entity.Brand;
import com.wilczewski.shared.entity.Car;
import com.wilczewski.shared.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ProductController {

    private ProductService productService;
    private BrandService brandService;
    private CarService carService;

    @Autowired
    public ProductController(ProductService productService, BrandService brandService, CarService carService) {
        this.productService = productService;
        this.brandService = brandService;
        this.carService = carService;
    }

    @GetMapping("/products")
    public String listAll(Model model){
        List<Product> listProducts = productService.listAll();

        model.addAttribute("listProducts", listProducts);

        return "products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model){

        List<Brand> listBrands = brandService.listAll();
        List<Car> listCars = carService.listOfCars();

        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("listCars", listCars);
        model.addAttribute("pageTitle", "Nowy produkt");

        return "product_form";

    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes redirectAttributes){
        productService.save(product);
        redirectAttributes.addFlashAttribute("message", "Produkt został zapisany.");

        return "redirect:/products";
    }

    @GetMapping("/products/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Integer id,
                                              @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        productService.updateProductEnabledStatus(id, enabled);
        String status = enabled ? "aktywny" : "nieaktywny";
        String message = "Produkt o ID " + id + " jest " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Integer id,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            productService.delete(id);

            redirectAttributes.addFlashAttribute("message",
                    "Produkt o ID " + id + " został usunięty.");
        } catch (ProductNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/products";
    }

}
