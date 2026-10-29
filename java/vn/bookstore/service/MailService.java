package vn.bookstore.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

	private final JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String fromEmail;

	@Value("${app.otp.expire-minutes}")
	private int otpExpireMinutes;

	public boolean sendOtpMail(String toEmail, String otp, String purpose) {
		String subject;
		String content;

		if ("REGISTER".equals(purpose)) {
			subject = "[Bookstore Chain] Mã OTP kích hoạt tài khoản";
			content = buildHtml("Kích hoạt tài khoản",
					"Mã OTP để kích hoạt tài khoản của bạn là:", otp, "#0d6efd");
		} else {
			subject = "[Bookstore Chain] Mã OTP đặt lại mật khẩu";
			content = buildHtml("Đặt lại mật khẩu",
					"Mã OTP để đặt lại mật khẩu của bạn là:", otp, "#dc3545");
		}

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setFrom(fromEmail, "Bookstore Chain Management");
			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(content, true);
			mailSender.send(message);
			return true;
		} catch (Exception e) {
			log.error("Gửi email OTP thất bại: {}", e.getMessage());
			return false;
		}
	}

	private String buildHtml(String title, String label, String otp, String color) {
		return "<div style='font-family:Arial,sans-serif;max-width:480px;margin:auto'>"
				+ "<h2>" + title + "</h2>"
				+ "<p>" + label + "</p>"
				+ "<p style='font-size:28px;font-weight:bold;letter-spacing:6px;color:" + color + "'>" + otp + "</p>"
				+ "<p>Mã có hiệu lực trong <b>" + otpExpireMinutes + " phút</b>.</p>"
				+ "<p style='color:#888;font-size:13px'>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.</p>"
				+ "</div>";
	}
}
