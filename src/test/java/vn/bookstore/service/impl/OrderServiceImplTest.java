package vn.bookstore.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class OrderServiceImplTest {

    @Test
    void rapidlyGeneratedOrderCodesAreDistinctAndFitDatabaseColumn() {
        OrderServiceImpl service = new OrderServiceImpl(null, null, null, null, null);
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String code = ReflectionTestUtils.invokeMethod(service, "generateOrderCode");
            assertTrue(code != null && code.startsWith("ORD") && code.length() <= 50);
            assertTrue(codes.add(code), "Orders created together must not share a code");
        }
    }
}