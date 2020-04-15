package ModerationModule;

import Commands.Command;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModCommandMap {
    private Map<String, ModCommand> modCmdMap = Collections.synchronizedMap(new HashMap());

    public void put(String key, ModCommand cmd){
        modCmdMap.put(key, cmd);
    }

    public ModCommand get(String key){
        return modCmdMap.get(key);
    }

    public boolean containsKey(String key){
        return modCmdMap.containsKey(key);
    }
}
