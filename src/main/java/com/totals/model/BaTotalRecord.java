package com.totals.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "ba_totals")
public class BaTotalRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "kenmerk")
    @NotBlank(message = "Kenmerk mag niet leeg zijn")
    @NotNull(message = "Kenmerk is verplicht")
    private String kenmerk;
    
    @Column(name = "totaal1")
    @NotNull(message = "Totaal1 is verplicht")
    private Double totaal1;
    
    @Column(name = "totaal2")
    @NotNull(message = "Totaal2 is verplicht")
    private Double totaal2;
    
    @Column(name = "totaal3")
    @NotNull(message = "Totaal3 is verplicht")
    private Double totaal3;
    
    @Column(name = "totaal4")
    @NotNull(message = "Totaal4 is verplicht")
    private Double totaal4;
    
    @Column(name = "totaal5")
    @NotNull(message = "Totaal5 is verplicht")
    private Double totaal5;
    
    @Column(name = "request_timestamp")
    @JsonIgnore
    private Long requestTimestamp;
    
    // Default constructor
    public BaTotalRecord() {}
    
    // Copy constructor
    public BaTotalRecord(String kenmerk, BaTotalRecord source) {
        this.kenmerk = kenmerk;
        this.totaal1 = source.totaal1;
        this.totaal2 = source.totaal2;
        this.totaal3 = source.totaal3;
        this.totaal4 = source.totaal4;
        this.totaal5 = source.totaal5;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getKenmerk() { return kenmerk; }
    public void setKenmerk(String kenmerk) { this.kenmerk = kenmerk; }
    
    public Double getTotaal1() { return totaal1; }
    public void setTotaal1(Double totaal1) { this.totaal1 = totaal1; }
    
    public Double getTotaal2() { return totaal2; }
    public void setTotaal2(Double totaal2) { this.totaal2 = totaal2; }
    
    public Double getTotaal3() { return totaal3; }
    public void setTotaal3(Double totaal3) { this.totaal3 = totaal3; }
    
    public Double getTotaal4() { return totaal4; }
    public void setTotaal4(Double totaal4) { this.totaal4 = totaal4; }
    
    public Double getTotaal5() { return totaal5; }
    public void setTotaal5(Double totaal5) { this.totaal5 = totaal5; }
    
    public Long getRequestTimestamp() { return requestTimestamp; }
    public void setRequestTimestamp(Long requestTimestamp) { this.requestTimestamp = requestTimestamp; }
}
