package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        model.addAttribute("user", user);
        if (result.hasErrors()) {
            return "registration";
        } else {
            userService.saveUser(user);
            model.addAttribute("message", "User Account Created");
        }
        return "redirect:/login";
    }

    @RequestMapping("/")
    public String index(Principal principal, Model model) {
        if(userService.getUser()!=null) {
            model.addAttribute("myuser", userService.getUser());
            String username = principal.getName();
            model.addAttribute("user", userRepository.findByUsername(username));
        }
        model.addAttribute("messages", messageRepository.findAll());
        return "index";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/profile")
    public String showProfile(Principal principal, Model model) {
        String username = principal.getName();
        model.addAttribute("user", userRepository.findByUsername(username));
        return "profile";
    }

    @GetMapping("/add-message")
    public String messageForm(Model model) {
        model.addAttribute("myuser", userService.getUser());
        model.addAttribute("user", userService.getUser());
        model.addAttribute("message", new Message());
        return "message";
    }

    @PostMapping("/process-message")
    public String processForm(@ModelAttribute Message message, BindingResult result,
                              @RequestParam("file") MultipartFile file, @RequestParam String img, Model model) {
        model.addAttribute("myuser", userService.getUser());
        model.addAttribute("user", userService.getUser());
        //check for errors on the form
        if (result.hasErrors()) {
            return "message";
        }
        if (file.isEmpty()){
            message.setImg(img);
            message.setUser(userService.getUser());
            messageRepository.save(message);
            return  "redirect:/";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype","auto"));
            message.setImg(uploadResult.get("url").toString());
            message.setUser(userService.getUser());
            messageRepository.save(message);
        }catch (IOException e){
            e.printStackTrace();
            return  "redirect:/add-message";}

        if (result.hasErrors()) {
            return "message";
        }
        return "redirect:/";
    }

    @RequestMapping("/user/{id}")
    public String getUser(@PathVariable("id") long id, Model model) {
        User user = userRepository.findById(id).get();
        model.addAttribute("user", user);
        model.addAttribute("myuser", userService.getUser());
        return "profile";
    }

    @RequestMapping("/update/{id}")
    public String updateItem(@PathVariable("id") long id, Model model){
        model.addAttribute("myuser", userService.getUser());
        model.addAttribute("message", messageRepository.findById(id).get());
        if (userService.getUser() != null) {
            model.addAttribute("user", userService.getUser());
        }
        Message message = messageRepository.findById(id).get();
        String img = message.getImg();
        model.addAttribute("img", img);
        return "update-message";
    }

    @RequestMapping("/delete/{id}")
    public String deleteItem(@PathVariable("id") long id){
        messageRepository.deleteById(id);
        return "redirect:/";
    }

    @RequestMapping("/login?logout")
        public String logout() {
        return "redirect:/";
    }

}
