package cn.edu.iip.nju.service;

import cn.edu.iip.nju.common.HospitalEnum;
import cn.edu.iip.nju.common.PageHelper;
import cn.edu.iip.nju.dao.HospitalDataDao;
import cn.edu.iip.nju.model.HospitalData;
import cn.edu.iip.nju.util.ProductCatUtil;
import cn.edu.iip.nju.util.WarningDegree;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by xu on 2017/10/23.
 */
@Service
public class HospitalDataService {
    private final Logger logger = LoggerFactory.getLogger(HospitalDataService.class);
    private final HospitalDataDao hospitalDataDao;

    @Autowired
    public HospitalDataService(HospitalDataDao hospitalDataDao) {
        this.hospitalDataDao = hospitalDataDao;
    }

    public Page<HospitalData> getByPage(Pageable pageable) {
        return hospitalDataDao.findAll(pageable);
    }

    //读取所有excel文件
    private File[] getFiles() throws IOException {
        Resource resource = new ClassPathResource("hospital");
        return resource.getFile().listFiles();
    }

    //读取用
    public void readExcelToDatabase() throws IOException {
        for (File file : getFiles()) {
            InputStream inputStream = new FileInputStream(file);
            if (file.getName().endsWith("xls")) {
                //07年以前的excel
                POIFSFileSystem fs = new POIFSFileSystem(inputStream);
                HSSFWorkbook wb = new HSSFWorkbook(fs);
                HSSFSheet hssfSheet = wb.getSheetAt(0);
                processXLS(hssfSheet);
            } else if (file.getName().endsWith("xlsx")) {
                //07年以后的excel
                XSSFWorkbook xwb = new XSSFWorkbook(inputStream);
                XSSFSheet xssfSheet = xwb.getSheetAt(0);
                processXLSX(xssfSheet);
            }
        }
        logger.info("save hospotal data finish!");
    }

    //处理xls文件并入库
    private void processXLSX(XSSFSheet xssfSheet) {
        int rowLength = xssfSheet.getLastRowNum();
        for (int i = 1; i < rowLength; i++) {
            HospitalData hospitalData = new HospitalData();
            XSSFRow tmpRow = xssfSheet.getRow(i);
            XSSFCell nameCell = tmpRow.getCell(3);
            if (nameCell != null) {
                hospitalData.setName(nameCell.getStringCellValue());
            }
            XSSFCell genderCell = tmpRow.getCell(4);
            if (genderCell != null) {
                hospitalData.setGender(genderCell.getStringCellValue());
            }
            XSSFCell ageCell = tmpRow.getCell(5);
            if (ageCell != null) {
                hospitalData.setAge((int) ageCell.getNumericCellValue());
            }
            XSSFCell hujiCell = tmpRow.getCell(6);
            if (hujiCell != null) {
                String hujiNum = hujiCell.getStringCellValue();
                hospitalData.setHuji(HospitalEnum.getHuji().get(Integer.parseInt(hujiNum)));
            }
            if (tmpRow.getCell(7) != null) {
                String eduNum = tmpRow.getCell(7).getStringCellValue();
                hospitalData.setEduDegree(HospitalEnum.getEducationDegree().get(Integer.parseInt(eduNum)));
            }
            XSSFCell vocationCell = tmpRow.getCell(8);
            if (vocationCell != null) {
                String vocationNum = vocationCell.getStringCellValue();
                hospitalData.setVocation(HospitalEnum.getVocation().get(Integer.parseInt(vocationNum)));
            }
            XSSFCell injureTimeCell = tmpRow.getCell(9);
            if (injureTimeCell != null) {
                String injureTime = injureTimeCell.getStringCellValue().split(" ")[0];
                hospitalData.setInjureDate(parseDate(injureTime));
            }
            XSSFCell visitTimeCell = tmpRow.getCell(10);
            if (visitTimeCell != null) {
                String visTime = visitTimeCell.getStringCellValue().split(" ")[0];
                hospitalData.setVisDate(parseDate(visTime));
            }
            XSSFCell reasonCell = tmpRow.getCell(11);
            if (reasonCell != null) {
                String injureReasonNum = reasonCell.getStringCellValue();
                hospitalData.setInjureReason(HospitalEnum.getInjureReason().get(Integer.parseInt(injureReasonNum)));
            }
            XSSFCell locationCell = tmpRow.getCell(12);
            if (locationCell != null) {
                String injureLocationNum = locationCell.getStringCellValue();
                hospitalData.setInjureLocation(HospitalEnum.getInjureLocation().get(Integer.parseInt(injureLocationNum)));
            }
            XSSFCell cityCell = tmpRow.getCell(13);
            if (cityCell != null) {
                String injureActivityNum = cityCell.getStringCellValue();
                hospitalData.setActivityWhenInjure(HospitalEnum.getActivityWhenInjure().get(Integer.parseInt(injureActivityNum)));
            }
            XSSFCell intentionCell = tmpRow.getCell(14);
            if (intentionCell != null) {
                String isIntentionNum = intentionCell.getStringCellValue();
                hospitalData.setIfIntentional(HospitalEnum.getIfIntentional().get(Integer.parseInt(isIntentionNum)));
            }
            XSSFCell typeCell = tmpRow.getCell(15);
            if (typeCell != null) {
                String injureTypeNum = typeCell.getStringCellValue();
                hospitalData.setInjureType(HospitalEnum.getInjureType().get(Integer.parseInt(injureTypeNum)));
            }
            XSSFCell siteCell = tmpRow.getCell(16);
            if (siteCell != null) {
                String injureSiteNum = siteCell.getStringCellValue();
                hospitalData.setInjureSite(HospitalEnum.getInjureSite().get(Integer.parseInt(injureSiteNum)));
            }
            XSSFCell degreeCell = tmpRow.getCell(17);
            if (degreeCell != null) {
                String injureDegreeNum = degreeCell.getStringCellValue();
                hospitalData.setInjureDegree(HospitalEnum.getInjureDegree().get(Integer.parseInt(injureDegreeNum)));
            }
            XSSFCell lunchuangCell = tmpRow.getCell(18);
            if (lunchuangCell != null) {
                String linchuangzhenduan = lunchuangCell.getStringCellValue();
                hospitalData.setInjurejudge(linchuangzhenduan);
            }
            XSSFCell resultCell = tmpRow.getCell(19);
            if (resultCell != null) {
                String injureResultNum = resultCell.getStringCellValue();
                hospitalData.setInjureResult(HospitalEnum.getInjureResult().get(Integer.parseInt(injureResultNum)));
            }
            String productOne = "";
            if (tmpRow.getCell(30) != null) {
                productOne = tmpRow.getCell(30).getStringCellValue();
            }
            XSSFCell productTwo = tmpRow.getCell(31);
            if (productTwo != null) {
                String string = ((XSSFCell) productTwo).getStringCellValue();
                productOne += string;
            }

            hospitalData.setProduct(productOne);
            XSSFCell ancoholCell = tmpRow.getCell(36);
            try {
                if (ancoholCell != null) {
                    String ancoholNum = ancoholCell.getStringCellValue();
                    hospitalData.setAlcohol(HospitalEnum.getAlcohol().get(Integer.parseInt(ancoholNum)));
                }
            } catch (NumberFormatException e) {
                hospitalData.setAlcohol("不清楚");
            }
            XSSFCell injureSystemCell = tmpRow.getCell(37);
            if (injureSystemCell != null) {
                String injureSystemNum = injureSystemCell.getStringCellValue();
                try {
                    int tmpNum = Integer.parseInt(injureSystemNum);
                    hospitalData.setInjureSystem(HospitalEnum.getInjureSystem().get(tmpNum));
                } catch (NumberFormatException e) {
                    hospitalData.setInjureSystem("不清楚");
                }
            }
            XSSFCell howGetInjureCell = tmpRow.getCell(39);
            if (howGetInjureCell != null) {
                String howGetInjureNum = howGetInjureCell.getStringCellValue();
                hospitalData.setHowGetInjure(HospitalEnum.getHowGetInjure().get(Integer.parseInt(howGetInjureNum)));
            }
            hospitalDataDao.save(hospitalData);
            logger.info("save hospital data success");
        }

    }

    //处理xlsx文件并入库
    private void processXLS(HSSFSheet hssfSheet) {
        int rowLength = hssfSheet.getLastRowNum();
        for (int i = 1; i < rowLength; i++) {
            HospitalData hospitalData = new HospitalData();
            HSSFRow tmpRow = hssfSheet.getRow(i);
            HSSFCell nameCell = tmpRow.getCell(3);
            if (nameCell != null) {
                hospitalData.setName(nameCell.getStringCellValue());
            }
            HSSFCell genderCell = tmpRow.getCell(4);
            if (genderCell != null) {
                hospitalData.setGender(genderCell.getStringCellValue());
            }
            HSSFCell ageCell = tmpRow.getCell(5);
            if (ageCell != null) {
                hospitalData.setAge((int) ageCell.getNumericCellValue());
            }
            HSSFCell hujiCell = tmpRow.getCell(6);
            if (hujiCell != null) {
                String hujiNum = hujiCell.getStringCellValue();
                hospitalData.setHuji(HospitalEnum.getHuji().get(Integer.parseInt(hujiNum)));
            }
            if (tmpRow.getCell(7) != null) {
                String eduNum = tmpRow.getCell(7).getStringCellValue();
                hospitalData.setEduDegree(HospitalEnum.getEducationDegree().get(Integer.parseInt(eduNum)));
            }
            HSSFCell vocationCell = tmpRow.getCell(8);
            if (vocationCell != null) {
                String vocationNum = vocationCell.getStringCellValue();
                hospitalData.setVocation(HospitalEnum.getVocation().get(Integer.parseInt(vocationNum)));
            }
            HSSFCell injureTimeCell = tmpRow.getCell(9);
            if (injureTimeCell != null) {
                String injureTime = injureTimeCell.getStringCellValue().split(" ")[0];
                hospitalData.setInjureDate(parseDate(injureTime));
            }
            HSSFCell visitCell = tmpRow.getCell(10);
            if (visitCell != null) {
                String visTime = visitCell.getStringCellValue().split(" ")[0];
                hospitalData.setVisDate(parseDate(visTime));
            }
            HSSFCell reasonCell = tmpRow.getCell(11);
            if (reasonCell != null) {
                String injureReasonNum = reasonCell.getStringCellValue();
                hospitalData.setInjureReason(HospitalEnum.getInjureReason().get(Integer.parseInt(injureReasonNum)));
            }
            HSSFCell locationCell = tmpRow.getCell(12);
            if (locationCell != null) {
                String injureLocationNum = locationCell.getStringCellValue();
                hospitalData.setInjureLocation(HospitalEnum.getInjureLocation().get(Integer.parseInt(injureLocationNum)));
            }
            HSSFCell activyCell = tmpRow.getCell(13);
            if (activyCell != null) {
                String injureActivityNum = activyCell.getStringCellValue();
                hospitalData.setActivityWhenInjure(HospitalEnum.getActivityWhenInjure().get(Integer.parseInt(injureActivityNum)));
            }
            HSSFCell intentionCell = tmpRow.getCell(14);
            if (intentionCell != null) {
                String isIntentionNum = intentionCell.getStringCellValue();
                hospitalData.setIfIntentional(HospitalEnum.getIfIntentional().get(Integer.parseInt(isIntentionNum)));
            }
            HSSFCell typeCELL = tmpRow.getCell(15);
            if (typeCELL != null) {
                String injureTypeNum = typeCELL.getStringCellValue();
                hospitalData.setInjureType(HospitalEnum.getInjureType().get(Integer.parseInt(injureTypeNum)));
            }
            HSSFCell siteCell = tmpRow.getCell(16);
            if (siteCell != null) {
                String injureSiteNum = siteCell.getStringCellValue();
                hospitalData.setInjureSite(HospitalEnum.getInjureSite().get(Integer.parseInt(injureSiteNum)));
            }
            HSSFCell degreeCell = tmpRow.getCell(17);
            if (degreeCell != null) {
                String injureDegreeNum = degreeCell.getStringCellValue();
                hospitalData.setInjureDegree(HospitalEnum.getInjureDegree().get(Integer.parseInt(injureDegreeNum)));
            }
            HSSFCell lunchuangCell = tmpRow.getCell(18);
            if (lunchuangCell != null) {
                String linchuangzhenduan = lunchuangCell.getStringCellValue();
                hospitalData.setInjurejudge(linchuangzhenduan);
            }
            HSSFCell resultCell = tmpRow.getCell(19);
            if (reasonCell != null) {
                String injureResultNum = resultCell.getStringCellValue();
                hospitalData.setInjureResult(HospitalEnum.getInjureResult().get(Integer.parseInt(injureResultNum)));
            }
            String productOne = "";
            if (tmpRow.getCell(30) != null) {
                productOne = tmpRow.getCell(30).getStringCellValue();
            }
            HSSFCell productTwo = tmpRow.getCell(31);
            if (productTwo != null) {
                String string = ((HSSFCell) productTwo).getStringCellValue();
                productOne += string;
            }

            hospitalData.setProduct(productOne);
            HSSFCell ancoholCell = tmpRow.getCell(36);
            try {
                if (ancoholCell != null) {
                    String ancoholNum = ancoholCell.getStringCellValue();
                    hospitalData.setAlcohol(HospitalEnum.getAlcohol().get(Integer.parseInt(ancoholNum)));
                }
            } catch (NumberFormatException e) {
                hospitalData.setAlcohol("不清楚");
            }
            HSSFCell systemCell = tmpRow.getCell(37);
            if (systemCell != null) {
                String injureSystemNum = systemCell.getStringCellValue();
                hospitalData.setInjureSystem(HospitalEnum.getInjureSystem().get(Integer.parseInt(injureSystemNum)));
            }
            HSSFCell howGetInjureCell = tmpRow.getCell(39);
            if (howGetInjureCell != null) {
                String howGetInjureNum = howGetInjureCell.getStringCellValue();
                hospitalData.setHowGetInjure(HospitalEnum.getHowGetInjure().get(Integer.parseInt(howGetInjureNum)));
            }
//                    System.out.println(hospitalData);
            hospitalDataDao.save(hospitalData);
            logger.info("save hospital data success");
        }
    }

    //日期转换
    private Date parseDate(String dateString) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
        Date date;
        try {
            if (dateString.contains("/")) {
                date = sdf2.parse(dateString);
            } else if (dateString.contains("-")) {
                date = sdf1.parse(dateString);
            } else {
                date = null;
            }
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date(0);
    }

    //查询所有地点
    public Set<String> getLocations() {
        Set<String> set = Sets.newHashSet();
        List<String> location = hospitalDataDao.getLocation();
        location.forEach(s -> set.add(s));
        return set;

    }

    //根据location查询记录数
    @Cacheable(value = "injureLocationCount",key = "'1'+#location")
    public Long countByLocation(String location) {
        return hospitalDataDao.countAllByInjureLocation(location);
    }

    //根据月份查询记录数
    @Cacheable(value = "monthhospitalcount",key = "'1'+#month")
    public int countByMonth(int month) {
        return Math.toIntExact(hospitalDataDao.countByMonth(month));
    }

    //根据条件sql查询并封装分页结果
    public PageHelper<HospitalData> getListData(String sql, int page, String countSQL) {
        List<HospitalData> hospitalData = hospitalDataDao.pagingGet(sql, page);
        long count = hospitalDataDao.conditionCount(countSQL);
        PageHelper<HospitalData> pageData = new PageHelper(page, (int) count);
        pageData.setContent(hospitalData);
        return pageData;
    }

    //给每一条记录增加product_cat列
    public void setProductCat(Pageable pageable) {
        Page<HospitalData> all = hospitalDataDao.findAll(pageable);
        List<HospitalData> content = all.getContent();
        for (HospitalData hospitalData : content) {
            String productName = hospitalData.getProduct();
            if (ProductCatUtil.getCloths().contains(productName)) {
                hospitalData.setProductCat(ProductCatUtil.clothsStr);
            } else if (ProductCatUtil.getJiaju().contains(productName)) {
                hospitalData.setProductCat(ProductCatUtil.jiajuStr);
            } else if (ProductCatUtil.getEduSports().contains(productName)) {
                hospitalData.setProductCat(ProductCatUtil.eduSportsStr);
            } else if (ProductCatUtil.getElec().contains(productName)) {
                hospitalData.setProductCat(ProductCatUtil.elecStr);
            } else if (ProductCatUtil.getRuiqi().contains(productName)) {
                hospitalData.setProductCat(ProductCatUtil.ruiqiStr);
            } else if (ProductCatUtil.getToy().contains(productName)) {
                hospitalData.setProductCat(ProductCatUtil.toyStr);
            } else {
                hospitalData.setProductCat(ProductCatUtil.otherStr);
            }
        }
        hospitalDataDao.save(content);
    }


    public Map<String, Double> pca() throws Exception {
//        服装：1
//        家具及家庭生活用品：2
//        文教体育用品：3
//        锐器：4
//        玩具：5
//        家用电器：6
//        其他：7
        //顺序 f p 轻中重
        Map<String, Double> pca = Maps.newTreeMap();
        String[] cats = {"服装", "家具及家庭生活用品", "文教体育用品", "锐器", "玩具", "家用电器", "其他"};
        for (String cat : cats) {
            Long F = hospitalDataDao.countAllByProductCat(2017, cat);
            System.out.println(F);
            Date date = hospitalDataDao.findLastDateByProductCat(cat);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Calendar today = Calendar.getInstance();
            Long p = ((today.getTimeInMillis() - calendar.getTimeInMillis()) / (1000 * 60 * 60 * 24));
            Long sc1 = hospitalDataDao.countAllByInjureDegreeAndProductCat(2017, "轻度", cat);
            Long sc2 = hospitalDataDao.countAllByInjureDegreeAndProductCat(2017, "中度", cat);
            Long sc3 = hospitalDataDao.countAllByInjureDegreeAndProductCat(2017, "重度", cat);
            System.out.println(WarningDegree.warningScore(F, p, sc1, sc2, sc3));

            pca.put(cat, WarningDegree.warningScore(F, p, sc1, sc2, sc3));

        }
        return pca;
    }
}
