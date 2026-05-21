package com.masasajt.backendfotograf.mapper;


import com.masasajt.backendfotograf.dto.UserDTO;
import com.masasajt.backendfotograf.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toUserDTO(User user);
    List<UserDTO> toUserDTOList(List<User> users);
}
