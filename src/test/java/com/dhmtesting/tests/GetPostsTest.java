package com.dhmtesting.tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

class GetPostsTest {

    @Test
    void shouldReturnPostOne(){

        Response responseSinglePost =
                given()
                        .baseUri("https://jsonplaceholder.typicode.com")
                .when()
                        .get("/posts/1");

        String getTitle = responseSinglePost.path("title");
        Integer getUserId = responseSinglePost.path("userId");
        System.out.print(responseSinglePost.asPrettyString());

        assertEquals(200, responseSinglePost.statusCode());
        assertEquals("sunt aut facere repellat provident occaecati excepturi optio reprehenderit", getTitle);
        assertEquals(1, getUserId);
    }

    @Test
    void shouldReturnPostsByUserId() {

        Response userPostsResponse =
                given()
                        .baseUri("https://jsonplaceholder.typicode.com")
                        .queryParam("userId", 1)
                .when()
                        .get("/posts");

        String secondTitle = userPostsResponse.path("[1].title");
        Integer secondPostId = userPostsResponse.path("[1].id");
        List<Integer> allUserIds = userPostsResponse.path("userId");

        System.out.println(userPostsResponse.asPrettyString());

        assertEquals(200, userPostsResponse.statusCode());
        assertFalse(allUserIds.isEmpty());
        assertEquals("qui est esse", secondTitle);
        assertTrue(allUserIds.stream().allMatch(userId -> userId.equals(1)));
        assertEquals(2, secondPostId);
    }

    @Test
    void shouldReturnNestedUserDetails(){

        Response nestedDataResponse =
                given()
                        .baseUri("https://jsonplaceholder.typicode.com")
                .when()
                        .get("/users/1");

        String cityName = nestedDataResponse.path("address.city");
        String zipCode = nestedDataResponse.path("address.zipcode");
        String addressLatitude = nestedDataResponse.path("address.geo.lat");
        String companyName = nestedDataResponse.path("company.name");
        String userName = nestedDataResponse.path("name");

        System.out.println(nestedDataResponse.asPrettyString());

        assertEquals(200, nestedDataResponse.statusCode());
        assertEquals("Leanne Graham", userName);
        assertEquals("Gwenborough", cityName);
        assertEquals("92998-3874", zipCode);
        assertEquals("-37.3159", addressLatitude);
        assertEquals("Romaguera-Crona", companyName);
    }

    @Test
    void shouldReturnAllUsersDetails(){

        Response returnAllUsers =
        given()
                .baseUri("https://jsonplaceholder.typicode.com")
        .when()
                .get("/users");

        //Data extractions
        String secondUsersName = returnAllUsers.path("[1].name");
        String secondUsersCity = returnAllUsers.path("[1].address.city");
        String secondUsersLat = returnAllUsers.path("[1].address.geo.lat");
        List<String> allCompanyNames = returnAllUsers.path("company.name");

        System.out.println(returnAllUsers.asPrettyString());

        //Assertions
        assertEquals(200, returnAllUsers.statusCode());
        assertNotNull(allCompanyNames);
        assertFalse(allCompanyNames.isEmpty());
        assertTrue(allCompanyNames.stream().allMatch(name -> name != null && !name.isBlank()));
    }
}