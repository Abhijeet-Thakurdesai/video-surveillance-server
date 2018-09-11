package server;


import com.googlecode.javacv.cpp.videoInputLib.videoInput;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.concurrent.Executors;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.util.List;
import javax.imageio.ImageIO;

import util.ConnectionManager;
import util.FileHelper;
import util.SendRotationsMotor;
import util.ServerConstants;
import util.StringHelper;

public class RemoteVideoStreamingServer {

    public static final int SERVER_PORT = 7878;
    public static int selcam = 0;
    public static int selqual = 2;
    public static PTZCameraController sc = null;

    public static void main(String[] args) {
        try {

            startMainServer();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static HttpServer server = null;

    public static void startMainServer() throws IOException {
        Main.setTheme();
        HashMap preStoredValues = FileHelper.deserializeObject();
        System.out.println("preStoredValues:- "+preStoredValues);

        if (preStoredValues != null) {
            FileHelper.setEnteredValues(ServerConstants.class, preStoredValues);
        }
        sc = new PTZCameraController(selcam, selqual);
        if (server == null) {
            InetSocketAddress addr = new InetSocketAddress(SERVER_PORT);

            server = HttpServer.create(addr, 0);

            server.createContext("/", new MyHandler());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            System.out.println("Server is listening on port " + SERVER_PORT);
        }
        sc.setVisible(true);
        sc.startApp();

    }

    public static class MyHandler implements HttpHandler {

        public void handle(HttpExchange exchange) throws IOException {

            try {
                System.out.println("Got Connection");
                boolean skipWriting = false;
                String requestMethod = exchange.getRequestMethod();
                String uri = exchange.getRequestURI().getQuery();
                System.out.println("URI " + uri);
                uri = URLDecoder.decode(uri);

                HashMap parameters = new HashMap();
                if (uri != null) {
                    String tokens[] = uri.split("&");
                    System.out.println("tokens " + tokens.length);
                    for (String keyvalue : tokens) {
                        String tok[] = keyvalue.split("=");
                        String key = "", value = "";
                        if (tok.length > 0) {
                            key = tok[0];
                        }
                        if (tok.length > 1) {
                            value = tok[1];
                        }
                        parameters.put(key, URLDecoder.decode(value));
                    }
                }
                String method = StringHelper.n2s(parameters.get("method"));
                Headers responseHeaders = exchange.getResponseHeaders();
                if (!method.equalsIgnoreCase("receiveImage")) {
                    responseHeaders.set("Content-Type", "text/plain");
                }

                exchange.sendResponseHeaders(200, 0);
                OutputStream responseBody = exchange.getResponseBody();


                System.out.println("Method " + method);
                StringBuffer sb = new StringBuffer();
                if (method.equalsIgnoreCase("checkIMEI")) {
                    String imei = StringHelper.n2s(parameters.get("imei"));

                    List success = ConnectionManager.isIMEIExists(imei);
                    if (success.size() > 0) {
                        HashMap map = (HashMap) success.get(0);
                        String loginid = StringHelper.n2s(map.get("loginid"));
                        sb.append("true");
                        ConnectionManager.saveActivityLog("PHONE- IMEI " + imei + "-" + loginid + " Accessing from phone");
                    } else {
                        sb.append("false");
                        ConnectionManager.saveActivityLog("PHONE- IMEI " + imei + " Unauthorized Access");

                    }
                } else if (method.equalsIgnoreCase("getCameraList")) {
                    videoInput.listDevices();
                    for (int i = 0; i < videoInput.listDevices(); i++) {
                        String string = videoInput.getDeviceName(i);
                        sb.append(string + "\n");
                    }
                } else if (method.equalsIgnoreCase("getEmergencyContact")) {
                    sb.append(ConnectionManager.getEmegencyContactsPhone());
                } else if (method.equalsIgnoreCase("receiveImage")) {
                    System.out.println(" Send Image... ");
                    try {
                        if (PTZCameraController.buffimg != null) {
                            ImageIO.write(PTZCameraController.buffimg, "jpg", responseBody);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    responseBody.flush();
                } else if (method.equalsIgnoreCase("launchCamera")) {
                    int cameraNo = StringHelper.n2i(parameters.get("camerno"));
                    try {
                        selcam = cameraNo;
                        try {
                            sc.stopCamera();
                        } catch (Exception e) {
                        }
                        sc.setVisible(false);
                        sc.dispose();
                        sc = new PTZCameraController(cameraNo, selqual);
                        sc.setVisible(true);
                        sc.startApp();
                    } catch (Exception e) {
                    }
                    responseBody.flush();
                } else if (method.equalsIgnoreCase("changequality")) {
                    int qual = StringHelper.n2i(parameters.get("quality"));
                    try {
                        selqual = qual;
                        try {
                            sc.stopCamera();
                        } catch (Exception e) {
                        }
                        sc.setVisible(false);
                        sc.dispose();
                        sc = new PTZCameraController(selcam, selqual);
                        sc.setVisible(true);
                        sc.startApp();
                    } catch (Exception e) {
                    }
                    responseBody.flush();
                } else if (method.equalsIgnoreCase("getQuality")) {
                    String arr[] = new String[]{
                        "320x240", "640x480", "800x600", "1280x720"};
                    for (int i = 0; i < arr.length; i++) {
                        String string = arr[i];
                        sb.append(string + "\n");
                    }
                    skipWriting = false;
                }else if (method.equalsIgnoreCase("faceTracking")) {
                    int rotations = StringHelper.n2i(parameters.get("r")); // 1=on 0=off
                    if(rotations==0){
                        ServerConstants.ENABLE_FACE_TRACKING=false;
                    }
                    else
                        ServerConstants.ENABLE_FACE_TRACKING=true;
                    skipWriting = false;
                }


                 else if (method.equalsIgnoreCase("rotate")) {
                    int rotations = StringHelper.n2i(parameters.get("r"));


                    String rotationString = "";
                    if (rotations > 0) {
                            rotationString = "R";
                    } else {
                        rotationString = "L";
                    }



                    final String rt = rotationString;
                    new Thread() {
                        public void run() {
                            super.run();
                            System.out.println("Sending Rotations "+rt);
                            SendRotationsMotor.sendData(rt);
                        }
                    }.start();

                    skipWriting = false;
                }else if (method.equalsIgnoreCase("changemode")) {
                    boolean mode = StringHelper.n2b(parameters.get("mode"));
                    try {
                        try {
                            sc.stopCamera();
                        } catch (Exception e) {
                        }
                        sc.dispose();
                        sc = new PTZCameraController(selcam, selqual);
                        sc.setMode(mode);
                        sc.setVisible(true);
                        sc.startApp();
                    } catch (Exception e) {
                    }
                    responseBody.flush();
                } else if (method.equalsIgnoreCase("playAlarm")) {
                    Thread playAlarm = new Thread() {

				                           public void run() {
				                               try {
				                                   ConnectionManager.saveActivityLog("Phone - Playing Alarm");
				                                 StringHelper.runcommand("cmd /k \""+new java.io.File (ServerConstants.VOICE_CLIP).getCanonicalPath()+"\"");
				                               } catch (Exception ex) {
				                                   ex.printStackTrace();
				                               }
				                           }
				                       };
                    playAlarm.start();
                }

                if (!skipWriting) {
                    System.out.println("after method");
                    System.out.println("StringBUff"+sb.toString());
                    responseBody.write(sb.toString().getBytes());
                    responseBody.flush();
                    responseBody.close();
                }
                System.out.print("..........Completing Response from here ..............");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
    public static int prevRotations=0;
}