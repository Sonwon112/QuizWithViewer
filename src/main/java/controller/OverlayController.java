package controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OverlayController {
	
	@GetMapping("/overlay")
	public String overlay(@RequestParam String room, Model model) {
		model.addAttribute("roomNum",room);
		return "overlay";
	}
	
}
