package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Audited
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
    @NonNull
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private User user;

}
