package org.example.mybooks.constant;


import lombok.Getter;

public enum RoleType {
    USER(0,"ROLE_USER"),
    ADMIN(1,"ROLE_ADMIN");
    @Getter
    private final int code;
    @Getter
    private final String roleName;
    RoleType(int code, String roleName){
        this.code=code;
        this.roleName=roleName;
    }
    public static RoleType fromCode(int code){
        for(RoleType role:RoleType.values()){
            if(role.code==code)return role;
        }
        return USER;
    }

}
