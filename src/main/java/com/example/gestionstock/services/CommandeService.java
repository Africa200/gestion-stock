package com.example.gestionstock.services;

import com.example.gestionstock.dtos.CommadeDTO;
import com.example.gestionstock.entity.Commande;

import javax.swing.*;
import java.util.List;

public interface CommandeService {
    // interface pour les service de gestions des commandes
    public CommadeDTO addCommande(CommadeDTO commande, List<Long> productIds);
    public CommadeDTO getCommandeById(Long id);
    public List<CommadeDTO> getAllCommande();
    public CommadeDTO updateCommade(CommadeDTO commande);
    public void deleteCommade(Long id);
    public Double getTotalPriceOfAllCommandes();
    public Double getTotalPriceOfCommande(Long id);


    byte[] genererFacturePourCommande(Long id);
}
