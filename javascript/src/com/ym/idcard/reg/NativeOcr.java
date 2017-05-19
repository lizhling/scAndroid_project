/*    */ package com.ym.idcard.reg;
/*    */ 
/*    */ import android.util.Log;
/*    */ 
/*    */ public class NativeOcr
/*    */ {
/*    */   private static final String TAG = "NativeOcr";
/*    */   private static final String LIB = "IDCardengine";
/*    */   private static int mProgress;
/*    */   private static boolean mCancel;
/*    */ 
/*    */   static
/*    */   {
/*    */     try
/*    */     {
/* 28 */       System.loadLibrary("IDCardengine");
/*    */     } catch (Exception e) {
/* 30 */       Log.e("ocrengine", "", e);
/*    */     }
/*    */ 
/* 34 */     mProgress = 0;
/* 35 */     mCancel = false;
/*    */   }
/*    */ 
/*    */   public void finalize()
/*    */   {
/*    */   }
/*    */ 
/*    */   public static int getProgress()
/*    */   {
/* 50 */     return mProgress;
/*    */   }
/*    */ 
/*    */   public static int Progress(int progress, int relative)
/*    */   {
/* 60 */     if (relative != 0)
/* 61 */       mProgress += progress;
/*    */     else {
/* 63 */       mProgress = progress;
/*    */     }
/*    */ 
/* 66 */     return mProgress;
/*    */   }
/*    */ 
/*    */   public native int startBCR(long[] paramArrayOfLong, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt, byte[] paramArrayOfByte3);
/*    */ 
/*    */   public native int closeBCR(long[] paramArrayOfLong);
/*    */ 
/*    */   public native int doImageBCR(long paramLong1, long paramLong2, long[] paramArrayOfLong);
/*    */ 
/*    */   public native int freeImage(long paramLong, long[] paramArrayOfLong);
/*    */ 
/*    */   public native int imageChecking(long paramLong1, long paramLong2, int paramInt);
/*    */ 
/*    */   public native void freeBField(long paramLong1, long paramLong2, int paramInt);
/*    */ 
/*    */   public native void setProgressFunc(long paramLong, boolean paramBoolean);
/*    */ 
/*    */   public native long loadImageMem(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3);
/*    */ 
/*    */   public native int getFieldId(long paramLong);
/*    */ 
/*    */   public native int getFieldText(long paramLong, byte[] paramArrayOfByte, int paramInt);
/*    */ 
/*    */   public native long getNextField(long paramLong);
/*    */ 
/*    */   public native int getFieldRect(long paramLong, int[] paramArrayOfInt);
/*    */ 
/*    */   public native int codeConvert(long paramLong, byte[] paramArrayOfByte, int paramInt);
/*    */ 
/*    */   public native int setoption(long paramLong, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*    */ }
