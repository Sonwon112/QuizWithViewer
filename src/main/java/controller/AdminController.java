package controller;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import model.QuizRoom;
import service.QuizRoomService;
import service.QuizService;

@Controller
public class AdminController {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private QuizRoomService qrService;
	@Autowired
	private QuizService qService;
	
	/**
	 * url을 입력해서 방 생성을 제한하기 위한 redirect
	 * @return
	 */
	@GetMapping("/createRoom")
	public String redirectMain() {
		return "redirect:/";
	}
	/**
	 * 관리자가 방생성 시 호출되는 Controller
	 * @param model 모델
	 * @param password 입력한 방 비밀번호
	 * @param session 새로고침 시 새로운 방이 생성되지 않게 하기 위해 세션에 저장
	 * @return
	 */
	@PostMapping("/createRoom")
	public String createRoom(Model model, @RequestParam("password") String password, HttpSession session) {
//		System.out.println(session.getAttribute("room"));
		if(session.getAttribute("room")!=null) {
			try {
				QuizRoom savedQR = (QuizRoom)session.getAttribute("room");
				QuizRoom qr = qrService.findQuizRoomByRoomNum(savedQR.getRoomNum());
				System.out.println(qr);
				if(qr == null) {throw new Exception("해당 방이 존재하지않습니다");}
				model.addAttribute("quizRoom",session.getAttribute("room"));
				return "admin";
			}catch (Exception e) {
				// TODO: handle exception
				session.removeAttribute("room");
			}
			
		}
		
		if(password == null || password.isEmpty()) {
			model.addAttribute("msg", "비밀번호를 입력해주세요");
			model.addAttribute("url", "/");
			return "alert";
		}
		
		QuizRoom quizRoom = qrService.createQuizRoom(password);
		
		model.addAttribute("quizRoom", quizRoom);
		session.setAttribute("room", quizRoom);
		
		return "admin";
	}
	/**
	 * 파일을 업로드 하고 문제에 데이터를 집어넣는 컨트롤러 메소드
	 * @param uploadFile
	 * @param roomNum
	 * @return
	 */
	@PostMapping("/upload")
	@ResponseBody
	public String uploadQuiz(MultipartFile[] uploadFile,String roomNum) {
		log.info("upload Quiz file");
		MultipartFile file = uploadFile[0];
		log.info("Upload FileName : " + file.getOriginalFilename());	
		try {
			qService.getQuizToFile(roomNum,file.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "complete upload";
	}
	
}
