package com.px.tool.controller;

import com.px.tool.domain.request.payload.PageDashBoardCongViecCuaToi;
import com.px.tool.domain.request.service.RequestService;
import com.px.tool.infrastructure.BaseController;
import com.px.tool.infrastructure.model.payload.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashBoardController extends BaseController {

    @Autowired
    private RequestService requestService;

    @GetMapping("/receiver")
    public PageDashBoardCongViecCuaToi getListcongViecCuaToi(SecurityContextHolderAwareRequestWrapper httpServletRequest,
                                                             @RequestParam(required = false, defaultValue = "1") Integer page,
                                                             @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        Long userId = extractUserInfo(httpServletRequest);
        return requestService.timVanBanCanGiaiQuyet(userId, new PageRequest(page, size));
    }
}
