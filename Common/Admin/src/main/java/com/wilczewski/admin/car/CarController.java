package com.wilczewski.admin.car;


import com.wilczewski.admin.FileUpload;
import com.wilczewski.shared.entity.Car;
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
import java.util.List;

@Controller
public class CarController {

    private CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/cars")
    public String listFirstPage(String sortDir, Model model){

        return listByPage(1, sortDir, null, model);
    }

    @GetMapping("/cars/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, String sortDir, String keyword, Model model){
        if (sortDir == null || sortDir.isEmpty()){
            sortDir = "asc";
        }

        CarPageInfo pageInfo = new CarPageInfo();
        List<Car> listOfCars = carService.listByPage(pageInfo, pageNum, sortDir, keyword);

        long startCount = (pageNum-1) * carService.ROOT_MANUFACTURERS_PER_PAGE + 1;
        long stopCount = startCount + carService.ROOT_MANUFACTURERS_PER_PAGE - 1;
        if (stopCount > pageInfo.getTotalElements()) {
            stopCount = pageInfo.getTotalElements();
        }

        String reverseSort = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("totalPages", pageInfo.getTotalpages());
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", "name");
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startCount", startCount);
        model.addAttribute("stopCount", stopCount);
        model.addAttribute("listOfCars", listOfCars);
        model.addAttribute("reverseSort", reverseSort);

        return "cars";
    }

    @GetMapping("/cars/new")
    public String newCar(Model model){
        Car car = new Car();
        List<Car> listCars = carService.listOfCars();

        model.addAttribute("listCars", listCars);
        model.addAttribute("car", car);
        model.addAttribute("pageTitle", "Nowe auto");

        return "car_form";
    }

    @PostMapping("/cars/save")
    public String saveCar(Car car, @RequestParam("fileImage") MultipartFile multipartFile, RedirectAttributes redirectAttributes) throws IOException {

        if (!multipartFile.isEmpty()) {

            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            car.setImage(fileName);

            Car savedCar = carService.save(car);

            String uploadDir = "../car-images/" + savedCar.getId();

            FileUpload.cleanDir(uploadDir);
            FileUpload.saveFile(uploadDir, fileName, multipartFile);

        } else {
            carService.save(car);
        }

        redirectAttributes.addFlashAttribute("message", "Nowe auto zostało zapisane.");

        return "redirect:/cars";

    }

    @GetMapping("/cars/edit/{id}")
    public String editCar(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes){
        try {

            Car car = carService.get(id);
            List<Car> listCars = carService.listOfCars();

            model.addAttribute("car", car);
            model.addAttribute("listCars", listCars);
            model.addAttribute("pageTitle", "Edycja auta (ID: " + id + ")");

            return "car_form";

        } catch (CarNotFoundException exception){
            redirectAttributes.addFlashAttribute("message", exception.getMessage());

            return "redirect:/cars";
        }
    }

    @GetMapping("/cars/delete/{id}")
    public String deleteCar(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes){
        try {
            carService.delete(id);
            String manufacturerDir = "../car-images/" + id;
            FileUpload.removeDir(manufacturerDir);

            redirectAttributes.addFlashAttribute("message", "Auto o ID " + id + " zostało usunięte");
        } catch (CarNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }
        return "redirect:/cars";
    }


}
