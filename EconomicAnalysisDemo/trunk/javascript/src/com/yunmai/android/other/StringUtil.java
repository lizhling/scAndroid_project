/*     */ package com.yunmai.android.other;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ public class StringUtil
/*     */ {
/*     */   public void finalize()
/*     */   {
/*     */   }
/*     */ 
/*     */   public static String convertGbkToUnicode(byte[] array)
/*     */   {
/*  50 */     String str = null;
/*     */     try
/*     */     {
/*  54 */       byte[] text = filterAndCut(array);
/*  55 */       if (text != null)
/*  56 */         str = new String(text, "GBK");
/*     */       else
/*  58 */         str = "";
/*     */     }
/*     */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*     */     }
/*  62 */     return str.trim();
/*     */   }
/*     */ 
/*     */   public static String convertAscIIToUnicode(byte[] array) {
/*  66 */     String str = null;
/*     */     try
/*     */     {
/*  70 */       byte[] text = filterAndCut(array);
/*  71 */       if (text != null)
/*  72 */         str = new String(text, "ISO-8859-1");
/*     */       else
/*  74 */         str = "";
/*     */     }
/*     */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*     */     }
/*  78 */     return str.trim();
/*     */   }
/*     */ 
/*     */   public static byte[] filter(byte[] array)
/*     */   {
/*  91 */     int len = array.length;
/*  92 */     byte[] filter = new byte[len];
/*  93 */     int i = 0; for (int cnt = 0; i < len; ++i) {
/*  94 */       if (array[i] == 13)
/*     */         continue;
/*  96 */       filter[(cnt++)] = array[i];
/*     */     }
/*  98 */     return filter;
/*     */   }
/*     */ 
/*     */   public static byte[] filterAndCut(byte[] array)
/*     */   {
/* 115 */     int len = strlen(array);
/* 116 */     if (len < 1)
/* 117 */       return null;
/* 118 */     byte[] filter = new byte[len];
/* 119 */     int i = 0; for (int cnt = 0; i < len; ++i) {
/* 120 */       if (array[i] == 13)
/*     */         continue;
/* 122 */       filter[(cnt++)] = array[i];
/*     */     }
/* 124 */     return filter;
/*     */   }
/*     */ 
/*     */   public static int strlen(byte[] array)
/*     */   {
/* 136 */     int len = -1;
/* 137 */     if (array != null) {
/* 138 */       if (array.length == 0)
/* 139 */         len = 0;
/*     */       else {
/* 141 */         for (int i = 0; i < array.length; ++i) {
/* 142 */           if (array[i] == 0) {
/* 143 */             len = i;
/* 144 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 149 */     return len;
/*     */   }
/*     */ 
/*     */   public static byte[] convertUnicodeToAscii(String str)
/*     */   {
/* 161 */     byte[] result = (byte[])null;
/*     */     try
/*     */     {
/* 164 */       int cnt = str.length();
/* 165 */       byte[] res = str.getBytes("US-ASCII");
/* 166 */       result = new byte[cnt + 1];
/*     */ 
/* 168 */       for (int i = 0; i < cnt; ++i) {
/* 169 */         result[i] = res[i];
/*     */       }
/* 171 */       result[cnt] = 0;
/*     */     } catch (UnsupportedEncodingException e) {
/* 173 */       result = (byte[])null;
/*     */     }
/*     */ 
/* 176 */     return result;
/*     */   }
/*     */ 
/*     */   public static String convertBig5ToUnicode(byte[] array) {
/* 180 */     String str = null;
/*     */     try
/*     */     {
/* 183 */       byte[] text = filterAndCut(array);
/* 184 */       if (text != null)
/* 185 */         str = new String(text, "big5");
/*     */       else
/* 187 */         str = "";
/*     */     }
/*     */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*     */     }
/* 191 */     return str.trim();
/*     */   }
/*     */ 
/*     */   public static byte[] convertUnicodeToGbk(String str)
/*     */   {
/* 203 */     byte[] result = (byte[])null;
/*     */     try
/*     */     {
/* 206 */       byte[] res = str.getBytes("GBK");
/* 207 */       int cnt = res.length;
/* 208 */       result = new byte[cnt + 1];
/*     */ 
/* 210 */       for (int i = 0; i < cnt; ++i) {
/* 211 */         result[i] = res[i];
/*     */       }
/* 213 */       result[cnt] = 0;
/*     */     } catch (UnsupportedEncodingException e) {
/* 215 */       result = (byte[])null;
/*     */     }
/*     */ 
/* 218 */     return result;
/*     */   }
/*     */ 
/*     */   public static byte[] convertToUnicode(String str) {
/* 222 */     byte[] result = (byte[])null;
/*     */     try
/*     */     {
/* 225 */       byte[] res = str.getBytes("utf-8");
/* 226 */       int cnt = res.length;
/* 227 */       result = new byte[cnt + 1];
/*     */ 
/* 229 */       for (int i = 0; i < cnt; ++i) {
/* 230 */         result[i] = res[i];
/*     */       }
/* 232 */       result[cnt] = 0;
/*     */     } catch (UnsupportedEncodingException e) {
/* 234 */       result = (byte[])null;
/*     */     }
/*     */ 
/* 237 */     return result;
/*     */   }
/*     */ }