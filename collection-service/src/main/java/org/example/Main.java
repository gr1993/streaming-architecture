package org.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Netty 웹소켓 클라이언트 실행 코드 작성
 */
public class Main {
    private static boolean continueRunning = true;

    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        final String URL =  0 < args.length? args[0] : "ws://localhost:8080/rsvp";

        URI uri = new URI(URL);

        try {
            // 우리가 구현한 수집 핸들러 생성
            final MeetupWebSocketClientHandler handler =
                    new MeetupWebSocketClientHandler(
                            WebSocketClientHandshakerFactory.newHandshaker(
                                    uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));

            // 파이프라인 설정
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpClientCodec());
                            p.addLast(new HttpObjectAggregator(8192));
                            p.addLast(WebSocketClientCompressionHandler.INSTANCE);
                            p.addLast(handler);
                        }
                    });

            // 백그라운드에서 수집 단계 애플리케이션 실행하고
            // Meetup API 서버와 연동되어 메시지가 전달되기까지 기다린다.
            Channel ch = b.connect(uri.getHost(), uri.getPort()).sync().channel();
            // 애플리케이션이 종료될 때까지 block 상태로 대기
            handler.handshakeFuture().sync();

            // 윕소켓의 핑 설정과 종료 메시지 처리
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            do {
                String msg = console.readLine();
                if (msg == null) {
                    continueRunning = false;
                } else if ("bye".equals(msg.toLowerCase())) {
                    ch.writeAndFlush(new CloseWebSocketFrame());
                    ch.closeFuture().sync();
                    continueRunning = false;
                } else if ("ping".equals(msg.toLowerCase())) {
                    WebSocketFrame frame = new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[] { 8, 1, 8, 1 }));
                    ch.writeAndFlush(frame);
                } else {
                    WebSocketFrame frame = new TextWebSocketFrame(msg);
                    ch.writeAndFlush(frame);
                }
            }while (continueRunning);
        } finally {
            group.shutdownGracefully();
        }
    }
}