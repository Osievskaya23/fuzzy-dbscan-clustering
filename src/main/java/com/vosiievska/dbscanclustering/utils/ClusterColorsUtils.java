package com.vosiievska.dbscanclustering.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClusterColorsUtils {

    private List<Color> colors = new ArrayList<>(Arrays.asList(
        new Color(0, 0, 153),
        new Color(51, 244, 255),
        new Color(255, 0, 0),
        new Color(0, 125, 125),
        new Color(255, 102, 102),
        new Color(255, 255, 0),
        new Color(102, 0, 153),
        new Color(0, 51, 0),
        new Color(0, 204, 0),
        new Color(152, 255, 153),
        new Color(255, 72, 54),
        new Color(51, 0, 0),
        new Color(153, 120, 0),
        new Color(255, 102, 0),
        new Color(128, 128, 128),
        new Color(204, 255, 255),
        new Color(255, 255, 153),
        new Color(255, 0, 127),
        new Color(51, 0, 102),
        new Color(0, 0, 153),
        new Color(51, 244, 255),
        new Color(255, 0, 0),
        new Color(0, 125, 125),
        new Color(255, 102, 102),
        new Color(255, 255, 0),
        new Color(102, 0, 153),
        new Color(0, 51, 0),
        new Color(0, 204, 0),
        new Color(152, 255, 153),
        new Color(255, 72, 54),
        new Color(51, 0, 0),
        new Color(153, 120, 0),
        new Color(255, 102, 0),
        new Color(128, 128, 128),
        new Color(204, 255, 255),
        new Color(255, 255, 153),
        new Color(255, 0, 127),
        new Color(0, 0, 153),
        new Color(51, 244, 255),
        new Color(255, 0, 0),
        new Color(0, 125, 125),
        new Color(255, 102, 102),
        new Color(255, 255, 0),
        new Color(102, 0, 153),
        new Color(0, 51, 0),
        new Color(0, 204, 0),
        new Color(152, 255, 153),
        new Color(255, 72, 54),
        new Color(51, 0, 0),
        new Color(153, 120, 0),
        new Color(255, 102, 0),
        new Color(128, 128, 128),
        new Color(204, 255, 255),
        new Color(255, 255, 153),
        new Color(255, 0, 127),
        new Color(51, 0, 102),
        new Color(0, 0, 153),
        new Color(51, 244, 255),
        new Color(255, 0, 0),
        new Color(0, 125, 125),
        new Color(255, 102, 102),
        new Color(255, 255, 0),
        new Color(102, 0, 153),
        new Color(0, 51, 0),
        new Color(0, 204, 0),
        new Color(152, 255, 153),
        new Color(255, 72, 54),
        new Color(51, 0, 0),
        new Color(153, 120, 0),
        new Color(255, 102, 0),
        new Color(128, 128, 128),
        new Color(204, 255, 255),
        new Color(255, 255, 153),
        new Color(255, 0, 127),
        new Color(51, 0, 102)));

    public Color getNextColor() {
        if (colors.isEmpty()) {
            System.out.println("ERROR: Colors left.");
        }
        return colors.remove(0);
    }
}
