package org.example.mybooks.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mybooks.dto.BookDto;
import org.example.mybooks.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class BookController {
    private final BookService bookService;

//    @GetMapping("/")
//    public String index() {
//        return "index";
//    }

    @GetMapping("/list")
    public ResponseEntity<List<BookDto>> findAll(){
        List<BookDto> bookList = bookService.findAll();
        log.info(bookList.toString());
        return ResponseEntity.ok(bookList);
//        model.addAttribute("bookList", bookList);
//        return "list";
    }

//    @GetMapping("/description")
//    public String description(@RequestParam("id") Long id, Model model) {
//        bookService.updateFavorite(id);
//        Book book=bookService.findById(id);
//        model.addAttribute("book",book);
//        return "description";
//    }
//    @GetMapping("/delete")
//    public String delete(@RequestParam("id") Long id, Model model) {
//        int count=bookService.delete(id);
//        return "redirect:/list";
//    }
//
//    @GetMapping("/add")
//    public String addBook() {
//        return "add";
//    }
//
//    @PostMapping("/add")
//    public String addBook(@ModelAttribute BookDto bookDto)throws IOException {
//        try {
//        MultipartFile file=bookDto.getUploadFile();
//        if(!file.isEmpty()) {
//            String projectPath = "/home/ksh/upload/book/";
//            UUID uuid = UUID.randomUUID();
//            String filename = uuid + "_" + file.getOriginalFilename();
//            File saveFile = new File(projectPath + filename);
//            file.transferTo(saveFile);
//            BookDto book=BookDto.builder()
//                    .title(bookDto.getTitle())
//                    .description(bookDto.getDescription())
//                    .author(bookDto.getAuthor())
//                    .image(filename)
//                    .published(bookDto.getPublished()).build();
//
//            log.info("파일 저장 시도 경로: " + saveFile.getAbsolutePath());
//            int count = bookService.save(book);
//        }
//            return"redirect:/list";
//        } catch (Error e) {
//            return "add";
//        }
//    }
//
//    @GetMapping("/update")
//    public String updateBook(@RequestParam("id")Long id,Model model) {
//        Book book=bookService.findById(id);
//        model.addAttribute("book",book);
//        return  "update";
//    }
//    @PostMapping("/update")
//    public String updateBook(@ModelAttribute Book book) {
//        log.info("id:{}",book.getId());
//        int count=bookService.update(book);
//        return  "redirect:/list";
//    }
}