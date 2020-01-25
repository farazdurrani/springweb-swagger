package hello.two;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecondController {

	
	@GetMapping
	public String get() {
		return "Hi";
	}
}
