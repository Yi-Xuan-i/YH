package com.yixuan.yh.video.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/ws/test")
@Slf4j
public class Server {

    // 存储会话与处理参数
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private float brightness = 1.2f;
    private float blurLevel = 0.3f;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        sessions.put(session.getId(), session);
        log.info("客户端连接成功: {}", session.getId());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        sessions.remove(session.getId());
        log.info("连接关闭: {}", session.getId());
    }

    /**
     * 处理二进制消息（视频帧）
     */
    @OnMessage
    public void onBinaryMessage(byte[] data, Session session) {
        try {
            // 1. 转换字节数组为图像
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));

            // 2. 执行美颜处理
            BufferedImage processedImage = toGrayscale(image);

            // 3. 转换回字节数据
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(processedImage, "jpg", bos);
            byte[] processedData = bos.toByteArray();

            // 4. 发送处理后的帧
            session.getAsyncRemote().sendBinary(ByteBuffer.wrap(processedData));
        } catch (IOException e) {
            log.error("图像处理异常", e);
        }
    }

    /**
     * 处理文本消息（参数调整）
     */
    @OnMessage
    public void onTextMessage(String message, Session session) {
        System.out.println(message);
    }

    /**
     * 错误处理
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket错误: {}", error.getMessage());
        sessions.remove(session.getId());
    }

    public static BufferedImage toGrayscale(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = grayscaleImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, null);
        g2d.dispose();
        return grayscaleImage;
    }
}
