package com.loess.todoloo.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.loess.todoloo.model.enums.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true) //for mapper tests get working at empty fields
public class UserInfoRequest {

    String email;
    String password;
    String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING) //не работает
    Role role;


}
