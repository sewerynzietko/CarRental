package org.example.web;

import org.example.models.VehicleCategoryConfig;
import org.example.services.RentalServiceInterface;
import org.example.services.impl.VehicleCategoryConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final VehicleCategoryConfigService configService;
    public CategoryController ( VehicleCategoryConfigService configService ) {
        this.configService = configService;
    }
    @GetMapping("/{category}")
    public VehicleCategoryConfig get( @PathVariable String category){
        return configService.getByCategory(category);
    }
    @GetMapping
    public List<VehicleCategoryConfig> list(){
        return configService.findAllCategories();
    }
}
