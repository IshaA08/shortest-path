import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Random;

/*
 * A Maze visualizer that visualizes an n x n maze. Methods are
 * provided to specify edges. Additionally, one or more paths
 * can also be drawn.
 */

public class MazeVisualizer extends JApplet {
    
    final static Color bg = Color.white;
    final static Color fg = Color.black;
    
    // 12 distinct colors to use for drawing paths.
    // obtained from www.colorbrewer2.org
    private static int[] qColors = {
        166,206,227,
        31,120,180,
        178,223,138,
        51,160,44,
        251,154,153,
        227,26,28,
        253,191,111,
        255,127,0,
        202,178,214,
        106,61,154,
        255,255,153,
        177,89,40
    };
    private int cOffset; // to select starting color
    
    // The number of vertices in one direction of the grid
    private int N;
    
    // list of edges to be drawn
    private List<Pair<Integer, Integer>> edgeList = null;
    
    // list of paths to be drawn
    private List<List<Integer>> pathList = null;
    
    /**
     * Creates a maze visualizer for an n x n square maze with
     * no edges.
     * @param n The number of vertices in the horizontal (and
     * vertical) direction.
     */
    public MazeVisualizer( int n ) {
        this.N = n;
        edgeList = new LinkedList<>();
        pathList = new LinkedList<>();
        Random rg = new Random();
        cOffset = 3 * rg.nextInt(qColors.length/3);
    }
    
    /**
     * Performs any necessary initialization before drawing
     * begins.
     */
    public void init() {
        setBackground(bg);
        setForeground(fg);
    }
    
    
    /**
     * Method to add an edge to be visualized. Each edge is
     * visualized as a line.
     * @ param from The vertex where the edge starts.
     * @ para to The vertex where the edge ends.
     */
    public void addEdge( int from, int to ) {
        edgeList.add( new Pair<Integer, Integer>( from, to) );
    }
    
    /**
     * Method to add a path to be visualized. The path is
     * specified as a list of integer nodes. The path is
     * assumed to be a valid path in the maze.
     * @ param nodeList List of nodes that make up the path
     */
    public void addPath( List<Integer> nodeList ) {
        LinkedList<Integer> path = new LinkedList<>();
        for( Integer i : nodeList ) {
            path.add( new Integer( i ) );
        }
        pathList.add( path );
    }
    
    
    /**
     * The paint method for this applet. Do not call this method,
     * it is called automatically whenever the applet needs to
     * draw its content.
     */
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform tform = AffineTransform.getTranslateInstance( 0, getHeight());
        tform.scale( getWidth()/(N+1), -getHeight()/(N+1) );
        g2.setTransform( tform);
        
        float radius = 0.125f;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw the edges
        g2.setColor( Color.LIGHT_GRAY );
        float LINE_THICKNESS = 0.05f;
        float lo = -0.5f * LINE_THICKNESS;
        g2.setStroke(new BasicStroke(LINE_THICKNESS));
        for( Pair<Integer,Integer> edge : edgeList ) {
            g2.draw( new Line2D.Double(lo+1+(edge.x-1)/N,lo+1+(edge.x-1)%N, lo+1+(edge.y-1)/N, lo+1+(edge.y-1)%N ));
        }
    
        // Draw paths if any
        g2.setStroke(new BasicStroke(2.0f*LINE_THICKNESS));
        if( pathList != null ) {
            int r = cOffset;
            for( List<Integer> path : pathList ) {
                g2.setColor( new Color(qColors[r],qColors[r+1],qColors[r+2],127) );
                for( int i = 0; i < path.size() - 1; i++ ) {
                    g2.draw( new Line2D.Double(lo+1+(path.get(i)-1)/N,lo+1+(path.get(i)-1)%N, lo+1+(path.get(i+1)-1)/N, lo+1+(path.get(i+1)-1)%N ));
                }
                r = (r + 3) % qColors.length;
            }
        }
        
        
        // Draw a grid of points representing the vertices.
        g2.setColor( Color.DARK_GRAY );
        float offset = (float)Math.sqrt(2.0) * radius;
        for( int i = 1; i <= N; i++ ) {
            for( int j = 1; j <= N; j++) {
                g2.fill( new Ellipse2D.Float(
                                             i - offset, j - offset, 2*radius, 2*radius) );
            }
        }
    }
    
} // End of MazeVisualizer
