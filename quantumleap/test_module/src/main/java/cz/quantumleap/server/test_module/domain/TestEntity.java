package cz.quantumleap.server.test_module.domain;

import cz.quantumleap.server.persistence.hibernate.CreatedAt;
import cz.quantumleap.server.persistence.hibernate.CreatedBy;
import cz.quantumleap.server.persistence.hibernate.UpdatedAt;
import cz.quantumleap.server.persistence.hibernate.UpdatedBy;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "test_entity_seq_gen")
    @SequenceGenerator(name = "test_entity_seq_gen", sequenceName = "test_entity_id_seq")
    private long id;
    private String comment;
    @CreatedAt
    private LocalDateTime createdAt;
    @CreatedBy
    private long createdBy;
    @UpdatedAt
    private LocalDateTime updatedAt;
    @UpdatedBy
    private Long updatedBy;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }
}
