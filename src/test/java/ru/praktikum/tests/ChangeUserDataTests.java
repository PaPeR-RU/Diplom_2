package ru.praktikum.tests;

import io.qameta.allure.Link;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.praktikum.resthandlers.apiclients.ResponseChecks;
import ru.praktikum.resthandlers.apiclients.UserApiClient;

import java.util.UUID;

import static org.junit.Assert.fail;

@DisplayName("3. Изменение данных пользователя")
@Link(value = "Документация", url = "https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf")
public class ChangeUserDataTests {
    private String email, password, name, token;
    private String secondUserEmail, secondUserPassword, secondUserName;
    private final ResponseChecks checks = new ResponseChecks();
    private final UserApiClient userApi = new UserApiClient();

    @Before
    @Step("Подготовка тестовых данных")
    public void prepareTestData() {
        email = "e-mail_" + UUID.randomUUID() + "@mail.com";
        password = "pass";
        name = "name";

        Response response = userApi.createUser(email, password, name);
        checks.checkStatusCode(response, 200);

        if (response.getStatusCode() == 200) {
            token = userApi.getToken(response);
        }
        if (token == null)
            fail("Тестовый пользователь не создан");

        secondUserEmail = "second_" + UUID.randomUUID() + "@mail.com";
        secondUserPassword = "second_pass";
        secondUserName = "second_name";

        Response secondUserResponse = userApi.createUser(secondUserEmail, secondUserPassword, secondUserName);
        checks.checkStatusCode(secondUserResponse, 200);
    }

    @After
    @Step("Удаление тестовых пользователей")
    public void cleanTestData() {
        if (token != null) {
            checks.checkStatusCode(userApi.deleteUser(token), 202);
        }

        Response secondUserTokenResponse = userApi.loginUser(secondUserEmail, secondUserPassword);
        if (secondUserTokenResponse.getStatusCode() == 200) {
            String secondUserToken = userApi.getToken(secondUserTokenResponse);
            checks.checkStatusCode(userApi.deleteUser(secondUserToken), 202);
        }
    }

    @Test
    @DisplayName("Изменение email пользователя: с авторизацией")
    public void changeUserEmailWithAuthIsSuccess() {
        String newEmail = "new_" + email;

        Response response = userApi.updateUser(newEmail, password, name, token);

        checks.checkStatusCode(response, 200);
        checks.checkLabelSuccess(response, "true");
        userApi.checkUser(response, newEmail, name);
    }

    @Test
    @DisplayName("Изменение name пользователя: с авторизацией")
    public void changeUserNameWithAuthIsSuccess() {
        String newName = "new_" + name;

        Response response = userApi.updateUser(email, password, newName, token);

        checks.checkStatusCode(response, 200);
        checks.checkLabelSuccess(response, "true");
        userApi.checkUser(response, email, newName);
    }

    @Test
    @DisplayName("Изменение данных пользователя: с авторизацией, передача почты второго пользователя")
    public void changeUserDataWithAuthWhenSendSecondUserEmailIsFailed() {
        Response response = userApi.updateUser(secondUserEmail, password, name, token);

        checks.checkStatusCode(response, 403);
        checks.checkLabelSuccess(response, "false");
        checks.checkLabelMessage(response, "User with such email already exists");
    }

    @Test
    @DisplayName("Изменение данных пользователя: без авторизации")
    public void changeUserDataWithoutAuthIsFailed() {
        String newEmail = "new_" + email;
        String newName = "new_" + name;

        Response response = userApi.updateUser(newEmail, password, newName, "");

        checks.checkStatusCode(response, 401);
        checks.checkLabelSuccess(response, "false");
        checks.checkLabelMessage(response, "You should be authorised");
    }
}