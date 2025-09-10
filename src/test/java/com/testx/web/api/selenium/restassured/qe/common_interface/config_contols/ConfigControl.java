
package com.testx.web.api.selenium.restassured.qe.common_interface.config_contols;


import com.testx.web.api.selenium.restassured.qe.common_interface.chain.ITransformData;
import com.testx.web.api.selenium.restassured.qe.common_interface.chain.RuleManagerCommand;
import com.testx.web.api.selenium.restassured.qe.common_interface.control_rules.PlayerName;
import com.testx.web.api.selenium.restassured.qe.common_interface.control_rules.PlayersCountry;


public class ConfigControl {


  public   RuleManagerCommand getProcessTransformationRules()
    {
        RuleManagerCommand ruleManagerCommand=new RuleManagerCommand();
        ruleManagerCommand.appendToRuleChain(getPlayerNameRule());
        ruleManagerCommand.appendToRuleChain(getPlayerRoleRule());
        return ruleManagerCommand;

    }

    ITransformData getPlayerNameRule()
    {
        return new PlayerName();
    }

    ITransformData getPlayerRoleRule()
    {
        return new PlayersCountry();
    }

}
