package com.wilczewski.shared.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String alias;

    @Column(nullable = false)
    private String image;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Car parent;

    @OneToMany(mappedBy = "parent")
    private Set<Car> children = new HashSet<>();


    public Car() {
    }

    public Car(Integer id) {
        this.id = id;
    }

    public Car(Integer id, String name, String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }

    public Car(String name) {
        this.name = name;
        this.alias = name;
        this.image ="default";
    }

    public Car(String name, Car parent) {
        this(name);
        this.parent = parent;
    }

    public static Car copyIdAndName(Car car){
        Car copyCar = new Car();
        copyCar.setId(car.getId());
        copyCar.setName(car.getName());

        return copyCar;
    }

    public static Car copyIdAndName(Integer id, String name){
        Car copyCar = new Car();
        copyCar.setId(id);
        copyCar.setName(name);

        return copyCar;
    }

    public static Car copyFull(Car car){
        Car copyCar = new Car();
        copyCar.setId(car.getId());
        copyCar.setName(car.getName());
        copyCar.setImage(car.getImage());
        copyCar.setAlias(car.getAlias());
        copyCar.setHasChildren(car.getChildren().size() > 0);

        return copyCar;
    }

    public static Car copyFull(Car car, String name){
        Car copyCar = Car.copyFull(car);
        copyCar.setName(name);

        return copyCar;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Car getParent() {
        return parent;
    }

    public void setParent(Car parent) {
        this.parent = parent;
    }

    public Set<Car> getChildren() {
        return children;
    }

    public void setChildren(Set<Car> children) {
        this.children = children;
    }

    @Transient
    public String getImagePath(){
        if (this.id == null) return "/images/image.png";
        return "/car-images/" + this.id + "/" + this.image;
    }

    public  boolean isHasChildren(){
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren){
        this.hasChildren = hasChildren;
    }

    @Transient
    private boolean hasChildren;

    @Override
    public String toString() {
        return this.name;
    }
}
