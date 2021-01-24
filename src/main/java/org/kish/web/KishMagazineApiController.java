package org.kish.web;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.kish.KishServer;
import org.kish.MainLogger;
import org.kish.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/api/kish-magazine")
public class KishMagazineApiController {
    public static String DIR_NAME = "magazine";
    private static final Gson gson = new Gson();
    private final File resourceFolder;
    @Autowired
    private CacheManager cacheManager;


    public KishMagazineApiController(){
        this.resourceFolder = new File(KishServer.RESOURCE_PATH.getAbsolutePath() + File.separator + DIR_NAME);

        Timer scheduler = new Timer();
        scheduler.schedule(new TimerTask() {
            @Override
            public void run() {
                updateArticles();
            }
        }, 1000 * 60 * 30, 1000 * 60 * 30);     // 30분 마다 반복
    }

    public void updateArticles(){
        Cache cache = cacheManager.getCache("articleList");
        if(cache != null)
            cache.clear();

        File originalArticlesPath = new File("kish magazine");
        if(!originalArticlesPath.exists()) originalArticlesPath.mkdirs();

        ArrayList<File> docs = Utils.getAllFiles(originalArticlesPath, new ArrayList<>());
        for (File article : docs) {
            String pdfFullPath = article.getAbsolutePath();
            String articleExtension = FilenameUtils.getExtension(article.getName());

            pdfFullPath = pdfFullPath
                    .replace(originalArticlesPath.getAbsolutePath(), resourceFolder.getAbsolutePath())
                    .replace("." + articleExtension, ".pdf")
                    .replace("&", ", ");

            File pdfFile = new File(pdfFullPath);
            if(pdfFile.exists()) continue;

            switch (articleExtension) {
                case "docx":
                case "png":
                case "jpg":
                    MainLogger.info("매거진 기사 추가 중 : " + article.getName());
                    KishServer.jodManager.fileToPDF(article, pdfFile);
                    break;

                case "pdf":
                    try {
                        MainLogger.info("매거진 기사 추가 중 : " + article.getName());
                        FileUtils.copyFile(article, pdfFile);
                    } catch (IOException e) {
                        MainLogger.error(e);
                    }
                    break;

            }
        }
    }

    @Cacheable(value = "articleList", key = "#path")
    @GetMapping(value = "getArticleList")
    @ResponseBody
    public String getArticleList(@RequestParam(required = false, defaultValue = "") String path){
        ArrayList<File> subfile = new ArrayList<>();
        ArrayList<Object> rs = new ArrayList<>();
        File file = new File(resourceFolder.getAbsolutePath() + File.separator +  path);
        MainLogger.info(resourceFolder.getAbsolutePath() + File.separator +  path);
        if(!file.exists() || file.isFile()){
            MainLogger.error(path);
            return "[]";
        }

        String[] tmp = file.list();
        Arrays.sort(tmp);
        for (String subfileName : tmp) {
            subfile.add(new File(resourceFolder.getAbsolutePath() + File.separator + subfileName));
        }

        for (File subFile : file.listFiles()) {
            if(subFile.isDirectory()) rs.add( new Category(subFile));
            else rs.add(new Article(subFile));
        }

        return gson.toJson(rs);
    }


    @Getter
    @Setter
    class Category {
        private final String type = "dir";
        private String name;
        private String path;
        private ArrayList<String> subfileName = new ArrayList<>();

        public Category(File file){
            this.name = file.getName().replace("^", "\n");
            this.path = file.getAbsolutePath().substring(resourceFolder.getAbsolutePath().length());
            //this.url = URLEncoder.encode(this.url,"UTF-8");
            //this.url =  file.getAbsolutePath().substring(resourcePath.getAbsolutePath().length());

            for (File subfile : file.listFiles()) {
                if(subfile.isFile()){
                    String fileName = subfile.getName();
                    if(FilenameUtils.getExtension(fileName).equals("pdf"))
                        subfileName.add(FilenameUtils.getBaseName(fileName));
                }
            }
        }
    }

    @Getter
    class Article{
        private final String type = "file";
        private String name;
        private String url;

        public Article(File file){
            this.name = FilenameUtils.getBaseName(file.getName()).replace("^", "\n");
            this.url = "/resource/" + DIR_NAME + file.getAbsolutePath().substring(resourceFolder.getAbsolutePath().length());
                /*this.url = URLEncoder
                        .encode(this.url,"UTF-8")
                        .replace("+", "%20")
                        .replace("%2F", "/");
                this.url = "/resource/" + DIR_NAME + this.url;*/
        }
    }
}
