package Commands;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CommandMap {
    private Map cmdMap = Collections.synchronizedMap(new HashMap());

    public void put(String key, Command cmd){
        cmdMap.put(key, cmd);
    }

    public Object get(String key){
        return cmdMap.get(key);
    }

    public boolean containsKey(String key){
        return cmdMap.containsKey(key);
    }
}
