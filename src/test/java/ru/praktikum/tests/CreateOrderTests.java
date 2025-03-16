package ru.praktikum.tests;

import io.qameta.allure.Link;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.praktikum.request.entities.Ingredient;
import ru.praktikum.response.entities.IngredientsResponsed;
import ru.praktikum.resthandlers.apiclients.ResponseChecks;
import ru.praktikum.resthandlers.apiclients.OrderApiClient;
import ru.praktikum.resthandlers.apiclients.UserApiClient;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.fail;

@DisplayName("4. Создание заказа")
@Link(value = "Документация", url = "https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf")
public class CreateOrderTests {

    private String email, password, name, token;
    private List<Ingredient> ingredients;
    private final OrderApiClient orderApi = new OrderApiClient();
    private final UserApiClient userApi = new UserApiClient();
    private final ResponseChecks checks = new ResponseChecks();

    @Before
    @Step("Подготовка тестовых данных")
    public void prepareTestData() {
        email = "e-mail_" + UUID.randomUUID() + "@mail.com";
        password = "pass_" + UUID.randomUUID();
        name = "name";

        // Создание пользователя
        Response response = userApi.createUser(email, password, name);
        checks.checkStatusCode(response, 200);

        // Получение токена
        if (response.getStatusCode() == 200) {
            token = userApi.getToken(response);
        }

        // Получение списка ингредиентов
        response = orderApi.getIngredientList();
        checks.checkStatusCode(response, 200);

        // Десериализация ответа
        IngredientsResponsed ingredientsResponse = response.body().as(IngredientsResponsed.class);
        if (ingredientsResponse == null) {
            fail("Ответ от API не содержит данных");
        }

        ingredients = ingredientsResponse.getData();
        if (ingredients == null || ingredients.isEmpty()) {
            fail("Список ингредиентов пуст или не получен");
        }

        // Логирование для отладки
        System.out.println("Полученные ингредиенты: " + ingredients);
        for (Ingredient ingredient : ingredients) {
            System.out.println("Ингредиент: " + ingredient.getId() + ", " + ingredient.getName());
        }

        // Проверка, что токен получен
        if (token == null) {
            fail("Токен не получен");
        }
    }

    @After
    @Step("Удаление тестовых пользователей")
    public void cleanTestData() {
        if (token == null)
            return;

        checks.checkStatusCode(userApi.deleteUser(token), 202);
    }

    @Test
    @DisplayName("Создание заказа: с авторизацией и с ингредиентами")
    public void createOrderWithAuthAndIngredientsIsSuccess() {
        // Проверка, что список ингредиентов не пуст
        if (ingredients == null || ingredients.isEmpty()) {
            fail("Список ингредиентов пуст или не получен");
        }

        // Проверка, что первый и последний ингредиенты не равны null
        Ingredient firstIngredient = ingredients.get(0);
        Ingredient lastIngredient = ingredients.get(ingredients.size() - 1);

        if (firstIngredient == null || lastIngredient == null) {
            fail("Ингредиенты в списке равны null");
        }

        // Получаем ID первого и последнего ингредиента
        String firstIngredientId = firstIngredient.getId();
        String lastIngredientId = lastIngredient.getId();

        // Проверка, что ID ингредиентов не равны null
        if (firstIngredientId == null || lastIngredientId == null) {
            fail("ID ингредиентов равны null");
        }

        Response response = orderApi.createOrder(List.of(firstIngredientId, lastIngredientId), token);

        checks.checkStatusCode(response, 200);
        checks.checkLabelSuccess(response, "true");
    }

    @Test
    @DisplayName("Создание заказа: без авторизации и с ингредиентами")
    public void createOrderWithoutAuthAndWithIngredientsIsFailed() {
        // Получаем ID первого и последнего ингредиента
        String firstIngredientId = ingredients.get(0).getId();
        String lastIngredientId = ingredients.get(ingredients.size() - 1).getId();

        Response response = orderApi.createOrder(List.of(firstIngredientId, lastIngredientId), "");

        checks.checkStatusCode(response, 400);
    }

    @Test
    @DisplayName("Создание заказа: с авторизацией и без ингредиентов")
    public void createOrderWithAuthAndWithoutIngredientsIsSuccess() {
        Response response = orderApi.createOrder(List.of(), token);

        checks.checkStatusCode(response, 400);
        checks.checkLabelSuccess(response, "false");
        checks.checkLabelMessage(response, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа: с неверным хешем ингредиентов")
    public void createOrderWithAuthAndIncorrectIngredientsIsFailed() {
        // Получаем ID первого ингредиента и добавляем случайный UUID
        String firstIngredientId = ingredients.get(0).getId();
        String randomIngredientId = UUID.randomUUID().toString();

        Response response = orderApi.createOrder(List.of(firstIngredientId, randomIngredientId), token);

        checks.checkStatusCode(response, 500);
    }
}