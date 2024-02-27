package pt.ulisboa.tecnico.tuplespaces.server.domain;

import java.util.ArrayList;
import java.util.List;

public class ServerState {

  private List<String> tuples;

  public ServerState() {
    this.tuples = new ArrayList<String>();

  }

  public void put(String tuple) {
    tuples.add(tuple);
  }

  private String getMatchingTuple(String pattern) {
    for (String tuple : this.tuples) {
      if (tuple.matches(pattern)) {
        return tuple;
      }
    }
    return null;
  }

  public String read(String pattern) {
    return getMatchingTuple(pattern);
  }

  public String take(String pattern) {
    String tuple = getMatchingTuple(pattern);
    tuples.remove(tuple);
    return tuple;
  }

  public List<String> getTupleSpacesState() {
    return this.tuples;
  }

  //MIGHT BE THIS IMPLEMENTATION THAT IS CORRECT
  /*private List<String> getMatchingTuple(String pattern) {
    List<String> matchingTuples = new ArrayList<String>();
    for (String tuple : this.tuples) {
      if (tuple.matches(pattern)) {
        matchingTuples.add(tuple);
      }
    }
    return matchingTuples;
  }

  public List<String> read(String pattern) {
    return getMatchingTuple(pattern);
  } 

  public List<String> take(String pattern) {
    List<String> matchingTuples = new ArrayList<String>();
    matchingTuples = removeTuples(pattern);
    return matchingTuples;
  }

  private List<String> removeTuples(String pattern){
    List<String> matchingTuples = new ArrayList<String>();
    for (String tuple : this.tuples) {
      if (tuple.matches(pattern)) {
        matchingTuples.add(tuple);
        tuples.remove(tuple);
      }
    }
    return matchingTuples;
    }*/

  }
