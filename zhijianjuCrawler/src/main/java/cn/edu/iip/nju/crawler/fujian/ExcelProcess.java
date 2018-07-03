package cn.edu.iip.nju.crawler.fujian;

import cn.edu.iip.nju.dao.AttachmentDataDao;
import cn.edu.iip.nju.model.AttachmentData;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xu on 2017/5/14.
 */
@Service
public class ExcelProcess {
    private static final Logger logger = LoggerFactory.getLogger(ExcelProcess.class);
    @Autowired
    private AttachmentDataDao dao;

    //处理07年之前的excel文件
    private void processXLS() throws IOException {
        Resource resource = new ClassPathResource("files");

        List<String> xls = Depress.getFileList(resource.getFile(), "xls");
        for (String s : xls) {
            InputStream inputStream = new FileInputStream(new File(s));
            POIFSFileSystem fs;
            HSSFWorkbook wb;
            try {
                fs = new POIFSFileSystem(inputStream);
                wb = new HSSFWorkbook(fs);
            } catch (Exception e) {
                continue;
            }
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row1 = sheet.getRow(1);
            HSSFRow row2 = sheet.getRow(2);
            //大部分的表头格式都是差不多的
            //序号	企业名称	所在地	产品名称	商标	规格型号	生产日期/批号	抽查结果	主要不合格项目	承检机构
            //表头可能在第二行或者第三行，需要判断
            List<String> title = new ArrayList<>();
            if (row1 != null) {
                int physicalNumberOfCells = row1.getPhysicalNumberOfCells();
                int tmp = 0;
                for (int i = 0; i < physicalNumberOfCells; i++) {
                    if ((row1.getCell(i) == null) || (!"".equals(row1.getCell(i).toString().trim()))) {
                        tmp++;
                    }
                }
                if (tmp > 5) {
                    for (int i = 0; i < physicalNumberOfCells; i++) {
                        if (row1.getCell(i) != null) {
                            title.add(row1.getCell(i).toString());
                        }
                    }
                } else if (row2 != null) {
                    int physicalNumberOfCells2 = row2.getPhysicalNumberOfCells();
                    for (int i = 0; i < physicalNumberOfCells2; i++) {
                        if (row2.getCell(i) != null) {
                            title.add(row2.getCell(i).toString());
                        }
                    }
                }
            }
            int rowNum = sheet.getLastRowNum();
            //第二行或者第三行可能是表头，所以从第四行开始
            for (int i = 3; i < rowNum; i++) {
                HSSFRow row = sheet.getRow(i);
                AttachmentData attachment = new AttachmentData();
                if (row != null) {
                    short lastCellNum = row1.getLastCellNum();
//                        System.out.println(lastCellNum);
                    if (lastCellNum == -1) {
                        continue;
                    }
                    for (int index = 0; index < lastCellNum; index++) {
                        //序号	企业名称	所在地	产品名称	商标	规格型号	生产日期/批号	抽查结果	主要不合格项目	承检机构
                        if (index >= title.size()) {
                            break;
                        }
                        if (title.get(index).contains("企业名") || title.get(index).contains("生产单位")) {
                            if (row.getCell(index) != null) {
                                attachment.setFactoryName(row.getCell(index).toString());
                            }
                        } else if (title.get(index).contains("所在")) {
                            if (row.getCell(index) != null) {
                                attachment.setFactoryAddress(row.getCell(index).toString());
                            }
                        } else if (title.get(index).contains("商标")) {
                            if (row.getCell(index) != null) {
                                attachment.setShangbiao(row.getCell(index).toString());
                            }
                        } else if (title.get(index).contains("产品名")) {
                            if (row.getCell(index) != null) {
                                attachment.setName(row.getCell(index).toString());
                            }
                        } else if (title.get(index).contains("规格") || title.get(index).contains("型号")) {
                            if (row.getCell(index) != null) {
                                attachment.setGuigexinghao(row.getCell(index).toString());
                            }
                        } else if (title.get(index).contains("日期") || title.get(index).contains("批号")) {
                            if (row.getCell(index) != null) {
                                attachment.setBirthday(row.getCell(index).toString());
                            }
                        } else if (title.get(index).contains("结果") || title.get(index).contains("结论")) {
                            if (row.getCell(index) != null) {
                                String result = row.getCell(index).toString();
                                if(result.contains("不")){
                                    result = "不合格";
                                }
                                logger.info(result);
                                attachment.setResult(result);
                            }
                        } else if (title.get(index).contains("不合格项")) {
                            if (row.getCell(index) != null) {
                                attachment.setErrorReason(row.getCell(index).toString());
                            }
                        } else if (title.get(index).contains("承建机构")) {
                            if (row.getCell(index) != null) {
                                attachment.setChengjianjigou(row.getCell(index).toString());
                            }
                        }
                    }
                }
                if (attachment.isValid()) {
                    dao.save(attachment);
                }
                logger.info("save success!");
//                //合格的不用存
//                if (attachment.isFilled() && !("合格".equals(attachment.getResult()))) {
//                    dao.save(attachment);
//                }
            }
        }
    }

    //07年以后的excel
    private void precessXLSX() throws IOException {
        Resource resource = new ClassPathResource("files");
        List<String> xlsx = Depress.getFileList(resource.getFile(), "xlsx");
        for (String s : xlsx) {
            XSSFWorkbook xwb = null;
            try {
                InputStream inputStream = new FileInputStream(new File(s));
                xwb = new XSSFWorkbook(inputStream);
            } catch (Exception e) {
                continue;
            }
            XSSFSheet sheet = xwb.getSheetAt(0);
            if (sheet == null) {
                continue;
            }
            XSSFRow row1 = sheet.getRow(1);
            XSSFRow row2 = sheet.getRow(2);
            List<String> title = new ArrayList<>();
            if (row1 != null) {
                int tmp = 0;
                Iterator<Cell> cellIterator = row1.cellIterator();
                while (cellIterator.hasNext()) {
                    String text = cellIterator.next().toString();
                    if (text != null && !text.isEmpty()) {
                        tmp++;
                    }
                }
                if (tmp > 5) {
                    while (cellIterator.hasNext()) {
                        title.add(cellIterator.next().toString());
                    }
                } else if (row2 != null) {
                    Iterator<Cell> cellIterator1 = row2.cellIterator();
                    while (cellIterator1.hasNext()) {
                        title.add(cellIterator1.next().toString());
                    }
                }
            }
            int lastRowNum = sheet.getLastRowNum();
            //第二行或者第三行可能是表头，所以从第四行开始
            for (int i = 3; i < lastRowNum; i++) {
                XSSFRow row = sheet.getRow(i);
                AttachmentData attachment = new AttachmentData();
                if (row != null) {
                    short lastCellNum = row1.getLastCellNum();
//                        System.out.println(lastCellNum);
                    if (lastCellNum == -1) {
                        continue;
                    }
                    for (int index = 0; index < lastCellNum; index++) {
                        //序号	企业名称	所在地	产品名称	商标	规格型号	生产日期/批号	抽查结果	主要不合格项目	承检机构
                        if (index >= title.size()) {
                            break;
                        }
                        if (title.get(index).contains("企业名") || title.get(index).contains("生产单位")) {
                            if (row.getCell(index) != null) {
                                attachment.setFactoryName(row.getCell(index).toString());
                            }
                        } else if (title.get(index).contains("所在")) {
                            if (row.getCell(index) != null) {
                                attachment.setFactoryAddress(row.getCell(index).toString());
                            }
                        } else if (title.get(index).contains("商标")) {
                            if (row.getCell(index) != null) {
                                attachment.setShangbiao(row.getCell(index).toString());
                            }
                        } else if (title.get(index).contains("产品名")) {
                            if (row.getCell(index) != null) {
                                attachment.setName(row.getCell(index).toString());
                            }
                        } else if (title.get(index).contains("规格") || title.get(index).contains("型号")) {
                            if (row.getCell(index) != null) {
                                if (row.getCell(index).toString().length() < 255)
                                    attachment.setGuigexinghao(row.getCell(index).toString());
                            }
                        } else if (title.get(index).contains("日期") || title.get(index).contains("批号")) {
                            if (row.getCell(index) != null) {
                                attachment.setBirthday(row.getCell(index).toString());
                            }
                        } else if (title.get(index).contains("结果") || title.get(index).contains("结论")) {
                            if (row.getCell(index) != null) {
                                String result = row.getCell(index).toString();
                                if(result.contains("不")){
                                    result = "不合格";
                                }
                                logger.info(result);
                                attachment.setResult(result);
                            }
                        } else if (title.get(index).contains("不合格项")) {
                            if (row.getCell(index) != null) {
                                attachment.setErrorReason(row.getCell(index).toString());
                            }
                        } else if (title.get(index).contains("承建机构")) {
                            if (row.getCell(index) != null) {
                                attachment.setChengjianjigou(row.getCell(index).toString());
                            }
                        }
                    }
                }
                if (attachment.isValid()) {
                    dao.save(attachment);
                }
                logger.info("save success!");

            }
        }
    }

    public void run() {
        try {
            processXLS();
            logger.info("xls process done!");
            precessXLSX();
            logger.info("xlsx process done!");
        } catch (IOException e) {
            logger.error("save excel data fail", e);
        }

    }
}
