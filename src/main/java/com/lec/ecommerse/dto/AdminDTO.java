package com.lec.ecommerse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {

    @Size(max = 15) // obje için kısıtlamalar
    @NotNull(message = "Please enter your first name !") // obje için kısıtlamalar
    private String firstName;

    @Size(max = 15)
    @NotNull(message = "Please enter your first name !")
    private String lastName;

    @Size(min = 4, max = 60)
    private String password;

    @Pattern(regexp = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$",
            message = "Please enter valid phone number")
    @Size(min = 14, max = 14, message = "you can write down error messages here!")
    @NotNull(message = "Please enter your phone number")
    private String phoneNumber;

    @Email(message = "Please enter valid email")
    @Size(min = 5, max = 150)
    @NotNull(message = "Please enter your email")
    private String email;

    @Size(max = 250)
    @NotNull(message = "Please enter your address")
    private String address;

    @Size(max = 15)
    @NotNull(message = "Please enter your zipCode")
    private String zipCode;

    private Boolean builtIn;

    private Boolean enabled;

    private Set<String> roles;
}
