package com.axaboutconsulting.consultant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/consultant")
public class ConsultantController {
	@Autowired
	public ConsultantService service;
	
	@GetMapping("/list")
	public getList() {
		
	}

}
