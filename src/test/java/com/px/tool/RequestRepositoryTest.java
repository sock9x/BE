package com.px.tool;

import com.px.tool.domain.request.repository.RequestRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RequestRepositoryTest extends PxApplicationTests {

    @Autowired
    private RequestRepository requestRepository;

    @Test
    public void updateReceiverId() {
        requestRepository.updateReceiverId(86L,1L,2L,3L,4L);
    }
}
