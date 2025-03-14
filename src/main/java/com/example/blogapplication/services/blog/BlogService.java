package com.example.blogapplication.services.blog;


import com.example.blogapplication.db2.entities.BlogEntity;
import com.example.blogapplication.db2.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class BlogService {
    @Autowired
    BlogRepository blogRepository;

    public boolean addBlog(BlogEntity blog){
        try {
            BlogEntity blogEntity = Objects.requireNonNull(blogRepository.save(blog));
        return true;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public Optional<BlogEntity> findById(Integer id){
        return blogRepository.findById(id);
    }

    public boolean deleteBlog(@RequestParam  Integer id) {
        Optional<BlogEntity> blogEntity = findById(id);
      if (!blogEntity.isEmpty()){
          blogRepository.deleteById(id);
          return true;
      }
        return false;
    }

    public boolean updateBlog(Integer id, BlogEntity blog) {
        Optional<BlogEntity> blogEntity = findById(id);
        if(blogEntity.isPresent()){
            BlogEntity updatedBlog = getUpdatedBlog(blogEntity.get(), blog);
            blogRepository.save(updatedBlog);
            return true;
        }
        return false;
    }

    private BlogEntity getUpdatedBlog(BlogEntity oldblog, BlogEntity newBlog) {
        oldblog.setContent(newBlog.getContent());
        oldblog.setAuthor(newBlog.getAuthor());
        oldblog.setTitle(newBlog.getTitle());
        oldblog.setUpdatedAt(LocalDateTime.now());
        return oldblog;
    }
}
