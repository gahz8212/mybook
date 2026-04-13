package org.example.mybooks.dto.api;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor

public class TokenRequestDto {
    private String refreshToken;
    private String accessToken;

}
