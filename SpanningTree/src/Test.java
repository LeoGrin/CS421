import geomap.GeoMap;
import geomap.Visualisation;
import trees.SpanningTree;
import trees.UnionFind;
import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import graph.*;

public class Test {
	static EuclideanGraph g;
	static Place v1;
	static Place v2;
	static GeoMap geomap;

	static Place getFirst(Collection<Place> c) {
		for (Place v : c)
			return v;
		return null;
	}

	public static void initFrance(int distance){
		geomap = new GeoMap("fr.txt");
		v1 = geomap.premierePlace("Palaiseau");
		g = new EuclideanGraph(distance);
		for (Place v : geomap.places()) {
			g.addPlace(v);
		}
		g.connectNeighbours(distance);
	}

	public static boolean existeEdge(EuclideanGraph g, Place s, Place t) {
		for (Edge a : g.edgesOut(s))
			if (a.target.equals(t))
				return true;
		return false;
	}

	static final Color TRANSLUCENT_GRAY = new Color(192, 192, 192, 64);
	static final Color TRANSLUCENT_ORANGE = new Color(255, 200, 0, 128);

	public static void test0() {
		initFrance(4000);
		// test de reciprocite
		for (Place s : g.places())
			for (Edge a : g.edgesOut(s)) {
				if (!existeEdge(g, a.target, s))
					throw new Error("the edge " + a + " is not symmetric");
			}
		// affichage graphique
		Visualisation f = new Visualisation("Test 2");
		f.setOvalRadius(4);
		f.defineGraph(g);
		Map<Edge, Color> colors = new HashMap<Edge, Color>();
		for (Place s : g.places())
			for (Edge a : g.edgesOut(s))
				if (a.length < 1500)
					colors.put(a, Color.green);
				else colors.put(a, Color.red);
		f.colorEdges(colors);
		int edges = 0;
		for (Place v : g.places())
			edges += g.edgesOut(v).size();
		System.out.println("There are " + edges + " edges in this graph.");
	}
	
    // Q1: UnionFind
    public static void test1( ){
    	geomap = new GeoMap("fr.txt");
        UnionFind u = new UnionFind( );
        Place v0 = geomap.premierePlace( "Palaiseau" );
        Place v1 = geomap.premierePlace( "Paris" );
        Place v2 = geomap.premierePlace( "Melun" );
        Place v3 = geomap.premierePlace( "Orly" );
        Place v4 = geomap.premierePlace( "Massy" );
        Place v5 = geomap.premierePlace( "Clamart" );
        u.union( v0, v1 );
        u.union( v2, v3 );
		u.union( v4, v5 );
		u.union( v0, v3 );
		System.out.println( u.find( v0 ).equals( u.find( v2 ) ) );
        System.out.println( u.find( v0 ).equals( u.find( v1 ) ) );
        System.out.println( u.find( v3 ).equals( u.find( v5 ) ) );
    }
	
    public static void analyzeMonoColor( Collection<Edge> edges, boolean display, String title ) {
		int length = 0;
		Set<Place> places = new HashSet<Place>();
		for (Edge a : edges) {
			length += a.length;
			places.add(a.source);
			places.add(a.target);
		}
		System.out.println("Total length: " + " : " + length + " m");
		System.out.println(places.size() + " connected cities");
		if (!display)
			return;
		Map<Place, Color> colorPlaces = new HashMap<Place, Color>();
		Map<Edge, Color> colorEdges = new HashMap<Edge,
				Color>();
		for (Place v : places)
			colorPlaces.put(v, TRANSLUCENT_ORANGE);
	
		for (Edge a : edges)
			colorEdges.put(a, TRANSLUCENT_ORANGE);
		Visualisation f = new Visualisation(title);
		f.colorPlaces(colorPlaces);
		f.colorEdges(colorEdges);
		f.defineGraph(g);
	}
	


    public static void analyzeMultiColor( Collection<Collection<Edge>> listes, boolean display, String title ) {
		int length = 0;
		Set<Place> places = new HashSet<Place>();
		for (Collection<Edge> edges:listes){
			for (Edge a : edges) {
				length += a.length;
				places.add(a.source);
				places.add(a.target);
			}
		}
		System.out.println("Total length " + " : " + length + " m");
		System.out.println(places.size() + " connected cities");
		System.out.println(listes.size()+" components in the forest");
		if (!display)
			return;
		Map<Place, Color> colorPlaces = new HashMap<Place, Color>();
		Map<Edge, Color> colorEdges = new HashMap<Edge,Color>();
		for (Place v : places)
			colorPlaces.put(v, TRANSLUCENT_ORANGE);
		Random rand=new Random();
		for (Collection<Edge> edges:listes){
			Color c=new Color(rand.nextInt(256),rand.nextInt(256),rand.nextInt(256));
			for (Edge a : edges)
				colorEdges.put(a, c);
		}

		Visualisation f = new Visualisation(title);
		f.colorPlaces(colorPlaces);
		f.colorEdges(colorEdges);
		f.defineGraph(g);
	}
		
    // Q2: Kruskal all components together
    public static void test2(){
		initFrance(4000);
		long t0 = System.currentTimeMillis();
		Collection<Edge> edges = SpanningTree.kruskal(new UnionFind(), g);
		long tf = System.currentTimeMillis();
		System.out.println("Computation time (Kruskal first version) : " + (tf - t0) + " ms");
		analyzeMonoColor(edges, true, "Kruskal first version");
	}
    
    // Q3: Kruskal separating the components
    public static void test3(){
		initFrance(4000);
		long t0 = System.currentTimeMillis();
		Collection<Collection<Edge>> listes = SpanningTree.kruskal(g);      
		long tf = System.currentTimeMillis();
		System.out.println("Computation time (Kruskal second version) : " + (tf - t0) + " ms");
		analyzeMultiColor(listes, true, "Kruskal second version");
	}
	
    // Q4: Prim tree
    public static void test4(){
		initFrance(4000);
		HashSet<Place> nonVisited=new HashSet<Place>();
		for (Place v:g.places()) nonVisited.add(v);
		long t0 = System.currentTimeMillis();
		Place start=geomap.premierePlace("Palaiseau");
		Collection<Edge> edges = SpanningTree.primTree(nonVisited,start, g);
		long tf = System.currentTimeMillis();
		System.out.println("Computation time (Prim first version) : " + (tf - t0) + " ms");
		analyzeMonoColor(edges, true, "Prim first version");
	}

	// Q8: Prim forest
    public static void test5(){
		initFrance(4000);
		long t0 = System.currentTimeMillis();
		Collection<Collection<Edge>> listes = SpanningTree.primForest(g);
		long tf = System.currentTimeMillis();
		System.out.println("Computation time (Prim second version) : " + (tf - t0) + " ms");
		analyzeMultiColor(listes, true, "Prim second version");
    }
    
    public static void main(String[] args) {
        //test0();
        //test1();
        //test2();
        //test3();
        //test4();
        test5();
    }
}









