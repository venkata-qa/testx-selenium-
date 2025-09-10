
package com.testx.web.api.selenium.restassured.qe.api.stepdefs;

import com.testx.web.api.selenium.restassured.qe.api.httpservicemanager.RestRequestManager;

import com.testx.web.api.selenium.restassured.qe.util.MailBoxReader;
import io.cucumber.java.en.Given;
import lombok.extern.log4j.Log4j;

@Log4j
public class MailBoxStepDefs {

  public RestRequestManager restRequestManager;
  TestManagerContext testManagerContext;

  public MailBoxStepDefs(TestManagerContext context) {
    testManagerContext = context;
    restRequestManager = testManagerContext.getRestRequest();
  }

  @Given("I read mailbox of configured account")
  public void iHaveAPI()  {
    try {
      MailBoxReader.readMail();
    }
    catch (Exception exception)
    {
      log.info(exception.getMessage());
    }

  }

}
