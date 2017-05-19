/*     */ package com.yunmai.android.engine;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.res.AssetManager;
/*     */ import android.graphics.Rect;
/*     */ import android.os.Build;
/*     */ import com.ym.idcard.reg.NativeOcr;
/*     */ import com.yunmai.android.other.FileUtil;
/*     */ import com.yunmai.android.other.StringUtil;
/*     */ import com.yunmai.android.vo.IDCard;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public class OcrEngine
/*     */ {
/*     */   private static final int OCR_LAN_NIL = 0;
/*     */   private static final int OCR_LAN_ENGLISH = 1;
/*     */   private static final int OCR_LAN_CHINESE_SIMPLE = 2;
/*     */   private static final int OCR_LAN_CHINESE_TRADITIONAL = 21;
/*     */   private static final int OCR_LAN_EUROPEAN = 3;
/*     */   private static final int OCR_LAN_RUSSIAN = 4;
/*     */   private static final int OCR_LAN_JAPAN = 6;
/*     */   private static final int OCR_LAN_CENTEURO = 7;
/*     */   private static final int OCR_CODE_NIL = 0;
/*     */   private static final int OCR_CODE_GB = 1;
/*     */   private static final int OCR_CODE_B5 = 2;
/*     */   private static final int OCR_CODE_GB2B5 = 3;
/*     */   public static final int RECOG_FAIL = -2;
/*     */   public static final int RECOG_CANCEL = -1;
/*     */   public static final int RECOG_NONE = 0;
/*     */   public static final int RECOG_OK = 1;
/*     */   public static final int RECOG_SMALL = 2;
/*     */   public static final int RECOG_BLUR = 3;
/*     */   public static final int RECOG_LANGUAGE = 4;
/*     */   public static final int RECOG_BLUR_TIP = 5;
/*     */   private static final int MIN_WIDTH_LIMIT = 1024;
/*     */   private static final int MIN_HEIGHT_LIMIT = 720;
/*     */   private static final int BIDC_NON = 0;
/*     */   private static final int BIDC_NAME = 1;
/*     */   private static final int BIDC_CARDNO = 3;
/*     */   private static final int BIDC_SEX = 4;
/*     */   private static final int BIDC_FOLK = 11;
/*     */   private static final int BIDC_BIRTHDAY = 5;
/*     */   private static final int BIDC_ADDRESS = 6;
/*     */   private static final int BIDC_ISSUE_AUTHORITY = 7;
/*     */   private static final int BIDC_VALID_PERIOD = 9;
/*     */   private static final int BIDC_MEMO = 99;
/*  79 */   protected long[] ppEngine = null;
/*  80 */   protected long[] ppImage = null;
/*  81 */   protected long[] ppField = null;
/*  82 */   protected long pEngine = 0L;
/*  83 */   protected long pImage = 0L;
/*  84 */   protected long pField = 0L;
/*  85 */   protected NativeOcr mNativeOcr = null;
/*  86 */   protected boolean mBeCancel = false;
/*     */ 
/*  88 */   private static boolean OPT_CANCEL = false;
/*     */ 
/*     */   public OcrEngine()
/*     */   {
/*  94 */     this.ppEngine = new long[1];
/*  95 */     this.ppImage = new long[1];
/*  96 */     this.ppField = new long[1];
/*  97 */     this.mNativeOcr = new NativeOcr();
/*     */   }
/*     */ 
/*     */   public void finalize()
/*     */   {
/* 102 */     this.ppEngine = null;
/* 103 */     this.ppImage = null;
/* 104 */     this.ppField = null;
/* 105 */     this.mNativeOcr = null;
/*     */ 
/* 107 */     this.pEngine = 0L;
/* 108 */     this.pImage = 0L;
/*     */   }
/*     */ 
/*     */   private boolean startBCR(String cfgPath, String dataPath, int lanuage, byte[] license)
/*     */   {
/* 115 */     boolean result = false;
/* 116 */     int ret = this.mNativeOcr.startBCR(this.ppEngine, 
/* 117 */       StringUtil.convertUnicodeToAscii(dataPath), 
/* 118 */       StringUtil.convertUnicodeToAscii(cfgPath), lanuage, license);
/* 119 */     if (ret == 1) {
/* 120 */       this.pEngine = this.ppEngine[0];
/* 121 */       result = true;
/*     */     }
/* 123 */     return result;
/*     */   }
/*     */ 
/*     */   private void closeBCR() {
/* 127 */     if ((this.ppEngine != null) && (this.mNativeOcr != null)) {
/* 128 */       this.mNativeOcr.closeBCR(this.ppEngine);
/* 129 */       this.ppEngine[0] = 0L;
/* 130 */       this.pEngine = 0L;
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean loadImageMem(long pBuffer, int width, int height, int component)
/*     */   {
/* 136 */     boolean result = false;
/*     */ 
/* 138 */     if (pBuffer != 0L) {
/* 139 */       this.pImage = this.mNativeOcr.loadImageMem(this.pEngine, pBuffer, width, height, 
/* 140 */         component);
/* 141 */       if (this.pImage != 0L) {
/* 142 */         this.ppImage[0] = this.pImage;
/* 143 */         result = true;
/*     */       }
/*     */     }
/*     */ 
/* 147 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean doImageBCR() {
/* 151 */     boolean result = false;
/* 152 */     this.mBeCancel = false;
/* 153 */     this.mNativeOcr.setoption(this.pEngine, StringUtil.convertToUnicode("-fmt"), null);
/* 154 */     int ret = this.mNativeOcr.doImageBCR(this.pEngine, this.pImage, this.ppField);
/* 155 */     if (ret == 1) {
/* 156 */       this.pField = this.ppField[0];
/* 157 */       result = true;
/* 158 */     } else if (ret == 3) {
/* 159 */       this.mBeCancel = true;
/*     */     }
/* 161 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean isCancel() {
/* 165 */     return this.mBeCancel;
/*     */   }
/*     */ 
/*     */   private void freeImage() {
/* 169 */     if (this.mNativeOcr != null) {
/* 170 */       this.mNativeOcr.freeImage(this.pEngine, this.ppImage);
/* 171 */       this.ppImage[0] = 0L;
/* 172 */       this.pImage = 0L;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void freeBFields() {
/* 177 */     if (this.mNativeOcr != null) {
/* 178 */       this.mNativeOcr.freeBField(this.pEngine, this.ppField[0], 0);
/* 179 */       this.ppField[0] = 0L;
/* 180 */       this.pField = 0L;
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isBlurImage() {
/* 185 */     boolean result = false;
/* 186 */     if (this.mNativeOcr != null) {
/* 187 */       int ret = this.mNativeOcr.imageChecking(this.pEngine, this.pImage, 0);
/* 188 */       if (ret == 2) {
/* 189 */         result = true;
/*     */       }
/*     */     }
/* 192 */     return result;
/*     */   }
/*     */ 
/*     */   private int getFieldId() {
/* 196 */     long field = this.pField;
/* 197 */     return this.mNativeOcr.getFieldId(field);
/*     */   }
/*     */ 
/*     */   private String getFieldText(int keyLanguage) {
/* 201 */     long field = this.pField;
/* 202 */     int textsize = 256;
/* 203 */     byte[] text = new byte[256];
/* 204 */     this.mNativeOcr.getFieldText(field, text, 256);
/*     */ 
/* 206 */     int ocrLang = 2;
/* 207 */     if ((keyLanguage == 3) && (ocrLang == 2)) {
/* 208 */       this.mNativeOcr.codeConvert(this.pEngine, text, keyLanguage);
/* 209 */       return StringUtil.convertBig5ToUnicode(text);
/* 210 */     }if (ocrLang == 3) {
/* 211 */       return StringUtil.convertAscIIToUnicode(text);
/*     */     }
/* 213 */     return StringUtil.convertGbkToUnicode(text);
/*     */   }
/*     */ 
/*     */   private Rect getFieldRect()
/*     */   {
/* 222 */     Rect rect = new Rect();
/* 223 */     long field = this.pField;
/* 224 */     int[] point = new int[4];
/* 225 */     this.mNativeOcr.getFieldRect(field, point);
/* 226 */     rect.left = point[0];
/* 227 */     rect.top = point[1];
/* 228 */     rect.right = point[2];
/* 229 */     rect.bottom = point[3];
/* 230 */     return rect;
/*     */   }
/*     */ 
/*     */   private void getNextField() {
/* 234 */     if (!isFieldEnd())
/* 235 */       this.pField = this.mNativeOcr.getNextField(this.pField);
/*     */   }
/*     */ 
/*     */   private boolean isFieldEnd()
/*     */   {
/* 241 */     return this.pField == 0L;
/*     */   }
/*     */ 
/*     */   private void setProgressFunc(boolean install)
/*     */   {
/* 246 */     if ((this.pEngine != 0L) && (this.mNativeOcr != null))
/* 247 */       this.mNativeOcr.setProgressFunc(this.pEngine, install);
/*     */   }
/*     */ 
/*     */   private boolean fields2Object(IDCard idCard, int keyLanguage)
/*     */   {
/* 260 */     if (idCard != null) {
/* 261 */       while (!isFieldEnd()) {
/* 262 */         switch (getFieldId())
/*     */         {
/*     */         case 1:
/* 264 */           idCard.setName(getFieldText(keyLanguage));
/* 265 */           break;
/*     */         case 3:
/* 267 */           idCard.setCardNo(getFieldText(keyLanguage));
/* 268 */           break;
/*     */         case 4:
/* 270 */           idCard.setSex(getFieldText(keyLanguage));
/* 271 */           break;
/*     */         case 11:
/* 273 */           idCard.setEthnicity(getFieldText(keyLanguage));
/* 274 */           break;
/*     */         case 5:
/* 276 */           idCard.setBirth(getFieldText(keyLanguage));
/* 277 */           break;
/*     */         case 6:
/* 279 */           idCard.setAddress(getFieldText(keyLanguage));
/* 280 */           break;
/*     */         case 7:
/* 282 */           idCard.setAuthority(getFieldText(keyLanguage));
/* 283 */           break;
/*     */         case 9:
/* 285 */           idCard.setPeriod(getFieldText(keyLanguage));
/* 286 */           break;
/*     */         case 99:
/* 288 */           idCard.setMemo(getFieldText(keyLanguage));
/*     */         }
/*     */ 
/* 291 */         getNextField();
/*     */       }
/*     */ 
/* 294 */       return true;
/*     */     }
/* 296 */     return false;
/*     */   }
/*     */ 
/*     */   public static void doCancel()
/*     */   {
/* 304 */     OPT_CANCEL = true;
/*     */   }
/*     */ 
/*     */   public IDCard recognize(Context context, String imagePath)
/*     */     throws IOException
/*     */   {
/* 317 */     return recognize(context, 2, imagePath);
/*     */   }
/*     */ 
/*     */   public IDCard recognize(Context context, byte[] imgBuffer)
/*     */   {
/* 328 */     return recognize(context, 2, imgBuffer, null);
/*     */   }
/*     */ 
/*     */   public IDCard recognize(Context context, byte[] idCardDataA, byte[] idCardDataB)
/*     */   {
/* 340 */     return recognize(context, 2, idCardDataA, idCardDataB);
/*     */   }
/*     */ 
/*     */   private IDCard recognize(Context context, int ocrLanguage, String imagePath)
/*     */     throws IOException
/*     */   {
/* 353 */     File file = new File(imagePath);
/* 354 */     byte[] imgBuffer = FileUtil.getBytesFromFile(file);
/* 355 */     return recognize(context, ocrLanguage, imgBuffer, null);
/*     */   }
/*     */ 
/*     */   private IDCard recognize(Context context, int ocrLanguage, byte[] idCardDataA, byte[] idCardDataB)
/*     */   {
/* 368 */     IDCard idCard = new IDCard();
/* 369 */     int keyLanguage = 1;
/* 370 */     boolean blurDetection = false;
/* 371 */     if (ocrLanguage == 21) {
/* 372 */       keyLanguage = 3;
/*     */     }
/*     */ 
/* 375 */     OPT_CANCEL = false;
/*     */ 
/* 377 */     byte[] license = new byte[256];
/* 378 */     AssetManager assetManager = context.getAssets();
/*     */     try {
/* 380 */       InputStream is = assetManager.open("license.info");
/* 381 */       is.read(license);
/* 382 */       is.close();
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/* 387 */     if (startBCR("", "", ocrLanguage, license)) {
/* 388 */       if (idCardDataA != null) {
/* 389 */         idCard = recognizing(idCardDataA, blurDetection, keyLanguage);
/*     */       }
/* 391 */       if (idCardDataB != null) {
/* 392 */         IDCard idCardB = recognizing(idCardDataB, blurDetection, keyLanguage);
/* 393 */         idCard.setAuthority(idCardB.getAuthority());
/* 394 */         idCard.setPeriod(idCardB.getPeriod());
/*     */       }
/*     */ 
/* 397 */       closeBCR();
/*     */     }
/* 399 */     return idCard;
/*     */   }
/*     */ 
/*     */   private IDCard recognizing(byte[] imgBuffer, boolean blurDetection, int keyLanguage)
/*     */   {
/* 411 */     IDCard idCard = new IDCard();
/*     */ 
/* 413 */     ImageEngine imageEngine = new ImageEngine();
/* 414 */     if ((imageEngine.init(1, 90)) && 
/* 415 */       (imageEngine.load(imgBuffer)))
/*     */     {
/* 417 */       int width = imageEngine.getWidth();
/* 418 */       int height = imageEngine.getHeight();
/* 419 */       int component = imageEngine.getComponent();
/* 420 */       if ((!Build.MODEL.contains("SO-01")) && 
/* 421 */         (!Build.MODEL.contains("X10")) && 
/* 422 */         (!Build.MODEL.contains("E10")) && ((
/* 425 */         (width < 1024) || (height < 720)))) {
/* 426 */         imageEngine.finalize();
/* 427 */         imageEngine = null;
/* 428 */         closeBCR();
/* 429 */         idCard.setRecogStatus(2);
/* 430 */         return idCard;
/*     */       }
/*     */ 
/* 433 */       long pBuffer = imageEngine.getDataEx();
/* 434 */       boolean loadImageMem = loadImageMem(pBuffer, width, height, 
/* 435 */         component);
/*     */ 
/* 438 */       imageEngine.finalize();
/* 439 */       imageEngine = null;
/*     */ 
/* 441 */       if (loadImageMem)
/*     */       {
/* 444 */         setProgressFunc(true);
/*     */ 
/* 446 */         if ((blurDetection) && 
/* 448 */           (isBlurImage()) && (!OPT_CANCEL)) {
/* 449 */           freeImage();
/* 450 */           idCard.setRecogStatus(3);
/* 451 */           return idCard;
/*     */         }
/*     */ 
/* 456 */         if (doImageBCR()) {
/* 457 */           if (fields2Object(idCard, keyLanguage)) {
/* 458 */             idCard.setRecogStatus(1);
/*     */           }
/*     */ 
/* 461 */           freeBFields();
/* 462 */         } else if (isCancel()) {
/* 463 */           idCard.setRecogStatus(-1);
/*     */ 
/* 465 */           freeBFields();
/*     */         }
/*     */ 
/* 469 */         freeImage();
/*     */       }
/*     */     }
/*     */ 
/* 473 */     return idCard;
/*     */   }
/*     */ }
