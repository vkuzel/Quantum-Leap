package cz.quantumleap.server.i18n.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Language {

    @Id
    private String isoCode; // TODO ISO 639-1 code (Hibernate's improved naming strategy has problems with names containing numbers)
    private String name;

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
