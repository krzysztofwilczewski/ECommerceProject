package com.wilczewski.admin.car;

import com.wilczewski.shared.entity.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CarService {

    public static final int ROOT_MANUFACTURERS_PER_PAGE = 4;

    private CarRepository carRepository;

    @Autowired
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> listByPage(CarPageInfo pageInfo, int pageNum, String sortDir, String keyword) {
        Sort sort = Sort.by("name");


        if (sortDir.equals("asc")) {
            sort = sort.ascending();
        } else if (sortDir.equals("desc")) {
            sort = sort.descending();
        }

        Pageable pageable = PageRequest.of(pageNum-1, ROOT_MANUFACTURERS_PER_PAGE, sort);

        Page<Car> pageCars = null;

        if (keyword != null && !keyword.isEmpty()){
            pageCars = carRepository.search(keyword, pageable);
        } else {

            pageCars = carRepository.findRootCars(pageable);
        }

        List<Car> rootCars = pageCars.getContent();

        pageInfo.setTotalElements(pageCars.getTotalElements());
        pageInfo.setTotalpages(pageCars.getTotalPages());

        if (keyword != null && !keyword.isEmpty()){
            List<Car> searchResult = pageCars.getContent();
            for (Car car : searchResult){
                car.setHasChildren(car.getChildren().size() > 0);
            }
            return searchResult;

        } else {
            return listHierarchicalCars(rootCars, sortDir);
        }
    }

    private List<Car> listHierarchicalCars(List<Car> rootCars, String sortDir) {
        List<Car> hierarchicalCars = new ArrayList<>();

        for (Car rootCar: rootCars){
            hierarchicalCars.add(Car.copyFull(rootCar));

            Set<Car> children = sortSubCars(rootCar.getChildren(), sortDir);

            for (Car subCar: children){
                String name = "--" + subCar.getName();
                hierarchicalCars.add(Car.copyFull(subCar, name));

                listSubHierarchicalCars(hierarchicalCars, subCar, 1, sortDir);
            }
        }

        return hierarchicalCars;
    }


    private void listSubHierarchicalCars(List<Car> hierarchicalCars ,Car parent, int subLevel, String sortDir){

        Set<Car> children = sortSubCars(parent.getChildren(), sortDir);
        int newSubLevel = subLevel + 1;

        for (Car subCar : children){
            String name = "";
            for (int i = 0; i < newSubLevel; i++){
                name += "--";
            }
            name += subCar.getName();

            hierarchicalCars.add(Car.copyFull(subCar, name));

            listSubHierarchicalCars(hierarchicalCars, subCar, newSubLevel, sortDir);
        }
    }

    public List<Car> listOfCars() {

        List<Car> carsUsedInList = new ArrayList<>();
        Iterable<Car> carsInDb = carRepository.findRootCars(Sort.by("name").ascending());

        for (Car car : carsInDb){
            if (car.getParent() == null){
                carsUsedInList.add(Car.copyIdAndName(car));

                Set<Car> children = sortSubCars(car.getChildren());

                for (Car subCar : children){
                    String name = "--" + subCar.getName();
                    carsUsedInList.add(Car.copyIdAndName(subCar.getId(), name));

                    listChildren(carsUsedInList, subCar,1);
                }
            }
        }

        return carsUsedInList;
    }

    private void listChildren(List<Car> carsUsedInList ,Car parent, int level){
        int newLevel = level + 1;
        Set<Car> children = sortSubCars(parent.getChildren());

        for (Car subCar : children){
            String name = "";
            for (int i=0; i<newLevel; i++){
                name += "--";
            }
            name += subCar.getName();
            carsUsedInList.add(Car.copyIdAndName(subCar.getId(), name));

            listChildren(carsUsedInList ,subCar, newLevel);
        }
    }

    private SortedSet<Car> sortSubCars(Set<Car> children){
        return sortSubCars(children, "asc");
    }

    private SortedSet<Car> sortSubCars(Set<Car> children, String sortDir) {
        SortedSet<Car> sortedChildren = new TreeSet<>(new Comparator<Car>() {
            @Override
            public int compare(Car o1, Car o2) {
                if (sortDir.equals("asc")) {
                    return o1.getName().compareTo(o2.getName());
                } else {
                    return o2.getName().compareTo(o1.getName());
                }
            }
        });
        sortedChildren.addAll(children);
        return sortedChildren;
    }

    public String checkUnique(Integer id, String name, String alias){

        boolean isCreatingNew = (id == null || id == 0);

        Car carByName = carRepository.findByName(name);

        if (isCreatingNew){
            if (carByName != null){
                return "DuplicateName";
            } else {
                Car carByAlias = carRepository.findByAlias(alias);
                if(carByAlias != null) {
                    return "DuplicateAlias";
                }
            }
        } else {
            if (carByName != null && carByName.getId() != id) {
                return "DuplicateName";
            }
            Car carByAlias = carRepository.findByAlias(alias);
            if (carByAlias != null && carByAlias.getId() != id){
                return "DuplicateAlias";
            }
        }

        return "OK";
    }


    public Car save(Car car) {
        return carRepository.save(car);
    }


    public Car get(Integer id) throws CarNotFoundException {
        try{
            return carRepository.findById(id).get();
        } catch(NoSuchElementException exception) {
            throw new CarNotFoundException("Nie można znaleźć auta o ID " + id);
        }
    }


    public void delete(Integer id) throws CarNotFoundException {
        Long countById = carRepository.countById(id);

        if (countById == null || countById == 0){
            throw new CarNotFoundException("Nie można znaleźć auta o ID " + id);
        }
        carRepository.deleteById(id);
    }
}
