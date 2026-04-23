package org.example.mybooks.constant;


import lombok.Getter;

public enum RoleType {
    USER(0,"USER"),
    ADMIN(1,"ADMIN");
    @Getter
    private final int code;
    @Getter
    private final String roleName;
    RoleType(int code, String roleName){
        this.code=code;
        this.roleName=roleName;
    }
    public static RoleType fromCode(String name){
        for(RoleType role:RoleType.values()){
            if (role.roleName.equalsIgnoreCase(name.trim())) {
                return role;
            }
        }
        System.out.println("⚠️ 매칭 실패! DB에서 온 값: [" + name + "] -> USER로 강제 변환됨");
        return USER;
    }
}
