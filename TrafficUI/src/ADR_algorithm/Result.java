package ADR_algorithm;

import java.util.Vector;

public class Result
{
	public double Vf;
	public Vector<Double>[] speed;
	public Vector<Integer>[] routes;
	public double[] percentage;
	public double systemTimeCost;
	public double[][] roadUsedTimes;
	public Result(double Vf,Vector<Double>[] speed,Vector<Integer>[] routes,double[] percentage,double systemTimeCost,double[][] roadUsedTimes)
	{
		this.Vf=Vf;
		this.speed=speed;
		this.routes=routes;
		this.percentage=percentage;
		this.systemTimeCost=systemTimeCost;
		this.roadUsedTimes=roadUsedTimes;
	}
}
