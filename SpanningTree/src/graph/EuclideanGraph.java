package graph;

import java.util.*;

public class EuclideanGraph {

  Map<Place, List<Edge>> toNeighbours;
  HashMap<Integer,HashMap<Integer,LinkedList<Place>>> vertexList;
  int distance;
  double angleDistance;

  public EuclideanGraph(int distance) { // Distance is measured in metres
    double R = 6371000; // Earth's radius
    this.distance = distance;
    // On calcule la distance en degres
    angleDistance = distance / Math.PI * 180.0 / R;
    vertexList = new HashMap<Integer,HashMap<Integer,LinkedList<Place>>>();
    toNeighbours = new LinkedHashMap<Place, List<Edge>>();
  }

  int caseX(Place s) {
    return (int) ((s.getLongitude()+180.0)/angleDistance);
  }

  int caseY(Place s) {
    return (int) ((s.getLatitude()+180.0)/angleDistance);
  }

  public boolean contains(Place v) {
    return toNeighbours.containsKey(v);
  }

  public Set<Place> places() {
    return toNeighbours.keySet();
  }

    public void addPlace(Place s) {
	if (!contains(s)){
            toNeighbours.put(s, new LinkedList<Edge>());
	    HashMap<Integer,LinkedList<Place>> column = vertexList.get(caseX(s));
	    if (column == null) { 
		column = new HashMap<Integer,LinkedList<Place>>();
		vertexList.put(caseX(s), column);
	    }
	    LinkedList<Place> cellule = column.get(caseY(s));
	    if (cellule == null){
		cellule = new LinkedList<Place>();
		column.put(caseY(s), cellule);
	    }
	    cellule.add(s);
	}
    }

      public void addEdge(Edge edge) {
    List<Edge> c = toNeighbours.get(edge.source);
    if (c == null)
      throw new IllegalArgumentException("Unknown place");
    c.add(edge);
  }

  public List<Edge> edgesOut(Place s){
      List<Edge> c = toNeighbours.get(s);
      if (c == null)
	  throw new IllegalArgumentException("Unknown place");
	  return Collections.unmodifiableList(c);
  }
  
  public List<Edge> getAllEdges(){
	  LinkedList<Edge> res = new LinkedList<Edge>();
      for( Place v: this.places()){
          for( Edge a: this.edgesOut( v ) ){
              res.add(a);
          }
      }
      return res;
  }

  public void connectNeighbours(double distEssence) {
    euclidean(); // On utilise le champ distance plutot que distEssence
  }

  Collection<Place> verticesAt(int x,int y) {
      if (!vertexList.containsKey(x))
	  return java.util.Collections.emptySet();
    HashMap<Integer,LinkedList<Place>> ll = vertexList.get(x);
    if (!ll.containsKey(y)) return java.util.Collections.emptySet();
    return ll.get(y);
  }
    
    public void euclidean() {
	// TODO A COMPLETER
	for (Place s : places()) {
	    int x = caseX(s), y = caseY(s);
	    for (int i=-1; i<=1; i++)
		for (int j=-1; j<=1; j++)
		    for (Place t : verticesAt (x+i, y+j))
			if (!s.equals(t) && s.distance(t) < distance)
			    addEdge(new Edge(s,t,s.distance(t)));
	}
    }
       
}
