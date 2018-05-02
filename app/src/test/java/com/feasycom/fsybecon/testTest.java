package com.feasycom.fsybecon;


import com.feasycom.util.TeaCode;

import org.junit.Test;

/**
 * Created by ${YORK} on 2017/8/4.
 */
public class testTest  {
    byte[] a = new byte[]{
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00
    };
    byte[] b;
    byte[] c;

    @Test
    public void haha() throws Exception {
        TeaCode teaCode = new TeaCode();
        b = teaCode.encrypt_bitstream(a);
        c = teaCode.decrypt_bitstream(b);
        for (int i = 0; i < 8; i++) {
            System.out.println(b[i]);
        }
        for (int i = 0; i < 8; i++) {
            System.out.println(c[i]);
        }
    }
}