package net.dommasch;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

/**
 * Unit test for simple App.
 */
public class AppTest
{
    @Before
    public void setUp() throws Exception
    {
        App.main(new String[] { });
    }

    @After
    public void tearDown() throws Exception
    {
        App.stop();
    }

    @Test
    public void testGet() throws Exception
    {
        RestAssured
                .given()
                .get("http://localhost:8080/posts")
                .then()
                .body("posts.post_id", Matchers.contains(123, 456, 789))
                .body("posts.post_id", Matchers.contains(123, 456, 789))
                .body("posts.post_id", Matchers.contains(123, 456, 789))
        ;
    }

    @Test
    public void testBadGet() throws Exception
    {
        RestAssured
                .given()
                .get("http://localhost:8080/foo")
                .then()
                .statusCode(HttpServletResponse.SC_NOT_FOUND)
        ;
    }

    @Test
    public void testPost()
    {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body("{ \"title\": \"ok\", \"body\": \"Try this.\" }")
                .post("http://localhost:8080/post")
                .then()
                .statusCode(200);
    }

    @Test
    public void testBadPost()
    {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body("{ \"title\": \"ok\", \"body\": \"Try this.\" }")
                .post("http://localhost:8080/bar")
                .then()
                .statusCode(404);
    }
}
