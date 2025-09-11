package xray.xrayhelper;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.testx.web.api.selenium.restassured.qe.ui.config.ConfigurationManager;
import net.minidev.json.JSONArray;
import org.assertj.core.api.Fail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xray.XrayHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

/**
 * Contains a number of support functions for the Xray Utility
 * @author Rahul Jain
 */
public class Model {

    protected static DocumentContext documentContext;
    public static final Logger logger = LoggerFactory.getLogger(XrayHelper.class);

    protected static String jiraURL = "https://eu.xray.cloud.getxray.app";
    protected String confluenceUrl = "ABC";

    protected List<Integer> matchedLines;
    public LinkedHashMap<String, String> map;

    protected String testPlanPath = "/data/testPlanInfo.json";
    protected HashMap<String, List<String>> scenarioToRequirement = new HashMap<>();

    List<String> skipCharacters = Arrays.asList(new String[]{"X", "", "x"});

    //////       GENERAL SETTERS AND GETTERS     ///////

    public DocumentContext getDocumentContext() {
        return this.documentContext;
    }

    public void setDocumentContext(DocumentContext documentContext) {
        this.documentContext = documentContext;
    }

    /**
     * @param templatePath sets a JSON DocumentContext in the session
     */
    protected void setTemplate(String templatePath) {
        try {
            setDocumentContext(JsonPath.parse(getClass().getResource(templatePath)));
        } catch (Exception e) {
            logger.error("Error reading json {}", e);
        }
    }

    //////      SET EXECUTION DETAILS     ///////

    /**
     * A function for setting a JSON key with a particular value
     * @param path the JSON path of the key to set
     * @param value the value to set the JSON path with
     */
    protected void setJsonPathValue(String path, Object value) {
        getDocumentContext().set(path, value);
    }

    protected void removeJsonPath(String path) {
        getDocumentContext().delete(path);
    }

    /**
     * A function to set values in the JSON template for the Xray import
     * @param project the JIRA project key
     * @param testPlan the Xray test plan to associate
     * @param executionSummary a summary for the execution ticket
     * @param executionDescription a description for the execution ticket
     * @param executionFixVersion the JIRA fixVersion to associate to the ticket
     * @param environment the environment the execution has against
     * @param component a JIRA component to associate with the execution
     */
    protected void setTestExecutionDetails(String project, String testPlan, String executionSummary, String executionDescription, String executionFixVersion, String environment, String component, String revision, String label) {
        setTemplate(testPlanPath);
        setJsonPathValue("fields.project.key", project);
        if (!skipCharacters.contains(testPlan)) {
            setJsonPathValue("fields.customfield_19038[0]", testPlan);
        } else {
            removeJsonPath("fields.customfield_19038");
        }
        if (!skipCharacters.contains(revision)) {
            setJsonPathValue("fields.customfield_19030", revision);
        } else {
            removeJsonPath("fields.customfield_19030");
        }
        setJsonPathValue("fields.summary", executionSummary);
        setJsonPathValue("fields.description", executionDescription);
        if (!skipCharacters.contains(executionFixVersion)) {
            setJsonPathValue("fields.fixVersions[0].name", executionFixVersion);
        } else {
            removeJsonPath("fields.fixVersions");
        }
        if (!skipCharacters.contains(environment)) {
            setJsonPathValue("fields.customfield_19036[0]", environment);
            setJsonPathValue("fields.environment", environment);
        } else {
            removeJsonPath("fields.customfield_19036");
            removeJsonPath("fields.environment");
        }
        if (!skipCharacters.contains(component)) {

            JSONArray componentArray = new JSONArray();

            List<String> componentList = Arrays.asList(component.split(","));

            for (int i = 0; i < componentList.size(); i++) {
                LinkedHashMap componentItem = new LinkedHashMap();
                componentItem.put("name", componentList.get(i));
                componentArray.add(componentItem);
            }

            setJsonPathValue("fields.components", componentArray);
        } else {
            removeJsonPath("fields.components");
        }

        if (!skipCharacters.contains(label)) {
            JSONArray labelArray = new JSONArray();
            List<String> labels = Arrays.asList(label.split(","));
            labelArray.addAll(labels);
            setJsonPathValue("fields.labels", labelArray);
        } else {
            removeJsonPath("fields.labels");
        }
    }

    //////      UTILITIES TO ADD TAGS TO SCENARIOS     ///////

    /**
     * This function adds tags to the .feature file being imported with the JIRA key for the created scenarios in Xray
     * @param featureFilePath the location of the imported .feature file
     * @param xrayOutputFile the Xray output file to process from a .feature file import
     * @return a list of scenarios tags are added for
     * @throws IOException where file cannot be found
     */
    public List<String> addTagsToScenarios_Cloud(String featureFilePath, String xrayOutputFile) throws IOException {

        List<Integer> ScenarioLinenumbers = getLineNumbersWithWord(featureFilePath, "Scenario");
        JSONArray jsonArray = getJsonArrayFileData(xrayOutputFile);
        List<String> scenarios = new ArrayList<>();

        map = (LinkedHashMap) jsonArray.get(0);
        map.get("key");

        logger.info("KEY IS "+ map.get("key"));

        Boolean compareOutPutSizeWithScenarioSize = jsonArray.size() == ScenarioLinenumbers.size();
        if (compareOutPutSizeWithScenarioSize) {

            for (int i = 0; i < ScenarioLinenumbers.size(); i++) {

                Path path = Paths.get(featureFilePath);
                List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                map = (LinkedHashMap) jsonArray.get(i);
                scenarios.add(map.get("key").trim());

                int position = ScenarioLinenumbers.get(i) - 2;

                if (!lines.get(position).contains("@" + map.get("key").trim())) {

                    String extraLine = "@" + map.get("key").trim() + " " + lines.get(position).trim();
                    lines.set(position, extraLine);
                    Files.write(path, lines, StandardCharsets.UTF_8);
                }

                if (lines.get(position).contains("@R_")) {
                    List<String> requirements = new LinkedList<>(Arrays.asList(lines.get(position).split("@")));
                    logger.info("Number of tags " + requirements.size());

                    for (int j = 0; j < requirements.size(); j++) {
                        if (!requirements.get(j).startsWith("R_")) {
                            logger.info("removing" + requirements.get(j));
                            requirements.remove(requirements.get(j));
                        }
                    }

                    logger.info("Number of tags " + requirements.size());
                    List<String> requirements2 = new ArrayList<>();
                    for (int x = 0; x < requirements.size(); x++) {
                        if (requirements.get(x).startsWith("R_")) {
                            logger.info(requirements.get(x).replace("R_", ""));
                            requirements2.add(requirements.get(x).replace("R_", ""));
                        }

                    }

                    logger.info("KEY IS 2 "+ map.get("key"));


                    scenarioToRequirement.put(map.get("key"),requirements2);

                }
            }
        } else {

            logger.info("no of scenarios " + ScenarioLinenumbers.size()
                    + " doesn't match with no.of issues returned " + jsonArray.size() + "please resolve issues");
        }

        logger.info("Requirements to link " + scenarioToRequirement.size());

        return scenarios;
    }

    public List<String> addTagsToScenarios(String featureFilePath, String xrayOutputFile) throws IOException {
        List<Integer> ScenarioLinenumbers = getLineNumbersWithWord(featureFilePath, "Scenario");
        JSONArray jsonArray = getJsonArrayFileData(xrayOutputFile);
        List<String> scenarios = new ArrayList<>();
        map = (LinkedHashMap) jsonArray.get(0);
        map.get("key");
        logger.info("KEY IS " + map.get("key"));
        Boolean compareOutPutSizeWithScenarioSize = jsonArray.size() == ScenarioLinenumbers.size();
        if (compareOutPutSizeWithScenarioSize) {
            for (int i = 0; i < ScenarioLinenumbers.size(); i++) {
                Path path = Paths.get(featureFilePath);
                List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                map = (LinkedHashMap) jsonArray.get(i);
                scenarios.add(map.get("key").trim());
                int position = ScenarioLinenumbers.get(i) - 2;
                if (!lines.get(position).contains("@" + map.get("key").trim())) {
                    String extraLine = "@" + map.get("key").trim() + " " + lines.get(position).trim();
                    lines.set(position, extraLine);
                    Files.write(path, lines, StandardCharsets.UTF_8);
                }
                if (lines.get(position).contains("@R_")) {
                    List<String> requirements = new LinkedList<>(Arrays.asList(lines.get(position).split("@")));
                    logger.info("Number of tags " + requirements.size());
                    for (int j = 0; j < requirements.size(); j++) {
                        if (!requirements.get(j).startsWith("R_")) {
                            logger.info("removing" + requirements.get(j));
                            requirements.remove(requirements.get(j));
                        }
                    }
                    logger.info("Number of tags " + requirements.size());
                    List<String> requirements2 = new ArrayList<>();
                    for (int x = 0; x < requirements.size(); x++) {
                        if (requirements.get(x).startsWith("R_")) {
                            logger.info(requirements.get(x).replace("R_", ""));
                            requirements2.add(requirements.get(x).replace("R_", ""));
                        }
                    }
                    logger.info("KEY IS 2 " + map.get("key"));
                    scenarioToRequirement.put(map.get("key"), requirements2);
                }
            }
        } else {
            logger.info("no of scenarios " + ScenarioLinenumbers.size() + " doesn't match with no.of issues returned " + jsonArray.size() + "please resolve issues");
        }
        logger.info("Requirements to link " + scenarioToRequirement.size());
        return scenarios;
    }

    /**
     * A function for searching for strings
     * @param source the path of the source file
     * @param destination the path to write the target file
     * @param searchStrings the Strings to search for
     */
    public static void copyScenarioLinesToTempFile(final String source, String destination, final String... searchStrings) {

        Path src = Paths.get(source);
        Path dst = Paths.get(destination);

        File file = new File(destination);
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            // e.printStackTrace();
        }

        final Predicate<String> predicate
                = s -> Arrays.stream(searchStrings).anyMatch(s::contains);

        try (

                final Stream<String> lines = Files.lines(src, StandardCharsets.UTF_8);
                final BufferedWriter writer = Files.newBufferedWriter(dst, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE_NEW);
        ) {
            lines.filter(predicate).forEach(line -> {
                try {
                    writer.write(line);
                    writer.newLine();

                } catch (IOException e) {
                    // e.printStackTrace();
                }

            });
            writer.flush();
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    /**
     * A function for checking number of occurances of a given word
     * @param filePath the location of the file
     * @param word a word to search for
     * @return an integer list of matches
     * @throws IOException where file cannot be found
     */
    public List<Integer> getLineNumbersWithWord(String filePath, String word) throws IOException {

        matchedLines = new ArrayList<>();
        final List<String> lines1 = Files.readAllLines(Paths.get(filePath));
        IntStream.rangeClosed(0, lines1.size() - 1).forEach(f -> {
            if (lines1.get(f).trim().startsWith(word)) {
                matchedLines.add(++f);
            }
        });

        return matchedLines;
    }

    /**
     * A function to check the contents of the imported .feature file and fail if there are duplicate scenarios
     * @param tempFilePath the location of the file to read
     */
    public void failTestIfDuplicateScenarios(String tempFilePath) {

        File file = new File(tempFilePath);
        Boolean flag = true;

        Map<String, Long> map = new HashMap<>();
        Map<String, Long> longLines = new HashMap<>();
        Scanner read = null;
        try {
            read = new Scanner(file);
        } catch (FileNotFoundException e) {
            // Exception handled silently
        }
        while (read.hasNext()) {
            String line = read.nextLine();
            if (map.containsKey(line)) {
                map.put(line, map.get(line).longValue() + 1);
            } else {
                map.put(line, 1L);
            }
            if (line.length()>255) {
                longLines.put(line, map.get(line).longValue() + 1);
            }
        }

        for (Map.Entry<String, Long> entry : map.entrySet()) {

            if (entry.getValue() > 1) {
                logger.error(entry.getKey() + ": is repeated " + (entry.getValue() - 1) + " times");
                flag = false;

            }

            if (!flag) {
                Fail.fail("There are repeated Scenarios ");
            }
        }

        if (longLines.size()>0) {
            for (Map.Entry<String, Long> entry : longLines.entrySet()) {
                logger.error(entry.getKey() + ": is longer than 255 characters");
            }
            Fail.fail("There are lines longer than 255 characters");
        }
        else {
            logger.info("All tests of required length");
        }
    }

    //////      other Utils     ///////

    /**
     * A function for getting file data
     * @param filePath location of the file to process
     * @return a JSONArray
     * @throws IOException where file cannot be found
     */
    public JSONArray getJsonArrayFileData_Cloud(String filePath) throws IOException {
        String isOnPrem = ConfigurationManager.getConfiguration().isOnPrem();

        final String json = new String(Files.readAllBytes(Paths.get(filePath)));
        DocumentContext documentContext = JsonPath.parse(json);
        JSONArray jsonArray = null;
        LinkedHashMap<String,JSONArray> tester = null;
        try {
            tester = documentContext.read("$");
        }
        catch (ClassCastException e) {
            jsonArray = documentContext.read("$.testIssues");
        }
        return tester.get("updatedOrCreatedTests");

    }
    public JSONArray getJsonArrayFileData(String filePath) throws IOException {
        final String json = new String(Files.readAllBytes(Paths.get(filePath)));
        DocumentContext documentContext = JsonPath.parse(json);
        JSONArray jsonArray = null;
        try {
            jsonArray = documentContext.read("$");
        } catch (java.lang.ClassCastException e) {
            jsonArray = documentContext.read("$.testIssues");
        }
        return jsonArray;
    }
    /**
     * A generic function for unzipping a zip file
     * @param destinationDir a direction to unzip to
     * @param zipEntry the location of the file to unzip
     * @return a file
     * @throws IOException where the file cannot be found
     */
    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        logger.info(destDirPath + File.separator);
        logger.info(destFilePath);
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    /**
     * A generic function for creating a folder
     * @param folderName a name for the folder to create
     */
    public void createFolder(String folderName) {
        new File(folderName).mkdirs();
    }

}

