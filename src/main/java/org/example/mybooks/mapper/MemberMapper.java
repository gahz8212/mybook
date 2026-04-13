package org.example.mybooks.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.mybooks.dto.MemberDto;
import org.example.mybooks.model.Member;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberMapper {
    Optional<Member> findByEmail(@Param("email")String email);

    Optional<Member> findById(@Param("id") Long id);



    void insertMember(@Param("member")Member member);

    void insertRoles(@Param("id") Long id, @Param("code") int code);

    List<MemberDto> findAll();

    void updatePassword(@Param("id")Long id,@Param("password") String password);

    void insertRoles(@Param("id")Long id,@Param("roleCode")Integer roleCode);
    void deleteRole(@Param("id")Long id);
}
