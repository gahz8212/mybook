package org.example.mybooks.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
   private Long id;
    private String title;
    private String description;
    private String image;
    private String author;
    private Date published;
    private Integer favorite;
    private MultipartFile uploadFile;
}
