package com.furkan.thyeducation.controllers;

import com.furkan.thyeducation.models.CategoryRepository;
import com.furkan.thyeducation.models.EducationRepository;
import com.furkan.thyeducation.models.data.Category;
import com.furkan.thyeducation.models.data.Education;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/admin/educations")
public class AdminEducationsController {

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String index(Model model, @RequestParam(value="page", required = false) Integer p) {

        int perPage = 6;  //Default 6
        int page = (p != null) ? p : 0;
        Pageable pageable = PageRequest.of(page, perPage);

        Page<Education> educations = educationRepository.findAll(pageable);
        List<Category> categories = categoryRepository.findAll();

        HashMap<Integer, String> cats = new HashMap<>();
        for (Category cat : categories) {
            cats.put(cat.getId(), cat.getName());
        }

        model.addAttribute("educations", educations);
        model.addAttribute("cats", cats);

        long count = educationRepository.count();
        double pageCount = Math.ceil((double)count / (double)perPage);

        model.addAttribute("pageCount", (int)pageCount);
        model.addAttribute("perPage", perPage);
        model.addAttribute("count", count);
        model.addAttribute("page", page);

        return "admin/educations/index";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute Education education, Model model) {

        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        return "admin/educations/add";
    }

    @PostMapping("/add")
    public String add(@Valid Education education,
                      BindingResult bindingResult,
                      MultipartFile file,
                      RedirectAttributes redirectAttributes,
                      Model model) throws IOException {

        List<Category> categories = categoryRepository.findAll();

        if(bindingResult.hasErrors()){
            model.addAttribute("categories", categories);
            return "admin/educations/add";
        }

        boolean fileOK = false;
        byte[] bytes = file.getBytes();
        String filename = file.getOriginalFilename();
        Path path = Paths.get("src/main/resources/static/media/" + filename);

        if (filename.endsWith("jpg") || filename.endsWith("png")) {
            fileOK = true;
        }

        redirectAttributes.addFlashAttribute("message", "Educations added");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        String slug = education.getName().toLowerCase().replace(" ", "-");

        Education educationExists = educationRepository.findBySlug(slug);

        if ( !fileOK ) {
            redirectAttributes.addFlashAttribute("message", "Image must be a jpg or png");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("education", education);
        }
        else if ( educationExists != null) {
            redirectAttributes.addFlashAttribute("message", "Education exists, choose another");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("education", education);
        } else {
            education.setSlug(slug);
            education.setImage(filename);
            educationRepository.save(education);

            Files.write(path, bytes);
        }
        return "redirect:/admin/educations/add";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model) {

        List<Category> categories = categoryRepository.findAll();
        Education education = educationRepository.getOne(id);

        model.addAttribute("education", education);
        model.addAttribute("categories", categories);

        return "admin/educations/edit";
    }

    @PostMapping("/edit")
    public String edit(@Valid Education education,
                       BindingResult bindingResult,
                       MultipartFile file,
                       RedirectAttributes redirectAttributes,
                       Model model) throws IOException {

        Education currentEducation = educationRepository.getOne(education.getId());


        List<Category> categories = categoryRepository.findAll();

        if(bindingResult.hasErrors()){
            model.addAttribute("educationName", currentEducation.getName());
            model.addAttribute("categories", categories);

            return "admin/educations/edit";
        }

        boolean fileOK = false;
        byte[] bytes = file.getBytes();
        String filename = file.getOriginalFilename();
        Path path = Paths.get("src/main/resources/static/media/" + filename);


        if (!file.isEmpty()) {
            if (filename.endsWith("jpg") || filename.endsWith("png")) {
                fileOK = true;
            }
        } else {
            fileOK = true;
        }

        redirectAttributes.addFlashAttribute("message", "Educations edited");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        String slug = education.getName().toLowerCase().replace(" ", "-");

        Education educationExists = educationRepository.findBySlugAndIdNot(slug, education.getId());

        if ( !fileOK ) {
            redirectAttributes.addFlashAttribute("message", "Image must be a jpg or png");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("education", education);
        }
        else if ( educationExists != null) {
            redirectAttributes.addFlashAttribute("message", "Education exists, choose another");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("education", education);
        } else {
            education.setSlug(slug);

            if (!file.isEmpty()) {
                Path path2 = Paths.get("src/main/resources/static/media/" + currentEducation.getImage());
                Files.delete(path2);
                education.setImage(filename);
                Files.write(path, bytes);
            } else {
                education.setImage(currentEducation.getImage());
            }
            educationRepository.save(education);

        }
        return "redirect:/admin/educations/edit/" + education.getId();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) throws IOException {

        Education education = educationRepository.getOne(id);
        Education currentEducation = educationRepository.getOne(education.getId());

        Path path2 = Paths.get("src/main/resources/static/media/" + currentEducation.getImage());
        Files.delete(path2);
        educationRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("message", "Education Deleted");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        return "redirect:/admin/educations";
    }
}
