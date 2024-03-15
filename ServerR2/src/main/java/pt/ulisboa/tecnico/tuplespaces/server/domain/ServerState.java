package pt.ulisboa.tecnico.tuplespaces.server.domain;

import java.util.ArrayList;
import java.util.List;

public class ServerState {

  private class TupleStruct {
    private String tuple;
    private boolean flag;
    private Integer clientId;

    public TupleStruct(String tuple) {
        this.tuple = tuple;
        this.flag = false;
        this.clientId = null;
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

    public void setClientId(Integer clientId){
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
    TupleStruct tupleStruct = getMatchingTuple(pattern);
    while (tupleStruct == null){
      try {
        wait(); // wait until the tuple is inserted
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      tupleStruct = getMatchingTuple(pattern);
    }

    return tupleStruct.getTuple();
  }

  public synchronized String takePhase1(String pattern, Integer clientId) {
    TupleStruct tupleStruct = getMatchingTuple(pattern);
    if (tupleStruct == null) {
      return "REFUSED";
    }

    if (tupleStruct.getFlag() && tupleStruct.getClientId() != null) {
      if (tupleStruct.getClientId().equals(clientId))
        return tupleStruct.getTuple();
      return "LOCKED";
    }

    tupleStruct.setFlag(true);
    tupleStruct.setClientId(clientId);
    return tupleStruct.getTuple();
  }

  public synchronized void takePhase1Release(String pattern, Integer clientId) {
    TupleStruct tupleStruct = getMatchingTuple(pattern);
    if (tupleStruct == null) return;
    if (tupleStruct.getClientId() != null && tupleStruct.getClientId().equals(clientId)) {
      tupleStruct.setFlag(false);
      tupleStruct.setClientId(null);
    }
  }

  public synchronized void takePhase2(String pattern, Integer clientId) {
    TupleStruct tupleStruct = getMatchingTuple(pattern);
    if (tupleStruct == null) return;
    if (tupleStruct.getClientId().equals(clientId)) {
      tuples.remove(tupleStruct);
    }
  }


  public synchronized List<String> getTupleSpacesState() {
    List<String> tuple_list = new ArrayList<String>();
    for (TupleStruct tupleStruct : this.tuples)
      tuple_list.add(tupleStruct.getTuple());
    
    return tuple_list;
  }

}
