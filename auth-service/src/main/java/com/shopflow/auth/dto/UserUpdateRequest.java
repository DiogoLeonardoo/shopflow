package com.shopflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String password;
}
