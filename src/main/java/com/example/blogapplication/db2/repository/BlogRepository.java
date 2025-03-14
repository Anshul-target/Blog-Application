package com.example.blogapplication.db2.repository;

import com.example.blogapplication.db2.entities.BlogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<BlogEntity,Integer> {

}
