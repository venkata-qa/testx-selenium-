
package com.testx.web.api.selenium.restassured.qe.common_interface.chain;

import com.google.gson.JsonArray;

import java.util.HashMap;
import java.util.List;

public interface ITransformData
{

    public void getAndProcessData(JsonArray input ,
                                  HashMap<String, List<String>> output);

}
