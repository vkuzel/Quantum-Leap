package cz.quantumleap.server.test_module.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Entity
public class TestEntity {
    @Id
    private long id;
    private List<Integer> flatArray;
    private List<List<Integer>> multidimensionalArray;
    private LocalDate date;
    private LocalTime time;
    private LocalDateTime dateTime;
    private Map<String, Object> json; // TODO Don't forget to specify generic type (for String)

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Integer> getFlatArray() {
        return flatArray;
    }

    public void setFlatArray(List<Integer> flatArray) {
        this.flatArray = flatArray;
    }

    public List<List<Integer>> getMultidimensionalArray() {
        return multidimensionalArray;
    }

    public void setMultidimensionalArray(List<List<Integer>> multidimensionalArray) {
        this.multidimensionalArray = multidimensionalArray;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<String, Object> getJson() {
        return json;
    }

    public void setJson(Map<String, Object> json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "id=" + id +
                ", arr=" + flatArray +
                ", multiArr=" + multidimensionalArray +
                ", date=" + date +
                ", time=" + time +
                ", dateTime=" + dateTime +
                ", json=" + json +
                '}';
    }
}
