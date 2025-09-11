package com.testx.web.api.selenium.restassured.qe.util.cucumberreport;

import com.testx.web.api.selenium.restassured.qe.ui.config.ConfigurationManager;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.Reportable;
import net.masterthought.cucumber.reducers.ReducingMethod;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GenerateReport {
    private static final Logger log = LoggerFactory.getLogger(GenerateReport.class);
    private File reportOutputDirectory = new File("target");
    private String inputDirectoryPath = System.getProperty("user.dir") + "/target/cucumber-report";
    private String fileNamesOrRegexWithCommaSeperated = "*.json";
    private List<String> jsonFiles = new ArrayList<>();
    private Configuration configuration;
    private static final String path = System.getProperty("user.dir")+"/src/test/resources/config.properties";
    private static Properties prop;

    private static final String MESSAGE = "------------------------------------------------\n UNABLE TO GENERATE REPORT \n NO SCENARIO HAS BEEN RUN \n PLEASE CHECK SERVICES ARE UP AND URL ACCESSIBLE \n------------------------------------------------";
    private void setConfiguration() {
        configuration = new Configuration(reportOutputDirectory, "Project");
        configuration.addReducingMethod(ReducingMethod.MERGE_FEATURES_WITH_RETEST);
        configuration.addClassifications("URL", ConfigurationManager.getConfiguration().url());
        configuration.addClassifications("Browser", ConfigurationManager.getConfiguration().browser());
        configuration.addClassifications("Headless", ConfigurationManager.getConfiguration().headless().toString());
    }

    public void generateReport() {
        try {
            prop = new Properties();
            prop.load(new FileReader(path));
        } catch (IOException e) {
            throw new RuntimeException(MESSAGE);
        }
        addJsonFiles(fileNamesOrRegexWithCommaSeperated);
        removeEmptyJson();
        setConfiguration();
        ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
        Reportable result = reportBuilder.generateReports();
        // and here validate 'result' to decide what to do if report has failed
    }

    private List<String> getJsonFiles(File targetDirectory, String[] fileRegex) {
        if (ArrayUtils.isEmpty(fileRegex)) {
            return Collections.emptyList();
        } else {
            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setIncludes(fileRegex);
            scanner.setBasedir(targetDirectory);
            scanner.scan();
            String[] files = scanner.getIncludedFiles();
            return fullPathToFiles(files, targetDirectory);
        }
    }

    private List<String> fullPathToFiles(String[] files, File targetDirectory) {
        List<String> fullPathList = new ArrayList<>();
        for (String file : files) {
            fullPathList.add(new File(targetDirectory, file).getAbsolutePath());
        }
        return fullPathList;
    }

    private String[] getRegexStrings(String fileNameOrRegex) {
        if ((fileNameOrRegex.split(",")).length == 1) {
            return new String[] {fileNameOrRegex};
        } else {
            return fileNameOrRegex.split(",");
        }
    }

    private void addJsonFiles(String fileNameOrRegex) {
        List<String> jsonPaths = getJsonFiles(new File(inputDirectoryPath), getRegexStrings(fileNameOrRegex));
        for (String path : jsonPaths) {
            jsonFiles.add(path);
        }
    }

    private void removeEmptyJson(){
        Iterator<String> iterator = jsonFiles.listIterator();
        while (iterator.hasNext()){
            String filePath = iterator.next();
            File file = new File(filePath);
            if(file.length()==0){
                iterator.remove();
            }
        }
        if(jsonFiles.size()==0){
            throw new RuntimeException(MESSAGE);
        }
    }

    public void addScreenshotsToReport() {
        File reportDir = new File("target/cucumber-html-reports");
        File[] reportFiles = reportDir.listFiles((dir, name) -> name.endsWith(".html"));

        if (reportFiles != null) {
            for (File file : reportFiles) {
                try {
                    Document doc = Jsoup.parse(file, "UTF-8");
                    for (Element scenario : doc.select(".scenario")) {
                        String scenarioName = scenario.select("a").text();
                        File screenshot = new File("target/cucumber-report/screenshots/" + scenarioName + ".png");
                        if (screenshot.exists()) {
                            Element img = new Element("img");
                            img.attr("src", "screenshots/" + scenarioName + ".png");
                            img.attr("width", "5");
                            img.attr("height", "5");
                            scenario.appendChild(img);
                        }
                    }
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(doc.outerHtml());
                    }
                } catch (IOException e) {
                    // e.printStackTrace();
                }
            }
        }
    }
}
