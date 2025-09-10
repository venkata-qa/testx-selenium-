package xray.xrayhelper;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.assertj.core.util.Lists;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

public class NexusIQReportUploadEvaluate {

    public static void main(String[] args) {

        ImportFeaturesAndTestResults xrayHelper = new ImportFeaturesAndTestResults();

        Set<String> artifactoryLoggers = new HashSet<>(Arrays.asList("org.apache.http", "groovyx.net.http", "io.restassured", "com.jayway"));
        for(String log:artifactoryLoggers) {
            ch.qos.logback.classic.Logger artLogger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(log);
            artLogger.setLevel(ch.qos.logback.classic.Level.ERROR);
            artLogger.setAdditive(false);
        }

        ArrayList<String> arguments = Lists.newArrayList(args);

        String nexusIqPdf = arguments.get(0);
        String nexusIqJson = arguments.get(1);
        String component = arguments.get(2);
        String fixVersion = arguments.get(3);
        String token = arguments.get(4);
        String confluencePage = arguments.get(5);
        String buildUrl = arguments.get(6);
        String branchName = arguments.get(7);
        String jiraProjectKey = "XPS";
        if (arguments.size() > 8) {
            jiraProjectKey = arguments.get(8);
        }
        String confSpace = "SCAUTH";
        if (arguments.size() > 10) {
            confSpace = arguments.get(10);
        }


        if (!xrayHelper.getFriendlyBranchName(branchName).equalsIgnoreCase("N/A")) {

            try {
                DocumentContext nexusIqDC = JsonPath.parse(new File(nexusIqJson));

                JSONArray securityIssues = nexusIqDC.read("$..components[?(@.securityData.securityIssues.size()>0)]");
                System.out.println("NUMBER OF PACKAGES WITH SECURITY ISSUE : " + securityIssues.size());

                for (int i = 0; i < securityIssues.size(); i++) {
                    LinkedHashMap securityIssue = (LinkedHashMap) securityIssues.get(i);

                    String packageName = securityIssue.get("packageUrl").toString();
                    String componentIdentifier = securityIssue.get("componentIdentifier").toString();
                    String pathNames = securityIssue.get("pathnames").toString();

                    LinkedHashMap securityData = (LinkedHashMap) securityIssue.get("securityData");
                    JSONArray securityIssuesArr = (JSONArray) securityData.get("securityIssues");

                    String packageUrl = securityIssue.get("packageUrl").toString();
                    String packagePath = packageUrl.split("/")[1];
                    String summary = "NexusIQ - " + packageUrl.split("/")[0] + "/" + packageUrl.split("/")[1];
                    String description = "";
                    description = description + "\n\nPackage Name: " + packageName;
                    description = description + "\n\nComponent Identifier: " + componentIdentifier;
                    description = description + "\n\nPath Names: " + pathNames;
                    description = description + "\n\n\n\n";
                    String severity = "3 - Medium";
                    String priority = "Medium";


                    System.out.println("NUMBER OF ISSUES WITH " + packageName + " : " + securityIssuesArr.size());
                    for (int j = 0; j < securityIssuesArr.size(); j++) {

                        LinkedHashMap issue = (LinkedHashMap) securityIssuesArr.get(j);

                        String source = "NOT PROVIDED";
                        try {
                            source = issue.get("source").toString();
                        } catch (Exception e) {
                        }

                        String reference = "NOT PROVIDED";
                        try {
                            reference = issue.get("reference").toString();
                        } catch (Exception e) {
                        }

                        String url = "UNKNOWN";
                        try {
                            url = issue.get("url").toString();
                        } catch (Exception e) {
                        }

                        String iqSeverity = "UNDEFINED";
                        try {
                            iqSeverity = issue.get("severity").toString();
                        } catch (Exception e) {
                        }

                        String status = "Open";
                        try {
                            status = issue.get("status").toString();
                        } catch (Exception e) {
                        }

                        String threatCategory = "severe";
                        try {
                            threatCategory = issue.get("threatCategory").toString();
                        } catch (Exception e) {
                        }

                        description = description + "\n\nIssue : " + (j+1);

                        description = description + "\n\nSource: " + source;
                        description = description + "\n\nReference: " + reference;
                        description = description + "\n\nUrl: " + url;

                        description = description + "\n\nStatus: " + status;
                        description = description + "\n\nThreat Cat: " + threatCategory;
                        description = description + "\n\nSeverity: " + iqSeverity;
                        description = description + "\n\n\n\n";

                        if (j == 0) {
                            severity = getSeverity(threatCategory);
                            priority = getPriority(threatCategory);
                        }

                    }

                    try {


                        description = description.replace("\n", "\\n");
                        description = description.replaceAll("[^a-zA-Z0-9n/\\-.,:;?=\\s\\\\+]", "");
                        //description = description.replaceAll("nn", "\\\\n");

                        summary = summary.replaceAll("[^a-zA-Z0-9n/\\-.,:;@?=\\s\\\\+]", "");

                        System.out.println("DESCRIPTION " + description);

                        System.out.println("SUMMARY " + summary);

                        String defectId = xrayHelper.createNexusIqDefect(token, jiraProjectKey, summary, description, component, fixVersion, severity, priority, buildUrl, xrayHelper.getFriendlyBranchName(branchName), packagePath);
                        System.out.println("Logged defect : " + defectId);
                    } catch (Exception e) {
                        System.out.println("Could not log issue");
                    }

                }

            } catch (Exception e) {
                System.out.println("Could not read nexus IQ report");
                System.out.println(e.getMessage());
                System.out.println(e.getStackTrace());
            }

            try {
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date());
                File iqPdf = new File(nexusIqPdf);
                String pageId = xrayHelper.getPageId(token, confluencePage, confSpace);
                byte[] fileContent = Files.readAllBytes(iqPdf.toPath());
                xrayHelper.uploadAttachment(token, pageId, fileContent, iqPdf.getName() + "-" + "-" + xrayHelper.getFriendlyBranchName(branchName) + timeStamp+".pdf", "application/pdf", "Nexus IQ report generated by build : " + buildUrl);

            } catch (Exception e) {
                System.out.println("Cannot find file");
                System.out.println(e.getMessage());

            }
        }

    }

    public static String getSeverity(String threat) {
        switch (threat) {
            case "critical":
                return "2 - Major";
            case "severe":
                return "3 - Medium";
            case " moderate":
                return "4 - Low";
            default:
                return "3 - Medium";
        }
    }

    public static String getPriority(String threat) {
        switch (threat) {
            case "critical":
                return "High";
            case "severe":
                return "Medium";
            case " moderate":
                return "Low";
            default:
                return "Medium";
        }
    }

}

