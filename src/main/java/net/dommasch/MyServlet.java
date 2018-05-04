package net.dommasch;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class MyServlet extends HttpServlet
{
    public MyServlet()
    {
        System.out.println("MyServlet started");
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {
        final String servletPath = req.getServletPath();
        if (!servletPath.equals("/posts")) resp.sendError(HttpServletResponse.SC_NOT_FOUND);

        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json");
        try (PrintWriter printWriter = resp.getWriter())
        {
            printWriter.print("{ \"posts\": [ " +
                    "{ \"post_id\": 123, \"title\": \"hello\", \"body\": \"Hello World!\"}, " +
                    "{ \"post_id\": 456, \"title\": \"hi\", \"body\": \"Me again.\"}, " +
                    "{ \"post_id\": 789, \"title\": \"bye bye\", \"body\": \"Signing off.\"} ] }");
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
        }
    }
}
