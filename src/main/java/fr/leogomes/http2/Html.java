package fr.leogomes.http2;

import java.util.Random;

public class Html {
  
  static final String HEADER = header("leogomes.fr/http2");
  
  static final String FOOTER =  "<hr><a href='http://leogomes.fr'>&lt;&lt leogomes.fr</a></body></html>"; 
    
  private static String header(String url) {
    return "<html><head lang=\"en\"><title>Netty HTTP/2 Example</title>"
        + "<style>body {background:#DDD;}</style></head>"
        + "<body>A grid of 200 tiled images is shown below. Compare:" +
        "<p>[<a href='https://" + url + "?latency=0'>HTTP/2, 0 latency</a>] [<a href='http://" + url + "?latency=0'>HTTP/1, 0 latency</a>]<br>" +
        "[<a href='https://"+ url + "?latency=30'>HTTP/2, 30ms latency</a>] [<a href='http://" + url + "?latency=30'>HTTP/1, 30ms latency</a>]<br>" + 
        "[<a href='https://"+ url + "?latency=200'>HTTP/2, 200ms latency</a>] [<a href='http://" + url + "?latency=200'>HTTP/1, 200ms latency</a>]<br>" +
        "[<a href='https://"+ url + "?latency=1000'>HTTP/2, 1s latency</a>] [<a href='http://" + url + "?latency=1000'>HTTP/1, 1s latency</a>]<br>";
  }
  
  private static String body(String latency) {
    StringBuilder sb = new StringBuilder("<p>");
    double r = new Random().nextDouble();

    for (int y = 0; y < 20; y++) {
      for (int x = 0; x < 10; x++) {
        sb.append("<img width=29 height=30 src='/http2?x=").append(x)
          .append("&y=").append(y)
          .append("&cachebust=").append(r)
          .append("&latency=").append(latency);
      }
      sb.append("<br/>");
    }
    
    sb.append("</p>");
    return sb.toString();
  }
}
