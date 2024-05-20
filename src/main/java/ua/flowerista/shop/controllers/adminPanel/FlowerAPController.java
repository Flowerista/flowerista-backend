package ua.flowerista.shop.controllers.adminPanel;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.flowerista.shop.dto.FlowerDto;
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
    private final FlowerService flowerService;
    private final FlowerMapper flowerMapper;
    private final FlowerValidator flowerValidator;
    private final DataSourceTransactionManagerAutoConfiguration dataSourceTransactionManagerAutoConfiguration;

    @GetMapping
    public ModelAndView getFlowers(@QuerydslPredicate(root = Flower.class)
                                   Predicate predicate,
                                   @RequestParam(name = "page", defaultValue = "0", required = false)
                                   Integer page,
                                   @RequestParam(name = "size", defaultValue = "10", required = false)
                                   Integer size,
                                   Pageable pageable,
                                   @RequestParam(defaultValue = "en") Languages lang) {
        Page<FlowerDto> flowers = flowerService.getAllFlowers(predicate,
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
        flowerService.insert(flower);
        return new ModelAndView("redirect:/api/admin/flowers");
    }

    @GetMapping("/{id}")
    public ModelAndView getFlowerById(@PathVariable int id, @RequestParam(defaultValue = "en") Languages lang) {
        ModelAndView result = new ModelAndView("admin/flowers/flowerView");
        FlowerDto flower = flowerService.getFlower(id).map(flow -> flowerMapper.toDto(flow, lang)).orElse(null);
        result.addObject("flower", flower);
        return result;
    }

    @PostMapping("/{id}")
    public ModelAndView changeFlowerName(@PathVariable int id,
                                         @RequestParam("inputName") String flowerName,
                                         @RequestParam(name = "lang", defaultValue = "en") Languages lang) {
        System.out.println(lang);
        flowerService.update(new FlowerDto(id, flowerName), lang);
        return new ModelAndView("redirect:/api/admin/flowers/" + id + "?lang=" + lang);
    }

}
