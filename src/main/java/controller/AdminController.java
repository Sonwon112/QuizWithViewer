package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import service.QuizRoomService;

@Controller
public class AdminController {
	
	@Autowired
	private QuizRoomService qrService;
	
	@GetMapping("/createRoom")
	public String redirectMain() {
		return "redirect:/";
	}
	
	@PostMapping("/createRoom")
	public String createRoom(Model model, @RequestParam("password") String password) {
		if(password == null || password.isEmpty()) {
			model.addAttribute("msg", "비밀번호를 입력해주세요");
			model.addAttribute("url", "/");
			return "alert";
		}
		
		String roomNum = qrService.createQuizRoom(password);
		model.addAttribute("roomNum", roomNum);
		model.addAttribute("password", password);
		
		return "admin";
	}
	
}
