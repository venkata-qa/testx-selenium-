
package com.testx.web.api.selenium.restassured.qe.common_interface.control_rules;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.testx.web.api.selenium.restassured.qe.common_interface.chain.TransformationRuleCommand;
import com.testx.web.api.selenium.restassured.qe.common_interface.config_contols.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayersCountry extends TransformationRuleCommand
{
    @Override
    public void getAndProcessData(JsonArray input , HashMap<String, List<String>> output)
    {

        List<String> playerCountry=new ArrayList<>();
        for(JsonElement jsonElement:input)
        {
            JsonObject profile = jsonElement.getAsJsonObject().getAsJsonObject(Constants.PROFILE);
            String playerRole = profile.get(Constants.COUNTRY).getAsString();
            playerCountry.add(playerRole);
        }
        output.put(Constants.COUNTRY,playerCountry);
    }
}
