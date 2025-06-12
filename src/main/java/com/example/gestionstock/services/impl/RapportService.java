package com.example.gestionstock.services.impl;

import com.example.gestionstock.entity.Commande;
import com.example.gestionstock.entity.Product;
import com.example.gestionstock.repository.CommandeRepository;
import com.example.gestionstock.repository.ProductRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class RapportService {
    private ProductRepository productRepository;
    private CommandeRepository commandeRepository;


    public RapportService(ProductRepository repository, CommandeRepository commandeRepository) {
        this.productRepository = repository;
        this.commandeRepository = commandeRepository;
    }

    public byte[] genererRapportGlobal() {
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, out);

            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    try {
                        PdfContentByte canvas = writer.getDirectContentUnder();
                        Font watermarkFont = new Font(Font.FontFamily.HELVETICA, 52, Font.BOLD, new GrayColor(0.85f));
                        Phrase watermark = new Phrase("Rapport Global", watermarkFont);
                        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                                watermark, 297.5f, 421, 45);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            document.open();

            // Titre
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("Rapport Global de Gestion de Stock", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Chiffres globaux
            Font font = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph globalStats = new Paragraph();
            globalStats.add("Nombre total de produits : " + productRepository.count() + "\n");
            globalStats.add("Nombre total de commandes : " + commandeRepository.count() + "\n");
            java.util.List<Product> products= productRepository.findAll();
            int totalQuantity = products.stream().mapToInt(Product::getQuantiteEnStock).sum();

            globalStats.add("Valeur totale du stock : " + totalQuantity + " FCFA\n");
            globalStats.setSpacingAfter(15f);
            document.add(globalStats);

            // Tableau des produits
            Paragraph produitSection = new Paragraph("Produits :", titleFont);
            produitSection.setSpacingBefore(10f);
            document.add(produitSection);

            PdfPTable produitTable = new PdfPTable(4);
            produitTable.setWidthPercentage(100);
            produitTable.setWidths(new float[]{3, 2, 2, 2});
            String[] headers = {"Nom", "Catégorie", "Prix", "Stock"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, font));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                produitTable.addCell(cell);
            }

            java.util.List<Product> produits = productRepository.findAll();
            for (Product p : produits) {
                produitTable.addCell(p.getLibelle());
                produitTable.addCell(p.getCategory().getName());
                produitTable.addCell(String.format("%.2f", p.getPrixUnitaire()));
                produitTable.addCell(String.valueOf(p.getQuantiteEnStock()));
            }

            document.add(produitTable);

            // Tableau des commandes (simplifié)
            Paragraph commandeSection = new Paragraph("\nCommandes :", titleFont);
            document.add(commandeSection);

            PdfPTable commandeTable = new PdfPTable(3);
            commandeTable.setWidthPercentage(100);
            commandeTable.setWidths(new float[]{2, 3, 3});
            String[] cmdHeaders = {"N°", "Client", "Date"};
            for (String h : cmdHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(h, font));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                commandeTable.addCell(cell);
            }

            List<Commande> commandes = commandeRepository.findAll();
            for (Commande c : commandes) {
                commandeTable.addCell(String.valueOf(c.getId()));
                commandeTable.addCell(c.getClientInfo().getClientName());
            }

            document.add(commandeTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport PDF", e);
        }
    }

}
