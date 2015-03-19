package fr.leogomes.http2;

import java.util.Random;

/**
 * Just a bunch of hard-coded and dynamically generated HTML.
 * 
 * @author Leonardo Gomes <http://leogomes.fr>
 */
public class Html {

  static final byte[] HEADER = header("127.0.0.1/http2").getBytes();

  static final String FORK_ME = "<a href=\"https://github.com/leogomes/http2-netty\"><img style=\"position: absolute; top: 0; right: 0; border: 0;\" src=\"https://camo.githubusercontent.com/a6677b08c955af8400f44c6298f40e7d19cc5b2d/68747470733a2f2f73332e616d617a6f6e6177732e636f6d2f6769746875622f726962626f6e732f666f726b6d655f72696768745f677261795f3664366436642e706e67\" alt=\"Fork me on GitHub\" data-canonical-src=\"https://s3.amazonaws.com/github/ribbons/forkme_right_gray_6d6d6d.png\"></a>";

  static final byte[] FOOTER = ("<hr><a href='http://leogomes.fr'>&lt;&lt leogomes.fr</a>" + FORK_ME + "</body></html>")
      .getBytes();

  private static String header(String url) {
    return "<html><head lang=\"en\"><title>Netty HTTP/2 Example</title>"
        + "<style>body {background:#DDD;}</style></head>" + "<body>A grid of 200 tiled images is shown below. Compare:"
        + "<p>[<a href='https://" + url + "?latency=0'>HTTP/2, 0 latency</a>] [<a href='http://" + url
        + "?latency=0'>HTTP/1, 0 latency</a>]<br>" + "[<a href='https://" + url
        + "?latency=30'>HTTP/2, 30ms latency</a>] [<a href='http://" + url
        + "?latency=30'>HTTP/1, 30ms latency</a>]<br>" + "[<a href='https://" + url
        + "?latency=200'>HTTP/2, 200ms latency</a>] [<a href='http://" + url
        + "?latency=200'>HTTP/1, 200ms latency</a>]<br>" + "[<a href='https://" + url
        + "?latency=1000'>HTTP/2, 1s latency</a>] [<a href='http://" + url
        + "?latency=1000'>HTTP/1, 1s latency</a>]<br>";
  }

  public static byte[] body(int latency) {
    StringBuilder sb = new StringBuilder("<p>");
    int r = Math.abs(new Random().nextInt());
    for (int y = 0; y < 10; y++) {
      for (int x = 0; x < 20; x++) {
        sb.append("<img width=30 height=29 src='/http2?x=").append(x).append("&y=").append(y).append("&cachebust=")
            .append(r).append("&latency=").append(latency).append("'>");
      }
      sb.append("<br/>\r\n");
    }
    sb.append("</p>");
    return sb.toString().getBytes();
  }
}
