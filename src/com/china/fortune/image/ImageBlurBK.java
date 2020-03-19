package com.china.fortune.image;

import com.china.fortune.global.Log;
import com.china.fortune.os.common.OsDepend;
import com.china.fortune.timecontrol.timeout.TimeoutAction;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;

public class ImageBlurBK {
    public static int xyToIndex(int w, int h, int n, int i, int j, int k) {
        if (i < 0 || i >= w) {
            return -1;
        } else if (j < 0 || j >= h) {
            return -1;
        } else {
            return n * (j * w + i) + k;
        }
    }

    static private int getMatrixSize(int w, int h, int i, int j, int radius) {
        int x = 1;
        if (i < radius) {
            x += i;
        } else {
            x += radius;
        }
        if (i > w - radius) {
            x += (w - 1 - i);
        } else {
            x += radius;
        }
        int y = 1;
        if (j < radius) {
            y += j;
        } else {
            y += radius;
        }
        if (j > h - radius) {
            y += (h - 1 - j);
        } else {
            y += radius;
        }
        return x * y;
    }

    public static int sumMatrix(int[] pix, int w, int h, int n, int i, int j, int k, int radius) {
        int sum = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                int xy = xyToIndex(w, h, n, i + x, j + y, k);
                if (xy >= 0) {
                    sum += pix[xy];
                }
            }
        }
        return sum;
    }

    public static int difRow(int[] pix, int w, int h, int n, int i, int j, int k, int radius) {
        int dif = 0;
        for (int y = -radius; y <= radius; y++) {
            if ((j + y) >= 0 && (j + y) < h) {
                int xy = n * ((j + y) * w + i + radius) + k;
                if (i + radius < w) {
                    if (xy >= 0 && xy < pix.length) {
                        dif += pix[xy];
                    }
                }
                if (i - radius - 1 >= 0) {
                    xy -= (radius * 2 + 1) * n;
                    if (xy >= 0 && xy < pix.length) {
                        dif -= pix[xy];
                    }
                }
            }
        }
        return dif;
    }

    public static int difCol(int[] pix, int w, int h, int n, int i, int j, int k, int radius) {
        int dif = 0;
        for (int x = -radius; x <= radius; x++) {
            int xy = xyToIndex(w, h, n, i + x, j + radius, k);
            if (xy >= 0) {
                dif += pix[xy];
            }
            xy = xyToIndex(w, h, n, i + x, j - radius - 1, k);
            if (xy >= 0) {
                dif -= pix[xy];
            }
        }
        return dif;
    }

    static public void doBlur(BufferedImage bi, int radius) {
        Raster raster = bi.getData();
        int w = raster.getWidth();
        int h = raster.getHeight();
        int n = raster.getNumBands();

        int[] buf = new int[w * h * n];
        int[] pix = raster.getPixels(0, 0, w, h, buf);
        int[] data = new int[w * h * n];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int xy = xyToIndex(w, h, n, i, j, 0);
                for (int k = 0; k < n; k++) {
                    if (i == 0) {
                        if (j == 0) {
                            data[xy] = sumMatrix(pix, w, h, n, i, j, k, radius);
                        } else {
                            data[xy] = data[xy - n * w] + difCol(pix, w, h, n, i, j, k, radius);
                        }
                    } else {
                        data[xy] = data[xy - n] + difRow(pix, w, h, n, i, j, k, radius);
                    }
                    xy++;
                }
            }
        }
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int count = getMatrixSize(w, h, i, j, radius);
                for (int k = 0; k < n; k++) {
                    int index = xyToIndex(w, h, n, i, j, k);
                    data[index] /= count;
                }
            }
        }
        bi.getRaster().setPixels(0, 0, w, h, data);
    }

    public static void main(String[] args) {
        String sSrc = null;
        String sDes = null;
        if (OsDepend.isWin()) {
            sSrc = "F:\\test.jpg";
//            sSrc = "z:\\9-99-1619076315.jpg";
            sDes = "z:\\blur.jpg";
        } else {
            sSrc = "/Users/zjrcsoft/OneDrive/111.jpg";
            sDes = "/Users/zjrcsoft/OneDrive/blur.jpg";
        }

        try {
            TimeoutAction ta = new TimeoutAction();
            ta.start();
            BufferedImage img = ImageIO.read(new File(sSrc));
            Log.log("read " + ta.getMilliseconds());
            doBlur(img, 18);
            Log.log("blur " + ta.getMilliseconds());
            ImageIO.write(img, "jpg", new File(sDes));
            Log.log("write " + ta.getMilliseconds());
        } catch (Exception e) {
            Log.logException(e);
        }
    }
}
