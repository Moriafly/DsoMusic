package com.dirror.music.music.qq;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QQMusic {

    /**
     * 解析 QQ 音乐 VIP 歌曲
     * @param
     * @return
     */
    public static String parse(String songmid){
        // String songmid = url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
        String  filename = "C400" + songmid + ".m4a";
        String guid =Math.abs((long)(Math.random() * 2147483647) * (int)(System.currentTimeMillis() * 1000) % 10000000000L)+"";

        Map<String, String> data = new HashMap<>();
        data.put("format", "json");
        data.put("cid", "205361747");
        data.put("outCharset", "utf8");
        data.put("uin", "0");
        data.put("songmid", songmid);
        data.put("filename", filename);
        data.put("guid", guid);


        String audioUrl = "";
        try {
            String a = "https://u.y.qq.com/cgi-bin/musicu.fcg?-=getplaysongvkey05137740976859173&g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=0&data=%7B%22req%22%3A%7B%22module%22%3A%22CDN.SrfCdnDispatchServer%22%2C%22method%22%3A%22GetCdnDispatch%22%2C%22param%22%3A%7B%22guid%22%3A%22953482270%22%2C%22calltype%22%3A0%2C%22userip%22%3A%22%22%7D%7D%2C%22req_0%22%3A%7B%22module%22%3A%22vkey.GetVkeyServer%22%2C%22method%22%3A%22CgiGetVkey%22%2C%22param%22%3A%7B%22guid%22%3A%22953482270%22%2C%22songmid%22%3A%5B%22{0}%22%5D%2C%22songtype%22%3A%5B0%5D%2C%22uin%22%3A%220%22%2C%22loginflag%22%3A1%2C%22platform%22%3A%2220%22%7D%7D%2C%22comm%22%3A%7B%22uin%22%3A0%2C%22format%22%3A%22json%22%2C%22ct%22%3A24%2C%22cv%22%3A0%7D%7D\".format(songmid)";
            // String vkeyContent = Jsoup.connect("https://u.y.qq.com/cgi-bin/musicu.fcg").ignoreContentType(true).data(data).get().getElementsByTag("body").text();
            String vkeyContent = Jsoup.connect("https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg").ignoreContentType(true).data(data).get().getElementsByTag("body").text();
            JSONObject json = new JSONObject(vkeyContent);
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonData = (JSONObject) json.get("data");
            JSONObject jsonVkey = (JSONObject) jsonData.getJSONArray("items").get(0);
            String vkey = jsonVkey.get("vkey").toString();

            audioUrl = "http://dl.stream.qqmusic.qq.com/"+filename+"?vkey="+vkey+"&guid="+guid+"&uin=0&fromtag=66";
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return audioUrl;
    }

}
