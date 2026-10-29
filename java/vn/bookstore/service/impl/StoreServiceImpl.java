package vn.bookstore.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.bookstore.entity.Store;
import vn.bookstore.exception.BusinessException;
import vn.bookstore.repository.StoreRepository;
import vn.bookstore.service.StoreService;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

	private final StoreRepository storeRepository;

	@Override
	public Store save(Store store) {
		return storeRepository.save(store);
	}

	@Override
	public Store findById(Integer id) {
		return storeRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Không tìm thấy chi nhánh có id = " + id));
	}

	@Override
	public List<Store> findAll() {
		return storeRepository.findAll();
	}

	@Override
	public List<Store> findActive() {
		return storeRepository.findByStatus(1);
	}

	@Override
	public void delete(Integer id) {
		Store store = findById(id);
		if (!store.getBooks().isEmpty()) {
			throw new BusinessException(
					"Không thể xóa chi nhánh đang có " + store.getBooks().size() + " đầu sách!");
		}
		storeRepository.delete(store);
	}
}
