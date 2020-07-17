package com.px.tool.infrastructure.service;

import com.px.tool.domain.RequestType;
import com.px.tool.domain.request.payload.ThongKePageResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

public interface ExcelService {
    void exportFile(Long requestId, RequestType requestType, OutputStream outputStream, Long startDate, Long endDate, Long toKH,Long spId);

    void exports(Long startDate, Long endDate, Long spId, Long toTruongId, Integer page, Integer size);
}
