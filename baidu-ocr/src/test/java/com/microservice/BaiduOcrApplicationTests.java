package com.microservice;

import com.baidu.aip.ocr.AipOcr;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BaiduOcrApplicationTests {

    // 设置APPID/AK/SK
    public static final String APP_ID = "22879651";
    public static final String API_KEY = "tWBK8XIDfSHQfWhhU8jEXgv8";
    public static final String SECRET_KEY = "NuZgt0R8mHEpR0u1M3HhZmyMxwjUAW76";

    @Test
    public void contextLoads() {

        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
        // client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
        // client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        // System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        // 调用接口
        String path = "C:\\Users\\lenovo\\Desktop\\cor_test.png";
        JSONObject res = client.basicGeneral(path, new HashMap<String, String>());
        System.out.println(res.toString(2));
    }

    @Test
    public void demo() throws Exception {
//        InetAddress addr = InetAddress.getLocalHost();
//        System.out.println("Local HostAddress:" + addr.getHostAddress());
//        String hostname = addr.getHostName();
//        System.out.println("Local host name: " + hostname);


        Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        while (allNetInterfaces.hasMoreElements())
        {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            System.out.println(netInterface.getName());
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements())
            {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address)
                {
                    System.out.println("本机的IP = " + ip.getHostAddress());
                }
            }
        }
    }

    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("IP地址获取失败" + e.toString());
        }
        return "";
    }


    @Test
    public void ansiToUtf8() throws Exception {
        /*
         * 文件由ANSI转化为UTF-8
         * 需要用到流InputStreamReader和OutputStreamWriter
         * 这两个流有charset功能
         * */
        File srcFile = new File("C:\\Users\\lenovo\\Desktop\\ansi.txt");
        File destFile = new File("C:\\Users\\lenovo\\Desktop\\utf8.txt");
        InputStreamReader isr = new InputStreamReader(new FileInputStream(srcFile), "GBK"); //ANSI编码
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8"); //存为UTF-8

        int len = isr.read();
        while (-1 != len) {

            osw.write(len);
            len = isr.read();
        }
        //刷新缓冲区的数据，强制写入目标文件
        osw.flush();
        osw.close();
        isr.close();
    }

    @Test
    public void change() throws Exception {
        BufferedReader buf = null;
        OutputStreamWriter pw = null;
        String str = null;
        String allstr = "";

        //用于输入换行符的字节码
        byte[] c = new byte[2];
        c[0] = 0x0d;
        c[1] = 0x0a;
        String t = new String(c);
        File srcFile = new File("C:\\Users\\lenovo\\Desktop\\ansi.txt");
        String codeString = codeString(srcFile);
        System.out.println("原文件的格式: " + codeString);
        buf = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile), "GBK"));
        while ((str = buf.readLine()) != null) {
            allstr = allstr + str + t;
            System.out.println(allstr);

        }
        String encode = encode(allstr);
        System.out.println(encode);
        String decode = decode(encode);
        System.out.println(decode);
        buf.close();
        File destFile = new File("C:\\Users\\lenovo\\Desktop\\utf8.txt");
        pw = new OutputStreamWriter(new FileOutputStream(destFile), StandardCharsets.UTF_8);
        pw.write(decode);
        pw.close();
        String s = codeString(destFile);
        System.out.println("新文件的格式: " + s);
    }

    /**
     * <p>转为unicode 编码<p>
     *
     * @param str
     * @return unicodeString
     */
    public static String encode(String str) {
        String prefix = "\\u";
        StringBuilder sb = new StringBuilder();
        char[] chars = str.toCharArray();
        if (chars.length == 0) {
            return null;
        }
        for (char c : chars) {
            sb.append(prefix);
            sb.append(Integer.toHexString(c));
        }
        return sb.toString();
    }

    /**
     * 把unicode编码转换为中文
     *
     * @param str
     * @return
     */
    public static String decode(String str) {
        String sg = "\\u";
        int a = 0;
        List<String> list = new ArrayList<>();
        while (str.contains(sg)) {
            str = str.substring(2);
            String substring;
            if (str.contains(sg)) {
                substring = str.substring(0, str.indexOf(sg));
            } else {
                substring = str;
            }
            if (str.contains(sg)) {
                str = str.substring(str.indexOf(sg));
            }
            list.add(substring);
        }
        StringBuilder sb = new StringBuilder();
        if (!CollectionUtils.isEmpty(list)) {
            for (String string : list) {
                sb.append((char) Integer.parseInt(string, 16));
            }
        }
        return sb.toString();
    }

    public String change2(String string) {
        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {

            // 取出每一个字符
            char c = string.charAt(i);
            String str = Integer.toHexString(c);
            switch (4 - str.length()) {
                case 0:
                    unicode.append("\\u" + str);
                    break;
                case 1:
                    str = "0" + str;
                    unicode.append("\\u" + str);
                    break;
                case 2:
                case 3:
                default:
                    str = String.valueOf(c);
                    unicode.append(str);
                    break;
            }


        }
        return unicode.toString();
    }

    public static String codeString(File file) throws Exception {
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
        int p = (bin.read() << 8) + bin.read();
        bin.close();
        String code = null;

        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }

        return code;
    }
}
