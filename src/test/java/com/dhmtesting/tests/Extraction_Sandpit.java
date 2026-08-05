package com.dhmtesting.tests;

import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Extraction_Sandpit {

    @Test
    void shouldNavigateNestedListsAndMaps() {

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

        String projectName = (String) jsonRoot.get("project");
        //System.out.println(projectName);

        List<Map<String, Object>> allTeams = jsonPath.getList("teams");
        //System.out.println(allTeams);
        //System.out.println("the number of elements in the teams list is " + allTeams.size());

        Map<String, Object> firstTeam = allTeams.get(0);
        //System.out.println(firstTeam);

        Map<String, Object> secondTeam = allTeams.get(1);
        //System.out.println(secondTeam);

        List<String> firstTeamMembers = jsonPath.getList("teams[0].members");
        //System.out.println(firstTeamMembers);

        List<String> secondTeamMembers = jsonPath.getList("teams[1].members");
        //System.out.println(secondTeamMembers);

        //ASSERTIONS
        assertEquals("API Automation", projectName);

        assertEquals(2, allTeams.size());
        //System.out.println("the number of elements in the teams list is " + allTeams.size());

        assertEquals("Test Engineering", firstTeam.get("name"));
        //System.out.println("the name of the first team is " + firstTeam.get("name"));

        assertEquals(3, firstTeamMembers.size());
        //System.out.println("the number of members in the first teams " + firstTeamMembers.size());

        assertTrue(firstTeamMembers.contains("Steve"));

        assertTrue(firstTeamMembers.stream().noneMatch(String::isBlank));

        assertTrue(secondTeamMembers.contains("Diana"));

        assertTrue(firstTeamMembers.stream().noneMatch(secondTeamMembers::contains));
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

        Boolean isCompleted = (Boolean) jsonRoot.get("completed");
        //System.out.println(isCompleted);

        List<Map<String, Object>> allTests = jsonPath.getList("tests");
        //System.out.println(tests);

        List<String> testNames = allTests.stream().map(test -> (String) test.get("name")).toList();
        //System.out.println(testNames);

        List<String> testStatuses = allTests.stream().map(test -> (String) test.get("status")).toList();
        //System.out.println(testStatuses);

        List<Integer> testDurations = allTests.stream().map(test -> (Integer) test.get("durationMs")).toList();
        //System.out.println(testDurations);

        List<List<String>> testTags = allTests.stream().map(test -> (List<String>) test.get("tags")).toList();
        //System.out.println(testTags);

        //ASSERTIONS
        //The suite name is exactly "Regression".
        assertEquals("Regression", jsonRoot.get("suite"));

        //The run is completed.
        assertTrue(isCompleted);


        //Exactly three tests were returned.
        assertEquals(3, allTests.size());

        //Every test name contains non-blank text.
        assertTrue(testNames.stream().allMatch(name -> name != null && !name.isBlank()));

        //At least one test has status "FAILED"
        assertTrue(testStatuses.contains("FAILED"));

        //No test has a blank status.
        assertTrue(testNames.stream().noneMatch(String::isBlank));

        //Every duration is greater than zero.
        assertTrue(testDurations.stream().allMatch(duration -> duration > 0));

        assertTrue(testStatuses.stream().noneMatch(String::isBlank));

        //The statuses include "PASSED".
        assertTrue(testStatuses.contains("PASSED"));

        //The first test’s tags contain "smoke".
        assertTrue(testTags.get(0).contains("smoke"));

        //any test’s tags contain "users".
        assertTrue(testTags.stream().anyMatch(tags -> tags.contains("users")));
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

        //EXTRACTIONS
        Map<String, Object> jsonRoot = jsonPath.getMap("");
        //System.out.println(jsonRoot);

        String release = jsonRoot.get("release").toString();
        //System.out.println(release);

        List<Map<String, Object>> allEnvironments = jsonPath.getList("environments");
        //System.out.println(environments);

        Map<String, Object> firstEnvironment = allEnvironments.get(0);
        //System.out.println(firstEnvironment);

        Boolean firstEnvironmentActive = (Boolean) firstEnvironment.get("active");
        //System.out.println(firstEnvironmentActive);

        Map<String, Object> secondEnvironment = allEnvironments.get(1);
        //System.out.println(secondEnvironment);

        Boolean secondEnvironmentActive = (Boolean) secondEnvironment.get("active");
        //System.out.println(secondEnvironmentActive);

        List<Map<String, Object>> firstEnvironmentServices = (List<Map<String, Object>>) firstEnvironment.get("services");
        //System.out.println(firstEnvironmentServices);

        List<Map<String, Object>> secondEnvironmentServices = (List<Map<String, Object>>) secondEnvironment.get("services");
        //System.out.println(secondEnvironmentServices);

        Map<String, Object> firstEnvironmentFirstService = firstEnvironmentServices.get(0);
        //System.out.println(firstEnvironmentFirstService);

        Map<String, Object> secondEnvironmentFirstService = secondEnvironmentServices.getFirst();
        //System.out.println(secondEnvironmentFirstService);

        Map<String, Object> firstEnvironmentSecondService = firstEnvironmentServices.get(1);
        //System.out.println(firstEnvironmentSecondService);

        List<Integer> firstServiceResponseTimes = (List<Integer>) firstEnvironmentFirstService.get("responseTimesMs");
        //System.out.println(firstServiceResponseTimes);

        List<Integer> secondServiceResponseTimes = (List<Integer>) firstEnvironmentSecondService.get("responseTimesMs");
        //System.out.println(secondServiceResponseTimes);

        List<Integer> secondEnvironmentServiceResponseTimes = (List<Integer>) secondEnvironmentFirstService.get("responseTimesMs");
        //System.out.println(secondEnvironmentServicesResponseTimes);

        List<String> incidents = jsonPath.getList("incidents");
        //System.out.println(incidents);

        //ASSERTIONS
        assertEquals("2026.07", release);

        assertEquals(2, allEnvironments.size());

        assertTrue(firstEnvironmentActive);

        assertFalse(secondEnvironmentActive);

        assertEquals(2, firstEnvironmentServices.size());

        assertTrue(firstEnvironmentServices.stream().allMatch(service -> service.get("status").equals("UP")));

        assertTrue(firstEnvironmentServices.stream().noneMatch(service -> ((String) service.get("name")).isBlank()));

        assertEquals(3, firstServiceResponseTimes.size());

        assertTrue(firstServiceResponseTimes.stream().allMatch(responseTime -> responseTime > 0));

        assertTrue(secondServiceResponseTimes.stream().anyMatch(responseTime -> responseTime > 170));

        assertTrue(secondEnvironmentServices.stream().anyMatch(service -> service.get("status").equals("DOWN")));

        assertNotNull(secondEnvironmentServiceResponseTimes);
        assertTrue(secondEnvironmentServiceResponseTimes.isEmpty());

        assertTrue(incidents.isEmpty());
    }
}
