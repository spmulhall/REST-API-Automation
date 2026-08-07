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
        System.out.println(firstServiceResponseTimes);

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

    @Test
    void shouldValidateDeploymentPipelineResults() {

        String json = """
                {
                  "pipeline": "Customer Platform",
                  "successful": false,
                  "environments": [
                    {
                      "name": "integration",
                      "deployed": true,
                      "components": [
                        {
                          "name": "users-service",
                          "version": "3.4.1",
                          "checks": [
                            {
                              "name": "health",
                              "passed": true,
                              "durationMs": 125
                            },
                            {
                              "name": "database",
                              "passed": true,
                              "durationMs": 240
                            }
                          ]
                        },
                        {
                          "name": "orders-service",
                          "version": "2.8.0",
                          "checks": [
                            {
                              "name": "health",
                              "passed": true,
                              "durationMs": 180
                            }
                          ]
                        }
                      ]
                    },
                    {
                      "name": "production",
                      "deployed": false,
                      "components": [
                        {
                          "name": "users-service",
                          "version": "3.4.0",
                          "checks": [
                            {
                              "name": "health",
                              "passed": false,
                              "durationMs": 95
                            }
                          ]
                        }
                      ]
                    }
                  ],
                  "warnings": []
                }
                """;

        JsonPath jsonPath = new JsonPath(json);

        // EXTRACTIONS
        Map<String, Object> jsonRoot = jsonPath.getMap("");
        //System.out.print(jsonRoot);

        String pipeline = (String) jsonRoot.get("pipeline");
        //System.out.println(pipeline);

        Boolean successful = (Boolean) jsonRoot.get("successful");
        //System.out.println(successful);

        List<Map<String, Object>> allEnvironments = (List<Map<String, Object>>) jsonRoot.get("environments");
        //System.out.println(allEnvironments);

        Map<String, Object> integrationEnvironment = allEnvironments.get(0);
        //System.out.println(integrationEnvironment);

        Map<String, Object> productionEnvironment =
                allEnvironments.stream()
                        .filter(environment ->
                                "production".equals(environment.get("name")))
                        .findFirst()
                        .orElseThrow();

        //List<Boolean> allDeployed = allEnvironments.stream()
        //        .map(env -> (Boolean) env.get("deployed")).toList();
        //System.out.println(allDeployed);

        List<List<Map<String, Object>>> allComponents = allEnvironments.stream()
                .map(env -> (List<Map<String, Object>>) env.get("components")).toList();
        //System.out.println(allComponents);

        List<Map<String, Object>> integrationEnvironmentComponents = allComponents.get(0);
        //System.out.println(integrationEnvironmentComponents);
        //System.out.print("the size of the integration environment's components is " + integrationEnvironmentComponents.size());

        List<Map<String, Object>> productionEnvironmentComponents = allComponents.get(1);
        //System.out.println(productionEnvironmentComponents);
        //System.out.print("the size of the production environment's components is " + productionEnvironmentComponents.size());

        List<Map<String, Object>> firstIntegrationComponentChecks = jsonPath.getList("environments[0].components[0].checks");
        //System.out.println(firstIntegrationComponentChecks );
        //System.out.print("the size of the integration environment's components checks is " + firstIntegrationComponentChecks .size());

        List<Map<String, Object>> allIntegrationChecks = integrationEnvironmentComponents.stream()
                .flatMap(component ->
                        ((List<Map<String, Object>>) component.get("checks"))
                                .stream())
                .toList();

        List<Map<String, Object>> firstProductionComponentChecks = jsonPath.getList("environments[1].components[0].checks");
        //System.out.println(firstProductionComponentChecks );
        //System.out.print("the size of the production environment's components checks is " + firstProductionComponentChecks .size());

        List<List<String>> allVersions = allComponents.stream().map(component -> component.stream()
                .map(comp -> (String) comp.get("version")).toList()).toList();
        //System.out.println(allVersions);


        // Remove these until you have a requirement that needs every check
        // across every environment and component.
        //List<Map<String, Object>> allChecks = jsonPath.getList("environments.components.checks");
        //System.out.println(allChecks);

        //List<Integer> allDurationsMs = jsonPath.getList("environments.components.checks.durationMs");
        //System.out.println(allDurationsMs);

        List<Integer> integrationEnvironmentDurations = firstIntegrationComponentChecks.stream()
                .map(check -> (Integer) check.get("durationMs")).toList();
        //System.out.println(integrationEnvironmentDurations);

        Map<String, Object> firstProductionComponentCheck = firstProductionComponentChecks.get(0);

        Integer firstProductionCheckDuration = (Integer) firstProductionComponentCheck.get("durationMs");

        //Integer productionEnvironmentDurations = firstProductionComponentChecks .stream()
        //        .map(check -> (Integer) check.get("durationMs")).toList().get(0);
        //System.out.println(productionEnvironmentDurations);

        List<String> integrationEnvironmentComponentCheckNames = firstIntegrationComponentChecks.stream()
                .map(check -> (String) check.get("name")).toList();
        //System.out.println(integrationEnvironmentComponentCheckNames);

        List<Object> warnings = jsonPath.getList("warnings");
        //System.out.println(warnings);

        // ASSERTIONS

        //assert that The pipeline name is exactly "Customer Platform".
        assertEquals("Customer Platform", pipeline);

        //assert that The overall pipeline was not successful.
        assertTrue(Boolean.FALSE.equals(jsonRoot.get("successful")));

        //assert that Exactly two environments were returned.
        assertEquals(2, allEnvironments.size());

        //assert that The first environment is named "integration".
        assertEquals("integration", integrationEnvironment.get("name"));

        //assert thatThe integration environment was deployed.
        assertTrue(Boolean.TRUE.equals(integrationEnvironment.get("deployed")));

        //assert that The production environment was not deployed.
        assertFalse(Boolean.TRUE.equals(productionEnvironment.get("deployed")));

        //assert that The integration environment contains exactly two components.
        assertEquals(2, integrationEnvironmentComponents.size());

        //assert that Every integration component has a non-blank name.
        assertTrue(
                integrationEnvironmentComponents.stream()
                        .allMatch(component -> {
                            String name = (String) component.get("name");
                            return name != null && !name.isBlank();
                        })
        );

        //assert that Every integration component has a non-blank version.
        assertTrue(
                integrationEnvironmentComponents.stream()
                        .allMatch(component -> {
                            String version = (String) component.get("version");
                            return version != null && !version.isBlank();
                        })
        );

        //assert that At least one integration component has a version equal to "2.8.0"
        assertTrue(integrationEnvironmentComponents.stream().anyMatch((component -> ((String) component.get("version")).equals("2.8.0"))));
        //assertTrue(integrationEnvironmentComponents.contains("2.8.0"));

        //assert that The first integration component contains exactly two checks
        assertEquals(2, firstIntegrationComponentChecks.size());

        //assert that Every check belonging to the first integration component passed.
        assertTrue(firstIntegrationComponentChecks.stream().allMatch(check -> Boolean.TRUE.equals(check.get("passed"))));

        //assert that Every check belonging to the first integration component took more than zero milliseconds.
        assertTrue(integrationEnvironmentDurations.stream().allMatch(check -> check > 0));

        //assert that At least one check across all integration components is named "health".
        assertTrue(allIntegrationChecks.stream().anyMatch(check -> "health".equals(check.get("name"))));

        //assert that The production component contains at least one failed check.
        assertTrue(firstProductionComponentChecks.stream().anyMatch(check -> Boolean.FALSE.equals(check.get("passed"))));

        //assert that The production component's first check took less than 100 milliseconds.
        assertTrue(firstProductionCheckDuration < 100);

        assertNotNull(warnings);
        assertTrue(warnings.isEmpty());
    }

    @Test
    void shouldValidateReleaseResults() {

        String json = """
 {
              "releaseId": "REL-2026-08",
              "approved": false,
              "applications": [
                {
                  "name": "customer-portal",
                  "owner": "Digital",
                  "deployed": true,
                  "environments": [
                    {
                      "name": "test",
                      "healthy": true,
                      "checks": [
                        {
                          "name": "availability",
                          "passed": true,
                          "durationMs": 140,
                          "messages": []
                        },
                        {
                          "name": "authentication",
                          "passed": true,
                          "durationMs": 220,
                          "messages": ["Login successful"]
                        }
                      ]
                    },
                    {
                      "name": "production",
                      "healthy": false,
                      "checks": [
                        {
                          "name": "availability",
                          "passed": false,
                          "durationMs": 85,
                          "messages": ["Service unavailable"]
                        }
                      ]
                    }
                  ]
                },
                {
                  "name": "admin-console",
                  "owner": "Operations",
                  "deployed": false,
                  "environments": [
                    {
                      "name": "test",
                      "healthy": true,
                      "checks": [
                        {
                          "name": "availability",
                          "passed": true,
                          "durationMs": 175,
                          "messages": []
                        }
                      ]
                    }
                  ]
                }
              ],
              "rollbackReason": null,
              "warnings": []
            }
            """;

        JsonPath jsonPath = new JsonPath(json);

        //EXTRACTIONS

        //extract root Json
        Map<String, Object> jsonRoot = jsonPath.getMap("");

        //extract a list of all applications from the root Json
        List<Map<String, Object>> allApplications = (List<Map<String, Object>>) jsonRoot.get("applications");
        //System.out.println(allApplications);
        //System.out.println(allApplications.size());

        //extract all application environments list from the list of all applications
        List<List<Map<String, Object>>> allApplicationsEnvironments = allApplications.stream()
                .map(application -> (List<Map<String, Object>>) application.get("environments")).toList();

        //extract all the checks from every environment for every application
        List<Map<String, Object>> allChecksAcrossAllApplications = allApplicationsEnvironments.stream()
                .flatMap(List::stream)
                .flatMap(environment ->
                        ((List<Map<String, Object>>) environment.get("checks")).stream())
                .toList();

        //extract all check durations
        List<Integer> allDurations = allChecksAcrossAllApplications.stream()
                .map(check -> (Integer) check.get("durationMs"))
                .toList();

        List<String> allCheckNames = allChecksAcrossAllApplications.stream()
                .map(check -> (String) check.get("name"))
                .toList();
        //System.out.println(allCheckNames);

        //extract customer portal application map from the list of all applications
        Map<String, Object> customerPortalApplication =
                allApplications.stream()
                        .filter(application ->
                                "customer-portal".equals(application.get("name")))
                        .findAny()
                        .orElseThrow();
        //System.out.println(customerPortalApplication);

        //extract admin console application map from the list of all applications
        //Map<String, Object> adminConsoleApplication = allApplications.get(1);
        Map<String, Object> adminConsoleApplication =
                allApplications.stream()
                        .filter(application ->
                                "admin-console".equals(application.get("name")))
                        .findAny()
                        .orElseThrow();
        //System.out.println(adminConsoleApplication);

        //extract and list all customer portal application environments list from customer portal application map
        List<Map<String, Object>> customerPortalApplicationAllEnvironments = (List<Map<String, Object>>) customerPortalApplication.get("environments");

        //extract customer portal test environment map from list of all customer portal environments
        Map<String, Object> customerPortalApplicationTestEnvironment =
                customerPortalApplicationAllEnvironments.stream()
                        .filter(environment ->
                                "test".equals(environment.get("name")))
                        .findFirst()
                        .orElseThrow();

        //extract customer portal production environment map from list of all customer portal environments
        Map<String, Object> customerPortalApplicationProductionEnvironment =
                customerPortalApplicationAllEnvironments.stream()
                        .filter(environment ->
                                "production".equals(environment.get("name")))
                        .findFirst()
                        .orElseThrow();

        //extract the test environment checks list from the customer portal test environment map
        List<Map<String, Object>> customerPortalApplicationTestEnvironmentChecks = (List<Map<String, Object>>) customerPortalApplicationTestEnvironment.get("checks");

        //extract the customer portal test environment checks duration list from the customer portal test environment checks map
        List<Integer> customerPortalApplicationTestEnvironmentChecksDuration = customerPortalApplicationTestEnvironmentChecks.stream()
                .map(check -> (Integer) check.get("durationMs")).toList();

        //extract the production environment checks list from the customer portal production environment map
        List<Map<String, Object>> customerPortalApplicationProductionEnvironmentChecks = (List<Map<String, Object>>) customerPortalApplicationProductionEnvironment.get("checks");

        //extract the "messages" list from customer portal application production environment checks
        List<String> firstCustomerPortalProductionCheckMessages = (List<String>) customerPortalApplicationProductionEnvironmentChecks.get(0).get("messages");
        System.out.println(firstCustomerPortalProductionCheckMessages);

        //extract the messages lists from the test environment checks
        List<List<String>> testEnvironmentMessages = customerPortalApplicationTestEnvironmentChecks.stream()
                .map(check -> (List<String>) check.get("messages"))
                .toList();

        //extract the "warnings" list
        List<Object> warnings = jsonPath.getList("warnings");

        Map<String, Object> firstCustomerPortalTestCheck =
                customerPortalApplicationTestEnvironmentChecks.get(0);

        List<String> firstCustomerPortalTestCheckMessages =
                (List<String>) firstCustomerPortalTestCheck.get("messages");


        //ASSERTIONS

        //assert that The release ID is exactly "REL-2026-08".
        assertEquals("REL-2026-08", jsonRoot.get("releaseId"));

        //assert that The release has an actual Boolean value of false for approved
        assertTrue(Boolean.FALSE.equals(jsonRoot.get("approved")));

        //Exactly two applications were returned.
        assertEquals(2, allApplications.size());

        //Every application has a non-null, non-blank name.
        assertTrue(
                allApplications.stream()
                        .allMatch(application -> {
                            String name = (String) application.get("name");
                            return name != null && !name.isBlank();
                        }));

        //assert that Every application has a non-null, non-blank owner
        assertTrue(
                allApplications.stream()
                        .allMatch(application -> {
                            String name = (String) application.get("owner");
                            return name != null && !name.isBlank();
                        }));

        //The application named "customer-portal" was deployed.
        assertTrue(Boolean.TRUE.equals(customerPortalApplication.get("deployed")));

        //The application named "admin-console" was not deployed
        assertTrue(Boolean.FALSE.equals(adminConsoleApplication.get("deployed")));

        //The customer portal contains exactly two environments
        assertEquals(2, customerPortalApplicationAllEnvironments.size());

        //The customer portal environment named "test" is healthy.
        assertTrue(Boolean.TRUE.equals(customerPortalApplicationAllEnvironments.get(0).get("healthy")));

        //The customer portal environment named "production" has an actual Boolean value of false for healthy
        assertTrue(Boolean.FALSE.equals(customerPortalApplicationAllEnvironments.get(1).get("healthy")));

        //Every check in the customer portal test environment passed
        assertTrue(customerPortalApplicationTestEnvironmentChecks.stream().allMatch(check -> Boolean.TRUE.equals(check.get("passed"))));

        //Every check in the customer portal test environment took more than zero milliseconds.
        assertTrue(customerPortalApplicationTestEnvironmentChecksDuration.stream().allMatch(check -> (Integer) check > 0));
        assertTrue(customerPortalApplicationTestEnvironmentChecksDuration.stream().allMatch(duration -> duration > 0));

        //At least one check across all environments of all applications took less than 100 milliseconds.
        assertTrue(allChecksAcrossAllApplications.stream().anyMatch(check -> (Integer) check.get("durationMs") < 100));
        assertTrue(allDurations.stream().anyMatch(check -> (Integer) check < 100));

        //At least one check across all environments of all applications is named "availability"
        //assertTrue(allCheckNames.stream().anyMatch(name -> name.equals("availability")));
        assertTrue(
                allChecksAcrossAllApplications.stream()
                        .anyMatch(check ->
                                "availability".equals(check.get("name")))
        );

        //No check across all environments of all applications has a null or blank name.
        assertTrue(
                allChecksAcrossAllApplications.stream()
                        .allMatch(application -> {
                            String name = (String) application.get("name");
                            return name != null && !name.isBlank();
                        }));

        //The customer portal production environment contains at least one failed check
        assertTrue(customerPortalApplicationProductionEnvironmentChecks.stream()
                .anyMatch(check -> Boolean.FALSE.equals(check.get("passed"))));

        //assert that The first production check contains the exact message "Service unavailable"
        //assertEquals(List.of("Service unavailable"), firstCustomerPortalProductionCheckMessages);
        assertTrue(
                firstCustomerPortalProductionCheckMessages
                        .contains("Service unavailable")
        );

        //assert that The first test check for the customer portal has an existing and empty messages list
        assertNotNull(firstCustomerPortalTestCheckMessages);
        assertTrue(firstCustomerPortalTestCheckMessages.isEmpty());

        //assert that rollbackReason exists and is null
        assertTrue(jsonRoot.containsKey("rollbackReason"));

        //assert that rollbackReason exists and is null
        assertNull(jsonRoot.get("rollbackReason"));

        //assert that The warnings list exists and is empty.
        assertNotNull(warnings);
        assertTrue(warnings.isEmpty());
    }
}

