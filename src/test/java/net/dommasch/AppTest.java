package net.dommasch;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

public class AppTest
{
    @Before
    public void setUp() throws Exception
    {
        App.main(new String[] { });
        App.reset();
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
                .body("posts.post_id", Matchers.contains(1))
                .body("posts.title", Matchers.contains("hai"))
                .body("posts.body", Matchers.contains("hai"))
                .statusCode(HttpServletResponse.SC_OK);
    }

    @Test
    public void testBadGet() throws Exception
    {
        RestAssured
                .given()
                .get("http://localhost:8080/foo")
                .then()
                .statusCode(HttpServletResponse.SC_NOT_FOUND);
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
                .statusCode(HttpServletResponse.SC_OK);
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
                .statusCode(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void testCombo()
    {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body("{ \"title\": \"a new message\", \"body\": \"Here is some new text.\" }")
                .post("http://localhost:8080/post")
                .then()
                .statusCode(HttpServletResponse.SC_OK);

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body("{ \"title\": \"another new message\", \"body\": \"Here is even more new text.\" }")
                .post("http://localhost:8080/post")
                .then()
                .statusCode(HttpServletResponse.SC_OK);

        RestAssured
                .given()
                .get("http://localhost:8080/posts")
                .then()
                .body("posts.post_id", Matchers.contains(1, 2, 3))
                .body("posts.title", Matchers.contains("hai", "a new message", "another new message"))
                .body("posts.body", Matchers.contains("hai", "Here is some new text.", "Here is even more new text."))
                .statusCode(HttpServletResponse.SC_OK);
    }
}
