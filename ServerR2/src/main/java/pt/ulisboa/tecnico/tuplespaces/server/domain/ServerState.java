package pt.ulisboa.tecnico.tuplespaces.server.domain;

import java.util.ArrayList;
import java.util.List;

public class ServerState {

  private class TupleStruct {
    private String tuple;
    private boolean flag;
    private int clientId;

    public TupleStruct(String tuple) {
        this.tuple = tuple;
    }

    public String getTuple(){
      return this.tuple;
    }

    public boolean getFlag(){
      return this.flag;
    }

    public Integer getClientId(){
      return this.clientId;
    }

    public void setFlag(boolean flag){
      this.flag = flag;
    }

    public void setClientId(int clientId){
      this.clientId = clientId;
    }
  }


  private List<TupleStruct> tuples;

  public ServerState() {
    this.tuples = new ArrayList<TupleStruct>();

  }

  public synchronized void put(String tuple) {
    tuples.add(new TupleStruct(tuple));
    notifyAll();
  }

  private synchronized TupleStruct getMatchingTuple(String pattern) {
    for (TupleStruct tupleStruct : this.tuples) {
      if (tupleStruct.getTuple().matches(pattern)) {
        return tupleStruct;
      }
    }
    return null;
  }

  public synchronized String read(String pattern) {
    String tuple = getMatchingTuple(pattern).getTuple();
    
    while (tuple == null){
      try {
        wait(); // wait until the tuple is inserted
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      tuple = getMatchingTuple(pattern).getTuple();
    }

    return tuple;
  }

  public synchronized String take(String pattern) {
    TupleStruct tupleStruct = getMatchingTuple(pattern);
    while (tupleStruct.getTuple() == null){
      try {
        wait(); // wait until the tuple is inserted
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      tupleStruct = getMatchingTuple(pattern);
    }
    tuples.remove(tupleStruct);
    return tupleStruct.getTuple();
  }

  public synchronized List<String> getTupleSpacesState() {
    List<String> tuple_list = new ArrayList<String>();
    for (TupleStruct tupleStruct : this.tuples)
      tuple_list.add(tupleStruct.getTuple());
    
      return tuple_list;
  }

}
