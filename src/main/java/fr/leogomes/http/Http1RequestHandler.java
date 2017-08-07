package fr.leogomes.http;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
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
    isKeepAlive = HttpUtil.isKeepAlive(request);
    if (HttpUtil.is100ContinueExpected(request)) {
      ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
    }
    super.channelRead0(ctx, request);
  }

  @Override
  protected void sendResponse(ChannelHandlerContext ctx, String streamId, int latency, FullHttpResponse response) {
    HttpUtil.setContentLength(response, response.content().readableBytes());
    ctx.executor().schedule(new Runnable() {
      public void run() {
        if (isKeepAlive) {
          response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
          ctx.writeAndFlush(response);
        } else {
          ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
      }
    }, latency, TimeUnit.MILLISECONDS);
  }
}
