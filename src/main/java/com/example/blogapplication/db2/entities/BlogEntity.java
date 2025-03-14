package com.example.blogapplication.db2.entities;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "blogs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

  @NotBlank(message = "Title cannot be empty")
  @Size(min = 5,max = 60 ,message = "Title must be betw")
  private String title;
    @NotBlank(message = "Content cannot be empty")
    @Size(min = 10, message = "Content must have at least 10 characters")
  private String content;

    @NotBlank(message = "Author name is required")
    private String author;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
