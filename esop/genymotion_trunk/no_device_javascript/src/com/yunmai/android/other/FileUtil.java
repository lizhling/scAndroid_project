/*     */ package com.yunmai.android.other;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.util.UUID;
/*     */ 
/*     */ public class FileUtil
/*     */ {
/*     */   public void finalize()
/*     */   {
/*     */   }
/*     */ 
/*     */   public static boolean exist(String filepath)
/*     */   {
/*  47 */     if (filepath == null) return false;
/*  48 */     File file = new File(filepath);
/*  49 */     boolean exist = file.exists();
/*  50 */     file = null;
/*  51 */     return exist;
/*     */   }
/*     */ 
/*     */   public static boolean isDirectory(String filepath)
/*     */   {
/*  63 */     File file = new File(filepath);
/*  64 */     return file.isDirectory();
/*     */   }
/*     */ 
/*     */   public static boolean makeSureDirExist(String dirpath)
/*     */   {
/*  77 */     boolean exist = true;
/*  78 */     File file = new File(dirpath);
/*  79 */     if (!file.exists()) {
/*  80 */       exist = file.mkdir();
/*     */     }
/*  82 */     return exist;
/*     */   }
/*     */ 
/*     */   public static boolean makeSureFileExist(String filepath)
/*     */   {
/*  95 */     boolean exist = true;
/*  96 */     File file = new File(filepath);
/*  97 */     if (!file.exists()) {
/*     */       try {
/*  99 */         exist = file.createNewFile();
/*     */       } catch (IOException e) {
/* 101 */         exist = false;
/*     */       }
/*     */     }
/* 104 */     return exist;
/*     */   }
/*     */ 
/*     */   public static int makeSureFileExistEx(String filepath)
/*     */   {
/* 117 */     int status = -1;
/* 118 */     File file = new File(filepath);
/* 119 */     if (!file.exists())
/*     */       try {
/* 121 */         if (file.createNewFile())
/* 122 */           status = 0;
/*     */       } catch (IOException e) {
/* 124 */         status = -1;
/*     */       }
/*     */     else {
/* 127 */       status = (int)file.length();
/*     */     }
/* 129 */     return status;
/*     */   }
/*     */ 
/*     */   public static int getFileLength(String filepath)
/*     */   {
/* 141 */     File file = new File(filepath);
/* 142 */     if (file.exists()) {
/* 143 */       return (int)file.length();
/*     */     }
/* 145 */     return -1;
/*     */   }
/*     */ 
/*     */   public static boolean deleteFile(String filepath)
/*     */   {
/* 157 */     File file = new File(filepath);
/* 158 */     if (file.exists()) {
/* 159 */       return file.delete();
/*     */     }
/* 161 */     return true;
/*     */   }
/*     */ 
/*     */   public static boolean copyFile(String src, String dst)
/*     */   {
/* 176 */     boolean result = false;
/*     */     try {
/* 178 */       File in = new File(src);
/* 179 */       File out = new File(dst);
/* 180 */       FileInputStream inFile = new FileInputStream(in);
/* 181 */       FileOutputStream outFile = new FileOutputStream(out);
/*     */ 
/* 183 */       int i = 0;
/* 184 */       byte[] buffer = new byte[1024];
/* 185 */       while ((i = inFile.read(buffer)) != -1) {
/* 186 */         outFile.write(buffer, 0, i);
/*     */       }
/*     */ 
/* 189 */       buffer = (byte[])null;
/* 190 */       inFile.close();
/* 191 */       outFile.close();
/* 192 */       result = true;
/*     */     } catch (IOException localIOException) {
/*     */     }
/* 195 */     return result;
/*     */   }
/*     */ 
/*     */   public static String newImageName() {
/* 199 */     String uuidStr = UUID.randomUUID().toString();
/* 200 */     return uuidStr.replaceAll("-", "") + ".jpg";
/*     */   }
/*     */ 
/*     */   public static byte[] getBytesFromFile(String path) throws IOException {
/* 204 */     File file = new File(path);
/* 205 */     return getBytesFromFile(file);
/*     */   }
/*     */ 
/*     */   public static byte[] getBytesFromFile(File file) throws IOException {
/* 209 */     InputStream is = new FileInputStream(file);
/* 210 */     long length = file.length();
/* 211 */     if (length > 2147483647L) {
/* 212 */       is.close();
/* 213 */       throw new IOException("File is to large " + file.getName());
/*     */     }
/* 215 */     byte[] bytes = new byte[(int)length];
/*     */     try {
/* 217 */       int offset = 0;
/* 218 */       int numRead = 0;
/* 219 */       while ((offset < bytes.length) && 
/* 220 */         ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)) {
/* 221 */         offset += numRead;
/*     */       }
/* 223 */       if (offset < bytes.length) {
/* 224 */         throw new IOException("Could not completely read file " + 
/* 225 */           file.getName());
/*     */       }
/* 227 */       is.close();
/* 228 */       return bytes;
/*     */     } catch (Exception e) {
/* 230 */       return null;
/*     */     } finally {
/* 232 */       bytes = (byte[])null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getStrFromFile(File file) throws IOException {
/* 237 */     InputStream is = new FileInputStream(file);
/*     */ 
/* 239 */     long length = file.length();
/* 240 */     if (length > 2147483647L) {
/* 241 */       is.close();
/*     */ 
/* 243 */       throw new IOException("File is to large " + file.getName());
/*     */     }
/* 245 */     StringBuffer sb = new StringBuffer();
/* 246 */     BufferedReader br = new BufferedReader(new InputStreamReader(is, "GBK"));
/* 247 */     String data = "";
/* 248 */     while ((data = br.readLine()) != null) {
/* 249 */       sb.append(data);
/* 250 */       sb.append("\n");
/*     */     }
/* 252 */     String result = sb.toString();
/* 253 */     is.close();
/* 254 */     return result;
/*     */   }
/*     */ 
/*     */   public static void generateOtherImg(String imagePath)
/*     */   {
/*     */   }
/*     */ }