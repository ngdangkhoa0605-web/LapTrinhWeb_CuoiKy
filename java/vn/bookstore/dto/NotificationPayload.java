package vn.bookstore.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/** Payload gửi qua WebSocket (JSON) tới client đang subscribe topic của chi nhánh. */
@Getter
@Setter
@AllArgsConstructor
public class NotificationPayload {
	private String title;
	private String message;
	private String type;
	private Integer orderId;
	private String orderCode;
	private LocalDateTime time;
}
