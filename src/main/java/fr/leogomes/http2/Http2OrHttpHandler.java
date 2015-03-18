package fr.leogomes.http2;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.codec.http2.DefaultHttp2FrameReader;
import io.netty.handler.codec.http2.DefaultHttp2FrameWriter;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2InboundFrameLogger;
import io.netty.handler.codec.http2.Http2OrHttpChooser;
import io.netty.handler.codec.http2.Http2OutboundFrameLogger;
import io.netty.handler.codec.http2.HttpToHttp2ConnectionHandler;
import io.netty.handler.codec.http2.InboundHttp2ToHttpAdapter;

import javax.net.ssl.SSLEngine;

/**
 * Used during protocol negotiation, the main function of this handler is to
 * return the HTTP/1.1 or HTTP/2 handler once the protocol has been negotiated.
 * 
 * @author Leonardo Gomes <http://leogomes.fr>
 */
public class Http2OrHttpHandler extends Http2OrHttpChooser {

  protected Http2OrHttpHandler(int maxHttpContentLength) {
    super(maxHttpContentLength);
  }

  @Override
  protected SelectedProtocol getProtocol(SSLEngine engine) {
    String[] protocol = engine.getSession().getProtocol().split(":");
    if (protocol != null && protocol.length > 1) {
      SelectedProtocol selectedProtocol = SelectedProtocol.protocol(protocol[1]);
      System.err.println("Selected Protocol is " + selectedProtocol);
      return selectedProtocol;
    }
    return SelectedProtocol.UNKNOWN;
  }

  @Override
  protected void addHttp2Handlers(ChannelHandlerContext ctx) {
    DefaultHttp2Connection connection = new DefaultHttp2Connection(true);
    DefaultHttp2FrameWriter writer = new DefaultHttp2FrameWriter();
    DefaultHttp2FrameReader reader = new DefaultHttp2FrameReader();
    InboundHttp2ToHttpAdapter listener = new InboundHttp2ToHttpAdapter.Builder(connection)
    .propagateSettings(true)
    .validateHttpHeaders(false)
    .maxContentLength(1024 * 100)
    .build();
    
    //ctx.pipeline().addLast("http2toHttp", new TilesHttp2ToHttpHandler(connection, reader, writer, listener));
    ctx.pipeline().addLast("httpToHttp2", new HttpToHttp2ConnectionHandler(connection, 
        new Http2InboundFrameLogger(reader, TilesHttp2ToHttpHandler.logger), 
        new Http2OutboundFrameLogger(writer, TilesHttp2ToHttpHandler.logger), listener));
    ctx.pipeline().addLast("fullHttpRequestHandler", new FullHttpMessageHandler());
  }

  @Override
  protected ChannelHandler createHttp1RequestHandler() {
    return new TilesHttp1Handler();
  }

  @Override
  protected Http2ConnectionHandler createHttp2RequestHandler() {
    return null; //NOOP
  }

}
