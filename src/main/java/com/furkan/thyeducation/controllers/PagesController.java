package com.furkan.thyeducation.controllers;

import com.furkan.thyeducation.models.CategoryRepository;
import com.furkan.thyeducation.models.PageRepository;
import com.furkan.thyeducation.models.data.Category;
import com.furkan.thyeducation.models.data.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class PagesController {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    PageRepository pageRepository;

    @GetMapping
    public String home(Model model) {

        //Page page = pageRepository.findBySlug("home");
        List<Category> categories = categoryRepository.findAllByOrderBySortingAsc();
        //model.addAttribute("page", page);

        model.addAttribute("categories", categories);
        //model.addAttribute("page", page);

        return "category_view";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/{slug}")
    public String page(@PathVariable String slug, Model model) {

        Page page = pageRepository.findBySlug(slug);

        if (page == null) {
            return "redirect:/";
        }

        model.addAttribute("page", page);

        return "page";
    }
}
