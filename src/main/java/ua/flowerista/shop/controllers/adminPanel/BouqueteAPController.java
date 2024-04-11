package ua.flowerista.shop.controllers.adminPanel;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import ua.flowerista.shop.dto.*;
import ua.flowerista.shop.mappers.BouqueteMapper;
import ua.flowerista.shop.models.Bouquete;
import ua.flowerista.shop.services.BouqueteService;
import ua.flowerista.shop.services.BouqueteSizeService;
import ua.flowerista.shop.services.ColorService;
import ua.flowerista.shop.services.FlowerService;

import java.util.List;

@Controller
@RequestMapping("/api/admin/bouquets")
@RequiredArgsConstructor
public class BouqueteAPController {
    private final BouqueteService bouqueteService;
    private final BouqueteMapper bouqueteMapper;
    private final FlowerService flowerService;
    private final ColorService colorService;
    private final BouqueteSizeService bouqueteSizeService;

    @GetMapping
    public ModelAndView getAllBouquets(@QuerydslPredicate(root = Bouquete.class) Predicate predicate,
                                       @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                                       @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
                                       @RequestParam(name = "bouquetName", defaultValue = "", required = false) String name) {
        Page<BouqueteDto> bouquets;
        if (!name.equals("")) {
            bouquets = bouqueteService.searchBouquetsByName(name, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))
                    .map(bouqueteMapper::toDto);
        } else {
            bouquets = bouqueteService.getAllBouquetes(predicate, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))
                    .map(bouqueteMapper::toDto);
        }
        return new ModelAndView("admin/bouquets/bouquetList").addObject("bouquets", bouquets);
    }

    @GetMapping("/{id}")
    public ModelAndView getBouquet(@PathVariable Integer id) {
        ModelAndView result = new ModelAndView("admin/bouquets/bouquetView");
        BouqueteDto bouqueteDto = bouqueteService.getBouqueteById(id);
        result.addObject("bouquet", bouqueteDto);

        List<FlowerDto> flowersDto = flowerService.getAllFlowers();
        result.addObject("flowers", flowersDto);

        List<ColorDto> colorsDto = colorService.getAllColors();
        result.addObject("colors", colorsDto);
        return result;
    }

    @PostMapping("/{id}")
    public ModelAndView updateBouquet(@PathVariable Integer id, @RequestBody BouqueteDto bouqueteDto) {
        Bouquete bouquete = bouqueteService.getBouquet(id);
        bouqueteDto.getSizes().stream().forEach(bouqueteSize -> bouqueteSize.setBouquete(bouquete));
        bouqueteDto.setImageUrls(bouquete.getImageUrls());

        bouqueteSizeService.saveAll(bouqueteDto.getSizes());
        bouqueteService.insert(bouqueteDto);
        return new ModelAndView("redirect:/api/admin/bouquets/" + id);
    }

    @PostMapping("/image/{id}")
    public ModelAndView uploadImages(@PathVariable Integer id, @RequestParam("images") List<MultipartFile> images) {
        if (!images.isEmpty() && !images.stream().allMatch(MultipartFile::isEmpty)) {
            BouqueteDto bouquetDto = bouqueteService.getBouqueteById(id);
            bouqueteService.insert(bouquetDto, images);
        }
        return new ModelAndView(new RedirectView("redirect:/api/admin/bouquets/" + id, true, false));
    }

    @DeleteMapping("/{bouquetId}/{imageId}")
    public ModelAndView deleteImageFromBouquet(@PathVariable("bouquetId") Integer bouquetId, @PathVariable("imageId") Integer imageId) {
        bouqueteService.deleteImageFromBouquet(bouquetId, imageId);
        return new ModelAndView(new RedirectView("/api/admin/bouquets/" + bouquetId, true, false));
    }
}
