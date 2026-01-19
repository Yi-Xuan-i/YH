package com.yixuan.yh.chat.websocket.inter;

import com.yixuan.yh.chat.websocket.pojo.StompPrincipal;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public class StompHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        List<String> ids = request.getHeaders().get("id");
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("WebSocket 握手必须包含 id");
        }

        return new StompPrincipal(ids.get(0));
    }

}