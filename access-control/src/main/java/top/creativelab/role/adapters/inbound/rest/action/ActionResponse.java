package top.creativelab.role.adapters.inbound.rest.action;

import top.creativelab.role.domain.Action;

import java.util.UUID;

public record ActionResponse(String id, String name) {

    public static ActionResponse from(Action action){
        return new ActionResponse(action.getId().getValue().toString(), action.getName());
    }
}
