package org.example.mybooks.service;

import lombok.RequiredArgsConstructor;
import org.example.mybooks.dto.BookDto;
import org.example.mybooks.mapper.BookMapper;
import org.example.mybooks.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;

    public List<BookDto> findAll() {
    return bookMapper.findAll();
    }

    public int save(BookDto book) {
        return bookMapper.create(book);
    }

    public Book findById(Long id) {
        return bookMapper.findById(id);
    }

    public int update(Book book) {
        return bookMapper.update(book);
    }

    public int delete(Long id) {
        return bookMapper.delete(id);
    }

    public void updateFavorite(Long id) {
        bookMapper.updateFav(id);
    }
}
