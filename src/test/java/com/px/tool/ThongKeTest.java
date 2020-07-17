package com.px.tool;

import com.px.tool.domain.phuongan.repository.PhuongAnRepository;
import com.px.tool.domain.request.Request;
import com.px.tool.domain.request.payload.ThongKePageResponse;
import com.px.tool.domain.request.payload.ThongKePageRequest;
import com.px.tool.domain.request.repository.RequestRepository;
import com.px.tool.domain.request.service.impl.RequestServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class ThongKeTest extends PxApplicationTests{
    @Autowired
    private RequestRepository requestRepository;


    @Autowired
    private RequestServiceImpl requestService;

    @Autowired
    private PhuongAnRepository phuongAnRepository;

    @Test
    public void thongKe() {
        ThongKePageResponse data = requestService.collectDataThongKe(null);
        System.out.println();
    }
}
