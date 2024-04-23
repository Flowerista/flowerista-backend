package ua.flowerista.shop.controllers.adminPanel;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
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
import ua.flowerista.shop.services.FlowerService;
import ua.flowerista.shop.services.validators.FlowerValidator;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/admin/flowers")
@RequiredArgsConstructor
public class FlowerAPController {
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
                                   Pageable pageable) {
        Page<FlowerDto> flowers = flowerService.getAllFlowers(predicate,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))).map(flowerMapper::toDto);
        return new ModelAndView("admin/flowers/flowersList").addAllObjects(Map.of("flowers", flowers, "flowerDto", new FlowerDto()));
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
    public ModelAndView getFlowerById(@PathVariable int id) {
        ModelAndView result = new ModelAndView("admin/flowers/flowerView");
        FlowerDto flower = flowerMapper.toDto(flowerService.getFlower(id).orElseThrow());
        result.addObject("flower", flower);
        return result;
    }

    @PostMapping("/{id}")
    public ModelAndView changeFlowerName(@PathVariable int id, @RequestParam("inputName") String flowerName) {
        flowerService.update(new FlowerDto(id, flowerName));
        return new ModelAndView("redirect:/api/admin/flowers/" + id);
    }

}
