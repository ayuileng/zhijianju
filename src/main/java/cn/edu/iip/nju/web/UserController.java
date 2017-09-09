package cn.edu.iip.nju.web;

import cn.edu.iip.nju.exception.UsernameExsitedException;
import cn.edu.iip.nju.model.User;
import cn.edu.iip.nju.model.UserSearchHistory;
import cn.edu.iip.nju.service.UserSearchHistoryService;
import cn.edu.iip.nju.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户信息管理控制器
 * Created by xu on 2017/9/4.
 */
@Controller
@RequestMapping("/u")
public class UserController {
    private final UserService userService;
    private final UserSearchHistoryService userSearchHistoryService;

    @Autowired
    public UserController(UserService userService,
                          UserSearchHistoryService userSearchHistoryService) {
        this.userService = userService;
        this.userSearchHistoryService = userSearchHistoryService;
    }

    @GetMapping("/{id}")
    public String userInfo(@PathVariable Integer id, Model model) {
        if (noAccess(id, model)) {
            return "error/accessError";
        }
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "u/info";


    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {
        if (noAccess(id, model)) {
            return "error/accessError";
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", user);
        return "u/edit";
    }

    /**
     * 1. 判断路径参数中的id是不是当前登录用户的id，如果不是则发生横向越权问题
     * 2. 验证表单提交的user对象有没有数据域没有通过valid的，如果有则返回修改页面
     * 3. 如果数据域都通过验证了，则判断用户输入的password_ori是不是原密码，如果不是原密码则返回修改页面
     * 4. 如果原始密码输入正确，更新用户的信息，然后退出登录
     */
    @PostMapping("/edit")
    public String saveEditedUser(Model model, @RequestParam(name = "password_new") String password_new,
                                 @RequestParam(name = "password_ori") String password_ori,
                                 @Valid User user, Errors errors) {
        if (noAccess(user.getId(), model)) {
            return "error/accessError";
        }
        if (errors.hasErrors()) {
            return "/u/edit";
        }
        if(password_new.length()>15||password_new.length()<6){
            model.addAttribute("pwsNewError","密码的长度为6~15!");
            return "/u/edit";
        }
        if (userService.isPasswordRight(user, password_ori)) {
            try {
                //注：此处service层必须调用dao层的saveAndFlush方法，
                //及时的更新数据库，不然的话马上要进行的重新登录可能会出现问题
                user.setPassword(password_new);
                userService.updateUser(user);
                return "redirect:/temp";
            } catch (UsernameExsitedException e) {
                user.setPassword(password_ori);
                model.addAttribute("msg", "usernameExsit");
                return "/u/edit";
            }
        } else {
            model.addAttribute("oriPwdError", "原始密码输入错误！！！");
            return "/u/edit";
        }
    }

    //需要对结果进行分页
    @GetMapping("/{id}/searchHistory")
    public String showUserSearchHistory(@PathVariable Integer id,Model model,
                                        @RequestParam(name = "page",defaultValue = "0")Integer page,
                                        @RequestParam(name = "size",defaultValue = "10",required = false)Integer size){
        if (noAccess(id, model)) {
            return "error/accessError";
        }
        Page<UserSearchHistory> history = userSearchHistoryService.getRecentHistory(id, new PageRequest(page, size));
        model.addAttribute("history",history);
        System.out.println(history.getTotalElements());
        return "/u/searchHistory";
    }

    @GetMapping("/{id}/searchHistory/delete")
    public String deleteHistory(@PathVariable Integer id,Model model){
        userSearchHistoryService.deleteAllHistoryByUserId(id);
        model.addAttribute("deleteSuccess","删除用户搜索记录成功");
        return "/u/searchHistory";
    }

    private boolean noAccess(@PathVariable Integer id, Model model) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!id.equals(currentUser.getId())) {
            model.addAttribute("msg", "无权限");
            return true;
        }
        return false;
    }
}
