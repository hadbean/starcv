package com.star.demo.graber;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;

import javax.swing.*;

public class RstpGrabber {

    private String decodeName = "h264_cuvid";
    private String encodeName = "hevc_nvenc";
    private int pixFormat = avutil.AV_PIX_FMT_CUDA;
    private String input = "rtsp://keyvalue:Admin123456@172.16.65.139:554/Streaming/Channels/101?transportmode=unicast&profile=Profile_1";
    private String output = "/home/keyvalue/Videos/xx01.mp4";
    private boolean showImage = false;
    private boolean isEncode = true;

    /**
     * 按帧录制视频
     *
     *                         -该地址只能是文件地址，如果使用该方法推送流媒体服务器会报错，原因是没有设置编码格式
     * @throws FrameGrabber.Exception
     * @throws FrameRecorder.Exception
     * @throws org.bytedeco.javacv.FrameRecorder.Exception
     */
    public void frameRecord(int audioChannel)
            throws Exception, org.bytedeco.javacv.FrameRecorder.Exception {

        boolean isStart = true;//该变量建议设置为全局控制变量，用于控制录制结束
        // 获取视频源
        FFmpegLogCallback.set();
        avutil.av_log_set_level(avutil.AV_LOG_ERROR);
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(getInput());
        grabber.setFormat("rtsp");
        grabber.setOption("rtsp_transport", "tcp");
        grabber.setOption("rtsp_flags", "prefer_tcp");
        grabber.setOption("stimeout", "300000");
        grabber.setOption("hwaccel", "cuvid");
        grabber.setVideoCodecName(decodeName);
        grabber.setImageMode(FrameGrabber.ImageMode.RAW);
        grabber.setPixelFormat(pixFormat);
        // 开始取视频源
        OpenCVFrameGrabber cvFrameGrabber;
        grabber.setFrameRate(30);
        grabber.setVideoBitrate(3000000);
        // 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(getOutput(), 1280, 720, audioChannel);
        recorder.setFrameRate(30);
        recorder.setVideoBitrate(3000000);
        if (recorder != null) {
//            recorder.setVideoCodecName("h264_nvenc");
//            recorder.setVideoCodecName("h264_vaapi");
//            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
            recorder.setFormat("flv");
            recorder.setFrameRate(30);
        }

        recordByFrame(grabber, recorder, isStart);
    }

    private void recordByFrame(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder, Boolean status)
            throws Exception {
        try {//建议在线程中使用该方法
            grabber.start();
            if (isEncode) {
                recorder.start();
            }
            System.out.println("开始抓取流：<" + input + " >;");
            Frame frame = null;
            CanvasFrame caname = null;
            if (showImage) {
                caname = new CanvasFrame("显示器");
                caname.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                caname.setAlwaysOnTop(true);
            }

            while (status && (frame = grabber.grabFrame()) != null) {
                if (isEncode) {
                    recorder.record(frame);
                }
                if (showImage && frame.getTypes().contains(Frame.Type.VIDEO)) {
                    caname.showImage(frame);
                }
            }
            if (isEncode) {
                recorder.stop();
            }
            grabber.stop();
        } finally {
            if (grabber != null) {
                grabber.stop();
            }
        }
    }

    public String getDecodeName() {
        return decodeName;
    }

    public void setDecodeName(String decodeName) {
        this.decodeName = decodeName;
    }

    public String getEncodeName() {
        return encodeName;
    }

    public void setEncodeName(String encodeName) {
        this.encodeName = encodeName;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public boolean isShowImage() {
        return showImage;
    }

    public void setShowImage(boolean showImage) {
        this.showImage = showImage;
    }

    public boolean isEncode() {
        return isEncode;
    }

    public void setEncode(boolean encode) {
        isEncode = encode;
    }
}
