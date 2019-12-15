package ADR_algorithm;

public class ArcNode
{
	public int adjvex;//the vertex pointed to by the edge
	public double distance;//the length of the road
	public double cars;//number of cars
	public double pheromone;//pheromone
	public double criticalDensity;
	public double freeSpeed;
	public double capacity;
	public ArcNode next;//next edge connected to the same vertex
	public ArcNode(int adjvex,double distance,double cars,double criticalDensity,double freeSpeed,double capacity)
	{
		next=null;
		this.adjvex=adjvex;
		this.distance=distance;
		this.cars=cars;
		this.pheromone=0;
		this.criticalDensity=criticalDensity;
		this.freeSpeed=freeSpeed;
		this.capacity=capacity;
	}
	public void setCar(double car)
	{
		this.cars=car;
	}
}
