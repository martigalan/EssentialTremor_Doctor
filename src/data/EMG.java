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

public class EMG {
    /**
     * Signal values of acceleration data
     */
    private List<Integer> signalData;
    /**
     * Time values of acceleration data
     */
    private List<Integer> timestamp;

    /**
     * Empty constructor
     */
    public EMG() {
        this.signalData = new ArrayList<>();
        this.timestamp = new ArrayList<>();
    }

    /**
     * Constructor
     * @param emg emg data
     * @param time time data
     */
    public EMG(List<Integer> emg, List<Integer> time) {
        this.signalData = emg;
        this.timestamp = time;
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
     * String representation of emg
     * @return String representing emg
     */
    @Override
    public String toString() {
        return "EMG [timestamp=" + timestamp + "]";
    }

    /**
     * Graphical representation of emg.
     * x axis: timestamp ; y axis: signalData
     */
    public void plotSignal() {
        XYSeries series = new XYSeries("EMG Signal");

        for (int i = 0; i < signalData.size(); i++) {
            series.add(timestamp.get(i), signalData.get(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "EMG Signal over Time",
                "Time (ms)",
                "EMG",
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