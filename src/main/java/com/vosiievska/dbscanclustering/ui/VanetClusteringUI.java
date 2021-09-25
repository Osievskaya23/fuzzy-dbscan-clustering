package com.vosiievska.dbscanclustering.ui;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.Viewer.ThreadingModel;

public class VanetClusteringUI {

    private static final int MAIN_WINDOW_HIGH = 750;
    private static final int MAIN_WINDOW_WIDTH = 1000;
    private static final int GRAPH_PANEL_WIDTH = 700;

    private DefaultGraph graph;
    private JTextArea logsTextArea;

    public DefaultGraph getGraph() {
        return graph;
    }

    public JTextArea getLogsTextArea() {
        return logsTextArea;
    }

    public VanetClusteringUI() {
        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HIGH));
        frame.setLayout(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0, 0, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HIGH);

        // get graph area
        graph = new DefaultGraph("My graph");
        graph.setStrict(false);
        Viewer viewer = new Viewer(graph, ThreadingModel.GRAPH_IN_GUI_THREAD);
        DefaultView view = (DefaultView) viewer.addDefaultView(false);   // false indicates "no JFrame".
        view.setPreferredSize(new Dimension(GRAPH_PANEL_WIDTH - 40, MAIN_WINDOW_HIGH - 40));
        viewer.enableAutoLayout();

//        JSlider slider = new JSlider();
//        slider.addChangeListener(e -> view.getCamera().setViewPercent(slider.getValue() / 10.0));

        JPanel graphPanel = new JPanel();
        graphPanel.setLayout(new FlowLayout());
        graphPanel.setBounds(10, 10, GRAPH_PANEL_WIDTH - 20, MAIN_WINDOW_HIGH - 20);
        graphPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
        graphPanel.add(view);
//        graphPanel.add(slider);

        // get logs panel
        logsTextArea = new JTextArea();
        JScrollPane scrollableTextArea = new JScrollPane(logsTextArea);
        scrollableTextArea.setBounds(5, 5, MAIN_WINDOW_WIDTH - GRAPH_PANEL_WIDTH - 25, MAIN_WINDOW_HIGH - 30);
        scrollableTextArea.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollableTextArea.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);

        JPanel logsPanel = new JPanel();
        logsPanel.setLayout(null);
        logsPanel
            .setBounds(GRAPH_PANEL_WIDTH + 5, 10, MAIN_WINDOW_WIDTH - GRAPH_PANEL_WIDTH - 15, MAIN_WINDOW_HIGH - 20);
        logsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
        logsPanel.add(scrollableTextArea);

        panel.add(graphPanel);
        panel.add(logsPanel);
        panel.setVisible(true);

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
