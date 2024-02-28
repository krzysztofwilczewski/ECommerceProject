package com.wilczewski.admin.car;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarRestController {

    private CarService carService;

    @Autowired
    public CarRestController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping("/cars/check_unique")
    public String checkUnique(Integer id, String name, String alias){
        return carService.checkUnique(id, name, alias);
    }
}
