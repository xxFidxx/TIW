package it.polimi.tiw.beans;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Offerta {
    private int id;
    private int astaId;
    private String utenteUsername;
    private BigDecimal prezzo;
    private LocalDateTime dataOra;

    public Offerta(int id, int astaId, String utenteUsername, BigDecimal prezzo, LocalDateTime dataOra) {
        this.id = id;
        this.astaId = astaId;
        this.utenteUsername = utenteUsername;
        this.prezzo = prezzo;
        this.dataOra = dataOra;
    }

    public Offerta() {
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getAstaId() {
        return astaId;
    }
    public void setAstaId(int astaId) {
        this.astaId = astaId;
    }
    public String getUtenteUsername() {
        return utenteUsername;
    }
    public void setUtenteUsername(String utenteUsername) {
        this.utenteUsername = utenteUsername;
    }
    public BigDecimal getPrezzo() {
        return prezzo;
    }
    public void setPrezzo(BigDecimal prezzo) {
        this.prezzo = prezzo;
    }
    public LocalDateTime getDataOra() {
        return dataOra;
    }
    public void setDataOra(LocalDateTime dataOra) {
        this.dataOra = dataOra;
    }

}
