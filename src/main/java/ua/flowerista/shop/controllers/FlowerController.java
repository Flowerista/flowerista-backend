package ua.flowerista.shop.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.flowerista.shop.dto.FlowerDto;
import ua.flowerista.shop.mappers.FlowerMapper;
import ua.flowerista.shop.models.Languages;
import ua.flowerista.shop.services.FlowerService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/flower")
@CrossOrigin
@Tag(name = "Flower controller")
public class FlowerController {

    private final FlowerService service;
    private final FlowerMapper flowerMapper;

    @GetMapping
    @Operation(summary = "Get all flowers", description = "Returns list of all flowers")
    public List<FlowerDto> getAllFlowers(@RequestParam(defaultValue = "en") Languages lang) {
        return flowerMapper.toDto(service.getAll(), lang);
    }

}
