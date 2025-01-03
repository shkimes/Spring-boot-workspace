package com.kh.khtAcademy.controller;

import com.kh.khtAcademy.dto.User;
import com.kh.khtAcademy.service.UserProfileService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller //java에서 service와 html에 변수명을 주고 받는 공간
public class IndexController {
    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/") // - 엔드포인트 html 파일에 작성한 화면을 보여줄 주소
    public String index(Model model) { // model은 index.html에 자바로 작성한 값을 전달할 변수
        List<Map<String, Object>> users = userProfileService.getAllUsers();
        System.out.println("users 목록 확인  : "  + users);
        model.addAttribute("users", users);
        //model.addAttribute("message", "Hello World");
        return "index";
    }

    @GetMapping("/register")
    public String register() {
        // 회원가입을 진행할 때는 DB에서 가져올게 없기 때문에
        // Model model을 사용하지 않고
        // http://localhost:8080/register 로 이동했을 때
        // register.html 에 작성한 화면이 보일 수 있도록 설정
        return "register";
    }

    // DB에 값을 집어넣을 때는 PostMapping 사용하고 엔드포인트 form action에서 작성한
    // 주소를 엔드포인트로 지정
    /*
     *  Whitelabel Error Page Current request is not a multipart request
     *  1. html 파일에서 enctype="multipart/form-data" 데이터 설정을 했는지 확인
     *  2. springboot  파일업로드를 지원할 수 있도록 설정 했는지 application.properties / config.properties 확인
     * spring.servlet.multipart.enabled=true  -> 사진 업로드 허용
     * spring.servlet.multipart.max-file-size=10MB ->파일 하나당 최대 10MB
     * spring.servlet.multipart.max-request-size=10MB -> 한번에 업로드 가능한 모든 파일에 총합 크기
     * */
    @PostMapping("/register-success")
    public String registerSuccess( @RequestParam("username")   String username,
                                   @RequestParam("email")    String email,
                                   @RequestParam("birthdate")@DateTimeFormat(pattern = "yyyy-MM-dd") Date birthdate,
                                   @RequestParam("accountBalance")   String accountBalance,
                                   @RequestParam("gender")   String gender,
                                   @RequestParam("hobbies")   String hobbies,
                                   @RequestParam("profileImagePath")   MultipartFile profileImagePath,
                                   Model model) {

        userProfileService.insertUser(username, email, birthdate, accountBalance, gender, hobbies, profileImagePath);

        model.addAttribute("msg", "회원가입이 성공적으로 완료되었습니다.");
        return "success"; //회원가입이 무사히 완료될 경우 success 페이지로 이동
    }


    /*
    @PostMapping("/register-success")
    public String registerSuccess(@ModelAttribute("user") User user, Model model) {
        userProfileService.insertUser(user);
        model.addAttribute("msg", "회원가입이 성공적으로 완료되었습니다.");
        return "success"; //회원가입이 무사히 완료될 경우 success 페이지로 이동
    }
    * */

    // get DB에서 값을 가져와 조회하거나 또는 endpoint를 통해 html 화면 보여주기 위해 작성
    @GetMapping("/find-username") // endpoint = api = /find-username
    public String findByUsername(){
        return "find-username";
    }

    // 이메일로 이름을 찾은 결과 find-username-result 페이지에서 보여주기
    @GetMapping("/find-username-result") // endpoint = api = /find-username-result
    public String findByUsername(@RequestParam("email")String email,
                                 Model model){
        String username = userProfileService.findByUsername(email);
        model.addAttribute("username", username);
        return "find-username-result";
    }

    @GetMapping("/find-email") // endpoint = api = /find-username
    public String findByEmail(){
        return "find-email";
    }
    @GetMapping("/find-email-result")
    public String findByEmail(@RequestParam("username")String username,
                              @RequestParam("gender") String gender,
                              Model model){
        userProfileService.findByEmail(username,gender);
        model.addAttribute("email", userProfileService.findByEmail(username,gender));
        return "find-email-result";
    }

    /**
     * @GetMapping("/detail/{userId}")   특정변수에 대한 값으로 페이지 변동을 원한다면 {변수이름}
     * @param userId
     * @param model
     * @return
     */
    @GetMapping("/detail/{userId}")
    public String getUser(@PathVariable int userId, Model model){
        /*
            자료형으로 service 변수명 설정할 때 어떤 자료형을 사용해야할지 모르겠다면
            0. service에서 기능 명칭 앞에 자료형을 무엇을 작성했는지 확인하는 것이 제일 좋음
            1. String 작성해보기   2. Model 작성해보기  3. DTO에 작성한 클래스명칭 작성해보기
            ※ 단 자료형이 void일 경우 변수명 설정이 어려움
        */
        User user =  userProfileService.getUser(userId);
        model.addAttribute("user",user);
        return "detail";
    }

    @GetMapping("/search.naver")
    public String naverSearch(@RequestParam("query") String query, Model model){
        model.addAttribute("query", query);
        return "naver-search";
    }


    // /login 주소로 접속하면 login.html에 작성한 화면이 보임
    @GetMapping("/login")
    public String login(){
        return "login";
    }



    /**
     /login 이 post로 mapping 되었기 때문에 login.html 에 작성한 username, email 에 대한 결과를 확인하는 주소값
     @RequestParam login.html 에서 form 태그 내부나 특정 태그에 작성된 name 값 중 필수로 전달되어야하는 name 명칭에 대해 작성

      * @param username = login.html에서 name=username 으로 작성되어있는 name 명칭
     * @param email    = login.html에서 name=email      로 작성되어있는 name 명칭
     * @param model    = login.html에서 요청한 회원 정보가 없을 때, 일치하는 회원이 없어 로그인을 할 수 없다는 메세지 전달
     *
     * @param session  = 사용자의 정보를 관리하기 위한 객체 사용자의 세션에 데이터를 저장하거나 기존 데이터를 불러오는데 활용
     *                   └─── 세션을 이용해서 로그인을 완성한 후 30분 뒤 자동 로그아웃이나 로그아웃 설정 가능
     *
     * @return         = 로그인 완료 여부에 따라 보여줄 html 작성
     *                   └─── 로그인 완료시 메인 페이지로 전송
     *                   └─── 로그인 실패시 로그인 페이지에 머무르기 + 로그인 실패 메세지 전송
     * Model model          = @PostMapping 에서는 로그인 실패했을 경우 실패 메세지만 전달
     * HttpSession session  = @PostMapping 에서는 로그인 성공했을 경우 로그인한 유저 정보 전달
     */
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String email,
                        Model model, HttpSession session ){
        // 유저프로필 서비스 로그인을 진행했을 때에 대한 결과를 가져옴
        User user = userProfileService.login(username, email);
        // 1. 로그인에 성공했을 경우 회원정보가 존재할 것
        if(user != null){
            session.setAttribute("loggedInUser", user);
            return "redirect:/"; // 로그인 성공한 정보를 가지고 메인페이지로 전달하면서 돌아가기
            /*
             * redirect = api / endpoint / url 과 같은 주소 명칭 주로 작성
             * */
        }
        // 2. 로그인에 실패했을 경우 회원정보가 null 값일 것 (왜냐하면 정보가 없기 때문)
        else {
            model.addAttribute("fail", "유효하지 않은 유저이름 또는 이메일입니다.");
            return "login"; //login.html로 전달
        }

    }

    // 로그인한 정보를 index.html 이외 로그인한 회원에 정보가 필요한 모든 곳에서 사용할 수 있도록 설정하는
    // 로그인 정보 탐지 모델
    @ModelAttribute("loggedInUser")
    public Object addLoggedInUser(HttpSession session){
        return  session.getAttribute("loggedInUser");
    }


    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate(); // invalidate 로그인된 정보를 초기화하면서 없애기
        return "redirect:/";    // 로그아웃 된 정보를 재설정하면서 index.html 돌아가기
    }



    // mypage.html로 접속할 수 있는 api = /mypage 만들기
    @GetMapping("/mypage")
    public String myPage(Model model, HttpSession session){

        Object loggedInUser = session.getAttribute("loggedInUser");
        if(loggedInUser != null){
            model.addAttribute("user", loggedInUser);
        }
        return "mypage";
    }


    // 검색 기능
    @GetMapping("/hobby")
    public String searchHobby(@RequestParam String hobbies, Model model){
        List<User> searchUser = userProfileService.searchHobby(hobbies);
        // hobbies 변수이름에 searchUser로 담겨있는 유저 검색 정보가 들어있음
        model.addAttribute("hobbies", searchUser);
        return "hobby"; //.html
    }



    /*
     * @GetMapping("/error")
     * public String getError(){
     *   return "error"; // .html 기본적으로 작성되어 있기 때문에 error.html로 이동하는 것
     *                   // "" 파일명 뒤에는 .html 숨겨져 있음
     * }
     *
     * api = url = endpoint 로
     * /error 를 작성하지 않아도 error.html로 이동하는 이유는
     * 스프링부트 자체에 기본적으로 /error 주소를 사용해서 WhiteLabel이 작성된
     * 스프링부트 error.html로 이동하고 있기 때문
     *
     * 개발자가 error.html을 만들었기 때문에 스프링부트에서 기본으로 제공하는
     * error.html로 이동하는 것이 아니라 개발자가 만든 error.html 이동
     *
     * */


    /*
     * controller - Get - Post - RequestParam
     * */


    // 검색 새로고침 로그인 유지 -> 쿠키  추후 검증을 통해 로그인 유지할 수 있도록 설정



    @PostMapping("/check-username")
    public void checkDuplicatedUsername(@RequestParam("username") String username,
                                        HttpServletResponse response
    ) throws IOException {
        boolean isDuplicate = userProfileService.checkDuplicatedUsername(username);
        response.setContentType("application/json");
        response.getWriter().write("{\"isDuplicate\" : " + isDuplicate + "}");
    }


}