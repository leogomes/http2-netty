package fr.leogomes.http2;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2OrHttpChooser;

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
  protected ChannelHandler createHttp1RequestHandler() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected Http2ConnectionHandler createHttp2RequestHandler() {
    // TODO Auto-generated method stub
    return null;
  }

}
