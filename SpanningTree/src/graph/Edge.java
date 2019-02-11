package graph;


public class Edge {
  public final Place source, target;
  public double length;

  public Edge(Place o, Place d, double c) {
    source = o;
    target = d;
    length = c;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Edge))
      return false;
    Place o2 = ((Edge) o).source;
    Place d2 = ((Edge) o).target;
    return (source == null && o2 == null || source.equals(o2))
        && (target == null && d2 == null || target.equals(d2))
        || (source == null && d2 == null || source.equals(d2))
        && (target == null && o2 == null || target.equals(o2));
  }

  @Override
  public int hashCode() {
    int ho = source == null ? 0 : source.hashCode();
    int hd = target == null ? 0 : target.hashCode();
    return ho ^ (ho - 1) * hd ^ (hd - 1);
  }

  @Override
  public String toString() {
    return source + " -- " + target;

  }
}
