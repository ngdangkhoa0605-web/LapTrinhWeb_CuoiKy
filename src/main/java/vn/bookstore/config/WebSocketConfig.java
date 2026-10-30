package vn.bookstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Kênh WebSocket (STOMP over SockJS) dùng để đẩy thông báo REAL-TIME cho
 * Manager/Staff của từng chi nhánh khi có đơn hàng mới.
 *
 * Client (JSP dashboard) subscribe tới: /topic/store/{storeId}/orders
 * Server broadcast qua NotificationService -> SimpMessagingTemplate.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
				.setAllowedOriginPatterns("*")
				.withSockJS(); // fallback cho trình duyệt/mạng không hỗ trợ WebSocket thuần
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// Client subscribe (nhận tin) qua "/topic/..."
		registry.enableSimpleBroker("/topic");
		// Client gửi tin (nếu cần) qua "/app/..."
		registry.setApplicationDestinationPrefixes("/app");
	}
}
