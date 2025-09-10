package com.testx.web.api.selenium.restassured.qe.api.customexceptions;

public class CustomException extends RuntimeException {

    public CustomException(Throwable cause) {
        super(cause);
    }
    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomException(String message) {
        super(message);
    }
}
