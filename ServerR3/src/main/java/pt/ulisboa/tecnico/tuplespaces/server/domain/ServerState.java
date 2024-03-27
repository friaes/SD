package pt.ulisboa.tecnico.tuplespaces.server.domain;

import java.util.ArrayList;
import java.util.List;

public class ServerState {

  private Integer currentSeqNumber;
  private List<String> tuples;

  public ServerState() {
    this.tuples = new ArrayList<String>();
    this.currentSeqNumber = 1;
  }

  public void put(String tuple, Integer seqNumber) {
    synchronized (this) {
        waitSeqNumber(seqNumber);

        synchronized (this.tuples) {
            tuples.add(tuple);
            this.tuples.notifyAll(); 
        }

        incrementSeqNumber();
    }
}


  private synchronized String getMatchingTuple(String pattern) {
    for (String tuple : this.tuples) {
      if (tuple.matches(pattern)) {
        return tuple;
      }
    }
    return null;
  }

  public String read(String pattern) {
    String tuple = getMatchingTuple(pattern);
    
    synchronized(this.tuples) {
      while (tuple == null){
        try {
          wait(); // wait until the tuple is inserted
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        tuple = getMatchingTuple(pattern);
      }
    }

    return tuple;
  }

  public synchronized String take(String pattern, Integer seqNumber) {
    synchronized (this) {
      waitSeqNumber(seqNumber);

      String tuple = getMatchingTuple(pattern);

      synchronized(this.tuples) {
        if (tuple == null) incrementSeqNumber();
        while (tuple == null){
          try {
            wait(); // wait until the tuple is inserted
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          tuple = getMatchingTuple(pattern);
        }
        this.tuples.remove(tuple);
      }
      incrementSeqNumber();
      return tuple;
    }
  }

  public synchronized List<String> getTupleSpacesState() {
    return this.tuples;
  }

  private void waitSeqNumber(final int seqNumber) {
    synchronized (this) {
      while (this.currentSeqNumber != seqNumber) {
        try {
          this.wait();
        } catch (InterruptedException ignored) {}
      }
    }
  }

  public synchronized void incrementSeqNumber() {
    synchronized(this) {
      this.currentSeqNumber++;
      notifyAll(); 
    }
  }
}
