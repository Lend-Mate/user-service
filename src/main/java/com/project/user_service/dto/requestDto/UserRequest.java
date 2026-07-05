package com.project.user_service.dto.requestDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "İsim alanı boş bırakılamaz")
    @Size(max = 100, message = "İsim en fazla 100 karakter olabilir")
    private String firstName;

    @NotBlank(message = "Soyisim alanı boş bırakılamaz")
    @Size(max = 100, message = "Soyisim en fazla 100 karakter olabilir")
    private String lastName;

    @NotBlank(message = "Kullanıcı adı boş bırakılamaz")
    @Size(min = 3, max = 50, message = "Kullanıcı adı 3 ile 50 karakter arasında olmalıdır")
    private String username;

    @NotBlank(message = "Şifre alanı boş bırakılamaz")
    @Size(min = 6, max = 255, message = "Şifre en az 6 karakter olmalıdır")
    private String password;

    @NotBlank(message = "E-posta alanı boş bırakılamaz")
    @Email(message = "Lütfen geçerli bir e-posta adresi giriniz")
    @Size(max = 255, message = "E-posta en fazla 255 karakter olabilir")
    private String email;

    @Size(max = 30, message = "Telefon numarası en fazla 30 karakter olabilir")
    private String phone;

    private String role; // Opsiyonel: Eğer endpoint üzerinden rol belirlenebilecekse

    private String profileImage; // Opsiyonel: Profil resmi URL'i veya base64 verisi
}
