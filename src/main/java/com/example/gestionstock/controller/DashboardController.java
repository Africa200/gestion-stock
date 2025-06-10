package com.example.gestionstock.controller;

import com.example.gestionstock.repository.CategoryRepository;
import com.example.gestionstock.services.CommandeService;
import com.example.gestionstock.services.ProductService;
import com.example.gestionstock.services.impl.ProductServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class DashboardController {
    private final CategoryRepository categoryRepository;
    private ProductService productService;
    private CommandeService commandeService;

    public DashboardController(ProductService productService, CommandeService commandeService, CategoryRepository categoryRepository) {
        this.productService = productService;
        this.commandeService = commandeService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Simuler les données
        long totalItems = productService.countAllProducts();
        double inventoryValue = commandeService.getTotalPriceOfAllCommandes();
        long lowStockItems = productService.nombreProduitParEnStockFaible();
        long totalCategories = categoryRepository.count();

        Map<String, Integer> itemsByCategory = new LinkedHashMap<>();
        itemsByCategory.put("Electronics", 3);
        itemsByCategory.put("Office Supplies", 1);
        itemsByCategory.put("Furniture", 2);
        itemsByCategory.put("Others", 4);

        model.addAttribute("totalItems", totalItems);
        model.addAttribute("inventoryValue", inventoryValue);
        model.addAttribute("lowStockItems", lowStockItems);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("itemsByCategory", itemsByCategory);

        return "dashboard";
    }
}
