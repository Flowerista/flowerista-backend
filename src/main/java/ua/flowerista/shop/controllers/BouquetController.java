package ua.flowerista.shop.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.flowerista.shop.dto.BouquetDto;
import ua.flowerista.shop.exceptions.AppException;
import ua.flowerista.shop.mappers.BouquetMapper;
import ua.flowerista.shop.models.textContent.Languages;
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
    public List<BouquetDto> getBouquetBestSellers(@RequestParam(defaultValue = "en") Languages lang) {
        return bouquetService.getBestSellers().stream()
                .map(bouquet -> bouquetMapper.toDto(bouquet, lang))
                .toList();
    }

    @GetMapping("/ts")
    @Operation(summary = "Get topsales", description = "Returns list (5 units) of topsales")
    public List<BouquetDto> getBouquetTopSales(@RequestParam(defaultValue = "en") Languages lang) {
        return bouquetService.getTop5Sales().stream()
                .map(bouquet -> bouquetMapper.toDto(bouquet, lang))
                .toList();
    }

    @GetMapping
    @Operation(summary = "Get catalog with filters",
            description = "Returns page (20 units) of bouquets filtered and sorted")
    public Page<BouquetDto> getBouquetCatalog(
            @RequestParam(required = false) List<Integer> flowerIds,
            @RequestParam(required = false) List<Integer> colorIds,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(defaultValue = "false") Boolean sortByNewest,
            @RequestParam(defaultValue = "false") Boolean sortByPriceHighToLow,
            @RequestParam(defaultValue = "false") Boolean sortByPriceLowToHigh,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "en") Languages lang) {
        List<BouquetDto> bouqueteList = bouquetService.getCatalogFiltered(flowerIds, colorIds, minPrice,
                        maxPrice, sortByNewest, sortByPriceHighToLow, sortByPriceLowToHigh)
                .stream()
                .map(bouquet -> bouquetMapper.toDto(bouquet, lang))
                .toList();
        return convertListToPage(bouqueteList, page, size);
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
    public BouquetDto getById(@PathVariable("id") Integer id,
                              @RequestParam(defaultValue = "en") Languages lang) {
        return bouquetService.getById(id)
                .map(bouquet -> bouquetMapper.toDto(bouquet, lang))
                .orElseThrow(() -> new AppException("Bouquet not found. Id: " + id, HttpStatus.NOT_FOUND));
    }

    @GetMapping("/search")
    @Operation(summary = "Search bouquets", description = "Returns empty list if request is less than 3 symbols")
    public List<BouquetDto> search(@RequestParam("name") String text,
                                        @RequestParam(defaultValue = "en") Languages lang) {
        return bouquetService.search(text)
                .stream()
                .map(bouquet -> bouquetMapper.toDto(bouquet, lang))
                .toList();
    }

    private static <T> Page<T> convertListToPage(List<T> list, int page, int size) {
        if (list.isEmpty()) {
            return Page.empty();
        }
        int start = page * size;
        int end = Math.min(start + size, list.size());
        return new PageImpl<>(list.subList(start, end), PageRequest.of(page, size), list.size());
    }

}
