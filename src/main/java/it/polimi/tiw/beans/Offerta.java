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

    // Getters e Setters
    // (puoi generarli automaticamente se usi un IDE come IntelliJ o Eclipse)
}
