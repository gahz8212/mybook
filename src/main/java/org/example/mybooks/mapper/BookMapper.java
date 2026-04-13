package org.example.mybooks.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.mybooks.dto.BookDto;
import org.example.mybooks.model.Book;

import java.util.List;


@Mapper
public interface BookMapper {

    List<BookDto> findAll();

    int create(@Param("book") BookDto bookdto);
    Book findById(@Param("id") Long id);
    int update(@Param("book") Book book);
    int delete(@Param("id") Long id);

    void updateFav(@Param("id")Long id);
}
