package org.kish2020.utils.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.kish2020.MainLogger;
import org.kish2020.entity.LunchMenu;
import org.kish2020.entity.Post;

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

    /**
     * Menu는 아래와 같은 형식입니다.
     * <tr class="h_line_dot">
			<td>
			5
			</td>
			<td class="h_left"><a href="default.asp?board_mode=view&menu_no=25&bno=14&issearch=&keyword=&keyfield=&page=1" class="no1">2020학년도 중등 7월 전편입학 문의에 대한 기본 안내 사항</a>&nbsp;</td>
			<td>중등학적</td>
			<td>2020-06-04</td>
			<td>222</td>
			<td><img src='http://upload70.withschool.co.kr/image/icon_PDF.gif' alt='첨부' border='0' /></td>
		</tr>**/

    public static ArrayList<Post> parseMenu(String id){
        return parseMenu(id, "1");
    }

    public static ArrayList<Post> parseMenu(String id, String page){
        ArrayList<Post> list = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(ROOT_URL + "?menu_no=" + id).get();
            Elements items = doc.select(".h_line_dot");
            items.forEach((element -> {
                Elements elements = items.select("td");
                String postId = elements.get(0).text();
                String title = elements.get(1).text();
                String author = elements.get(2).text();
                String postDate = elements.get(3).text();

                String attachmentIconUrl = items.select("img").attr("src");
                String postUrl = ROOT_URL + items.select("a").attr("href");

                list.add(new Post(postUrl, postId, title, author, postDate, attachmentIconUrl));
            }));
        } catch (IOException e) {
            MainLogger.error("", e);
        }
        return list;
    }
}
