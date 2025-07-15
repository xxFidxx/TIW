package it.polimi.tiw.beans;

public class Articolo {
    private Integer codice;
    private Integer userId;
    private String nome;
    private String descrizione;
    private String immagine;
    private Integer prezzo;
    private boolean disponibile;
    private Integer astaId;

    public Articolo() {}

    // costruttore default
    public Articolo(Integer userId, Integer codice, String nome, String descrizione, String immagine, Integer prezzo, boolean disponibile, Integer astaId) {
        this.userId = userId;
        this.codice = codice;
        this.nome = nome;
        this.descrizione = descrizione;
        this.immagine = immagine;
        this.prezzo = prezzo;
        this.disponibile = disponibile;
        this.astaId = astaId;
    }

    // costruttore per quando non hai codice ( prima di entrare nle db) e non hai asta id
    public Articolo(Integer userId, String nome, String descrizione, String immagine, Integer prezzo, boolean disponibile) {
        this.userId = userId;
        this.codice = null;
        this.nome = nome;
        this.descrizione = descrizione;
        this.immagine = immagine;
        this.prezzo = prezzo;
        this.disponibile = disponibile;
        this.astaId = null;
    }

    // costruttore pr fase in cui non hai astaId
    public Articolo(Integer userId, Integer codice, String nome, String descrizione, String immagine, Integer prezzo, boolean disponibile) {
        this.userId = userId;
        this.codice = codice;
        this.nome = nome;
        this.descrizione = descrizione;
        this.immagine = immagine;
        this.prezzo = prezzo;
        this.disponibile = disponibile;
        this.astaId = null;
    }

    public Integer getCodice() {
        return codice;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setCodice(Integer codice) {
        this.codice = codice;
    }

    public String getNome() {
        return nome;
    }



    public String getDescrizione() {
        return descrizione;
    }

    public String getImmagine() {
        return immagine;
    }



    public Integer getPrezzo() {
        return prezzo;
    }



    public boolean isDisponibile() {
        return disponibile;
    }


    public void setAstaId(Integer astaId){
        this.astaId = astaId;
    }

    public Integer getAstaId(){
        return astaId;
    }


}
