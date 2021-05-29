package com.star.demo.test;

import com.star.demo.graber.RtspFFmpegerGraberWithGpu;

import java.util.concurrent.atomic.AtomicInteger;

public class RtspTest {

    public static void main(String[] args) {

        AtomicInteger integer = new AtomicInteger();
        for (int i = 0; i < 20; i++) {
            System.out.println(integer.getAndIncrement());
        }
    }
}
