package ADR_algorithm;

public class Graph
{
	private int verNum;
	private int arcNum;
	private VerNode[] vertices;
	public Graph(int verNum,int arcNum,VerNode[] vertices)
	{
		this.verNum=verNum;
		this.arcNum=arcNum;
		this.vertices=vertices;
	}
	public void updateCars(int[] carsNum,int[] correspondingToGraphData)
	{
		for(int i=0;i<carsNum.length;i++)
		{
			ArcNode p=vertices[correspondingToGraphData[2*i]].firstEdge;
			while(p.adjvex!=correspondingToGraphData[2*i+1])
				p=p.next;
			p.cars=carsNum[i];
		}
		for(int i=0;i<carsNum.length;i++)
		{
			ArcNode p=vertices[correspondingToGraphData[2*i+1]].firstEdge;
			while(p.adjvex!=correspondingToGraphData[2*i])
				p=p.next;
			p.cars=carsNum[i];
		}
	}
	public int getverNum()
	{
		return verNum;
	}
	public int getarcNum()
	{
		return arcNum;
	}
	public VerNode[] getVertices()
	{
		return vertices;
	}
	public void setverNum(int verNum)
	{
		this.verNum=verNum;
	}
	public void setarcNum(int arcNum)
	{
		this.arcNum=arcNum;
	}
	public void setVertices(VerNode[] vertices)
	{
		this.vertices=vertices;
	}
}


