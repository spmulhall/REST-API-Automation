package com.dhmtesting.tests;

import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class JsonShapeTest_Sandpit {

    @Test
    void shouldNavigateNestedListsAndMaps(){

        String json = """
                {
                  "project": "API Automation",
                  "teams": [
                    {
                      "name": "Test Engineering",
                      "members": [
                        "Steve",
                        "Alice",
                        "Bob"
                      ]
                    },
                    {
                      "name": "Development",
                      "members": [
                        "Charlie",
                        "Diana"
                      ]
                    }
                  ]
                }""";

        JsonPath jsonPath = new JsonPath(json);


        //EXTRACTIONS
        Map<String, Object> jsonRoot = jsonPath.getMap("");
        //System.out.println(jsonRoot);

        List<Map<String, Object>> jsonTeams = jsonPath.getList("teams");
        //System.out.println(jsonTeams);

        Map<String, Object> firstTeam = jsonTeams.get(0);
        //System.out.print(firstTeam);

        List<String> firstTeamMembers = (List<String>) firstTeam.get("members");
        //System.out.println(firstTeamMembers);

        Map<String, Object> secondTeam = jsonTeams.get(1);
        //System.out.print(secondTeam);

        List<String> secondTeamMembers = (List<String>) secondTeam.get("members");
        //System.out.println(secondTeamMembers);

        //ASSERTIONS
        assertEquals("API Automation", jsonRoot.get("project"));
        //System.out.print(jsonRoot.get("project"));

        assertEquals(2, jsonTeams.size());
        //System.out.println("the number of teams is " + jsonTeams.size());
        //assertEquals(3, firstTeamMembers.size());
        //System.out.println("the number of members in the first team is " + firstTeamMembers.size());

        assertEquals("Test Engineering", firstTeam.get("name"));

        assertEquals(3, firstTeamMembers.size());

        assertTrue(secondTeamMembers.contains("Diana"));

        assertTrue(firstTeamMembers.contains("Steve"));

        assertTrue(firstTeamMembers.stream().noneMatch(String::isBlank));

        assertTrue(secondTeamMembers.stream().noneMatch(firstTeamMembers::contains));
    }

    @Test
    void shouldValidateTestRunResults() {

        String json = """
                {
                  "suite": "Regression",
                  "completed": true,
                  "tests": [
                    {
                      "name": "Regression",
                      "status": "PASSED",
                      "durationMs": 420,
                      "tags": ["smoke", "auth"]
                    },
                    {
                      "name": "Create user",
                      "status": "PASSED",
                      "durationMs": 780,
                      "tags": ["regression", "users"]
                    },
                    {
                      "name": "Delete user",
                      "status": "FAILED",
                      "durationMs": 610,
                      "tags": ["regression", "users"]
                    }
                  ]
                }
                """;

        JsonPath jsonPath = new JsonPath(json);

        //EXTRACTIONS
        Map<String, Object> jsonRoot = jsonPath.getMap("");
        //System.out.println(jsonRoot);

        Boolean completed = (Boolean) jsonRoot.get("completed");
        //System.out.println(completed);

        List<Map<String, Object>> allTests = jsonPath.getList("tests");
        //System.out.println(allTests);

        List<String> allTestStatuses = allTests.stream()
                .map(test -> (String) test.get("status"))
                .toList();
        //System.out.println(allTestStatuses);

        List<String> testNames = allTests.stream()
                .map(test -> (String) test.get("name"))
                .collect(Collectors.toList());
        //System.out.println(testNames);

        String firstTestName = testNames.getFirst();
        //System.out.println(firstTestName);

        List<Integer> allDurations = allTests.stream()
                .map(test -> (Integer) test.get("durationMs"))
                .toList();
        //System.out.println(allDurations);

        List<List<String>> allTags = allTests.stream()
                .map(test -> (List<String>) test.get("tags"))
                .collect(Collectors.toList());
        System.out.println(allTags);

        List<String> firstTestTags = (List<String>) allTags.get(0);
        //System.out.println(firstTestTags);

        //ASSERTIONS
        assertEquals("Regression", jsonRoot.get("suite"));
        //System.out.print(jsonRoot.get("suite"));

        assertTrue(completed);

        assertNotNull(allTests);

        assertEquals(3, allTests.size());

        assertTrue(testNames.stream().noneMatch(String::isBlank));

        assertTrue(allTestStatuses.contains("FAILED"));

        assertTrue(allTestStatuses.stream().noneMatch(String::isBlank));

        assertTrue(allDurations.stream().allMatch(durations -> durations > 0));

        assertTrue(allTestStatuses.contains("PASSED"));

        assertTrue(firstTestTags.contains("smoke"));

        assertTrue(allTags.stream().anyMatch(testTags -> testTags.contains("users")));

    }

    @Test
    void shouldValidateEnvironmentHealthData() {

        String json = """
                {
                  "release": "2026.07",
                  "environments": [
                    {
                      "name": "test",
                      "active": true,
                      "services": [
                        {
                          "name": "users-api",
                          "status": "UP",
                          "responseTimesMs": [120, 135, 110]
                        },
                        {
                          "name": "orders-api",
                          "status": "UP",
                          "responseTimesMs": [180, 165, 172]
                        }
                      ]
                    },
                    {
                      "name": "staging",
                      "active": false,
                      "services": [
                        {
                          "name": "users-api",
                          "status": "DOWN",
                          "responseTimesMs": []
                        }
                      ]
                    }
                  ],
                  "incidents": []
                }
                """;

        JsonPath jsonPath = new JsonPath(json);

        Map<String, Object> jsonRoot = jsonPath.getMap("");
        //System.out.println(jsonRoot);

        String release = jsonPath.getString("release");
        //System.out.println(release);

        List<Map<String, Object>> allEnvironments = jsonPath.getList("environments");
        //System.out.println(environments);

        Map<String, Object> firstEnvironment = allEnvironments.get(0);
        //System.out.println(firstEnvironment);

        Map<String, Object> secondEnvironment = allEnvironments.get(1);
        //System.out.println(secondEnvironment);

        List<Map<String, Object>> allServices = jsonPath.getList("environments.services");
        //System.out.println(allServices);

        List<Map<String, Object>> firstEnvironmentsServices = (List<Map<String, Object>>) firstEnvironment.get("services");
        //System.out.println(firstEnvironmentsServices);

        List<String> firstEnvironmentServiceNames = firstEnvironmentsServices.stream()
                .map(test -> (String) test.get("name"))
                .toList();
        //System.out.println(firstEnvironmentServiceNames);

        List<Map<String, Object>> secondEnvironmentsServices = (List<Map<String, Object>>) secondEnvironment.get("services");
        //System.out.println(secondEnvironmentsServices);

        List<Integer> secondEnvironmentServiceResponseTimes = (List<Integer>) secondEnvironmentsServices.get(0).get("responseTimesMs");
        //System.out.println(secondEnvironmentServiceResponseTimes);

        Map<String, Object> firstEnvironmentFirstService = firstEnvironmentsServices.get(0);
        //System.out.println(firstEnvironmentFirstService);

        Map<String, Object> firstEnvironmentSecondService = firstEnvironmentsServices.get(1);
        //System.out.println(firstEnvironmentSecondService);

        List<Integer> firstEnvironmentFirstServiceResponseTimes = (List<Integer>) firstEnvironmentFirstService.get("responseTimesMs");
        //System.out.println(firstServiceResponseTimes);

        List<Integer> firstEnvironmentSecondServiceResponseTimes = (List<Integer>) firstEnvironmentSecondService.get("responseTimesMs");
        //System.out.println(secondServiceResponseTimes);

        List<String> incidentsList = (List<String>) jsonRoot.get("incidents");
        //System.out.println(incidentsList);

        boolean firstEnvironmentActive = (boolean) firstEnvironment.get("active");

        boolean secondEnvironmentActive = (boolean) secondEnvironment.get("active");

        //ASSERTIONS
        assertEquals("2026.07", release);

        assertEquals(2, allEnvironments.size());

        assertTrue(firstEnvironmentActive);

        assertFalse(secondEnvironmentActive);

        assertEquals(2, firstEnvironmentsServices.size());

        assertTrue(firstEnvironmentsServices.stream().allMatch(status -> status.get("status").equals("UP")));

        assertTrue(firstEnvironmentServiceNames.stream().noneMatch(String::isBlank));

        assertEquals(3, firstEnvironmentFirstServiceResponseTimes.size());

        assertTrue(firstEnvironmentFirstServiceResponseTimes.stream().allMatch(responseTimesMs -> responseTimesMs > 0));

        assertTrue(firstEnvironmentSecondServiceResponseTimes.stream().anyMatch(responseTimesMs -> responseTimesMs > 170));

        assertTrue(secondEnvironmentsServices.stream().anyMatch(status -> status.get("status").equals("DOWN")));

        assertEquals(0, secondEnvironmentServiceResponseTimes.size());
        assertTrue(secondEnvironmentServiceResponseTimes.isEmpty());

        assertEquals(0, incidentsList.size());
        assertTrue(incidentsList.isEmpty());
    }
}
