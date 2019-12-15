package ADR_algorithm;
import java.util.Random;
import java.util.Vector;

public class Ants implements Cloneable
{
	private Vector<Integer> searched; // cities already searched
	private double[][] delta; // Pheromoune deposit
	private Vector<Double> distance;// distance list
	private Graph map;

	private int routeLength;
	private int originCity;
	private int currentCity;
	private int terminalCity;

	public Ants(Graph map)
	{
		this.map = map;
		routeLength = 0;
		distance = new Vector<Double>();
	}

	public Vector<Integer> getSearched()
	{
		return (Vector<Integer>) searched.clone();
	}

	public void initialAnt(int originCIty, int terminalCity)
	{
		this.originCity = originCIty;
		this.terminalCity = terminalCity;
		this.currentCity = originCIty;
		// Initiate the searched list
		searched = new Vector<Integer>();
		delta = new double[map.getverNum()][map.getverNum()];
		for (int i = 0; i < map.getverNum(); i++)
			for (int j = 0; j < map.getverNum(); j++)
				delta[i][j] = 0.0;
		searched.add(originCIty);
	}

	public int selectNextCity()// 0:encounter dead end,1:selected next city,2:reached end
	{
		double[] probability = new double[map.getverNum()];// transfer probability
		double sum = 0;

		// calculate the denominator
		ArcNode p = map.getVertices()[currentCity].firstEdge;
		Vector<Integer> canReach = new Vector<Integer>();
		while (p != null)
		{
			if (!inSearch(p.adjvex))
			{
				sum += p.pheromone;
				canReach.add(p.adjvex);
			}
			p = p.next;
		}
		// can't go to next step
		if (canReach.isEmpty())
			return 0;
		// calculate the probability
		p = map.getVertices()[currentCity].firstEdge;
		while (p != null)
		{
			if (canReach.contains(p.adjvex))
			{
				probability[p.adjvex] = p.pheromone / sum;
			}
			p = p.next;
		}

		// choose another city
		int selectedCity = -1;
		Random random = new Random();
		double rand = random.nextDouble();
		double proSum = 0;
		for (int i = 0; i < map.getverNum(); i++)
		{
			proSum += probability[i];
			if (proSum >= rand)
			{
				selectedCity = i;
				break;
			}
		}
		p = map.getVertices()[currentCity].firstEdge;
		while (p.adjvex != selectedCity)
		{
			p = p.next;
		}
		// p.cars++;//just use to prune the net,so don't need to add current
		distance.add(p.distance);
		searched.add(selectedCity);
		currentCity = selectedCity;
		if (currentCity == terminalCity)
			return 2;
		return 1;
	}

	public int getRouteLength()
	{
		int length = 0;
		for (double i : distance)
			length += i;
		return length;
	}

	public boolean inSearch(int toTest)
	{
		for (int k : searched)
			if (toTest == k)
				return true;
		return false;
	}

}
