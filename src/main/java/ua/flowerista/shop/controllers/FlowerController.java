package ua.flowerista.shop.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ua.flowerista.shop.dto.FlowerDto;
import ua.flowerista.shop.services.FlowerService;

@RestController
@RequestMapping("/api/flower")
@CrossOrigin
@Tag(name="Flower controller")
public class FlowerController {
	
	@Autowired
	FlowerService service;
	
	@GetMapping
	@Operation(summary = "Get all flowers", description = "Returns list of all flowers")
	public ResponseEntity<List<FlowerDto>> getAllFlowers() {
		List<FlowerDto> flowers = service.getAllFlowers();
		return ResponseEntity.ok(flowers);
	}

}
