package com.vosiievska.dbscanclustering.ui;

import java.awt.event.MouseEvent;
import java.util.Random;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.util.MouseManager;

public class MouseClicksManager implements MouseManager {

    protected View view;
    protected GraphicGraph graph;
    protected GraphicElement element;

    @Override
    public void init(GraphicGraph graphicGraph, View view) {
        this.graph = graphicGraph;
        this.view = view;
        view.addMouseListener(this);
        view.addMouseMotionListener(this);
    }

    @Override
    public void release() {
        view.removeMouseListener(this);
        view.removeMouseMotionListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        element = view.findNodeOrSpriteAt(event.getX(), event.getY());
        if (element != null) {
            Random r = new Random();
            element.setAttribute("ui.style",
                "fill-color: rgb(" + r.nextInt(256) + "," + r.nextInt(256) + "," + r.nextInt(256) + ");");
        }
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
}
