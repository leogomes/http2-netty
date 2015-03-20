package fr.leogomes.http2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http2.Http2OrHttpChooser.SelectedProtocol;
import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolConfig.Protocol;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectedListenerFailureBehavior;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectorFailureBehavior;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.SupportedCipherSuiteFilter;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

/**
 * Demonstrates a Http2 server using Netty to display a bunch of images and
 * simulate latency.
 * 
 * @author Leonardo Gomes <http://leogomes.fr>
 */
public class Http2Server {

  static final String IP = System.getProperty("ip", "127.0.0.1");
  static final int PORT = Integer.parseInt(System.getProperty("http2-port", "8443"));
  static final int MAX_CONTENT_LENGTH = 1024 * 100;

  private final EventLoopGroup bossGroup;
  private final EventLoopGroup workerGroup;

  public Http2Server() {
    bossGroup = new NioEventLoopGroup(1);
    workerGroup = new NioEventLoopGroup();
  }

  public ChannelFuture start() throws Exception {
    final SslContext sslCtx = configureTLS();
    ServerBootstrap b = new ServerBootstrap();
    b.option(ChannelOption.SO_BACKLOG, 1024);
    b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
    // .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(sslCtx.newHandler(ch.alloc()), new Http2OrHttpHandler(MAX_CONTENT_LENGTH));
          }
        });

    Channel ch = b.bind(PORT).sync().channel();
    System.err.println("Open your HTTP/2-enabled web browser and navigate to " + "https://127.0.0.1:" + PORT + '/');
    return ch.closeFuture();
  }

  public void stop() {
    bossGroup.shutdownGracefully();
    workerGroup.shutdownGracefully();
  }

  private SslContext configureTLS() throws CertificateException, SSLException {
    SelfSignedCertificate ssc = new SelfSignedCertificate();
    final SslContext sslCtx = SslContext.newServerContext(SslProvider.JDK, ssc.certificate(), ssc.privateKey(), null,
        Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE,
        new ApplicationProtocolConfig(Protocol.ALPN, SelectorFailureBehavior.FATAL_ALERT,
            SelectedListenerFailureBehavior.FATAL_ALERT, SelectedProtocol.HTTP_2.protocolName(),
            SelectedProtocol.HTTP_1_1.protocolName()), 0, 0);
    return sslCtx;
  }
}
