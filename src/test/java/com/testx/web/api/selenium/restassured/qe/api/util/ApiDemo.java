
package com.testx.web.api.selenium.restassured.qe.api.util;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ApiDemo {


    public static void main(String[] args) {
        Response response = testGetUserDetails();
        System.out.println(response.getContentType());
    }
    public static Response testGetUserDetails() {
        // Set base URL
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

        // Specify user ID for the request
        int userId = 1;

        // Send GET request and capture the response
        Response response = given()
                .pathParam("userId", userId)
                .when()
                .get("/users/{userId}");
        System.out.println(response.body().prettyPrint());
      return response;
    }
}
