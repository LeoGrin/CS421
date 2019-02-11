package trees;

import java.util.*;

import com.sun.org.apache.xpath.internal.operations.Bool;
import graph.*;

public class SpanningTree {
    
    public static Collection<Edge> kruskal(UnionFind u, EuclideanGraph g){
    	List<Edge> col = g.getAllEdges();
    	List<Edge> res = new LinkedList<Edge>();
    	EdgeComparator ac = new EdgeComparator();
        Collections.sort(col, ac);

        for (Edge edge:col) {
            if (!u.find(edge.source).equals(u.find(edge.target))) {
                res.add(edge);
                u.union(edge.source, edge.target);
            }
        }

        return res;

    }


    
    public static Collection<Collection<Edge>> kruskal(EuclideanGraph g){
        Place rep;
        UnionFind u = new UnionFind();
        Collection<Edge> col = kruskal(u, g);
        HashMap<Place,Collection<Edge>> edgelist = new HashMap<Place,Collection<Edge>>();
        for (Edge edge:col){
            rep = u.find(edge.source);
            if (edgelist.get(rep) == null)
                edgelist.put(rep, new LinkedList<Edge>());
            edgelist.get(rep).add(edge);
        }

    	return edgelist.values();
    }


    public static Collection<Edge> primTree(HashSet<Place> nonVisited, Place start, EuclideanGraph g){
        Edge next_edge;
    	PriorityQueue<Edge> q = new PriorityQueue<Edge>(new EdgeComparator());
    	Collection<Edge> res = new LinkedList<Edge>();

    	nonVisited.remove(start);
        for (Edge e:g.edgesOut(start))
            q.add(e);
        int nb_visited = 1;

    	while(nb_visited < g.places().size() && q.peek()!=null){
            next_edge = q.poll();
            if (nonVisited.contains(next_edge.target)){
                nonVisited.remove(next_edge.target);
                nb_visited += 1;
                res.add(next_edge);
                for (Edge e:g.edgesOut(next_edge.target))
                    q.add(e);

            }
        }
    	return res;
    }
    
    public static Collection<Collection<Edge>> primForest(EuclideanGraph g){
        HashSet<Place> nonVisited = new HashSet<Place>(g.places());
        Collection<Collection<Edge>> res = new LinkedList<Collection<Edge>>();
        while (nonVisited.size() > 0){
            Place start = peek(nonVisited);
            Collection<Edge> to_add = primTree(nonVisited, start, g);
            if (to_add.size() > 0)
                res.add(to_add);
        }

    	return res;
    }

    public static Place peek(HashSet<Place> set){
        List<Place> list = new ArrayList<Place>(set);
        return list.get(0);

    }

    
   
}
