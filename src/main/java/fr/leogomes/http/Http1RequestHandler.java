package fr.leogomes.http;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpHeaderValues;
import fr.leogomes.http2.Http2RequestHandler;

/**
 * Handles the requests for the tiled image using HTTP 1.x as a protocol.
 * 
 * @author Leonardo Gomes <http://leogomes.fr>
 */
public class Http1RequestHandler extends Http2RequestHandler {

  boolean isKeepAlive;

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
    isKeepAlive = HttpHeaderUtil.isKeepAlive(request);
    super.channelRead0(ctx, request);
  }

  @Override
  protected void sendResponse(ChannelHandlerContext ctx, String streamId, int latency, FullHttpResponse response) {
    HttpHeaderUtil.setContentLength(response, response.content().readableBytes());
    if (isKeepAlive) {
      response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
    } else {
      ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
  }
}
