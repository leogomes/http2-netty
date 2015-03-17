package fr.leogomes.http2;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Handles the exceptional case where HTTP 1.x was negotiated under TLS
 * 
 * @author Leonardo Gomes <http://leogomes.fr>
 */
public class TilesHttp1Handler extends SimpleChannelInboundHandler<HttpRequest> {

  private static final String response = "<html><body>"
      + "<h2>To view the example you need a browser that supports HTTP/2</h2>"
      + "<p>Try with Chrome 40+ or Firefox 36+</p>"
      + Html.FOOTER;

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
    if (HttpHeaderUtil.is100ContinueExpected(req)) {
      ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
    }

    ByteBuf content = ctx.alloc().buffer();
    content.writeBytes(response.getBytes());

    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, content);
    response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
    response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    // cause.printStackTrace(); FIXME log in debug mode
    ctx.close();
  }

}
