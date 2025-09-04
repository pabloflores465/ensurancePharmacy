package com.sources.app.dto;

/**
 * DTO para la creación de medicamentos que encapsula los múltiples parámetros
 * necesarios para crear un nuevo medicamento.
 */
public class MedicineCreateRequest {
    private String name;
    private String activeMedicament;
    private String description;
    private String image;
    private String concentration;
    private Double presentacion;
    private Integer stock;
    private String brand;
    private Boolean prescription;
    private Double price;
    private Integer soldUnits;

    public MedicineCreateRequest() {}

    public MedicineCreateRequest(String name, String activeMedicament, String description, String image,
                               String concentration, Double presentacion, Integer stock, String brand,
                               Boolean prescription, Double price, Integer soldUnits) {
        this.name = name;
        this.activeMedicament = activeMedicament;
        this.description = description;
        this.image = image;
        this.concentration = concentration;
        this.presentacion = presentacion;
        this.stock = stock;
        this.brand = brand;
        this.prescription = prescription;
        this.price = price;
        this.soldUnits = soldUnits;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getActiveMedicament() { return activeMedicament; }
    public void setActiveMedicament(String activeMedicament) { this.activeMedicament = activeMedicament; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getConcentration() { return concentration; }
    public void setConcentration(String concentration) { this.concentration = concentration; }

    public Double getPresentacion() { return presentacion; }
    public void setPresentacion(Double presentacion) { this.presentacion = presentacion; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Boolean getPrescription() { return prescription; }
    public void setPrescription(Boolean prescription) { this.prescription = prescription; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getSoldUnits() { return soldUnits; }
    public void setSoldUnits(Integer soldUnits) { this.soldUnits = soldUnits; }
}
