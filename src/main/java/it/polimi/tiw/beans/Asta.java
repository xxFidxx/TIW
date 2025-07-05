package it.polimi.tiw.beans;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Asta {
    private int id;
    private String venditoreUsername;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private BigDecimal prezzoIniziale;
    private int rialzoMinimo;
    private boolean chiusa;

    public Asta() {
    }

    public Asta(int id, String venditoreUsername, LocalDateTime dataInizio, LocalDateTime dataFine,
                BigDecimal prezzoIniziale, int rialzoMinimo, boolean chiusa) {
        this.id = id;
        this.venditoreUsername = venditoreUsername;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.prezzoIniziale = prezzoIniziale;
        this.rialzoMinimo = rialzoMinimo;
        this.chiusa = chiusa;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getVenditoreUsername() {
        return venditoreUsername;
    }

    public LocalDateTime getDataInizio() {
        return dataInizio;
    }

    public LocalDateTime getDataFine() {
        return dataFine;
    }

    public BigDecimal getPrezzoIniziale() {
        return prezzoIniziale;
    }

    public int getRialzoMinimo() {
        return rialzoMinimo;
    }

    public boolean isChiusa() {
        return chiusa;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setVenditoreUsername(String venditoreUsername) {
        this.venditoreUsername = venditoreUsername;
    }

    public void setDataInizio(LocalDateTime dataInizio) {
        this.dataInizio = dataInizio;
    }

    public void setDataFine(LocalDateTime dataFine) {
        this.dataFine = dataFine;
    }

    public void setPrezzoIniziale(BigDecimal prezzoIniziale) {
        this.prezzoIniziale = prezzoIniziale;
    }

    public void setRialzoMinimo(int rialzoMinimo) {
        this.rialzoMinimo = rialzoMinimo;
    }

    public void setChiusa(boolean chiusa) {
        this.chiusa = chiusa;
    }
}
