package ua.flowerista.shop.controllers.adminPanel;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.flowerista.shop.dto.ColorDto;
import ua.flowerista.shop.dto.admin.ColorFullDto;
import ua.flowerista.shop.exceptions.AppException;
import ua.flowerista.shop.mappers.ColorMapper;
import ua.flowerista.shop.models.Color;
import ua.flowerista.shop.models.textContent.Languages;
import ua.flowerista.shop.services.ColorService;

import java.util.List;

@Controller
@RequestMapping("/api/admin/colors")
@RequiredArgsConstructor
public class ColorAPController {

    private final ColorService colorService;
    private final ColorMapper colorMapper;

    @GetMapping
    public ModelAndView getAll(@RequestParam(defaultValue = "en") Languages lang) {
        List<ColorDto> colors = colorMapper.toDto(colorService.getAllUncached(), lang);
        return new ModelAndView("admin/colors/colorsList").addObject("colors", colors);
    }

    @GetMapping("/{id}")
    public ModelAndView getById(@PathVariable Integer id) {
        ColorFullDto color = colorService.getById(id)
                .map(colorMapper::toAdminDto)
                .orElseThrow(() -> new AppException("Color not found", HttpStatus.BAD_REQUEST));
        return new ModelAndView("admin/colors/colorView").addObject("color", color);
    }

    @PostMapping("/{id}")
    public ModelAndView update(@ModelAttribute("color") ColorFullDto color) {
        colorService.update(colorMapper.toEntity(color));
        return new ModelAndView("redirect:/api/admin/colors/" + color.getId());
    }

    @PostMapping("/add")
    public ModelAndView add(@RequestParam("name") String name) {
        Color color = colorService.addColor(name);
        return new ModelAndView("redirect:/api/admin/colors/" + color.getId());
    }

    @ModelAttribute("color")
    private ColorFullDto color() {
        return new ColorFullDto();
    }

}
