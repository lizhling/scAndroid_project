/*     */ package com.yunmai.android.engine;
/*     */ 
/*     */ import com.ym.idcard.reg.NativeImage;
/*     */ 
/*     */ public class ImageEngine
/*     */ {
/*     */   public static final int IMG_FMT_UNK = 0;
/*     */   public static final int IMG_FMT_BMP = 1;
/*     */   public static final int IMG_FMT_JPG = 2;
/*     */   public static final int IMG_COMPONENT_GRAY = 1;
/*     */   public static final int IMG_COMPONENT_RGB = 3;
/*     */   public static final int RET_OK = 1;
/*     */   public static final int RET_ERR_UNK = 0;
/*     */   public static final int RET_ERR_PTR = -1;
/*     */   public static final int RET_ERR_ARG = -2;
/*     */   public static final int RET_ERR_MEM = -3;
/*  35 */   protected long mEngine = 0L;
/*  36 */   protected NativeImage mNativeImage = null;
/*     */ 
/*     */   public ImageEngine()
/*     */   {
/*  42 */     this.mNativeImage = new NativeImage();
/*  43 */     this.mEngine = this.mNativeImage.createEngine();
/*     */   }
/*     */ 
/*     */   public void finalize() {
/*  47 */     if ((this.mNativeImage != null) && (this.mEngine != 0L)) {
/*  48 */       this.mNativeImage.freeImage(this.mEngine);
/*  49 */       this.mNativeImage.closeEngine(this.mEngine);
/*  50 */       this.mEngine = 0L;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean init(int components, int quality)
/*     */   {
/*  58 */     if (this.mNativeImage != null) {
/*  59 */       int ret = this.mNativeImage.initImage(this.mEngine, components, quality);
/*  60 */       if (ret == 1)
/*  61 */         return true;
/*     */     }
/*  63 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean load(byte[] imgbuffer) {
/*  67 */     if (this.mNativeImage != null) {
/*  68 */       int ret = this.mNativeImage.loadmemjpg(this.mEngine, imgbuffer, 
/*  69 */         imgbuffer.length);
/*  70 */       if (ret == 1) {
/*  71 */         return true;
/*     */       }
/*     */     }
/*  74 */     return false;
/*     */   }
/*     */ 
/*     */   public long getDataEx() {
/*  78 */     if (this.mNativeImage != null) {
/*  79 */       return this.mNativeImage.getImageDataEx(this.mEngine);
/*     */     }
/*  81 */     return 0L;
/*     */   }
/*     */ 
/*     */   public int getWidth() {
/*  85 */     if (this.mNativeImage != null) {
/*  86 */       return this.mNativeImage.getImageWidth(this.mEngine);
/*     */     }
/*  88 */     return 0;
/*     */   }
/*     */ 
/*     */   public int getHeight() {
/*  92 */     if (this.mNativeImage != null) {
/*  93 */       return this.mNativeImage.getImageHeight(this.mEngine);
/*     */     }
/*  95 */     return 0;
/*     */   }
/*     */ 
/*     */   public int getComponent() {
/*  99 */     if (this.mNativeImage != null) {
/* 100 */       return this.mNativeImage.getImageComponent(this.mEngine);
/*     */     }
/* 102 */     return 0;
/*     */   }
/*     */ }