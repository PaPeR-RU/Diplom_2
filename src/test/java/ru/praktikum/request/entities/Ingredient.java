package ru.praktikum.request.entities;

import lombok.Getter;
import lombok.Setter;

public class Ingredient {
    @Getter @Setter
    private String id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String type;
    @Getter @Setter
    private String proteins;
    @Getter @Setter
    private String fat;
    @Getter @Setter
    private String carbohydrates;
    @Getter @Setter
    private String calories;
    @Getter @Setter
    private String price;
    @Getter @Setter
    private String image;
    @Getter @Setter
    private String imageMobile;
    @Getter @Setter
    private String imageLarge;
    @Getter @Setter
    private String version;

    public Ingredient() { }

    public Ingredient(String id, String name, String type, String proteins, String fat,
                      String carbohydrates, String calories, String price, String image,
                      String imageMobile, String imageLarge, String version) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.proteins = proteins;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.calories = calories;
        this.price = price;
        this.image = image;
        this.imageMobile = imageMobile;
        this.imageLarge = imageLarge;
        this.version = version;
    }
}