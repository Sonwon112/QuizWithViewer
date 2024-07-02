package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import model.QuizRoom;
import service.QuizRoomService;

@Controller
public class OverlayController {
	@Autowired
	private QuizRoomService qrService;
	
	@GetMapping("/overlay")
	public String overlay(@RequestParam String room, Model model) {
		QuizRoom qr = qrService.findQuizRoomByRoomNum(room);
		model.addAttribute("quizRoom",qr);
		return "overlay";
	}
	
}
