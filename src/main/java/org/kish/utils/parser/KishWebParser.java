package org.kish.utils.parser;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.kish.MainLogger;
import org.kish.entity.LunchMenu;
import org.kish.entity.Post;
import org.kish.entity.SimplePost;
import org.kish.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

public class KishWebParser {
    public static final String ROOT_URL = "http://www.hanoischool.net/";

    public static ArrayList<LunchMenu> parseLunch(){
        return parseLunch("");
    }

    public static ArrayList<LunchMenu> parseLunch(String changeDate){
        ArrayList<LunchMenu> list = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(ROOT_URL + "?menu_no=47&ChangeDate=" + changeDate).get();
            Elements items = doc.select(".h_line_dot");
            items.addAll(doc.select(".h_line_color"));

            String temp = doc.select("caption").get(0).text();
            String[] tempArray = temp.split("년");
            String yyyymm = tempArray[0].trim() + "-" + tempArray[1].split("월")[0].trim() + "-";

            items.forEach((element -> {
                String date = yyyymm + (element.select(".h_view_name").text()).split("일")[0].trim();
                Elements bodyElements = element.select(".mm_to");
                String menu = "정보 없음";
                String salt = "정보 없음";
                String imageUrl = "";
                if(bodyElements.size() > 0) {
                    Elements info = bodyElements.select("p");
                    Elements elementImg = bodyElements.select("img");
                    menu = info.get(0).text();
                    if (info.size() > 1) salt = info.get(1).text();
                    if (elementImg.size() > 0) imageUrl = elementImg.get(0).attr("src");
                }
                list.add(new LunchMenu(date, menu, salt, imageUrl));
            }));
        } catch (IOException e) {
            MainLogger.error(e);
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
		</tr>
     */

    public static ArrayList<SimplePost> parseMenu(String id){
        return parseMenu(id, "1");
    }

    public static ArrayList<SimplePost> parseMenu(String menuId, String page){
        ArrayList<SimplePost> list = new ArrayList<>();
        String url = ROOT_URL + "?menu_no=" + menuId + "&page=" + page;
        try {
            Document doc = Jsoup.connect(url).get();
            Elements items = doc.select(".h_line_dot");
            items.addAll(doc.select(".h_line_color"));
            if(items.size() > 15){
                MainLogger.warn("다음 페이지의 게시물 개수가 옳바르지 않습니다.");
                MainLogger.warn(url);
            }
            items.forEach((element -> {
                Elements elements = element.select("td");
                Element aElement = element.select("a").get(0);
                String postId = aElement.attr("href").split("bno=")[1].split("&")[0];
                String no = elements.get(0).text().trim();
                if(no.equals("공지")) return;
                try {
                    Integer.parseInt(no);
                }catch (NumberFormatException e){
                    MainLogger.error(menuId + "의" + postId + "를 가져오는 도중 잘못된 no값을 확인하였습니다. skip...", e);
                    return;
                }
                String title = elements.get(1).text();
                String author = elements.get(2).text();
                String postDate = elements.get(3).text();

                String attachmentIconUrl = elements.select("img").attr("src");
                String postUrl = ROOT_URL + elements.select("a").attr("href");

                list.add(
                        new SimplePost(postUrl, Integer.parseInt(menuId), Integer.parseInt(postId), title, author, postDate, attachmentIconUrl));
            }));
        } catch (IOException | ArrayIndexOutOfBoundsException | NullPointerException e) {
            MainLogger.error("parseMenu 작업중 오류 발생 : " + url, e);
        }
        return list;
    }

    public static String generatePostToNormal(Document doc){
        doc = doc.clone();
        Utils.generateUrl(doc);
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
        doc.select(".fr").forEach(Node::remove);
        doc.select(".sub_title").forEach(Node::remove);
        doc.select(".h_board").get(1).remove();
        doc.select("caption").forEach(Node::remove);
        Elements trElements = doc.select(".h_board").get(0).select("tr");
        for(int i = 0; i < 4; i++) {
            trElements.get(i).remove();
        }
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
*/
    }

    public static Post parsePost(int menu, int postID){
        Post post = new Post(menu, postID);
        try {
            Document doc = Jsoup.connect("http://hanoischool.net/default.asp?menu_no=" + menu + "&board_mode=view&bno=" + postID).get();
            Elements elements = doc.select(".h_board").get(0).getAllElements();
            Elements thElements = elements.select("th");
            Elements titleElements = elements.select(".h_view_title");

            post.setMenu(menu);
            post.setId(postID);
            //(doc.html());
            post.setTitle(thElements.get(0).text());
            post.setAuthor(titleElements.get(0).text());
            post.setPost_date(titleElements.get(1).text().split(" ")[0]);
            post.setContent(KishWebParser.getPostRawContent(doc));

            //첨부파일
            Utils.generateUrl(doc);
            Elements attachmentElements = titleElements.get(2).select("a");
            post.setHasAttachments(attachmentElements.size());
            /*if(attachmentElements.size() < 1){
                post.setHasAttachment(false);
            }else{
                post.setHasAttachment(true);
                attachmentElements.forEach(element -> {
                    post.addAttachmentUrl(element.text(), element.attr("href"));
                });
            }*/
        } catch (IOException e) {
            MainLogger.error("postKey : " + menu + "," + postID, e);
            return null;
        }
        return post;
    }

    public static String getPostRawContent(Document doc){
        doc = doc.clone();
        Elements elements = doc.select("#bbs_view_contents");
        Element content = elements.get(0);
        content.select("p").forEach(element -> {
            /*if(element.tagName().equals("p") || element.tagName().equals("br") || element.tagName().equals("span")){
                if(element.select("br").size() > 0) element.after("\\n");
            }*/
            element.after("\\n");
        });
        return StringUtils.replace(content.text(), "\\n", "\n");
    }

/*    public static SchoolCalendar getSchoolCalendar(){
        Calendar calendar = Calendar.getInstance();
        return getSchoolCalendar(Integer.toString(calendar.get(Calendar.YEAR)), Integer.toString(calendar.get(Calendar.MONTH) + 1));
    }*/

    public static LinkedHashMap<Calendar, ArrayList<String>> getPlansFromServer(Calendar targetDate){
        String strTargetDate = targetDate.get(Calendar.YEAR) + "-" + (targetDate.get(Calendar.MONTH) + 1) + "-1";
        LinkedHashMap<Calendar, ArrayList<String>> rs = new LinkedHashMap<>();

        try {
            Document doc = Jsoup.connect("http://www.hanoischool.net/?menu_no=41&ChangeDate=" + strTargetDate).get();
            //String thisYear = doc.select(".design_font").get(0).text();

            Elements dates = doc.select(".defTable2").get(0).select(".date");
            for (Element element : dates) {
                int day;
                try {
                    day = Integer.parseInt(element.text());
                } catch (NumberFormatException e){
                    continue;
                }

                Elements parentElements = element.parent().getAllElements().select("div");
                ArrayList<String> plans = new ArrayList<>();

                for(int i = 1; i < parentElements.size(); i ++){
                    String plan = parentElements.get(i).text().trim();
                    if(plan.isEmpty()) continue;

                    plans.add(plan);
                }

                Calendar date = Calendar.getInstance();
                date.set(Calendar.YEAR, targetDate.get(Calendar.YEAR));
                date.set(Calendar.MONTH, targetDate.get(Calendar.MONTH));
                date.set(Calendar.DATE, day);
                rs.put(date, plans);
            }
            return rs;
        } catch (IOException e) {
            MainLogger.error(e);
            return null;
        }
    }

}
