package net.dommasch;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class MyServlet extends HttpServlet
{
    public static final String POST_ID = "post_id";
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String POSTS = "posts";

    public MyServlet()
    {
        System.out.println("MyServlet started");
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {
        final String servletPath = req.getServletPath();
        if (!servletPath.equals("/" + POSTS)) resp.sendError(HttpServletResponse.SC_NOT_FOUND);

        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json");
        try (PrintWriter printWriter = resp.getWriter())
        {
            try (Connection connection = getConnection())
            {
                try (Statement statement = connection.createStatement())
                {
                    try (ResultSet resultSet = statement.executeQuery("select " + POST_ID + ", " + TITLE + ", " + BODY + " from " + POSTS))
                    {
                        final JsonArray jsonArray = new JsonArray();
                        while (resultSet.next())
                        {
                            final JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty(POST_ID, resultSet.getInt(POST_ID));
                            jsonObject.addProperty(TITLE, resultSet.getString(TITLE));
                            jsonObject.addProperty(BODY, resultSet.getString(BODY));
                            jsonArray.add(jsonObject);
                        }
                        final JsonObject jsonObject = new JsonObject();
                        jsonObject.add(POSTS, jsonArray);
                        printWriter.print(jsonObject.toString());
                    }
                }
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {
        final String servletPath = req.getServletPath();
        if (!servletPath.equals("/post")) resp.sendError(HttpServletResponse.SC_NOT_FOUND);

        try (BufferedReader reader = req.getReader())
        {
            final StringBuilder sb = new StringBuilder();
            while (true)
            {
                final int c = reader.read();
                if (-1 == c) break;
                sb.append((char) c);
            }
            final String s = sb.toString();
            System.out.println("post: " + s);
            final JsonObject jsonObject = new JsonParser().parse(s).getAsJsonObject();

            try (Connection connection = getConnection())
            {
                try (PreparedStatement preparedStatement = connection.prepareStatement("insert into " + POSTS + " (" + TITLE + ", " + BODY + ") VALUES (?, ?)"))
                {
                    preparedStatement.setString(1, jsonObject.get(TITLE).getAsString());
                    preparedStatement.setString(2, jsonObject.get(BODY).getAsString());
                    preparedStatement.executeUpdate();
                }
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private static Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection("jdbc:sqlite:data/blog.db");
    }

    // for tests only
    public static void reset()
    {
        try (Connection connection = getConnection())
        {
            try (PreparedStatement preparedStatement = connection.prepareStatement("delete from " + POSTS))
            {
                preparedStatement.executeUpdate();
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement("delete from sqlite_sequence"))
            {
                preparedStatement.executeUpdate();
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement("insert into " + POSTS + " (" + TITLE + ", " + BODY + ") VALUES (?, ?)"))
            {
                preparedStatement.setString(1, "hai");
                preparedStatement.setString(2, "hai");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
