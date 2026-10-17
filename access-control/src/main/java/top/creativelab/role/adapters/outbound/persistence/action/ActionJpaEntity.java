package top.creativelab.role.adapters.outbound.persistence.action;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import persistance.JpaEntityBaseModel;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="actions")
public class ActionJpaEntity extends JpaEntityBaseModel {

    @Column(nullable = false)
    private String name;

    ActionJpaEntity(UUID id, String name){
        super(id);
        this.name = name;
    }
}
