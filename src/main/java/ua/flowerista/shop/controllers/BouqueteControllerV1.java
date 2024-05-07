package ua.flowerista.shop.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.flowerista.shop.dto.BouqueteCardDto;
import ua.flowerista.shop.dto.BouqueteSmallDto;
import ua.flowerista.shop.dto.PriceRangeDto;
import ua.flowerista.shop.models.Languages;
import ua.flowerista.shop.services.BouqueteService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bouquete")
@CrossOrigin(origins = "*")
@Tag(name = "Bouquete controller")
public class BouqueteControllerV1 {

	@Autowired
	private BouqueteService service;

	@GetMapping("/bs")
	@Operation(summary = "Get bestsellers", description = "Returns list (5 units) of bestsellers")
	public ResponseEntity<List<BouqueteSmallDto>> getBouqueteBestSellers(@RequestParam(defaultValue = "EN") Languages lang) {
		List<BouqueteSmallDto> bouquetesModels = service.getBouquetesBestSellers(lang);
		return ResponseEntity.ok(bouquetesModels);
	}

	@GetMapping("/ts")
	@Operation(summary = "Get topsales", description = "Returns list (5 units) of topsales")
	public ResponseEntity<List<BouqueteSmallDto>> getBouqueteTopSales(@RequestParam(defaultValue = "EN") Languages lang) {
		List<BouqueteSmallDto> bouquetesModels = service.getBouquetesTop5Sales(lang);
		return ResponseEntity.ok(bouquetesModels);
	}

	@GetMapping
	@Operation(summary = "Get catalog with filters", description = "Returns page (20 units) of bouquetes filtered and sorted")
	public ResponseEntity<Page<BouqueteSmallDto>> getBouqueteCatalog(
			@RequestParam(required = false) List<Integer> flowerIds,
			@RequestParam(required = false) List<Integer> colorIds, @RequestParam(required = false) Integer minPrice,
			@RequestParam(required = false) Integer maxPrice,
			@RequestParam(defaultValue = "false") Boolean sortByNewest,
			@RequestParam(defaultValue = "false") Boolean sortByPriceHighToLow,
			@RequestParam(defaultValue = "false") Boolean sortByPriceLowToHigh,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "EN") Languages lang) {

		return ResponseEntity.ok(service.getBouquetesCatalogFiltered(flowerIds, colorIds, minPrice, maxPrice,
				sortByNewest, sortByPriceHighToLow, sortByPriceLowToHigh, page, lang));

	}

	@GetMapping("/price-range")
	@Operation(summary = "Get price range of bouquetes", description = "Returns 2 Integers with min and max price")
	public ResponseEntity<PriceRangeDto> getPriceRange() {
		PriceRangeDto priceRange = service.getMinMaxPrices();
		return ResponseEntity.ok(priceRange);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get bouquete card dto by id", description = "Returns bouquete card dto")
	@ApiResponses(value = { @ApiResponse(responseCode = "404", description = "If bouquete was not found"),
			@ApiResponse(responseCode = "200", description = "If bouquete was found") })
	public ResponseEntity<BouqueteCardDto> getById(@PathVariable("id") int id, @RequestParam(defaultValue = "EN") Languages lang) {
		BouqueteCardDto dto = service.getById(id, lang);
		if (dto == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(dto);
	}

	@GetMapping("/search")
	@Operation(summary = "Search bouquetes by names", description = "Returns empty list if in request was less than 3 symbols")
	public ResponseEntity<List<BouqueteSmallDto>> searchBouquetesByName(@RequestParam("name") String name, @RequestParam(defaultValue = "EN") Languages lang) {
		return ResponseEntity.ok(service.searchBouquetesByName(name, lang));
	}
}
