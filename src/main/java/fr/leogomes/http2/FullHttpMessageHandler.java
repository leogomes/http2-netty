package fr.leogomes.http2;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.AsciiString;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http2.HttpUtil;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FileUtils;

public class FullHttpMessageHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

  private static final String Y = "y";
  private static final String X = "x";
  private static final String PATH = "/http2";
  private static final byte[] NOTHING_HERE = "Nothing here for you :)".getBytes();
  private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
  private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");

  static final InternalLogger log = InternalLoggerFactory.getInstance(FullHttpMessageHandler.class);

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

    String streamId = request.headers().get(HttpUtil.ExtensionHeaderNames.STREAM_ID.text());
    QueryStringDecoder queryString = new QueryStringDecoder(request.uri());
    Map<String, List<String>> params = queryString.parameters();

    // Check arguments
    if (!PATH.equals(queryString.path()) || missing(params, "latency")) {
      writeDummyResponse(ctx, streamId);
      return;
    }

    int latency = toInt(params.get("latency").get(0), 0);
    latency = (latency > 1000 ? 0 : latency); // Make sure that latency is not
                                              // too big
    if (missing(params, X) && missing(params, Y)) {
      handlePage(ctx, streamId, latency);
    } else {
      handleImage(params, ctx, streamId, latency);  
    }
    
    log.info("Sent back response");

  }

  private void handleImage(Map<String, List<String>> params, ChannelHandlerContext ctx, String streamId, int latency) {
    int x = toInt(params.get(X).get(0), 0);
    int y = toInt(params.get(Y).get(0), 0);

    try {
      File file = new File(getClass().getResource("tile-" + y + "-" + x + ".jpeg").toURI());
      byte[] fileBytes = FileUtils.readFileToByteArray(file);
      FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(fileBytes));
      response.headers().set(CONTENT_TYPE, "image/jpeg");
      HttpHeaderUtil.setContentLength(response, fileBytes.length);
      response.headers().set(HttpUtil.ExtensionHeaderNames.STREAM_ID.text(), streamId);

      // Write the initial line and the header.
      ctx.writeAndFlush(response);


    } catch (URISyntaxException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  private void handlePage(ChannelHandlerContext ctx, String streamId, int latency) {
    ByteBuf content = Unpooled.wrappedBuffer(Html.HEADER, Html.body(latency), Html.FOOTER);
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, content);
    response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
    response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
    response.headers().set(HttpUtil.ExtensionHeaderNames.STREAM_ID.text(), streamId);
    ctx.writeAndFlush(response);
  }

  private void writeDummyResponse(ChannelHandlerContext ctx, String streamId) {
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(NOTHING_HERE));
    response.headers().set(CONTENT_TYPE, "text/plain");
    response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
    response.headers().set(HttpUtil.ExtensionHeaderNames.STREAM_ID.text(), streamId);
    ctx.writeAndFlush(response);
  }

  private boolean missing(Map<String, List<String>> params, String string) {
    List<String> values = params.get(string);
    return (params == null || values == null || values.size() == 0 || EMPTY.equals(values.get(0)));
  }

}
