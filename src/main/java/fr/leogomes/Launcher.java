package fr.leogomes;

import fr.leogomes.http.HttpServer;
import fr.leogomes.http2.Http2Server;

public class Launcher {

  public static void main(String[] args) {
    Http2Server http2 = new Http2Server();
    HttpServer http = new HttpServer();
    try {
      http2.start();
      http.start().sync();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      http2.stop();
      http.stop();
    }
  }
}
