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
}
