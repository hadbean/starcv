package com.star.demo.test;

import com.star.demo.graber.RstpGrabber;
import com.star.demo.utils.CodeCheck;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RtspGrabberWithGPU {
    //nvdia编解码：h264_nvenc,hevc_nvenc,h264_cuvid支持的像素格式：yuv420p nv12 p010le yuv444p p016le yuv444p16le bgr0 rgb0 cuda d3d11
    //intel编解码：hevc_qsv ,h264_qsv,像素格式 ：nv12 p010le qsv,其中qsv像素格式可以解码但是无法编码。

//    public static String decodeName = "h264";
    public static String decodeName = "h264_cuvid";
//    public static String encodeName = "hevc_nvenc";
    public static String encodeName = "hevc";
    public static String input = "rtsp://keyvalue:Admin123456@172.16.65.139:554/Streaming/Channels/101?transportmode=unicast&profile=Profile_1";
    public static String output = "/home/keyvalue/Videos/xx01.mp4";
    public static String outputDir = "/home/keyvalue/Videos/";
    public static List<String> inputs = new ArrayList<>();
    public static boolean showImage = true;
    public static boolean isEncode = false;
    public static int thread = 1;

    public static void main(String[] args) {

        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                String flag = args[i];
                switch (flag) {
                    case "-decodeName": {
                        i++;
                        decodeName = args[i];
                        break;
                    }
                    case "-encodeName": {
                        i++;
                        encodeName = args[i];
                        break;
                    }
                    case "-input": {
                        i++;
                        input = args[i];
                        for (String s :
                                input.split(";")) {
                            inputs.add(s);
                        }

                        break;
                    }
                    case "-output": {
                        i++;
                        output = args[i];
                        break;
                    }
                    case "-showImage": {
                        i++;
                        showImage = args[i].equals("1");
                        break;
                    }
                    case "-thread": {
                        i++;
                        thread = Integer.parseInt(args[i]);
                        break;
                    }
                    case "-outputDir": {
                        i++;
                        outputDir = args[i];
                        break;
                    }
                    case "-isEncode": {
                        i++;
                        isEncode = args[i].equals("1");
                        break;
                    }

                }
            }
        }

        if (CodeCheck.supportDecode(decodeName) && CodeCheck.supportEncode(encodeName)) {
            FFmpegLogCallback.set();
            try {
                AtomicInteger count = new AtomicInteger();
                if (inputs.size() == 0 && input != null){
                    inputs.add(input);
                }
                if (inputs.size() > 0) {
                    inputs.forEach(x -> {
                        String[] s = x.split(",");
                        String in = s[0];
                        int n = thread;
                        if (s.length == 2) {
                            n = Integer.valueOf(s[1]);
                        }
                        for (int i = 0; i < n; i++) {
                            Thread t = new Thread(() -> {
                                RstpGrabber grabber = new RstpGrabber();
                                grabber.setDecodeName(decodeName);
                                grabber.setEncodeName(encodeName);
                                grabber.setEncode(isEncode);
                                grabber.setInput(in);
                                grabber.setOutput(outputDir + count.getAndIncrement() + ".mp4");
                                try {
                                    grabber.frameRecord(1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                            t.start();
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (CodeCheck.supportDecode(decodeName)) {
                System.out.println("不支持解码类型：" + decodeName);
            }

            if (CodeCheck.supportEncode(encodeName)) {
                System.out.println("不支持编码类型：" + encodeName);
            }
        }
    }
}