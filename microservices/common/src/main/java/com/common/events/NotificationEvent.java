package com.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class NotificationEvent extends BaseEvent {
    private Long userId;
    private String title;
    private String message;
    private String type;
    private String channel;
    private String email;
    private String phone;
}
