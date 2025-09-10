package xray.xrayhelper;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class BootstrapHelper extends Model {

    int failCount;
    int warnCount;
    int passCount;
    HashMap<String, LinkedList<HashMap<String, String>>> propResults;
    List<String> ucdProps = new ArrayList<>();
    Map<String,String> propToUCD = new HashMap<>();
    Map<String,String> ucdToProp = new HashMap<>();
    EnvType environmentType;

    public enum EnvType {
        SIT, RTL, SANDBOX
    }

    public void bootstrapCompare(String apiName, String newZipPath, String bootstrapZipPath, String bootstrapPropsPath, String defaultsPath, String overridesPath, String targetFolder, String confluencePage, String token, String buidUrl, String branchName, String confSpace) {

        unzip(newZipPath, targetFolder+"/"+apiName+"/new-config/");
        unzip(bootstrapZipPath, targetFolder + "/"+apiName+"/bootstrap-config/");

        mapProperties(bootstrapPropsPath);

        failCount = 0;
        warnCount = 0;
        passCount = 0;
        propResults = new HashMap<>();
        ucdProps = new ArrayList<>();

        File dir = new File(targetFolder+"/"+apiName+"/new-config/");
        File[] directoryListing = dir.listFiles();


        if (directoryListing != null) {
            for (File child : directoryListing) {
                logger.info("FILE:" + child.getName());

                try {

                    if (child.getName().contains("-")) {
                        String cellId = child.getName().substring(0, child.getName().indexOf("-"));
                        logger.info("CELLID: "+cellId);
                        String environment = identifyEnvironment(cellId, overridesPath);
                        logger.info("ENVIRONMENT: " + environment);
                        String envDefaultsPath = setDefaultsFile(environment, defaultsPath);
                        logger.info("ENV DEFAULTS FILE: " + envDefaultsPath);
                        checkProperties(child.getPath(), environment, envDefaultsPath, overridesPath, apiName, cellId, child.getName());
                    }
                } catch (Exception e) {
                    logger.error("problem analysing environment");
                }
            }

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(targetFolder+"/" + apiName + "-bootstrap-test.html"));
                bw.write("<html><head><title>"+apiName+" Bootstrap Test</title></head>"
                        + "<body><p>PASS COUNT : "+passCount+"</p>"
                        + "<p>FAIL COUNT : "+failCount+"</p>"
                        + "<p>WARNING COUNT : "+warnCount+"</p>"
                        + "<p>The following properties must be mapped in UCD:</p><ul>");
                for (int i = 0; i < ucdProps.size(); i++) {
                    bw.write("<li>"+ucdProps.get(i)+"<li>");
                }
                bw.write("</ul><p>"+getHtmlTable()+"</p>");
                bw.write("</body></html>");
                bw.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(targetFolder+"/" + apiName + "-UCD_Requirements.txt"));
                bw.write("The following properties must be mapped in UCD:\n");
                for (int i = 0; i < ucdProps.size(); i++) {
                    bw.write("- "+ucdProps.get(i)+"\n");
                }
                bw.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

            byte[] excel = writeExcel(getHtmlTable(), targetFolder+"/"+apiName+"-bootstrap-test.xls");

            deleteDirectory(new File(targetFolder + "/" + apiName + "/"));

            if (confluencePage != "") {
                ImportFeaturesAndTestResults xray = new ImportFeaturesAndTestResults();

                if (!xray.getFriendlyBranchName(branchName).equalsIgnoreCase("N/A")) {
                    try {
                        String pageId = xray.getPageId(token, confluencePage, confSpace);
                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date());
                        String filename = apiName.replace("#", "") + "-bootstrap-report-" + xray.getFriendlyBranchName(branchName) + "-" + timeStamp + ".xls";

                        xray.uploadAttachment(token, pageId, excel, filename, "application/vnd.ms-excel", "Bootstrap report generated by build : " + buidUrl);
                    } catch (Exception e) {
                        System.out.println("Could not attach file");
                    }
                }
            }

        }
    }

    private String getHtmlTable() {

        String table = "<style type=\"text/css\">\n"
                + ".tg  {border-collapse:collapse;border-spacing:0;}\n"
                + ".tg td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:black;}\n"
                + ".tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:black;}\n"
                + ".tg .tg-ycr8{background-color:#ffffff;text-align:left;vertical-align:top}\n"
                + ".tg .tg-hxtu{font-weight:bold;background-color:#036400;color:#ffffff;text-align:left;vertical-align:top}\n"
                + ".tg .tg-iih1{font-weight:bold;background-color:#036400;border-color:#000000;text-align:left;vertical-align:top}\n"
                + "</style>\n"
                + "<table class=\"tg\">\n"
                + "  <tr>\n"
                + "    <th class=\"tg-hxtu\">Filename</th>\n"
                + "    <th class=\"tg-hxtu\">CellId</th>\n"
                + "    <th class=\"tg-hxtu\">Environment</th>\n"
                + "    <th class=\"tg-hxtu\">EnvironmentType</th>\n"
                + "    <th class=\"tg-hxtu\">PropertyName</th>\n"
                + "    <th class=\"tg-hxtu\">PlaceHolderName</th>\n"
                + "    <th class=\"tg-hxtu\">Result</th>\n"
                + "    <th class=\"tg-hxtu\">In Override</th>\n"
                + "    <th class=\"tg-hxtu\">In Default</th>\n"
                + "    <th class=\"tg-hxtu\">UCD</th>\n"
                + "    <th class=\"tg-hxtu\">Expected Value</th>\n"
                + "    <th class=\"tg-hxtu\">New configs value</th>\n"
                + "    <th class=\"tg-hxtu\">Bootstrap Value</th>\n"
                + "    <th class=\"tg-hxtu\">Notes</th>\n"
                + "  </tr>\n";


        List<String> envKeys = new ArrayList<>(propResults.keySet());
        Collections.sort(envKeys);

        for (int j = 0; j < envKeys.size(); j++) {

            for (int i = 0; i < (propResults.get(envKeys.get(j))).size(); i++) {

                HashMap<String,String> temp = propResults.get(envKeys.get(j)).get(i);

                if (!temp.get("result").equals("PASS")) {

                    String colour = "color:rgb(254, 0, 0)";
                    if (temp.get("result").equals("PASS")) {
                        colour = "color:rgb(50, 203, 0)";
                    } else if (temp.get("result").equals("WARNING")) {
                        colour = "color:rgb(248, 161, 2)";
                    }

                    table = table + "  <tr>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("fileName") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("cellId") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("environment") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("environmentType") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("propertyName") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("propertyMapping") + "</td>\n"
                            + "    <td class=\"tg-ycr8\"><span style=\"font-weight:bold;" + colour + "\">" + temp.get("result") + "</span></td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("inOverride") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("inDefaults") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("ucd") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("expectedValue") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("actualValue") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("bootstrapValue") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("notes") + "</td>\n"
                            + "  </tr>\n";
                }
            }
        }

        for (int j = 0; j < envKeys.size(); j++) {

            for (int i = 0; i < (propResults.get(envKeys.get(j))).size(); i++) {

                HashMap<String, String> temp = propResults.get(envKeys.get(j)).get(i);

                if (temp.get("result").equals("PASS")) {

                    String colour = "color:rgb(254, 0, 0)";
                    if (temp.get("result").equals("PASS")) {
                        colour = "color:rgb(50, 203, 0)";
                    } else if (temp.get("result").equals("WARNING")) {
                        colour = "color:rgb(248, 161, 2)";
                    }

                    table = table + "  <tr>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("fileName") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("cellId") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("environment") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("environmentType") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("propertyName") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("propertyMapping") + "</td>\n"
                            + "    <td class=\"tg-ycr8\"><span style=\"font-weight:bold;" + colour + "\">" + temp.get("result") + "</span></td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("inOverride") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("inDefaults") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("ucd") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("expectedValue") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("actualValue") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("bootstrapValue") + "</td>\n"
                            + "    <td class=\"tg-ycr8\">" + temp.get("notes") + "</td>\n"
                            + "  </tr>\n";
                }
            }
        }

        return table;
    }

    private byte[] writeExcel(String htmlData, String excelLocation) {

        //LOG.info(htmlData);

        // create work book
        HSSFWorkbook wb = new HSSFWorkbook();
        // create excel sheet for page 1
        HSSFSheet sheet = wb.createSheet();

        //        //Set Header Font
        HSSFFont headerFont = wb.createFont();
        headerFont.setBold(true);;
        headerFont.setFontHeightInPoints((short) 12);
        //
        //        //Set Header Style
        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
        headerStyle.setFont(headerFont);
        int rowCount = 0;
        Row header;

        Document doc = Jsoup.parse(htmlData);

        Cell cell;
        for (Element table : doc.select("table")) {
            // loop through all tr of table
            for (Element row : table.select("tr")) {
                // create row for each
                header = sheet.createRow(rowCount);

                Elements ths = row.select("th");
                int count = 0;
                for (Element element : ths) {
                    // set header style
                    cell = header.createCell(count);
                    cell.setCellValue(element.text());
                    cell.setCellStyle(headerStyle);
                    count++;
                }
                // now loop through all td tag
                Elements tds = row.select("td");
                count = 0;
                for (Element element : tds) {
                    // create cell for each
                    cell = header.createCell(count);
                    cell.setCellValue(element.text());
                    count++;
                }
                rowCount++;
                // set auto size column for excel sheet
                sheet = wb.getSheetAt(0);

            }
            rowCount++;
        }
        try {
            File file = new File(excelLocation);

            FileOutputStream outputStream = new FileOutputStream(file);
            wb.write(outputStream);
            wb.close();
            return Files.readAllBytes(file.toPath());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public void unzip(String fileLocation, String destDir) {
        //LOG.info("unzipping file..." + fileLocation);
        createFolder(destDir);
        String fileZip = fileLocation;
        try {
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(new File(destDir), zipEntry);
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
        } catch (Exception e) {
            logger.error("Could not extract files");
            logger.error(e.getMessage());
        }
    }

    private String setDefaultsFile(String environment, String defaultConfigFilePath) {

        String defaultsFilePathIt = "";

        if (environment.contains("cit")||environment.contains("sit")) {
            defaultsFilePathIt = defaultConfigFilePath + "SIT_config_default.yaml";
            environmentType = EnvType.SIT;
        } else if (environment.contains("luat")||environment.contains("prod")||environment.contains("nft")) {
            defaultsFilePathIt = defaultConfigFilePath + "RTL_config_default.yaml";
            environmentType = EnvType.RTL;
        } else {
            environmentType = EnvType.SANDBOX;
            defaultsFilePathIt = defaultConfigFilePath + "SANDBOX_config_default.yaml";
        }

        return defaultsFilePathIt;
    }

    private void checkProperties(String filePath, String environment, String defaultConfigFilePath, String overrideFilePath, String apiName, String cellId, String fileName) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(filePath));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        LinkedList<HashMap<String, String>> propertyDetails = new LinkedList<>();

        for(String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);

            HashMap<String, String> propResult = new HashMap<>();

            propResult.put("cellId", cellId);
            propResult.put("fileName", fileName);
            propResult.put("propertyName", key);
            propResult.put("actualValue", value);

            String propertyName = propToUCD.get(key);
            //LOG.info(propertyName);

            propResult.put("propertyMapping", propertyName);

            propResult.put("environmentType", environmentType.toString());
            propResult.put("environment", environment);

            // Check if in environment overide

            String overrideValue = (isInEnvironmentOverride(overrideFilePath, environment, propertyName));

            if (propertyName.contains("%%")) {
                propResult.put("notes", propertyName + " ("+key+") value is not present in overrides or defaults and must be mapped in UCD with placeholder " + value);
                propResult.put("result", "PASS");
                propResult.put("expectedValue", "UCD");
                propResult.put("inOverride", "false");
                propResult.put("inDefaults", "false");
                propResult.put("ucd", "true");
                passCount++;
                if (!ucdProps.contains(propertyName)) {
                    ucdProps.add(propertyName);
                }
            } else if (key.equals("app-name")||key.equals("context-root")||key.equals("app-location")) {
                propResult.put("expectedValue", "*"+apiName+"*");
                propResult.put("inOverride", "false");
                propResult.put("inDefaults", "false");
                if (value.contains(apiName)) {
                    propResult.put("notes", propertyName + "("+key+") correctly contains " + apiName + " with value " + value);
                    propResult.put("result", "PASS");
                    propResult.put("ucd", "false");
                    passCount++;
                } else {
                    propResult.put("result", "PASS");
                    propResult.put("ucd", "false");
                    propResult.put("notes", propertyName + "("+key+") does not contain " + apiName + " with value " + value);
                    passCount++;
                }
            }
            else if (overrideValue!=null) {
                propResult.put("inOverride", "true");
                propResult.put("ucd", "false");
                propResult.put("expectedValue", overrideValue);
                propResult.put("inDefaults", "N/A");

                if (value.contains("ihs") && environmentType == EnvType.SIT && !value.contains(environment)) {
                    propResult.put("result", "WARNING");
                    propResult.put("notes", propertyName + "(" + key + ") correctly taking override value for " + environment + " of " + overrideValue + " but this appears to be an IHS URL which does not seem to be for " + environment);
                    warnCount++;


                } else if ((value.contains("sca-retail-api-digital") || value.contains("channel-retail-auth-api") || value.contains("sca-eia-callsign-api")) && environmentType==EnvType.SIT && !value.contains("sit01")) {
                    propResult.put("result", "WARNING");
                    propResult.put("notes", propertyName + "("+key+") correctly taking override value for " + environment + " of " + overrideValue + " but all " + environmentType + " gateway urls should point to APIC sit01 environment");
                    warnCount++;

                } else if (value.equals(overrideValue)) {
                    propResult.put("result", "PASS");
                    propResult.put("notes", propertyName + "("+key+") correctly taking override value for " + environment + " of " + overrideValue);
                    passCount++;
                } else {
                    propResult.put("result", "FAIL");
                    propResult.put("notes", propertyName + "("+key+") has value " + value + " where override for " + environment + " is expected with value " + overrideValue);
                    failCount++;
                }
            } else {

                propResult.put("inOverride", "false");
                propResult.put("ucd", "false");

                String defaultsValue = getDefaultsValue(defaultConfigFilePath, propertyName);
                if (defaultsValue !=null) {

                    propResult.put("expectedValue", defaultsValue);
                    propResult.put("inDefaults", "true");

                    if (defaultsValue.equals("WILL_COME_FROM_ENV_CONFIG")) {
                        propResult.put("result", "FAIL");
                        propResult.put("notes", propertyName + " ("+key+") value must come from overrides but is not present");
                        failCount++;
                    } else if (value.contains("ihs")) {
                        propResult.put("result", "WARNING");
                        propResult.put("notes", propertyName + " ("+key+") contains an IHS url for " + environmentType + " of " + value + " which is environment specific");
                        warnCount++;
                    } else if ((value.contains("sca-retail-api-digital") || value.contains("channel-retail-auth-api") || value.contains("sca-eia-callsign-api")) && environmentType==EnvType.SIT && !value.contains("sit01")) {
                        propResult.put("result", "WARNING");
                        propResult.put("notes", propertyName + "("+key+") correctly taking default value for " + environmentType + " of " + value + " but all " + environmentType + " gateway urls should point to APIC sit01 environment");
                        warnCount++;

                    } else if (defaultsValue.contains("%%")) {
                        propResult.put("result", "PASS");
                        propResult.put("ucd", "true");
                        if (!ucdProps.contains(propertyName)) {
                            ucdProps.add(propertyName);
                        }
                        propResult.put("notes", propertyName + " ("+key+") value must be mapped in UCD with placeholder " + defaultsValue);
                        passCount++;
                    } else if (value.equals(defaultsValue)) {
                        propResult.put("result", "PASS");
                        propResult.put("notes", propertyName + " ("+key+") correctly taking defaults value for " + environmentType + " of " + defaultsValue);
                        passCount++;
                    } else {
                        propResult.put("result", "FAIL");
                        propResult.put("notes", propertyName + " ("+key+") has value " + value + " where defaults for " + environmentType + " is expected with value " + defaultsValue);
                        failCount++;
                    }
                } else {
                    propResult.put("inDefaults", "false");
                    propResult.put("result", "FAIL");
                    propResult.put("expectedValue", "UNKNOWN");
                    propResult.put("notes", propertyName + " ("+key+") has no default or override value for " + environment + "");
                    failCount++;
                }
            }
            if (environmentType!=EnvType.SANDBOX) {
                matchFinalConfig(filePath, key, value, propResult, overrideValue != null);
            }
            propertyDetails.add(propResult);
        }

        checkForMissingProperties(filePath, environment, defaultConfigFilePath, overrideFilePath, cellId, fileName, propertyDetails);

        propResults.put(environment, propertyDetails);
    }

    private void matchOldConfig(String filePath, String property, String value, HashMap<String, String> propResult, boolean override) {

        filePath = filePath.replace("new-config", "old-config");

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(filePath));

            try {
                propResult.put("oldValue",properties.getProperty(property));
                if (!properties.getProperty(property).equals(value)) {
                    propResult.put("result", "WARNING");
                    propResult.put("compareNotes", property + " New Config Value : " + value + " differs from old config value " + properties.get(property) + " and environment override is " + override);
                }
            } catch (Exception e) {
                propResult.put("result", "WARNING");
                propResult.put("compareNotes", property + "New Config Value : " + value + " could not be found in old config");
            }

        } catch (IOException e) {
            System.out.println("I AM HERE IN PROPS LOAD FAIL");
            System.out.println(e.getMessage());
        }
    }


    private List<String> checkForMissingInOldConfig(String filePath, Properties newProps) {

        filePath = filePath.replace("new-config", "old-config");
        List<String> missingProperties = new ArrayList<>();

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(filePath));

            for(String key : properties.stringPropertyNames()) {
                if (!newProps.containsKey(key)) {
                    missingProperties.add(key);
                }
            }

        } catch (IOException e) {
            System.out.println("I AM HERE IN PROPS LOAD FAIL");
            System.out.println(e.getMessage());
        }
        return missingProperties;
    }

    private void matchFinalConfig(String filePath, String property, String value, HashMap<String, String> propResult, boolean override) {

        filePath = filePath.replace("new-config", "bootstrap-config");

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(filePath));

            try {
                propResult.put("bootstrapValue",properties.getProperty(property));
                if (!properties.getProperty(property).equals(value)) {
                    propResult.put("result", "FAIL");
                    propResult.put("compareNotes", property + " New Config Value : " + value + " differs from final bootstrap value " + properties.get(property) + " and environment override is " + override);
                }
            } catch (Exception e) {
                propResult.put("result", "WARNING");
                propResult.put("notes", property + "New Config Value : " + value + " could not be found in final bootstrap");
            }

        } catch (IOException e) {
            System.out.println("I AM HERE IN PROPS LOAD FAIL");
            System.out.println(e.getMessage());
        }
    }

    private void checkForMissingProperties(String filePath, String environment, String defaultConfigFilePath, String overrideFilePath, String cellId, String fileName, LinkedList<HashMap<String, String>> propertyDetails) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(filePath));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // TESTING AGAINST DEFAULTS

        try {
            Yaml yaml = new Yaml();

            InputStream is = new FileInputStream(defaultConfigFilePath);
            Map<String, Object> obj = yaml.load(is);
            Map<String, Object> tokens = (Map<String,Object>)obj.get("tokens");
            for (Map.Entry<String, Object> entry : tokens.entrySet()) {

                assert (ucdToProp.get(entry.getKey())!=null);
                if (ucdToProp.get(entry.getKey())!=null&&!properties.stringPropertyNames().contains(ucdToProp.get(entry.getKey()))) {

                    HashMap<String, String> propResult = new HashMap<>();
                    propResult.put("cellId", cellId);
                    propResult.put("fileName", fileName);
                    propResult.put("propertyName", ucdToProp.get(entry.getKey()));
                    propResult.put("actualValue", "N/A");
                    propResult.put("expectedValue", entry.getValue().toString());
                    propResult.put("inOverride", "false");
                    propResult.put("inDefaults", "true");
                    propResult.put("ucd", "false");
                    propResult.put("propertyMapping", entry.getKey());

                    propResult.put("environmentType", environmentType.toString());
                    propResult.put("environment", environment);
                    propResult.put("result", "FAIL");
                    propResult.put("notes", entry.getKey() + " not present in bootstrap but in environment defaults for "+environmentType+" for known property " + ucdToProp.get(entry.getKey()));
                    failCount++;
                    propertyDetails.add(propResult);
                } else if (ucdToProp.get(entry.getKey())==null){
                    HashMap<String, String> propResult = new HashMap<>();
                    propResult.put("cellId", cellId);
                    propResult.put("fileName", fileName);
                    propResult.put("propertyName", "UNKNOWN");
                    propResult.put("actualValue", "N/A");
                    propResult.put("expectedValue", entry.getValue().toString());
                    propResult.put("inOverride", "false");
                    propResult.put("inDefaults", "true");
                    propResult.put("ucd", "false");
                    propResult.put("propertyMapping", entry.getKey());

                    propResult.put("environmentType", environmentType.toString());
                    propResult.put("environment", environment);
                    propResult.put("result", "WARNING");
                    propResult.put("notes", entry.getKey() + " not present in bootstrap but in environments defaults for "+environmentType+" and unable to map back to a known property name");
                    warnCount++;
                    propertyDetails.add(propResult);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }


        // TESTING AGAINST OVERRIDES

        try {
            Yaml yaml = new Yaml();

            InputStream is = new FileInputStream(overrideFilePath);
            Map<String, Object> obj = yaml.load(is);
            Map<String, Object> environments = (Map<String,Object>)obj.get("environments");

            Map<String, String> envProps = (Map<String,String>)environments.get(environment);

            for (Map.Entry<String, String> entry : envProps.entrySet()) {

                if (entry.getKey().equals("_CELLID")){
                    if (!cellId.equals(entry.getValue())) {
                        HashMap<String, String> propResult = new HashMap<>();
                        propResult.put("cellId", cellId);
                        propResult.put("fileName", fileName);
                        propResult.put("propertyName", ucdToProp.get(entry.getKey()));
                        propResult.put("actualValue", "N/A");

                        propResult.put("propertyMapping", entry.getKey());

                        propResult.put("environmentType", environmentType.toString());
                        propResult.put("environment", environment);
                        propResult.put("result", "FAIL");
                        propResult.put("notes", entry.getKey() + " does not have the expected value " + cellId);
                        failCount++;
                        propertyDetails.add(propResult);
                    }

                } else if (entry.getKey().equals("_OLD_CONFIG_FILE")) {
                    // DO NOTHING
                }
                //LOG.info(entry.getKey());
                //LOG.info(ucdToProp.get(entry.getKey()));
                else if (ucdToProp.get(entry.getKey())!=null&&!properties.stringPropertyNames().contains(ucdToProp.get(entry.getKey()))) {

                    HashMap<String, String> propResult = new HashMap<>();
                    propResult.put("cellId", cellId);
                    propResult.put("fileName", fileName);
                    propResult.put("propertyName", ucdToProp.get(entry.getKey()));
                    propResult.put("actualValue", "N/A");
                    propResult.put("expectedValue", entry.getValue().toString());
                    propResult.put("propertyMapping", entry.getKey());
                    propResult.put("inOverride", "true");
                    propResult.put("inDefaults", "N/A");
                    propResult.put("ucd", "false");
                    propResult.put("environmentType", environmentType.toString());
                    propResult.put("environment", environment);
                    propResult.put("result", "FAIL");
                    propResult.put("notes", entry.getKey() + " not present in bootstrap but in environments override");
                    failCount++;
                    propertyDetails.add(propResult);
                } else if (ucdToProp.get(entry.getKey())==null){

                    HashMap<String, String> propResult = new HashMap<>();
                    propResult.put("cellId", cellId);
                    propResult.put("fileName", fileName);
                    propResult.put("propertyName", "UNKNOWN");
                    propResult.put("actualValue", "N/A");
                    propResult.put("expectedValue", entry.getValue().toString());
                    propResult.put("propertyMapping", entry.getKey());
                    propResult.put("inOverride", "true");
                    propResult.put("inDefaults", "N/A");
                    propResult.put("ucd", "false");
                    propResult.put("environmentType", environmentType.toString());
                    propResult.put("environment", environment);
                    propResult.put("result", "WARNING");
                    propResult.put("notes", entry.getKey() + " not present in bootstrap but in environments override");
                    warnCount++;
                    propertyDetails.add(propResult);
                }
            }
        } catch (Exception e) {
        }

    }

    private String isInEnvironmentOverride(String overrideFilePath, String environmentName, String propertyName) {
        try {
            Yaml yaml = new Yaml();

            InputStream is = new FileInputStream(overrideFilePath);
            Map<String, Object> obj = yaml.load(is);
            Map<String, Object> environments = (Map<String,Object>)obj.get("environments");

            Map<String, String> envProps = (Map<String,String>)environments.get(environmentName);

            for (Map.Entry<String, String> entry : envProps.entrySet()) {
                if (entry.getKey().equals(propertyName)) {
                    return entry.getValue();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getDefaultsValue(String defaultsFilePath, String propertyName) {

        try {
            Yaml yaml = new Yaml();

            InputStream is = new FileInputStream(defaultsFilePath);
            Map<String, Object> obj = yaml.load(is);
            Map<String, Object> tokens = (Map<String,Object>)obj.get("tokens");
            for (Map.Entry<String, Object> entry : tokens.entrySet()) {
                if (entry.getKey().equals(propertyName)) {
                    return entry.getValue().toString();
                }
            }
            return null;
        } catch (Exception e) {
            System.out.println("In get defaults fail");
            logger.error(e.getMessage());
            return null;
        }
    }

    private String identifyEnvironment(String cellId, String filePath) {
        try {
            Yaml yaml = new Yaml();

            InputStream is = new FileInputStream(filePath);
            Map<String, Object> obj = yaml.load(is);
            Map<String, Object> environments = (Map<String,Object>)obj.get("environments");
            for (Map.Entry<String, Object> entry : environments.entrySet()) {
                Map<String, String> envProps = (Map<String,String>)entry.getValue();
                if (envProps.get("_CELLID").equals(cellId)) {
                    return entry.getKey();
                }
            }
            return cellId;
        } catch (Exception e) {
            return cellId;
        }
    }

    private void mapProperties(String mappingFilePath) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(mappingFilePath));
        } catch (IOException e) {
            System.out.println("In Map Properties error");
        }

        for(String key : properties.stringPropertyNames()) {
            ucdToProp.put(properties.getProperty(key).replace("&&",""), key);
            propToUCD.put(key, properties.getProperty(key).replace("&&",""));
        }

        ucdToProp.put("SERVER_BOX_ID", "serverBoxId");
        ucdToProp.put("serverBoxId", "SERVER_BOX_ID");
    }

    private String compareFiles(String masterFile, String compareFile) {

        String result = "";

        try {
            BufferedReader reader1 = new BufferedReader(new FileReader(masterFile));

            BufferedReader reader2 = new BufferedReader(new FileReader(compareFile));

            String line1 = reader1.readLine();

            String line2 = reader2.readLine();

            boolean areEqual = true;

            int lineNum = 1;

            while (line1 != null || line2 != null) {
                if (line1 == null || line2 == null) {
                    areEqual = false;

                    break;
                } else if (!line1.equalsIgnoreCase(line2)) {
                    areEqual = false;

                    break;
                }

                line1 = reader1.readLine();

                line2 = reader2.readLine();

                lineNum++;
            }

            if (!areEqual) {
                result = result + "<li>File : " + new File(masterFile).getParent() + "\nand File: " + new File(compareFile).getParent() + "\ndiffer at line " + lineNum + "\n"+line1+" != " + line2+"</li>";
            }

            reader1.close();

            reader2.close();

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
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


    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

}
