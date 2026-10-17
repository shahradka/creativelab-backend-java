package top.creativelab.role.adapters.inbound.rest.resource;

import top.creativelab.role.domain.Resource;
import top.creativelab.role.domain.vo.ResourceId;

public record ResourceResponse(String id, String name) {

    public static ResourceResponse from(Resource resource){
        return new ResourceResponse(resource.getId().getValue().toString(), resource.getName());
    }

}
