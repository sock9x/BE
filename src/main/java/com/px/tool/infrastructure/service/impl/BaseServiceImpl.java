package com.px.tool.infrastructure.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public abstract class BaseServiceImpl {
    protected Logger logger = LoggerFactory.getLogger("Service Layer");

    @Value("${vanbanden.kiemhong.noidung}")
    protected String vbdKiemHong;

    @Value("${vanbanden.dathang.noidung}")
    protected String vbdDatHang;

    @Value("${vanbanden.phuongan.noidung}")
    protected String vbdPhuongAn;
}
