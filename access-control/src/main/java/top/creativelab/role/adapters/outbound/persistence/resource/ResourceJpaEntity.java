package top.creativelab.role.adapters.outbound.persistence.resource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import persistance.JpaEntityBaseModel;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "resources")
public class ResourceJpaEntity extends JpaEntityBaseModel {

    @Column(nullable = false)
    private String name;

    public ResourceJpaEntity(UUID id, String name){
        super(id);
        this.name = name;
    }

}
