/*
 * (C) Copyright 2013-2017, by Barak Naveh and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package trash;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JApplet;
import javax.swing.JFrame;

import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.swing.mxGraphComponent;

import element.Goal;
import element.Relation;
import real.JSonLoaderExample;

/**
 * A demo applet that shows how to use JGraphX to visualize JGraphT graphs. Applet based on
 * JGraphAdapterDemo.
 *
 * @since July 9, 2013
 */
public class GoalGraphViewerShortExample
    extends JApplet
{
    private static final long serialVersionUID = 2202072534703043194L;
    private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);

    private JGraphXAdapter<String, DefaultEdge> jgxAdapter;

    /**
     * An alternative starting point for this demo, to also allow running this applet as an
     * application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        GoalGraphViewerShortExample applet = new GoalGraphViewerShortExample();
        applet.init();

        JFrame frame = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle("Goal Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init()
    {
        // create a JGraphT graph
        ListenableGraph<String, DefaultEdge> g =
            new DefaultListenableGraph<>(new DefaultDirectedGraph<>(DefaultEdge.class));

        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(g);

        getContentPane().add(new mxGraphComponent(jgxAdapter));
        resize(DEFAULT_SIZE);

        JSonLoaderExample jsload = new JSonLoaderExample();
        HashMap<String, Goal> goalList = new HashMap<String,Goal>();
        try {
        	goalList = jsload.readGoal("./data-misoo/rsu.json");
        	jsload.relValidateGoals(goalList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Iterator<String> iter = goalList.keySet().iterator();
        while(iter.hasNext()){
        	String goalID = iter.next();
        	Goal goal = goalList.get(goalID);
        	g.addVertex(goal.getNodeID());
        }
        
        iter = goalList.keySet().iterator();
        while(iter.hasNext()){
        	String goalID = iter.next();
        	Goal goal = goalList.get(goalID);
        	String parentGoal = goal.getParentNode();
        	Relation rel = goal.getRelation();
        	ArrayList<String> subGoals = rel.getSubGoals();
        	ArrayList<String> contGoals = rel.getContGoals();
        	
        	if(!parentGoal.equals(""))
        		g.addEdge(goalID, parentGoal);
        	for(int i = 0 ; i <subGoals.size(); i++){
//        		System.out.println(subGoals.get(i)+" "+goalID);
        		g.addEdge(subGoals.get(i), goalID);
        	}
        	for(int i = 0 ; i <contGoals.size(); i++){
        		g.addEdge(contGoals.get(i), goalID);
        	}
        }
        
        
//        g.addEdge(v1, v2);
//        g.addEdge(v2, v3);
//        g.addEdge(v3, v1);
//        g.addEdge(v4, v3);

        // positioning via jgraphx layouts
//        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
//        layout.execute(jgxAdapter.getDefaultParent());
        mxFastOrganicLayout layout = new mxFastOrganicLayout(jgxAdapter);
        layout.setResetEdges(false);
        
        
        layout.execute(jgxAdapter.getDefaultParent());        

        // that's all there is to it!...
    }
}

// End JGraphXAdapterDemo.java
