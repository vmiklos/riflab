package monitor;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.Rotation;
import java.awt.event.ActionListener;

//http://sites.google.com/site/drjohnbmatthews/jfreechartdemo
//http://www.java2s.com/Code/Java/Chart/JFreeChartTimeSeriesDemo10withperminutedata.htm
//http://www.jfree.org/jfreechart/
//http://www.vogella.de/articles/JFreeChart/article.html


public class MyChart extends JFrame {
	
	private TimeSeries series = null;
	private int min = 0;
	
	//private static final long serialVersionUID = 1L;
	
	//private static final Random random=new Random();
	
	final TimeSeriesCollection dataset;

	public MyChart(String applicationTitle, String chartTitle) {
		super(applicationTitle);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// This will create the dataset 
		 dataset = createDataset();

		// based on the dataset we create the chart
		JFreeChart chart = createChart(dataset, chartTitle);
		// we put the chart into a panel
		ChartPanel chartPanel = new ChartPanel(chart);
		// default size
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		
		// addButton();
		
		// add it to our application
		//setContentPane(chartPanel);
		this.add(chartPanel, BorderLayout.CENTER);

		pack();
		show();
	}

	public void addNewValue(double value) {
		Hour hour = new Hour();
		series.add(new Minute(min++, hour), value);
	}
	
	public void updateChart(double load){
    	dataset.getSeries(0).add(new Second(), load);
	}

	/**
	 * Creates a sample dataset 
	 */
	private  TimeSeriesCollection createDataset() {

		series = new TimeSeries("Per Minute Data", Minute.class);
		/*addNewValue(13.2);
		addNewValue(17.2);
		addNewValue(3.2);
		addNewValue(11.0);*/
		final TimeSeriesCollection dataset = new TimeSeriesCollection(series); 

		return dataset;
	}

	/**
	 * Creates a chart
	 */
	private JFreeChart createChart(TimeSeriesCollection dataset, String title) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"Time Series Demo 10",
				"Time", 
				"Value",
				dataset,
				true,
				true,
				false
		);


		// set chart background
		chart.setBackgroundPaint(Color.white);

		// set a few custom plot features
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(new Color(0xffffe0));
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);

		/*
	    // set the plot's axes to display integers
	    TickUnitSource ticks = NumberAxis.createIntegerTickUnits();
	    NumberAxis domain = (NumberAxis) plot.getDomainAxis();
	    domain.setStandardTickUnits(ticks);
	    NumberAxis range = (NumberAxis) plot.getRangeAxis();
	    range.setStandardTickUnits(ticks);
		//*/

		// render shapes and lines
		XYLineAndShapeRenderer renderer =
			new XYLineAndShapeRenderer(true, true);
		plot.setRenderer(renderer);
		renderer.setBaseShapesVisible(true);
		renderer.setBaseShapesFilled(true);

		// set the renderer's stroke
		Stroke stroke = new BasicStroke(
				3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
		renderer.setBaseOutlineStroke(stroke);

		// label the points
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(2);
		XYItemLabelGenerator generator =
			new StandardXYItemLabelGenerator(
					StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT,
					format, format);
		renderer.setBaseItemLabelGenerator(generator);
		renderer.setBaseItemLabelsVisible(true);

		return chart;

	}
	
	public static void main(String[] args) {
		new MyChart("chart demo", "riflab7 demo");
	}
}
