package library;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ForEachCollector implements Consumer<String> {
    public int count = 0;
    public ArrayList<String> strings = new ArrayList<>();
    
    @Override
    public void accept(String string) {
        count++;
        strings.add(string);
    }
}