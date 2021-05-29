package com.star.demo.parse;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
public class ImageParser implements Runnable{

    private String srcFile;
    private String distFile;

    public ImageParser(String srcFile, String distFile) {
        this.srcFile = srcFile;
        this.distFile = distFile;
    }

    public void smooth(String srcFile, String distFile){
        long begin = System.currentTimeMillis();
        Mat image = imread(srcFile);

        if (image != null) {
            GaussianBlur(image, image, new Size(3, 3), 0);
            imwrite(distFile, image);
        }
        System.out.println(distFile.concat(":")+(System.currentTimeMillis()-begin));
    }
    public void smooth(Mat image){
//        long begin = System.currentTimeMillis();
//        Mat image = imread(srcFile);

        if (image != null) {
            GaussianBlur(image, image, new Size(11, 11), 0);
//            imwrite(distFile, image);
        }
//        System.out.println(distFile.concat(":")+(System.currentTimeMillis()-begin));
    }

    @Override
    public void run() {
        smooth(srcFile,distFile);
    }
}
