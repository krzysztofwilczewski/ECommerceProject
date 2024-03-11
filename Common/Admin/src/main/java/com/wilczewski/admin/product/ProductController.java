package com.wilczewski.admin.product;

import com.wilczewski.admin.FileUpload;
import com.wilczewski.admin.brand.BrandService;
import com.wilczewski.admin.car.CarService;
import com.wilczewski.shared.entity.Brand;
import com.wilczewski.shared.entity.Car;
import com.wilczewski.shared.entity.Product;
import com.wilczewski.shared.entity.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public String listAll(Model model) {
        List<Product> listProducts = productService.listAll();

        model.addAttribute("listProducts", listProducts);

        return "products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {

        List<Brand> listBrands = brandService.listAll();
        List<Car> listCars = carService.listOfCars();

        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("listCars", listCars);
        model.addAttribute("pageTitle", "Nowy produkt");
        model.addAttribute("numberOfExistingExtraImages",0);

        return "product_form";

    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes redirectAttributes,
                              @RequestParam("fileImage") MultipartFile mainImageMultipart,
                              @RequestParam("extraImage") MultipartFile[] extraImageMultiparts,
                              @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames
                              )
            throws IOException {
        setMainImageName(mainImageMultipart, product);
        setExistingExtraImageNames(imageIDs, imageNames, product);
        setNewExtraImageNames(extraImageMultiparts, product);
        setProductDetails(detailIDs, detailNames, detailValues, product);

        Product savedProduct = productService.save(product);

        saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

        deleteExtraImagesWeredRemovedOnForm(product);

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

            String productExtraImagesDir = "../product-images/" + id + "/extras";
            String productImagesDir = "../product-images/" + id;

            FileUpload.removeDir(productExtraImagesDir);
            FileUpload.removeDir(productImagesDir);

            redirectAttributes.addFlashAttribute("message",
                    "Produkt o ID " + id + " został usunięty.");
        } catch (ProductNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id, Model model,
                              RedirectAttributes ra) {
        try {
            Product product = productService.get(id);
            List<Brand> listBrands = brandService.listAll();
            List<Car> listCars = carService.listOfCars();
            Integer numberOfExistingExtraImages = product.getImages().size();

            model.addAttribute("product", product);
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("listCars", listCars);
            model.addAttribute("pageTitle", "Edycja produktu (ID: " + id + ")");
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);


            return "product_form";

        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());

            return "redirect:/products";
        }
    }

    @GetMapping("/products/detail/{id}")
    public String viewProductDetails(@PathVariable("id") Integer id, Model model,
                                     RedirectAttributes ra) {
        try {
            Product product = productService.get(id);
            model.addAttribute("product", product);

            return "product_detail";

        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());

            return "redirect:/products";
        }
    }

    private void saveUploadedImages(MultipartFile mainImageMultipart,
                                    MultipartFile[] extraImageMultiparts, Product savedProduct) throws IOException {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            String uploadDir = "../product-images/" + savedProduct.getId();

            FileUpload.cleanDir(uploadDir);


            FileUpload.saveFile(uploadDir, fileName, mainImageMultipart);
        }

        if (extraImageMultiparts.length > 0) {
            String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";

            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (multipartFile.isEmpty()) continue;

                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                FileUpload.saveFile(uploadDir, fileName, multipartFile);
            }
        }

    }

    private void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
        if (extraImageMultiparts.length > 0) {
            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    if (!product.containsImageName(fileName)) {
                        product.addExtraImage(fileName);
                    }
                }
            }
        }
    }

    private void setMainImageName(MultipartFile mainImageMultipart, Product product) {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            product.setMainImage(fileName);
        }

    }

    private void deleteExtraImagesWeredRemovedOnForm(Product product) {
        String extraImageDir = "../product-images/" + product.getId() + "/extras";
        Path dirPath = Paths.get(extraImageDir);

        try {
            Files.list(dirPath).forEach(file -> {
                String filename = file.toFile().getName();

                if (!product.containsImageName(filename)) {
                    try {
                        Files.delete(file);

                    } catch (IOException e) {
                    }
                }

            });
        } catch (IOException ex) {

        }
    }

    private void setExistingExtraImageNames(String[] imageIDs, String[] imageNames,
                                            Product product) {
        if (imageIDs == null || imageIDs.length == 0) return;

        Set<ProductImage> images = new HashSet<>();

        for (int count = 0; count < imageIDs.length; count++) {
            Integer id = Integer.parseInt(imageIDs[count]);
            String name = imageNames[count];

            images.add(new ProductImage(id, name, product));
        }

        product.setImages(images);

    }

    private void setProductDetails(String[] detailIDs, String[] detailNames,
                                   String[] detailValues, Product product) {
        if (detailNames == null || detailNames.length == 0) return;

        for (int count = 0; count < detailNames.length; count++) {
            String name = detailNames[count];
            String value = detailValues[count];

            Integer id = Integer.parseInt(detailIDs[count]);

            if (id != 0) {
                product.addDetail(id, name, value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetail(name, value);
            }
        }
    }



}