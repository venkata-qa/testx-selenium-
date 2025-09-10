

package com.testx.web.api.selenium.restassured.qe.common_interface.chain;

import com.google.gson.JsonArray;
import com.testx.web.api.selenium.restassured.qe.common_interface.config_contols.ConfigControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RuleManagerCommand extends TransformationRuleCommand {
    private static List<ITransformData> processTransformationRules = new ArrayList<>();

    static {
        ConfigControl configControl = new ConfigControl();
        processTransformationRules = configControl.getProcessTransformationRules().getProcessTransformationRules();
    }

    public void appendToRuleChain(ITransformData iTransformData) {
        processTransformationRules.add(iTransformData);
    }

    public void setRuleChain(List<ITransformData> ruleChain) {
        processTransformationRules = ruleChain;
    }

    public List<ITransformData> getProcessTransformationRules() {
        return processTransformationRules;
    }

    @Override
    public void getAndProcessData(JsonArray input, HashMap<String, List<String>> output) {
        for (ITransformData iTransformData : processTransformationRules) {
            iTransformData.getAndProcessData(input, output);
        }
    }


}

