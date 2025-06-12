package com.example.gestionstock.dtos;

import com.example.gestionstock.entity.ClientInfo;
import com.example.gestionstock.entity.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommadeDTO {
    private Long id;
    @Embedded
    private ClientInfo clientInfo;
    private Double prixTotal;
    @Transient
    private List<ProductDTO> products;
    private List<Long> productIds;
    private List<Integer> quantities;
}
