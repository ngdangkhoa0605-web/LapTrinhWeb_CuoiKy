package vn.bookstore.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.bookstore.entity.Category;
import vn.bookstore.entity.Role;
import vn.bookstore.entity.Store;
import vn.bookstore.entity.User;
import vn.bookstore.repository.CategoryRepository;
import vn.bookstore.repository.RoleRepository;
import vn.bookstore.repository.StoreRepository;
import vn.bookstore.repository.UserRepository;

/**
 * Tự động tạo dữ liệu khởi tạo (Role, tài khoản Admin mặc định, 1 chi
 * nhánh mẫu, vài danh mục mẫu) khi ứng dụng khởi động LẦN ĐẦU (idempotent -
 * chỉ tạo nếu chưa tồn tại, chạy lại nhiều lần không bị trùng lặp).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final StoreRepository storeRepository;
	private final CategoryRepository categoryRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {
		seedRoles();
		Store defaultStore = seedDefaultStore();
		seedAdminAccount();
		seedCategories();
		log.info(">>> DataSeeder hoàn tất. Tài khoản admin mặc định: admin / admin@123");
		if (defaultStore != null) {
			log.info(">>> Chi nhánh mặc định: {} (id={})", defaultStore.getStoreName(), defaultStore.getStoreId());
		}
	}

	private void seedRoles() {
		createRoleIfNotExists("ROLE_ADMIN");
		createRoleIfNotExists("ROLE_MANAGER");
		createRoleIfNotExists("ROLE_STAFF");
		createRoleIfNotExists("ROLE_CUSTOMER");
	}

	private void createRoleIfNotExists(String roleName) {
		if (roleRepository.findByRoleName(roleName).isEmpty()) {
			roleRepository.save(new Role(roleName));
		}
	}

	private Store seedDefaultStore() {
		if (storeRepository.count() > 0) {
			return storeRepository.findAll().get(0);
		}
		Store store = new Store("Chi nhánh Quận 1", "123 Nguyễn Huệ, Quận 1, TP.HCM", "0900000001");
		return storeRepository.save(store);
	}

	private void seedAdminAccount() {
		if (userRepository.existsByUsername("admin")) {
			return;
		}
		Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN").orElseThrow();

		User admin = new User();
		admin.setUsername("admin");
		admin.setPassword(passwordEncoder.encode("admin@123"));
		admin.setEmail("admin@bookstorechain.vn");
		admin.setFullName("Quản trị hệ thống");
		admin.setRole(adminRole);
		admin.setStatus(1); // kích hoạt sẵn, không cần OTP
		userRepository.save(admin);
	}

	private void seedCategories() {
		if (categoryRepository.count() > 0) {
			return;
		}
		categoryRepository.save(new Category("Văn học", "Tiểu thuyết, truyện ngắn, thơ ca", null, 1));
		categoryRepository.save(new Category("Kinh tế", "Sách kinh doanh, tài chính, quản trị", null, 1));
		categoryRepository.save(new Category("Thiếu nhi", "Truyện tranh, sách giáo dục trẻ em", null, 1));
		categoryRepository.save(new Category("Kỹ năng sống", "Sách phát triển bản thân", null, 1));
	}
}
