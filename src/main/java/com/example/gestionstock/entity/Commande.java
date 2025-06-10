package com.example.gestionstock.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Commande {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private ClientInfo clientInfo;
    private Double prixTotal;
    @ManyToMany
    private List<Product> products =new ArrayList<>();

}
