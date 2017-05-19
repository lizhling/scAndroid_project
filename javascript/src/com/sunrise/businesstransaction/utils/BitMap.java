package com.sunrise.businesstransaction.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BitMap {

    private BITMAPFILEHEADER bmfHeader;
    private BITMAPINFOHEADER bmiHeader;
    private RGBQUAD[] palettes;
    
    private int[] pixelDatas;
    
    public BitMap(String filename) {
        try {
            read(filename);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    public BitMap(int[] pixels, int width, int height, int biBitCount, boolean isAlpha8) {
        int[] pixelDatas = pixels;
        if (isAlpha8) {
            pixelDatas = new int[pixels.length];
            for (int i = 0; i < pixels.length; i++) {
                pixelDatas[i] = convertAlpha8Pixel(pixels[i]);
            }
        }
        
        if (null == pixelDatas || pixelDatas.length == 0) throw new RuntimeException("pixelDatas is empty.");
        if (width <= 0 || height <= 0) throw new RuntimeException("width|height is zero.");
        
        this.pixelDatas = pixelDatas;

        int bfOffBits;
        int biSizeImage;
        int lineBcnt;
        switch (biBitCount) {
        case 1:
            bfOffBits = 62;
            setPalettes1();
            lineBcnt = ((width + 7) / 8 + 3) / 4 * 4;
            biSizeImage = lineBcnt * height;
            break;
        case 4:
            bfOffBits = 118;
            setPalettes4();
            lineBcnt = ((width + 1) / 2 + 3) / 4 * 4;
            biSizeImage = lineBcnt * height;
            break;
        case 8:
            bfOffBits = 1078;
            setPalettes8();
            lineBcnt = (width + 3) / 4 * 4;
            biSizeImage = lineBcnt * height;
            break;
        case 24:
            bfOffBits = 54;
            lineBcnt = (width * 3 + 3) / 4 * 4;
            biSizeImage = lineBcnt * height;
            break;
        default:
            throw new RuntimeException("invalid biBitCount:" + biBitCount + ".");
        }
        
        bmfHeader = new BITMAPFILEHEADER();
        bmfHeader.bfType = 0x424d;
        bmfHeader.bfSize = bfOffBits + biSizeImage;
        bmfHeader.bfReserved1 = 0;
        bmfHeader.bfReserved2 = 0;
        bmfHeader.bfOffBits = bfOffBits;
        
        bmiHeader = new BITMAPINFOHEADER();
        bmiHeader.biSize = 40;
        bmiHeader.biWidth = width;
        bmiHeader.biHeight = height;
        bmiHeader.biPlanes = 1;
        bmiHeader.biBitCount = biBitCount;
        bmiHeader.biCompression = 0;
        bmiHeader.biSizeImage = biSizeImage;
        bmiHeader.biXPelsPerMeter = 0;
        bmiHeader.biYPelsPerMeter = 0;
        bmiHeader.biClrUsed = 0;
        bmiHeader.biClrImportant = 0;
    }
    
    public BitMap(int[] pixelDatas, int width, int height, int biBitCount) {
        this(pixelDatas, width, height, biBitCount, false);
    }
    
    private int convertAlpha8Pixel(int pixel) {
        int alpha = pixel >> 24 & 0xff;
        double touming = 1.0 - ((double) alpha) / 255;
        
        if (touming >= 0.1) {
            return 0xffffffff;
        } else {
            return 0xff000000;
        }
    }

    private void setPalettes1() {
        palettes = new RGBQUAD[2];
        palettes[0] = new RGBQUAD(0, 0, 0);
        palettes[1] = new RGBQUAD(255, 255, 255);
    }
    private void setPalettes4() {
        palettes = new RGBQUAD[16];
        int idx = 0;
        palettes[idx++] = new RGBQUAD(0, 0, 0);
        palettes[idx++] = new RGBQUAD(128, 0, 0);
        palettes[idx++] = new RGBQUAD(0, 128, 0);
        palettes[idx++] = new RGBQUAD(128, 128, 0);
        palettes[idx++] = new RGBQUAD(0, 0, 128);
        palettes[idx++] = new RGBQUAD(128, 0, 128);
        palettes[idx++] = new RGBQUAD(0, 128, 128);
        palettes[idx++] = new RGBQUAD(128, 128, 128);
        palettes[idx++] = new RGBQUAD(192, 192, 192);
        palettes[idx++] = new RGBQUAD(255, 0, 0);
        palettes[idx++] = new RGBQUAD(0, 255, 0);
        palettes[idx++] = new RGBQUAD(255, 255, 0);
        palettes[idx++] = new RGBQUAD(0, 0, 255);
        palettes[idx++] = new RGBQUAD(255, 0, 255);
        palettes[idx++] = new RGBQUAD(0, 255, 255);
        palettes[idx++] = new RGBQUAD(255, 255, 255);
    }
    private void setPalettes8() {
        palettes = new RGBQUAD[256];
        int idx = 0;
        palettes[idx++] = new RGBQUAD(0, 0, 0);
        palettes[idx++] = new RGBQUAD(128, 0, 0);
        palettes[idx++] = new RGBQUAD(0, 128, 0);
        palettes[idx++] = new RGBQUAD(128, 128, 0);
        palettes[idx++] = new RGBQUAD(0, 0, 128);
        palettes[idx++] = new RGBQUAD(128, 0, 128);
        palettes[idx++] = new RGBQUAD(0, 128, 128);
        palettes[idx++] = new RGBQUAD(192, 192, 192);
        palettes[idx++] = new RGBQUAD(192, 220, 192);
        palettes[idx++] = new RGBQUAD(166, 202, 240);
        palettes[idx++] = new RGBQUAD(64, 32, 0);
        palettes[idx++] = new RGBQUAD(96, 32, 0);
        palettes[idx++] = new RGBQUAD(128, 32, 0);
        palettes[idx++] = new RGBQUAD(160, 32, 0);
        palettes[idx++] = new RGBQUAD(192, 32, 0);
        palettes[idx++] = new RGBQUAD(224, 32, 0);
        palettes[idx++] = new RGBQUAD(0, 64, 0);
        palettes[idx++] = new RGBQUAD(32, 64, 0);
        palettes[idx++] = new RGBQUAD(64, 64, 0);
        palettes[idx++] = new RGBQUAD(96, 64, 0);
        palettes[idx++] = new RGBQUAD(128, 64, 0);
        palettes[idx++] = new RGBQUAD(160, 64, 0);
        palettes[idx++] = new RGBQUAD(192, 64, 0);
        palettes[idx++] = new RGBQUAD(224, 64, 0);
        palettes[idx++] = new RGBQUAD(0, 96, 0);
        palettes[idx++] = new RGBQUAD(32, 96, 0);
        palettes[idx++] = new RGBQUAD(64, 96, 0);
        palettes[idx++] = new RGBQUAD(96, 96, 0);
        palettes[idx++] = new RGBQUAD(128, 96, 0);
        palettes[idx++] = new RGBQUAD(160, 96, 0);
        palettes[idx++] = new RGBQUAD(192, 96, 0);
        palettes[idx++] = new RGBQUAD(224, 96, 0);
        palettes[idx++] = new RGBQUAD(0, 128, 0);
        palettes[idx++] = new RGBQUAD(32, 128, 0);
        palettes[idx++] = new RGBQUAD(64, 128, 0);
        palettes[idx++] = new RGBQUAD(96, 128, 0);
        palettes[idx++] = new RGBQUAD(128, 128, 0);
        palettes[idx++] = new RGBQUAD(160, 128, 0);
        palettes[idx++] = new RGBQUAD(192, 128, 0);
        palettes[idx++] = new RGBQUAD(224, 128, 0);
        palettes[idx++] = new RGBQUAD(0, 160, 0);
        palettes[idx++] = new RGBQUAD(32, 160, 0);
        palettes[idx++] = new RGBQUAD(64, 160, 0);
        palettes[idx++] = new RGBQUAD(96, 160, 0);
        palettes[idx++] = new RGBQUAD(128, 160, 0);
        palettes[idx++] = new RGBQUAD(160, 160, 0);
        palettes[idx++] = new RGBQUAD(192, 160, 0);
        palettes[idx++] = new RGBQUAD(224, 160, 0);
        palettes[idx++] = new RGBQUAD(0, 192, 0);
        palettes[idx++] = new RGBQUAD(32, 192, 0);
        palettes[idx++] = new RGBQUAD(64, 192, 0);
        palettes[idx++] = new RGBQUAD(96, 192, 0);
        palettes[idx++] = new RGBQUAD(128, 192, 0);
        palettes[idx++] = new RGBQUAD(160, 192, 0);
        palettes[idx++] = new RGBQUAD(192, 192, 0);
        palettes[idx++] = new RGBQUAD(224, 192, 0);
        palettes[idx++] = new RGBQUAD(0, 224, 0);
        palettes[idx++] = new RGBQUAD(32, 224, 0);
        palettes[idx++] = new RGBQUAD(64, 224, 0);
        palettes[idx++] = new RGBQUAD(96, 224, 0);
        palettes[idx++] = new RGBQUAD(128, 224, 0);
        palettes[idx++] = new RGBQUAD(160, 224, 0);
        palettes[idx++] = new RGBQUAD(192, 224, 0);
        palettes[idx++] = new RGBQUAD(224, 224, 0);
        palettes[idx++] = new RGBQUAD(0, 0, 64);
        palettes[idx++] = new RGBQUAD(32, 0, 64);
        palettes[idx++] = new RGBQUAD(64, 0, 64);
        palettes[idx++] = new RGBQUAD(96, 0, 64);
        palettes[idx++] = new RGBQUAD(128, 0, 64);
        palettes[idx++] = new RGBQUAD(160, 0, 64);
        palettes[idx++] = new RGBQUAD(192, 0, 64);
        palettes[idx++] = new RGBQUAD(224, 0, 64);
        palettes[idx++] = new RGBQUAD(0, 32, 64);
        palettes[idx++] = new RGBQUAD(32, 32, 64);
        palettes[idx++] = new RGBQUAD(64, 32, 64);
        palettes[idx++] = new RGBQUAD(96, 32, 64);
        palettes[idx++] = new RGBQUAD(128, 32, 64);
        palettes[idx++] = new RGBQUAD(160, 32, 64);
        palettes[idx++] = new RGBQUAD(192, 32, 64);
        palettes[idx++] = new RGBQUAD(224, 32, 64);
        palettes[idx++] = new RGBQUAD(0, 64, 64);
        palettes[idx++] = new RGBQUAD(32, 64, 64);
        palettes[idx++] = new RGBQUAD(64, 64, 64);
        palettes[idx++] = new RGBQUAD(96, 64, 64);
        palettes[idx++] = new RGBQUAD(128, 64, 64);
        palettes[idx++] = new RGBQUAD(160, 64, 64);
        palettes[idx++] = new RGBQUAD(192, 64, 64);
        palettes[idx++] = new RGBQUAD(224, 64, 64);
        palettes[idx++] = new RGBQUAD(0, 96, 64);
        palettes[idx++] = new RGBQUAD(32, 96, 64);
        palettes[idx++] = new RGBQUAD(64, 96, 64);
        palettes[idx++] = new RGBQUAD(96, 96, 64);
        palettes[idx++] = new RGBQUAD(128, 96, 64);
        palettes[idx++] = new RGBQUAD(160, 96, 64);
        palettes[idx++] = new RGBQUAD(192, 96, 64);
        palettes[idx++] = new RGBQUAD(224, 96, 64);
        palettes[idx++] = new RGBQUAD(0, 128, 64);
        palettes[idx++] = new RGBQUAD(32, 128, 64);
        palettes[idx++] = new RGBQUAD(64, 128, 64);
        palettes[idx++] = new RGBQUAD(96, 128, 64);
        palettes[idx++] = new RGBQUAD(128, 128, 64);
        palettes[idx++] = new RGBQUAD(160, 128, 64);
        palettes[idx++] = new RGBQUAD(192, 128, 64);
        palettes[idx++] = new RGBQUAD(224, 128, 64);
        palettes[idx++] = new RGBQUAD(0, 160, 64);
        palettes[idx++] = new RGBQUAD(32, 160, 64);
        palettes[idx++] = new RGBQUAD(64, 160, 64);
        palettes[idx++] = new RGBQUAD(96, 160, 64);
        palettes[idx++] = new RGBQUAD(128, 160, 64);
        palettes[idx++] = new RGBQUAD(160, 160, 64);
        palettes[idx++] = new RGBQUAD(192, 160, 64);
        palettes[idx++] = new RGBQUAD(224, 160, 64);
        palettes[idx++] = new RGBQUAD(0, 192, 64);
        palettes[idx++] = new RGBQUAD(32, 192, 64);
        palettes[idx++] = new RGBQUAD(64, 192, 64);
        palettes[idx++] = new RGBQUAD(96, 192, 64);
        palettes[idx++] = new RGBQUAD(128, 192, 64);
        palettes[idx++] = new RGBQUAD(160, 192, 64);
        palettes[idx++] = new RGBQUAD(192, 192, 64);
        palettes[idx++] = new RGBQUAD(224, 192, 64);
        palettes[idx++] = new RGBQUAD(0, 224, 64);
        palettes[idx++] = new RGBQUAD(32, 224, 64);
        palettes[idx++] = new RGBQUAD(64, 224, 64);
        palettes[idx++] = new RGBQUAD(96, 224, 64);
        palettes[idx++] = new RGBQUAD(128, 224, 64);
        palettes[idx++] = new RGBQUAD(160, 224, 64);
        palettes[idx++] = new RGBQUAD(192, 224, 64);
        palettes[idx++] = new RGBQUAD(224, 224, 64);
        palettes[idx++] = new RGBQUAD(0, 0, 128);
        palettes[idx++] = new RGBQUAD(32, 0, 128);
        palettes[idx++] = new RGBQUAD(64, 0, 128);
        palettes[idx++] = new RGBQUAD(96, 0, 128);
        palettes[idx++] = new RGBQUAD(128, 0, 128);
        palettes[idx++] = new RGBQUAD(160, 0, 128);
        palettes[idx++] = new RGBQUAD(192, 0, 128);
        palettes[idx++] = new RGBQUAD(224, 0, 128);
        palettes[idx++] = new RGBQUAD(0, 32, 128);
        palettes[idx++] = new RGBQUAD(32, 32, 128);
        palettes[idx++] = new RGBQUAD(64, 32, 128);
        palettes[idx++] = new RGBQUAD(96, 32, 128);
        palettes[idx++] = new RGBQUAD(128, 32, 128);
        palettes[idx++] = new RGBQUAD(160, 32, 128);
        palettes[idx++] = new RGBQUAD(192, 32, 128);
        palettes[idx++] = new RGBQUAD(224, 32, 128);
        palettes[idx++] = new RGBQUAD(0, 64, 128);
        palettes[idx++] = new RGBQUAD(32, 64, 128);
        palettes[idx++] = new RGBQUAD(64, 64, 128);
        palettes[idx++] = new RGBQUAD(96, 64, 128);
        palettes[idx++] = new RGBQUAD(128, 64, 128);
        palettes[idx++] = new RGBQUAD(160, 64, 128);
        palettes[idx++] = new RGBQUAD(192, 64, 128);
        palettes[idx++] = new RGBQUAD(224, 64, 128);
        palettes[idx++] = new RGBQUAD(0, 96, 128);
        palettes[idx++] = new RGBQUAD(32, 96, 128);
        palettes[idx++] = new RGBQUAD(64, 96, 128);
        palettes[idx++] = new RGBQUAD(96, 96, 128);
        palettes[idx++] = new RGBQUAD(128, 96, 128);
        palettes[idx++] = new RGBQUAD(160, 96, 128);
        palettes[idx++] = new RGBQUAD(192, 96, 128);
        palettes[idx++] = new RGBQUAD(224, 96, 128);
        palettes[idx++] = new RGBQUAD(0, 128, 128);
        palettes[idx++] = new RGBQUAD(32, 128, 128);
        palettes[idx++] = new RGBQUAD(64, 128, 128);
        palettes[idx++] = new RGBQUAD(96, 128, 128);
        palettes[idx++] = new RGBQUAD(128, 128, 128);
        palettes[idx++] = new RGBQUAD(160, 128, 128);
        palettes[idx++] = new RGBQUAD(192, 128, 128);
        palettes[idx++] = new RGBQUAD(224, 128, 128);
        palettes[idx++] = new RGBQUAD(0, 160, 128);
        palettes[idx++] = new RGBQUAD(32, 160, 128);
        palettes[idx++] = new RGBQUAD(64, 160, 128);
        palettes[idx++] = new RGBQUAD(96, 160, 128);
        palettes[idx++] = new RGBQUAD(128, 160, 128);
        palettes[idx++] = new RGBQUAD(160, 160, 128);
        palettes[idx++] = new RGBQUAD(192, 160, 128);
        palettes[idx++] = new RGBQUAD(224, 160, 128);
        palettes[idx++] = new RGBQUAD(0, 192, 128);
        palettes[idx++] = new RGBQUAD(32, 192, 128);
        palettes[idx++] = new RGBQUAD(64, 192, 128);
        palettes[idx++] = new RGBQUAD(96, 192, 128);
        palettes[idx++] = new RGBQUAD(128, 192, 128);
        palettes[idx++] = new RGBQUAD(160, 192, 128);
        palettes[idx++] = new RGBQUAD(192, 192, 128);
        palettes[idx++] = new RGBQUAD(224, 192, 128);
        palettes[idx++] = new RGBQUAD(0, 224, 128);
        palettes[idx++] = new RGBQUAD(32, 224, 128);
        palettes[idx++] = new RGBQUAD(64, 224, 128);
        palettes[idx++] = new RGBQUAD(96, 224, 128);
        palettes[idx++] = new RGBQUAD(128, 224, 128);
        palettes[idx++] = new RGBQUAD(160, 224, 128);
        palettes[idx++] = new RGBQUAD(192, 224, 128);
        palettes[idx++] = new RGBQUAD(224, 224, 128);
        palettes[idx++] = new RGBQUAD(0, 0, 192);
        palettes[idx++] = new RGBQUAD(32, 0, 192);
        palettes[idx++] = new RGBQUAD(64, 0, 192);
        palettes[idx++] = new RGBQUAD(96, 0, 192);
        palettes[idx++] = new RGBQUAD(128, 0, 192);
        palettes[idx++] = new RGBQUAD(160, 0, 192);
        palettes[idx++] = new RGBQUAD(192, 0, 192);
        palettes[idx++] = new RGBQUAD(224, 0, 192);
        palettes[idx++] = new RGBQUAD(0, 32, 192);
        palettes[idx++] = new RGBQUAD(32, 32, 192);
        palettes[idx++] = new RGBQUAD(64, 32, 192);
        palettes[idx++] = new RGBQUAD(96, 32, 192);
        palettes[idx++] = new RGBQUAD(128, 32, 192);
        palettes[idx++] = new RGBQUAD(160, 32, 192);
        palettes[idx++] = new RGBQUAD(192, 32, 192);
        palettes[idx++] = new RGBQUAD(224, 32, 192);
        palettes[idx++] = new RGBQUAD(0, 64, 192);
        palettes[idx++] = new RGBQUAD(32, 64, 192);
        palettes[idx++] = new RGBQUAD(64, 64, 192);
        palettes[idx++] = new RGBQUAD(96, 64, 192);
        palettes[idx++] = new RGBQUAD(128, 64, 192);
        palettes[idx++] = new RGBQUAD(160, 64, 192);
        palettes[idx++] = new RGBQUAD(192, 64, 192);
        palettes[idx++] = new RGBQUAD(224, 64, 192);
        palettes[idx++] = new RGBQUAD(0, 96, 192);
        palettes[idx++] = new RGBQUAD(32, 96, 192);
        palettes[idx++] = new RGBQUAD(64, 96, 192);
        palettes[idx++] = new RGBQUAD(96, 96, 192);
        palettes[idx++] = new RGBQUAD(128, 96, 192);
        palettes[idx++] = new RGBQUAD(160, 96, 192);
        palettes[idx++] = new RGBQUAD(192, 96, 192);
        palettes[idx++] = new RGBQUAD(224, 96, 192);
        palettes[idx++] = new RGBQUAD(0, 128, 192);
        palettes[idx++] = new RGBQUAD(32, 128, 192);
        palettes[idx++] = new RGBQUAD(64, 128, 192);
        palettes[idx++] = new RGBQUAD(96, 128, 192);
        palettes[idx++] = new RGBQUAD(128, 128, 192);
        palettes[idx++] = new RGBQUAD(160, 128, 192);
        palettes[idx++] = new RGBQUAD(192, 128, 192);
        palettes[idx++] = new RGBQUAD(224, 128, 192);
        palettes[idx++] = new RGBQUAD(0, 160, 192);
        palettes[idx++] = new RGBQUAD(32, 160, 192);
        palettes[idx++] = new RGBQUAD(64, 160, 192);
        palettes[idx++] = new RGBQUAD(96, 160, 192);
        palettes[idx++] = new RGBQUAD(128, 160, 192);
        palettes[idx++] = new RGBQUAD(160, 160, 192);
        palettes[idx++] = new RGBQUAD(192, 160, 192);
        palettes[idx++] = new RGBQUAD(224, 160, 192);
        palettes[idx++] = new RGBQUAD(0, 192, 192);
        palettes[idx++] = new RGBQUAD(32, 192, 192);
        palettes[idx++] = new RGBQUAD(64, 192, 192);
        palettes[idx++] = new RGBQUAD(96, 192, 192);
        palettes[idx++] = new RGBQUAD(128, 192, 192);
        palettes[idx++] = new RGBQUAD(160, 192, 192);
        palettes[idx++] = new RGBQUAD(255, 251, 240);
        palettes[idx++] = new RGBQUAD(160, 160, 164);
        palettes[idx++] = new RGBQUAD(128, 128, 128);
        palettes[idx++] = new RGBQUAD(255, 0, 0);
        palettes[idx++] = new RGBQUAD(0, 255, 0);
        palettes[idx++] = new RGBQUAD(255, 255, 0);
        palettes[idx++] = new RGBQUAD(0, 0, 255);
        palettes[idx++] = new RGBQUAD(255, 0, 255);
        palettes[idx++] = new RGBQUAD(0, 255, 255);
        palettes[idx++] = new RGBQUAD(255, 255, 255);
    }
    
    private void read(String filename) throws IOException {
        InputStream dis = null;
        try {
            dis = new FileInputStream(filename);

            bmfHeader = new BITMAPFILEHEADER();
            byte[] bmfh = new byte[14];
            dis.read(bmfh, 0, 14);
            bmfHeader.bfType = bmfh[0] << 8 | bmfh[1];
            bmfHeader.bfSize = bint2int(bmfh, 2, 5);
            bmfHeader.bfReserved1 = bint2int(bmfh, 6, 7);
            bmfHeader.bfReserved2 = bint2int(bmfh, 8, 9);
            bmfHeader.bfOffBits = bint2int(bmfh, 10, 13);
            
            bmiHeader = new BITMAPINFOHEADER();
            byte[] bmih = new byte[40];
            dis.read(bmih, 0, 40);
            bmiHeader.biSize = bint2int(bmih, 0, 3);
            bmiHeader.biWidth = bint2int(bmih, 4, 7);
            bmiHeader.biHeight = bint2int(bmih, 8, 11);
            bmiHeader.biPlanes = bint2int(bmih, 12, 13);
            bmiHeader.biBitCount = bint2int(bmih, 14, 15);
            bmiHeader.biCompression = bint2int(bmih, 16, 19);
            bmiHeader.biSizeImage = bint2int(bmih, 20, 23);
            bmiHeader.biXPelsPerMeter = bint2int(bmih, 24, 27);
            bmiHeader.biYPelsPerMeter = bint2int(bmih, 28, 31);
            bmiHeader.biClrUsed = bint2int(bmih, 32, 35);
            bmiHeader.biClrImportant = bint2int(bmih, 36, 39);
            
            if (bmiHeader.biCompression != 0) {
                throw new RuntimeException("unsupported compression.");
            }
            
            if (bmiHeader.biBitCount <= 8) {
                int numcolors = (bmiHeader.biClrUsed <= 0) ? (1 & 0xff) << bmiHeader.biBitCount : bmiHeader.biClrUsed;
                palettes = new RGBQUAD[numcolors];
                for (int i = 0; i < numcolors; i++) {
                    byte[] bcolor = new byte[4];
                    dis.read(bcolor, 0, 4);
                    palettes[i] = new RGBQUAD();
                    palettes[i].rgbBlue = bcolor[0];
                    palettes[i].rgbGreen = bcolor[1];
                    palettes[i].rgbRed = bcolor[2];
                    palettes[i].rgbReserved = bcolor[3];
                }
            } else if (bmiHeader.biBitCount != 24) {
                throw new RuntimeException("unsupported biBitCount: " + bmiHeader.biBitCount + ".");
            }
            
            byte[] datas = new byte[bmiHeader.biSizeImage];
            dis.read(datas, 0, bmiHeader.biSizeImage);
            
            pixelDatas = new int[bmiHeader.biWidth * bmiHeader.biHeight];
            
            switch (bmiHeader.biBitCount) {
            case 1:
                convertBitMap1(datas);
                break;
            case 4:
                convertBitMap4(datas);
                break;
            case 8:
                convertBitMap8(datas);
                break;
            case 24:
                convertBitMap24(datas);
                break;
            }
            
        } finally {
            if (null != dis) try { dis.close(); } catch (Exception ex1) {}
        }
    }
    
    private void convertBitMap1(byte[] datas) {
        int realLineBcnt = (bmiHeader.biWidth + 7) / 8;
        int fileLineBcnt = bmiHeader.biSizeImage / bmiHeader.biHeight;
        int padCnt = (bmiHeader.biWidth % 8 > 0) ? 8 - (bmiHeader.biWidth % 8) : 0;
        
        int pixelDatasIdx = 0;
        for (int i = (bmiHeader.biHeight - 1); i >= 0; i--) {
            for (int j = 0; j < realLineBcnt; j++) {
                byte b = datas[i * fileLineBcnt + j];
                
                int tmpEnd = (j == (realLineBcnt - 1)) ? padCnt : 0;
                for (int n = 7; n >= tmpEnd; n--) {
                    int tmpPixel;
                    if (n > 0) {
                        tmpPixel = rgb2pixel(palettes[((b >> n) & 0x01)]);
                    } else {
                        tmpPixel = rgb2pixel(palettes[(b & 0x01)]);
                    }
                    pixelDatas[pixelDatasIdx++] = tmpPixel;
                }
            }
        }
    }
    private void convertBitMap4(byte[] datas) {
        int realLineBcnt = (bmiHeader.biWidth + 1) / 2;
        int fileLineBcnt = bmiHeader.biSizeImage / bmiHeader.biHeight;
        int padCnt = (bmiHeader.biWidth % 2 > 0) ? 2 - (bmiHeader.biWidth % 2) : 0;
        
        int pixelDatasIdx = 0;
        for (int i = (bmiHeader.biHeight - 1); i >= 0; i--) {
            for (int j = 0; j < realLineBcnt; j++) {
                byte b = datas[i * fileLineBcnt + j];
                
                int tmpEnd = (j == (realLineBcnt - 1)) ? padCnt : 0;
                for (int n = 1; n >= tmpEnd; n--) {
                    int tmpPixel;
                    if (n > 0) {
                        tmpPixel = rgb2pixel(palettes[((b >> 4) & 0x0f)]);
                    } else {
                        tmpPixel = rgb2pixel(palettes[(b & 0x0f)]);
                    }
                    pixelDatas[pixelDatasIdx++] = tmpPixel;
                }
            }
        }
    }
    private void convertBitMap8(byte[] datas) {
        int realLineBcnt = bmiHeader.biWidth;
        int fileLineBcnt = bmiHeader.biSizeImage / bmiHeader.biHeight;
        
        int pixelDatasIdx = 0;
        for (int i = (bmiHeader.biHeight - 1); i >= 0; i--) {
            for (int j = 0; j < realLineBcnt; j++) {
                byte b = datas[i * fileLineBcnt + j];
                int tmpPixel = rgb2pixel(palettes[b & 0xff]);
                pixelDatas[pixelDatasIdx++] = tmpPixel;
            }
        }
    }
    private void convertBitMap24(byte[] datas) {
        int fileLineBcnt = bmiHeader.biSizeImage / bmiHeader.biHeight;
        int pixelDatasIdx = 0;
        for (int i = (bmiHeader.biHeight - 1); i >= 0; i--) {
            for (int j = 0; j < bmiHeader.biWidth * 3; j += 3) {
                RGBQUAD bmiColor = new RGBQUAD();
                bmiColor.rgbBlue = datas[i * fileLineBcnt + j];
                bmiColor.rgbGreen = datas[i * fileLineBcnt + j + 1];
                bmiColor.rgbRed = datas[i * fileLineBcnt + j + 2];
                pixelDatas[pixelDatasIdx++] = rgb2pixel(bmiColor);
            }
        }
    }
    
//    protected static String toHexString(int b) {
//        String str = Integer.toHexString(b);
//        if (str.length() < 2) {
//            str = "0" + str;
//        }
//        return str;
//    }
    
    private int rgb2pixel(RGBQUAD palette) {
        int ret = 0xff;
        ret = (ret << 8) | (palette.rgbRed & 0xff);
        ret = (ret << 8) | (palette.rgbGreen & 0xff);
        ret = (ret << 8) | (palette.rgbBlue & 0xff);
        return ret;
    }
    
//    private int int2bint(int num, int bcnt) {
//        int result = 0x0;
//        for (int i = 0; i < bcnt; i++) {
//            if (i == 0) {
//                result = result | (byte) (num & 0xff);
//            } else {
//                result = result << 8 | (byte) (num >> (8 * i) & 0xff);
//            }
//        }
//        return result;
//    }
    private byte[] int2bytes(int num, int bcnt) {
        byte[] bytes = new byte[bcnt];
        for (int i = 0; i < bcnt; i++) {
            if (i == 0) {
                bytes[i] = (byte) (num & 0xff);
            } else {
                bytes[i] = (byte) (num >> (i * 8) & 0xff);
            }
        }
        return bytes;
    }
    
    private int bint2int(byte[] bytes, int begin, int end) {
        int result = 0 & 0xff;
        for (int i = begin; i <= end; i++) {
            if (i == begin) {
                result = result | (bytes[i] & 0xff);
            } else {
                result = result | ((bytes[i] & 0xff) << (8 * (i - begin)));
            }
        }
        return result;
    }
    
    private byte[] pixels2byte() {
        switch (bmiHeader.biBitCount) {
        case 1:
            return pixels2byte1();
        case 4:
            return pixels2byte4();
        case 8:
            return pixels2byte8();
        case 24:
            return pixels2byte24();
        }
        return null;
    }
    private byte[] pixels2byte1() {
        int lineBCnt = (bmiHeader.biWidth + 7) / 8;
        byte[] result = new byte[lineBCnt * bmiHeader.biHeight];
        int idx = 0;
        
        int linePadCnt = (bmiHeader.biWidth % 8) <= 0 ? 0 : 8 - (bmiHeader.biWidth % 8);
        int pixelsIdx = 0;
        for (int i = 0; i < bmiHeader.biHeight; i++) {
            int pixelcnt = 0;
            int bdata = 0 & 0xff;
            for (int j = 0; j < (bmiHeader.biWidth + linePadCnt); j++) {
                if (j < bmiHeader.biWidth) {
                    int pixel = pixelDatas[pixelsIdx++] & 0xffffff;
                    if (pixel < 0xffffff) {
                        bdata = (bdata << 1) | 0x00;
                    } else {
                        bdata = (bdata << 1) | 0x01;
                    }
                } else {
                    bdata = (bdata << 1) | 0x00;
                }
                
                pixelcnt++;
                
                
                if (pixelcnt >= 8) {
                    result[idx++] = (byte) (bdata & 0xff);
                    pixelcnt = 0;
                    bdata = 0 & 0xff;
                }
            }
            
        }

        return result;
    }
    private byte[] pixels2byte4() {
        int lineBcnt = (bmiHeader.biWidth + 1) / 2;
        int linePadPixelCnt = (bmiHeader.biWidth % 2) <= 0 ? 0 : 2 - (bmiHeader.biWidth % 2);
        
        byte[] result = new byte[lineBcnt * bmiHeader.biHeight];
        int idx = 0;
        
        int pixelsIdx = 0;
        for (int i = 0; i < bmiHeader.biHeight; i++) {
            int pixelcnt = 0;
            int bdata = 0 & 0xff;
            for (int j = 0; j < (bmiHeader.biWidth + linePadPixelCnt); j++) {
                if (j < bmiHeader.biWidth) {
                    bdata = (bdata << 4) | findPalette(pixelDatas[pixelsIdx++] & 0xffffff);
                } else {
                    bdata = (bdata << 4) | 0x00;
                }
                
                pixelcnt++;
                
                
                if (pixelcnt >= 2) {
                    result[idx++] = (byte) (bdata & 0xff);
                    pixelcnt = 0;
                    bdata = 0 & 0xff;
                }
            }
        }
        
        return result;
    }
    private byte[] pixels2byte8() {
        byte[] result = new byte[bmiHeader.biWidth * bmiHeader.biHeight];
        int idx = 0;
        for (int i = 0; i < bmiHeader.biHeight; i++) {
            for (int j = 0; j < bmiHeader.biWidth; j++) {
                result[idx++] = (byte) (findPalette(pixelDatas[i * bmiHeader.biWidth + j]) & 0xff);
            }
        }
        return result;
    }
    private byte[] pixels2byte24() {
        byte[] result = new byte[bmiHeader.biWidth * bmiHeader.biHeight * 3];
        
        int idx = 0;
        for (int i = 0; i < pixelDatas.length; i++) {
            int pixel = pixelDatas[i];
            result[idx++] = (byte) (pixel & 0xff);
            result[idx++] = (byte) (pixel >> 8 & 0xff);
            result[idx++] = (byte) (pixel >> 16 & 0xff);
        }
        
        return result;
    }
    
    private int findPalette(int pixel) {
        int b = pixel & 0xff;
        int g = pixel >> 8 & 0xff;
        int r = pixel >> 16 & 0xff;
        
        int reulst = -1;
        for (int i = 0; i < palettes.length; i++) {
            RGBQUAD paltette = palettes[i];
            if (r == paltette.rgbRed && g == paltette.rgbGreen && b == paltette.rgbBlue) {
                reulst = i;
                break;
            }
        }
        
        if (reulst == -1) {
            int tmp1 = 766;
            for (int i = 0; i < palettes.length; i++) {
                RGBQUAD paltette = palettes[i];
                int tmp2 = Math.abs(paltette.rgbRed - r) + Math.abs(paltette.rgbGreen - g) 
                        + Math.abs(paltette.rgbBlue - b);
                if (tmp2 < tmp1) {
                    reulst = i;
                    tmp1 = tmp2;
                }
            }
        }

        return reulst;
    }
    
    public int getWidth() {
        return bmiHeader.biWidth;
    }
    public int getHeight() {
        return bmiHeader.biHeight;
    }
    public int getBitCount() {
        return bmiHeader.biBitCount;
    }
    
    public int[] getPixels(int x, int y, int w, int h) {
        int[] result = new int[w * h];
        int idx = 0;
        for (int i = y; i < (y + h); i++) {
            for (int j = x; j < (x + w); j++) {
                result[idx++] = this.pixelDatas[i * bmiHeader.biWidth + j];
            }
        }
        return result;
    }

    public byte[] printLogoConvert() {
        if (bmiHeader.biBitCount != 1)
            throw new RuntimeException("unsupported bitcount:" + bmiHeader.biBitCount + ".");
        
        if (bmiHeader.biWidth < 1 || bmiHeader.biWidth > 384)
            throw new RuntimeException("Width's range: (1<= Width <= 384).");
        
        if (bmiHeader.biHeight < 1 || bmiHeader.biHeight > 500)
            throw new RuntimeException("Height's range: (1<= Height <= 500).");

        int lineBcnt = (bmiHeader.biWidth + 7) / 8;
        int mpixelCnt = bmiHeader.biWidth % 8;

        byte[] result = new byte[lineBcnt * bmiHeader.biHeight + 5];
        int idx = 0;
        
        result[idx++] = (byte) (bmiHeader.biWidth >> 8 & 0xff);
        result[idx++] = (byte) (bmiHeader.biWidth & 0xff);
        result[idx++] = (byte) (bmiHeader.biHeight >> 8 & 0xff);
        result[idx++] = (byte) (bmiHeader.biHeight & 0xff);
        result[idx++] = 0x00;
        
        byte[] abyMask = new byte[] {(byte) 0xff, (byte) 0x80, (byte) 0xc0, (byte) 0xe0, (byte) 0xf0, (byte) 0xf8, (byte) 0xfc, (byte) 0xfe};
        byte[] bdatas = pixels2byte();
        for (int i = 0; i < bmiHeader.biHeight; i++) {
            for (int j = 0; j < lineBcnt; j++) {
                byte b = (byte) (bdatas[i * lineBcnt + j] ^ 0xff);
                if (j == (lineBcnt - 1)) {
                    b = (byte) (b & abyMask[mpixelCnt]);
                }
                result[idx++] = b;
            }
        }
        
        return result;
    }
    
    public void output2file(String filename) throws IOException {
        OutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            
            out.write(bmfHeader.bfType >> 8 & 0xff);
            out.write(bmfHeader.bfType & 0xff);
            out.write(int2bytes(bmfHeader.bfSize, 4));
            out.write(int2bytes(bmfHeader.bfReserved1, 2));
            out.write(int2bytes(bmfHeader.bfReserved2, 2));
            out.write(int2bytes(bmfHeader.bfOffBits, 4));
            
            out.write(int2bytes(bmiHeader.biSize, 4));
            out.write(int2bytes(bmiHeader.biWidth, 4));
            out.write(int2bytes(bmiHeader.biHeight, 4));
            out.write(int2bytes(bmiHeader.biPlanes, 2));
            out.write(int2bytes(bmiHeader.biBitCount, 2));
            out.write(int2bytes(bmiHeader.biCompression, 4));
            out.write(int2bytes(bmiHeader.biSizeImage, 4));
            out.write(int2bytes(bmiHeader.biXPelsPerMeter, 4));
            out.write(int2bytes(bmiHeader.biYPelsPerMeter, 4));
            out.write(int2bytes(bmiHeader.biClrUsed, 4));
            out.write(int2bytes(bmiHeader.biClrImportant, 4));
            
            if (null != palettes) {
                for (int i = 0; i < palettes.length; i++) {
                    RGBQUAD palette = palettes[i];
                    out.write(palette.rgbBlue & 0xff);
                    out.write(palette.rgbGreen & 0xff);
                    out.write(palette.rgbRed & 0xff);
                    out.write(palette.rgbReserved & 0xff);
                }
            }
            
            byte[] bdatas = pixels2byte();
            int lineBcnt = bmiHeader.biSizeImage / bmiHeader.biHeight;
            int lineRealBcnt = bdatas.length / bmiHeader.biHeight;
            int padBcnt = lineBcnt - lineRealBcnt;
            
            for (int i = (bmiHeader.biHeight - 1); i >= 0; i--) {
                for (int j = 0; j < lineRealBcnt; j++) {
                    byte b = bdatas[i * lineRealBcnt + j];
                    out.write(b);
                }
                if (padBcnt > 0) {
                    for (int j = 0; j < padBcnt; j++) {
                        out.write(0x00);
                    }
                }
            }
            
        } finally {
            if (null != out) try { out.close(); } catch (Exception ex1) {}
        }
    }
    
    protected class BITMAPFILEHEADER {
        protected int bfType; // 2 byte
        protected int bfSize; // 4 byte
        protected int bfReserved1;    // 2 byte
        protected int bfReserved2;    // 2 byte
        protected int bfOffBits;      // 4 byte

    }
    
    protected class BITMAPINFOHEADER {
        protected int biSize;         // 4 byte
        protected int biWidth;        // 4 byte
        protected int biHeight;       // 4 byte
        protected int biPlanes;       // 2 byte
        protected int biBitCount;     // 2 byte
        protected int biCompression;  // 4 byte
        protected int biSizeImage;    // 4 byte
        protected int biXPelsPerMeter;    // 4 byte
        protected int biYPelsPerMeter;    // 4 byte
        protected int biClrUsed;          // 4 byte
        protected int biClrImportant;     // 4 byte

    }
    
    protected class RGBQUAD {
        protected int rgbBlue;          // 1 byte
        protected int rgbGreen;         // 1 byte
        protected int rgbRed;           // 1 byte
        protected int rgbReserved = 0;  // 1 byte
        
        protected RGBQUAD() {
        }
        
        protected RGBQUAD(int r, int g, int b) {
            this.rgbRed = r;
            this.rgbGreen = g;
            this.rgbBlue = b;
        }
    }
    
//    public static void main(String[] args) {
//        try {
//            BitMap bitMap1 = new BitMap("D:/temp/bmp/test1_1.bmp");
//            
//            BitMap bitMap = new BitMap(bitMap1.getPixels(0, 0, bitMap1.getWidth(), bitMap1.getHeight()), 
//                    bitMap1.getWidth(), bitMap1.getHeight(), 1);
//            
//            bitMap.output2file("D:/temp/bmp/out.bmp");
//            
//            JFrame theFrame = new JFrame("bmp");
//            Image image = Toolkit.getDefaultToolkit().createImage(
//                    new MemoryImageSource(bitMap.bmiHeader.biWidth, bitMap.bmiHeader.biHeight, 
//                            bitMap.pixelDatas, 0, bitMap.bmiHeader.biWidth));
//            JLabel theLabel = new JLabel(new ImageIcon(image));
//            theFrame.getContentPane().add(new JScrollPane(theLabel));
//            theFrame.setSize(300, 300);
//            theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            theFrame.setVisible(true);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
    
}
