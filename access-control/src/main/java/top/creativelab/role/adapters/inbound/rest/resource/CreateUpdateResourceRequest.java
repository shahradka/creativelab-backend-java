package top.creativelab.role.adapters.inbound.rest.resource;
import jakarta.validation.constraints.NotBlank;

record CreateUpdateResourceRequest (@NotBlank String name){
}
