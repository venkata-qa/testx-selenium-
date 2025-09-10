
package com.testx.web.api.selenium.restassured.qe.common_interface;

import com.google.gson.JsonArray;
import com.testx.web.api.selenium.restassured.qe.common_interface.chain.RuleManagerCommand;
import com.testx.web.api.selenium.restassured.qe.common_interface.config_contols.Constants;
import com.testx.web.api.selenium.restassured.qe.util.ResourceReaderUtil;


import java.util.HashMap;
import java.util.List;


public class Demo {


    public static void main(String[] args) {
        RuleManagerCommand ruleManagerCommand=new RuleManagerCommand();
        JsonArray input = ResourceReaderUtil.getResource();
        HashMap<String, List<String>> output=new HashMap<>();
        ruleManagerCommand.getAndProcessData(input,output);
        System.out.println(output.get(Constants.COUNTRY));
        System.out.println(output.get(Constants.PLAYER_NAME));
        System.out.println(output.get(Constants.COUNTRY).size());
        System.out.println(output.get(Constants.PLAYER_NAME).size());
    }

}
