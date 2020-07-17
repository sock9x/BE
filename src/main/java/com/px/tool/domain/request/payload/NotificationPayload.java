package com.px.tool.domain.request.payload;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NotificationPayload {
    private Long notiId;
    private Long requestId;
    private String body;
    private boolean isRead;
    private String time;
}
