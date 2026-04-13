package org.example.mybooks.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Builder
@Data
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
