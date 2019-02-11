package geomap;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import graph.*;

public class Visualisation extends JFrame {

  private static final long serialVersionUID = 1L;
  Set<Place> vertices = null;
 // EuclideanGraph graph = null;
  Collection<Edge> edges = null;
  Map<Place, Color> colorPlaces = null;
  Map<Edge, Color> colorEdges = null;
  double latmin = -180, latmax = 180, lonmin = -90, lonmax = 90;
  JComponent dessin;
  JPanel panel;
  // JComboBox_ boite;
  boolean afficheNoms = false;
  int ovalRadius =1;

  void toCenter(double x, double y) {
    double deltaX = (latmax + latmin) / 2.0
        - (latmin + (latmax - latmin) * (1 - y));
    double deltaY = (lonmax + lonmin) / 2.0 - (lonmin + (lonmax - lonmin) * x);
    System.out.println(x + " " + y + " " + deltaX + " " + deltaY);
    System.out.println(lonmin + " " + lonmax + " " + latmin + " " + latmax);
    latmin -= deltaX;
    latmax -= deltaX;
    lonmin -= deltaY;
    lonmax -= deltaY;
    System.out.println(lonmin + " " + lonmax + " " + latmin + " " + latmax);
    repaint();
  }

  void zoomIn() {
    double centreX = (latmin + latmax) / 2.0;
    double centreY = (lonmin + lonmax) / 2.0;
    double deltaX = latmax - centreX;
    double deltaY = lonmax - centreY;
    latmin = centreX - .9 * deltaX;
    latmax = centreX + .9 * deltaX;
    lonmin = centreY - .9 * deltaY;
    lonmax = centreY + .9 * deltaY;
    repaint();
  }

  void zoomOut() {
    double centreX = (latmin + latmax) / 2.0;
    double centreY = (lonmin + lonmax) / 2.0;
    double deltaX = latmax - centreX;
    double deltaY = lonmax - centreY;
    latmin = centreX - deltaX / .9;
    latmax = centreX + deltaX / .9;
    lonmin = centreY - deltaY / .9;
    lonmax = centreY + deltaY / .9;
    repaint();
  }
  
  public void setOvalRadius(int i){
	  ovalRadius=i;
  }
  
  public int getOvalRadius(){
	  return ovalRadius;
  }

  public Visualisation(String titre) {
    super(titre);
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    System.setProperty("swing.aatext", "true");
    dessin = new DrawingPane(this);
    add("Center", dessin);
    JMenuBar menuBar = new JMenuBar();
    JMenu mFichier = new JMenu("File");
    menuBar.add(mFichier);
    JMenuItem itemQuit = new JMenuItem("Quit");
    mFichier.add(itemQuit);
    itemQuit.addActionListener(new ActionListener() {
      
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });

    JMenu mAffiche = new JMenu("Show");
    menuBar.add(mAffiche);
    JMenuItem item = new JMenuItem("Show place names");
    mAffiche.add(item);
    item.addActionListener(new ActionListener() {
      
      public void actionPerformed(ActionEvent e) {
        afficheNoms(!afficheNoms);
        repaint();
      }
    });
    JMenuItem itemCadrage = new JMenuItem("Initial zoom");
    mAffiche.add(itemCadrage);
    itemCadrage.addActionListener(new ActionListener() {
      
      public void actionPerformed(ActionEvent e) {
        calculLimites();
        repaint();
      }
    });
    setJMenuBar(menuBar);
    panel = new JPanel();
    add("South", panel);
    defineVertices(new HashSet<Place>());
    synchronized(getTreeLock())
    {validateTree();}
    setVisible(true);
  }

  public void defineVertices(Set<Place> places) {
    panel.removeAll();
    vertices = places;
    calculLimites();

    // if (!vertices.isEmpty()) {
      Vector<String> tab = new Vector<String>(vertices.size());
      for (Place v : vertices) {
        tab.add(v.getName());
      }
      if (vertices.isEmpty()) tab.add("No vertex");
      // JPanel panel = new JPanel();
      final JComboBox boite = new JComboBox_(tab);
      panel.add(boite);

      JButton valide = new JButton("OK");
      valide.addActionListener(new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
          // On se centre sur la place
          double deltaLat2 = (latmax - latmin) / 2.0;
          double deltalon2 = (lonmax - lonmin) / 2.0;
          String s = (String) boite.getSelectedItem();
          Place v = null;
          for (Place vv : vertices)
            if (vv.getName().equals(s))
              v = vv;
          if (v != null) {
            double latCentre = v.getLatitude();
            double lonCentre = v.getLongitude();
            latmin = latCentre - deltaLat2;
            latmax = latCentre + deltaLat2;
            lonmin = lonCentre - deltalon2;
            lonmax = lonCentre + deltalon2;
            repaint();
          }
        }
      });
      panel.add(valide);
      panel.validate();
      dessin.repaint();
      // add("South", panel);
      // invalidate();
      // validateTree();
    // }
  }

  public void colorPlaces(Map<Place, Color> c) {
    colorPlaces = c;
    dessin.repaint();
  }

  public void defineEdges(Collection<Edge> a) {
    edges = a;
    panel.validate();
  }

  public void defineGraph(EuclideanGraph g) {
    // graph = g;
     Collection<Edge> newEdges = new LinkedList<Edge>();
     for (Place v:g.places()) for (Edge a :g.edgesOut(v)) newEdges.add(a);
     edges = newEdges;
     defineVertices(g.places());
  }

  public void colorEdges(Map<Edge, Color> c) {
    colorEdges = c;
    dessin.repaint();
  }

  void afficheNoms(boolean b) {
    afficheNoms = b;
  }

  void calculLimites() {
    double llatmin = 0, llatmax = 0, llonmin = 0, llonmax = 0;
    int i = 0;

    if (vertices == null || vertices.size() == 0)
      return;

    for (Place v : vertices) {
      if (i == 0) {
        llatmin = v.getLatitude();
        llatmax = v.getLatitude();
        llonmin = v.getLongitude();
        llonmax = v.getLongitude();
      }
      i++;

      if (v.getLatitude() < llatmin)
        llatmin = v.getLatitude();
      if (v.getLatitude() > llatmax)
        llatmax = v.getLatitude();
      if (v.getLongitude() < llonmin)
        llonmin = v.getLongitude();
      if (v.getLongitude() > llonmax)
        llonmax = v.getLongitude();
    }
    latmin = llatmin;
    latmax = llatmax;
    lonmin = llonmin;
    lonmax = llonmax;
  }
}

class DrawingPane extends JComponent implements MouseListener {
  private static final long serialVersionUID = 1L;
  Visualisation window;

  DrawingPane(Visualisation f) {
    window = f;
    setOpaque(true);
    setBackground(Color.white);
    addMouseListener(this);
  }

  
  public void paintComponent(Graphics g) {
	Color savedColor = g.getColor();
	Color TRANSLUCENT_GRAY = new Color(192, 192, 192, 64);
	if (window.colorPlaces!=null) savedColor=TRANSLUCENT_GRAY;
    super.paintComponent(g);
    if (g instanceof Graphics2D) {
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
    }
    if (window.vertices == null)
      return;
    
    // On affiche les aretes
    // if (window.graph != null) {
    if (window.edges != null) {
      //if (window.colorEdges != null) {
        // d'abord avec la color par defaut
        //Color defColor = window.colorEdges.get(null);
        //if (defColor != null)
        //  g.setColor(defColor);
      //}
    	
      g.setColor(savedColor);
      // toutes les aretes
      for (Edge a : window.edges)
        if(a.source!=null && a.target!=null)
        a.source.drawEdge(g, window.latmin, window.lonmin, window.latmax,
            window.lonmax, getWidth(), getHeight(), a.target);
      /*
      for (Place v : window.vertices) {
        Collection<Edge> voisins = window.graph.edgesOut(v);
        if (voisins != null)
          for (Edge a : voisins) {
            v.drawEdge(g, window.latmin, window.lonmin, window.latmax,
                window.lonmax, getWidth(), getHeight(), a.target);
          }
      }
      */
      // puis on retrace les aretes qui ont une color particuliere
      if (window.colorEdges != null)
        for (Map.Entry<Edge, Color> entry : window.colorEdges.entrySet()) {
          Edge a = entry.getKey();
          
          g.setColor(entry.getValue());
          a.source.drawEdge(g, window.latmin, window.lonmin,
              window.latmax, window.lonmax, getWidth(), getHeight(),
              a.target);
        }
      g.setColor(savedColor);
    }
    // puis les places
    for (Place v : window.vertices) {
      Color c = null;
      if (window.colorPlaces != null)
        c = window.colorPlaces.get(v);
      if (c != null)
        g.setColor(c);
      else
        g.setColor(savedColor);
      v.draw(g, window.latmin, window.lonmin, window.latmax,
          window.lonmax, getWidth(), getHeight(), window.afficheNoms,window.getOvalRadius());
    }
    g.setColor(savedColor);
  }

  
  public void mouseClicked(MouseEvent e) {
    int bouton = e.getButton();
    if (bouton == MouseEvent.BUTTON1)
      window.zoomIn();
    else if (bouton == MouseEvent.BUTTON3)
      window.zoomOut();
    else
      window.toCenter(((double) e.getX()) / getWidth(), ((double) e.getY())
          / getHeight());
  }

  
  public void mouseEntered(MouseEvent e) { //
  }

  
  public void mouseExited(MouseEvent e) { //
  }

  
  public void mousePressed(MouseEvent e) { //
  }

  
  public void mouseReleased(MouseEvent e) { //
  }
}

class JComboBox_ extends JComboBox {
  /**
     * 
     */
  private static final long serialVersionUID = 1L;
  public int caretPos = 0;
  public JTextField tf = null;

  // public JComboBox_(final Object items[]) {
  public JComboBox_(final Vector<String> items) {
    super(items);
    this.setEditor(new BasicComboBoxEditor());
    this.setEditable(true);
  }

  
  public void setSelectedIndex(int ind) {
    super.setSelectedIndex(ind);
    tf.setText(getItemAt(ind).toString());
    tf.setSelectionEnd(caretPos + tf.getText().length());
    tf.moveCaretPosition(caretPos);
  }

  
  public void setEditor(ComboBoxEditor anEditor) {
    super.setEditor(anEditor);
    if (anEditor.getEditorComponent() instanceof JTextField) {
      tf = (JTextField) anEditor.getEditorComponent();
      tf.addKeyListener(new KeyAdapter() {
        
        public void keyReleased(KeyEvent ev) {
          char key = ev.getKeyChar();
          if (!(Character.isLetterOrDigit(key) || Character.isSpaceChar(key)))
            return;
          caretPos = tf.getCaretPosition();
          String text = "";
          try {
            text = tf.getText(0, caretPos);
          } catch (Exception ex) {
            ex.printStackTrace();
          }
          int n = getItemCount();
          for (int i = 0; i < n; i++) {
            int ind = ((String) getItemAt(i)).indexOf(text);
            if (ind == 0) {
              setSelectedIndex(i);
              return;
            }
          }
        }
      });
    }
  }

}
