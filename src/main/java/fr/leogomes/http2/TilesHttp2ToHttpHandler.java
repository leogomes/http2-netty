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

  static final Http2FrameLogger logger = new Http2FrameLogger(INFO,
      InternalLoggerFactory.getInstance(TilesHttp2ToHttpHandler.class));

  public TilesHttp2ToHttpHandler(Http2Connection connection, DefaultHttp2FrameReader reader, 
      DefaultHttp2FrameWriter writer,InboundHttp2ToHttpAdapter listener) {
    super(connection,
        new Http2InboundFrameLogger(reader, logger),
        new Http2OutboundFrameLogger(writer, logger), 
        listener);
  }
}
