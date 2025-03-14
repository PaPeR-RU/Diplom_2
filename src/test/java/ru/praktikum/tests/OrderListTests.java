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

@DisplayName("5. Получение списка заказов")
@Link(value = "Документация", url = "https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf")
public class OrderListTests {
    private String email, password, name, token;
    private List<Ingredient> ingredients;
    private final OrderApiClient orderApi = new OrderApiClient();
    private final UserApiClient userApi = new UserApiClient();
    private final ResponseChecks checks = new ResponseChecks();

    @Before
    @Step("Подготовка тестовых данных")
    public void prepareTestData() {
        email = "e-mail_" + UUID.randomUUID() + "@mail.com";
        password = "pass";
        name = "name";

        // Создание пользователя
        Response response = userApi.createUser(email, password, name);
        checks.checkStatusCode(response, 200);

        // Получение токена авторизации
        if (response.getStatusCode() == 200) {
            token = userApi.getToken(response);
        }

        // Получение списка ингредиентов
        response = orderApi.getIngredientList();
        checks.checkStatusCode(response, 200);

        ingredients = response.body().as(IngredientsResponsed.class).getData();

        // Проверка, что список ингредиентов не пуст
        if (ingredients == null || ingredients.isEmpty()) {
            fail("Список ингредиентов пуст или не получен");
        }

        // Создание заказа
        response = orderApi.createOrder(
                List.of(ingredients.get(0).getId(), ingredients.get(ingredients.size() - 1).getId()),
                token
        );
        checks.checkStatusCode(response, 200);

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
    @DisplayName("Получение заказов конкретного пользователя: авторизованный пользователь")
    public void getOrderListWithAuthIsSuccess() {
        Response response = orderApi.getOrderList(token);

        checks.checkStatusCode(response, 200);
        checks.checkLabelSuccess(response, "true");
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя: неавторизованный пользователь")
    public void getOrderListWithoutAuthIsFailed() {
        Response response = orderApi.getOrderList("");

        checks.checkStatusCode(response, 401);
        checks.checkLabelSuccess(response, "false");
        checks.checkLabelMessage(response, "You should be authorised");
    }

    @Test
    @DisplayName("Получение всех заказов")
    public void getOrderListAllIsSuccess() {
        Response response = orderApi.getOrderListAll();

        checks.checkStatusCode(response, 200);
        checks.checkLabelSuccess(response, "true");
    }
}