package com.example.gestionstock.controller;

import com.example.gestionstock.dtos.CommadeDTO;
import com.example.gestionstock.dtos.ProductDTO;
import com.example.gestionstock.entity.Product;
import com.example.gestionstock.services.CommandeService;
import com.example.gestionstock.services.ProductService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;

@Controller
public class CommandeController {
    private  CommandeService commandeService;
    private ProductService productService;

    public CommandeController(CommandeService commandeService, ProductService productService) {
        this.commandeService = commandeService;
        this.productService = productService;
    }

    @GetMapping("/commandes")
    public String showCommandes(Model model) {
        model.addAttribute("commandes", commandeService.getAllCommande());
        model.addAttribute("products",productService.getAllProducts() );
        return "commandes";
    }
    @PostMapping("/commandes/add")
    public String addCommande(@ModelAttribute CommadeDTO commandeDTO,
                              @RequestParam(name = "productIds", required = false) List<Long> productIds,
                              @RequestParam(name = "quantities", required = false) List<Integer> quantities,
                              Model model, RedirectAttributes redirectAttributes, BindingResult bindingResult) {

        try {
            // Validation des paramètres
            if (productIds == null || quantities == null || productIds.isEmpty() || quantities.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Erreur technique. Veillez entrez des donnees valide");
                return "redirect:/commandes";
            }

            LinkedHashMap<Product, Integer> productMap = commandeService.matchProductsToCommande(productIds, quantities);

            // Vérification du stock
            commandeService.verifierQuantiteEnStock(productMap);

            Double montantTotal = commandeService.calculCommandePriceWithProduct(productMap);
            List<ProductDTO> produits = productService.getProductsByIds(productIds);

            commandeDTO.setProducts(produits);
            commandeDTO.setPrixTotal(montantTotal);
            commandeDTO.setProductIds(productIds);
            commandeDTO.setQuantities(quantities);

            CommadeDTO savedCommande = commandeService.addCommande(commandeDTO, productIds, productMap);

            redirectAttributes.addFlashAttribute("success", "Commande #" + savedCommande.getId() + " créée avec succès");
            redirectAttributes.addFlashAttribute("commadeDTO", new CommadeDTO());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.commadeDTO", bindingResult);
            redirectAttributes.addFlashAttribute("commadeDTO", commandeDTO);
            redirectAttributes.addFlashAttribute("productIds", productIds);
            redirectAttributes.addFlashAttribute("quantities", quantities);
            redirectAttributes.addFlashAttribute("error", e.getMessage());

            return "redirect:/commandes";
        }
        return "redirect:/commandes";
    }

    @PostMapping("/commandes/delete/{id}")
    public String deleteCommande(@PathVariable Long id) {
        commandeService.deleteCommade(id);
        return "redirect:/commandes";
    }

    @GetMapping("/commandes/details/{id}")
    public String voirDetails(@PathVariable Long id, Model model) {
        CommadeDTO commande = commandeService.getCommandeById(id);
        model.addAttribute("commande", commande);
        return "details";
    }

    @GetMapping("/commandes/facture/{id}")
    public ResponseEntity<byte[]> genererFacture(@PathVariable Long id) {
        CommadeDTO dto=commandeService.getCommandeById(id);
        List<Long> productIds=dto.getProductIds();
        List<Integer> quantities=dto.getQuantities();
        LinkedHashMap<Product, Integer> map = commandeService.matchProductsToCommande(productIds, quantities);
        byte[] pdf = commandeService.genererFacturePourCommande(id,map);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "facture_" + id + ".pdf");
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

}
