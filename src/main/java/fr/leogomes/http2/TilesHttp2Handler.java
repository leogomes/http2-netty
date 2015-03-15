package fr.leogomes.http2;

import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2FrameListener;

public class TilesHttp2Handler extends Http2ConnectionHandler {

  public TilesHttp2Handler(boolean server, Http2FrameListener listener) {
    super(server, listener);
    // TODO Auto-generated constructor stub
  }

  
  
}
