package com.example.gestionstock.controller;

import com.example.gestionstock.dtos.CommadeDTO;
import com.example.gestionstock.dtos.ProductDTO;
import com.example.gestionstock.entity.Commande;
import com.example.gestionstock.services.CommandeService;
import com.example.gestionstock.services.ProductService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
                              @RequestParam(name = "productIds", required = false) List<Long> productIds) {
        List<ProductDTO> produits = productService.getProductsByIds(commandeDTO.getProductIds());
        //produits.stream().map(p-> p.getPrixUnitaire()*commandeDTO).toList();
        commandeDTO.setProducts(produits);
        commandeService.addCommande(commandeDTO, productIds);

        return "redirect:/commandes";
    }

    @PostMapping("/commandes/edit")
    public String updateCommande(@ModelAttribute CommadeDTO commandeDTO) {
        commandeService.updateCommade(commandeDTO);
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
        byte[] pdf = commandeService.genererFacturePourCommande(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "facture_" + id + ".pdf");
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

}
