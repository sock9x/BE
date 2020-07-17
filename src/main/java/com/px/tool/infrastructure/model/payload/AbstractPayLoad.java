package com.px.tool.infrastructure.model.payload;

import com.px.tool.domain.RequestType;
import com.px.tool.domain.user.User;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractPayLoad<E extends EntityDefault> extends AbstractObject {
    public abstract void processSignImgAndFullName(Map<Long, User> userById);

    public abstract Collection<Long> getDeletedIds(E o);

    public abstract void capNhatNgayThangChuKy(E request, E existed);

    public abstract void validateXacNhan(User user, E request, E existed);

    public abstract E toEntity(E e);

    public abstract <O extends AbstractPayLoad> O andStatus(RequestType status);
}
