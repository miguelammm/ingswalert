package es.codeurjc.ais.tictactoe;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StatisticsController {

	@RequestMapping("/estadisticas")
	public String estadisticas(Model model) {
		return "stats";
		
	}

}
