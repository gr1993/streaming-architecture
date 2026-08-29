package org.example;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MeetupWebSocketClientHandler extends SimpleChannelInboundHandler {

    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    MeetupWebSocketClientHandler(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("WebSocket Client disconnected!");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();

        // 웹소켓 핸드쉐이킹이 완료되었는지 검증
        if (!handshaker.isHandshakeComplete()) {
            //API 서버와 연동 시작
            handshaker.finishHandshake(channel, (FullHttpResponse) msg);
            handshakeFuture.setSuccess();
            System.out.println("WebSocket Client connected and ready to consume RSVPs!");
            return;
        }

        // 핸드쉐이킹 전에는 FullHttpResponse가 정상적인 HTTP Upgrade 응답
        // 반면 핸드쉐이킹이 완료된 이후에는 WebSocket 통신이 이루어져야 하므로 FullHttpResponse가 들어오면 예상하지 못한 HTTP 응답으로 간주해서 예외를 발생
        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;

            //메시지 키에 임의 값 사용
            final String messageKey = UUID.randomUUID().toString();
            final byte[] messagePayload = new byte[textFrame.content().readableBytes()];
            textFrame.content().readBytes(messagePayload);

            HybridMessageLogger.addEvent(messageKey,messagePayload);
            //rsvpProducer.sendMessage(messageKey,messagePayload);

            System.out.println("msg : " + new String(messagePayload, StandardCharsets.UTF_8));

        } else if (frame instanceof CloseWebSocketFrame) {
            channel.close();
            //rsvpProducer.close();
        }
    }
}
