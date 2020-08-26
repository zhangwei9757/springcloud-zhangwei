//package com.zhangwei.utils;
//
///**
// * @author zhangwei
// * @date 2020-08-22
// * <p> 字节数组工具类
// */
//public class ByteUtils {
//
//    /**
//     * 1 个字节 转 int
//     *
//     * @param b
//     * @return
//     */
//    public static int byte2int(byte[] b) {
//        int res = 0;
//        int bLen = b.length;
//
//        if (bLen < 5) {
//            for (int i = 0; i < bLen; i++) {
//                res += (b[i] & 0xFF) << (8 * i);
//            }
//        }
//        return res;
//    }
//
//    /**
//     * 4个字节数组 转 int
//     *
//     * @param b      字节数组
//     * @param offset 位置
//     * @return
//     */
//    public static int byteArrayToInt(byte[] b, int offset) {
//        int value = 0;
//        for (int i = 0; i < 4; i++) {
//            int shift = (4 - 1 - i) * 8;
//            value += (b[i + offset] & 0x000000FF) << shift;
//        }
//        return value;
//    }
//}
