package com.baidu.speech.restapi.common;

import lombok.Data;

@Data
public class ObtainInfo {
    private byte[] bytes;

    private String string;

    private String errorMsg;
}
