package com.dhmtesting.tests;

import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JsonShapeTest {
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

        JsonPath myJson = new JsonPath(json);

        //Extractions
        Map<String, Object> extractRoot = myJson.getMap("");
        String projectName = (String) extractRoot.get("project");
        List<Map<String, Object>> extractTeams = (List<Map<String, Object>>) extractRoot.get("teams");
        Map<String, Object> extractFirstTeam = extractTeams.get(0);
        Map<String, Object> extractSecondTeam = extractTeams.get(1);
        String firstTeamName = (String) extractFirstTeam.get("name");
        List<String> firstTeamMembers = (List<String>) extractFirstTeam.get("members");
        List<String> secondTeamMembers = (List<String>) extractSecondTeam.get("members");

        //Assertions
        assertEquals("API Automation", projectName);
        assertEquals(2, extractTeams.size());
        assertEquals("Test Engineering", firstTeamName);
        assertEquals(3, firstTeamMembers.size());
        assertTrue((firstTeamMembers.contains("Steve")));

        //2 versions of the same assertion
        assertTrue(firstTeamMembers.stream().noneMatch(members -> members.isBlank()));
        assertTrue(firstTeamMembers.stream().noneMatch(String::isBlank));

        assertTrue(secondTeamMembers.contains("Diana"));
        assertTrue(firstTeamMembers.stream().noneMatch(secondTeamMembers::contains));
    }

    @Test
    void shouldValidateTestRunResults() {

        String newJson = """
                {
                  "suite": "Regression",
                  "completed": true,
                  "tests": [
                    {
                      "name": "Login",
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
                }""";

        JsonPath jsonPath = new JsonPath(newJson);

        //extractions
        String suite = jsonPath.getString("suite");
        Boolean completed = jsonPath.getBoolean("completed");

        List<Map<String, Object>> tests = jsonPath.getList("tests");

        Map<String, Object> firstTest = tests.get(0);

        List<String> firstTestTags = (List<String>) firstTest.get("tags");

        List<String> names = jsonPath.getList("tests.name");

        List<String> statuses = jsonPath.getList("tests.status");

        List<Integer> durations = jsonPath.getList("tests.durationMs");

        List<List<String>> tags = jsonPath.getList("tests.tags");

        //assertions
        assertEquals("Regression", suite);

        assertTrue(completed);
        assertTrue(Boolean.TRUE.equals(completed));

        //run the null test before the size check - otherwise the size check will throw a null pointer exception if there are any null values
        assertNotNull(tests);
        assertEquals(3, tests.size());

        assertTrue(names.stream().noneMatch(String::isBlank));

        //this will check if any of the statuses contain the string "FAILED" so will pass for a partial match
        assertTrue(statuses.stream().anyMatch(status -> status.contains("FAILED")));
        //these 2 are a stronger version of the above check which will only pass if the status is exactly "FAILED"
        assertTrue(statuses.stream().anyMatch(status -> status.equals("FAILED")));
        assertTrue(statuses.contains("FAILED"));

        assertTrue(statuses.stream().noneMatch(String::isBlank));

        assertTrue(durations.stream().allMatch(duration -> duration > 0));

        //this will check if any of the statuses contain the string "FAILED" so will pass for a partial match
        assertTrue(statuses.stream().anyMatch(status -> status.contains("PASSED")));
        //this is a stronger version of the above check which will only pass if the status is exactly "PASSED"
        assertTrue(statuses.stream().anyMatch(status -> status.equals("PASSED")));
        assertTrue(statuses.contains("PASSED"));

        assertTrue(firstTestTags.contains("smoke"));

        assertTrue(tags.stream().anyMatch(testTags -> testTags.contains("users")));
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

        //extractions
        Map<String, Object> extractRoot = jsonPath.getMap("");
        System.out.println(extractRoot);

        Boolean firstEnvironmentActive = jsonPath.getBoolean("environments[0].active");
        //System.out.println(firstEnvironmentActive);

        Boolean secondEnvironmentActive = jsonPath.getBoolean("environments[1].active");
        //System.out.println(secondEnvironmentActive);

        String extractRelease = jsonPath.getString("release");
        //System.out.println(extractRelease);

        List<Map<String, Object>> allEnvironments = jsonPath.getList("environments");
        //System.out.println("environments size = " + allEnvironments.size());
        //System.out.println("environments = " + allEnvironments);
        //System.out.println(allEnvironments);

        List<Map<String, Object>> firstEnvironmentServices = jsonPath.getList("environments[0].services");
        //System.out.println(firstEnvironmentServices);

        String firstEnvironmentServicesName = firstEnvironmentServices.get(0).get("name").toString();

        List<Integer> firstServiceResponseTimes = jsonPath.getList("environments[0].services[0].responseTimesMs");
        //System.out.println(firstServiceResponseTimes);

        List<Integer> secondServiceResponseTimes = jsonPath.getList("environments[0].services[1].responseTimesMs");
        //System.out.println(secondServiceResponseTimes);

        List<Map<String, Object>> secondEnvironmentServices = jsonPath.getList("environments[1].services");
        //System.out.println(secondEnvironmentServices);

        List<Integer> secondEnvironmentResponseTimes = jsonPath.getList("environments[1].services[0].responseTimesMs");
        //System.out.println(secondEnvironmentResponseTimes);

        List<Map<String, Object>> incidents = jsonPath.getList("incidents");
        //System.out.println(incidents);

        //assertions
        //The release is exactly "2026.07"
        assertEquals("2026.07", extractRoot.get("release"));

        //There are exactly two environments
        assertEquals(2, allEnvironments.size());

        //The first environment is active.
        assertTrue(firstEnvironmentActive);

        //The second environment is not active.
        assertFalse(secondEnvironmentActive);

        //The first environment contains exactly two services.
        assertEquals(2, firstEnvironmentServices.size());

        //every service in the first environment has status "UP"
        assertTrue(firstEnvironmentServices.stream().allMatch(service -> "UP".equals(service.get("status"))));

        //No service in the first environment has a blank name.
        assertTrue(firstEnvironmentServicesName != null && !firstEnvironmentServicesName.isBlank());

        //The first service contains exactly three response times.
        assertEquals(3, firstServiceResponseTimes.size());

        //Every response time for the first service is greater than zero.
        assertTrue(firstServiceResponseTimes.stream().noneMatch(responseTimesMs -> responseTimesMs > 0));

        //At least one response time for the second service is greater than 170.
        assertTrue(secondServiceResponseTimes.stream().anyMatch(responseTimesMs -> responseTimesMs > 170));

        //The second environment contains a service with status "DOWN"
        assertTrue(secondEnvironmentServices.stream().anyMatch(service -> "DOWN".equals(service.get("status"))));

        //The staging service’s response-time list is empty.
        assertTrue(secondEnvironmentResponseTimes.isEmpty());

        //The incident list exists.
        assertNotNull(incidents);

        //The incident list is empty.
        assertTrue(incidents.isEmpty());
    }

    @Test
    void shouldValidateOrderData() {
        String json = """
                {
                  "batchId": "BATCH-1001",
                  "orders": [
                    {
                      "orderId": 101,
                      "customer": {
                        "name": "Alice",
                        "email": "alice@example.com"
                      },
                      "items": [
                        {
                          "quantity": 2,
                          "product": {
                            "sku": "KB-01",
                            "name": "Keyboard",
                            "price": 49.99
                          }
                        },
                        {
                          "quantity": 1,
                          "product": {
                            "sku": "MS-02",
                            "name": "Mouse",
                            "price": 19.50
                          }
                        }
                      ],
                      "discountCode": null
                    },
                    {
                      "orderId": 102,
                      "customer": {
                        "name": "Bob",
                        "email": "bob@example.com"
                      },
                      "items": [],
                      "discountCode": "SAVE10"
                    }
                  ]
                  }
                """;

        JsonPath jsonPath = new JsonPath(json);

        //EXTRACTIONS
        Map<String, Object> jsonRoot = jsonPath.getMap("");
        //System.out.println(jsonRoot);

        String batchId = (String) jsonRoot.get("batchId");
        //System.out.println(batchId);

        List<Map<String, Object>> orders = jsonPath.getList("orders");
        //System.out.println(orders);
        //System.out.println("orders list size = " + orders.size());

        Map<String, Object> firstOrder = orders.get(0);
        //System.out.println(firstOrder);

        Map<String, Object> secondOrder = orders.get(1);
        //System.out.println(secondOrder);

        Map<String, Object> firstCustomer = jsonPath.getMap("orders[0].customer");
        //System.out.println(firstCustomer);

        String firstCustomerEmail = (String) firstCustomer.get("email");

        String firstCustomerName = (String) firstCustomer.get("name");
        //System.out.println(firstCustomerName);

        List<Map<String, Object>> firstOrderItems = jsonPath.getList("orders[0].items");
        //System.out.println(firstOrderItems);

        List<Integer> firstOrderItemsQuantity = jsonPath.getList("orders[0].items.quantity");

        Map<String, Object> firstItem = firstOrderItems.get(0);
        //System.out.println(firstItem);

        Map<String, Object> secondItem = firstOrderItems.get(1);
        //System.out.println(secondItem);

        List<Map<String, Object>> firstOrderProducts = jsonPath.get("orders[0].items.product");
        //System.out.println(firstOrderProducts);

        String firstItemSku = (String) firstOrderProducts.get(0).get("sku");
        //System.out.println(firstItemSku);

        List<String> firstOrderSkus = jsonPath.get("orders[0].items.product.sku");
        //System.out.println(firstOrderSkus);

        String firstOrderSkuOne = firstOrderSkus.getFirst();
       // System.out.println(firstOrderSkuOne);

        String firstOrderSkuTwo = firstOrderSkus.get(1);
        //System.out.println(firstOrderSkuTwo);

        Float firstProductPrice = (Float) firstOrderProducts.get(0).get("price");
        //Object price = firstOrderProduct.get("price");
        //System.out.println(price);
        //System.out.println(price.getClass().getName());

        //String firstOrderDiscountCode = (String) orders.get(0).get("discountCode");
        Object firstDiscountCode = firstOrder.get("discountCode");
        //System.out.print(firstDiscountCode);

        //Map<String, Object> secondOrderProduct = jsonPath.get("orders[0].items[1].product");
        //System.out.println(secondOrderProduct);
        Map<String, Object> secondProduct = jsonPath.getMap("orders[0].items[1].product");

        //List secondOrderItems = jsonPath.getList("orders[1].items");
        //System.out.println(secondOrderItems);
        List<Map<String, Object>> secondOrderItems = jsonPath.getList("orders[1].items");

        String secondOrderDiscountCode = (String) orders.get(1).get("discountCode");
        //System.out.println(secondOrderDiscountCode);


        //ASSERTIONS
        assertEquals("BATCH-1001", batchId);

        assertEquals(2, orders.size());

        assertEquals(101,firstOrder.get("orderId"));

        assertEquals("Alice", firstCustomer.get("name"));

        //assertNotNull(firstCustomer.get("email"));
        assertTrue(firstCustomerEmail != null && !firstCustomerEmail.isBlank());

        assertEquals(2, firstOrderItems.size());

        assertTrue(firstOrderItemsQuantity.stream().allMatch(quantity -> quantity > 0));

        //assertTrue(firstOrderItems.stream().allMatch(item -> item.get("product") != null));
        assertTrue(
                firstOrderProducts.stream().noneMatch(product -> {
                            Object sku = product.get("sku");
                            return sku == null || ((String) sku).isBlank();
                        })
        );

        assertTrue(firstOrderItems.stream().noneMatch(item -> item.get("product.sku") != null));

        assertTrue(firstOrderProducts.stream().anyMatch(product -> (Float) product.get("price") > 40));

        assertEquals("Keyboard", firstOrderProducts.getFirst().get("name"));

        assertNotNull(secondOrderItems);

        assertTrue(secondOrderItems.isEmpty());

        //assertTrue(firstDiscountCode == null);
        assertNull(firstDiscountCode);

        assertEquals("SAVE10", secondOrderDiscountCode);

        assertNotEquals(firstOrderSkuOne, firstOrderSkuTwo);
    }
}