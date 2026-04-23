package org.example.mybooks.dto;

import lombok.Getter;
import lombok.Setter;


import java.util.List;
@Setter
@Getter
public class AuthorityDto {
    private Long id;
    private List<String> roleArray;

}
