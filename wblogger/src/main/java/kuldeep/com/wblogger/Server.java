package kuldeep.com.wblogger;

import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Implementation of a very basic HTTP server. The contents are loaded from the assets folder. This
 * server handles one request at a time. It only supports GET method.
 */
 class Server implements Runnable {

    private static final String TAG = "SimpleWebServer";

    public  static  final  String PORT="port";
    /**
     * The port number we listen to
     */
    private final int mPort;

    /**
     * True if the server is running.
     */
    private boolean mIsRunning;

    /**
     * The {@link ServerSocket} that we listen to.
     */
    private ServerSocket mServerSocket;

    /**
     * WebServer constructor.
     */
    public Server(int port) {
        mPort = port;

    }

    /**
     * This method starts the web server listening to the specified port.
     */
    public void start() {
        mIsRunning = true;
        Log.i(TAG, "start: Starting server at port>>"+mPort);
        new Thread(this).start();
    }

    /**
     * This method stops the web server
     */
    public void stop() {
        try {
            mIsRunning = false;
            if (null != mServerSocket) {
                mServerSocket.close();
                mServerSocket = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing the server socket.", e);
        }
    }

    public int getPort() {
        return mPort;
    }

    @Override
    public void run() {
        try {
            Log.i(TAG, "run: started server at port"+mPort );
            mServerSocket = new ServerSocket(mPort);
            while (mIsRunning) {
                Socket socket = mServerSocket.accept();
                handle(socket);
                socket.close();
            }
        } catch (SocketException e) {
            // The server was stopped; ignore.
        } catch (IOException e) {
            Log.e(TAG, "Web server error.", e);
        }
    }

    /**
     * Respond to a request from a client.
     *
     * @param socket The client socket.
     * @throws IOException
     */
    private void handle(Socket socket) throws IOException {
        Log.i(TAG, "handle: get request>>>");
        BufferedReader reader = null;
        PrintStream output = null;
        try {
            String route = null;

            // Read HTTP headers and parse out the route.
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while (!TextUtils.isEmpty(line = reader.readLine())) {
                if (line.startsWith("GET /")) {
                    int start = line.indexOf('/') + 1;
                    int end = line.indexOf(' ', start);
                    route = line.substring(start, end);
                    break;
                }
            }

            // Output stream that we send the response to

            Log.i(TAG, "handle: got route>>>"+route);
            output = new PrintStream(socket.getOutputStream());

            // Prepare the content to send.
//            if (!"logs/".equals(route)) {
//                writeServerError(output);
//                return;
//            }
            byte[] bytes = loadContent(route);
            // Send out the content.
            output.println("HTTP/1.0 200 OK");
            output.println("Content-Type: " + "text/html");
            output.println("Content-Length: " + bytes.length);
            output.println();
            output.write(bytes);
            output.flush();
        } finally {
            if (null != output) {
                output.close();
            }
            if (null != reader) {
                reader.close();
            }
        }
    }

    /**
     * Writes a server error response (HTTP/1.0 500) to the given output stream.
     *
     * @param output The output stream.
     */
    private void writeServerError(PrintStream output) {
        output.println("HTTP/1.0 500 Error in getting response");
        output.flush();
    }

    /**
     * Loads all the content of {@code fileName}.
     *
     * @param fileName The name of the file.
     * @return The content of the file.
     * @throws IOException
     */
    private byte[] loadContent(String fileName) throws IOException {
        Log.i(TAG, "loadContent: file path>>"+fileName);

        String outputString="";
        if(fileName!=null&&fileName.equals("logs/"))
        outputString=toHtml(readLogs().toString());
        else if(fileName!=null&&fileName.equals("log/api/")){
            outputString=toHtmlWithoutBody(readLogs().toString());

        }

            return outputString.getBytes();

    }


    private  String toHtmlWithoutBody(String string){
        BufferedReader st = new BufferedReader( new StringReader( string ) );
        StringBuffer buf = new StringBuffer( "<html><body>" );
        try
        {
            String str = st.readLine();
            while( str != null )
            {
                    if (str.equalsIgnoreCase("<br/>")) {
                        str = "<br>";
                    }
                    buf.append(str);
                    if (!str.equalsIgnoreCase("<br>")) {
                        buf.append("<br>");
                    }
                    str = st.readLine();
                }

        }
        catch( IOException e )
        {
            e.printStackTrace();
        }

        string = buf.toString();
        return string;
    }


    private String toHtml( String string )
    {
        if( string.isEmpty() )
            return "<html><body></body></html>";

        BufferedReader st = new BufferedReader( new StringReader( string ) );
        StringBuffer buf = new StringBuffer( "<html><body> <div id=\"content\">" );

        try
        {
            String str = st.readLine();

            while( str != null )
            {
                if( str.equalsIgnoreCase( "<br/>" ) )
                {
                    str = "<br>";
                }

                buf.append( str );

                if( !str.equalsIgnoreCase( "<br>" ) )
                {
                    buf.append( "<br>" );
                }

                str = st.readLine();
            }
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }

        buf.append( "" +
                "</div><script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>" +
                "<script> " +
                "$(window).on(\"load\",function(){" +
                "setInterval(function() {\n" +
                "getData();  // method to be executed;\n" +
                "}, 3000);" +
                "});" +
                "" +
                "function getData(){" +
                "$.ajax({" +
                "url:\"/log/api/\"," +
                "success:function(result){" +
                "console.log(result);" +
                "$(\"#content\").append(result);" +
                "}})};" +
                "" +
                "</script></body></html>" );
        string = buf.toString();
        return string;
    }




    public static StringBuilder readLogs() {
        StringBuilder logBuilder = new StringBuilder();
        try {
            String[] command = new String[] { "logcat", "-d", "-v", "threadtime" };

            Process process = Runtime.getRuntime().exec(command);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                logBuilder.append(line + "\n");
            }
            Runtime.getRuntime().exec("logcat -c");

        } catch (IOException e) {
        }
        return logBuilder;
    }

    /**
     * Detects the MIME type from the {@code fileName}.
     *
     * @param fileName The name of the file.
     * @return A MIME type.
     */
    private String detectMimeType(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        } else if (fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else {
            return "application/octet-stream";
        }
    }

}