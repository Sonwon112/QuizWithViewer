package container;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import controller.AdminController;
import controller.OverlayController;
import controller.ViewerController;

@Configuration
public class ControllerConfig {
	
	@Bean
	public AdminController adminController() {
		return new AdminController();
	}
	
	@Bean
	public OverlayController overlayController() {
		return new OverlayController();
	}
	
	@Bean
	public ViewerController viewerController() {
		return new ViewerController();
	}
	
}
