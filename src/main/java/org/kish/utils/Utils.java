package org.kish.utils;

import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.kish.KishServer;
import org.kish.MainLogger;
import org.kish.MenuID;
import org.kish.entity.Post;
import org.kish.entity.SimplePost;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@SuppressWarnings("unchecked")
public class Utils {
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

    public static void downloadImgs(Document doc) {
        for (Element element : doc.select("img")) {
            try {
                File file = new java.io.File("resource/downloaded/" + element.attr("src").split(".net/")[1]);
                if (file.exists()) continue;
                File dir = new File(FilenameUtils.getFullPath(file.getAbsolutePath()));
                dir.mkdirs();

                Connection.Response resultImageResponse = Jsoup.connect(element.attr("src")).ignoreContentType(true).execute();
                FileOutputStream out = new FileOutputStream(file);
                out.write(resultImageResponse.bodyAsBytes());
                out.close();

                MainLogger.warn("파일 다운로드 중 : " + file.getPath());
            } catch (Exception e) {
                MainLogger.warn(e);
            }
        }
    }

    public static void enhanceImgTags(Document doc) {
        for (Element element : doc.select("img")) {
            element.attr("style", element.attr("style") + "max-width:100%; height:auto;");
        }
    }

    public static void replaceImgPaths(Document doc, String replacement) {
        for (Element element : doc.select("img")) {
            element.attr("src", element.attr("src").replace(KishServer.KISH_WEB_ROOT + "/", replacement));
        }
    }

    public static String getMenuTitle(String id) {
        for (MenuID value : MenuID.values()) {
            if (value.id.equals(id)) {
                return value.name;
            }
        }

        return "";
    }

    public static List<SimplePost> convertPostList2SimplePostList(List<Post> list) {
        ArrayList<SimplePost> result = new ArrayList<>();
        for (Post post : list) {
            result.add(new SimplePost(post));
        }
        return result;
    }
    
    public static List<File> getAllSubFiles(File parentDir) {
        ArrayList<File> list = new ArrayList<>();

        if(parentDir.isDirectory()) {
            for (File file : parentDir.listFiles()) {
                if (file.isDirectory()) list.addAll(getAllSubFiles(file));
                else list.add(file);
            }
        }

        return list;
    }

    @SneakyThrows
    public static void extractFirstImgFromPdf(PDDocument document, File imgPath) {
        int limit = 0;

        for (PDPage page : document.getDocumentCatalog().getPages()) {
            if (limit > 0) return;
            PDResources pdResources = page.getResources();
            for (COSName xObjectName : pdResources.getXObjectNames()) {
                PDXObject xObject = pdResources.getXObject(xObjectName);
                if (xObject instanceof PDImageXObject) {
                    ImageIO.write(((PDImageXObject) xObject).getImage(), "png", imgPath);
                    limit ++;
                }
            }
        }
    }
}
