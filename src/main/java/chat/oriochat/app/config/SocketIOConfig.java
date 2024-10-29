package chat.oriochat.app.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketIOConfig {
    @Value("${socket.io.host}")
    private String socketHost;

    @Value("${socket.io.port}")
    private int socketPort;

    private volatile SocketIOServer server;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(socketHost);
        config.setPort(socketPort);

        // Set specific allowed origin
        config.setOrigin("http://localhost:3000");

        server = new SocketIOServer(config);
        server.start();

        log.info("Socket.IO server started at host: {}, port: {}", socketHost, socketPort);
        return server;
    }

    @PreDestroy
    public void stopSocketIoServer() {
        if (server != null) {
            server.stop();
            log.info("Socket.IO server stopped successfully.");
        }
    }
}
