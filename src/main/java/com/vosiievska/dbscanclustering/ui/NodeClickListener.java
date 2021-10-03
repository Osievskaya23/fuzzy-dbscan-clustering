package com.vosiievska.dbscanclustering.ui;

import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;
import org.graphstream.graph.Graph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

public class NodeClickListener implements ViewerListener, MouseInputListener {

    public boolean loop = true;
    private ViewerPipe vpipe = null;
    private View vw = null;
    private Graph graph = null;

    public NodeClickListener(ViewerPipe vpipe, View vw, Graph g) {
        this.loop = true;
        this.vpipe = vpipe;
        this.vw = vw;
        this.graph = g;
        // Keep piping back while grph is out to hook mouse clicks
        this.vw.addMouseListener(this);

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void viewClosed(String s) {
        vw.removeMouseListener(this);
    }

    @Override
    public void buttonPushed(String s) {
        System.out.println("Button pushed on node " + s);
    }

    @Override
    public void buttonReleased(String s) {
    }
}
