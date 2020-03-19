package com.china.fortune.image;

import com.china.fortune.global.Log;
import com.china.fortune.os.common.OsDepend;
import com.china.fortune.timecontrol.timeout.TimeoutAction;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;

public class ImageBlur {
    static private int getRowSize(int w, int i, int radius) {
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
        return x;
    }

    static private int getColSize(int h, int j, int radius) {
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
        return y;
    }

    private static int sumOnRow(int[] pix, int w, int h, int n, int i, int j, int k, int radius) {
        int sum = 0;
        int xMax = i + radius;
        for (int x = i - radius; x <= xMax; x++) {
            if (x >= 0 && x < w) {
                sum += pix[n * (j * w + x) + k];
            }
        }
        return sum;
    }

    private static int sumOnCol(int[] pix, int w, int h, int n, int i, int j, int k, int radius) {
        int sum = 0;
        int yMax = j + radius;
        for (int y = j - radius; y <= yMax; y++) {
            if (y >= 0 && y < h) {
                sum += pix[n * (y * w + i) + k];
            }
        }
        return sum;
    }

    static public void doBlur(BufferedImage bi, int definition) {
        int radius = bi.getWidth() / definition / 2;
        if (radius > 0) {
            Raster raster = bi.getData();
            int w = raster.getWidth();
            int h = raster.getHeight();
            int n = raster.getNumBands();

            int[] buf = new int[w * h * n];
            int[] pix = raster.getPixels(0, 0, w, h, buf);
            int[] data = new int[w * h * n];

            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    int xy = n * (j * w + i);
                    for (int k = 0; k < n; k++) {
                        if (j == 0) {
                            data[xy] = sumOnCol(pix, w, h, n, i, j, k, radius);
                        } else {
                            data[xy] = data[xy - n * w];
                            if (j >= radius + 1) {
                                data[xy] -= pix[xy - w * n * (radius + 1)];
                            }
                            if (j + radius < h) {
                                data[xy] += pix[xy + w * n * radius];
                            }
                        }
                        xy++;
                    }
                }
            }

            for (int j = 0; j < h; j++) {
                int count = getColSize(h, j, radius);
                for (int i = 0; i < w; i++) {
                    for (int k = 0; k < n; k++) {
                        int xy = n * (j * w + i) + k;
                        data[xy] /= count;
                    }
                }
            }

            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    int xy = n * (j * w + i);
                    for (int k = 0; k < n; k++) {
                        if (i == 0) {
                            pix[xy] = sumOnRow(data, w, h, n, i, j, k, radius);
                        } else {
                            pix[xy] = pix[xy - n];
                            if (i - radius - 1 >= 0) {
                                pix[xy] -= data[xy - n * (radius + 1)];
                            }
                            if (i + radius < w) {
                                pix[xy] += data[xy + n * radius];
                            }
                        }
                        xy++;
                    }
                }
            }
            for (int i = 0; i < w; i++) {
                int count = getRowSize(w, i, radius);
                for (int j = 0; j < h; j++) {
                    for (int k = 0; k < n; k++) {
                        int xy = n * (j * w + i) + k;
                        pix[xy] /= count;
                    }
                }
            }

            bi.getRaster().setPixels(0, 0, w, h, pix);
        }
    }

    public static void main(String[] args) {
        String sSrc;
        String sDes;
        if (OsDepend.isWin()) {
            sSrc = "F:\\080.jpg";
            sSrc = "z:\\111.jpg";
            sDes = "Z:\\blur.jpg";
        } else {
            sSrc = "/Users/zjrcsoft/OneDrive/111.jpg";
            sDes = "/Users/zjrcsoft/OneDrive/blur.jpg";
        }

        try {
            TimeoutAction ta = new TimeoutAction();
            ta.start();
            BufferedImage img = ImageIO.read(new File(sSrc));
            Log.log("read " + ta.getMilliseconds());
            doBlur(img, 15);
            Log.log("blur " + ta.getMilliseconds());
            ImageIO.write(img, "jpg", new File(sDes));
            Log.log("write " + ta.getMilliseconds());
        } catch (Exception e) {
            Log.logException(e);
        }
    }
}
