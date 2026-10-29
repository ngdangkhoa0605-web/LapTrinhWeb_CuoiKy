package vn.bookstore;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Cho phép đóng gói WAR để deploy lên Tomcat ngoài (external servlet
 * container) - cần thiết vì Spring Boot chạy JSP đáng tin cậy nhất khi
 * deploy dạng WAR (executable fat JAR có hạn chế với JSP/Jasper).
 * Khi phát triển, vẫn có thể chạy bình thường bằng "mvn spring-boot:run".
 */
public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BookstoreChainManagementApplication.class);
	}
}
