package com.starcpt.cmuc.utils;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class ZipFileUtils {
	
    private static final int BUFFER = 4096;

    private ZipFileUtils() {}
    
    /**
     * 将SD卡上ZIP文件里面的内容复制到软件目录下
     * @param zipFile ZIP路径
     * @param targetDir 手机上的路径
     */
    public static boolean Unzip(String zipFile, String targetDir) {
        try {
            //文件输入流
            FileInputStream fis = new FileInputStream(zipFile);
            //ZIP文件输入流
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            //ZIP里面文件的对象
            ZipEntry entry = null;
            while ((entry = zis.getNextEntry()) != null) {
                int count;
                byte data[] = new byte[BUFFER];
                File entryFile = new File(targetDir + entry.getName());
                File entryDir = new File(entryFile.getParent());
                //如果没这个文件就创建这个文件
                if (!entryDir.exists()) {
                    entryDir.mkdirs();
                }
                //文件输出流
                FileOutputStream fos = new FileOutputStream(entryFile);
                //缓冲输出流
                BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER);
                while ((count = zis.read(data, 0, BUFFER)) != -1) {
                    bos.write(data, 0, count);
                }
                bos.flush();
                bos.close();
                fos.close();
            }
            zis.close();
            fis.close();
            return true;
        } catch (Exception e) {
            System.out.println("复制皮肤出错!"+e.toString());
            FileUtils.directoryIsExist(targetDir);
            return false;
        }
    }
    
    /**
     * 获得皮肤包下的配置文件信息
     * @param zipFile
     * @return
     */
    public static String getSkinInfo(String zipFile) {
    	//皮肤文件的配置信息
    	String skinInfo = "";
        try {
            //文件输入流
            FileInputStream fis = new FileInputStream(zipFile);
            //ZIP文件输入流
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            //ZIP里面文件的对象
            ZipEntry entry = null;
            while ((entry = zis.getNextEntry()) != null) {
                int count;
                byte data[] = new byte[1024];
                
                if(entry.getName().equals("skininfo.txt")) {
                	ByteArrayOutputStream baos = new ByteArrayOutputStream();
                	zis.skip(3);
        			while((count = zis.read(data)) != -1)
        			{
        				baos.write(data,0,count);
        			}
        			skinInfo = new String(baos.toByteArray(),"UTF_8");
        			baos.close();
        			return skinInfo;
                } else {
                	continue;
                }
            }
            zis.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return skinInfo;
    }
}
