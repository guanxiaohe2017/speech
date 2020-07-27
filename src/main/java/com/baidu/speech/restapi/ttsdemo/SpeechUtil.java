package com.baidu.speech.restapi.ttsdemo;

import com.baidu.speech.restapi.common.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SpeechUtil {

    public static ObtainInfo obtainAudioBytes(ParamInfo param) throws IOException, DemoException {

        TokenHolder holder = new TokenHolder(param.getAppKey(), param.getSecretKey(), TokenHolder.ASR_SCOPE);
        holder.refresh();
        String token = holder.getToken();

        // 此处2次urlencode， 确保特殊字符被正确编码
        String params = "tex=" + ConnUtil.urlEncode(ConnUtil.urlEncode(param.getText()));
        params += "&per=" + param.getPer();
        params += "&spd=" + param.getSpd();
        params += "&pit=" + param.getPit();
        params += "&vol=" + param.getVol();
        params += "&cuid=" + param.getCuid();
        params += "&tok=" + token;
        params += "&aue=" + param.getAue();
        params += "&lan=zh&ctp=1";
        System.out.println(param.url + "?" + params); // 反馈请带上此url，浏览器上可以测试
        HttpURLConnection conn = (HttpURLConnection) new URL(param.getUrl()).openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        PrintWriter printWriter = new PrintWriter(conn.getOutputStream());
        printWriter.write(params);
        printWriter.close();
        String contentType = conn.getContentType();
        ObtainInfo obtainInfo = new ObtainInfo();
        if (contentType.contains("audio/")) {
            byte[] bytes = ConnUtil.getResponseBytes(conn);
            String format = getFormat(param.getAue());
            obtainInfo.setBytes(bytes);
            obtainInfo.setString(format);
        } else {
            System.err.println("ERROR: content-type= " + contentType);
            String res = ConnUtil.getResponseString(conn);
            obtainInfo.setErrorMsg(res);
            System.err.println(res);
        }
        return obtainInfo;
    }

    // 下载的文件格式, 3：mp3(default) 4： pcm-16k 5： pcm-8k 6. wav
    private static String getFormat(int aue) {
        String[] formats = {"mp3", "pcm", "pcm", "wav"};
        return formats[aue - 3];
    }
}
