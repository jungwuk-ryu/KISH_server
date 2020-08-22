package org.kish2020.utils;

import io.github.bangjunyoung.KoreanChar;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.kish2020.MainLogger;
import org.kish2020.entity.Post;
import org.kish2020.entity.PostInfo;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class Utils {
    public static String makeChoseongSentence(String sentence){
        StringBuilder choseongSb = new StringBuilder();
        for(char c : sentence.toCharArray()){
            if(KoreanChar.isSyllable(c)) c = KoreanChar.getCompatChoseong(c);
            choseongSb.append(c);
        }
        return choseongSb.toString();
    }

    public static ArrayList<String> getContentTokens(String postContent){
        if(postContent == null || postContent.isEmpty()) return null;
        ArrayList<String> list = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(postContent);
        while (st.hasMoreTokens()){
            list.add(st.nextToken());
        }
        return list;
    }

    public static LinkedHashMap<String, Integer> getContentTokenMap(String postContent){
        if(postContent == null || postContent.isEmpty()) return null;
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        StringTokenizer st = new StringTokenizer(postContent);
        while (st.hasMoreTokens()){
            String token = st.nextToken();
            map.put(token, map.getOrDefault(token, 0) + 1);
        }
        return map;
    }

    public static void addPostToKeyword(String postKey, String title, LinkedHashMap<String, String> attachmentUrlMap, LinkedHashMap<String, HashMap<String, Long>> targetKeywordMap, LinkedHashMap<String, Integer> tokenMap){
        if(tokenMap == null){
            MainLogger.error(postKey + "의 tokenMap이 null입니다.");
            MainLogger.error("해당 게시물의 본문이 비어있음을 뜻할 수 있습니다.");
            return;
        }
        tokenMap = (LinkedHashMap<String, Integer>) tokenMap.clone();
        tokenMap.putAll(Utils.getContentTokenMap(title));
        for(String attachmentName : attachmentUrlMap.keySet()) {
            tokenMap.putAll(Utils.getContentTokenMap(attachmentName));
        }
        for(String key : tokenMap.keySet()){
            long count = tokenMap.get(key);
            HashMap<String, Long> keywordData = targetKeywordMap.getOrDefault(key, new HashMap<>());
            keywordData.put(postKey, count);
            String choseongKeywordData = makeChoseongSentence(key);
            HashMap<String, Long> choseongMap = targetKeywordMap.getOrDefault(choseongKeywordData, new HashMap<>());
            choseongMap.put(postKey, count);

            targetKeywordMap.put(key, keywordData);
            targetKeywordMap.put(choseongKeywordData, choseongMap);
        }
        MainLogger.info(postKey + "의 " + tokenMap.size() + "개의 토큰이 저장됨");
    }

    /* 리팩토링 필요 */
    public static void removePostFromKeyword(String postKey, String title, LinkedHashMap<String, String> attachmentUrlMap, LinkedHashMap<String, HashMap<String, Long>> targetKeywordMap, LinkedHashMap<String, Integer> tokenMap){
        tokenMap = (LinkedHashMap<String, Integer>) tokenMap.clone();
        tokenMap.putAll(Utils.getContentTokenMap(title));
        for(String attachmentName : attachmentUrlMap.keySet()) {
            tokenMap.putAll(Utils.getContentTokenMap(attachmentName));
        }
        for(String key : tokenMap.keySet()){
            HashMap<String, Long> keywordData = targetKeywordMap.getOrDefault(key, new HashMap<>());
            keywordData.remove(postKey);
            String choseongKeywordData = makeChoseongSentence(key);
            HashMap<String, Long> choseongMap = targetKeywordMap.getOrDefault(choseongKeywordData, new HashMap<>());
            choseongMap.remove(postKey);

            targetKeywordMap.put(key, keywordData);
            targetKeywordMap.put(choseongKeywordData, choseongMap);
        }
        MainLogger.info(postKey + "의 " + tokenMap.size() + "개의 토큰이 제거됨");
    }

    // TODO : 검색결과 향상 및 최적화 매우 필요 ( 약 20 ~ 90ms 소요 )
    public static ArrayList<String> search(LinkedHashMap<String, HashMap<String, Long>> keywordMap, HashMap<String, PostInfo> postInfoMap, String searchSentence, int index){
        //long start = System.currentTimeMillis();
        //임시
        HashMap<String, Double> srcMap = new HashMap<>();
        String[] keywords = searchSentence.split(" ");
        List<String> tokens = new ArrayList<>();
        for(String token : keywords){
            token = token.trim();
            tokens.add(token);
        }

        String[] arrayTokens = tokens.toArray(new String[0]);
        String noSpace = StringUtils.deleteWhitespace(searchSentence);
        HashSet<Character> noSpaceCharSet = new HashSet<>();
        char[] noSpaceChars = noSpace.toCharArray();
        for(char c : noSpaceChars) noSpaceCharSet.add(c);
        postInfoMap.forEach( (k, v) -> {
            int matchCount = 0;
            String noSpaceTitle = StringUtils.deleteWhitespace(v.getTitle());
            for(char c : noSpaceCharSet) {
                matchCount += StringUtils.countMatches(noSpaceTitle, c);
            }
            srcMap.put(k, 0.4 * matchCount);
        });
        for(String keyword : keywordMap.keySet()){
            for(String token : arrayTokens){
                if(keyword.length() > 1){
                    if(keyword.equals(token)){
                        for (String postKey : keywordMap.get(keyword).keySet()) {
                            srcMap.put(postKey, srcMap.getOrDefault(postKey, 0D) + 1.5);
                        }
                        continue;
                    }
                    if(keyword.contains(token)) {
                        for (String postKey : keywordMap.get(keyword).keySet()) {
                            srcMap.put(postKey, srcMap.getOrDefault(postKey, 0D) + 1);
                        }
                        continue;
                    }
                    if(token.contains(keyword)){
                        for (String postKey : keywordMap.get(keyword).keySet()) {
                            srcMap.put(postKey, srcMap.getOrDefault(postKey, 0D) + 0.7);
                        }
                    }else{
                        if(token.length() > 4){
                            int matchCount = StringUtils.countMatches(token, keyword);
                            if(matchCount > (token.length() / 2)){
                                for (String postKey : keywordMap.get(keyword).keySet()) {
                                    srcMap.put(postKey, srcMap.getOrDefault(postKey, 0D) + 0.05 * matchCount);
                                }
                            }
                        }
                    }
                }else{
                    if(token.length() == 1){
                        if(keyword.equals(token)){
                            for (String postKey : keywordMap.get(keyword).keySet()) {
                                srcMap.put(postKey, srcMap.getOrDefault(postKey, 0D) + 0.2);
                            }
                        }
                    }
                }
            }
        }

        ArrayList<String> keyList = new ArrayList<>(srcMap.keySet());
        Collections.sort(keyList, (o1, o2) -> (srcMap.get(o2).compareTo(srcMap.get(o1))));
        ArrayList<String> resultKeyList = new ArrayList<>();
        int max = keyList.size();
        int maxPage = max / 20;
        //index는 0부터 시작
        if(maxPage >= index) {
            for (int i = 20 * index; i < 20 * index + 20; i++) {
                if(max <= i) continue;
                resultKeyList.add(keyList.get(i));
            }
        }

        /*long stop = System.currentTimeMillis();
        MainLogger.warn(" 소요 " + (stop - start));*/
        return resultKeyList;
    }

    /**
     * post/posts/에 저장된 게시물인지 확인합니다.
     */
    public static boolean isSavedPost(String postKey){
        String[] tokens = postKey.split(",");
        return isSavedPost(tokens[0], tokens[1]);
    }

    public static boolean isSavedPost(String menuId, String postId){
        File file = new File("post/posts/" + menuId + "/" + postId + ".json");
        return file.exists();
    }

    /**
     * ./post/posts 에 저장된 게시물들을 postInfo로 가져옵니다.
     */
    public static LinkedHashMap<String, PostInfo> getAllPostInfoFromPost(){
        LinkedHashMap<String, PostInfo> map = new LinkedHashMap<>();
        File[] indexes = (new File("post/posts")).listFiles();
        for(File indexFolder : indexes){
            if(indexFolder.isDirectory()){
                String menuID = indexFolder.getName();
                File[] files = indexFolder.listFiles();
                for(File postFile : files){
                    String[] tokens = postFile.getName().split("[.]");
                    if(tokens.length < 2){
                        MainLogger.info("post id 분석 실패 : " + postFile.getName());
                        continue;
                    }
                    String PostID = tokens[0];
                    Post post = new Post(menuID, PostID, false);
                    PostInfo postInfo = new PostInfo(post);
                    map.put(menuID + "," + PostID, postInfo);
                }
            }
        }
        return map;
    }

    public static Document postToDocument(Post post){
        Document doc = Jsoup.parse(post.getFullHtml());
        doc.setBaseUri("http://www.hanoischool.net/?menu_no=" + post.getMenuId() + "&board_mode=view&bno=" + post.getPostId());
        return doc;
    }

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
}
