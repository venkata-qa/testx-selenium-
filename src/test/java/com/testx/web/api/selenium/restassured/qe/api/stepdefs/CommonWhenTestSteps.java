
package com.testx.web.api.selenium.restassured.qe.api.stepdefs;

import com.testx.web.api.selenium.restassured.qe.api.httpservicemanager.HttpResponseManager;
import com.testx.web.api.selenium.restassured.qe.api.httpservicemanager.RestRequestManager;
import com.testx.web.api.selenium.restassured.qe.api.enums.ApiContext;
import com.testx.web.api.selenium.restassured.qe.api.util.ApiUtilManager;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CommonWhenTestSteps {

  HttpResponseManager httpResponseManager;
  TestManagerContext testManagerContext;
  RestRequestManager restRequestManager;

  private static final String API_RESPONSE_PATH="src/test/resources/api_response_data/";

  public CommonWhenTestSteps(TestManagerContext context) {
    testManagerContext = context;
    httpResponseManager = testManagerContext.getHttpResponse();
    restRequestManager = testManagerContext.getRestRequest();
  }

  @When("^the client performs (.+) request on API \"(.+)\"$")
  public void perform_Http_Request(String httpMethod, String url) throws Throwable {
    httpResponseManager.setResponsePrefix("");
    ApiUtilManager apiUtilManager = new ApiUtilManager();
    httpResponseManager.setReponse(httpResponseManager.doRequest(httpMethod, apiUtilManager.getBasePath(url)));
  }

  @When("I call method {string}")
  public void iCallMethodPOST(String httpMethod) throws Exception {
    httpResponseManager.setResponsePrefix("");
    String basePath = (String) testManagerContext.getScenarioContext().getContext(ApiContext.BASE_PATH);
    httpResponseManager.setReponse(httpResponseManager.doRequest(httpMethod, basePath));
  }

  @And("I get the response")
  public void iGetTheResponse() {
    testManagerContext
        .getScenarioContext()
        .setContext(ApiContext.RESPONSE_BODY, httpResponseManager.getResponse().getBody().asString());

  }

  @And("I save the initial response for {string}")
  public void iSaveTheInitialResponseInSystem(String api) throws IOException {

    System.out.println("The Data is "+httpResponseManager.getResponse().asString());
    String response = httpResponseManager.getResponse().asString();
    BufferedWriter writer = new BufferedWriter(new FileWriter(API_RESPONSE_PATH+api+".json"));
    writer.write(response);

    writer.close();
  }

    @And("I save the initial response")
    public void iSaveTheInitialResponse() {
      testManagerContext
              .getScenarioContext()
              .setContext(ApiContext.INITIAL_RESPONSE_BODY, httpResponseManager.getResponse().asString());
    }
}
