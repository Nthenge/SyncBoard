package com.eclectics.collaboration.Tool.mapper;

import com.eclectics.collaboration.Tool.dto.ActivityLogResponseDTO;
import com.eclectics.collaboration.Tool.model.ActivityLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActivityLogMapper {

    @Mapping(source = "board.id", target = "boardId")
    @Mapping(source = "user.id", target = "userId")
    static ActivityLogResponseDTO toDto(ActivityLog activityLog) {
        return null;
    }
}
