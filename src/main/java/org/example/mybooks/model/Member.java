package org.example.mybooks.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.mybooks.constant.RoleType;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Member {
    private Long id;
    private String name;
    private String email;
    private String password;
    private List<String> roleNames;
    private Date deleted_at;

//    public String getRoleNames(){
//        if(this.roleCodes==null||this.roleCodes.isEmpty()){
//            return "권한없음";
//        }
//        return this.roleCodes.stream().map(code-> RoleType.fromCode(code).name())
//                .collect(Collectors.joining(", "));
//    }
}
