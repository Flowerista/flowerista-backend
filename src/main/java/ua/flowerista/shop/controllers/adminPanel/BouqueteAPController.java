package ua.flowerista.shop.controllers.adminPanel;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.flowerista.shop.dto.BouqueteDto;
import ua.flowerista.shop.mappers.BouqueteMapper;
import ua.flowerista.shop.models.Bouquete;
import ua.flowerista.shop.services.BouqueteService;

@Controller
@RequestMapping("/api/admin/bouquets")
@RequiredArgsConstructor
public class BouqueteAPController {
    private final BouqueteService bouqueteService;
    private final BouqueteMapper bouqueteMapper;

    @GetMapping
    public ModelAndView getBouqets(@QuerydslPredicate(root = Bouquete.class) Predicate predicate,
                                   @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                                   @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
                                   @RequestParam(name = "bouquetName",  defaultValue = "", required = false) String name) {
        System.out.println(name);
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

}
