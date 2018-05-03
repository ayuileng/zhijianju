package cn.edu.iip.nju.web;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;

/**
 * Created by xu on 2018/1/18.
 */
@Controller
@RequestMapping("/dataSource")
public class DataSourceController {

    @PostMapping("/hosUp")
    public String uploadHosFile(@RequestPart("file") MultipartFile file, RedirectAttributes falshModel, Model model) {
        if (file == null||file.isEmpty()) {
            model.addAttribute("msg","未选择任何文件");
            return "setting";
        }
        String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        System.out.println(fileType);
        if (!(".xls".equals(fileType) || ".xlsx".equals(fileType))) {
            //上传文件不是excel文件
            model.addAttribute("msg", "上传文件格式错误！！");
            return "setting";
        }
        try {
            Resource resource = new ClassPathResource("hospital");
            File dir = resource.getFile();
            if (dir.isDirectory()) {
                System.out.println(dir + "\\" + file.getOriginalFilename());
                file.transferTo(new File(dir + "\\" + file.getOriginalFilename()));
            }
            falshModel.addFlashAttribute("msg", "上传成功！");
            return "redirect:/setting";
        } catch (IOException e) {
            model.addAttribute("msg", "文件上传失败，请重试！");
        }
        return "setting";
    }
}
