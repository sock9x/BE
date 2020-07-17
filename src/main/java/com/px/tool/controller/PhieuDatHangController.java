package com.px.tool.controller;

import com.px.tool.domain.dathang.PhieuDatHang;
import com.px.tool.domain.dathang.PhieuDatHangPayload;
import com.px.tool.domain.dathang.service.PhieuDatHangService;
import com.px.tool.infrastructure.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pdh")
public class PhieuDatHangController extends BaseController {
    @Autowired
    private PhieuDatHangService phieuDatHangService;

    @GetMapping("/{id}")
    public PhieuDatHangPayload getPhieuDatHangDetail(SecurityContextHolderAwareRequestWrapper httpServletRequest, @PathVariable Long id) {
        return this.phieuDatHangService.findById(extractUserInfo(httpServletRequest), id);
    }

    @GetMapping
    public List<PhieuDatHang> getPhieuDatHangTheoPhongBan(SecurityContextHolderAwareRequestWrapper httpServletRequest) {
        Long userId = extractUserInfo(httpServletRequest);
        return this.phieuDatHangService.findByPhongBan(userId);

    }

    @PostMapping
    public void taoPhieuDatHang(SecurityContextHolderAwareRequestWrapper httpServletRequest, @RequestBody PhieuDatHangPayload phieuDatHangPayload) {
        logger.info("Save Phieu dat hang, \nrequestId: {}", phieuDatHangPayload.getRequestId());
        this.phieuDatHangService.save(extractUserInfo(httpServletRequest), phieuDatHangPayload);
    }

    @DeleteMapping
    public void xoaPhieuDatHang() {

    }
}
