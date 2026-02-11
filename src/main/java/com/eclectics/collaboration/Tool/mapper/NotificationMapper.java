package com.eclectics.collaboration.Tool.mapper;

import com.eclectics.collaboration.Tool.dto.NotificationResponseDTO;
import com.eclectics.collaboration.Tool.model.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    static NotificationResponseDTO toDto(Notification notification) {
        return null;
    }
}
