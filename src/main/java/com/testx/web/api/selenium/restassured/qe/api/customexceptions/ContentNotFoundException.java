package com.testx.web.api.selenium.restassured.qe.api.customexceptions;

public class ContentNotFoundException extends Exception {

    public ContentNotFoundException(){
        super();
    }

    public ContentNotFoundException(String message) {
        super(message);
    }
}
