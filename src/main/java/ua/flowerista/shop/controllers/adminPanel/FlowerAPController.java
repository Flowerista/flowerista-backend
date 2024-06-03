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
import ua.flowerista.shop.exceptions.AppException;
import ua.flowerista.shop.mappers.FlowerMapper;
import ua.flowerista.shop.models.Flower;
import ua.flowerista.shop.models.Languages;
import ua.flowerista.shop.services.FlowerService;
import ua.flowerista.shop.services.validators.FlowerValidator;

import java.util.List;

@Controller
@RequestMapping("/api/admin/flowers")
@RequiredArgsConstructor
public class FlowerAPController {
    private static final Logger logger = LoggerFactory.getLogger(FlowerAPController.class);

    private final FlowerService flowerService;
    private final FlowerMapper flowerMapper;
    private final FlowerValidator flowerValidator;


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

    @PostMapping
    public ModelAndView saveFlower(@RequestParam("flowerName") String flowerName) {
        FlowerDto flower = new FlowerDto(flowerName);
        List<String> errors = flowerValidator.validate(flower);
        if (!errors.isEmpty()) {
            ModelAndView modelAndView = new ModelAndView("admin/error");
            modelAndView.addObject("errors", errors);
            return modelAndView;
        }
        flowerService.insert(flowerMapper.toEntity(flower));
        return new ModelAndView("redirect:/api/admin/flowers");
    }

    @GetMapping("/{id}")
    public ModelAndView getFlowerById(@PathVariable int id, @RequestParam(defaultValue = "en") Languages lang) {
        ModelAndView result = new ModelAndView("admin/flowers/flowerView");
        FlowerDto flower = flowerService.getById(id).map(flow -> flowerMapper.toDto(flow, lang)).
                orElseThrow(() -> {
                    logger.error("Flower not found {}", id);
                    return new AppException("Flower not found. {} " + id, HttpStatus.INTERNAL_SERVER_ERROR);
                });
        result.addObject("flower", flower);
        return result;
    }

    @PostMapping("/{id}")
    public ModelAndView changeFlowerName(@PathVariable int id,
                                         @RequestParam("inputName") String flowerName,
                                         @RequestParam(name = "lang", defaultValue = "en") Languages lang) {
        flowerService.update(new FlowerDto(id, flowerName), lang);
        return new ModelAndView("redirect:/api/admin/flowers/" + id + "?lang=" + lang);
    }

}
