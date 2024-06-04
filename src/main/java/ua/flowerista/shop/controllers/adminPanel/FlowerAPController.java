package ua.flowerista.shop.controllers.adminPanel;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.flowerista.shop.dto.FlowerDto;
import ua.flowerista.shop.dto.admin.FlowerFullDto;
import ua.flowerista.shop.exceptions.AppException;
import ua.flowerista.shop.mappers.FlowerMapper;
import ua.flowerista.shop.models.Flower;
import ua.flowerista.shop.models.textContent.Languages;
import ua.flowerista.shop.services.FlowerService;

@Controller
@RequestMapping("/api/admin/flowers")
@RequiredArgsConstructor
public class FlowerAPController {
    private static final Logger logger = LoggerFactory.getLogger(FlowerAPController.class);

    private final FlowerService flowerService;
    private final FlowerMapper flowerMapper;


    @GetMapping
    public ModelAndView getFlowers(@QuerydslPredicate(root = Flower.class)
                                   Predicate predicate,
                                   @RequestParam(name = "page", defaultValue = "0", required = false)
                                   Integer page,
                                   @RequestParam(name = "size", defaultValue = "10", required = false)
                                   Integer size,
                                   @RequestParam(defaultValue = "en") Languages lang) {
        Page<FlowerDto> flowers = flowerService.getAll(predicate,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))).map(flower -> flowerMapper.toDto(flower, lang));
        return new ModelAndView("admin/flowers/flowersList").addObject("flowers", flowers);
    }

    @GetMapping("/{id}")
    public ModelAndView getFlowerById(@PathVariable Integer id) {
        ModelAndView result = new ModelAndView("admin/flowers/flowerView");
        FlowerFullDto flower = flowerService.getById(id)
                .map(flowerMapper::toAdminDto)
                .orElseThrow(() -> {
                    logger.error("Flower not found by id: {}", id);
                    return new AppException("Flower not found", HttpStatus.NOT_FOUND);
                });
        result.addObject("flower", flower);
        return result;
    }

    @PostMapping("/{id}")
    public ModelAndView updateFlower(@ModelAttribute("flower") FlowerFullDto flower) {
        flowerService.update(flowerMapper.toEntity(flower));
        return new ModelAndView("redirect:/api/admin/flowers/" + flower.getId());
    }

    @PostMapping("/add")
    public ModelAndView addFlower(@RequestParam("name") String name) {
        Flower flower = flowerService.addFlower(name);
        return new ModelAndView("redirect:/api/admin/flowers/" + flower.getId());
    }

    @ModelAttribute("flower")
    private FlowerFullDto getDefaultProduct() {
        return new FlowerFullDto();
    }

}
