package ua.flowerista.shop.controllers;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/test")
@CrossOrigin
@Tag(name="Test controller", description = "To test api")
public class TestController {
	
	@GetMapping
    @ResponseBody
    public String helloWorld() {
        return "{\"name\":\"Hello, World!\"}";
    }

}
