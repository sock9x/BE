package com.px.tool.domain.vanbanden.repository.impl;

import com.px.tool.domain.RequestType;
import com.px.tool.domain.user.User;
import com.px.tool.domain.user.service.UserService;
import com.px.tool.domain.vanbanden.payload.VanBanDenPageRequest;
import com.px.tool.domain.vanbanden.payload.VanBanDenPageResponse;
import com.px.tool.domain.vanbanden.repository.VanBanDenRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VanBanDenRepositoryCustomImpl implements VanBanDenRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserService userService;

    @Override
    public VanBanDenPageResponse findByNoiNhan(VanBanDenPageRequest request, Map<Long, String> noiNhanById) {
        StringBuilder query = new StringBuilder();
        query.append(" WHERE v.noiNhan LIKE :userId AND (v.deleted is null or v.deleted <> true OR v.deleted = false OR v.deleted = 0) ");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", "%" + request.getUserId() + "%");
        if (Objects.nonNull(request.getDate())) {
            query.append(" AND v.createdAt <= :date ");
            params.put("date", request.getDate());
        }
        if (!StringUtils.isEmpty(request.getSoVb())) {
            query.append(" AND LOWER(v.soPa) LIKE LOWER(:soVb) ");
            params.put("soVb", "%" + request.getSoVb() + "%");
        }
        if (Objects.nonNull(request.getFolder())) {
            query.append(" AND v.folder = :folder ");
            params.put("folder", request.getFolder());
        }
        if (Objects.nonNull(request.getLoaiVb())) {
            query.append(" AND v.requestType = :loaiVB ");
            params.put("loaiVB", request.getLoaiVb());
        }

        User user = userService.findById(request.getUserId());
        if(user.isGiamDoc()){
            query.append(" AND v.requestType <> :loaiVB ");
            params.put("loaiVB", RequestType.CONG_NHAN_THANH_PHAM);
        }

        Query jpaQuery = entityManager.createQuery("SELECT v FROM VanBanDen v " + query.toString() + " order by v.createdAt DESC");
        params.forEach((k, v) -> jpaQuery.setParameter(k, v));
        jpaQuery.setFirstResult(request.getPage() * request.getSize());
        jpaQuery.setMaxResults(request.getSize());

        Query totalQuery = entityManager.createQuery("SELECT count(v.vbdId) FROM VanBanDen v " + query.toString());
        params.forEach((k, v) -> totalQuery.setParameter(k, v));
        long count = (long) totalQuery.getSingleResult();


        VanBanDenPageResponse pageResponse = new VanBanDenPageResponse(request.getPage(), request.getSize());
        pageResponse.parse(jpaQuery.getResultList(), noiNhanById);
        pageResponse.setTotal(count);

        return pageResponse;
    }
}
