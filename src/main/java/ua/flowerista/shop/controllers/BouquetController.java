package ua.flowerista.shop.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.flowerista.shop.dto.BouqueteCardDto;
import ua.flowerista.shop.dto.BouqueteSmallDto;
import ua.flowerista.shop.exceptions.AppException;
import ua.flowerista.shop.mappers.BouquetMapper;
import ua.flowerista.shop.models.Languages;
import ua.flowerista.shop.services.BouquetService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bouquete")
@CrossOrigin(origins = "*")
@Tag(name = "Bouquet controller")
public class BouquetController {

    private final BouquetService bouquetService;
    private final BouquetMapper bouquetMapper;

    @GetMapping("/bs")
    @Operation(summary = "Get bestsellers", description = "Returns list (5 units) of bestsellers")
    public List<BouqueteSmallDto> getBouquetBestSellers(@RequestParam(defaultValue = "en") Languages lang) {
        return bouquetService.getBouquetsBestSellers().stream()
                .map(bouquet -> bouquetMapper.toSmallDto(bouquet, lang))
                .toList();
    }

    @GetMapping("/ts")
    @Operation(summary = "Get topsales", description = "Returns list (5 units) of topsales")
    public List<BouqueteSmallDto> getBouquetTopSales(@RequestParam(defaultValue = "en") Languages lang) {
        return bouquetService.getBouquetsTop5Sales().stream()
                .map(bouquet -> bouquetMapper.toSmallDto(bouquet, lang))
                .toList();
    }

    @GetMapping
    @Operation(summary = "Get catalog with filters",
            description = "Returns page (20 units) of bouquets filtered and sorted")
    public Page<BouqueteSmallDto> getBouquetCatalog(
            @RequestParam(required = false) List<Integer> flowerIds,
            @RequestParam(required = false) List<Integer> colorIds,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(defaultValue = "false") Boolean sortByNewest,
            @RequestParam(defaultValue = "false") Boolean sortByPriceHighToLow,
            @RequestParam(defaultValue = "false") Boolean sortByPriceLowToHigh,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "en") Languages lang) {
        List<BouqueteSmallDto> bouqueteList = bouquetService.getBouquetsCatalogFiltered(flowerIds, colorIds, minPrice,
                        maxPrice, sortByNewest, sortByPriceHighToLow, sortByPriceLowToHigh)
                .stream()
                .map(bouquet -> bouquetMapper.toSmallDto(bouquet, lang))
                .toList();
        return convertListToPage(bouqueteList, page);
    }

    @GetMapping("/price-range")
    @Operation(summary = "Get price range of bouquets", description = "Returns map with min and max price")
    public Map<String, Integer> getPriceRange() {
        Map<String, Integer> result = new HashMap<>();
        result.put("minPrice", bouquetService.getMinPrice());
        result.put("maxPrice", bouquetService.getMaxPrice());
        return result;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get bouquet cardDto by id")
    public BouqueteCardDto getById(@PathVariable("id") Integer id,
                                   @RequestParam(defaultValue = "en") Languages lang) {
        return bouquetService.getBouquetById(id)
                .map(bouquet -> bouquetMapper.toCardDto(bouquet, lang))
                .orElseThrow(() -> new AppException("Bouquet not found. Id: " + id, HttpStatus.NOT_FOUND));
    }

    @GetMapping("/search")
    @Operation(summary = "Search bouquets", description = "Returns empty list if request is less than 3 symbols")
    public List<BouqueteSmallDto> search(@RequestParam("name") String text,
                                         @RequestParam(defaultValue = "en") Languages lang) {
        return bouquetService.searchBouquets(text)
                .stream()
                .map(bouquet -> bouquetMapper.toSmallDto(bouquet, lang))
                .toList();
    }

    private static <T> Page<T> convertListToPage(List<T> list, int page) {
        int size = list.size();
        int start = page * size;
        int end = Math.min(start + size, list.size());
        return new PageImpl<>(list.subList(start, end), PageRequest.of(page, size), list.size());
    }

}
