/*     */ package com.yunmai.android.vo;
/*     */ 
/*     */ public class IDCard
/*     */ {
/*  25 */   private int recogStatus = -2;
/*     */   private String ymName;
/*     */   private String ymCardNo;
/*     */   private String ymSex;
/*     */   private String ymEthnicity;
/*     */   private String ymBirth;
/*     */   private String ymAddress;
/*     */   private String ymAuthority;
/*     */   private String ymPeriod;
/*     */   private String ymMemo;
/*     */ 
/*     */   public int getRecogStatus()
/*     */   {
/*  42 */     return this.recogStatus;
/*     */   }
/*     */ 
/*     */   public void setRecogStatus(int recogStatus) {
/*  46 */     this.recogStatus = recogStatus;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  53 */     return this.ymName;
/*     */   }
/*     */ 
/*     */   public void setName(String name) {
/*  57 */     this.ymName = name;
/*     */   }
/*     */ 
/*     */   public String getCardNo()
/*     */   {
/*  64 */     return this.ymCardNo;
/*     */   }
/*     */ 
/*     */   public void setCardNo(String cardNo) {
/*  68 */     this.ymCardNo = cardNo;
/*     */   }
/*     */ 
/*     */   public String getSex()
/*     */   {
/*  75 */     return this.ymSex;
/*     */   }
/*     */ 
/*     */   public void setSex(String sex) {
/*  79 */     this.ymSex = sex;
/*     */   }
/*     */ 
/*     */   public String getEthnicity()
/*     */   {
/*  86 */     return this.ymEthnicity;
/*     */   }
/*     */ 
/*     */   public void setEthnicity(String ethnicity) {
/*  90 */     this.ymEthnicity = ethnicity;
/*     */   }
/*     */ 
/*     */   public String getBirth()
/*     */   {
/*  97 */     return this.ymBirth;
/*     */   }
/*     */ 
/*     */   public void setBirth(String birth) {
/* 101 */     this.ymBirth = birth;
/*     */   }
/*     */ 
/*     */   public String getAddress()
/*     */   {
/* 108 */     return this.ymAddress;
/*     */   }
/*     */ 
/*     */   public void setAddress(String address) {
/* 112 */     this.ymAddress = address;
/*     */   }
/*     */ 
/*     */   public String getAuthority()
/*     */   {
/* 119 */     return this.ymAuthority;
/*     */   }
/*     */ 
/*     */   public void setAuthority(String authority) {
/* 123 */     this.ymAuthority = authority;
/*     */   }
/*     */ 
/*     */   public String getPeriod()
/*     */   {
/* 130 */     return this.ymPeriod;
/*     */   }
/*     */ 
/*     */   public void setPeriod(String period) {
/* 134 */     this.ymPeriod = period;
/*     */   }
/*     */ 
/*     */   public String getMemo()
/*     */   {
/* 141 */     return this.ymMemo;
/*     */   }
/*     */ 
/*     */   public void setMemo(String memo) {
/* 145 */     this.ymMemo = memo;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 151 */     StringBuffer strBuf = new StringBuffer();
/* 152 */     strBuf.append("姓名：").append(this.ymName).append("\n");
/* 153 */     strBuf.append("身份号码：").append(this.ymCardNo).append("\n");
/* 154 */     strBuf.append("性别：").append(this.ymSex).append("\n");
/* 155 */     strBuf.append("民族：").append(this.ymEthnicity).append("\n");
/* 156 */     strBuf.append("出生：").append(this.ymBirth).append("\n");
/* 157 */     strBuf.append("住址：").append(this.ymAddress).append("\n");
/* 158 */     strBuf.append("签发机关：").append(this.ymAuthority).append("\n");
/* 159 */     strBuf.append("有效期限：").append(this.ymPeriod).append("\n");
/* 160 */     return strBuf.toString();
/*     */   }
/*     */ }