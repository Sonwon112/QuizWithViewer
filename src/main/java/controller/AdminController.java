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
	
	@GetMapping("/createRoom")
	public String redirectMain() {
		return "redirect:/";
	}
	
	@PostMapping("/createRoom")
	public String createRoom(Model model, @RequestParam("password") String password, HttpSession session) {
		
		if(session.getAttribute("room")!=null) {
			model.addAttribute("quizRoom",session.getAttribute("room"));
			return "admin";
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
