package cn.edu.iip.nju.web;

import cn.edu.iip.nju.model.User;
import cn.edu.iip.nju.model.UserSearchHistory;
import cn.edu.iip.nju.service.UserSearchHistoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Created by xu on 2017/9/5.
 */
@Controller
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private UserSearchHistoryService userSearchHistoryService;

    @GetMapping
    public String processQuery(@RequestParam String queryWord, Model model) {
        //TODO solr indexing

        //空查询则返回首页
        if(StringUtils.isBlank(queryWord)){
            return "/index";
        }
        System.out.println(queryWord);
        //判断用户是否登录，如果是登录状态，则保存搜索历史
        Integer userId = isUserLogIn();
        if (userId != -1) {
            UserSearchHistory history = new UserSearchHistory();
            history.setUserId(userId);
            history.setSearchHistory(queryWord);
            history.setSearchTime(new Date());
            userSearchHistoryService.saveSearchHistory(history);
        }
        model.addAttribute("queryWord",queryWord);
        return "/s/result";

    }

    private Integer isUserLogIn() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser == null ? -1 : currentUser.getId();
    }
}
