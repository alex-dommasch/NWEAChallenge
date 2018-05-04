package net.dommasch;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * launch jetty server
 */
public class App
{
    private static Server server;

    public static void main(String[] args) throws Exception
    {
        server = new Server(8080);
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(MyServlet.class, "/");
        server.setHandler(handler);
        server.start();
    }

    public static void stop() throws Exception
    {
        server.stop();
    }
}
