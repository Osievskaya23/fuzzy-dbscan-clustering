package com.vosiievska.dbscanclustering.ui;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

public class MouseClicks implements ViewerListener {

    protected boolean loop = true;

    public MouseClicks(Graph graph) {
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

//        View view = viewer.addDefaultView(false);
//        viewer.getDefaultView().setMouseManager(new MouseClicksManager());

        viewer.getDefaultView().enableInputMethods(true);
//        viewer.addDefaultView(false).enableInputMethods(true);
        ViewerPipe fromViewer = viewer.newViewerPipe();

//        viewer.getDefaultView().setMouseManager(new MouseOverMouseManager(
//            EnumSet.of(InteractiveElement.EDGE, InteractiveElement.NODE)));

        fromViewer.addViewerListener(this);
        fromViewer.addSink(graph);

//        while (loop) {
//            fromViewer.pump();
//            System.out.println("loop");
//        }
    }

    @Override
    public void viewClosed(String s) {

    }

    @Override
    public void buttonPushed(String s) {
        System.out.println("Button pushed on node " + s);

    }

    @Override
    public void buttonReleased(String s) {
        System.out.println("Button released on node " + s);
    }
}
