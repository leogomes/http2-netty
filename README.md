# http2-netty

This is a [Netty](http://netty.io/)-based version of the tiles HTTP/2 example [published by Go lang](https://http2.golang.org/gophertiles).

It shows the loading of an image composed by 200 tiles using both HTTP/2 and HTTP 1.x. You can also simulate different latencies and that's where HTTP/2 really makes a difference.

![screenshot](https://cloud.githubusercontent.com/assets/84847/6761805/fde6dc28-cf56-11e4-845e-180c3ef4663a.png)

To run the example you will need
- Java 8
- Jetty ALPN library
- HTTP/2-powered browser (preferably Chrome 40+)
- A patched version of Netty 4.1, if your browser don't support HTTP/2 Draft 16 yet.

The ALPN library must match the version of your JDK (I'm using 1.8.0_25). Check out [the documentation](http://eclipse.org/jetty/documentation/current/alpn-chapter.html#alpn-versions) to know which version you should use.
The jars can be obtained from your local maven repository, or [downloaded here](http://mvnrepository.com/artifact/org.mortbay.jetty.alpn/alpn-boot).

Since Netty currently supports draft 16 of the HTTP/2 and both Chrome and Firefox are still on Draft 14, you will need to tweak Netty 
to use draft 14. I simply [changed the version constant on `Http2CodecUtil`](https://github.com/netty/netty/blob/f691ae558cb2305a6c55aae3eb11e9f7a29b754e/codec-http2/src/main/java/io/netty/handler/codec/http2/Http2CodecUtil.java),
from `h2-16` to `h2-14`, and built Netty 4.1 locally.

Finally, just build the project with:

```
$ mvn clean install -Prelease
```
and launch it making sure to set the correct path to the ALPN jar:

```
java -server -Xbootclasspath/p:/alpn-boot-8.1.2.v20141202.jar -Dhttp-port=8080 -Dhttp2-port=8443 -jar target/http2-netty.jar
```

