package com.furkan.thyeducation.security;


import com.furkan.thyeducation.models.UserRepository;
import com.furkan.thyeducation.models.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping("/change-password")
public class ChangePassword {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String changePassword(User user) {
        return "change-password";
    }

    @PostMapping
    public String changePassword(@Valid User user, BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 HttpServletRequest httpServletRequest) throws IOException {

        if (bindingResult.hasErrors()) {
            return "change-password";
        }

        if (!user.getUsername().equals(httpServletRequest.getParameter("username")) && !user.getPassword().equals(httpServletRequest.getParameter("oldPassword"))) {
            redirectAttributes.addFlashAttribute("message", "Username not found or Password is not true");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            System.out.println(httpServletRequest.getParameter("username"));
            System.out.println(httpServletRequest.getParameter("oldPassword"));
            return "change-password";
        }

        if (!httpServletRequest.getParameter("password").equals(httpServletRequest.getParameter("confirmPassword"))) {
            redirectAttributes.addFlashAttribute("message", "Passwords do not match!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "change-password";
        }

        redirectAttributes.addFlashAttribute("message", "Changed Successfully");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        user.setPassword(passwordEncoder.encode(httpServletRequest.getParameter("password")));
        userRepository.save(user);

        return "redirect:/login";
    }
}
