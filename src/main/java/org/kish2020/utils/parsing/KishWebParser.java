package org.kish2020.utils.parsing;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.kish2020.MainLogger;
import org.kish2020.entity.LunchMenu;

import java.io.IOException;
import java.util.ArrayList;

public class KishWebParser {
    public static final String ROOT_URL = "http://www.hanoischool.net/";

    public static ArrayList<LunchMenu> parseLunch(){
        return parseLunch("");
    }

    public static ArrayList<LunchMenu> parseLunch(String changeData){
        ArrayList<LunchMenu> list = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(ROOT_URL + "?menu_no=47&ChangeDate=" + changeData).get();
            Elements items = doc.select(".mm_to");
            items.forEach((element -> {
                Elements info = element.select("p");
                Elements elementImg = element.select("img");
                String menu = info.get(0).text();
                String salt = "정보 없음";
                String imageUrl = "";  // 염도, 이미지 url
                if(info.size() > 1){
                    salt = info.get(1).text();
                }
                if(elementImg.size() > 0){
                    imageUrl = elementImg.get(0).attr("src");
                }
                list.add(new LunchMenu(menu, salt, imageUrl));
            }));
        } catch (IOException e) {
            MainLogger.error("", e);
        }

        return list;
    }
}
