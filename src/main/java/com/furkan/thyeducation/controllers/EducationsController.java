package com.furkan.thyeducation.controllers;

import com.furkan.thyeducation.models.CategoryRepository;
import com.furkan.thyeducation.models.EducationRepository;
import com.furkan.thyeducation.models.data.Category;
import com.furkan.thyeducation.models.data.Education;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/education")
public class EducationsController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EducationRepository educationRepository;

    @GetMapping("/view/{id}")
    public String education(@PathVariable int id, Model model) {

        Education educations = educationRepository.getOne(id);

        String categoryId = educations.getCategoryId();

        Category category = categoryRepository.getOne(Integer.parseInt(categoryId));

        String categoryName = category.getName();

        model.addAttribute("educations", educations);
        model.addAttribute("categoryName", categoryName);

        return "education_view";
    }
}
