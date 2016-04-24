package cz.quantumleap.server.autoincrement.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Increment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "increment_seq_gen") // TODO Really? Commont this annotation mess? There must be some interceptor or default values!
    @SequenceGenerator(name = "increment_seq_gen", sequenceName = "increment_id_seq")
    private Long id;
    private String module;
    private int version;
    private String fileName;
//    @CreationTimestamp
    private LocalDateTime createdAt;

    public Increment() {
    }

    public Increment(String module, int version, String fileName) {
        this.module = module;
        this.version = version;
        this.fileName = fileName;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
