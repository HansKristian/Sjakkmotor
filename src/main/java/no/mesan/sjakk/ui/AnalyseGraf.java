package no.mesan.sjakk.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import no.mesan.sjakk.ui.chessboard.BrettLytter;
import no.mesan.sjakk.ui.chessboard.BrikkeUI;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class AnalyseGraf implements ChangeListener, BrettLytter {
	private XYDataset dataset;
	private TimeSeries serie;
	
	private BoundedRangeModel analyseModell;
	private JFreeChart chart;

	public AnalyseGraf(BoundedRangeModel analyseModell) {
		this.analyseModell = analyseModell;
		
		analyseModell.addChangeListener(this);
		initComponents();
	}
	
	

	private void initComponents(){
		serie = new TimeSeries("");
		dataset = new TimeSeriesCollection(serie);
		chart = ChartFactory.createTimeSeriesChart("", "", "", dataset);
		
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(new GradientPaint(0, 0, Color.black, 0, 200, Color.white));
		
		BasicStroke stroke = new  BasicStroke(3);
		XYItemRenderer xyir = plot.getRenderer();
		xyir.setSeriesPaint(0, Color.blue);
		xyir.setSeriesStroke(0, stroke);
         
		chart.getLegend().setVisible(false);
		chart.setBackgroundPaint(new JPanel().getBackground());
		
		
		int max = analyseModell.getMaximum()+200;
		int min = analyseModell.getMinimum()-200;
		
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setRange(min, max);
	}
	
	   
	   
	public JFreeChart getGraf(){
		return chart;
	}

	private long lastUpdate = System.currentTimeMillis();
	private double forrigeVerdi;
	
	private void leggTilVerdi(double value) {
		long time = System.currentTimeMillis();
		
		if (time-lastUpdate>1000 && value==forrigeVerdi){
			serie.addOrUpdate(new Second(), value);
			forrigeVerdi = value;
			lastUpdate = time;
		}
		if (value!=forrigeVerdi){
			serie.addOrUpdate(new Second(), value);
			forrigeVerdi = value;
			lastUpdate = time;
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		
		DefaultBoundedRangeModel model = (DefaultBoundedRangeModel) e.getSource();
		
		int value = model.getValue();
		leggTilVerdi(value);
	}



	@Override
	public void modelUpdated() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void pieceKilled(BrikkeUI brikke) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void resetBrett() {
		serie.clear();
//		serie.removeAgedItems(0, true);
	}
}
