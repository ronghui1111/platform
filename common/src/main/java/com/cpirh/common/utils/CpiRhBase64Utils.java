package com.cpirh.common.utils;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.json.JSONUtil;

import java.nio.charset.Charset;
import java.util.Objects;


/**
 * @author ronghui
 * @Description
 * @date 2023/2/15 11:27
 */
public class CpiRhBase64Utils {
    private static final Charset CHARSET = Charset.forName("UTF-8");

    public static String encode(Object obj) {
        String json = JSONUtil.toJsonStr(obj);
        return Base64Encoder.encode(json, CHARSET);
    }

    public static String decode(String json) {
        return Base64Decoder.decodeStr(json, CHARSET);
    }

    public static <T> T decode(String json, Class<T> clazz) {
        return JSONUtil.toBean(decode(json), clazz);
    }
}
