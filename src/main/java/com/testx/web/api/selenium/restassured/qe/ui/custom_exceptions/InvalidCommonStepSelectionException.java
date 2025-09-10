package com.testx.web.api.selenium.restassured.qe.ui.custom_exceptions;

public class InvalidCommonStepSelectionException extends IllegalArgumentException{

    public InvalidCommonStepSelectionException(String stepType) {
        super(String.format("Cucumber common step selection: %s is not supported. Use either partially or exactly", stepType));
    }
}
