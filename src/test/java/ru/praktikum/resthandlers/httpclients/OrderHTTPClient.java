package ru.praktikum.resthandlers.httpclients;

import io.restassured.response.Response;
import ru.praktikum.ApiUrls;
import ru.praktikum.request.entities.Order;

public class OrderHTTPClient extends BaseHTTPClient {
    public Response createOrder(Order order, String token) {
        return doPostRequest(
                ApiUrls.HOST + ApiUrls.ORDERS,
                order,
                "application/json",
                token
        );
    }
    public Response getIngredientList() {
        return doGetRequest(
                ApiUrls.HOST + ApiUrls.INGREDIENTS
        );
    }

    public Response getOrderList(String token) {
        return doGetRequest(
                ApiUrls.HOST + ApiUrls.ORDERS,
                token
        );
    }

    public Response getOrderListAll() {
        return doGetRequest(
                ApiUrls.HOST + ApiUrls.ORDERS_ALL
        );
    }
}