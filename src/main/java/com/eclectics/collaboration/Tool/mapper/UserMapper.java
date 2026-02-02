package com.eclectics.collaboration.Tool.mapper;

import com.eclectics.collaboration.Tool.dto.UserLoginRequestDTO;
import com.eclectics.collaboration.Tool.dto.UserRegistrationRequestDTO;
import com.eclectics.collaboration.Tool.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRegistrationRequestDTO dto);
    UserRegistrationRequestDTO toResponse(User entity);
    User toLogin(UserLoginRequestDTO dto);
}
