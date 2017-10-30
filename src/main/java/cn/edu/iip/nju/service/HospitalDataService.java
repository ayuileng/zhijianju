package cn.edu.iip.nju.service;

import cn.edu.iip.nju.common.HospitalEnum;
import cn.edu.iip.nju.dao.HospitalDataDao;
import cn.edu.iip.nju.model.HospitalData;
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
import java.util.Date;
import java.util.Set;


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

    private File[] getFiles() throws IOException {
        Resource resource = new ClassPathResource("hospital");
        return resource.getFile().listFiles();
    }
    public Page<HospitalData> getByPage(Pageable pageable){
        return hospitalDataDao.findAll(pageable);
    }
    private Set[] getSheets() throws IOException {
        Set<XSSFSheet> xssfSheets = Sets.newHashSet();
        Set<HSSFSheet> hssfSheets = Sets.newHashSet();
        for (File file : getFiles()) {
            InputStream inputStream = new FileInputStream(file);
            if (file.getName().endsWith("xls")) {
                //07年以前的excel
                POIFSFileSystem fs = new POIFSFileSystem(inputStream);
                HSSFWorkbook wb = new HSSFWorkbook(fs);
                hssfSheets.add(wb.getSheetAt(0));
            } else if (file.getName().endsWith("xlsx")) {
                //07年以后的excel
                XSSFWorkbook xwb = new XSSFWorkbook(inputStream);
                xssfSheets.add(xwb.getSheetAt(0));
            }
        }
        return new Set[]{hssfSheets, xssfSheets};
    }

    public void readExcelToDatabase() {
        try {
            Set<HSSFSheet> hssfSheets = getSheets()[0];
            Set<XSSFSheet> xssfSheets = getSheets()[1];
            for (HSSFSheet hssfSheet : hssfSheets) {
//                HSSFRow title = hssfSheet.getRow(0);
//                Iterator<Cell> cellIterator = title.cellIterator();
//                while (cellIterator.hasNext()) {
//                    System.out.println(cellIterator.next());
//                }

                int rowLength = hssfSheet.getLastRowNum();
                for (int i = 1; i < rowLength; i++) {
                    HospitalData hospitalData = new HospitalData();
                    HSSFRow tmpRow = hssfSheet.getRow(i);
                    hospitalData.setName(tmpRow.getCell(3).getStringCellValue());
                    hospitalData.setGender(tmpRow.getCell(4).getStringCellValue());
                    hospitalData.setAge((int) tmpRow.getCell(5).getNumericCellValue());
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
                    String injureTime = tmpRow.getCell(9).getStringCellValue().split(" ")[0];
                    String visTime = tmpRow.getCell(10).getStringCellValue().split(" ")[0];
                    hospitalData.setInjureDate(parseDate(injureTime));
                    hospitalData.setVisDate(parseDate(visTime));
                    String injureReasonNum = tmpRow.getCell(11).getStringCellValue();
                    hospitalData.setInjureReason(HospitalEnum.getInjureReason().get(Integer.parseInt(injureReasonNum)));
                    String injureLocationNum = tmpRow.getCell(12).getStringCellValue();
                    hospitalData.setInjureLocation(HospitalEnum.getInjureLocation().get(Integer.parseInt(injureLocationNum)));
                    String injureActivityNum = tmpRow.getCell(13).getStringCellValue();
                    hospitalData.setActivityWhenInjure(HospitalEnum.getActivityWhenInjure().get(Integer.parseInt(injureActivityNum)));
                    String isIntentionNum = tmpRow.getCell(14).getStringCellValue();
                    hospitalData.setIfIntentional(HospitalEnum.getIfIntentional().get(Integer.parseInt(isIntentionNum)));
                    String injureTypeNum = tmpRow.getCell(15).getStringCellValue();
                    hospitalData.setInjureType(HospitalEnum.getInjureType().get(Integer.parseInt(injureTypeNum)));
                    String injureSiteNum = tmpRow.getCell(16).getStringCellValue();
                    hospitalData.setInjureSite(HospitalEnum.getInjureSite().get(Integer.parseInt(injureSiteNum)));
                    String injureDegreeNum = tmpRow.getCell(17).getStringCellValue();
                    hospitalData.setInjureDegree(HospitalEnum.getInjureDegree().get(Integer.parseInt(injureDegreeNum)));
                    HSSFCell lunchuangCell = tmpRow.getCell(18);
                    if (lunchuangCell!=null) {
                        String linchuangzhenduan = lunchuangCell.getStringCellValue();
                        hospitalData.setInjurejudge(linchuangzhenduan);
                    }
                    String injureResultNum = tmpRow.getCell(19).getStringCellValue();
                    hospitalData.setInjureResult(HospitalEnum.getInjureResult().get(Integer.parseInt(injureResultNum)));
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
                    String injureSystemNum = tmpRow.getCell(37).getStringCellValue();
                    hospitalData.setInjureSystem(HospitalEnum.getInjureSystem().get(Integer.parseInt(injureSystemNum)));
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

            for (XSSFSheet xssfSheet : xssfSheets) {

                int rowLength = xssfSheet.getLastRowNum();
                for (int i = 1; i < rowLength; i++) {
                    HospitalData hospitalData = new HospitalData();
                    XSSFRow tmpRow = xssfSheet.getRow(i);
                    hospitalData.setName(tmpRow.getCell(3).getStringCellValue());
                    hospitalData.setGender(tmpRow.getCell(4).getStringCellValue());
                    hospitalData.setAge((int) tmpRow.getCell(5).getNumericCellValue());
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
                    String injureTime = tmpRow.getCell(9).getStringCellValue().split(" ")[0];
                    String visTime = tmpRow.getCell(10).getStringCellValue().split(" ")[0];
                    hospitalData.setInjureDate(parseDate(injureTime));
                    hospitalData.setVisDate(parseDate(visTime));
                    String injureReasonNum = tmpRow.getCell(11).getStringCellValue();
                    hospitalData.setInjureReason(HospitalEnum.getInjureReason().get(Integer.parseInt(injureReasonNum)));
                    String injureLocationNum = tmpRow.getCell(12).getStringCellValue();
                    hospitalData.setInjureLocation(HospitalEnum.getInjureLocation().get(Integer.parseInt(injureLocationNum)));
                    String injureActivityNum = tmpRow.getCell(13).getStringCellValue();
                    hospitalData.setActivityWhenInjure(HospitalEnum.getActivityWhenInjure().get(Integer.parseInt(injureActivityNum)));
                    String isIntentionNum = tmpRow.getCell(14).getStringCellValue();
                    hospitalData.setIfIntentional(HospitalEnum.getIfIntentional().get(Integer.parseInt(isIntentionNum)));
                    String injureTypeNum = tmpRow.getCell(15).getStringCellValue();
                    hospitalData.setInjureType(HospitalEnum.getInjureType().get(Integer.parseInt(injureTypeNum)));
                    String injureSiteNum = tmpRow.getCell(16).getStringCellValue();
                    hospitalData.setInjureSite(HospitalEnum.getInjureSite().get(Integer.parseInt(injureSiteNum)));
                    String injureDegreeNum = tmpRow.getCell(17).getStringCellValue();
                    hospitalData.setInjureDegree(HospitalEnum.getInjureDegree().get(Integer.parseInt(injureDegreeNum)));
                    XSSFCell lunchuangCell = tmpRow.getCell(18);
                    if (lunchuangCell!=null) {
                        String linchuangzhenduan = lunchuangCell.getStringCellValue();
                        hospitalData.setInjurejudge(linchuangzhenduan);
                    }
                    String injureResultNum = tmpRow.getCell(19).getStringCellValue();
                    hospitalData.setInjureResult(HospitalEnum.getInjureResult().get(Integer.parseInt(injureResultNum)));
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
                    if (injureSystemCell!=null) {
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

        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("save hospotal data finish!");
    }

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
}
