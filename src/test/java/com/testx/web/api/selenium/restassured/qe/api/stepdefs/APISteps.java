package com.testx.web.api.selenium.restassured.qe.api.stepdefs;

import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APISteps {

    public RequestSpecification request;
    String token;
    Response response;
    public String endPoint;
    public static List<Map<String,String>> apiList=new ArrayList<>();

    @When("I am initiate api request on {string}")
    public void initiateAPIRequest(String baseURL) throws Exception {
        RestAssured.baseURI=baseURL;
        request = RestAssured.given().log().all();
    }

    @When("I prepare {string} request")
    public void prepareEndpoint(String endpoint){
        this.endPoint=endpoint;
    }
    @When("I set the authorization for api")
    public void setAutherizationHeader() throws Exception {
        request.headers("Authorization" ,"bearer "+token);
    }

    @When("I set the query param")
    public void setQueryParam(List<Map<String,String>> data) throws Exception {
        Map<String,String> map=new HashMap<>();
            for(Map<String,String> mapOfData:data){
            map.put(mapOfData.get("key"),mapOfData.get("value"));
        }
        request.queryParams(map);
    }

    @When("I set the param")
    public void setParams(List<Map<String,String>> data) throws Exception {
        Map<String,String> map=new HashMap<>();
        for(Map<String,String> mapOfData:data){
            map.put(mapOfData.get("key"),mapOfData.get("value"));
        }
        request.params(map);
    }

    @When("I hit the {string} request")
    public void setParams(String requestType) throws Exception {
        switch (requestType){
            case "get":
                response= request.get(endPoint);
                break;
            case "post":
                response= request.post(endPoint);
                break;
            default:
                System.out.println("Provided"+requestType+" method is not available....");
        }
    }

    @When("I validate the status code as {string}")
    public void validateStatusCode(String statusCode){
        System.out.println("<===Response===>"+response.getBody().asString());
        Assert.assertEquals(statusCode,String.valueOf(response.getStatusCode()), "Found mismatch in status code...");
    }
    @When("I set content type as {string}")
    public void setContentType(String contentType) throws Exception {
        request.contentType(contentType);
    }

    @When("I collect API Data")
    public void collectAPIData(List<Map<String,String>> list) throws JSONException {
        Map<String,String> map=new HashMap<>();
        String responseData = response.getBody().asString();
        JSONArray array=new JSONArray(responseData);
        for(int i=0;i<array.length();i++) {
            for (int j = 0; j < list.size(); j++) {
                map.put(list.get(j).get("fieldName"), new JSONObject(array.get(i).toString()).getString(list.get(j).get("fieldName")));
            }
        }
        apiList.add(map);
        System.out.println("===apiList==="+apiList);
    }
}