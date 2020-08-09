package org.kish2020.utils.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.kish2020.MainLogger;
import org.kish2020.entity.LunchMenu;
import org.kish2020.entity.SimplePost;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class KishWebParser {
    public static final String ROOT_URL = "http://www.hanoischool.net/";

    public static Document generateUrl(Document doc){
        // src attribute 가 있는 엘리먼트들을 선택
        try {
            Elements elems = doc.select("[src]");
            for (Element elem : elems) {
                if (!elem.attr("src").equals(elem.attr("abs:src"))) {
                    elem.attr("src", "abs:src");
                }
            }

            // href attribute 가 있는 엘리먼트들을 선택
            elems = doc.select("[href]");
            for (Element elem : elems) {
                if (!elem.attr("href").equals(elem.attr("abs:href"))) {
                    String attr = elem.attr("abs:href");
                    if(attr.contains("dfname=")){
                        String[] split = attr.split("dfname=");
                        /*다운로드 경로는 EUC-KR으로 인코드 해주지 않을 경우 404발생*/
                        attr = split[0] + "dfname=" + URLEncoder.encode(split[1], "EUC-KR");
                    }
                    elem.attr("href", attr);
                }
            }
        } catch (UnsupportedEncodingException e){
            MainLogger.error("generateUrl에서 발생한 오류", e);
        }
        return doc;
    }

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

    public static ArrayList<SimplePost> parseMenu(String id){
        return parseMenu(id, "1");
    }

    public static ArrayList<SimplePost> parseMenu(String id, String page){
        ArrayList<SimplePost> list = new ArrayList<>();
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

                list.add(new SimplePost(postUrl, postId, title, author, postDate, attachmentIconUrl));
            }));
        } catch (IOException e) {
            MainLogger.error("", e);
        }
        return list;
    }

    public static String generatePostToNormal(String fullUrl){
        try {
            Document doc = Jsoup.connect(fullUrl).get();
            generateUrl(doc);
            Elements elements = doc.select("link");
            for (Element element : elements) {
                if("http://www.hanoischool.net/html/css/style.css?ver=1.0.0.0.0".equals(element.attr("href")))
                    element.remove();
            }
            doc.head().append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            doc.select("#nav").forEach((Node::remove));
            doc.select("#skipnavigation").forEach(Node::remove);
            doc.select("#header").forEach((Node::remove));
            doc.select("#sub_visual").forEach((Node::remove));
            doc.select("#footer").forEach(Node::remove);
            doc.select("#sub_left").forEach(Node::remove);
            doc.select(".h_board table").forEach(Node::remove);
            doc.select(".h_btn_area2").forEach(Node::remove);
            doc.select(".table_b5").forEach(Node::remove);
            return doc.toString();
            /*return "<html lang=\"ko\">\n" +
                    "\t<head>\n" +
                    "\n" +
                    "\t\t<meta charset=\"euc-kr\">\n" +
                    "\t\t<meta name=\"robots\" content=\"all\" />\n" +
                    "\t\t<meta name=\"robots\" content=\"index, follow\" />\n" +
                    "\t\t<meta name=\"content-language\" content=\"kr\" />\n" +
                    "\t\t<meta name=\"build\" content=\"\" />\n" +
                    "</head><body>" + doc.select("style") + doc.select("script") + doc.select(".h_body").get(0).html() + "</body></html>";
*/        } catch (IOException e) {
            MainLogger.error("", e);
            return "";
        }
    }
}
