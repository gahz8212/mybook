package org.example.mybooks.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mybooks.model.Member;
import org.springframework.data.relational.core.sql.In;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private Long id;
    private String name;
    private String email;
    private List<String> roleNames;
//    private List<Integer> roleCodes;

    public static MemberDto from(Member member){
        return MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
//                .roleCodes(member.getRoleCodes())
                .roleNames(member.getRoleNames())
                .build();
    }
//    public String getRoleList(){
//        if(this.roleNames==null||this.roleNames.isEmpty()){
//            return "";
//        }
//        return (this.roleNames.split(", "));
//    }
}
