package fr.leogomes.http2;

import static io.netty.buffer.Unpooled.wrappedBuffer;
import static io.netty.handler.codec.http.HttpHeaderUtil.setContentLength;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static java.lang.Math.abs;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import fr.leogomes.Html;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.AsciiString;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http2.HttpUtil;
import io.netty.handler.codec.http2.InboundHttp2ToHttpAdapter;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Handles all the requests for data. It receives a {@link FullHttpRequest},
 * which has been converted by a {@link InboundHttp2ToHttpAdapter} before it
 * arrived here. For further details, check {@link Http2OrHttpHandler} where the
 * pipeline is setup.
 * 
 * @author Leonardo Gomes <http://leogomes.fr>
 */
public class Http2RequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

  static final String LATENCY = "latency";
  static final String Y = "y";
  static final String X = "x";
  static final String PATH = "/http2";
  static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
  static final byte[] dummy = "nothing".getBytes();

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

    QueryStringDecoder queryString = new QueryStringDecoder(request.uri());
    String streamId = streamId(request);
    
    // Check arguments: path must match and latency parameter must be present
    if (!PATH.equals(queryString.path()) || missing(queryString, LATENCY)) {
      sendDummy(ctx, streamId);
      return;
    }

    int latency = latency(queryString);
    
    if (missing(queryString, X) && missing(queryString, Y)) {
      handlePage(ctx, streamId, latency);
    } else {
      handleImage(queryString, ctx, streamId, latency);
    }
  }

  private void handleImage(QueryStringDecoder query, ChannelHandlerContext ctx, String streamId, int latency) {
    int x = toInt(query.parameters().get(X).get(0), 0);
    int y = toInt(query.parameters().get(Y).get(0), 0);
    byte[] image = ImageCache.instance().image(x, y);
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, wrappedBuffer(image));
    response.headers().set(CONTENT_TYPE, "image/jpeg");
    sendResponse(ctx, streamId, latency, response);
  }

  private void handlePage(ChannelHandlerContext ctx, String streamId, int latency) {
    ByteBuf content = Unpooled.wrappedBuffer(Html.HEADER, Html.body(latency), Html.FOOTER);
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, content);
    response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
    sendResponse(ctx, streamId, latency, response);
  }

  protected void sendResponse(ChannelHandlerContext ctx, String streamId, int latency, FullHttpResponse response) {
    setContentLength(response, response.content().readableBytes());
    setStreamId(response, streamId);
    ctx.executor().schedule(new Runnable() {
      public void run() {
        ctx.writeAndFlush(response);
      }
    }, latency, TimeUnit.MILLISECONDS);
  }
  
  private int latency(QueryStringDecoder queryString) {
    int latency = toInt(queryString.parameters().get(LATENCY).get(0), 0);
    // Make sure that latency is not too big
    return abs((latency > 1000 ? 0 : latency));
  }

  private String streamId(FullHttpRequest request) {
    return request.headers().get(HttpUtil.ExtensionHeaderNames.STREAM_ID.text());
  }

  private void setStreamId(FullHttpResponse response, String streamId) {
    response.headers().set(HttpUtil.ExtensionHeaderNames.STREAM_ID.text(), streamId);
  }

  private boolean missing(QueryStringDecoder query, String string) {
    List<String> values = query.parameters().get(string);
    return (query.parameters() == null || values == null || values.size() == 0 || EMPTY.equals(values.get(0)));
  }
  
  private void sendDummy(ChannelHandlerContext ctx, String streamId) {
    DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, wrappedBuffer(dummy));
    response.headers().set(CONTENT_TYPE, "text/plain");
    sendResponse(ctx, streamId, 0, response);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    System.err.print(cause);
    ctx.channel().close();
  }

}
