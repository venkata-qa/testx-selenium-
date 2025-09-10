package xray;

import org.assertj.core.util.Lists;
import org.springframework.boot.SpringApplication;
import xray.xrayhelper.ImportFeaturesAndTestResults;

import java.util.ArrayList;


/**
 * Main execution class for triggering Xray Helper Utility functions
 * Provides users of JIRA Xray plugin with an easy way to import and export scenarios
 * @author Rahul Jain
 */
public class XrayHelper {
    /**
     * An ENUM to dictating the list of available functions in the Xray utility
     */
    public enum Command {
        EXPORT_FEATURE_FILES("exportFeatureFiles"),
        EXPORT_FEATURE_FILES_RENAME("exportFeatureFilesRename"),
        IMPORT_FEATURE_FILES("importFeatureFiles"),
        IMPORT_EXECUTION_RESULTS("importExecutionResults"),
        IMPORT_EXECUTION_RESULTS_TO_EXECUTION("importExecutionResultsToExecution"),
        CREATE_DEFECT("createDefect"),
        CREATE_DEFECTS_FOR_ALL_FAILURES("createAllDefects"),
        CREATE_DEFECT_WITH_TEMPLATE("createDefectWithTemplate"),
        SEARCH_FOR_ISSUES("search"),
        CREATE_EXECUTIONS("createExecutions"),
        CREATE_TEST_PLAN("createTestPlan"),
        ADD_REMOVE_TESTS_TO_TEST_PLAN("addRemoveTestsToTestPlan"),
        CREATE_TEST_SET("createTestSet"),
        ADD_REMOVE_TESTS_TO_TEST_SET("addRemoveTestsToTestSet"),
        CREATE_FOLDER("createFolder"),
        ADD_REMOVE_TESTS_TO_FOLDER("addRemoveTestsToFolder"),
        ADD_REMOVE_TESTS_TO_PRECONDITION("addRemoveTestsToPreCondition"),
        GET_ISSUE_LINKS("getIssueLinks"),
        CREATE_ISSUE_LINKS("createIssueLinks"),
        RESET_ISSUE_LINKS("resetIssueLinks"),
        THRESHOLD_EVALUATE("evaluateThreshold"),
        GET_ISSUE_ATTRIBUTE("getIssueAttribute"),
        UPLOAD_ATTACHMENTS("uploadAttachments"),
        UPDATE_STATUS("updateTicketStatus"),
        CREATE_API_TOKEN("createTokenWithAPIKey");

        private String name;


        Command(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * main method for execution of Xray helper utility functions
     * @param args  <a brief description of the param1>
     * @return  void
     * @throws  <any exceptions that this method throws>
     */
    public static void main(String[] args) {
        SpringApplication.run(XrayHelper.class, args);
        ImportFeaturesAndTestResults xrayHelper = new ImportFeaturesAndTestResults();
        ArrayList<String> arguments = Lists.newArrayList(args);
        System.out.println(arguments);
        String skipCharacter = "X";
        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("No command specified. Use one of " + getCommandNames());
        }

        String action = arguments.get(0);
        String token = arguments.get(1);

        if (Command.EXPORT_FEATURE_FILES.getName().equals(action)) {

            String testPlanList = arguments.get(3);
            String outputPath = arguments.get(4);
            int retries = Integer.parseInt(arguments.get(5));
            if (xrayHelper.exportFeatureFiles(token, testPlanList, outputPath, retries)) {
                System.exit(0);
            } else {
                System.exit(1);
            }

        }else if (Command.EXPORT_FEATURE_FILES_RENAME.getName().equals(action)) {

            String testPlanList = arguments.get(3);
            String outputPath = arguments.get(4);
            int retries = Integer.parseInt(arguments.get(5));
            String filename = arguments.get(6);
            if (xrayHelper.exportFeatureFilesRename(token, testPlanList, outputPath, retries, filename)) {
                System.exit(0);
            } else {
                System.exit(1);
            }

        } else if (Command.IMPORT_FEATURE_FILES.getName().equals(action)) {

            String pathToFeatureFile = arguments.get(3);
            String projectKey = arguments.get(4);
            String parentKey = skipCharacter;
            String component = skipCharacter;
            String fixVersion = skipCharacter;
            String targetStatus = skipCharacter;
            String repoPath  = skipCharacter;
            String testPlan  = skipCharacter;
            String testSet  = skipCharacter;
            if (arguments.size() > 5) {
                parentKey = arguments.get(5);
            }
            // update the component field if component provided
            if (arguments.size() > 6) {
                component = arguments.get(6);
            }
            if (arguments.size() > 7) {
                fixVersion = arguments.get(7);
            }
            // transition the issue if target status argument provided
            if (arguments.size() > 8) {
                targetStatus = arguments.get(8);
            }
            // populate the repository path if argument provided
            if (arguments.size() > 9) {
                repoPath = arguments.get(9);
            }
            // link to a set plan if provided (use NEW to create)
            if (arguments.size() > 10) {
                testPlan = arguments.get(10);
            }
            // link to a set set if provided (use NEW to create)
            if (arguments.size() > 11) {
                testSet = arguments.get(11);
            }

            xrayHelper.importFeatureJIRA(token, pathToFeatureFile, projectKey, parentKey, component, fixVersion, targetStatus, repoPath, testPlan, testSet);
            System.exit(0);

        } else if (Command.IMPORT_EXECUTION_RESULTS_TO_EXECUTION.getName().equals(action)) {

            String jsonResultsFilePath = arguments.get(3);
            String existingIssueKey = arguments.get(4);
            boolean removeEmbeds = false;

            if (arguments.size()>5) {
                removeEmbeds = Boolean.parseBoolean(arguments.get(5));
            }

            xrayHelper.importJsonReportToJiraExistingExecution(token, jsonResultsFilePath, existingIssueKey, removeEmbeds);
            System.exit(0);

        } else if (Command.GET_ISSUE_LINKS.getName().equals(action)) {

            String issueKey = arguments.get(3);
            String linkType = skipCharacter;
            String issueIds = skipCharacter;
            if (arguments.size() > 4) {
                linkType = arguments.get(4);
            }
            if (arguments.size() > 5) {
                issueIds = arguments.get(5);
            }
            xrayHelper.getIssueLinks(token, issueKey, linkType, issueIds);

        } else if (Command.CREATE_ISSUE_LINKS.getName().equals(action)) {

            String issueKey = arguments.get(3);
            String jql = arguments.get(4);
            String linkType = arguments.get(5);
            boolean inward = true;
            if (arguments.size() > 6) {
                inward = Boolean.parseBoolean(arguments.get(6));
            }
            xrayHelper.addIssueLinks(token,issueKey,jql,linkType,inward);

        } else if (Command.RESET_ISSUE_LINKS.getName().equals(action)) {

            String issueKey = arguments.get(3);
            String linkType = skipCharacter;
            String issueIds = skipCharacter;
            if (arguments.size() > 4) {
                linkType = arguments.get(4);
            }
            if (arguments.size() > 5) {
                issueIds = arguments.get(5);
            }
            xrayHelper.clearIssueLinks(token,issueKey,linkType,issueIds);

        } else if (Command.IMPORT_EXECUTION_RESULTS.getName().equals(action)) {

            String jsonResultsFilePath = arguments.get(3);

            if (arguments.size() > 5) {
                String projectKey = arguments.get(4);
                String testPlan = arguments.get(5);
                String executionSummary = arguments.get(6);
                String executionDescription = arguments.get(7);
                String executionFixVersion = arguments.get(8);
                String environment = arguments.get(9);
                String component = arguments.get(10);
                String revision = skipCharacter;
                String labels = skipCharacter;
                boolean removeEmbeds = false;

                if (arguments.size()>11) {
                    revision = arguments.get(11);
                }

                if (arguments.size()>12) {
                    removeEmbeds = Boolean.parseBoolean(arguments.get(12));
                }

                if (arguments.size()>13) {
                    labels = arguments.get(13);
                }

                xrayHelper.importJsonReportToJira(token, jsonResultsFilePath, projectKey, testPlan, executionSummary, executionDescription, executionFixVersion, environment, component, revision, removeEmbeds, labels);
                System.exit(0);
            } else {
                String executionDetailsFilePath = arguments.get(4);
                boolean removeEmbeds = false;
                xrayHelper.importJsonReportToJira(token, jsonResultsFilePath, executionDetailsFilePath, removeEmbeds);
                System.exit(0);
            }

        } else if (Command.CREATE_DEFECT.getName().equals(action)) {

            String projectKey = arguments.get(3);
            String summary = arguments.get(4);
            String description = arguments.get(5);
            String version = arguments.get(6);
            String component = arguments.get(7);
            String linkIssue = skipCharacter;
            String linkType = skipCharacter;

            if (arguments.size() > 8) {
                linkIssue = arguments.get(8);
                linkType = arguments.get(9);
            }

            xrayHelper.createDefect(token, projectKey, summary, description, component, version, linkIssue, linkType);
            System.exit(0);

        } else if (Command.CREATE_DEFECTS_FOR_ALL_FAILURES.getName().equals(action)) {

            String projectKey = arguments.get(3);
            String resultsPath = arguments.get(4);
            String version = arguments.get(5);
            String component = arguments.get(6);
            String linkType = arguments.get(7);
            boolean linkExecution = Boolean.valueOf(arguments.get(8));
            String executionId = arguments.get(9);
            String environment = arguments.get(10);

            xrayHelper.createDefectsForAllFailures(token, resultsPath, projectKey, component, version, linkType, linkExecution, executionId, environment);
            System.exit(0);

        } else if (Command.CREATE_DEFECT_WITH_TEMPLATE.getName().equals(action)) {

            String projectKey = arguments.get(3);
            String summary = arguments.get(4);
            String description = arguments.get(5);
            String templatePath = arguments.get(6);
            String linkIssue = skipCharacter;
            String linkType = skipCharacter;

            if (arguments.size() > 7) {
                linkIssue = arguments.get(7);
                linkType = arguments.get(8);
            }

            xrayHelper.createDefectWithTemplate(token, projectKey, summary, description,  templatePath, linkIssue, linkType);
            System.exit(0);

        } else if (Command.SEARCH_FOR_ISSUES.getName().equals(action)) {

            String jql = arguments.get(3);
            xrayHelper.searchForJiraIssues(token, jql);
            System.exit(0);

        } else if (Command.CREATE_EXECUTIONS.getName().equals(action)) {
            String version = skipCharacter;
            String environment = skipCharacter;
            String labels = skipCharacter;
            String projectKey = arguments.get(3);
            String executions = arguments.get(4);
            if (arguments.size()>5) {
                version = arguments.get(5);
            }
            if (arguments.size()>6) {
                environment = arguments.get(6);
            }
            if (arguments.size()>7) {
                labels = arguments.get(7);
            }
            xrayHelper.createExecutions(token, projectKey, executions, environment, version, labels);
            System.exit(0);

        } else if (Command.CREATE_TEST_PLAN.getName().equals(action)) {
            String component = skipCharacter;
            String version = skipCharacter;
            String projectKey = arguments.get(3);
            if (arguments.size()>4) {
                component = arguments.get(4);
            }
            if (arguments.size()>5) {
                version = arguments.get(5);
            }
            xrayHelper.createTestPlan(token, projectKey, component, version);
            System.exit(0);

        } else if (Command.ADD_REMOVE_TESTS_TO_TEST_PLAN.getName().equals(action)) {
            String jqlRemove = skipCharacter;
            String projectKey = arguments.get(3);
            String testPlanKey = arguments.get(4);
            String jqlAdd = arguments.get(5);
            if (arguments.size()>6) {
                jqlRemove = arguments.get(6);
            }
            xrayHelper.addRemoveTestsToTestPlan(token, projectKey, testPlanKey, jqlAdd, jqlRemove);
            System.exit(0);

        } else if (Command.CREATE_TEST_SET.getName().equals(action)) {
            String component = skipCharacter;
            String version = skipCharacter;
            String projectKey = arguments.get(3);
            if (arguments.size()>4) {
                component = arguments.get(4);
            }
            if (arguments.size()>5) {
                version = arguments.get(5);
            }
            xrayHelper.createTestSet(token, projectKey, component, version);
            System.exit(0);

        } else if (Command.ADD_REMOVE_TESTS_TO_TEST_SET.getName().equals(action)) {

            String jqlRemove = skipCharacter;
            String projectKey = arguments.get(3);
            String testPlanKey = arguments.get(4);
            String jqlAdd = arguments.get(5);
            if (arguments.size()>6) {
                jqlRemove = arguments.get(6);
            }
            xrayHelper.addRemoveTestsToTestSet(token, projectKey, testPlanKey, jqlAdd, jqlRemove);
            System.exit(0);

        } else if (Command.CREATE_FOLDER.getName().equals(action)) {

            String projectKey = arguments.get(3);
            String folderPath = arguments.get(4);
            xrayHelper.createFolder(token, projectKey, folderPath);
            System.exit(0);

        } else if (Command.ADD_REMOVE_TESTS_TO_FOLDER.getName().equals(action)) {

            String jqlRemove = skipCharacter;
            String projectKey = arguments.get(3);
            String folderPath = arguments.get(4);
            String jqlAdd = arguments.get(5);
            if (arguments.size()>6) {
                jqlRemove = arguments.get(6);
            }
            xrayHelper.addRemoveTestsToFolder(token, projectKey, folderPath, jqlAdd, jqlRemove);
            System.exit(0);

        } else if (Command.ADD_REMOVE_TESTS_TO_PRECONDITION.getName().equals(action)) {

            String jqlRemove = skipCharacter;
            String projectKey = arguments.get(3);
            String preCondition = arguments.get(4);
            String jqlAdd = arguments.get(5);
            if (arguments.size()>6) {
                jqlRemove = arguments.get(6);
            }
            xrayHelper.addRemoveTestsToPrecondition(token, projectKey, preCondition, jqlAdd, jqlRemove);
            System.exit(0);

        } else if (Command.THRESHOLD_EVALUATE.getName().equals(action)) {

            String threshold = arguments.get(1);
            String jsonPath = arguments.get(2);
            if (xrayHelper.thresholdEvaluate(Integer.parseInt(threshold), jsonPath)) {
                System.exit(0);
            } else {
                System.exit(1);
            }

        } else if (Command.GET_ISSUE_ATTRIBUTE.getName().equals(action)) {

            String issueId = arguments.get(3);
            String attribute = arguments.get(4);
            xrayHelper.getIssueAttribute(token, issueId, attribute);

        } else if (Command.UPLOAD_ATTACHMENTS.getName().equals(action)) {

            String pageTitle = arguments.get(3);
            String uploadPath = arguments.get(4);
            String confSpace = arguments.get(5);
            String comment = arguments.get(6);
            xrayHelper.uploadAttachmentsFromPath(token, pageTitle, uploadPath, confSpace, comment);

        } else if (Command.UPDATE_STATUS.getName().equals(action)) {

        	String jiraTicket = arguments.get(3);
            String newstatus = arguments.get(4);
            String resolution = skipCharacter;
            System.out.println("Arg SIze ++> " +arguments.size());
            if(arguments.size()>5)
            	resolution = arguments.get(5);
//            String confSpace = arguments.get(5);
//            String comment = arguments.get(6);
            xrayHelper.updateTicketStatus(token, jiraTicket, newstatus, resolution);

        } else if (Command.CREATE_API_TOKEN.getName().equals(action)) {

            String clientId = arguments.get(3);
            String clientSecret = arguments.get(4);
            System.out.println("Arg SIze ++> " +arguments.size());
            xrayHelper.createTokenWithAPIKey(clientId,clientSecret);

        }
        else {
            System.out.println("Command " + action + " not supported. Only " + getCommandNames() + " are supported.");
        }
    }

    private static String getCommandNames() {
        String names = "";
        for (Command value : Command.values()) {
            names += value.getName() + " ";
        }
        return names;
    }

}

