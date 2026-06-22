package com.martinsdev.caretrack.api;

import com.martinsdev.caretrack.api.repository.SolicitationSearchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class ApiApplicationTests {

    @MockitoBean
    SolicitationSearchRepository searchRepository;

    @MockitoBean
    ElasticsearchOperations operations;

	@Test
	void contextLoads() {
	}

}
