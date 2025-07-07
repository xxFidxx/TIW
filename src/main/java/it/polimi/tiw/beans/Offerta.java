package it.polimi.tiw.beans;
import java.time.LocalDateTime;

public class Offerta {
    private Integer id;
    private Integer astaId;
    private String utenteUsername;
    private Integer prezzo;
    private LocalDateTime dataOra;
    private boolean aggiudicata;

    public Offerta(Integer id, Integer astaId, String utenteUsername, Integer prezzo, LocalDateTime dataOra, boolean aggiudicata) {
        this.id = id;
        this.astaId = astaId;
        this.utenteUsername = utenteUsername;
        this.prezzo = prezzo;
        this.dataOra = dataOra;
        this.aggiudicata = aggiudicata;
    }

    public Offerta() {
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getAstaId() {
        return astaId;
    }
    public void setAstaId(Integer astaId) {
        this.astaId = astaId;
    }
    public String getUtenteUsername() {
        return utenteUsername;
    }
    public void setUtenteUsername(String utenteUsername) {
        this.utenteUsername = utenteUsername;
    }
    public Integer getPrezzo() {
        return prezzo;
    }
    public void setPrezzo(Integer prezzo) {
        this.prezzo = prezzo;
    }
    public LocalDateTime getDataOra() {
        return dataOra;
    }
    public void setDataOra(LocalDateTime dataOra) {
        this.dataOra = dataOra;
    }

    public void setAggiudicata(boolean aggiudicata) {
        this.aggiudicata = aggiudicata;
    }

    public boolean isAggiudicata() {
        return aggiudicata;
    }

}
