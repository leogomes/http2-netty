package fr.leogomes.http2;

import static io.netty.util.internal.logging.InternalLogLevel.INFO;
import io.netty.handler.codec.http2.DefaultHttp2FrameReader;
import io.netty.handler.codec.http2.DefaultHttp2FrameWriter;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.codec.http2.Http2InboundFrameLogger;
import io.netty.handler.codec.http2.Http2OutboundFrameLogger;
import io.netty.handler.codec.http2.InboundHttp2ToHttpAdapter;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * Displays the tiles example using HTTP/2
 * 
 * @author Leonardo Gomes <http://leogomes.fr>
 */
public class TilesHttp2ToHttpHandler extends Http2ConnectionHandler {

  private static final Http2FrameLogger logger = new Http2FrameLogger(INFO,
      InternalLoggerFactory.getInstance(TilesHttp2ToHttpHandler.class));

  public TilesHttp2ToHttpHandler(Http2Connection connection, int maxContentLength) {
    super(connection,
        new Http2InboundFrameLogger(new DefaultHttp2FrameReader(), logger),
        new Http2OutboundFrameLogger(new DefaultHttp2FrameWriter(), logger), 
        new InboundHttp2ToHttpAdapter.Builder(connection)
                                     .propagateSettings(true)
                                     .validateHttpHeaders(false)
                                     .maxContentLength(maxContentLength)
                                     .build());
  }
}
