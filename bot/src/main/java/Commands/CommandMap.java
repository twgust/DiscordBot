package Commands;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CommandMap {
    private Map<String, Command> cmdMap = Collections.synchronizedMap(new HashMap());
    private ArrayList<String> nameList = new ArrayList<>();

    public void put(String key, Command cmd){
        nameList.add(key);
        cmdMap.put(key, cmd);
    }

    public Object get(String key){
        return cmdMap.get(key);
    }

    public boolean containsKey(String key){
        return cmdMap.containsKey(key);
    }

    public ArrayList<String> getNameList() {
        return nameList;
    }
}
