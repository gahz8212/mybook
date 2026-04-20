package org.example.mybooks.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mybooks.dto.BookDto;
import org.example.mybooks.model.Book;
import org.example.mybooks.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class BookController {
    private final BookService bookService;

    @GetMapping("/book/{id}")
    public ResponseEntity<Book> index(@PathVariable ("id") Long id) {
        log.info("id:{}",id.toString());
        Book book=bookService.findById(id);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/list")
    public ResponseEntity<List<BookDto>> findAll(){
        List<BookDto> bookList = bookService.findAll();
        log.info(bookList.toString());
        return ResponseEntity.ok(bookList);
//        model.addAttribute("bookList", bookList);
//        return "list";
    }


    @DeleteMapping("/book/delete/{id}")
    public void delete(@PathVariable("id") Long id) {
        int count=bookService.delete(id);
        log.info("{}의 데이터가 삭제되었습니다.",count);

    }
//
//    @GetMapping("/add")
//    public String addBook() {
//        return "add";
//    }
//
    @PostMapping("/book/create")
    public void addBook(@ModelAttribute BookDto bookDto)throws IOException {
        log.info("수신된 데이터: {}", bookDto);
        try {
        MultipartFile file=bookDto.getUploadFile();
        if(!file.isEmpty()) {
//            String projectPath = "/home/ksh/upload/book/";//(Linux)
            String projectPath = "C:/Users/Public/Pictures/Books/";//(Window)
            UUID uuid = UUID.randomUUID();
            String filename = uuid + "_" + file.getOriginalFilename();
            File saveFile = new File(projectPath + filename);
            file.transferTo(saveFile);
            BookDto book=BookDto.builder()
                    .title(bookDto.getTitle())
                    .description(bookDto.getDescription())
                    .author(bookDto.getAuthor())
                    .image(filename)
                    .published(bookDto.getPublished()).build();

            log.info("파일 저장 시도 경로: " + saveFile.getAbsolutePath());
            int count = bookService.save(book);
        }
//            return"redirect:/list";
        } catch (Exception e) {
            log.info("Error:{}",e.getMessage());
//            return "add";
        }
    }
//
//    @GetMapping("/update")
//    public String updateBook(@RequestParam("id")Long id,Model model) {
//        Book book=bookService.findById(id);
//        model.addAttribute("book",book);
//        return  "update";
//    }
    @PutMapping("/book/update")
    public void updateBook(@RequestBody  BookDto book) {
        Long id=book.getId();
        log.info("book:{}",book);
        int count=bookService.update(id,book);

    }
}