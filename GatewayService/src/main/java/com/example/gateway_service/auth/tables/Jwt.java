package com.example.gateway_service.auth.tables;

import jakarta.persistence.*;

@Table(name = "jwt")
@Entity
public class Jwt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "value")
    private String value;

    @Column(name = "valid")
    private Boolean isValid;

    public boolean isValid() {
        return isValid;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }
}
