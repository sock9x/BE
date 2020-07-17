package com.px.tool.controller;

import com.px.tool.domain.phuongan.PhuongAn;
import com.px.tool.domain.phuongan.PhuongAnPayload;
import com.px.tool.domain.phuongan.PhuongAnTaoMoi;
import com.px.tool.domain.phuongan.RequestTaoPhuongAnMoi;
import com.px.tool.domain.phuongan.service.PhuongAnService;
import com.px.tool.domain.user.service.UserService;
import com.px.tool.infrastructure.BaseController;
import com.px.tool.infrastructure.exception.PXException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/pa")
public class PhuongAnController extends BaseController {
    @Autowired
    private PhuongAnService phuongAnService;

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public PhuongAnPayload getPhuongAnDetail(SecurityContextHolderAwareRequestWrapper httpServletRequest, @PathVariable Long id) {
        return phuongAnService.findById(extractUserInfo(httpServletRequest), id);
    }

    @GetMapping
    public List<PhuongAn> getPhuongAnTheoPhongBan(SecurityContextHolderAwareRequestWrapper httpServletRequest, @PathVariable Long id) {
        Long userId = extractUserInfo(httpServletRequest);
        return this.phuongAnService.findByPhongBan(userId);
    }

    @PostMapping
    public PhuongAn createPhuongAn(HttpServletRequest httpServletRequest, @RequestBody PhuongAnPayload phuongAnPayload) {
        return this.phuongAnService.save(extractUserInfo(httpServletRequest), phuongAnPayload);
    }

    @PostMapping("/tao-pa")
    public PhuongAnTaoMoi taoPhuongAnMoi(HttpServletRequest request, @RequestBody RequestTaoPhuongAnMoi requestTaoPhuongAnMoi) {
        Long userid = extractUserInfo(request);
        if (!userService.userById().get(userid).isTroLyKT()) {
            throw new PXException("phuongan.TLKT_permission");
        }
        if (CollectionUtils.isEmpty(requestTaoPhuongAnMoi.getDetailIds())) {
            throw new PXException("Phải chọn ít nhất một dòng để tạo phương án");
        }
        return phuongAnService.taoPhuongAnMoi(userid, requestTaoPhuongAnMoi);
    }
}
