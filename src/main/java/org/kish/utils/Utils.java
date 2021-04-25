package org.kish.utils;

import io.github.bangjunyoung.KoreanChar;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.kish.MainLogger;
import org.kish.MenuID;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@SuppressWarnings("unchecked")
public class Utils {
    public static String makeChoseongSentence(String sentence){
        StringBuilder choseongSb = new StringBuilder();
        for(char c : sentence.toCharArray()){
            if(KoreanChar.isSyllable(c)) c = KoreanChar.getCompatChoseong(c);
            choseongSb.append(c);
        }
        return choseongSb.toString();
    }

    @Deprecated
    public static ArrayList<String> getContentTokens(String postContent){
        if(postContent == null || postContent.isEmpty()) return null;
        ArrayList<String> list = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(postContent);
        while (st.hasMoreTokens()){
            list.add(st.nextToken());
        }
        return list;
    }

    public static MenuID getMenuFromID(String id){
        for(MenuID menu : MenuID.values()){
            if(menu.id.equals(id)) return menu;
        }
        throw new IllegalArgumentException("아이디 " + id + "에 해당하는 메뉴가 없습니다.");
    }

    public static MenuID getMenuFromName(String name){
        for(MenuID menu : MenuID.values()){
            if(menu.name.equals(name)) return menu;
        }
        throw new IllegalArgumentException("아이디 " + name + "에 해당하는 메뉴가 없습니다.");
    }


    public static String postUrlGenerator(int menu, int id){
        return "http://www.hanoischool.net/?menu_no=" + menu +
                "&board_mode=view&bno=" + id;
    }

    /*public static Document postToDocument(Post post){
        Document doc = Jsoup.parse(post.getFullHtml());
        doc.setBaseUri("http://www.hanoischool.net/?menu_no=" + post.getMenuId() + "&board_mode=view&bno=" + post.getPostId());
        return doc;
    }*/

    /**
     * <p>Document내 주소들을 절대 경로로 변환합니다.</p>
     * <p>또한 다운로드 주소는 EUC-KR으로 인코딩합니다.</p>
     * <p>https://stove99.tistory.com/129 을 이용하였습니다.</p>
     */
    public static Document generateUrl(Document doc){
        if(doc.baseUri().isEmpty()){
            throw new IllegalArgumentException("주어진 Document에 baseUrl이 설정되어 있지 않습니다.");
        }
        // src attribute 가 있는 엘리먼트들을 선택
        try {
            Elements elems = doc.select("[src]");
            for (Element elem : elems) {
                if (!elem.attr("src").equals(elem.absUrl("src"))) {
                    elem.attr("src", elem.absUrl("src"));
                }
            }

            // href attribute 가 있는 엘리먼트들을 선택
            elems = doc.select("[href]");
            for (Element elem : elems) {
                if (!elem.attr("href").equals(elem.absUrl("href"))) {
                    String attr = elem.absUrl("href");
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

    public static ArrayList<File> getAllFiles(File path, ArrayList<File> list){
        File[] files = path.listFiles();
        if(files == null) return list;

        for (File subfile : files) {
            if(subfile.isDirectory()) getAllFiles(subfile, list);
            else list.add(subfile);
        }

        return list;
    }
}
