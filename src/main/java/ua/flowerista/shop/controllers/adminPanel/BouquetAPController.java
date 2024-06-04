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
import ua.flowerista.shop.dto.admin.BouquetFullDto;
import ua.flowerista.shop.dto.admin.ColorFullDto;
import ua.flowerista.shop.dto.admin.FlowerFullDto;
import ua.flowerista.shop.mappers.BouquetMapper;
import ua.flowerista.shop.mappers.ColorMapper;
import ua.flowerista.shop.mappers.FlowerMapper;
import ua.flowerista.shop.models.Bouquet;
import ua.flowerista.shop.models.textContent.Languages;
import ua.flowerista.shop.services.*;

import java.util.List;

@Controller
@RequestMapping("/api/admin/bouquets")
@RequiredArgsConstructor
public class BouquetAPController {
    private final BouquetService bouquetService;
    private final FlowerService flowerService;
    private final ColorService colorService;

    private final BouquetMapper bouquetMapper;
    private final ColorMapper colorMapper;
    private final FlowerMapper flowerMapper;

    @GetMapping
    public ModelAndView getAllBouquets(@QuerydslPredicate(root = Bouquet.class) Predicate predicate,
                                       @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                                       @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
                                       @RequestParam(name = "bouquetName", defaultValue = "", required = false) String name,
                                       @RequestParam(name = "lang", defaultValue = "en", required = false) Languages lang) {
        Page<BouquetDto> bouquets;
        if (!name.isEmpty()) {
            List<BouquetDto> bouqueteList = bouquetService.search(name).stream()
                    .map(bouquet -> bouquetMapper.toDto(bouquet, lang))
                    .toList();
            bouquets = convertListToPage(bouqueteList, page);
        } else {
            bouquets = bouquetService.getAll(predicate, PageRequest.of(page, size, Sort.by("id")))
                    .map(bouquet -> bouquetMapper.toDto(bouquet, lang));
        }
        return new ModelAndView("admin/bouquets/bouquetList").addObject("bouquets", bouquets);
    }

    @GetMapping("/{id}")
    public ModelAndView getBouquet(@PathVariable Integer id, @RequestParam(defaultValue = "en") Languages lang) {
        ModelAndView result = new ModelAndView("admin/bouquets/bouquetView");
        Bouquet bouquet = bouquetService.findById(id);
        BouquetFullDto bouquetDto = bouquetMapper.toDto(bouquet);

        result.addObject("bouquet", bouquetDto);

        List<FlowerFullDto> flowersDto = flowerService.getAll().stream().map(flowerMapper::toAdminDto).toList();
        result.addObject("flowers", flowersDto);

        List<ColorFullDto> colorsDto = colorService.getAll().stream().map(colorMapper::toAdminDto).toList();
        result.addObject("colors", colorsDto);

        return result;
    }

    @PostMapping("/{id}")
    public ModelAndView updateBouquet(@PathVariable Integer id,
                                      @ModelAttribute("bouquet") BouquetFullDto bouquetDto) {
        Bouquet bouquet = bouquetService.getById(id).orElse(null);
        bouquet = bouquetMapper.partialUpdate(bouquetDto, bouquet);
        bouquetService.save(bouquet);
        return new ModelAndView("redirect:/api/admin/bouquets/" + id);
    }

    @PostMapping("/{id}/flowers")
    public ModelAndView updateFlowers(@PathVariable Integer id, @RequestParam List<Integer> flowerIds) {
        bouquetService.updateFlowers(id, flowerIds);
        return new ModelAndView("redirect:/api/admin/bouquets/" + id);
    }

    @PostMapping("/{id}/colors")
    public ModelAndView updateColors(@PathVariable Integer id, @RequestParam List<Integer> colorIds) {
        bouquetService.updateColors(id, colorIds);
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

    @ModelAttribute("bouquet")
    private BouquetFullDto getBouquetModel() {
        return new BouquetFullDto();
    }
    @ModelAttribute("color")
    private ColorFullDto getColorModel() {
        return new ColorFullDto();
    }
    @ModelAttribute("flower")
    private FlowerFullDto getFlowerModel() {
        return new FlowerFullDto();
    }

    private static <T> Page<T> convertListToPage(List<T> list, int page) {
        int size = list.size();
        int start = page * size;
        int end = Math.min(start + size, list.size());
        return new PageImpl<>(list.subList(start, end), PageRequest.of(page, size), list.size());
    }

}
