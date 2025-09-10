package com.testx.web.api.selenium.restassured.qe.util;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.Reportable;
import net.masterthought.cucumber.json.support.Status;
import net.masterthought.cucumber.presentation.PresentationMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.testx.web.api.selenium.restassured.qe.ui.config.ConfigurationManager.getConfiguration;

public class FolderUtil {

    public static void clearFolder(Path folder) throws IOException {
        // Check if the folder exists
        if (Files.exists(folder) || Files.isDirectory(folder)) {
            // Use Files.walkFileTree to traverse and delete files/directories
            Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file); // Delete file
                    return FileVisitResult.CONTINUE;
                }

                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir); // Delete directory after its files have been deleted
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    /**
     * Generate the cucumber report
     */
    public static void report(){
        File reportOutputDirectory = new File("target");
        List<String> jsonFiles = new ArrayList<>();
        jsonFiles.add("target/cucumber-report/cucumber.json");
        String buildNumber = "1";
        String projectName = "TestX-Selenium";
        Configuration configuration = new Configuration(reportOutputDirectory, projectName);
        //Do not make scenario failed when step has status SKIPPED
        configuration.setNotFailingStatuses(Collections.singleton(Status.SKIPPED));
        configuration.setBuildNumber(buildNumber);
        //Additional metadata presented on main page
        configuration.addClassifications("Platform", System.getProperty("os.name"));
        configuration.addClassifications("Browser", getConfiguration().browser());
        configuration.addClassifications("Branch", FolderUtil.getBranchName());
        configuration.addClassifications("Environment URL", getConfiguration().url());
        configuration.addClassifications("UserName", System.getProperty("user.name"));
        configuration.addPresentationModes(PresentationMode.PARALLEL_TESTING);
        ReportBuilder reportBuilder=new ReportBuilder(jsonFiles,configuration);
        //and here validate 'result' to decide what to do if report has failed
        Reportable result=reportBuilder.generateReports();
    }

    /**
     * Get the current GIT branch name
     * @return name of GIT branch
     */
    public static String getBranchName() {
        try {
            // Command to get the current branch name
            ProcessBuilder processBuilder = new ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD");
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String branchName = reader.readLine(); // Read the first line (branch name)

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0 && branchName != null) {
                System.out.println("Current Git Branch: " + branchName);
                return branchName;
            } else {
                System.out.println("Error fetching branch name. Exit code: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
