package ru.praktikum.response.entities;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import ru.praktikum.request.entities.Ingredient;

import java.util.List;

public class IngredientsResponsed {
    @Getter @Setter
    private String success;

    @Getter @Setter
    private List<Ingredient> data;

    public IngredientsResponsed() { }

    public IngredientsResponsed(String success, List<Ingredient> data) {
        this.success = success;
        this.data = data;
    }
}