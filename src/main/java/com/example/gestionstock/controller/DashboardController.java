package com.example.gestionstock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class DashboardController {
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Simuler les données
        int totalItems = 227;
        double inventoryValue = 24327.73;
        int lowStockItems = 0;
        int totalCategories = 5;

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
