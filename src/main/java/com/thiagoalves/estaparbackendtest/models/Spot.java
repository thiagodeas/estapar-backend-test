package com.thiagoalves.estaparbackendtest.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Spot {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sector_id")
    private Sector sector;

    private Double lat;
    private Double lng;
    private Boolean occupied;

    public Spot() {}

    public Spot(Long id, Sector sector, Double lat, Double lng, Boolean occupied) {
        this.id = id;
        this.sector = sector;
        this.lat = lat;
        this.lng = lng;
        this.occupied = occupied;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Sector getSector() { return sector; }
    public void setSector(Sector sector) { this.sector = sector; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
    public Boolean getOccupied() { return occupied; }
    public void setOccupied(Boolean occupied) { this.occupied = occupied; }
}
