package com.px.tool.domain.request.payload;

import com.px.tool.domain.user.User;
import com.px.tool.infrastructure.model.payload.AbstractObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NoiNhan extends AbstractObject implements Comparable<NoiNhan> {
    private Long id;
    private String name;

    public static NoiNhan fromUserEntity(User phongBan) {
        NoiNhan noiNhan = new NoiNhan();
        noiNhan.id = phongBan.getUserId();
        noiNhan.name = phongBan.getAlias();
        return noiNhan;
    }

    @Override
    public int compareTo(NoiNhan o) {
        if (this.id < o.id) {
            return -1;
        } else if (this.id > o.id) {
            return 1;
        }
        return 0;
    }
}
