package ua.flowerista.shop.controllers.translateTestingControllerV1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.flowerista.shop.dto.FlowerDto;
import ua.flowerista.shop.models.Languages;
import ua.flowerista.shop.services.FlowerService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flower")
@CrossOrigin
@Tag(name="Flower controller for translation testing")
public class FlowerControllerV1 {

	@Autowired
	FlowerService service;

	@GetMapping
	@Operation(summary = "Get all flowers", description = "Returns list of all flowers")
	public ResponseEntity<List<FlowerDto>> getAllFlowers(@RequestParam(defaultValue = "EN") Languages lang) {
		List<FlowerDto> flowers = service.getAllFlowers(lang);
		return ResponseEntity.ok(flowers);
	}

}
