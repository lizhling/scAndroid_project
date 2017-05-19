package com.sunrise.businesstransaction.service.vo;

public class ShenFenZheng2Data {
    
    // ---------------------文字域
    /** 姓名 */
    private String name = null;
    /** 性别 */
    private String sex = null;
    /** 民族 */
    private String minzu = null;
    /** 出生日期 */
    private String birthdate = null;
    /** 地址 */
    private String address = null;
    /** 身份证号 */
    private String shenfenzhengID = null;
    /** 发证机构 */
    private String fazhengjigou = null;
    /** 有效期 */
    private String validdate = null;
    /** 保留 */
    private String others = null;
    /** 身份证图片路径*/
    private String idCardImagePath=null;
    
    // ---------------------图片域
    private static byte[] photoBytes;
    
    
    
    public static byte[] getPhotoBytes() {
		return photoBytes;
	}
	public void setPhotoBytes(byte[] photoBytes) {
		this.photoBytes = photoBytes;
	}
	public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getMinzu() {
        return minzu;
    }
    public void setMinzu(String minzu) {
        this.minzu = minzu;
    }
    public String getBirthdate() {
        return birthdate;
    }
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getShenfenzhengID() {
        return shenfenzhengID;
    }
    public void setShenfenzhengID(String shenfenzhengID) {
        this.shenfenzhengID = shenfenzhengID;
    }
    public String getFazhengjigou() {
        return fazhengjigou;
    }
    public void setFazhengjigou(String fazhengjigou) {
        this.fazhengjigou = fazhengjigou;
    }
    public String getValiddate() {
        return validdate;
    }
    public void setValiddate(String validdate) {
        this.validdate = validdate;
    }
    public String getOthers() {
        return others;
    }
    public void setOthers(String others) {
        this.others = others;
    }
    
    
    public String getIdCardImagePath() {
		return idCardImagePath;
	}
	public void setIdCardImagePath(String idCardImagePath) {
		this.idCardImagePath = idCardImagePath;
	}
	public static ShenFenZheng2Data convert(byte[] data) {
        return convert(data, true);
    }
    
    public static ShenFenZheng2Data convert(byte[] data, boolean hasHeader) {
        try {
            int idx = 0;
            if (hasHeader) {
                final byte[] header = new byte[] {
                        (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, 
                        (byte) 0x96, (byte) 0x69, (byte) 0x05,
                        (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x90
                };
                
                // 检查数据头是否正确
                for (int i = 0; i < header.length; i++) {
                    if (data[i] != header[i]) {
                        // TODO 错误数据时需要的处理
                        return null;
                    }
                }
                
                idx = header.length;
            }
            
            idx += 4;
            
            byte wenziLen1 = data[idx++];
            byte wenziLen2 = data[idx++];
            int wenziLen = wenziLen1 + wenziLen2 * 256;
            
            byte picLen1 =  data[idx++];
            byte picLen2 =  data[idx++];
            int picLen = picLen1 + picLen2 * 256;
            
            byte[] wenziBytes = new byte[wenziLen];
            for (int i = 0; i < wenziBytes.length; i++) {
                wenziBytes[i] = data[idx++];
            }
            
            ShenFenZheng2Data result = new ShenFenZheng2Data();
            result.setName(new String(getUtf16Bytes(wenziBytes, 0, 30), "utf-16"));                 // 姓名
            result.setSex(new String(getUtf16Bytes(wenziBytes, 30, 2), "utf-16"));                  // 性别
            result.setMinzu(new String(getUtf16Bytes(wenziBytes, 32, 4), "utf-16"));                // 民族
            result.setBirthdate(new String(getUtf16Bytes(wenziBytes, 36, 16), "utf-16"));           // 出生日期
            result.setAddress(new String(getUtf16Bytes(wenziBytes, 52, 70), "utf-16"));             // 地址
            result.setShenfenzhengID(new String(getUtf16Bytes(wenziBytes, 122, 36), "utf-16"));     // 身份证号
            result.setFazhengjigou(new String(getUtf16Bytes(wenziBytes, 158, 30), "utf-16"));       // 发证机构
            result.setValiddate(new String(getUtf16Bytes(wenziBytes, 188, 32), "utf-16"));          // 有效期
            
            result.photoBytes = new byte[picLen];
            for (int i = 0; i < result.photoBytes.length; i++) {
            	result.photoBytes[i] = data[idx++];
            }
//            processPhoto();
            return result;
        } catch (Exception ex) {
            // TODO 异常的处理
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * 处理相片信息
     */
    private static void processPhoto() {
		byte[] wltdata = getPhotoBytes();
		
		byte[] licdata = new byte[]{
		(byte)0x05,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0xE3,(byte)0xB4,(byte)0x32,(byte)0x01,(byte)0x8B,(byte)0x21,(byte)0x0B,(byte)0x00};
		
    }
    
    private static byte[] getUtf16Bytes(byte[] bytes, int offset, int lenth) {
        byte[] tmpResult = new byte[lenth];
        int idx = 0;
        for (int i = offset; i < offset + lenth; i++) {
            tmpResult[idx++] = bytes[i];
        }
        
        byte[] result = new byte[lenth];
        int ridx = 0;
        idx = 0;
        while (true) {
            byte b1 = tmpResult[idx];
            byte b2 = tmpResult[idx + 1];
            
            result[ridx++] = b2;
            result[ridx++] = b1;
            
            idx += 2;
            if (idx >= tmpResult.length) break;
        }
        
        return result;
    }
    
}
