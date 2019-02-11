package trees;

import graph.Place;

import java.util.HashMap;

// Q1

public class UnionFind {
	//parent relation, parent.put(src,dst) indicates that src points to dst
    private HashMap<Place,Place> parent;
    
    public UnionFind( ){
        parent = new HashMap<Place, Place>();

    }
    
    public Place find( Place src ){
        Place p1 = src;
        Place p2 = parent.get(src);
        if (p2 == null)
            return src;

    	while (p2 != null){

    	    p1 = p2;
    	    p2 = parent.get(p2);
        }
        parent.put(src, p1); //path compression
    	return p1;
    }
    
    public void union( Place v0, Place v1 ){
        Place rep0 = find(v0);
        Place rep1 = find(v1);
        if (rep1.equals(rep0))
                return;
    	else
    	    parent.put(rep0, rep1);
    }
}
