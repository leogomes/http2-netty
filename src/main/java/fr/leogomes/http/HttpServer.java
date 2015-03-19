package fr.leogomes.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
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
 * Demonstrates a http server using Netty to display a bunch of images,
 * simulate latency and compare it against the http2 implementation.
 * 
 * @author Leonardo Gomes <http://leogomes.fr>
 */
public class HttpServer {
  
  static final int PORT = Integer.parseInt(System.getProperty("http-port", "8080"));
  static final int MAX_CONTENT_LENGTH = 1024 * 100;

  public void start() throws Exception {

    // Configure the server.
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      ServerBootstrap b = new ServerBootstrap();
      b.option(ChannelOption.SO_BACKLOG, 1024);
      
      b.group(bossGroup, workerGroup)
       .channel(NioServerSocketChannel.class)
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
      System.err.println("Open your HTTP/2-enabled web browser and navigate to " + "http://127.0.0.1:" + PORT + '/');
      ch.closeFuture().sync();
      
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
  
  public static void main(String[] args) {
    try {
      new HttpServer().start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
