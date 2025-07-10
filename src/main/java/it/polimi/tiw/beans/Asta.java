package it.polimi.tiw.beans;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Asta {
    private Integer id;
    private String venditoreUsername;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private String dataFineFormattata;
    private Integer prezzoIniziale;
    private Integer prezzoAttuale;
    private Integer rialzoMinimo;
    private boolean chiusa;
    public Asta() {
    }

    public Asta(Integer id, String venditoreUsername, LocalDateTime dataInizio, LocalDateTime dataFine,
                Integer prezzoIniziale, Integer prezzoAttuale, Integer rialzoMinimo, boolean chiusa) {
        this.id = id;
        this.venditoreUsername = venditoreUsername;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.prezzoIniziale = prezzoIniziale;
        this.prezzoAttuale = prezzoAttuale;
        this.rialzoMinimo = rialzoMinimo;
        this.chiusa = chiusa;
    }

    // Costruttore per aste non ancora nel DB
    public Asta(String venditoreUsername, LocalDateTime dataInizio, LocalDateTime dataFine,
                Integer prezzoIniziale, Integer prezzoAttuale, Integer rialzoMinimo) {
        this(null, venditoreUsername, dataInizio, dataFine, prezzoIniziale, prezzoAttuale, rialzoMinimo, false);
    }

    // Getters
    public Integer getId() {
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

    public Integer getPrezzoIniziale() {
        return prezzoIniziale;
    }

    public Integer getRialzoMinimo() {
        return rialzoMinimo;
    }

    public boolean isChiusa() {
        return chiusa;
    }

    public Integer getPrezzoAttuale() {
        return prezzoAttuale;
    }

    public String getDataFineFormattata() {
        return dataFineFormattata;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setVenditoreUsername(String venditoreUsername) {
        this.venditoreUsername = venditoreUsername;
    }

    public void setPrezzoIniziale(Integer prezzoIniziale) {
        this.prezzoIniziale = prezzoIniziale;
    }

    public void setRialzoMinimo(Integer rialzoMinimo) {
        this.rialzoMinimo = rialzoMinimo;
    }

    public void setChiusa(boolean chiusa) {
        this.chiusa = chiusa;
    }

    public void setPrezzoAttuale(Integer prezzoAttuale) {
        this.prezzoAttuale = prezzoAttuale;
    }

    public void setDataFineFormattata(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.dataFineFormattata = dataFine.format(formatter);
    }

}
