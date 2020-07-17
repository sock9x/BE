package com.px.tool.domain.vanbanden.repository;

import com.px.tool.domain.vanbanden.payload.VanBanDenPageRequest;
import com.px.tool.domain.vanbanden.payload.VanBanDenPageResponse;

import java.util.Map;

public interface VanBanDenRepositoryCustom {
    VanBanDenPageResponse findByNoiNhan(VanBanDenPageRequest request, Map<Long, String> longStringMap);
}
