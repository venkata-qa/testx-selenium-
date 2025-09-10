
package com.testx.web.api.selenium.restassured.qe.api.util;

import io.restassured.response.Response;
import org.junit.Test;
import org.testng.Assert;

public class ApiDemoUnitTests {


    @Test
    public void validateStatusCode()
    {
        Response response = ApiDemo.testGetUserDetails();
        Assert.assertEquals(response.statusCode(),200);

    }

    @Test
    public void validateResponseTime()
    {
        Response response = ApiDemo.testGetUserDetails();
        Assert.assertTrue(response.time()<10000);

    }
    @Test
    public void validateContentType()
    {
        Response response = ApiDemo.testGetUserDetails();
        Assert.assertEquals(response.getContentType(),"application/json; charset=utf-8");

    }

    @Test
    public void validateUserName()
    {
        Response response = ApiDemo.testGetUserDetails();
        Assert.assertEquals(response.jsonPath().getString("name"),"EXPECTED_NAME");

    }
}
