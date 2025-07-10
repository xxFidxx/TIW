package it.polimi.tiw.beans;

public class Articolo {
    private Integer codice;
    private String usernameProprietario;
    private String nome;
    private String descrizione;
    private String immagine;
    private Integer prezzo;
    private boolean disponibile;
    private Integer astaId;

    public Articolo() {}

    // costruttore default
    public Articolo(String usernameProprietario, Integer codice, String nome, String descrizione, String immagine, Integer prezzo, boolean disponibile, Integer astaId) {
        this.usernameProprietario = usernameProprietario;
        this.codice = codice;
        this.nome = nome;
        this.descrizione = descrizione;
        this.immagine = immagine;
        this.prezzo = prezzo;
        this.disponibile = disponibile;
        this.astaId = astaId;
    }

    // costruttore per quando non hai codice ( prima di entrare nle db) e non hai asta id
    public Articolo(String usernameProprietario,String nome, String descrizione, String immagine, Integer prezzo, boolean disponibile) {
        this.usernameProprietario = usernameProprietario;
        this.codice = null;
        this.nome = nome;
        this.descrizione = descrizione;
        this.immagine = immagine;
        this.prezzo = prezzo;
        this.disponibile = disponibile;
        this.astaId = null;
    }

    // costruttore pr fase in cui non hai astaId
    public Articolo(String usernameProprietario ,Integer codice, String nome, String descrizione, String immagine, Integer prezzo, boolean disponibile) {
        this.usernameProprietario = usernameProprietario;
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

    public String getUsernameProprietario() {
        return usernameProprietario;
    }

    public void setCodice(Integer codice) {
        this.codice = codice;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public Integer getPrezzo() {
        return prezzo;
    }
    public Integer getPrezzoInt(){
        return prezzo.intValue();
    }

    public void setPrezzo(Integer prezzo) {
        this.prezzo = prezzo;
    }

    public boolean isDisponibile() {
        return disponibile;
    }

    public void setDisponibile(boolean disponibile) {
        this.disponibile = disponibile;
    }

    public void setAstaId(Integer astaId){
        this.astaId = astaId;
    }

    public Integer getAstaId(){
        return astaId;
    }


}
