package ua.flowerista.shop.controllers.translateTestingControllerV1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.flowerista.shop.dto.ColorDto;
import ua.flowerista.shop.models.Languages;
import ua.flowerista.shop.services.ColorService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/color")
@CrossOrigin
@Tag(name="Color controller")
public class ColorControllerV1 {

	@Autowired
	private ColorService service;

	@GetMapping
	@Operation(summary = "Get all collors", description = "Returns list of all colors")
	public ResponseEntity<List<ColorDto>> getAllCollors(@RequestParam(defaultValue = "EN") Languages lang) {
		List<ColorDto> colors = service.getAllColors(lang);
		return ResponseEntity.ok(colors);
	}

}
