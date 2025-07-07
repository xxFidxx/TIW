package it.polimi.tiw.beans;
import java.time.LocalDateTime;

public class Asta {
    private Integer id;
    private String venditoreUsername;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private Integer prezzoIniziale;
    private Integer prezzoAttuale;
    private Integer rialzoMinimo;
    private boolean chiusa;

    public Asta() {
    }

    public Asta(Integer id, String venditoreUsername, LocalDateTime dataInizio, LocalDateTime dataFine,
                Integer prezzoIniziale,Integer prezzoAttuale, Integer rialzoMinimo, boolean chiusa) {
        this.id = id;
        this.venditoreUsername = venditoreUsername;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.prezzoIniziale = prezzoIniziale;
        this.prezzoAttuale = prezzoAttuale;
        this.rialzoMinimo = rialzoMinimo;
        this.chiusa = chiusa;
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

    // Setters
    public void setId(Integer id) {
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
}
