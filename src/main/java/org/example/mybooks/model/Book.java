package org.example.mybooks.model;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class Book {
        private Long id;
        private String title;
        private String description;
        private String image;
        private String author;
        private Date published;
        private Integer favorite;

    }

