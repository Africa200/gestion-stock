package com.example.gestionstock.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class ClientInfo {
    private String clientName;
    private String clientAddress;
    private String clientPhoneNumber;
}
