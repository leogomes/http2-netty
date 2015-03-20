package fr.leogomes;

import fr.leogomes.http.HttpServer;
import fr.leogomes.http2.Http2Server;

public class Launcher {

  public static void main(String[] args) {
    Http2Server http2 = new Http2Server();
    HttpServer http = new HttpServer();
    try {
      http2.start();
      System.err.println("Open your web browser and navigate to " + "http://" + Html.IP + ":" + HttpServer.PORT + '/');
      http.start().sync();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      http2.stop();
      http.stop();
    }
  }
}
