package org.example.mybooks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mybooks.model.Member;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordDto {
   @NotBlank(message="기존 비밀번호를 입력해 주세요.")
    private String old_password;
    @Size(min=4,max=16,message="4자리에서 16자리의 비밀번호가 필요 합니다.")
    @NotBlank(message="새로운 비밀번호를 입력해 주세요.")
    private String new_password;
    @Size(min=4,max=16,message="4자리에서 16자리의 비밀번호가 필요 합니다.")
    @NotBlank(message="새로운 비밀번호를 입력해 주세요.")
    private String confirm_password;

}
