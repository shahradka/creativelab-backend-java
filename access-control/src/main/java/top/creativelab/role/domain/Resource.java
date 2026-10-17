package top.creativelab.role.domain;

import lombok.Getter;
import lombok.Value;
import top.creativelab.role.domain.vo.ResourceId;

@Value
public class Resource {
    private final ResourceId id;
    private final String name;

    public static Resource create(ResourceId id, String name){

        if(id == null || name == null){
            throw new IllegalArgumentException("ResourceId or Name can not be null");
        }

        return new Resource(id, name);
    }

    public Resource updateName(String name){
        return new Resource(this.getId(), name);
    }
}
