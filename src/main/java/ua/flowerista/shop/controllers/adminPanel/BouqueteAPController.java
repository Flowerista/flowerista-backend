package ua.flowerista.shop.controllers.adminPanel;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import ua.flowerista.shop.dto.*;
import ua.flowerista.shop.mappers.BouquetMapper;
import ua.flowerista.shop.mappers.ColorMapper;
import ua.flowerista.shop.mappers.FlowerMapper;
import ua.flowerista.shop.models.Bouquet;
import ua.flowerista.shop.models.Languages;
import ua.flowerista.shop.services.*;

import java.util.List;

@Controller
@RequestMapping("/api/admin/bouquets")
@RequiredArgsConstructor
public class BouqueteAPController {
    private final BouquetService bouquetService;
    private final BouquetMapper bouquetMapper;
    private final FlowerService flowerService;
    private final ColorService colorService;
    private final BouqueteSizeService bouqueteSizeService;
    private final ColorMapper colorMapper;
    private final FlowerMapper flowerMapper;

    @GetMapping
    public ModelAndView getAllBouquets(@QuerydslPredicate(root = Bouquet.class) Predicate predicate,
                                       @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                                       @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
                                       @RequestParam(name = "bouquetName", defaultValue = "", required = false) String name,
                                       @RequestParam(name = "lang", defaultValue = "en", required = false) Languages lang) {
        Page<BouquetDto> bouquets;
        if (!name.equals("")) {
            List<BouquetDto> bouqueteList = bouquetService.searchBouquets(name).stream()
                    .map(bouquete -> bouquetMapper.toDto(bouquete, lang))
                    .toList();
            bouquets = convertListToPage(bouqueteList, page);
        } else {
            bouquets = bouquetService.getAllBouquets(predicate, PageRequest.of(page, size, Sort.by("id")))
                    .map(bouquete -> bouquetMapper.toDto(bouquete, lang));
        }
        return new ModelAndView("admin/bouquets/bouquetList").addObject("bouquets", bouquets);
    }

    @GetMapping("/{id}")
    public ModelAndView getBouquet(@PathVariable Integer id, @RequestParam(defaultValue = "en") Languages lang) {
        ModelAndView result = new ModelAndView("admin/bouquets/bouquetView");
        BouquetDto bouquetDto = bouquetService.getBouquetById(id)
                .map(bouquete -> bouquetMapper.toDto(bouquete, lang))
                .orElse(null);

        result.addObject("bouquet", bouquetDto);

        List<FlowerDto> flowersDto = flowerMapper.toDto(flowerService.getAll(), lang);
        result.addObject("flowers", flowersDto);

        List<ColorDto> colorsDto = colorMapper.toDto(colorService.getAll(), lang);
        result.addObject("colors", colorsDto);
        return result;
    }

    @PostMapping("/{id}")
    public ModelAndView updateBouquet(@PathVariable Integer id,
                                      @RequestBody BouquetDto bouquetDto,
                                      @RequestParam(defaultValue = "en") Languages lang) {
        Bouquet bouquet = bouquetService.getBouquetById(id).orElse(null);
        bouquetDto.getSizes().stream().forEach(bouqueteSize -> bouqueteSize.setBouquet(bouquet));
        bouquetDto.setImageUrls(bouquet.getImageUrls());

        bouqueteSizeService.saveAll(bouquetDto.getSizes());
        bouquetService.update(bouquetDto, lang);
        return new ModelAndView("redirect:/api/admin/bouquets/" + id);
    }

    @PostMapping("/image/{id}")
    public ModelAndView uploadImages(@PathVariable Integer id,
                                     @RequestParam("images") List<MultipartFile> images) {
        if (!images.isEmpty() && !images.stream().allMatch(MultipartFile::isEmpty)) {
            bouquetService.addImagesToBouquet(id, images);
        }
        return new ModelAndView("redirect:/api/admin/bouquets/" + id);
    }

    @DeleteMapping("/{bouquetId}/{imageId}")
    public ModelAndView deleteImageFromBouquet(@PathVariable("bouquetId") Integer bouquetId, @PathVariable("imageId") Integer imageId) {
        bouquetService.deleteImageFromBouquet(bouquetId, imageId);
        return new ModelAndView(new RedirectView("/api/admin/bouquets/" + bouquetId, true, false));
    }

    private static <T> Page<T> convertListToPage(List<T> list, int page) {
        int size = list.size();
        int start = page * size;
        int end = Math.min(start + size, list.size());
        return new PageImpl<>(list.subList(start, end), PageRequest.of(page, size), list.size());
    }
}
