package fr.leogomes.http2;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.AsciiString;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class FullHttpMessageHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

  private static final String Y = "y";
  private static final String X = "x";
  private static final String PATH = "http2";
  private static final byte[] NOTHING_HERE = "Nothing here for you :)".getBytes();
  private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
  private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");

  static final InternalLogger log = InternalLoggerFactory.getInstance(FullHttpMessageHandler.class);

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
    
    QueryStringDecoder queryString = new QueryStringDecoder(request.uri());
    Map<String, List<String>> params = queryString.parameters();
    
    // Check arguments
    if(!PATH.equals(queryString.path()) || 
                   missing(params, X) || 
                   missing(params, Y) || 
                   missing(params, "latency")) {
      writeDummyResponse(ctx);
      return;
    }
    
    int x = toInt(params.get(X).get(0), 0);
    int y = toInt(params.get(X).get(0), 0);
    int latency = toInt(params.get("latency").get(0), 0);
    latency = (latency > 1000 ? 0 : latency); // Make sure that latency is not too big
    
    handlePage();
    handleImage();
    
    
//    log.info(str.toString());
    
  }


  private void writeDummyResponse(ChannelHandlerContext ctx) {
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(NOTHING_HERE));
    response.headers().set(CONTENT_TYPE, "text/plain");
    response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());    
  }
  
  private boolean missing(Map<String, List<String>> params, String string) {
    List<String> values = params.get(string);
    return (params != null && values != null && values.size() > 0 && 
            !EMPTY.equals(values.get(0)));
  }


  
}
