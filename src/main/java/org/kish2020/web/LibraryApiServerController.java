package org.kish2020.web;

import org.json.simple.JSONObject;
import org.kish2020.DataBase.DataBase;
import org.kish2020.Kish2020Server;
import org.kish2020.MainLogger;
import org.kish2020.utils.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
@RequestMapping("/api/library")
public class LibraryApiServerController {
    private Kish2020Server main;
    private DataBase db;    // TODO : 책 목록 저장 및 불러오기

    public LibraryApiServerController(Kish2020Server main){
        this.main = main;
        this.db = main.getMainDataBase();
    }

    /**
     * 해당 ID가 이미 존재하는 ID인지 확인합니다
     * @param seq 회원 ID
     * @param id 입력한 ID
     * @param pwd 입력한 비밀번호
     * @param ck 재입력 비밀번호
     * */

    @RequestMapping(value = "/checkID", method = RequestMethod.POST)    //TODO : 테스트
    public @ResponseBody String checkID(@RequestParam String seq, @RequestParam String id, @RequestParam String pwd, @RequestParam(required = false, defaultValue = "") String ck){
        String parameters;
        /*ID_EXIST_CHECK 값이 무엇을 의미하는지는 모르겠습니다만, 회원가입 중복확인시 0을 사용합니다 --> 1도 사용되네요..?*/
        parameters = "ID_EXIST_CHECK=" + 0;
        parameters += "&MEMBER_REG_SEQ=" + seq;
        parameters += "&MEMBER_REG_ID=" + id;
        parameters += "&MEMBER_REG_PWD=" + pwd;
        parameters += "&MEMBER_PWD_CK=" + ck;

        /*
        * message : 결과 메세지
        * result가 0이면 사용 가능 아이디
        */
        JSONObject response = WebUtils.postRequest("http://lib.hanoischool.net:81/front/member/checkID", WebUtils.ContentType.FORM, parameters);
        return response.toJSONString();
    }

    /**
     * KISH 학생 여부를 확인합니다
     * @param name 본명
     * @param seq 도서관 회원 ID (대출증 id)*/

    @RequestMapping(value = "/isMember", method = RequestMethod.POST)   // TODO : 테스트
    public @ResponseBody String isMember(@RequestParam String name, @RequestParam String seq){
        try {
            name = URLEncoder.encode(name,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            MainLogger.error("name 인코딩중 오류가 발생하였습니다.", e);
            return "{\"message\":\"요청을 처리하는도중 오류가 발생하였습니다.\",\"result\":500}";
        }
        String parameters = "MEMBER_NM=" + name + "&MEMBER_SEQ=" + seq;
        JSONObject result = WebUtils.postRequest("http://lib.hanoischool.net:81/front/member/formRegister", WebUtils.ContentType.FORM, parameters);
        return result.toJSONString();
    }


}
