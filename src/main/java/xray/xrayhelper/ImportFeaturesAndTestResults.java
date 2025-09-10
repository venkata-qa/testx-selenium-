package xray.xrayhelper;

import ch.qos.logback.classic.LoggerContext;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.testx.web.api.selenium.restassured.qe.ui.config.ConfigurationManager;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;
import org.apache.http.params.CoreConnectionPNames;
import org.slf4j.Logger;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static io.restassured.RestAssured.given;

/**
 * Helper class providing all the functionality for integration with JIRA
 * @author Rahul Jain
 */
public class ImportFeaturesAndTestResults extends Model {

    String output = null;
    String xrayExecutionDetailsFile;
    String xrayJsonResultsFilePath;
    List<String> skipCharacters = Arrays.asList(new String[]{"X", "", "x"});
    List<String> scenarios = new ArrayList<>();
    String testPlanId = "";
    String testSetId = "";

        static String tokenEndPoint = null;
        static String importFeatureEndPoint = null;
        static String importResultsMultipartEndpoint = null;
        static String importResultsEndpoint = null;
    static String exportEndpoint = "/rest/raven/1.0/export/test";
    static String endpoint = "/rest/api/2/issue/%s";
    static String endpointIssueLink = "/rest/api/2/issueLink/%s";
    static String endpointValue = "/rest/api/2/issue/%s/attachments";
    static String endpointComment = "/rest/api/2/issue/%s/comment";
    static String baseEndpoint = "/rest/api/2/issue/";

    static String endpointLatestExecution = "/rest/api/2/search?jql=" + "project=%s AND component='%s' AND issueType='Test Execution' order by created DESC";
    static String endpointSearchJiraIssue = "/rest/api/2/search?maxResults=500&jql=%s";
    static String endpointFolder = "/rest/raven/1.0/api/testrepository/%s/folders";
    static String endpointCreateFolder = "/rest/raven/1.0/api/testrepository/%s/folders/";
    static String endpointRemoveTestToFolder = "/rest/raven/1.0/api/testrepository/%s/folders/%s/tests";
    static String endpointTestPlan = "/rest/raven/1.0/api/testplan/%s/test";
    static String endpointExecution = "/rest/raven/1.0/api/testexec/%s/test";


    static{
        boolean isOnPrem = Boolean.parseBoolean(isOnPremise());
            tokenEndPoint = "/api/v2/authenticate";
            importFeatureEndPoint = isOnPrem?"/rest/raven/1.0/import/feature":"/api/v2/import/feature";
            importResultsMultipartEndpoint = isOnPrem?"/rest/raven/1.0/import/execution/cucumber/multipart":"/api/v2/import/execution/cucumber/multipart";
            importResultsEndpoint = isOnPrem?"/rest/raven/1.0/import/execution/cucumber":"/api/v2/import/execution/cucumber";
        }

        private static String isOnPremise(){
            return ConfigurationManager.getConfiguration().isOnPrem();
        }

    //TODO-Chandan Done
    public String createTokenWithAPIKey(String clientId, String secret) {
        String onPremise = isOnPremise();
        String tokenValue = null;
        if (onPremise.equalsIgnoreCase("false")) {
            String inputData = "{\n"
                    + "    \"client_id\": \"" + clientId + "\",\n"
                    + "    \"client_secret\": \"" + secret + "\"\n"
                    + "    }";
            Response response = given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                    .redirects().follow(false)
                    .headers(getHeaders("", "application/json"))
                    .body(inputData)
                    .post(jiraURL + tokenEndPoint);
            String token = response.prettyPrint();
            tokenValue = token.substring(1, token.length() - 1);
        } else {
            tokenValue = ConfigurationManager.getConfiguration().jiraToken();
        }
        return tokenValue;
    }

    /**
     * This function is for importing a Cucumber .feature file into Xray via the Xray REST API
     * A number of additional functions can be executed post import by populating optional parameters. This functions use the the JIRA REST API.
     * This function will loop each feature file specified and write a JSON output file.
     * The imported feature files will be updated with the JIRA keys of the respective imported tests into annotations
     * @param token the JIRA token for authorization (mandatory)

     * @param path2FeatureFile the relative path to where the feature file(s) are located. Can be a directory, single file or comma separated list of files (mandatory)
     * @param projectKey the JIRA project key (e.g. SCA) (mandatory)
     * @param parentKey the JIRA issue id for the User Story (requirement) to link the imported tests to. (optional - enter "" to skip this)
     * @param component the JIRA component to associate the imported tests with (optional - enter "" to skip this)
     * @param fixVersion the JIRA fixVersion to associate the imported tests with (optional - enter "" to skip this)
     * @param targetStatus the JIRA transition id to execute on the imported tests (e.g. 21 to move to "In Use") (optional - enter "" to skip this)
     * @param repoPath the Xray test repository path to file the imported tests in. The path must already exist. (optional - enter "" to skip this)
     */


    //TODO-Chandan
    public void importFeatureJIRA(String token, String path2FeatureFile, String projectKey, String parentKey, String component, String fixVersion, String targetStatus, String repoPath, String testPlan, String testSet) {
        String isOnPrem = isOnPremise();

        createFolder("target");
        String tempFilePath = "target/temp";
        String xrayOutputFile;

        String[] fileList = path2FeatureFile.split(",");

        for(int i = 0; i< fileList.length; i++){

            if (new File(fileList[i]).isDirectory()) {
                logger.info("Path " + path2FeatureFile + " is a directory. Uploading all files.");
                File[] directoryListing = new File(fileList[i]).listFiles();
                int y = 0;
                for (File child : directoryListing) {
                    xrayOutputFile = "target/xrayOutPut"+i+"-"+y+".yml";
                    try {
                        processFeatureFile(token, child.getPath(), projectKey, tempFilePath, xrayOutputFile);
                        processPostImportUpdates(projectKey, parentKey, component, targetStatus, fixVersion, repoPath, testPlan, testSet, token);
                    }
                    catch (Exception e) {
                        logger.info("Unable to process feature file " + child.getPath());
                        logger.error(e.getStackTrace().toString());
                        logger.error(e.getMessage());
                    }
                    y++;
                }
            }

            else {
                xrayOutputFile = "target/xrayOutPut"+i+".yml";
                try {
                    processFeatureFile(token, fileList[i], projectKey, tempFilePath, xrayOutputFile);
                    processPostImportUpdates(projectKey, parentKey, component, targetStatus, fixVersion, repoPath, testPlan, testSet, token);
                }
                catch (Exception e) {
                    logger.info("Unable to process feature file " + fileList[i]);
                    logger.error(e.getMessage());
                }
            }
        }
    }

    /**
     * This private function is called within a loop from the main Cucumber .feature file import and imports a single .feature file
     * The JSON response of the REST API call is written to the specified location.
     * @param token the JIRA token for authorization
     * @param path2FeatureFile the relative path to where the feature file being imported is located.
     * @param projectKey the JIRA project key (e.g. SCA)
     * @param tempFilePath a path to write the temporary out
     * @param xrayOutputFile the relative path and filename to write the JSON output from the REST API call
     */
    //TODO-Chandan done
    private void processFeatureFile(String token, String path2FeatureFile, String projectKey, String tempFilePath, String xrayOutputFile) {

        logger.info("Processing feature file : " + path2FeatureFile);
        //String importFeatureEndPoint = "/api/v2/import/feature";
        copyScenarioLinesToTempFile(path2FeatureFile, tempFilePath, "Scenario:", "Scenario Outline:");
        failTestIfDuplicateScenarios(tempFilePath);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("projectKey", projectKey);

        Response response = given()
                .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                .redirects().follow(false)
                .headers(getHeaders(token, "multipart/form-data"))
                .multiPart("file", new File(path2FeatureFile))
                .queryParams(queryParams)
//                .auth().basic(jiraUsername,jiraPassword)
                .post(jiraURL + importFeatureEndPoint);

        try {

            FileWriter file = new FileWriter(xrayOutputFile);
            file.write(response.prettyPrint());
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //        List<String> scenarios = new ArrayList<>();

        try {
            logger.info("\ninfo: Starting process that imports features\n");

            BufferedReader br = new BufferedReader(new FileReader(xrayOutputFile));
            String line = null;
            while ((line = br.readLine()) != null) {
                logger.info(line);
            }

            scenarios = addTagsToScenarios(path2FeatureFile, xrayOutputFile);
        } catch (Exception e) {
            logger.error("\nerror: Tried to add issue tags to scenarios, something went wrong\n");

            e.printStackTrace();
        }

        logger.info("Processed feature file : " + path2FeatureFile + " Output in " + xrayOutputFile);
    }

    /**
     * @param parentKey the JIRA issue id for the User Story (requirement) to link the imported tests to. (optional - enter "" to skip this)
     * @param component the JIRA component to associate the imported tests with (optional - enter "" to skip this)
     * @param fixVersion the JIRA fixVersion to associate the imported tests with (optional - enter "" to skip this)
     * @param targetStatus the JIRA transition id to execute on the imported tests (e.g. 21 to move to "In Use") (optional - enter "" to skip this)
     * @param repoPath the Xray test repository path to file the imported tests in. The path must already exist. (optional - enter "" to skip this)
     * @param token the JIRA token for authorization
     */
    private void processPostImportUpdates(String projectKey, String parentKey, String component, String targetStatus, String fixVersion, String repoPath, String testPlan, String testSet, String token) {

        if (!skipCharacters.contains(parentKey) || !skipCharacters.contains(component) || !skipCharacters.contains(targetStatus) || !skipCharacters.contains(fixVersion) || !skipCharacters.contains(repoPath) || !skipCharacters.contains(testPlan) || !skipCharacters.contains(testSet)) {

            Iterator<String> iterator = scenarios.iterator();
            while (iterator.hasNext()) {
                String issueKey = iterator.next();

                logger.info(issueKey);

                if (!skipCharacters.contains(component) || !skipCharacters.contains(fixVersion)  || !skipCharacters.contains(repoPath)  || !skipCharacters.contains(testPlan)  || !skipCharacters.contains(testSet))  {
                    logger.info("Updating issue");
                    updateIssue(token, projectKey, issueKey, component, fixVersion, repoPath, testPlan, testSet);
                }

                if (!skipCharacters.contains(parentKey) && parentKey != null) {

                    if (parentKey.equals("INLINE")) {
                        logger.info("Linking using inline for " + issueKey);

                        if (scenarioToRequirement.get(issueKey)!=null && scenarioToRequirement.size()>0) {

                            List<String> requirements = scenarioToRequirement.get(issueKey);
                            for (int i = 0; i < requirements.size(); i++) {
                                logger.info("XXX" + requirements.get(i));
                                linkIssues(token, issueKey, requirements.get(i).trim(), "Tests");
                                logger.info("DONE LINKING");
                            }
                            logger.info(("DONE LINKING ALL"));
                        }
                    }

                    else {
                        logger.info("Linking issue to parent");
                        try {
                            linkIssues(token, issueKey, parentKey, "Tests");
                        } catch (Exception e) {
                            logger.error("Could not link issues");
                        }
                    }
                }

                if (!skipCharacters.contains(targetStatus) && targetStatus != null) {
                    logger.info("Transitioning issue");
                    try {
                        transitionIssue(token, issueKey, targetStatus);
                    } catch (Exception e) {
                        logger.error("Could not transition issues");
                    }
                }
            }
        }
    }

    public String importJsonReportToJira(String token, String jsonResultsFilePath, String testExecutionInfo){
        Response response = given()
                .redirects().follow(false)
                .headers(getHeaders(token, "multipart/form-data"))
                .multiPart("info", new File(testExecutionInfo))
                .multiPart("results", new File(jsonResultsFilePath))
                .post(jiraURL + importResultsMultipartEndpoint);
       return response.getBody().jsonPath().get("key");

    }
    /**
     * This method imports cucumber results into Xray for a given json result file
     * This allows for a number of additional JIRA fields to be populated in the created Xray execution JIRA ticket
     * @param token the JIRA token for authorization
     * @param jsonResultsFilePath relative path for the json file to import
     * @param projectKey the JIRA project key (e.g. SCA)
     * @param testPlan the JIRA issue key of the Xray Test plan to associate
     * @param executionSummary a summary for the JIRA issue
     * @param executionDescription a description for the JIRA issue
     * @param executionFixVersion the JIRA fixVersion to associate the execution with
     * @param environment the environment against which the execution was run
     * @param component the component to which the execution is associated with
     * @return String imported JIRA ticket id for execution
     */
    public String importJsonReportToJira(String token, String jsonResultsFilePath, String projectKey, String testPlan,
                                       String executionSummary, String executionDescription, String executionFixVersion, String environment, String component, String revision, boolean removeEmbeds, String label
    ) {

        setTestExecutionDetails(projectKey, testPlan, executionSummary, executionDescription, executionFixVersion, environment, component, revision, label);
        createFolder("target");

        String executionDetailsPath = "target/executionDetails.json";

        try (FileWriter file = new FileWriter(executionDetailsPath)) {
            file.write(documentContext.jsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        xrayExecutionDetailsFile = executionDetailsPath;
        xrayJsonResultsFilePath = jsonResultsFilePath;

        if (removeEmbeds) {
            xrayJsonResultsFilePath = deleteEmbeds(xrayJsonResultsFilePath);
        }

        RestAssuredConfig config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, 180000)
                        .setParam(CoreConnectionPNames.SO_TIMEOUT, 180000)).sslConfig(new SSLConfig().relaxedHTTPSValidation());

        int tries = 0;

        while (tries < 1) {

            Response response = given()
                    .config(config)
                    .redirects().follow(false)
                    .headers(getHeaders(token, "multipart/form-data"))
                    .multiPart("info", new File(xrayExecutionDetailsFile))
                    .multiPart("result", new File(xrayJsonResultsFilePath))
                    .post(jiraURL + importResultsMultipartEndpoint);

            if (response.statusCode()==200) {

                DocumentContext defectResponse = JsonPath.parse(response.prettyPrint());
                String executionId = defectResponse.read("$.testExecIssue.key");
                logger.info("EXECUTION=" + executionId);

                try {
                    logger.info("\ninfo: Starting process to import cucumber results\n");
                    BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

                    while ((output = br.readLine()) != null) {
                        logger.info(output);
                    }

                    logger.info("exit: " + response.getStatusCode());

                } catch (Throwable e) {
                    logger.error("\nerror: Something went wrong importing cucumber results\n");
                    e.printStackTrace();
                }

                if (removeEmbeds) {
                    try {
                        File xxx = new File(xrayJsonResultsFilePath);
                        xxx.deleteOnExit();
                    } catch (Exception e) {
                        logger.info("could not delete file");
                    }
                }

                return executionId;

            } else {

                tries++;

                logger.error("\nerror: Something went wrong importing cucumber results\n");
                logger.error("\n If request timedout while trying to import larger results, it well may be the case execution ticket was still created. Please refer test plan if it has execution ticket. \n");
                logger.error(response.then().log().toString());
                logger.error(response.then().log().all().toString());
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
            }
        }

        return "N/A";
    }

    /**
     * This method imports cucumber results into Xray for a given json result file
     * @param token the JIRA token for authorization
     * @param jsonResultsFilePath relative path for the json file to import
     * @param executionDetailsFilePath relative path to the template for the issue creation
     * @return String imported JIRA ticket id for execution
     */
    public String importJsonReportToJira(String token, String jsonResultsFilePath, String executionDetailsFilePath, boolean removeEmbeds) {

        String output = null;
        xrayExecutionDetailsFile = "@" + executionDetailsFilePath;
        xrayJsonResultsFilePath = "@" + jsonResultsFilePath;

        if (removeEmbeds) {
            xrayJsonResultsFilePath = "@" + deleteEmbeds(xrayJsonResultsFilePath);
        }

        Map<String, String> formParams = new HashMap<>();
        formParams.put("result", xrayJsonResultsFilePath);
        formParams.put("info", xrayExecutionDetailsFile);

        RestAssuredConfig config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000)
                        .setParam(CoreConnectionPNames.SO_TIMEOUT, 60000)).sslConfig(new SSLConfig().relaxedHTTPSValidation());

        int tries = 0;

        while (tries < 1) {

            Response response = given()
                    .config(config)
                    .redirects().follow(false)
                    .headers(getHeaders(token, "multipart/form-data"))
                    .formParams(formParams)
                    .post(jiraURL + importResultsMultipartEndpoint);

            if (response.getStatusCode() == 200) {

                DocumentContext defectResponse = JsonPath.parse(response.prettyPrint());
                String executionId = defectResponse.read("$.testExecIssue.key");
                logger.info("EXECUTION=" + executionId);

                try {
                    logger.info("\ninfo: Starting cucumber results file import\n");

                    BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

                    while ((output = br.readLine()) != null) {
                        logger.info(output);
                    }

                    logger.info("exit: " + response.getStatusCode());

                } catch (Throwable e) {
                    logger.error("\nerror: Something went wrong importing cucumber results\n");
                    e.printStackTrace();
                }

                if (removeEmbeds) {
                    new File(xrayJsonResultsFilePath).delete();
                }

                return executionId;
            } else {

                tries++;

                logger.error("\nerror: Something went wrong importing cucumber results\n");
                logger.error(response.then().log().toString());
                logger.error(response.then().log().all().toString());
                System.out.println(response.then().log().all().toString());

                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
            }
        }
        return "N/A";
    }


    /**
     * This method imports cucumber results into Xray for a given json result file into an existing execution key
     * @param token the JIRA token for authorization
     * @param jsonResultsFilePath relative path for the json file to import
     * @param executionId the JIRA key for the JIRA execution to upload results to
     */
    public String importJsonReportToJiraExistingExecution(String token, String jsonResultsFilePath, String executionId, boolean removeEmbeds) {

        if (removeEmbeds) {
            jsonResultsFilePath = deleteEmbeds(jsonResultsFilePath);
        }

        File results = new File(jsonResultsFilePath);

        try {
            setDocumentContext(JsonPath.parse(results));
        } catch (Exception e) {
            logger.error("Error reading json {}", e);
        }

        JSONArray features = getDocumentContext().read("$");
        logger.info("FEATURES"+features.size());

        for (int i = 0 ; i < features.size(); i++) {

            try {
                if (!getDocumentContext().read((("[" + i + "].tags[0].name")), String.class).equals(executionId)) {
                    logger.info("Setting feature " + i);
                    String tagxName = "";
                    String tagline = "";
                    tagxName = getDocumentContext().read("[" + i + "].tags[0].name");
                    tagline = getDocumentContext().read("[" + i + "].tags[0].line");
                    setJsonPathValue("[" + i + "].tags[0].name", "@" + executionId);

                    JSONArray tags = getDocumentContext().read("[" + i + "].tags");
                    LinkedHashMap<String, String> oldTag = new LinkedHashMap<>();
                    oldTag.put("name", tagxName);
                    oldTag.put("line", tagline);
                    tags.add(oldTag);
                    getDocumentContext().set("[" + i + "].tags", tags);

                }
            } catch (Exception e) {
                JSONArray tags = new JSONArray();
                LinkedHashMap<String, String> oldTag = new LinkedHashMap<>();
                oldTag.put("name", "@"+executionId);
                oldTag.put("line", "1");
                tags.add(oldTag);
                getDocumentContext().put("[" + i + "]","tags", tags);
            }
        }

        logger.info(getDocumentContext().jsonString());

        RestAssuredConfig config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000)
                        .setParam(CoreConnectionPNames.SO_TIMEOUT, 60000)).sslConfig(new SSLConfig().relaxedHTTPSValidation());
        int tries = 0;

        while (tries < 1) {

            Response response = given()
                    .config(config)
                    .redirects().follow(false)
                    .headers(getHeaders(token, "application/json"))
                    .body(getDocumentContext().jsonString())
                    .post(jiraURL + importResultsEndpoint);

            if (response.getStatusCode()==200) {

                DocumentContext defectResponse = JsonPath.parse(response.prettyPrint());
                String updatedExecutionId = defectResponse.read("$.testExecIssue.key");
                logger.info("EXECUTION=" + updatedExecutionId);

                try {
                    logger.info("\ninfo: Starting cucumber results file import\n");

                    BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

                    while ((output = br.readLine()) != null) {
                        logger.info(output);
                    }

                    logger.info("exit: " + response.getStatusCode());

                } catch (Throwable e) {
                    logger.error("\nerror: Something went wrong importing cucumber results\n");
                    e.printStackTrace();
                }

                if (removeEmbeds) {
                    new File(xrayJsonResultsFilePath).delete();
                }

                return executionId;

            } else {

                tries++;

                logger.error("\nerror: Something went wrong importing cucumber results\n");
                logger.error(response.then().log().toString());
                logger.error(response.then().log().all().toString());
                System.out.println(response.then().log().all().toString());

                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
            }
        }
        return "N/A";
    }

    /**
     * Returns the headers required for the JIRA REST API call
     * @param token the JIRA token for authorization
     * @param contentType the content type to set for the request
     * @return Headers required for JIRA request including authorization header
     */
    protected Headers getHeaders(String token, String contentType) {

        Set<String> artifactoryLoggers = new HashSet<>(Arrays.asList("org.apache.http", "groovyx.net.http", "io.restassured", "ps.qe.XrayHelper"));
        for(String log:artifactoryLoggers) {
//            ch.qos.logback.classic.Logger artLogger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(log);

            LoggerContext loggerContext = new LoggerContext();
            Logger rootLogger = loggerContext.getLogger(log);
            ((ch.qos.logback.classic.Logger) rootLogger).setLevel(ch.qos.logback.classic.Level.ERROR);
            ((ch.qos.logback.classic.Logger) rootLogger).setAdditive(false);
        }
//        Peronal Access Token is added to jenkins creds - confluence-publisher under password
//        byte[] encodedBytes = Base64.getEncoder().encode((jiraUsername + ":" + jiraPassword).getBytes());
        List<Header> list = new ArrayList<Header>();
        list.add(new Header("Content-Type", contentType));
        list.add(new Header("X-Atlassian-Token", "nocheck"));
//        list.add(new Header("Authorization", "Basic " + new String(encodedBytes)));
//        logger.info("***** Connecting Jira Using Personal Access Token PAT *****");
        list.add(new Header("Authorization", "Bearer "+ token));
        
        return new Headers(list);
    }


    /**
     * This function is for generating .feature files from Xray and exporting to a specified location
     * Xray can return a .zip so this function will un-package the .zip in that scenario
     * @param token the JIRA token for authorization
     * @param testPlanList a list of test plans to export from Xray separated by ;
     * @param outputPath the relative path to output the export the .feature bundle to
     */
    public boolean exportFeatureFiles(String token, String testPlanList, String outputPath, int retries) {

        //String exportEndpoint = "/rest/raven/1.0/export/test";

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("keys", testPlanList);

        int tries = 0;
        boolean downloadSuccessful = false;

        tries_loop:
        while (tries < retries) {

            Response response = given()
                    .config(RestAssured.config().httpClient(HttpClientConfig.httpClientConfig()
                            .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, 360000)
                            .setParam(CoreConnectionPNames.SO_TIMEOUT, 360000)).sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                    .redirects().follow(false)
                    .headers(getHeaders(token, "application/json"))
                    .queryParams(queryParams)
                    .get(jiraURL + exportEndpoint);


            if (response.statusCode() == 200) {

                boolean isZip = false;
                File destDir = new File(outputPath);
                destDir.mkdirs();
                String filename = response.headers().getValue("Content-Disposition");
                if (filename.contains(".zip")) {
                    logger.info("IS ZIP");
                    isZip = true;
                } else {
                    filename = filename.substring(filename.indexOf("filename=\"") + 10, filename.length() - 1);
                    logger.info("filename is : " + filename);
                }

                try {
                    logger.info("\ninfo: Starting feature export process\n");

                    OutputStream outputStream = null;

                    try {

                        if (isZip) {

                            logger.info("Is a ZIP file");

                            // write the inputStream to a FileOutputStream
                            outputStream = new FileOutputStream(new File("features.zip"));
                        } else {

                            logger.info("Is single file");
                            outputStream = new FileOutputStream(new File(outputPath + "/" + filename));
                        }
                        byte[] buffer = new byte[1024];
                        int len;

                        while ((len = response.body().asInputStream().read(buffer)) != -1) {
                            outputStream.write(buffer, 0, len);
                        }

                    } catch (IOException e) {
                        logger.error("ERROR : " + e.getMessage());
                        e.printStackTrace();
                    } finally {

                        if (outputStream != null) {
                            try {
                                outputStream.flush();
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    if (isZip) {
                        logger.info("\ninfo: ZIP file generated, extracting...\n");
                        String fileZip = "features.zip";

                        byte[] buffer = new byte[1024];
                        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
                        ZipEntry zipEntry = zis.getNextEntry();
                        while (zipEntry != null) {
                            File newFile = newFile(destDir, zipEntry);
                            FileOutputStream fos = new FileOutputStream(newFile);
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                            fos.close();
                            zipEntry = zis.getNextEntry();
                        }
                        zis.closeEntry();
                        zis.close();
                    }
                    downloadSuccessful = true;
                    break tries_loop;

                } catch (Throwable e) {
                    logger.error("\nerror: Something went wrong downloading feature files for Test Plan " + testPlanList + "\n");
                    logger.error(e.getMessage());
                } finally {
                    logger.info("Finished exporting plan " + testPlanList);
                }
            } else {

                tries++;

                logger.error("\nerror: Something went wrong downloading feature files for Test Plan " + testPlanList + "\n");
                logger.error(response.then().log().toString());
                logger.error(response.then().log().all().toString());
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                }

            }
        }

        return downloadSuccessful;
    }


    public boolean exportFeatureFilesRename(String token, String testPlanList, String outputPath, int retries, String filename) {
        boolean output = exportFeatureFiles(token, testPlanList, outputPath, retries);
        File dir = new File(outputPath);

        if (dir.isDirectory()) { // make sure it's a directory
            int i = 0;
            for (final File f : dir.listFiles()) {
                try {

                    File newfile = new File(outputPath + filename + ".feature");
                    if (i > 0) {
                        newfile = new File(outputPath + filename + "-"+i+".feature");
                    }
                    if (f.renameTo(newfile)) {
                        System.out.println("Rename successful");
                    } else {
                        System.out.println("Rename failed");
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                i++;
            }
        }

        return output;
    }

    /**
     * This function is for performing updates on an existing JIRA issue to set the component, fixVersion
     * @param token the JIRA token for authorization
     * @param issueKey the issue key to update
     * @param component the component to associate
     * @param fixVersion the fixVersion to associate
     * @param repoPath the repo path to store into
     * @param testPlan the Xray test plan to link to
     * @param testSet the Xray test set to link to
     */

    public void updateIssue(String token, String projectKey, String issueKey, String component, String fixVersion, String repoPath, String testPlan, String testSet) {

        if (testPlan.equals("NEW") && testPlanId.equals("")) {
            logger.info("Creating Test Plan");
            testPlan = createTestPlan(token,projectKey,component,fixVersion);
            testPlanId = testPlan;
        }
        else if (testPlan.equals("NEW")) {
            testPlan = testPlanId;
        }

        if (testSet.equals("NEW") && testSetId.equals("")) {
            logger.info("Creating Test Set");
            testSet = createTestSet(token,projectKey,component,fixVersion);
            testSetId = testSet;
        }
        else if (testSet.equals("NEW")) {
            testSet = testSetId;
        }

        if (!skipCharacters.contains(repoPath)) {
            logger.info("Linking folder");
            if (!repoPath.startsWith("/")) {
                repoPath = "/"+repoPath;
            }
            if (!checkFolderExists(token,projectKey,repoPath)) {
                logger.info("Creating folder");
                createFolder(token,projectKey, repoPath);
            }
        }

        DocumentContext inputBody = JsonPath.parse("{\n"
                + "   \"fields\": {\n"
                + "   }\n"
                + "}");

        if (!skipCharacters.contains(component)) {

            JSONArray components = (JSONArray) getIssueAttribute(token, issueKey, "fields.components");
            System.out.println("COMPONENT SIZE: " + components.size());

            LinkedHashMap<String, String> componentObject = new LinkedHashMap<>();
            componentObject.put("name",component);
            components.add(componentObject);

            System.out.println("COMPONENT SIZE: " + components.size());
            System.out.println(components);

            inputBody.put("fields", "components", components);
        }
        if (!skipCharacters.contains(fixVersion)) {

            JSONArray versions = (JSONArray) getIssueAttribute(token, issueKey, "fields.fixVersions");
            System.out.println("VERSION SIZE: " + versions.size());

            LinkedHashMap<String, String> versionObject = new LinkedHashMap<>();
            versionObject.put("name",fixVersion);
            versions.add(versionObject);

            System.out.println("VERSION SIZE: " + versions.size());
            System.out.println(versions);

            inputBody.put("fields", "fixVersions", versions);
        }
        if (!skipCharacters.contains(testPlan)) {
            try {
                JSONArray plans = (JSONArray) getIssueAttribute(token, issueKey, "fields.customfield_19020");
                System.out.println("PLANS SIZE: " + plans.size());
                plans.add(testPlan);
                System.out.println("PLANS SIZE: " + plans.size());
                System.out.println(plans);
                inputBody.put("fields", "customfield_19020", plans);
            } catch(Exception e) {
                throw e;
            }
        }
        if (!skipCharacters.contains(testSet)) {
            JSONArray sets = (JSONArray)getIssueAttribute(token, issueKey,"fields.customfield_19018");
            sets.add(testSet);
            inputBody.put("fields", "customfield_19018", sets);
        }
        if (!skipCharacters.contains(repoPath)) {
            inputBody.put("fields", "customfield_19022", repoPath);
        }
        logger.info(inputBody.jsonString());

        //String endpoint = "/rest/api/2/issue/" + issueKey;

        Response response = given()
                .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                .redirects().follow(false)
                .headers(getHeaders(token, "application/json"))
                .body(inputBody.jsonString())
                .put(jiraURL + String.format(endpoint,issueKey));

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

            while ((output = br.readLine()) != null) {
                logger.info(output);
            }

            logger.info("exit: " + response.getStatusCode());
        } catch (Throwable e) {
            logger.error("\nerror: Something went wrong updating issue " + issueKey + "\n");
            e.printStackTrace();
        }
    }


    // links two jira issues and any story linked to the original issue

    /**
     * A function for linking two existing JIRA issues
     * Used for creating a relationship between a test and the user story it covers
     * @param token the JIRA token for authorization
     * @param issueId the JIRA id of the Xray test
     * @param outwardIssue the JIRA id of the requirement (e.g. User Story)
     * @param linkName the name of the link type to relate the two issues with
     */
    private void linkIssues(String token, String issueId, String outwardIssue, String linkName) {

        
        
        
        String inputData = "{\n"
                + "   \"update\":{\n"
                + "      \"issuelinks\":[\n"
                + "         {\n"
                + "            \"add\":{\n"
                + "               \"type\":{\n"
                + "                  \"name\":\"" + linkName + "\"\n"
                + "               },\n"
                + "               \"outwardIssue\":{\n"
                + "                  \"key\":\"" + outwardIssue + "\"\n"
                + "               }\n"
                + "            }\n"
                + "         }\n"
                + "      ]\n"
                + "   }\n"
                + "}";

        //String endpoint = "/rest/api/2/issue/" + issueId;

        Response response = given()
                .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                .redirects().follow(false)
                .headers(getHeaders(token, "application/json"))
                .body(inputData)
                .put(jiraURL + endpointValue);

        try {
            logger.info("\ninfo: Linking issue " + issueId + " to outward issue id " + outwardIssue + " using link type " + linkName + "\n");
            BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

            while ((output = br.readLine()) != null) {
                logger.info(output);
            }

            logger.info("exit: " + response.getStatusCode());

        } catch (Throwable e) {
            logger.error("\nerror: Something went wrong linking issue " + issueId + " to outward issue id " + outwardIssue + " using link type " + linkName + "\n");
            e.printStackTrace();
        }
    }

    /**
     * A function for creating issue links in batch
     * @param token the JIRA token for authorization
     * @param issueKey the JIRA issue key to link to
     * @param jql a jql search to locate the issues to link
     * @param linkName the name of the link type to use e.g "Tests"
     * @param inward defines if the link should be inward from the issueKey else outward
     */
    public void addIssueLinks(String token, String issueKey, String jql, String linkName, boolean inward) {

        DocumentContext searchResults = searchForJiraIssues(token, jql);
        JSONArray issues = searchResults.read("$.issues[?(@.key)].key");
        System.out.println(issues.size());
        for (int i = 0; i < issues.size(); i++) {
            if (inward) {
                linkIssues(token, issues.get(i).toString(), issueKey, linkName);
            }
            else {
                linkIssues(token, issueKey, issues.get(i).toString(), linkName);
            }
        }
    }

    /**
     * A function for deleting an issue link
     * @param token the JIRA token for authorization
     * @param linkId the id of the link to remove
     */
    private boolean deleteIssueLink(String token, String linkId) {

        //String endpoint = "/rest/api/2/issueLink/" + linkId;

        Response response = given()
                .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                .redirects().follow(false)
                .headers(getHeaders(token, "application/json"))
                .delete(jiraURL + endpoint);

        int code = response.getStatusCode();

        if (code==204) {
            return true;
        }

        else {
            return false;
        }
    }

    /**
     * A function for removing all issue links of a certain type
     * @param token the JIRA token for authorization
     * @param issueKey the JIRA issue key to remove links from
     * @param linkType the link type to retreive, use X for all links
     * @param issueIds the issue ids to remove from the link list separated by ; use X for all
     */
    public void clearIssueLinks(String token, String issueKey, String linkType, String issueIds) {

        
        
        

        List<String> issues = getIssueLinks(token, issueKey,linkType, issueIds);

        for (int i = 0; i < issues.size(); i++) {
            deleteIssueLink(token, issues.get(i));
        }
    }

    /**
     * A function for transitioning an existing JIRA key by a given transition id
     * Used for putting created Xray scenarios into "In Use" status
     * @param token the JIRA token for authorization
     * @param issueKey the JIRA issue key for the issue to get existing links for
     * @param linkType the link type to retreive, use X for all links
     * @param issueIds the issue ids to remove from the link list separated by ;  use X for all
     */
    public List<String> getIssueLinks(String token, String issueKey, String linkType, String issueIds) {

        List<String> issueLinks = new ArrayList<>();

        //String endpoint = "/rest/api/2/issue/" + issueKey;

        Response response = given()
                .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                .redirects().follow(false)
                .headers(getHeaders(token, "application/json"))
                .get(jiraURL + endpointValue);

        DocumentContext result = JsonPath.parse(response.prettyPrint());
        JSONArray links = result.read("fields.issuelinks");

        for (int i = 0; i < links.size(); i++) {
            LinkedHashMap link = (LinkedHashMap)links.get(i);
            LinkedHashMap type = (LinkedHashMap)link.get("type");

            if (!skipCharacters.contains(issueIds)) {
                LinkedHashMap inwardIssue = (LinkedHashMap)link.get("inwardIssue");

                if (((type.get("name").equals(linkType)) || (skipCharacters.contains(linkType)))&&(issueIds.contains(inwardIssue.get("key").toString()))) {
                    logger.info("Found issue link id : " + link.get("id") + " - " + link);
                    issueLinks.add(link.get("id").toString());
                }
            }

            else {

                if ((type.get("name").equals(linkType)) || (skipCharacters.contains(linkType))) {
                    logger.info("Found issue link id : " + link.get("id") + " - " + link);
                    issueLinks.add(link.get("id").toString());
                }
            }
        }

        return issueLinks;
    }

    /**
     * A function for transitioning an existing JIRA key by a given transition id
     * Used for putting created Xray scenarios into "In Use" status
     * @param token the JIRA token for authorization
     * @param issueKey the JIRA issue key for the issue to transition
     * @param transitionId the transition id to action (e.g. 21 for the transition to "In Use"
     */
    public void transitionIssue(String token, String issueKey, String transitionId) {

        String inputData = "{\n"
                + "    \"transition\": {\n"
                + "        \"id\": \"" + transitionId + "\"\n"
                + "    }\n"
                + "}";

        //String endpoint = "/rest/api/2/issue/" + issueKey;

        Response response = given()
                .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                .redirects().follow(false)
                .headers(getHeaders(token, "application/json"))
                .body(inputData)
                .post(jiraURL + String.format(endpoint,issueKey));

        try {
            logger.info("\ninfo: Transitioning issue " + issueKey + " using transitionId " + transitionId + "\n");

            BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

            while ((output = br.readLine()) != null) {
                logger.info(output);
            }

            logger.info("exit: " + response.getStatusCode());

        } catch (Throwable e) {
            logger.error("\nerror: Something went wrong transitioning issue " + issueKey + " using transitionId " + transitionId + "n");
            e.printStackTrace();
        }
    }

    /**
     * A function for attaching a file to an existing JIRA issue
     * Used for attaching screenshots to bugs
     * @param token the JIRA token for authorization
     * @param issueKey the JIRA issue to attach the file to
     * @param attachment the attachment as byte[]
     * @param filename the name of the file to attach
     * @param mimetype the mimetype of the file to attach
     */
    public void issueAttachment(String token, String issueKey, byte[] attachment, String filename, String mimetype) {
        //String endpoint = "/rest/api/2/issue/" + issueKey + "/attachments";
        String endpoint = String.format(endpointValue,issueKey);

        Response response = given()
                .log().all()
                .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                .redirects().follow(false)
                .headers(getHeaders(token, "multipart/form-data"))
                .multiPart("file",filename,attachment, mimetype)
                .post(jiraURL + endpoint);

        response.then().log().all();

        try {
            logger.info("\ninfo: Attaching file to issue " + issueKey + "\n");

            BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

            while ((output = br.readLine()) != null) {
                logger.info(output);
            }

            logger.info("exit: " + response.getStatusCode());

        } catch (Throwable e) {
            logger.error("\nerror: Something went wrong attaching file to issue " + issueKey + "n");
            e.printStackTrace();
        }
    }

    /**
     * A function fo creating a Bug in JIRA for each unique failure in a cucumber .json results file following an execution
     * @param token the JIRA token for authorization
     * @param resultsPath the relative path to the Cucumber.json file to process
     * @param projectKey the JIRA project key (e.g. SCA)
     * @param component the component to associate the Bugs to
     * @param affectsVersion the JIRA affects version to associate the bugs to
     * @param linkType the link id to associate the bug with their failed scenarios in JIRA, e.g. Defect
     * @param linkExecution true to link the latest execution for this component or false for no
     * @param environment the environment the defect was found in
     */
    public void createDefectsForAllFailures(String token, String resultsPath, String projectKey, String component, String affectsVersion, String linkType, boolean linkExecution, String executionId, String environment) {

        int featureCounter = 0;

        Map<String, List<Map<byte[], String>>> issueScenarioAttachments = new HashMap<>();
        String latestExecution = "";
        if (linkExecution) {
            latestExecution = getLatestExecution(token, projectKey, component);
        }

        try {
            final String json = new String(Files.readAllBytes(Paths.get(resultsPath)));
            documentContext = JsonPath.parse(json);
        }
        catch (Exception e){
            logger.error("Error reading json results file");
        }

        JSONArray features = documentContext.read("$");
        int featuresCounter = 0;

        feature_loop:
        while (featuresCounter < features.size()) {

            Map<String, List<Map<String, String>>> issueScenarios = new HashMap<>();
            Map<String, List<String>> scenariolinkedIssues = new HashMap<>();

            JSONArray scenarios = documentContext.read("$[" + featureCounter + "].elements");
            int scenariosCounter = 0;

            while (scenariosCounter < scenarios.size()) {
                String issueKey = "";
                String errorMessage = "";
                String updateNotes = "";
                String finalStatus = "";
                String scenarioName = "";
                String scenarioId = "";
                String stepName = "";
                Map<byte[], String> attachments = new HashMap<>();
                Map<String, String> scenarioDetails = new HashMap<>();
                List<String> linkedIssues = new ArrayList<>();
                if (linkExecution) {
                    if (skipCharacters.contains(executionId)) {
                        linkedIssues.add(latestExecution);
                    }
                    else {
                        linkedIssues.add(executionId);
                    }
                }

                DocumentContext scenario = JsonPath.parse(scenarios.get(scenariosCounter));
                scenarioName = scenario.read("name");
                scenarioId = scenario.read("id");
                JSONArray tags = scenario.read("tags");
                int z = 0;

                boolean foundScenario = false;
                tag_loop:
                while (z < tags.size()) {
                    DocumentContext tag = JsonPath.parse(tags.get(z));
                    JSONArray tagKey = tag.read(".name");
                    String tagName = (tagKey.get(0).toString());
                    if ((tagName.startsWith("@SCA-")) && (tagName.contains("Q") == false) && (foundScenario == false)) {
                        issueKey = tagName.replace("@", "");
                        linkedIssues.add(issueKey);
                        foundScenario = true;
                    } else if (tagName.startsWith("@SCA")) {
                        linkedIssues.add(tagName.replace("@",""));
                    } else if(tagName.startsWith("@R_SCA")) {
                        logger.error("FOUND A REQUIREMENT");
                        linkedIssues.add(tagName.replace("@R_",""));
                    }
                    z++;
                }

                JSONArray steps = scenario.read("steps");

                int y = 0;
                while (y < steps.size()) {
                    DocumentContext step = JsonPath.parse(steps.get(y));

                    JSONArray keyword = step.read(".keyword");
                    String keywordName = (keyword.get(0).toString());

                    JSONArray name = step.read(".name");
                    stepName = (name.get(0).toString());

                    JSONArray status = step.read(".result.status");
                    String statusName = (status.get(0).toString());

                    if (statusName.equals("failed")) {
                        try {
                            JSONArray error = step.read(".error_message");
                            errorMessage = (error.get(0).toString());
                        }
                        catch (Exception e) {
                            logger.error("Could not get an error message");
                        }
                        scenarioDetails.put("errorMessage", errorMessage);
                    }

                    finalStatus = statusName;

                    updateNotes = updateNotes + keywordName + " " + stepName + " : " + statusName + "\n";

                    if ((statusName.equals("failed"))||(y==steps.size()-1 && statusName.equals("skipped"))) {

                        JSONArray embeddings = step.read(".embeddings");

                        int a = 0;
                        while (a < embeddings.size()) {
                            DocumentContext embedding = JsonPath.parse(steps.get(y));

                            JSONArray data = embedding.read(".data");
                            byte[] dataName = (data.get(0).toString().getBytes());

                            JSONArray mimeType = embedding.read(".mime_type");
                            String mimeTypeName = (mimeType.get(0).toString());

                            attachments.put(dataName, mimeTypeName);
                            a++;
                        }
                    }
                    y++;
                }

                if (finalStatus.equals("passed") == false) {
                    finalStatus = "failed";
                }
                scenarioDetails.put("finalStatus", finalStatus);
                scenarioDetails.put("updateNotes", updateNotes);
                scenarioDetails.put("scenarioName", scenarioName);
                scenarioDetails.put("scenarioId", scenarioId);

                List<Map<String, String>> existingIssue;
                try {
                    existingIssue = issueScenarios.get(issueKey);

                } catch (Exception e) {
                    throw e;
                }
                try {
                    existingIssue.add(scenarioDetails);
                } catch (Exception e) {
                    existingIssue = new ArrayList<>();
                    existingIssue.add(scenarioDetails);
                }

                List<Map<byte[], String>> existingAttachments;

                try {
                    existingAttachments = issueScenarioAttachments.get(issueKey);

                } catch (NullPointerException e) {
                    existingAttachments = new ArrayList<>();
                }

                try {
                    existingAttachments.add(attachments);
                } catch (Exception e) {
                    existingAttachments = new ArrayList<>();
                    existingAttachments.add(attachments);
                }

                if (finalStatus.equals("failed")) {
                    issueScenarios.put(issueKey, existingIssue);
                    scenariolinkedIssues.put(issueKey, linkedIssues);
                    issueScenarioAttachments.put(issueKey, existingAttachments);
                }
                scenariosCounter++;
            }

            Map<String,String> raisedIssues = new HashMap<>();
            jira_key_loop:
            for (Map.Entry<String, List<Map<String, String>>> entry : issueScenarios.entrySet()) {

                List<Map<String,String>> failedExecutions = new ArrayList<>();


                scenario_execution_list:
                for (int i = 0; i < entry.getValue().size(); i++) {

                    Map<String,String> scenarioExecution = entry.getValue().get(i);

                    if (scenarioExecution.get("finalStatus").equals("failed")) {
                        failedExecutions.add(scenarioExecution);
                    }
                }

                if (failedExecutions.size()>0) {
                    logger.info("ABOUT TO CREATE DEFECT FOR ISSUE KEY " + entry.getKey());
                    logger.info("ATTACHMENTS  SIZE " + issueScenarioAttachments.get(entry.getKey()).get(0).size());
                    List<String> linkMe = scenariolinkedIssues.get(entry.getKey());
                    for (int i = 0; i < linkMe.size(); i++) {
                        logger.info("Going to link issue : " + linkMe.get(i));
                    }

                    String summary = failedExecutions.get(0).get("scenarioName").replace("\n","").replaceAll("[^a-zA-Z0-9 ]"," ") + failedExecutions.get(0).get("finalStatus");
                    String errorMessage = failedExecutions.get(0).get("errorMessage");
                    if (errorMessage!="") {
                        summary = errorMessage.substring(0,errorMessage.indexOf("\n"));
                    }

                    if (summary.length() > 255) {
                        summary = summary.substring(0, 244);
                    }

                    logger.info("SUMMARY IS " + summary);

                    String description = failedExecutions.get(0).get("updateNotes").replace("\n","").replaceAll("[^a-zA-Z0-9 ]"," ") +  failedExecutions.get(0).get("errorMessage").replace("\n","").replaceAll("[^a-zA-Z0-9 ]"," ");
                    logger.info("DESCRIPTION IS : " + description);

                    String priority = (String)getIssueAttribute(token, entry.getKey(),"fields.priority.name");
                    logger.info("PRIORITY IS : " + priority);

                    String severity = "";
                    if (priority.contains("Highest")) {
                        severity = "1 - Critical";
                    } else if(priority.contains("High")) {
                        severity  = "2 - Major";
                    } else if (priority.contains("Medium")) {
                        severity = "3 - Medium";
                    } else {
                        severity = "4 - Minor";
                    }
                    logger.info("SEVERITY IS : " + severity);

                    String environmentParent = "";
                    String environmentChild = "";
                    if (environment.contains("SIT")) {
                        environmentParent = "SCA SIT";
                        if (environment.contains("1")) {
                            environmentChild = "SIT01";
                        } else if (environment.contains("2")) {
                            environmentChild = "SIT02";
                        } else if (environment.contains("3")) {
                            environmentChild = "SIT03";
                        } else if (environment.contains("4")) {
                            environmentChild = "SIT04";
                        } else if (environment.contains("5")) {
                            environmentChild = "SIT05";
                        } else if (environment.contains("6")) {
                            environmentChild = "SIT06";
                        }
                    } else if (environment.contains("NFT")) {
                        environmentParent = "SCA NFT";
                        if (environment.contains("1")) {
                            environmentChild = "NFT01";
                        } else if (environment.contains("2")) {
                            environmentChild = "NFT02";
                        } else if (environment.contains("3")) {
                            environmentChild = "NFT03";
                        }
                    } else if (environment.contains("LUAT")) {
                        environmentParent = "SCA LUAT";
                        if (environment.contains("1")) {
                            environmentChild = "LUAT01";
                        } else if (environment.contains("2")) {
                            environmentChild = "LUAT02";
                        }
                    } else {
                        environmentParent = "SCA Dev (Sandbox)";
                        environmentChild = "Master";
                    }
                    logger.info("Environment is " + environmentParent + " : " + environmentChild);

                    // Check if the issue is already raised
                    boolean similarIssue = false;
                    similar_issue:
                    for (Map.Entry<String, String> myEntry : raisedIssues.entrySet()) {
                        if (computeLevenshteinDistance(summary, myEntry.getKey())<=10) {
                            logger.info("Similar issue already raise, not raising");
                            similarIssue = true;
                            summary = myEntry.getKey();
                            break similar_issue;
                        }
                    }

                    if (!raisedIssues.containsKey(summary) && !similarIssue) {

                        String jql = "issue in linkedIssues(\""+entry.getKey()+"\", created) and issueType = Bug and status not in (Closed, \"Release Backlog\") and resolution is EMPTY";
                        DocumentContext defectdAgainstScenario = searchForJiraIssues(token,jql);

                        int numberOfResults = Integer.parseInt(defectdAgainstScenario.read("total").toString());
                        logger.info("Number of open defects linked to scenario : " + numberOfResults);

                        if (numberOfResults > 0) {
                            JSONArray issuesInSearch = defectdAgainstScenario.read("$.issues[?(@.key)].key");
                            String existingScenarioIssue = issuesInSearch.get(0).toString();
                            logger.info("Scenario already has defect open " + existingScenarioIssue);
                            postCommentToIssue(token, summary, existingScenarioIssue);
                        } else {

                            logger.info("CREATING DEFECT NOW");
                            String defectId = createDefectWithAttachment(token, projectKey, summary, description, component, affectsVersion, environmentParent, environmentChild, priority, severity, scenariolinkedIssues.get(entry.getKey()), linkType, issueScenarioAttachments.get(entry.getKey()).get(0));
                            logger.info("Created defect : " + defectId);
                            if (defectId != null && defectId != "") {
                                raisedIssues.put(summary, defectId);
                            }

                            raisedIssues.put(summary, defectId);
                        }
                    }
                    else {
                        logger.info("Linking scenario " + entry.getKey() + " to previously raised issue " + raisedIssues.get(summary));
                        linkIssues(token, entry.getKey(), raisedIssues.get(summary), linkType);
                    }
                }
            }
            logger.info("RAISING : " + raisedIssues.size() + " issues");
            for (Map.Entry<String, String> entry : raisedIssues.entrySet()) {
                logger.info(entry.getKey() + " : " + entry.getValue());
            }

            featuresCounter++;
        }
    }


    private static int minimum(int a, int b, int c)
    {
        return Math.min(Math.min(a, b), c);
    }

    public static int computeLevenshteinDistance(CharSequence str1, CharSequence str2)
    {
        int[][] distance = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= str2.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= str1.length(); i++)
            for (int j = 1; j <= str2.length(); j++)
                distance[i][j] = minimum(distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));

        return distance[str1.length()][str2.length()];
    }


    /**
     * A function for creating a Bug in JIRA and attaching a screenshot
     * This will first search JIRA for existing issues to make sure it doesnt already exist
     * @param token the JIRA token for authorization
     * @param comment the JIRA project Key
     * @param jiraId a summary text to give the JIRA issue
     */
    public void postCommentToIssue(String token, String comment, String jiraId) {

        
        
        

        comment = comment.replace("\n", "");

        String inputData = "{\n"
                + "    \"body\": \"" + comment + "\"\n"
                + "}";

        //String endpoint = "/rest/api/2/issue/"+jiraId+"/comment";
        String endpoint = String.format(endpointComment,jiraId);

        Response response = given()
                .log().all()
                .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                .redirects().follow(false)
                .headers(getHeaders(token, "application/json"))
                .body(inputData)
                .post(jiraURL + endpoint);
        response.then().log().all();

    }


    /**
     * A function for creating a Bug in JIRA and attaching a screenshot
     * This will first search JIRA for existing issues to make sure it doesnt already exist
     * @param token the JIRA token for authorization
     * @param projectKey the JIRA project Key
     * @param summary a summary text to give the JIRA issue
     * @param description a full description to give the JIRA issue
     * @param component the JIRA component to associate with the JIRA issue
     * @param affectsVersion the JIRA affectsVersion to associate with the JIRA issue
     * @param linkedIssues a list of the JIRA issue key of the Xray test to link
     * @param linkType the name of the link to associate the defect to its failed scenario
     * @param attachment a Map of attachments in as bytes together with mimetype
     */
    public String createDefectWithAttachment(String token, String projectKey, String summary, String description, String component, String affectsVersion, String environmentParent, String environmentChild, String priority, String severity, List<String> linkedIssues, String linkType, Map<byte[], String> attachment) {

        String jql = "project = " + projectKey + " AND issuetype = Bug AND status not in  (\"Release Backlog\", Closed) AND component = \"" + component + "\" and summary ~ \"" + summary + "\"";
        logger.info("SEARCHING FOR " + jql);
        DocumentContext searchResults = searchForJiraIssues(token, jql);
        logger.info(searchResults.read("$").toString());
        logger.info(searchResults.read("total").toString());
        int numberOfResults = Integer.parseInt(searchResults.read("total").toString());
        logger.info(Integer.toString(numberOfResults));

        String defectId = "";
        if (numberOfResults > 0) {
            JSONArray issuesInSearch = searchResults.read("$.issues[?(@.key)].key");
            defectId = issuesInSearch.get(0).toString();
        }

        if (numberOfResults == 0) {

            String inputData = "{\n"
                    + "                    \"fields\": {\n"
                    + "                       \"project\":\n"
                    + "                       {\n"
                    + "                         \"key\": \"" + projectKey + "\"\n"
                    + "                        },\n"
                    + "                       \"summary\": \"" + summary + "\",\n"
                    + "                       \"description\": \"" + description + "\",\n"
                    + "                       \"components\":[{\"name\":\"" + component + "\"}],\n"
                    + "                       \"versions\":[{\"name\":\"" + affectsVersion + "\"}],\n"
                    + "                       \"customfield_10311\": {\n"
                    + "                          \"value\": \""+severity+"\"\n"
                    + "                       },\n"
                    + "                       \"priority\": {\n"
                    + "                          \"name\": \""+priority+"\"\n"
                    + "                       },\n"
                    + "                       \"customfield_11931\":[{\"value\":\"All Brands\"}],\n"
                    + "                       \"customfield_14012\": {\n"
                    + "                          \"value\": \""+environmentParent+"\",\n"
                    + "                          \"child\": {\n"
                    + "                             \"value\": \""+environmentChild+"\"\n"
                    + "                         }\n"
                    + "                       },\n"
                    + "                       \"customfield_13802\": {\n"
                    + "                         \"value\": \"System Test\"\n"
                    + "                         },\n"
                    + "                        \"customfield_10616\": {\n"
                    + "                             \"value\": \"Waterloo\"\n"
                    + "                         },\n"
                    + "                       \"issuetype\": {\n"
                    + "                          \"name\": \"Bug\"\n"
                    + "                       }\n"
                    + "                   }\n"
                    + "                }";

            //String endpoint = "/rest/api/2/issue/";
            String endpoint = baseEndpoint;

            Response response = given()
                    .log().all()
                    .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                    .redirects().follow(false)
                    .headers(getHeaders(token, "application/json"))
                    .body(inputData)
                    .post(jiraURL + endpoint);
            response.then().log().all();
            DocumentContext defectResponse = JsonPath.parse(response.prettyPrint());
            defectId = defectResponse.read("key");

            try {
                logger.info("\ninfo: Creating defect\n");

                BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

                while ((output = br.readLine()) != null) {
                    logger.info(output);
                }

                logger.info("exit: " + response.getStatusCode());

            } catch (Throwable e) {
                logger.error("\nerror: Something went wrong creating defect\n");
                e.printStackTrace();
            }

            if (attachment.size()>0) {

                for (Map.Entry<byte[],String> entry : attachment.entrySet()) {
                    String extension = "";

                    if (entry.getValue().contains("png")) {
                        extension = ".png";
                    }
                    else {
                        extension = ".txt";
                    }

                    File file = null;
                    OutputStream opStream = null;
                    try {
                        logger.info(extension);

                        file = new File("target/file" + extension);
                        // check if file exist, otherwise create the file before writing
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        opStream = new FileOutputStream(file);
                        opStream.write(entry.getKey());
                        opStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally{
                        try{
                            if(opStream != null) opStream.close();
                        } catch(Exception ex){

                        }
                    }
                    issueAttachment(token, defectId, Base64.getDecoder().decode(entry.getKey()), "target/test" + extension, entry.getValue());
                }
            }
        }

        if (linkedIssues.size()>0) {

            for (int i = 0; i < linkedIssues.size(); i++) {
                linkIssues(token, linkedIssues.get(i), defectId, linkType);
            }
        }

        return defectId;
    }

    /**
     * A function to return a given attribute for an issue.
     * This will first search JIRA for existing issues to make sure it doesn't already exist
     * @param token the JIRA token for authorization
     * @param issueId the JIRA issue to lookup
     * @param attribute the JSON path to the issue to return
     */
    public Object getIssueAttribute(String token, String issueId, String attribute) {

        //String endpoint = "/rest/api/2/issue/"+issueId;
        String endpointValue = String.format(endpoint,issueId);

        Response response = given()
                .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                .redirects().follow(false)
                .headers(getHeaders(token, "application/json"))
                .get(jiraURL + endpointValue);

        DocumentContext responseJson = JsonPath.parse(response.prettyPrint());

        logger.info("ATTRIBUTE=" + responseJson.read(attribute));
        return responseJson.read(attribute);
    }

    /**
     * A function for creating a Bug in JIRA and linking to a failed test scenario
     * This will first search JIRA for existing issues to make sure it doesn't already exist
     * @param token the JIRA token for authorization
     * @param projectKey the JIRA project Key
     * @param summary a summary text to give the JIRA issue
     * @param description a full description to give the JIRA issue
     * @param component the JIRA component to associate with the JIRA issue
     * @param affectsVersion the JIRA affectsVersion to associate with the JIRA issue
     * @param linkIssue the JIRA issue key of the Xray test to link
     * @param linkType the name of the link to associate the defect to its failed scenario
     */
    public void createDefect(String token, String projectKey, String summary, String description, String component, String affectsVersion, String linkIssue, String linkType) {

        String jql = "project = " + projectKey + " AND issuetype = Bug AND status not in  (\"Release Backlog\", Closed) AND component = \"" + component + "\" and summary ~ \"" + summary + "\"";
        DocumentContext searchResults = searchForJiraIssues(token, jql);
        logger.info(searchResults.read("$").toString());
        logger.info(searchResults.read("total").toString());
        int numberOfResults = Integer.parseInt(searchResults.read("total").toString());
        logger.info(Integer.toString(numberOfResults));
        if (numberOfResults == 0) {

            String inputData = "{\n"
                    + "                    \"fields\": {\n"
                    + "                       \"project\":\n"
                    + "                       {\n"
                    + "                         \"key\": \"" + projectKey + "\"\n"
                    + "                        },\n"
                    + "                       \"summary\": \"" + summary + "\",\n"
                    + "                       \"description\": \"" + description + "\",\n"
                    + "                       \"components\":[{\"name\":\"" + component + "\"}],\n"
                    + "                       \"versions\":[{\"name\":\"" + affectsVersion + "\"}],\n"
                    + "                       \"customfield_10311\": {\n"
                    + "                          \"value\": \"2 - Major\"\n"
                    + "                       },\n"
                    + "                       \"customfield_11931\":[{\"value\":\"All Brands\"}],\n"
                    + "                       \"customfield_14012\": {\n"
                    + "                          \"value\": \"SCA Dev (Sandbox)\",\n"
                    + "                          \"child\": {\n"
                    + "                             \"value\": \"Master\"\n"
                    + "                         }\n"
                    + "                       },\n"
                    + "                       \"customfield_13802\": {\n"
                    + "                         \"value\": \"System Test\"\n"
                    + "                         },\n"
                    + "                        \"customfield_10423\": {\n"
                    + "                             \"value\": \"SCA\"\n"
                    + "                         },\n"
                    + "                        \"customfield_10616\": {\n"
                    + "                             \"value\": \"Waterloo\"\n"
                    + "                         },\n"
                    + "                       \"issuetype\": {\n"
                    + "                          \"name\": \"Bug\"\n"
                    + "                       }\n"
                    + "                   }\n"
                    + "                }";

            //String endpoint = "/rest/api/2/issue/";
            String endpoint = baseEndpoint;

            Response response = given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                    .redirects().follow(false)
                    .headers(getHeaders(token, "application/json"))
                    .body(inputData)
                    .post(jiraURL + endpoint);

            DocumentContext defectResponse = JsonPath.parse(response.prettyPrint());
            String defectId = defectResponse.read("key");

            try {
                logger.info("\ninfo: Creating defect\n");

                BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

                while ((output = br.readLine()) != null) {
                    logger.info(output);
                }

                logger.info("exit: " + response.getStatusCode());

            } catch (Throwable e) {
                logger.error("\nerror: Something went wrong creating defect\n");
                e.printStackTrace();
            }

            if (linkIssue != "") {
                linkIssues(token, defectId, linkIssue, linkType);
            }
        }
    }


    /**
     * A function for creating a Bug in JIRA and linking to a failed test scenario
     * This will first search JIRA for existing issues to make sure it doesn't already exist
     * This uses a template at a relative location for populating the fields in the JIRA issue
     * @param token the JIRA token for authorization
     * @param projectKey the JIRA project Key
     * @param summary a summary text to give the JIRA issue
     * @param description a full description to give the JIRA issue
     * @param pathToTemplate a template in JSON for creating the defect with
     * @param linkIssue the JIRA issue key of the Xray test to link
     * @param linkType the name of the link to associate the defect to its failed scenario
     */
    public void createDefectWithTemplate(String token, String projectKey, String summary, String description, String linkIssue,
                                         String linkType, String pathToTemplate) {

        String jql = "project = " + projectKey + " AND issuetype = Bug AND status not in  (\"Release Backlog\", Closed) and summary ~ \"" + summary + "\"";
        DocumentContext searchResults = searchForJiraIssues(token, jql);
        logger.info(searchResults.read("$").toString());
        logger.info(searchResults.read("total").toString());
        int numberOfResults = Integer.parseInt(searchResults.read("total").toString());
        logger.info(Integer.toString(numberOfResults));

        if (numberOfResults == 0) {
            DocumentContext bugTemplate = null;
            try {
                bugTemplate = JsonPath.parse(new File(pathToTemplate));
            } catch (Exception e) {
                logger.info("Could not parse file");
            }
            bugTemplate.set("fields.project.key", projectKey);
            bugTemplate.set("fields.summary", summary);
            bugTemplate.set("fields.description", description);
            bugTemplate.set("fields.summary", summary);

            //String endpoint = "/rest/api/2/issue/";
            String endpoint = baseEndpoint;

            Response response = given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                    .redirects().follow(false)
                    .headers(getHeaders(token, "application/json"))
                    .body(bugTemplate)
                    .post(jiraURL + endpoint);

            DocumentContext defectResponse = JsonPath.parse(response.prettyPrint());
            String defectId = defectResponse.read("key");

            try {
                logger.info("\ninfo: Creating defect\n");

                BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

                while ((output = br.readLine()) != null) {
                    logger.info(output);
                }

                logger.info("exit: " + response.getStatusCode());

            } catch (Throwable e) {
                logger.error("\nerror: Something went wrong creating defect\n");
                e.printStackTrace();
            }

            if (linkIssue != "") {
                linkIssues(token, defectId, linkIssue, linkType);
            }
        }
    }

    /**
     * A function for creating a Bug in JIRA and linking to a failed test scenario
     * This will first search JIRA for existing issues to make sure it doesn't already exist
     * @param token the JIRA token for authorization
     * @param component a JQL search in JIRA for issues
     * @return String the JIRA key of the most recent test execution
     */
    private String getLatestExecution(String token, String projectKey, String component) {

        //String endpoint = "/rest/api/2/search?jql=" + "project="+projectKey + " AND component='"+component +"' AND issueType='Test Execution' order by created DESC";
        String endpoint = String.format(endpointLatestExecution,projectKey,component);

        Response response = given()
                .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                .redirects().follow(false)
                .headers(getHeaders(token, "application/json"))
                .get(jiraURL + endpoint);

        DocumentContext searchResults = JsonPath.parse(response.prettyPrint());
        JSONArray issuesInSearch = searchResults.read("$.issues[?(@.key)].key");

        try {
            logger.info("\ninfo: Starting jira search");

            BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

            logger.info("exit: " + response.getStatusCode());

        } catch (Throwable e) {
            logger.error("\nerror: Something went wrong\n");
            e.printStackTrace();
        }

        return issuesInSearch.get(0).toString();

    }

    /**
     * A function for creating a Bug in JIRA and linking to a failed test scenario
     * This will first search JIRA for existing issues to make sure it doesn't already exist
     * @param token the JIRA token for authorization
     * @param jql a JQL search in JIRA for issues
     * @return a DocumentContext containing the search results of the JIRA query
     */
    public DocumentContext searchForJiraIssues(String token, String jql) {

        //String endpoint = "/rest/api/2/search?maxResults=500&jql=" + jql;
        String endpoint = String.format(endpointSearchJiraIssue,jql);

        Response response = given()
                .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                .redirects().follow(false)
                .headers(getHeaders(token, "application/json"))
                .get(jiraURL + endpoint);

        DocumentContext searchResults = JsonPath.parse(response.prettyPrint());

        try {
            logger.info("\ninfo: Starting jira search");

            BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

            logger.info("exit: " + response.getStatusCode());

        } catch (Throwable e) {
            logger.error("\nerror: Something went wrong\n");
            e.printStackTrace();
        }

        return searchResults;

    }

    /**
     * This function is for creating a list of exections in JIRA for a planned test execution cycle against an environment for a release
     * @param token the JIRA token for authorization
     * @param projectKey the JIRA project key (e.g. SCA)
     * @param executions a list of executions to create, separated by commas
     * @param environment the environment to associate that the executions will be run against
     * @param fixVersion a version to associate the executions with
     */
    public void createExecutions(String token, String projectKey, String executions, String environment, String fixVersion, String label) {

        String[] executionsArray = executions.split(",");
        logger.info("Going to create : " + executionsArray.length + " executions");
        LinkedHashMap<String, String> executionsCreated = new LinkedHashMap<>();

        for(int i = 0; i< executionsArray.length; i++) {
            logger.info("Creating execution for " + executionsArray[i]);
            String executionDetails[] = executionsArray[i].split(":");
            String summary = "Test execution of " + executionDetails[0] + " (" + executionDetails[1] + ") in " + environment;
            setTestExecutionDetails(projectKey, executionDetails[1], summary, summary, fixVersion, environment, executionDetails[0],"", label);

            //String endpoint = "/rest/api/2/issue/";
            String endpoint = baseEndpoint;

            Response response = given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                    .redirects().follow(false)
                    .headers(getHeaders(token, "application/json"))
                    .body(documentContext.jsonString())
                    .post(jiraURL + endpoint);

            DocumentContext responseJson = JsonPath.parse(response.prettyPrint());
            String executionId = responseJson.read("key");
            logger.info("Created execution " + summary + " : " + executionId);
            addRemoveTestsToTestExecution(token, executionId, executionDetails[1]);
            if (executionsCreated.containsKey(executionDetails[0])) {
                executionsCreated.put(executionDetails[0], executionsCreated.get(executionDetails[0])+";"+executionId);
            } else {
                executionsCreated.put(executionDetails[0], executionId);
            }
        }
        System.out.println("EXECUTIONS:");
        executionsCreated.entrySet().forEach(entry->{
            System.out.println(entry.getKey() + "=" + entry.getValue());
        });
    }

    private String folderId = "-1";
    private String foundPath = "";

    /**
     * A function to check if a folder already exists in the Xray repository
     * @param token the JIRA token for authorization
     * @param projectKey the JIRA project key (e.g. SCA)
     * @param folderPath
     * @return true if the folder already exists, else false
     */
    public boolean checkFolderExists(String token, String projectKey, String folderPath) {

        //String endpoint = "/rest/raven/1.0/api/testrepository/"+projectKey+"/folders";
        String endpoint = String.format(endpointFolder,projectKey);

        Response response = given()
                .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                .redirects().follow(false)
                .headers(getHeaders(token, "application/json"))
                .get(jiraURL + endpoint);

        DocumentContext result = JsonPath.parse(response.prettyPrint());

        try {
            logger.info("\ninfo: Getting Repository Folders");

            BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));
            logger.info("exit: " + response.getStatusCode());

        } catch (Throwable e) {
            logger.error("\nerror: Something went wrong\n");
            e.printStackTrace();
        }

        if (folderPath.startsWith("/")) {
            folderPath = folderPath.substring(1);
        }
        String[] repoPaths = folderPath.split("/");
        logger.info("Folder Depth" + repoPaths.length);

        String jsonPath = "$";

        boolean folderFound = false;

        for(int i = 0; i< repoPaths.length; i++){

            logger.info("Looking for folder " + repoPaths[i]);
            jsonPath = jsonPath + ".folders[?(@.name=='"+repoPaths[i]+"')]";
            logger.info(jsonPath);
            String tempId = (result.read(jsonPath + ".id").toString().replace("[","").replace("]",""));
            logger.info(tempId);
            if (tempId.equals("")) {
                folderFound = false;
                break;
            }
            else {
                folderFound = true;
                foundPath = foundPath + "/" + repoPaths[i];
                folderId = tempId;
            }
        }
        logger.info("Folder found =  " + folderFound);
        logger.info("Final folder id " + folderId);

        return folderFound;
    }

    /**
     * A function for creating a new folder in the Xray Repository
     * It will first be checked to what extent (folder depth) the path exists
     * The path needs to be created iteratively
     * @param token the JIRA token for authorization
     * @param projectKey the JIRA project key (e.g. SCA)
     * @param folderPath the Xray Repository path to create
     */
    public void createFolder(String token, String projectKey, String folderPath) {

        if(!checkFolderExists(token, projectKey, folderPath)) {

            folderPath = folderPath.replace(foundPath,"");
            if (folderPath.startsWith("/")) {
                folderPath = folderPath.substring(1);
            }
            logger.info("Path to create: " + folderPath);

            String endpoint;

            DocumentContext inputData = JsonPath.parse("{\n"
                    + "  \"name\": \"Folder1\"\n"
                    + "}");


            String[] repoPaths = folderPath.split("/");
            logger.info("Folder Depth" + repoPaths.length);

            for(int i = 0; i< repoPaths.length; i++) {

                if (repoPaths[i]!="") {

                    logger.info("Creating folder " + repoPaths[i]);

                    //endpoint = "/rest/raven/1.0/api/testrepository/"+projectKey+"/folders/" + folderId;
                    endpoint = String.format(endpointCreateFolder,projectKey) + folderId;

                    inputData.set("name", repoPaths[i]);

                    Response response = given()
                            .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                            .redirects().follow(false)
                            .headers(getHeaders(token, "application/json"))
                            .body(inputData.jsonString())
                            .post(jiraURL + endpoint);

                    DocumentContext result = JsonPath.parse(response.prettyPrint());
                    String id = result.read(".id").toString().replace("[","").replace("]","");
                    folderId = id;
                    logger.info("Created folder id: " + id + " for " + repoPaths[i]);


                    try {
                        logger.info("\ninfo: Creating Folder");

                        BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

                        while ((output = br.readLine()) != null) {
                            logger.info(output);
                        }

                        logger.info("exit: " + response.getStatusCode());

                    } catch (Throwable e) {
                        logger.error("\nerror: Something went wrong\n");
                        e.printStackTrace();
                    }
                }
                else {
                    logger.info("Folder name blank");
                }
            }
        }

        else {
            logger.info("Folder exists no need to create");
        }
    }

    /**
     * Add Xray tests to a folder in the Xray Test Repository using a JQL search
     * @param token the JIRA token for authorization
     * @param projectKey the JIRA project key (e.g. SCA)
     * @param folderPath the test repository folder path to add the tests to
     * @param jqlAdd the jql search to perform to locate the tests to add
     * @param jqlRemove the jql search to perform to locate the tests to add
     */
    public void addRemoveTestsToFolder(String token, String projectKey, String folderPath, String jqlAdd, String jqlRemove) {

        boolean issuesFound = false;
        DocumentContext payload = JsonPath.parse("\n"
                + "{\n"
                + "}");

        int issuesToAdd = 0;
        if (!skipCharacters.contains(jqlAdd) && jqlAdd != null) {
            jqlAdd = "project=" + projectKey + " AND issueType='Xray Test' AND " + jqlAdd;
            DocumentContext searchResults = searchForJiraIssues(token, jqlAdd);
            JSONArray issuesInSearch = searchResults.read("$.issues[?(@.key)].key");
            issuesToAdd = issuesInSearch.size();
            logger.info("Found " + issuesInSearch.size() + " issues to add");

            if (issuesInSearch.size()>0) {
                payload.put("$", "add", issuesInSearch);
                issuesFound = true;
            }
        }

        int issuesToRemove = 0;
        if (!skipCharacters.contains(jqlRemove) && jqlRemove != null) {
            jqlRemove = "project=" + projectKey + " AND issueType='Xray Test' AND " + jqlRemove;
            DocumentContext searchResults = searchForJiraIssues(token, jqlRemove);
            JSONArray issuesInSearch = searchResults.read("$.issues[?(@.key)].key");
            issuesToRemove = issuesInSearch.size();
            logger.info("Found " + issuesInSearch.size() + " issues to remove");

            if (issuesInSearch.size()>0) {
                payload.put("$", "remove", issuesInSearch);
                issuesFound = true;
            }
        }

        if (issuesFound) {
            createFolder(token, projectKey, folderPath);

            //String endpoint = "/rest/raven/1.0/api/testrepository/" + projectKey + "/folders/" + folderId + "/tests";
            String endpoint = String.format(endpointRemoveTestToFolder,projectKey,folderId);

            Response response = given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                    .redirects().follow(false)
                    .headers(getHeaders(token, "application/json"))
                    .body(payload.jsonString())
                    .put(jiraURL + endpoint);

            logger.info("Added " + issuesToAdd + " issues and removed " + issuesToRemove + " to folder " + folderPath + "(" + folderId + ")");

            try {
                logger.info("\ninfo: Adding tests to folder");

                BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

                while ((output = br.readLine()) != null) {
                    logger.info(output);
                }

                logger.info("exit: " + response.getStatusCode());

            } catch (Throwable e) {
                logger.error("\nerror: Something went wrong\n");
                e.printStackTrace();
            }
        }

        else {
            logger.warn("No issues found to add or remove");
        }
    }

    /**
     * For adding tests returned from a JQL search to a test plan
     * @param token the JIRA token for authorization
     * @param projectKey the JIRA project key (e.g. SCA)
     * @param testPlanKey the issue key for the test plan to add to "NEW" to create new
     * @param jqlAdd the JQL search to find issues to add to the Test Plan
     * @param jqlRemove the JQL search to find issues to remove from the Test Plan
     */
    public void addRemoveTestsToTestPlan(String token, String projectKey, String testPlanKey, String jqlAdd, String jqlRemove) {

        DocumentContext payload = JsonPath.parse("\n"
                + "{\n"
                + "}");

        boolean issuesFound = false;
        int issuesToAdd = 0;
        if (!skipCharacters.contains(jqlAdd) && jqlAdd != null) {
            jqlAdd = "project=" + projectKey + " AND issueType='Xray Test' AND " + jqlAdd;
            DocumentContext searchResults = searchForJiraIssues(token, jqlAdd);
            JSONArray issuesInSearch = searchResults.read("$.issues[?(@.key)].key");
            issuesToAdd = issuesInSearch.size();
            logger.info("Found " + issuesInSearch.size() + " issues to add");

            if (issuesInSearch.size()>0) {
                payload.put("$", "add", issuesInSearch);
                issuesFound = true;
            }
        }

        int issuesToRemove = 0;
        if (!skipCharacters.contains(jqlRemove) && jqlRemove != null) {
            jqlRemove = "project=" + projectKey + " AND issueType='Xray Test' AND " + jqlRemove;
            DocumentContext searchResults = searchForJiraIssues(token, jqlRemove);
            JSONArray issuesInSearch = searchResults.read("$.issues[?(@.key)].key");
            issuesToRemove = issuesInSearch.size();
            logger.info("Found " + issuesInSearch.size() + " issues to remove");

            if (issuesInSearch.size()>0) {
                payload.put("$", "remove", issuesInSearch);
                issuesFound = true;
            }
        }

        if (issuesFound) {

            if (testPlanKey.equals("NEW")) {
                testPlanKey = createTestPlan(token, projectKey, "", "");
            }

            //String endpoint = "/rest/raven/1.0/api/testplan/" + testPlanKey + "/test";
            String endpoint = String.format(endpointTestPlan,testPlanKey);

            Response response = given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                    .redirects().follow(false)
                    .headers(getHeaders(token, "application/json"))
                    .body(payload.jsonString())
                    .post(jiraURL + endpoint);

            logger.info("Added " + issuesToAdd + " issues to test plan " + testPlanKey + " and removed " + issuesToRemove);

            try {
                logger.info("\ninfo: Adding tests to Test Plan " + testPlanKey);

                BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

                while ((output = br.readLine()) != null) {
                    logger.info(output);
                }

                logger.info("exit: " + response.getStatusCode());

            } catch (Throwable e) {
                logger.error("\nerror: Something went wrong\n");
                e.printStackTrace();
            }
        }
        else {
            logger.warn("No issues found to add or remove");
        }
    }


    /**
     * For adding tests returned from a JQL search to a test plan
     * @param token the JIRA token for authorization
     * @param testExecutionKey the JIRA project key (e.g. SCA)
     * @param testPlanId the JIRA project key (e.g. SCA)
     */
    public void addRemoveTestsToTestExecution(String token, String testExecutionKey, String testPlanId) {

        boolean issuesFound = false;
        int issuesToAdd = 0;

        //String endpoint = "/rest/raven/1.0/api/testplan/"+testPlanId+"/test";
        String endpoint = String.format(endpointTestPlan,testPlanId);

        Response response = given()
                .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                .redirects().follow(false)
                .headers(getHeaders(token, "application/json"))
                .get(jiraURL + endpoint);

        DocumentContext payload = JsonPath.parse("\n"
                + "{\n"
                + "}");

        DocumentContext searchResults = JsonPath.parse(response.prettyPrint());
        JSONArray issuesInSearch = searchResults.read("$.[?(@.key)].key");
        issuesToAdd = issuesInSearch.size();
        logger.info("Found " + issuesInSearch.size() + " issues to add");

        if (issuesInSearch.size()>0) {
            payload.put("$", "add", issuesInSearch);
            issuesFound = true;
        }

        if (issuesFound) {
            //endpoint = "/rest/raven/1.0/api/testexec/"+testExecutionKey+"/test";
            endpoint = String.format(endpointExecution,testExecutionKey);

            response = given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                    .redirects().follow(false)
                    .headers(getHeaders(token, "application/json"))
                    .body(payload.jsonString())
                    .post(jiraURL + endpoint);

            logger.info("Added " + issuesToAdd + " issues to test plan " + testExecutionKey);

        } else {
            logger.warn("No issues found to add or remove");
        }
    }

    /**
     * For adding tests returned from a JQL search to a test set
     * @param token the JIRA token for authorization
     * @param projectKey the JIRA project key (e.g. SCA)
     * @param testSetKey the issue key for the test set to add to "NEW" to create new
     * @param jqlAdd the JQL search to find issues to add to the Test Set
     * @param jqlRemove the JQL search to find issues to remove from the Test Set
     */
    public void addRemoveTestsToTestSet(String token, String projectKey, String testSetKey, String jqlAdd, String jqlRemove) {

        DocumentContext payload = JsonPath.parse("\n"
                + "{\n"
                + "}");

        boolean issuesFound = false;
        int issuesToAdd = 0;
        if (!skipCharacters.contains(jqlAdd) && jqlAdd != null) {
            jqlAdd = "project=" + projectKey + " AND issueType='Xray Test' AND " + jqlAdd;
            DocumentContext searchResults = searchForJiraIssues(token, jqlAdd);
            JSONArray issuesInSearch = searchResults.read("$.issues[?(@.key)].key");
            issuesToAdd = issuesInSearch.size();
            logger.info("Found " + issuesInSearch.size() + " issues to add");

            if (issuesInSearch.size()>0) {
                payload.put("$", "add", issuesInSearch);
                issuesFound = true;
            }
        }

        int issuesToRemove = 0;
        if (!skipCharacters.contains(jqlRemove) && jqlRemove != null) {
            jqlRemove = "project=" + projectKey + " AND issueType='Xray Test' AND " + jqlRemove;
            DocumentContext searchResults = searchForJiraIssues(token, jqlRemove);
            JSONArray issuesInSearch = searchResults.read("$.issues[?(@.key)].key");
            issuesToRemove = issuesInSearch.size();
            logger.info("Found " + issuesInSearch.size() + " issues to remove");

            if (issuesInSearch.size()>0) {
                payload.put("$", "remove", issuesInSearch);
                issuesFound = true;
            }
        }

        if (issuesFound) {

            if (testSetKey.equals("NEW")) {
                testSetKey = createTestSet(token, projectKey, "", "");
            }

            String endpoint = "/rest/raven/1.0/api/testset/" + testSetKey + "/test";

            Response response = given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                    .redirects().follow(false)
                    .headers(getHeaders(token, "application/json"))
                    .body(payload.jsonString())
                    .post(jiraURL + endpoint);

            logger.info("Added " + issuesToAdd + " issues to test set " + testSetKey + " and removed " + issuesToRemove);

            try {
                logger.info("\ninfo: Adding tests to Test Set " + testSetKey);

                BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

                while ((output = br.readLine()) != null) {
                    logger.info(output);
                }

                logger.info("exit: " + response.getStatusCode());

            } catch (Throwable e) {
                logger.error("\nerror: Something went wrong\n");
                e.printStackTrace();
            }
        }
        else {
            logger.warn("No issues found to add or remove");
        }
    }

    /**
     * For adding tests returned from a JQL search to a precondition
     * @param token the JIRA token for authorization
     * @param projectKey the JIRA project key (e.g. SCA)
     * @param preConditionKey the issue key for the precondition to add to
     * @param jqlAdd the JQL search to find issues to add to the Precondition
     * @param jqlRemove the JQL search to find issues to remove from the Precondition
     */
    public void addRemoveTestsToPrecondition(String token, String projectKey, String preConditionKey, String jqlAdd, String jqlRemove) {

        DocumentContext payload = JsonPath.parse("\n"
                + "{\n"
                + "}");

        boolean issuesFound = false;
        int issuesToAdd = 0;
        if (!skipCharacters.contains(jqlAdd) && jqlAdd != null) {
            jqlAdd = "project=" + projectKey + " AND issueType='Xray Test' AND " + jqlAdd;
            DocumentContext searchResults = searchForJiraIssues(token, jqlAdd);
            JSONArray issuesInSearch = searchResults.read("$.issues[?(@.key)].key");
            issuesToAdd = issuesInSearch.size();
            logger.info("Found " + issuesInSearch.size() + " issues to add");

            if (issuesInSearch.size()>0) {
                payload.put("$", "add", issuesInSearch);
                issuesFound = true;
            }
        }

        int issuesToRemove = 0;
        if (!skipCharacters.contains(jqlRemove) && jqlRemove != null) {
            jqlRemove = "project=" + projectKey + " AND issueType='Xray Test' AND " + jqlRemove;
            DocumentContext searchResults = searchForJiraIssues(token, jqlRemove);
            JSONArray issuesInSearch = searchResults.read("$.issues[?(@.key)].key");
            issuesToRemove = issuesInSearch.size();
            logger.info("Found " + issuesInSearch.size() + " issues to remove");

            if (issuesInSearch.size()>0) {
                payload.put("$", "remove", issuesInSearch);
                issuesFound = true;
            }
        }

        if (issuesFound) {

            String endpoint = "/rest/raven/1.0/api/precondition/"+preConditionKey+"/test";

            Response response = given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                    .redirects().follow(false)
                    .headers(getHeaders(token, "application/json"))
                    .body(payload.jsonString())
                    .post(jiraURL + endpoint);

            logger.info("Added " + issuesToAdd + " issues to precondition " + preConditionKey + " and removed " + issuesToRemove);

            try {
                logger.info("\ninfo: Adding tests to Precondition " + preConditionKey);

                BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

                while ((output = br.readLine()) != null) {
                    logger.info(output);
                }

                logger.info("exit: " + response.getStatusCode());

            } catch (Throwable e) {
                logger.error("\nerror: Something went wrong\n");
                e.printStackTrace();
            }
        }
        else {
            logger.warn("No issues found to add or remove");
        }
    }

    /**
     * @param token the JIRA token for authorization
     * @param projectKey the JIRA project key (e.g. SCA)
     * @param component the JIRA component to associate the test plan to (optional)
     * @param fixVersion the JIRA fixVersion to associate the test plan to (optional)
     * @return the JIRA id of the created test plan
     */
    public String createTestPlan(String token, String projectKey, String component, String fixVersion) {

        String summary = projectKey + " Test Plan";
        if (!skipCharacters.contains(component)) {
            summary = summary + "   for " + component;
        }

        DocumentContext inputData = JsonPath.parse("{\n"
                + "                    \"fields\": {\n"
                + "                       \"project\":\n"
                + "                       {\n"
                + "                         \"key\": \"" + projectKey + "\"\n"
                + "                        },\n"
                + "                       \"summary\": \"" + summary + "\",\n"
                + "                       \"description\": \"" + summary + "\",\n"
//                + "                       \"components\":[{\"name\":\"" + component + "\"}],\n"
//                + "                       \"fixVersions\":[{\"name\":\"" + fixVersion + "\"}],\n"
//                + "                        \"customfield_10616\": {\n"
//                + "                             \"value\": \"Waterloo\"\n"
//                + "                         },\n"
                + "                       \"issuetype\": {\n"
                + "                          \"name\": \"Test Plan\"\n"
                + "                       }\n"
                + "                   }\n"
                + "                }");

        logger.info(inputData.jsonString());

        if (!skipCharacters.contains(component)) {
            logger.error("ERERfdgfgfdgdfgfdg");
            JSONArray array = new JSONArray();
            LinkedHashMap<String, String> componentObject = new LinkedHashMap<>();
            componentObject.put("name",component);
            array.add(componentObject);
            inputData.put("fields", "components", array);
        }
        if (!skipCharacters.contains(fixVersion)) {
            logger.error("ERERfdgfgfdgdfgfdg");
            JSONArray array = new JSONArray();
            LinkedHashMap<String, String> versionObject = new LinkedHashMap<>();
            versionObject.put("name",fixVersion);
            array.add(versionObject);
            inputData.put("fields", "fixVersions", array);
        }

        String endpoint = "/rest/api/2/issue/";
//        String endpoint = baseEndpoint;

        Response response = given()
                .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                .redirects().follow(false)
                .headers(getHeaders(token, "application/json"))
                .body(inputData.jsonString())
                .post(jiraURL + endpoint);

        DocumentContext responseData = JsonPath.parse(response.prettyPrint());
        String issueKey = responseData.read("key").toString().replace("[","").replace("]","");

        try {
            logger.info("\ninfo: Creating Test Plan\n");

            BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

            while ((output = br.readLine()) != null) {
                logger.info(output);
            }

            logger.info("exit: " + response.getStatusCode());

        } catch (Throwable e) {
            logger.error("\nerror: Something went wrong creating test plan\n");
            e.printStackTrace();
        }

        return issueKey;
    }

    /**
     * @param token the JIRA token for authorization
     * @param projectKey the JIRA project key (e.g. SCA)
     * @param component the JIRA component to associate the test set to (optional)
     * @param fixVersion the JIRA fixVersion to associate the test set to (optional)
     * @return the JIRA id of the created test set
     */
    public String createTestSet(String token, String projectKey, String component, String fixVersion) {

        String summary = projectKey + " Test Set";
        if (!skipCharacters.contains(component)) {
            summary = summary + "   for " + component;
        }

        DocumentContext inputData = JsonPath.parse("{\n"
                + "                    \"fields\": {\n"
                + "                       \"project\":\n"
                + "                       {\n"
                + "                         \"key\": \"" + projectKey + "\"\n"
                + "                        },\n"
                + "                       \"summary\": \"" + summary + "\",\n"
                + "                       \"description\": \"" + summary + "\",\n"
                + "                       \"issuetype\": {\n"
                + "                          \"name\": \"Test Set\"\n"
                + "                       }\n"
                + "                   }\n"
                + "                }");

        if (!skipCharacters.contains(component)) {
            JSONArray array = new JSONArray();
            LinkedHashMap<String, String> componentObject = new LinkedHashMap<>();
            componentObject.put("name",component);
            array.add(componentObject);
            inputData.put("fields", "components", array);
        }
        if (!skipCharacters.contains(fixVersion)) {
            JSONArray array = new JSONArray();
            LinkedHashMap<String, String> versionObject = new LinkedHashMap<>();
            versionObject.put("name",fixVersion);
            array.add(versionObject);
            inputData.put("fields", "fixVersions", array);
        }

        String endpoint = "/rest/api/2/issue/";

        Response response = given()
                .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                .redirects().follow(false)
                .headers(getHeaders(token, "application/json"))
                .body(inputData.jsonString())
                .post(jiraURL + endpoint);

        DocumentContext responseData = JsonPath.parse(response.prettyPrint());
        String issueKey = responseData.read("key").toString().replace("[","").replace("]","");

        try {
            logger.info("\ninfo: Creating Test Set\n");

            BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

            while ((output = br.readLine()) != null) {
                logger.info(output);
            }

            logger.info("exit: " + response.getStatusCode());

        } catch (Throwable e) {
            logger.error("\nerror: Something went wrong creating test set\n");
            e.printStackTrace();
        }

        return issueKey;
    }

    /**
     * For checking a json cucumber results file meets the pass threshold
     * @param threshold the pass threshold to evaluate
     * @param resultsPath path of results json file to evaluate
     * @return true if the result meets the threshold
     */
    public boolean thresholdEvaluate(int threshold, String resultsPath) {

        double pass = 0.0;
        double notPass = 0.0;
        double fail = 0.0;
        double scenarios = 0.0;

        if (new File(resultsPath).isDirectory()) {

            File[] directoryListing = new File(resultsPath).listFiles();
            for (File child : directoryListing) {

                if (child.getName().contains(".json") && !child.getName().contains("-REM")) {

                    logger.info(child.getName());

                    try {
                        final String json = new String(Files.readAllBytes(Paths.get(child.getAbsolutePath())));
                        documentContext = JsonPath.parse(json);
                    } catch (Exception e) {
                        logger.error("Error reading json results file");
                    }
                    JSONArray passed = documentContext.read("$[*].elements[*]..result[?(@.status=='passed')].status");
                    pass = pass + passed.size();

                    JSONArray notPassed = documentContext.read("$[*].elements[*]..result[?(@.status!='passed')].status");
                    notPass = notPass + notPassed.size();

                    JSONArray scenariosArray = documentContext.read("$[*].elements[*].[?(@.type!='background')].type");
                    scenarios = scenarios + scenariosArray.size();

                    JSONArray failed = documentContext.read("$[*].elements[*]..result[?(@.status=='failed')].status");
                    fail = fail + failed.size();
                }
            }
        } else {

            try {
                final String json = new String(Files.readAllBytes(Paths.get(resultsPath)));
                documentContext = JsonPath.parse(json);
            } catch (Exception e) {
                logger.error("Error reading json results file");
            }
            JSONArray passed = documentContext.read("$[*].elements[*]..result[?(@.status=='passed')].status");
            pass = pass + passed.size();

            JSONArray notPassed = documentContext.read("$[*].elements[*]..result[?(@.status!='passed')].status");
            notPass = notPass + notPassed.size();

            JSONArray scenariosArray = documentContext.read("$[*].elements[*].[?(@.type!='background')].type");
            scenarios = scenarios + scenariosArray.size();

            JSONArray failed = documentContext.read("$[*].elements[*]..result[?(@.status=='failed')].status");
            fail = fail + failed.size();
        }

        double percent = pass / (pass + notPass);
        percent *= 100;

        logger.info("OLD CALC" + percent);

        percent = (scenarios - fail) / (scenarios);
        percent *= 100;
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        percent = Double.valueOf(twoDForm.format(percent));

        if (percent >= threshold) {
            logger.info ("SUCCESS : Pass % " + percent + "% is greater than threshold " + threshold + "%");
            return true;
        } else {
            logger.info ("FAILED : Pass % " + percent + "% is less than threshold " + threshold + "%");
            return false;
        }
    }

    /**
     * For removing embeds from a cucumber json file to reduce the file size and help with import to Xray
     * @param jsonFile path of results json file to evaluate
     * @return path of updated file with removed embeds
     */
    public String deleteEmbeds(String jsonFile) {

        try {
            final String json = new String(Files.readAllBytes(Paths.get(jsonFile)));
            documentContext = JsonPath.parse(json);
        }
        catch (Exception e){
            logger.error("Error reading json results file");
        }

        documentContext.delete("$[*].elements[*].steps[*].embeddings");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile.replace(".json", "-REM.json")));
            writer.write(documentContext.jsonString());

            writer.close();
        } catch (Exception e) {
            logger.error("Failed to write file");
        }

        return jsonFile.replace(".json", "-REM.json");
    }

    /**
     * A function for creating a Bug in JIRA and linking to a failed test scenario
     * This will first search JIRA for existing issues to make sure it doesn't already exist
     * @param token the JIRA token for authorization
     * @param projectKey the JIRA project Key
     * @param summary a summary text to give the JIRA issue
     * @param description a full description to give the JIRA issue
     * @param component the JIRA component to associate with the JIRA issue
     * @param affectsVersion the JIRA affectsVersion to associate with the JIRA issue
     */
    public String createZapDefect(String token, String projectKey, String summary, String description, String component, String affectsVersion, String severity, String priority, String buildUrl, String branchName) {

        String jql = "project = " + projectKey + " AND issuetype = Bug AND component = \"" + component + "\" and summary ~ \"" + summary + "\" AND (status not in  (\"Release Backlog\", Closed) OR status in  (\"Release Backlog\", Closed) AND resolution not in (Done, Fixed))";
        DocumentContext searchResults = searchForJiraIssues(token, jql);
        logger.info(searchResults.read("$").toString());
        logger.info(searchResults.read("total").toString());
        int numberOfResults = Integer.parseInt(searchResults.read("total").toString());
        logger.info(Integer.toString(numberOfResults));

        if (numberOfResults == 0) {

            jql = "project = " + projectKey + " AND issuetype = Bug and summary ~ \"" + summary + "\" and status not in  (\"Release Backlog\", Closed)";
            searchResults = searchForJiraIssues(token, jql);
            logger.info(searchResults.read("$").toString());
            logger.info(searchResults.read("total").toString());
            numberOfResults = Integer.parseInt(searchResults.read("total").toString());
            logger.info(Integer.toString(numberOfResults));

            if (numberOfResults == 0) {

                String inputData = "{\n"
                        + "                    \"fields\": {\n"
                        + "                       \"project\":\n"
                        + "                       {\n"
                        + "                         \"key\": \"" + projectKey + "\"\n"
                        + "                        },\n"
                        + "                       \"summary\": \"" + summary + "\",\n"
                        + "                       \"description\": \"" + description + "\",\n"
                        + "                       \"components\":[{\"name\":\"" + component + "\"}],\n"
                        + "                       \"versions\":[{\"name\":\"" + affectsVersion + "\"}],\n"
                        + "                       \"customfield_10311\": {\n"
                        + "                          \"value\": \"" + severity + "\"\n"
                        + "                       },\n"
                        + "                       \"priority\": {\n"
                        + "                          \"name\": \"" + priority + "\"\n"
                        + "                       },\n"
                        + "                       \"customfield_11931\":[{\"value\":\"All Brands\"}],\n"
                        + "                       \"customfield_14012\": {\n"
                        + "                          \"value\": \"SCA Dev (Sandbox)\",\n"
                        + "                          \"child\": {\n"
                        + "                             \"value\": \"Release\"\n"
                        + "                         }\n"
                        + "                       },\n"
                        + "                       \"labels\": [\"CI_SECURITY\",\"CI_REVIEW\",\"ZAP\"],\n"
                        + "                       \"customfield_13802\": {\n"
                        + "                         \"value\": \"WebInspect\"\n"
                        + "                         },\n"
                        + "                       \"customfield_10109\": {\n"
                        + "                         \"value\": \"Internal\"\n"
                        + "                         },\n"
                        + "                        \"customfield_10423\": {\n"
                        + "                             \"value\": \"SCA\"\n"
                        + "                         },\n"
                        + "                        \"customfield_10616\": {\n"
                        + "                             \"value\": \"Waterloo\"\n"
                        + "                         },\n"
                        + "                       \"issuetype\": {\n"
                        + "                          \"name\": \"Bug\"\n"
                        + "                       }\n"
                        + "                   }\n"
                        + "                }";

                String endpoint = "/rest/api/2/issue/";

                Response response = given()
                        .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                        .redirects().follow(false)
                        .headers(getHeaders(token, "application/json"))
                        .body(inputData)
                        .post(jiraURL + endpoint);

                DocumentContext defectResponse = JsonPath.parse(response.prettyPrint());
                String defectId = defectResponse.read("key");

                try {
                    logger.info("\ninfo: Creating defect\n");

                    BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

                    while ((output = br.readLine()) != null) {
                        logger.info(output);
                    }

                    logger.info("exit: " + response.getStatusCode());

                } catch (Throwable e) {
                    logger.error("\nerror: Something went wrong creating defect\n");
                    e.printStackTrace();
                }

                postCommentToIssue(token, "New issue identified during ZAP scan for " + component + " in "+branchName+" build : " + buildUrl, defectId);

                return defectId;

            } else {

                String issueKey = searchResults.read("issues[0].key");
                logger.info("Found existing issue : " + issueKey + " adding " + component);
                updateIssue(token, projectKey, issueKey, component, "x","x", "x", "x");
                postCommentToIssue(token, "Existing issue identified during ZAP scan for " + component + " in "+branchName+" build : " + buildUrl, issueKey);
                return issueKey;
            }

        } else {
            logger.info("Already raised");
            return "";
        }
    }


    /**
     * A function for creating a Bug in JIRA and linking to a failed test scenario
     * This will first search JIRA for existing issues to make sure it doesn't already exist
     * @param token the JIRA token for authorization
     * @param projectKey the JIRA project Key
     * @param summary a summary text to give the JIRA issue
     * @param description a full description to give the JIRA issue
     * @param component the JIRA component to associate with the JIRA issue
     * @param affectsVersion the JIRA affectsVersion to associate with the JIRA issue
     */
    public String createNexusIqDefect(String token, String projectKey, String summary, String description, String component, String affectsVersion, String severity, String priority, String buildUrl, String branchName, String packagePath) {

        String jql = "project = " + projectKey + " AND issuetype = Bug AND component = \"" + component + "\" AND summary ~ \"NexusIQ AND "+packagePath+"\" AND (status not in  (\"Release Backlog\", Closed) OR status in  (\"Release Backlog\", Closed) AND resolution not in (Done, Fixed))";
        System.out.println(jql);
        DocumentContext searchResults = searchForJiraIssues(token, jql);
        logger.info(searchResults.read("$").toString());
        logger.info(searchResults.read("total").toString());
        int numberOfResults = Integer.parseInt(searchResults.read("total").toString());
        logger.info(Integer.toString(numberOfResults));

        if (numberOfResults == 0) {

            jql = "project = " + projectKey + " AND issuetype = Bug AND summary ~ \"NexusIQ AND "+packagePath+"\" AND status not in  (\"Release Backlog\", Closed)";
            System.out.println(jql);
            searchResults = searchForJiraIssues(token, jql);

            logger.info(searchResults.read("$").toString());
            logger.info(searchResults.read("total").toString());
            numberOfResults = Integer.parseInt(searchResults.read("total").toString());
            System.out.println(numberOfResults);
            logger.info(Integer.toString(numberOfResults));

            if (numberOfResults == 0) {

                System.out.println("CREATE NEW DEFECT SUMMARY : " + summary + " DECRIPTION : " + description);

//                String inputData = "{\n"
//                        + "                    \"fields\": {\n"
//                        + "                       \"project\":\n"
//                        + "                       {\n"
//                        + "                         \"key\": \"" + projectKey + "\"\n"
//                        + "                        },\n"
//                        + "                       \"summary\": \"" + summary + "\",\n"
//                        + "                       \"description\": \"" + description + "\",\n"
//                        + "                       \"components\":[{\"name\":\"" + component + "\"}],\n"
//                        + "                       \"versions\":[{\"name\":\"" + affectsVersion + "\"}],\n"
//                        + "                       \"customfield_10311\": {\n"
//                        + "                          \"value\": \"" + severity + "\"\n"
//                        + "                       },\n"
//                        + "                       \"priority\": {\n"
//                        + "                          \"name\": \"" + priority + "\"\n"
//                        + "                       },\n"
//                        + "                       \"customfield_11931\":[{\"value\":\"All Brands\"}],\n"
//                        + "                       \"customfield_14012\": {\n"
//                        + "                          \"value\": \"SCA Dev (Sandbox)\",\n"
//                        + "                          \"child\": {\n"
//                        + "                             \"value\": \"Release\"\n"
//                        + "                         }\n"
//                        + "                       },\n"
//                        + "                       \"customfield_13802\": {\n"
//                        + "                         \"value\": \"Sonatype\"\n"
//                        + "                         },\n"
//                        + "                       \"customfield_10109\": {\n"
//                        + "                         \"value\": \"Internal\"\n"
//                        + "                         },\n"
//                        + "                        \"customfield_10423\": {\n"
//                        + "                             \"value\": \"SCA\"\n"
//                        + "                         },\n"
//                        + "                       \"labels\": [\"CI_SECURITY\",\"CI_REVIEW\",\"ZAP\"],\n"
//                        + "                        \"customfield_10616\": {\n"
//                        + "                             \"value\": \"Waterloo\"\n"
//                        + "                         },\n"
//                        + "                       \"issuetype\": {\n"
//                        + "                          \"name\": \"Bug\"\n"
//                        + "                       }\n"
//                        + "                   }\n"
//                        + "                }";
//
//                String endpoint = "/rest/api/2/issue/";
//
//                Response response = given()
//                        .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
//                        .redirects().follow(false)
//                        .headers(getHeaders(token, "application/json"))
//                        .body(inputData)
//                        .post(jiraURL + endpoint);
//
//                DocumentContext defectResponse = JsonPath.parse(response.prettyPrint());
//                String defectId = defectResponse.read("key");
//
//                try {
//                    logger.info("\ninfo: Creating defect\n");
//
//                    BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));
//
//                    while ((output = br.readLine()) != null) {
//                        logger.info(output);
//                    }
//
//                    logger.info("exit: " + response.getStatusCode());
//
//                } catch (Throwable e) {
//                    logger.error("\nerror: Something went wrong creating defect\n");
//                    e.printStackTrace();
//                }

                //postCommentToIssue(token, "New issue identified during NexusIQ scan for " + component + " in "+branchName+" build : " + buildUrl, defectId);

                return "";

            } else {

                String issueKey = searchResults.read("issues[0].key");

                logger.info("Found existing issue : " + issueKey + " adding " + component);
                updateIssue(token, projectKey, issueKey, component, "x","x", "x", "x");
                postCommentToIssue(token, "Existing issue identified during NexusIQ scan for " + component + " in "+branchName+" build : " + buildUrl + " " + description, issueKey);
                return issueKey;
            }

        } else {
            logger.info("Already raised");
            return "";
        }
    }


    /**
     * A function to get a page id for a confluence page by title
     * @param token the Confluence token for authorization
     * @param title the page title to search for
     * @param confSpace the confluence space to search in
     */
    public String getPageId(String token, String title, String confSpace) {

        Map<String, String> params = new HashMap<>();
        params.put("title",title);
        params.put("spaceKey",confSpace);

        int tries = 0;

        tries_loop:
        while (tries < 3) {

            Response response = given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                    .redirects().follow(false)
                    .headers(getHeaders(token, "application/json"))
                    .params(params)
                    .get(confluenceUrl);

            if (response.statusCode() == 200) {
                DocumentContext responseJson = JsonPath.parse(response.prettyPrint());
                return responseJson.read("results[0].id");
            } else {
                tries++;

                logger.error("\nerror: Something went wrong getting version number\n");
                logger.error(response.then().log().toString());
                logger.error(response.then().log().all().toString());
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
            }
        }
        throw new NullPointerException("Could not get version number");
    }


    /**
     * A function to get a page id for a confluence page by title
     * @param token the Confluence token for authorization
     * @param pageId the pageId to get version for
     */
    public int getCurrentPageVersion(String token, String pageId) {

        int tries = 0;

        tries_loop:
        while (tries < 3) {

            Response response = given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                    .redirects().follow(false)
                    .headers(getHeaders(token, "application/json"))
                    .get(confluenceUrl + pageId);

            if (response.statusCode() == 200) {
                DocumentContext responseJson = JsonPath.parse(response.prettyPrint());
                return responseJson.read("version.number");
            } else {
                tries++;

                logger.error("\nerror: Something went wrong getting version number\n");
                logger.error(response.then().log().toString());
                logger.error(response.then().log().all().toString());
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
            }
        }
        throw new NullPointerException("Could not get version number");
    }


    /**
     * A function to get a page id for a confluence page by title
     * @param confUsername the Confluence username for authorization
     * @param confPassword the Confluence password for authorization
     * @param pageId the pageId to get page content for
     */
    public String getPageContent(String token, String pageId) {

        Map<String, String> params = new HashMap<>();
        params.put("expand","body.storage");

        int tries = 0;

        tries_loop:
        while (tries < 3) {

            Response response = given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                    .redirects().follow(false)
                    .headers(getHeaders(token, "application/json"))
                    .params(params)
                    .get(confluenceUrl + pageId);

            if (response.statusCode() == 200) {
                DocumentContext responseJson = JsonPath.parse(response.prettyPrint());
                return responseJson.read("body.storage.value").toString();
            } else {
                tries++;

                logger.error("\nerror: Something went wrong uploading to confluence\n");
                logger.error(response.then().log().toString());
                logger.error(response.then().log().all().toString());
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
            }
        }
        throw new NullPointerException("Could not get page content");
    }


    /**
     * A function to get a page id for a confluence page by title
     * @param confUsername the Confluence username for authorization
     * @param confPassword the Confluence password for authorization
     * @param pageId the pageId to update page content for
     * @param pageContent the pageId to update page content for
     * @param pageTitle the confluence page title
     * @param versionNumber the version number of the page
     */
    public boolean updatePageContent(String token, String pageId, String pageContent, String pageTitle, int versionNumber) {

        String body = "{\n"
                + "    \"id\": \""+pageId+"\",\n"
                + "    \"type\": \"page\",\n"
                + "    \"title\": \""+pageTitle+"\",\n"
                + "    \"body\": {\n"
                + "        \"storage\": {\n"
                + "            \"value\": \""+pageContent+"\",\n"
                + "            \"representation\": \"storage\"\n"
                + "        }\n"
                + "    },\n"
                + "    \"version\": {\n"
                + "        \"number\": "+versionNumber+"\n"
                + "    }\n"
                + "}";

        int tries = 0;

        tries_loop:
        while (tries < 3) {

            Response response = given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                    .redirects().follow(false)
                    .headers(getHeaders(token, "application/json"))
                    .body(body)
                    .put(confluenceUrl + pageId);

            if (response.statusCode() == 200) {
                DocumentContext responseJson = JsonPath.parse(response.prettyPrint());
                return true;
            } else {
                tries++;



                logger.error("\nerror: Something went wrong uploading to confluence\n");
                logger.error(response.then().log().toString());
                logger.error(response.then().log().all().toString());
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
            }
        }
        throw new NullPointerException("Could not update page content");
    }

    /**
     * A function to get a page id for a confluence page by title
     * @param token the Confluence token for authorization
     * @param pageId the pageId to update page content for
     * @param attachment the attachment as byte[]
     * @param filename the filename
     * @param mimetype the mimetype of the file
     * @param comment comment to associate with the upload
     */
    public boolean uploadAttachment(String token, String pageId, byte[] attachment, String filename, String mimetype, String comment) {

        String attachmentId = isAttachmentPresentOnPage(token, pageId, filename);

        if (attachmentId.equals("")) {

            int tries = 0;

            tries_loop:
            while (tries < 3) {

                Response response = given()
                        .log().all()
                        .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                        .redirects().follow(false)
                        .headers(getHeaders(token, "multipart/form-data"))
                        .multiPart("file", filename, attachment, mimetype)
                        .multiPart("comment", comment)
                        .post(confluenceUrl + pageId + "/child/attachment");

                response.then().log().all();

                if (response.statusCode() == 200) {

                    try {
                        logger.info("\ninfo: Attaching file to page " + pageId + "\n");

                        BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

                        while ((output = br.readLine()) != null) {
                            logger.info(output);
                        }

                        logger.info("exit: " + response.getStatusCode());

                    } catch (Throwable e) {
                        logger.error("\nerror: Something went wrong attaching file to issue " + pageId + "n");
                        e.printStackTrace();
                    }

                    return true;

                } else {

                    tries++;

                    logger.error("\nerror: Something went wrong uploading to confluence\n");
                    logger.error(response.then().log().toString());
                    logger.error(response.then().log().all().toString());
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {

                    }
                }
            }


        } else {

            int tries = 0;

            tries_loop:
            while (tries < 3) {

                Response response = given()
                        .log().all()
                        .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                        .redirects().follow(false)
                        .headers(getHeaders(token, "multipart/form-data"))
                        .multiPart("file", filename, attachment, mimetype)
                        .multiPart("comment", comment)
                        .multiPart("minorEdit", "false")
                        .post(confluenceUrl + pageId + "/child/attachment/"+attachmentId+"/data");

                response.then().log().all();

                if (response.statusCode() == 200) {

                    try {
                        logger.info("\ninfo: Attaching file to page " + pageId + "\n");

                        BufferedReader br = new BufferedReader(new InputStreamReader(response.asInputStream()));

                        while ((output = br.readLine()) != null) {
                            logger.info(output);
                        }

                        logger.info("exit: " + response.getStatusCode());

                    } catch (Throwable e) {
                        logger.error("\nerror: Something went wrong attaching file to issue " + pageId + "n");
                        e.printStackTrace();
                    }

                    return true;

                } else {

                    tries++;

                    logger.error("\nerror: Something went wrong uploading to confluence\n");
                    logger.error(response.then().log().toString());
                    logger.error(response.then().log().all().toString());
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {

                    }
                }
            }
        }
        throw new NullPointerException("Could not get page content");
    }



    /**
     * A function to get a page id for a confluence page by title
     * @param token the Confluence token for authorization
     * @param pageTitle the pageId to update page content for
     * @param filePath the pageId to update page content for
     * @param confSpace conf space to upload to
     * @param comment a comment to tag the upload with
     */
    public void uploadAttachmentsFromPath(String token, String pageTitle, String filePath, String confSpace, String comment) {

        String pageId = getPageId(token, pageTitle, confSpace);

        if (new File(filePath).isDirectory()) {

            File[] directoryListing = new File(filePath).listFiles();

            for (File child : directoryListing) {
                if (uploadFile(token, child.getPath(), pageId, comment)) {
                    logger.info("Successfully uploaded : " + filePath);
                } else {
                    logger.error("Failed to upload : " + filePath);
                }
            }


            } else {
            if (uploadFile(token, filePath, pageId, comment)) {
                logger.info("Successfully uploaded : " + filePath);
            } else {
                logger.error("Failed to upload : " + filePath);
            }
        }
    }


    private boolean uploadFile(String token, String filePath, String pageId, String comment) {
        File upload = new File(filePath);
        try {
            byte[] bFile = Files.readAllBytes(Paths.get(filePath));
            return uploadAttachment(token, pageId, bFile, upload.getName(),Files.probeContentType(Paths.get(filePath)), comment);
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * A function to get a page id for a confluence page by title
     * @param token the Confluence token for authorization
     * @param pageId the pageId to get page content for
     * @param attachmentTitle the title for the attachment to download
     */
    public String isAttachmentPresentOnPage(String token, String pageId, String attachmentTitle) {
        
        int tries = 0;

        tries_loop:
        while (tries < 3) {

            Response response = given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                    .redirects().follow(false)
                    .headers(getHeaders(token, "application/json"))
                    .get(confluenceUrl + pageId + "/child/attachment");

            if (response.statusCode() == 200) {

                DocumentContext responseJson = JsonPath.parse(response.prettyPrint());

                JSONArray downloads = responseJson.read("results");

                attachment_loop:
                for (int i = 0; i < downloads.size(); i++) {
                    LinkedHashMap<String, Object> attachment = (LinkedHashMap) downloads.get(i);

                    if (attachment.get("title").toString().contains(attachmentTitle)) {
                        return attachment.get("id").toString();
                    }
                }

                return "";

            } else {

                tries++;

                logger.error("\nerror: Something went wrong getting page attachment list\n");
                logger.error(response.then().log().toString());
                logger.error(response.then().log().all().toString());
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
            }
        }
        return "";
    }

    /**
     * A function to get a page id for a confluence page by title
     * @param token the Confluence token for authorization
     * @param pageId the pageId to get page content for
     * @param attachmentTitle the title for the attachment to download
     */
    public String getConfluenceDownloadUrl(String token, String pageId, String attachmentTitle, String targetLocation) {

        int tries = 0;

        tries_loop:
        while (tries < 3) {

            Response response = given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                    .redirects().follow(false)
                    .headers(getHeaders(token, "application/json"))
                    .get(confluenceUrl + pageId + "/child/attachment");

            if (response.statusCode() == 200) {

                DocumentContext responseJson = JsonPath.parse(response.prettyPrint());

                JSONArray downloads = responseJson.read("results");

                attachment_loop:
                for (int i = 0; i < downloads.size(); i++) {
                    LinkedHashMap<String, Object> attachment = (LinkedHashMap) downloads.get(i);

                    if (attachment.get("title").toString().contains(attachmentTitle)) {

                        LinkedHashMap<String, Object> links = (LinkedHashMap)attachment.get("_links");
                        try {
                            InputStream in = new URL(confluenceUrl + links.get("download").toString()).openStream();
                            Files.copy(in, Paths.get(targetLocation + "/"+ attachment.get("title")), StandardCopyOption.REPLACE_EXISTING);
                        } catch (Exception e) {
                            logger.error("Could not download file");
                        }

                        return confluenceUrl + links.get("download").toString();
                    }
                }

                throw new NullPointerException("Attachment not found matching " + attachmentTitle);

            } else {

                tries++;

                logger.error("\nerror: Something went wrong uploading to confluence\n");
                logger.error(response.then().log().toString());
                logger.error(response.then().log().all().toString());
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
            }
        }
        throw new NullPointerException("Could not get page content");
    }
    
    /**
     * This function is for updating jira ticjket status
     * @param token the JIRA token for authorization
     * @param jiraTicket whose status needs to be updated
     * @param newstatus which is transition id for new status
     * @param resolution is the resolution if needed to update as well with status
     */
    public void updateTicketStatus(String token, String jiraTicket, String newstatus, String resolution) {
    	
        
        String inputData = null;
        if(skipCharacters.contains(resolution)) {
        	logger.info("Updating jira ticket status to :" +newstatus);
        	inputData = "{\n"
                    + "                       \"transition\":\n"
                    + "                       {\n"
                    + "                         \"id\": \"" + newstatus + "\"\n"
                    + "                        }\n"
                    + "                }";
        }
        else {
        	logger.info("Updating jira ticket status to: "+newstatus +" with resolution: "+resolution);
        	inputData = 				"{\n"
                    + "                       \"transition\":\n"
                    + "                       {\n"
                    + "                         \"id\": \"" + newstatus + "\"\n"
                    + "                        },\n"
					+ "                    \"fields\": {\n"
                    + "                       \"resolution\":\n"
                    + "                       {\n"
                    + "                         \"name\": \"" + resolution + "\"\n"
                    + "                        }\n"
                    + "                        }\n"
                    + "                }";
        }

            String endpoint = "/rest/api/2/issue/"+jiraTicket+"/transitions";

            Response response = given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                    .redirects().follow(false)
                    .headers(getHeaders(token, "application/json"))
                    .body(inputData)
                    .post(jiraURL + endpoint);
					
            logger.info("Response ==>> " +response.statusCode());
            if(!(response.statusCode() == 204)) {
            	if(response.statusCode() == 400) {
            		logger.error("Provided inputs for status transition id is not correct. Please validate correct transition id is used based on applicable next status for the ticket");
            		logger.info("Refer below response to check for applicable transitions id for this issue based on current status");
            		endpoint = "/rest/api/2/issue/"+jiraTicket+"/transitions?expand=transitions.fields";
                
            		Response response1 = given()
                        .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                        .redirects().follow(false)
                        .headers(getHeaders(token, "application/json"))
                        .get(jiraURL + endpoint);
    					
                logger.info("Response ==>> " +response1.asString());
            	}
            }
            else 
            	logger.info("Status successfully updated for jira ticket: "+jiraTicket);
    }

    public String getFriendlyBranchName(String branchName) {
        if (branchName.contains("release01.1")) {
            return "release01-1";
        } else if (branchName.contains("release01")) {
            return "release01";
        } else if (branchName.contains("release04")) {
            return "release04";
        } else if (branchName.contains("release03")) {
            return "release03";
        } else if (branchName.contains("master")) {
            return "master";
        } else if (branchName.contains("rc")) {
            return "master";
        } else if (branchName.contains("PR")) {
            return "master";
        } else if (branchName.contains("pr")) {
            return "master";
        } else{
            return "N/A";
        }
    }


    public String getZapTag(String branchName) {
        if (branchName.contains("release01")) {
            return "zaprelease01";
        } else if (branchName.contains("release03")) {
            return "zaprelease03";
        } else if (branchName.contains("release04")) {
            return "zaprelease04";
        } else if (branchName.contains("master")) {
            return "zapmaster";
        } else if (branchName.contains("rc")) {
            return "zapmaster";
        } else{
            return "DONOTFINDMEEVER";
        }
    }

    public String getGatlingTag(String branchName) {
        if (branchName.contains("release01")) {
            return "gatlingrelease01";
        }  else if (branchName.contains("release03")) {
            return "gatlingrelease03";
        } else if (branchName.contains("release04")) {
            return "gatlingrelease04";
        } else if (branchName.contains("master")) {
            return "gatlingmaster";
        } else if (branchName.contains("rc")) {
            return "gatlingmaster";
        } else if (branchName.contains("PR")) {
            return "gatlingmaster";
        } else if (branchName.contains("pr")) {
            return "gatlingmaster";
        } else{
            return "DONOTFINDMEEVER";
        }
    }

}

