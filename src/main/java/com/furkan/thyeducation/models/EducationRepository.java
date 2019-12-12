package com.furkan.thyeducation.models;

import com.furkan.thyeducation.models.data.Education;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Integer> {

    Education findBySlug(String slug);

    Education findBySlugAndIdNot(String slug, int id);

    Page<Education> findAll(Pageable pagable);

    List<Education> findAllByCategoryId(String categoryId, Pageable pageable);

    long countByCategoryId(String categoryId);
}
