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
	
	/**
	 * 오버레이 접속 시 해당 방 방번호가 일치해야지 참여자를 목록에 표시가 가능
	 * @param room url에 입력된 방번호
	 * @param model 모델
	 * @return
	 */
	@GetMapping("/overlay")
	public String overlay(@RequestParam String room, Model model) {
		QuizRoom qr = qrService.findQuizRoomByRoomNum(room);
		model.addAttribute("quizRoom",qr);
		return "overlay";
	}
	
}
