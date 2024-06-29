package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import model.Participant;
import model.QuizRoom;
import service.QuizRoomService;

@Controller
public class ViewerController{
	
	@Autowired
	QuizRoomService qrService;
	
	@GetMapping("/partLogin")
	public String goLoginPage() {
		return "redirect:/participant";
	}
	
	@PostMapping("/partLogin")
	public String Login(@RequestParam String roomNum, @RequestParam String password, Participant participant, Model model) {
		QuizRoom qr = null;
		
		try {
			qr = qrService.findQuizRoomByRoomNum(roomNum);
		}catch (Exception e) {
			// TODO: handle exception
			model.addAttribute("msg", "입력하신 방이 없습니다. 방 번호를 확인해주세요");
			model.addAttribute("url", "backPage");
			return "alert";
		}
		if(qr == null){
			model.addAttribute("msg", "입력하신 방이 없습니다. 방 번호를 확인해주세요");
			model.addAttribute("url", "backPage");
			return "alert";
		}
		if(!qr.isAllowParticipant()) {
			model.addAttribute("msg", "해당 방이 참여를 허용하지 않고 있습니다. 다음에 다시 시도해주세요");
			model.addAttribute("url", "backPage");
			return "alert";
		}
		if(!qr.getPassword().equals(password)) {
			model.addAttribute("msg", "비밀번호가 일치하지 않습니다");
			model.addAttribute("url", "backPage");
			return "alert";
		}
		if(participant.getNickname().isEmpty() || participant.getNickname().equals("")) {
			model.addAttribute("msg", "닉네임을 입력해주세요");
			model.addAttribute("url", "backPage");
			return "alert";
		}
		qrService.partQuizRoom(qr, participant);
		model.addAttribute("roomNum", roomNum);

		
		return "participant/playPage";
	}
	
	
}
