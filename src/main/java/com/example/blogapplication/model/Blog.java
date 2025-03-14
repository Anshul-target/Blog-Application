package com.example.blogapplication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Blog
{
//        private int id;

        private String content;
        private String title;
        private String author;


//        private LocalDateTime createdAt;


    }


