package com.nominanuda.image;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.nominanuda.image.ImgFsPathConvertor;

public class ImgFsPathConvertorTest {

    @Test
    public void test() {
        ImgFsPathConvertor c = new ImgFsPathConvertor();
        assertEquals("0/0a/0afoo", c.apply("0afoo"));
    }

}
