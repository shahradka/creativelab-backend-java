package top.creativelab.role.domain;

import lombok.Value;
import top.creativelab.role.domain.vo.ActionId;
import top.creativelab.role.domain.vo.ResourceId;

@Value
public class Action {
    private final ActionId id;
    private final String name;

    public static Action create(ActionId id, String name){

        if(id == null || name == null){
            throw new IllegalArgumentException("ActionId or Name can not be null");
        }
        
        return new Action(id, name);
    }

    public Action updateName(String newName){
        return new Action(this.id, newName);
    }

}
