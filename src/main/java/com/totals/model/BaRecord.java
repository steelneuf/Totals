package com.totals.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "ba_records")
public class BaRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "kenmerk")
    @NotBlank(message = "Kenmerk mag niet leeg zijn")
    @NotNull(message = "Kenmerk is verplicht")
    private String kenmerk;
    
    @Column(name = "cijfer1")
    @NotNull(message = "Cijfer1 is verplicht")
    private Double cijfer1;
    
    @Column(name = "cijfer2")
    @NotNull(message = "Cijfer2 is verplicht")
    private Double cijfer2;
    
    @Column(name = "cijfer3")
    @NotNull(message = "Cijfer3 is verplicht")
    private Double cijfer3;
    
    @Column(name = "cijfer4")
    @NotNull(message = "Cijfer4 is verplicht")
    private Double cijfer4;
    
    @Column(name = "cijfer5")
    @NotNull(message = "Cijfer5 is verplicht")
    private Double cijfer5;
    
    @Column(name = "cijfer6")
    @NotNull(message = "Cijfer6 is verplicht")
    private Double cijfer6;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getKenmerk() { return kenmerk; }
    public void setKenmerk(String kenmerk) { this.kenmerk = kenmerk; }
    
    public Double getCijfer1() { return cijfer1; }
    public void setCijfer1(Double cijfer1) { this.cijfer1 = cijfer1; }
    
    public Double getCijfer2() { return cijfer2; }
    public void setCijfer2(Double cijfer2) { this.cijfer2 = cijfer2; }
    
    public Double getCijfer3() { return cijfer3; }
    public void setCijfer3(Double cijfer3) { this.cijfer3 = cijfer3; }
    
    public Double getCijfer4() { return cijfer4; }
    public void setCijfer4(Double cijfer4) { this.cijfer4 = cijfer4; }
    
    public Double getCijfer5() { return cijfer5; }
    public void setCijfer5(Double cijfer5) { this.cijfer5 = cijfer5; }
    
    public Double getCijfer6() { return cijfer6; }
    public void setCijfer6(Double cijfer6) { this.cijfer6 = cijfer6; }
}
