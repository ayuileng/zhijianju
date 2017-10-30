package cn.edu.iip.nju.util;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by xu on 2017/5/7.
 * 解压缩的工具类
 */
public class RarZipUtils {
    private static void unzip(String sourceZip, String destDir) throws Exception {
        try {
            Project p = new Project();
            Expand e = new Expand();
            e.setProject(p);
            e.setSrc(new File(sourceZip));
            e.setOverwrite(false);
            e.setDest(new File(destDir));
           /*
           ant下的zip工具默认压缩编码为UTF-8编码，
           而winRAR软件压缩是用的windows默认的GBK或者GB2312编码
           所以解压缩时要制定编码格式
           */
            e.setEncoding("gbk");
            e.execute();
        } catch (Exception e) {
            throw e;
        }
    }

    private static void unrar(String sourceRar, String destDir) throws Exception {
        Archive a = null;
        FileOutputStream fos = null;
        try {
            a = new Archive(new File(sourceRar));
            FileHeader fh = a.nextFileHeader();
            while (fh != null) {
                if (!fh.isDirectory()) {
                    //1 根据不同的操作系统拿到相应的 destDirName 和 destFileName
                    String compressFileName;
                    if(fh.isUnicode()){//解決中文乱码
                        compressFileName = fh.getFileNameW().trim();
                    }else{
                        compressFileName = fh.getFileNameString().trim();
                    }
                    String destFileName = "";
                    String destDirName = "";
                    //非windows系统
                    if (File.separator.equals("/")) {
                        destFileName = destDir + compressFileName.replaceAll("\\\\", "/");
                        destDirName = destFileName.substring(0, destFileName.lastIndexOf("/"));
                        //windows系统
                    } else {
                        destFileName = destDir + compressFileName.replaceAll("/", "\\\\");
                        destDirName = destFileName.substring(0, destFileName.lastIndexOf("\\"));
                    }
                    //2创建文件夹
                    File dir = new File(destDirName);
                    if (!dir.exists() || !dir.isDirectory()) {
                        dir.mkdirs();
                    }
                    //3解压缩文件
                    fos = new FileOutputStream(new File(destFileName));
                    a.extractFile(fh, fos);
                    fos.close();
                    fos = null;
                }
                fh = a.nextFileHeader();
            }
            a.close();
            a = null;
        } catch (Exception e) {
            throw e;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (a != null) {
                try {
                    a.close();
                    a = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void deCompress(String sourceFile,String destDir) throws Exception{
        //保证文件夹路径最后是"/"或者"\"
        char lastChar = destDir.charAt(destDir.length()-1);
        if(lastChar!='/'&&lastChar!='\\'){
            destDir += File.separator;
        }
        //根据类型，进行相应的解压缩
        String type = sourceFile.substring(sourceFile.lastIndexOf(".")+1);
        if(type.equals("zip")){
            RarZipUtils.unzip(sourceFile, destDir);
        }else if(type.equals("rar")){
            RarZipUtils.unrar(sourceFile, destDir);
        }else{
            throw new Exception("只支持zip和rar格式的压缩包！");
        }
    }

    @Test
    public void test() throws Exception {
        RarZipUtils.deCompress("C:\\Users\\63117\\IdeaProjects\\zhijianCrawler_4.30" +
                "\\attachment\\GJZLJDJYJYZJ\\rar\\P020130403469630469990.rar","C:\\Users\\63117\\IdeaProjects\\zhijianCrawler_4.30" +
                "\\attachment\\GJZLJDJYJYZJ\\zip");


    }
}
