package fr.leogomes.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import org.apache.commons.lang3.StringUtils;

import fr.leogomes.http2.Http2RequestHandler;

/**
 * Handles the requests for the tiled image using HTTP 1.x as a protocol. It
 * just extends the {@link Http2RequestHandler} overriding the streamId related
 * operations to do nothing, since streams don't exist in HTTP 1.x. The
 * remaining logic is the same between Http 1.x and 2. Since the aim of this
 * example is demonstrate Http2 I will keep the logic there, instead of trying
 * to move it to an abstract common class.
 * 
 * @author Leonardo Gomes <http://leogomes.fr>
 */
public class Http1RequestHandler extends Http2RequestHandler {

  @Override
  protected String streamId(FullHttpRequest request) {
    return StringUtils.EMPTY;
  }

  @Override
  protected void setStreamId(FullHttpResponse response, String streamId) {
    // NOOP
  }

}
