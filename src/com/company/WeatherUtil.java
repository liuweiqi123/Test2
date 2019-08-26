package com.company;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeatherUtil {
    public static void main(String[] args) {
        final String cityCode = "101050810";
        Thread t = new Thread() {
            @Override
            public void run() {
                while(true) {
                    try {
                        List<Weather> list = getURLInfo("http://www.weather.com.cn/weather/" + cityCode + ".shtml");
                        System.out.println(list);
                        Thread.sleep(1*1*10*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    public static List<Weather> getURLInfo(String urlInfo) {
        URL url;
        try {
            url = new URL(urlInfo);
            HttpURLConnection httpUrl;
            try {
                httpUrl = (HttpURLConnection) url.openConnection();
                InputStream is = httpUrl.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.replaceAll("</?a[^>]*>", "");
                    // line = line.replaceAll("<(\\w+)[^>]*>", "<$1>");
                    sb.append(line);
                }
                is.close();
                br.close();
                return getDataStructure(sb.toString().trim());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM月dd");
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static List<Weather> getDataStructure(String str) {
        List<Weather> list = new ArrayList<Weather>();
        String ul = str.split("<ul class=\"t clearfix\">")[1];
        String innerUl = ul.split("</ul>")[0];
        String[] strings = innerUl.split("</li>");
        for (int i = 0; i < strings.length - 1; i++) {
            String s = strings[i];
            Weather wea = new Weather();
            String str1 = s.split("<h1>")[1];
            String ieDate = str1.split("日")[0];

            Date date = new Date();
            String format = sdf.format(date);
            String month = format.split("月")[0];
            if (i == 0) {
                wea.setDate(month+"-"+ieDate);
            }else {
                if (Integer.parseInt(ieDate) > Integer.parseInt(strings[i-1].split("<h1>")[1].split("日")[0])) {
                    wea.setDate(month+"-"+ieDate);
                }else {
                    month = String.valueOf(Integer.parseInt(month) + 1);
                    wea.setDate(month+"-"+ieDate);
                }
            }

            String str2 = s.split("<p title=\"")[1];
            wea.setWeather(str2.split("\"")[0]);
            String str3 = s.split("<span>")[1];
            wea.setHigh(str3.split("</span>")[0]);
            String str4 = s.split("<i>")[1];
            wea.setLow(str4.split("℃</i>")[0]);
            String str5 = s.split("<span title=\"")[1];
            wea.setWind(str5.split("\"")[0]);
            wea.setCreateTime(sdf2.format(date));
            // System.out.println(wea);
            list.add(wea);
            wea = null;
        }
        return list;
    }
}

class Weather {
    private String date;
    private String weather;
    private String high;
    private String low;
    private String wind;
    private String createTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public Weather() {
        super();
    }

    @Override
    public String toString() {
        return "概况 [日期=" + date + ", 天气=" + weather + ", 最高温度=" + high + ", 最低温度=" + low + ", 风向=" + wind + ", 更新时间=" + createTime + "]";
    }

}
