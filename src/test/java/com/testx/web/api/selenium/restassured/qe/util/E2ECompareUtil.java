
package com.testx.web.api.selenium.restassured.qe.util;

import lombok.extern.log4j.Log4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class E2ECompareUtil {
    //Tested
    public static Map<List<String>, List<String>> getExtraInDestinationAsMap(Map<List<String>, List<String>> allSourceRecords, Map<List<String>, List<String>> allDestinationRecords) {
        Map<List<String>, List<String>> extraInDestination = new HashMap<>();
        allDestinationRecords.forEach((key, value) -> {
            if (!(allSourceRecords.get(key) != null))
                extraInDestination.put(key, value);
            else {
                if (!allSourceRecords.get(key).equals(value))
                    extraInDestination.put(key, value);
            }
        });
        return extraInDestination;
    }

    //Tested-main
    public static Map<String, String> compareTwoMapOfString(Map<String, String> targetMap, Map<String, String> viewMap) {
        Map<String, String> missingValues = new HashMap<>();
        for (Map.Entry<String, String> entry : targetMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if ((!viewMap.containsKey(key)) || (!viewMap.containsValue(value))) {
                if ((!viewMap.get(key).equals(value))) {
                    missingValues.put(key, value);
                }
            }
        }
        return missingValues;
    }

    //Tested-main
    public static List<String> checkDuplicateValue(List<String> list) {
        return list.stream()
                .collect(Collectors.groupingBy(s -> s))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    //Tested
    public static Map<List<String>, List<String>> getMissingInDestinationAsMap(Map<List<String>, List<String>> allSourceRecords, Map<List<String>, List<String>> allDestinationRecords) {
        Map<List<String>, List<String>> missingRecordsInDestination = new HashMap<>();
        allSourceRecords.forEach((key, value) -> {
            if (!(allDestinationRecords.get(key) != null))
                missingRecordsInDestination.put(key, value);
            else {
                if (!allDestinationRecords.get(key).equals(value))
                    missingRecordsInDestination.put(key, value);
            }
        });
        return missingRecordsInDestination;
    }

    //Tested
    public static Map<List<String>, List<String>> compareTwoMap(Map<List<String>, List<String>> allRecordsFromFeed, Map<List<String>, List<String>> allRecordsInView) {
        Map<List<String>, List<String>> diff = new HashMap<>();
        allRecordsFromFeed.forEach((key, value) -> {
            if ((!(allRecordsInView.get(key) != null)))
                diff.put(key, value);
            else {
                if (!allRecordsInView.get(key).equals(value))
                    diff.put(key, value);
            }
        });

        allRecordsInView.forEach((key, value) -> {
            if ((!(allRecordsFromFeed.get(key) != null)))
                diff.put(key, value);
            else {
                if (!allRecordsFromFeed.get(key).equals(value))
                    diff.put(key, value);
            }
        });
        return diff;
    }

    public static Map<String, String> convertMapForMobileRule(Map<String, String> input) {
        input.entrySet()
                .forEach(entry -> {
                    String updated = entry.getValue().replaceAll(" \\+", ", +");
                    String update2 = updated.replaceAll("\\+", "00");
                    String update3 = update2.replaceAll("\\(", "");
                    String update4 = update3.replaceAll("\\)", "");

                    if (update4.length() > 30)
                        entry.setValue(updated.substring(9, 31));
                    else
                        entry.setValue(updated);
                });
        return input;
    }
}