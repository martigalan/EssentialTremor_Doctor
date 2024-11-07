package data;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ACC {
    /**
     * Signal values of acceleration data
     */
    private List<Integer> signalData;
    /**
     * Time values of acceleration data
     */
    private List<Integer> timestamp;

    /**
     * Constructor
     * @param signalData acceleration data
     * @param timestamp time data
     */
    public ACC(List<Integer> signalData, List<Integer> timestamp) {
        this.signalData = signalData;
        this.timestamp = timestamp;
    }

    /**
     * Empty constructor
     */
    public ACC() {
        this.signalData = new ArrayList<>();
        this.timestamp = new ArrayList<>();
    }

    public List<Integer> getSignalData() {
        return signalData;
    }
    public void setSignalData(List<Integer> signalData) {
        this.signalData = signalData;
    }

    public List<Integer> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(List<Integer> timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * String representation of acceleration
     * @return String representing acceleration
     */
    @Override
    public String toString() {
        return "ACC [timestamp=" + timestamp + ", signalData=" + signalData + "]";
    }

    /**
     * Graphical representation of acceleration.
     * x axis: timestamp ; y axis: signalData
     */
    public void plotSignal() {
        XYSeries series = new XYSeries("Acceleration Signal");

        for (int i = 0; i < signalData.size(); i++) {
            series.add(timestamp.get(i), signalData.get(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Acceleration Signal over Time",
                "Time (ms)",
                "Acceleration",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame frame = new JFrame("Signal Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
