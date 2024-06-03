package ua.flowerista.shop.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.flowerista.shop.dto.ColorDto;
import ua.flowerista.shop.mappers.ColorMapper;
import ua.flowerista.shop.models.Languages;
import ua.flowerista.shop.services.ColorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/color")
@CrossOrigin
@Tag(name = "Color controller")
public class ColorController {

    private final ColorService service;
    private final ColorMapper colorMapper;

    @GetMapping
    @Operation(summary = "Get all collors", description = "Returns list of all colors")
    public ResponseEntity<List<ColorDto>> getAllCollors(@RequestParam(defaultValue = "en") Languages lang) {
        List<ColorDto> colors = service.getAll().stream()
                .map(color -> colorMapper.toDto(color, lang))
                .toList();
        return ResponseEntity.ok(colors);
    }

}
