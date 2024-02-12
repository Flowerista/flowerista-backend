package ua.flowerista.shop.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ua.flowerista.shop.dto.BouqueteCardDto;
import ua.flowerista.shop.dto.BouqueteSmallDto;
import ua.flowerista.shop.dto.PriceRangeDto;
import ua.flowerista.shop.services.BouqueteService;

@RestController
@RequestMapping("/api/bouquete")
@CrossOrigin(origins = "*")
@Tag(name = "Bouquete controller")
public class BouqueteController {

	@Autowired
	private BouqueteService service;

	@GetMapping("/bs")
	@Operation(summary = "Get bestsellers", description = "Returns list (5 units) of bestsellers")
	public ResponseEntity<List<BouqueteSmallDto>> getBouqueteBestSellers() {
		List<BouqueteSmallDto> bouquetesModels = service.getBouquetesBestSellers();
		return ResponseEntity.ok(bouquetesModels);
	}

	@GetMapping("/ts")
	@Operation(summary = "Get topsales", description = "Returns list (5 units) of topsales")
	public ResponseEntity<List<BouqueteSmallDto>> getBouqueteTopSales() {
		List<BouqueteSmallDto> bouquetesModels = service.getBouquetesTop5Sales();
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
			@RequestParam(defaultValue = "1") int page) {

		return ResponseEntity.ok(service.getBouquetesCatalogFiltered(flowerIds, colorIds, minPrice, maxPrice,
				sortByNewest, sortByPriceHighToLow, sortByPriceLowToHigh, page));

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
	public ResponseEntity<BouqueteCardDto> getById(@PathVariable("id") int id) {
		BouqueteCardDto dto = service.getById(id);
		if (dto == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(dto);
	}

	@GetMapping("/search")
	@Operation(summary = "Search bouquetes by names", description = "Returns empty list if in request was less than 3 symbols")
	public ResponseEntity<List<BouqueteSmallDto>> searchBouquetesByName(@RequestParam("name") String name) {
		return ResponseEntity.ok(service.searchBouquetesByName(name));
	}

}
