package com.example.blogapplication.controller;


import com.example.blogapplication.db2.entities.BlogEntity;
//import com.example.blogapplication.model.blog.BlogModel;
import com.example.blogapplication.model.Blog;
import com.example.blogapplication.services.blog.BlogService;
import jakarta.validation.Valid;
import org.hibernate.validator.constraints.ParameterScriptAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/blog")
public class BlogController {
   @Autowired
    private BlogService blogService;



@PostMapping("/addBlog")
    ResponseEntity<String> addBlog(@Valid @RequestBody BlogEntity  blog){

    boolean isSaved=blogService.addBlog(blog);

if (!isSaved)
    return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
return  ResponseEntity.status(HttpStatus.OK).body("Blog posted");
    }

    @PostMapping("/deleteBlog")
    ResponseEntity<String> deleteBlog( @RequestParam Integer id){

        boolean isDeleted=blogService.deleteBlog(id);
        if (isDeleted)
    return ResponseEntity.status(HttpStatus.OK).body("Deleted sucessfully");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");

    }

    @PostMapping("/updateBlog")
    ResponseEntity<String> updateBlog( @RequestParam Integer id,@Valid @RequestBody BlogEntity blog){
        boolean isUpdated=blogService.updateBlog(id,blog);
        if (isUpdated)
            return ResponseEntity.status(HttpStatus.OK).body("Updated sucessfully");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Blog not found");

    }


}
