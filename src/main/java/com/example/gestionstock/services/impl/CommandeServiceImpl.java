package com.example.gestionstock.services.impl;

import com.example.gestionstock.dtos.CommadeDTO;
import com.example.gestionstock.dtos.ProductDTO;
import com.example.gestionstock.entity.Commande;
import com.example.gestionstock.entity.Product;
import com.example.gestionstock.mapper.CommandeMapper;
import com.example.gestionstock.mapper.ProductMapper;
import com.example.gestionstock.repository.CommandeRepository;
import com.example.gestionstock.services.CommandeService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CommandeServiceImpl implements CommandeService {
    private final CommandeRepository commandeRepository;
    private final ProductServiceImpl productService;

    public CommandeServiceImpl(CommandeRepository commandeRepository, ProductServiceImpl productService) {
        this.commandeRepository = commandeRepository;
        this.productService = productService;
    }

    @Override
    public CommadeDTO addCommande(CommadeDTO commandeDTO, List<Long> productIds) {

        Commande commande = CommandeMapper.toCommandeSansProduits(commandeDTO);

        List<Product> validatedProducts = productIds.stream()
                .map(id -> ProductMapper.toProduct(productService.getProductById(id)))
                .toList();

        commande.setProducts(validatedProducts);
        Commande saved = commandeRepository.save(commande);
        return CommandeMapper.toCommadeDTO(saved);
    }

    @Override
    public CommadeDTO getCommandeById(Long id) {
        Commande commande = commandeRepository.findById(id).orElse(null);
        if (commande == null) {
            throw new RuntimeException("Commande introuvable");
        }
        return CommandeMapper.toCommadeDTO(commande);
    }

    @Override
    public List<CommadeDTO> getAllCommande() {
        List<Commande> commandes = commandeRepository.findAll();
        return commandes.stream()
                .map(CommandeMapper::toCommadeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CommadeDTO updateCommade(CommadeDTO commandeDTO) {
        if (commandeDTO.getId() == null) {
            throw new RuntimeException("L'ID de la commande est requis pour la mise à jour.");
        }

        Optional<Commande> optional = commandeRepository.findById(commandeDTO.getId());
        if (optional.isEmpty()) {
            throw new RuntimeException("Commande introuvable pour mise à jour.");
        }

        Commande existingCommande = optional.get();
        existingCommande.setClientInfo(commandeDTO.getClientInfo());
        existingCommande.setPrixTotal(commandeDTO.getPrixTotal());

        List<Product> validatedProducts = new ArrayList<>();
        /*for (Product p : CommandeMapper.toCommadeDTO(commandeDTO).getProducts()) {
            Product productFromDB = ProductMapper.toProduct(productService.getProductById(p.getId()));
            if (productFromDB != null) {
                validatedProducts.add(productFromDB);
            }
        }*/

        existingCommande.setProducts(validatedProducts);
        Commande updated = commandeRepository.save(existingCommande);
        return CommandeMapper.toCommadeDTO(updated);
    }

    @Override
    public void deleteCommade(Long id) {
        commandeRepository.deleteById(id);
    }

    @Override
    public Double getTotalPriceOfAllCommandes() {
        List<Commande> commandes = commandeRepository.findAll();
        Double totalPrice = 0.0;
        List<Double> list = commandes.stream().map(c -> c.getPrixTotal()).toList();
        for (Double d : list) {
            totalPrice += d;
        }

        return totalPrice;
    }

    @Override
    public Double getTotalPriceOfCommande(Long id) {
        Commande commande = commandeRepository.findById(id).orElse(null);
        if(commande==null) return null;
        return commande.getPrixTotal();
    }

    @Override
    public byte[] genererFacturePourCommande(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));

        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            // Logo
            InputStream logoStream = new ClassPathResource("static/images/logo.png").getInputStream();
            Image logo = Image.getInstance(logoStream.readAllBytes());
            logo.scaleToFit(120, 60);
            logo.setAlignment(Image.ALIGN_RIGHT);
            document.add(logo);

            // Titre
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("FACTURE - RootManagement", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Infos client
            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph infos = new Paragraph();
            infos.add("Commande N°: " + commande.getId() + "\n");
            infos.add("Client : " + commande.getClientInfo().getClientName() + "\n");
            infos.add("Adresse : " + commande.getClientInfo().getClientAddress() + "\n");
            infos.add("Téléphone : " + commande.getClientInfo().getClientPhoneNumber() + "\n");
            infos.setSpacingAfter(15f);
            document.add(infos);

            // Tableau produits
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setWidths(new float[]{4, 2});

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            PdfPCell h1 = new PdfPCell(new Phrase("Produit", headFont));
            PdfPCell h2 = new PdfPCell(new Phrase("Prix (FCFA)", headFont));
            h1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            h2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            h1.setHorizontalAlignment(Element.ALIGN_CENTER);
            h2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(h1);
            table.addCell(h2);

            for (Product p : commande.getProducts()) {
                table.addCell(p.getLibelle());
                table.addCell(String.valueOf(p.getPrixUnitaire()));
            }

            document.add(table);

            // Total
            Paragraph total = new Paragraph("\nPrix total : " + commande.getPrixTotal() + " FCFA", infoFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur PDF : " + e.getMessage(), e);
        }

    }
}