package com.star.demo.graber;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;

public class RtspFFmpegerGraberWithGpu {

    //nvdia编解码：h264_nvenc,hevc_nvenc,支持的像素格式：yuv420p nv12 p010le yuv444p p016le yuv444p16le bgr0 rgb0 cuda d3d11
    //intel编解码：hevc_qsv ,h264_qsv,像素格式 ：nv12 p010le qsv,其中qsv像素格式可以解码但是无法编码。
    public void graber(String input,String deCodeName,String output){

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(input);
        grabber.setFormat("rtsp");
        grabber.setOption("rtsp_transport","tcp");
        grabber.setOption("rtsp_flags","prefer_tcp");
        grabber.setOption("stimeout","300000");
        grabber.setVideoCodecName(deCodeName);
        grabber.setImageMode(FrameGrabber.ImageMode.RAW);
        grabber.setPixelFormat(avutil.AV_PIX_FMT_CUDA);
        try {
            grabber.start();
            FFmpegFrameRecorder recorder = null;
            if (output!=null){
                recorder = new FFmpegFrameRecorder(output,1);
                recorder.setVideoCodecName("hevc_nvenc");
                recorder.setPixelFormat(avutil.AV_PIX_FMT_CUDA);
                recorder.setFrameRate(25);
            }
            Frame frame;
            while (output!=null && (frame = grabber.grab())!=null){
                recorder.record(frame);
                Thread.sleep(20);
            }
            recorder.close();
            grabber.close();
        } catch (FFmpegFrameGrabber.Exception e) {
            e.printStackTrace();
        } catch (FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
