package cz.quantumleap.core.person.domain;

import javax.validation.constraints.Pattern;

public class Person {

    private Long id;
    @Pattern(regexp = "^\\S+@\\S+\\.\\S+$", message = "{admin.table.core.person.email.pattern}")
    private String email;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
