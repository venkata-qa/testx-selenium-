package com.testx.web.api.selenium.restassured.qe.util.cucumberreport;


import org.testng.annotations.Test;

import java.io.File;

public class CreateReport {

    @Test
    public static void createReport() {
        GenerateReport report= new GenerateReport();
        report.generateReport();
        report.addScreenshotsToReport();
    }
}
