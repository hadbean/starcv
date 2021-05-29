package com.star.demo.test;

import com.star.demo.parse.ImageParser;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class FfmpegTest {

    public static void main(String[] args) {

        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        ImageParser parser = new ImageParser("", "");
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        File xml = new File("/Users/bohu/lbpcascade_frontalface_improved.xml");
        CascadeClassifier cascadeClassifier = new CascadeClassifier(xml.getAbsolutePath());
//        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber();
//        FFmpegFrameRecorder fFmpegFrameGrabber = new FFmpegFrameRecorder(grabber);
        try {
            grabber.start();
            FrameRecorder recorder = FrameRecorder.createDefault("/Users/bohu/Desktop/img/a000.flv", grabber.getImageWidth(), grabber.getImageWidth());
            recorder.setFormat("flv");
            recorder.setFrameRate(50);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);

            recorder.start();
            CanvasFrame frame = new CanvasFrame("自拍");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setAlwaysOnTop(true);
            frame.getCanvas().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Frame f = null;
                    try {
                        f = grabber.grab();
                        Mat image = converter.convertToMat(f);
                        imwrite("/Users/bohu/Desktop/img/11.jpg", image);
                    } catch (FrameGrabber.Exception exception) {
                        exception.printStackTrace();
                    }

                }
            });
            while (true) {
                if (!frame.isDisplayable()) {
                    grabber.close();
                    recorder.close();
                    System.exit(1);
                }
                Frame f = grabber.grab();
                Mat image = converter.convertToMat(f);
                Mat grayImg = new Mat();
                cvtColor(image, grayImg, COLOR_BGRA2GRAY);
                equalizeHist(grayImg, grayImg);
                RectVector faces = new RectVector();
                cascadeClassifier.detectMultiScale(grayImg, faces);
                for (int i = 0; i < faces.size(); i++) {
                    Rect r = faces.get(i);
                    rectangle(image, r, new Scalar(0, 255, 0, 1));
                }
//                parser.smooth(image);
                recorder.record(f);
                frame.showImage(converter.convert(image));

                Thread.sleep(20);
            }
        } catch (FrameGrabber.Exception | InterruptedException | FrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }
}
