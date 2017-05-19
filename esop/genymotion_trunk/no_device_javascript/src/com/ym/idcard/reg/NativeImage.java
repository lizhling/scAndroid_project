/*    */ package com.ym.idcard.reg;
/*    */ 
/*    */ import android.util.Log;
/*    */ 
/*    */ public class NativeImage
/*    */ {
/*    */   private static final String TAG = "NativeImage";
/*    */   private static final String LIB = "imageengine";
/*    */ 
/*    */   static
/*    */   {
/*    */     try
/*    */     {
/* 30 */       System.loadLibrary("imageengine");
/*    */     } catch (Exception e) {
/* 32 */       Log.e("imageengine", "", e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void finalize()
/*    */   {
/*    */   }
/*    */ 
/*    */   public native int initImage(long paramLong, int paramInt1, int paramInt2);
/*    */ 
/*    */   public native int loadmemjpg(long paramLong, byte[] paramArrayOfByte, int paramInt);
/*    */ 
/*    */   public native int freeImage(long paramLong);
/*    */ 
/*    */   public native long createEngine();
/*    */ 
/*    */   public native int closeEngine(long paramLong);
/*    */ 
/*    */   public native long getImageDataEx(long paramLong);
/*    */ 
/*    */   public native int getImageWidth(long paramLong);
/*    */ 
/*    */   public native int getImageHeight(long paramLong);
/*    */ 
/*    */   public native int getImageComponent(long paramLong);
/*    */ }