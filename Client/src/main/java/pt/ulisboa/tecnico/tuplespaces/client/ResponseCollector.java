package pt.ulisboa.tecnico.tuplespaces.client;

import java.util.ArrayList;
import java.util.List;

public class ResponseCollector {
    ArrayList<String> collectedResponses;

    public ResponseCollector() {
        collectedResponses = new ArrayList<String>();
    }

    synchronized public void addString(String s) {
        collectedResponses.add(s);
        notifyAll();
    }

    synchronized public void addAll(List<String> s) {
        collectedResponses.addAll(s);
        notifyAll();
    }

    synchronized public ArrayList<String> getStringsList() {
        return collectedResponses;
    }

    synchronized public ArrayList<String> getStrings() {
        if (!collectedResponses.isEmpty())
            return collectedResponses;
        return null;
    }

    synchronized public String getString() {
        if (!collectedResponses.isEmpty())
            return collectedResponses.get(0);
        return null;
    }

    synchronized public void waitUntilAllReceived(int n) throws InterruptedException {
        while (collectedResponses.size() < n) 
            wait();
    }
}
