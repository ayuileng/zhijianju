package cn.edu.iip.nju.web;

import cn.edu.iip.nju.model.Document;
import cn.edu.iip.nju.model.User;
import cn.edu.iip.nju.model.UserSearchHistory;
import cn.edu.iip.nju.service.SolrDocumentService;
import cn.edu.iip.nju.service.UserSearchHistoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    @Autowired
    private SolrDocumentService solrDocumentService;

    @GetMapping
    public String processQuery(@RequestParam(name = "queryWord") String queryWord, Model model,
                               @RequestParam(name = "page", defaultValue = "0") Integer page,
                               @RequestParam(name = "sort",defaultValue = "keyWord") String sort) {
        //空查询则返回首页
        if (StringUtils.isBlank(queryWord)) {
            return "/index";
        }
        //判断用户是否登录，如果是登录状态，则保存搜索历史
        Integer userId = isUserLogIn();
        if (userId != -1) {

            UserSearchHistory history = new UserSearchHistory();
            history.setUserId(userId);
            history.setSearchHistory(queryWord);
            history.setSearchTime(new Date());
            if (userSearchHistoryService.isNewSearchHistory(history)) {
                userSearchHistoryService.saveSearchHistory(history);
            }
        }
        Sort s ;
        Page<Document> results;
        if(sort.equals("posttime")){
            s = new Sort(Sort.Direction.DESC,"post_time");
            results = solrDocumentService.findBySearchText(queryWord, new PageRequest(page, 10,s));
        }else {
            results = solrDocumentService.findBySearchText(queryWord, new PageRequest(page, 10));
        }
        model.addAttribute("queryWord", queryWord);
        model.addAttribute("results", results);
        model.addAttribute("sort",sort);

        //TODO 分页时保留原有的查询参数

        return "/s/result";

    }

    private Integer isUserLogIn() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser == null ? -1 : currentUser.getId();
    }
}
