package com.star.demo.utils;

import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.global.avcodec;

import java.io.Console;

import static org.bytedeco.ffmpeg.global.avutil.av_free;

public class CodeCheck {

    /**
     * 是否支持编码器
     * @param codecName 编码名称
     */
    public static boolean supportEncode(String codecName) {
        AVCodec codec= avcodec.avcodec_find_encoder_by_name(codecName);
        if (codec == null){
            System.out.println("不支持编码格式"+ codecName);
        }
        try {
            return codec!=null;
        }finally {
//            av_free(codec);
        }
    }

    /**
     * 是否支持解码器
     * @param codecName 解码名称
     */
    public static boolean supportDecode(String codecName) {
        AVCodec codec= avcodec.avcodec_find_decoder_by_name(codecName);
        if (codec == null){
            System.out.println("不支持解码格式"+ codecName);
        }
        try {
            return codec!=null;
        }finally {
//            av_free(codec);
        }
    }
}
