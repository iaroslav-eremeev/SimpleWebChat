package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "messageText", nullable = false)
    @NonNull
    private String messageText;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @Cascade(value = org.hibernate.annotations.CascadeType.DELETE)
    private User user;
}
