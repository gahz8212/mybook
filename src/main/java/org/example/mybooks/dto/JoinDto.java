package org.example.mybooks.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mybooks.model.Member;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinDto {
    @NotBlank(message="이메일은 필수 입력값 입니다.")
    @Email(message="이메일 형식이 올바르지 않습니다.")
    private String email;
    @NotBlank(message="이메일은 필수 입력값 입니다.")
    private String name;
    @NotBlank(message="이메일은 필수 입력값 입니다.")
    @Size(min=4,max=16,message="비밀번호는 4자 이상 16자 이하로 입력해 주세요.")
    private String password;

    private String confirm_password;
    public static JoinDto from(Member member){
        return JoinDto.builder()
                .email(member.getEmail())
                .name(member.getName())
                .password(member.getPassword())
                .build();
    }
}
