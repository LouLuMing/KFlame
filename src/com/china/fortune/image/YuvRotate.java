package com.china.fortune.image;

public class YuvRotate {
    public static byte[] newBytes(int w, int h) {
        return new byte[w * h * 3 / 2];
    }

    public static void degree90(byte[] src, int w, int h, byte[] dst) {
        int wh = w * h;
        int i = 0;
        int j;
        for (int x = 0; x < w; x++) {
            j = wh + x;
            for (int y = h - 1; y >= 0; y--) {
                j -= w;
                dst[i] = src[j];
                i++;
            }
        }
        i = wh * 3 / 2 - 1;
        for (int x = w - 1; x > 0; x = x - 2) {
            j = wh + x;
            for (int y = 0; y < h / 2; y++) {
                dst[i] = src[j];
                i--;
                dst[i] = src[j - 1];
                j += w;
                i--;
            }
        }
    }

    public static void degree180(byte[] src, int w, int h, byte[] dst) {
        int wh = w * h;
        int i = 0;
        int count = 0;
        for (i = wh - 1; i >= 0; i--) {
            dst[count] = src[i];
            count++;
        }
        for (i = wh * 3 / 2 - 1; i >= wh; i -= 2) {
            dst[count++] = src[i - 1];
            dst[count++] = src[i];
        }
    }

    public static byte[] degree270(byte[] src, int w, int h, byte[] dst) {
        int wh = w * h;
        int i = 0;
        int j;
        for (int x = w - 1; x >= 0; x--) {
            j = 0;
            for (int y = 0; y < h; y++) {
                dst[i] = src[j + x];
                i++;
                j += w;
            }
        }
        for (int x = w - 1; x > 0; x = x - 2) {
            j = wh + x;
            for (int y = 0; y < h / 2; y++) {
                dst[i] = src[j - 1];
                i++;
                dst[i] = src[j];
                i++;
                j += w;
            }
        }
        return dst;
    }

    public static void mirror(byte[] src, int w, int h) { //src是原始yuv数组
        int i;
        int index;
        byte temp;
        int a, b;
        for (i = 0; i < h; i++) {
            a = i * w;
            b = (i + 1) * w - 1;
            while (a < b) {
                temp = src[a];
                src[a] = src[b];
                src[b] = temp;
                a++;
                b--;
            }
        }

        // mirror u and v
        index = w * h;
        for (i = 0; i < h / 2; i++) {
            a = i * w;
            b = (i + 1) * w - 2;
            while (a < b) {
                temp = src[a + index];
                src[a + index] = src[b + index];
                src[b + index] = temp;

                temp = src[a + index + 1];
                src[a + index + 1] = src[b + index + 1];
                src[b + index + 1] = temp;
                a+=2;
                b-=2;
            }
        }
    }

    public static void main(String[] args) {
        byte[] src = newBytes(10, 10);
        byte[] dst = newBytes(10, 10);
        degree90(src, 10, 10, dst);
    }
}
