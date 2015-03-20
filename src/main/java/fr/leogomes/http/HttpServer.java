package fr.leogomes.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Demonstrates a http server using Netty to display a bunch of images, simulate
 * latency and compare it against the http2 implementation.
 * 
 * @author Leonardo Gomes <http://leogomes.fr>
 */
public class HttpServer {

  public static final int PORT = Integer.parseInt(System.getProperty("http-port", "80"));
  static final int MAX_CONTENT_LENGTH = 1024 * 100;

  private final EventLoopGroup bossGroup;
  private final EventLoopGroup workerGroup;

  public HttpServer() {
    bossGroup = new NioEventLoopGroup(1);
    workerGroup = new NioEventLoopGroup();
  }

  public ChannelFuture start() throws Exception {
    ServerBootstrap b = new ServerBootstrap();
    b.option(ChannelOption.SO_BACKLOG, 1024);

    b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
        //.handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("httpRequestDecoder", new HttpRequestDecoder());
            pipeline.addLast("httpResponseEncoder", new HttpResponseEncoder());
            pipeline.addLast("httpChunkAggregator", new HttpObjectAggregator(MAX_CONTENT_LENGTH));
            pipeline.addLast("httpRequestHandler", new Http1RequestHandler());
          }
        });

    Channel ch = b.bind(PORT).sync().channel();
    return ch.closeFuture();
  }

  public void stop() {
    bossGroup.shutdownGracefully();
    workerGroup.shutdownGracefully();
  }

}
