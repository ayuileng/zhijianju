package cn.edu.iip.nju.web;

import cn.edu.iip.nju.exception.UsernameExsitedException;
import cn.edu.iip.nju.model.User;
import cn.edu.iip.nju.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * 进行登录注册url转发的控制器
 * Created by xu on 2017/9/3.
 */
@Controller
public class MainController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/index")
    public String index_(){
        return "index";
    }

    @GetMapping("/login")
    public String login(@RequestParam(name = "error",defaultValue = "false",required = false) String error, Model model){
        if(!error.equals("true")){
            return "login";
        }else {
            model.addAttribute("errorMSG","用户名或者密码错误！");
            return "login";
        }
    }

    @GetMapping("/test")
    @ResponseBody
    public String test(){
        return "login success!";
    }

    @GetMapping("/signup")
    public String  signup(Model model){
        model.addAttribute("user",new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String register(Model model,@Valid User user, Errors errors){
        if(errors.hasErrors()){
            return "signup";
        }else {
            try {
                userService.saveUser(user);
            } catch (UsernameExsitedException e) {
                model.addAttribute("msg","usernameExsit");
                return "signup";
            }
//            System.out.println(newUser);
            //实现登录
            Authentication request = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            Authentication result = authenticationManager.authenticate(request);
            SecurityContextHolder.getContext().setAuthentication(result);
            return "redirect:/";
        }
    }

    @GetMapping("/temp")
    public String temp(){
        return "temp";
    }


}
