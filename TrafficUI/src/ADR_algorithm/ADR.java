package ADR_algorithm;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Ant Dispersion Route Algorithm
 */
public class ADR
{
	private Ants[] ants;
	private Graph map;

	private int antNum;
	private int carNum;// cars per iteration
	private int generationTimes;

	private int originCity;
	private int terminalCity;

	private ArrayList<Vector<Integer>> bestRoutes;// best Routes
	private Vector<Double> goodTimes;// the cost time of best routes
	private double bestTime;
	private double[][] updated;//record the roads on the best routes

	private double W;// the weighting factor of constant network cost
	private double evaporation;// evaporation rate
	private double steepness;// the steepness of the bell function of time cost
	private double carWeight;
	private double distanceWeight;
	private double accpetThreshold;

	public ADR(int origincity, int terminalcity, Graph map, int antNum, int carNum, int generation)
	{
		this.originCity = origincity;
		this.terminalCity = terminalcity;
		this.map = map;
		this.antNum = antNum;
		this.carNum = carNum;
		this.generationTimes = generation;
		this.W = -0.8;
		this.accpetThreshold = 0.193;
		this.carWeight = 1;
		this.distanceWeight = 1;
		this.updated=new double[map.getverNum()][map.getverNum()];
		steepness = 0.8;
		this.evaporation = 0.6;
		double start = (1.0 / (map.getverNum() - 1) * antNum);
		for (int i = 0; i < map.getverNum(); i++)
		{
			ArcNode p = map.getVertices()[i].firstEdge;
			while (p != null)
			{
				p.pheromone = start;
				p = p.next;
			}
		}
		bestRoutes = new ArrayList<Vector<Integer>>();
		bestTime = Integer.MAX_VALUE;
		this.goodTimes = new Vector<Double>();
		this.goodTimes.add(Double.MAX_VALUE);
		this.bestRoutes = new ArrayList<Vector<Integer>>();
		this.bestRoutes.add(new Vector<Integer>());
		ants = new Ants[antNum];
		for (int i = 0; i < antNum; i++)
		{
			ants[i] = new Ants(map);
			ants[i].initialAnt(originCity, terminalCity);
		}
	}

	public Result solve()
	{
		// Network Pruning
		for (int i = 0; i < generationTimes; i++)
		{
			for (int ant = 0; ant < antNum; ant++)
			{
				int temp = 4;
				while (temp != 2)
				{
					temp = ants[ant].selectNextCity();
					if (temp == 0)// dead end
						break;
				}
				if (temp == 0)
					continue;
				double newTime = totalTimeCost(ants[ant].getSearched());
				Vector<Integer> kkfdf = ants[ant].getSearched();
				if (newTime < bestTime)
				{
					for (int j = 0; j < goodTimes.size(); j++)
					{
						if (goodTimes.elementAt(j) - newTime >= bestTime*accpetThreshold)// 是否为10待定
						{
							goodTimes.remove(j);
							bestRoutes.remove(j);
						}
					}
					goodTimes.add(newTime);
					bestRoutes.add(ants[ant].getSearched());
					bestTime = newTime;
				} else if (newTime - bestTime <= bestTime*accpetThreshold)
				{
					if (!bestRoutes.contains(ants[ant].getSearched()))
					{
						goodTimes.add(newTime);
						bestRoutes.add(ants[ant].getSearched());
					}
				}
			}
			updatePheromone();
			// refresh the ants
			ants = new Ants[antNum];
			for (int v = 0; v < antNum; v++)
			{
				ants[v] = new Ants(map);
				ants[v].initialAnt(originCity, terminalCity);
			}
		}
		pruneTheNetwork();
		// Flow Optimization
		Object[] array = bestRoutes.toArray();
		Vector<Integer>[] routes = new Vector[array.length];
		for (int i = 0; i < routes.length; i++)
			routes[i] = (Vector<Integer>) array[i];
		int[] finalUsedTime = new int[goodTimes.size()];
		int[] usedTimes = new int[goodTimes.size()];// the use times of the routes
		for (int i = 0; i < generationTimes; i++)
		{
			for (int ant = 0; ant < antNum; ant++)
			{
				int temp = 4;
				while (temp != 2)
				{
					temp = ants[ant].selectNextCity();
					if (temp == 0)// dead end
						break;
				}
				if (temp == 0)
					continue;
				Vector<Integer> antRoute = ants[ant].getSearched();
				// recode the number of ants on a specific route
				for (int k = 0; k < routes.length; k++)
				{
					if (antRoute.equals(routes[k]))
					{
						usedTimes[k]++;
						break;
					}
				}
			}
			updatePheromone(usedTimes);
			// refresh ants
			for (int v = 0; v < antNum; v++)
			{
				ants[v] = new Ants(map);
				ants[v].initialAnt(originCity, terminalCity);
			}
			// 取最后三次做结果
			if (i >= generationTimes - 3)
				for (int p = 0; p < finalUsedTime.length; p++)
					finalUsedTime[p] += usedTimes[p];
			for (int p = 0; p < usedTimes.length; p++)
				usedTimes[p] = 0;
		}
		// calculate final average cost
		int[] finalUsedTimeDividedThree = new int[finalUsedTime.length];
		for (int k = 0; k < finalUsedTime.length; k++)
			finalUsedTimeDividedThree[k] = finalUsedTime[k] / 3;
		double[] routeLength = calculateRouteLength(routes);
		double[][] roadUsedTimes=new double[map.getverNum()][map.getverNum()];
		for(int i=0;i<routes.length;i++)
		{
			for(int j=0;j<routes[i].size()-1;j++)
			{
				ArcNode p = map.getVertices()[routes[i].elementAt(j)].firstEdge;
				while (p.adjvex != routes[i].elementAt(j+1))
					p = p.next;
				double percentage = p.distance / routeLength[i];
				roadUsedTimes[routes[i].elementAt(j)][routes[i].elementAt(j+1)]+=
						percentage * finalUsedTimeDividedThree[i] * ((double) carNum / (double) antNum);
			}
		}
		double[] timeCosts = totalTimeCost(routes, roadUsedTimes, routeLength);
		double averageCost = averageTimeCosts(finalUsedTimeDividedThree, timeCosts);
		// calculate speed Vector array
		Vector<Double>[] speed = new Vector[routes.length];
		for (int i = 0; i < finalUsedTimeDividedThree.length; i++)
		{
			speed[i] = new Vector<Double>();
			for (int j = 0; j < routes[i].size() - 1; j++)
			{
				int a = routes[i].elementAt(j);
				int b = routes[i].elementAt(j + 1);
				speed[i].add(Vr(a, b, roadUsedTimes));
			}
		}
		// calculate percentage
		int sum = 0;
		for (int i = 0; i < finalUsedTime.length; i++)
			sum += finalUsedTime[i];
		double[] percentage = new double[finalUsedTime.length];
		for (int i = 0; i < finalUsedTime.length; i++)
			percentage[i] = (double) finalUsedTime[i] / (double) sum;
		// return result
		return new Result(70.0, speed, routes, percentage, averageCost,roadUsedTimes);

	}

//functions for network pruning
	public double Vr(int a, int b)
	{
		ArcNode p = map.getVertices()[a].firstEdge;
		while (p.adjvex != b)
			p = p.next;
		double density = (carWeight * p.cars) / (distanceWeight * p.distance);
		double Am = -1 / Math.log(p.capacity / (p.freeSpeed * p.criticalDensity));
		return p.freeSpeed * Math.exp(-(1 / Am) * Math.pow((density / p.criticalDensity), Am));
	}

	public double timeCost(int a, int b)
	{
		ArcNode p = map.getVertices()[a].firstEdge;
		while (p.adjvex != b)
			p = p.next;
		double density = (carWeight * p.cars) / (distanceWeight * p.distance);
		if (density <= p.criticalDensity)
			return p.distance / Vr(a, b) + (1 / (Math.sqrt(2 * Math.PI) * steepness))
					* Math.exp(-(Math.pow((density - p.criticalDensity), 2) / (2 * steepness * steepness)));
		else
			return p.distance / Vr(a, b) + (1 / (Math.sqrt(2 * Math.PI) * steepness));
	}

	public double totalTimeCost(Vector<Integer> searched)
	{
		double sum = 0;
		for (int i = 0; i < searched.size() - 1; i++)
			sum += timeCost(searched.elementAt(i), searched.elementAt(i + 1));
		return sum;
	}

	public void updatePheromone()
	{
		double[][] depositMatrix = new double[map.getverNum()][map.getverNum()];
		Object[] array = bestRoutes.toArray();
		Vector<Integer>[] routes = new Vector[array.length];
		for (int i = 0; i < routes.length; i++)
			routes[i] = (Vector<Integer>) array[i];
		for (int i = 0; i < map.getverNum(); i++)// evaporate
		{
			ArcNode p = map.getVertices()[i].firstEdge;
			while (p != null)
			{
				p.pheromone = p.pheromone * (1 - evaporation);
				p = p.next;
			}
		}
		// construct deposit matrix
		for (int i = 0; i < goodTimes.size(); i++)
		{
			for (int j = 0; j < routes[i].size() - 1; j++)
			{
				depositMatrix[routes[i].elementAt(j)][routes[i].elementAt(j + 1)] += 1 / goodTimes.elementAt(i);
			}
		}
		for (int i = 0; i < map.getverNum(); i++)
			for (int j = 0; j < map.getverNum(); j++)
			{
				ArcNode p = map.getVertices()[i].firstEdge;
				while (p != null && p.adjvex != j)
					p = p.next;
				if (p == null)
					continue;
				p.pheromone += evaporation * depositMatrix[i][j];
				updated[i][j]+=depositMatrix[i][j];
			}
	}
	void pruneTheNetwork()
	{
		for (int i = 0; i < map.getverNum(); i++)
			for (int j = 0; j < map.getverNum(); j++)
			{
				ArcNode p = map.getVertices()[i].firstEdge;
				while (p != null && p.adjvex != j)
					p = p.next;
				if (p == null)
					continue;
				if (updated[i][j] == 0)// prune the net,make it invisible
					p.pheromone = 0;
			}
	}

	// functions for flow optimization
	public double Vr(int a, int b,double[][] roadUsedTimes)
	{
		ArcNode p = map.getVertices()[a].firstEdge;
		while (p.adjvex != b)
			p = p.next;
		double density = (carWeight * (p.cars + roadUsedTimes[a][b]))
				/ (distanceWeight * p.distance);
		double Am = -1 / Math.log(p.capacity / (p.freeSpeed * p.criticalDensity));
		return p.freeSpeed * Math.exp(-(1 / Am) * Math.pow((density / p.criticalDensity), Am));
	}

	public double timeCost(int a, int b, double[][] roadUsedTimes, double routeLength)
	{
		ArcNode p = map.getVertices()[a].firstEdge;
		while (p.adjvex != b)
			p = p.next;
		double density = (carWeight * (p.cars + roadUsedTimes[a][b]))
				/ (distanceWeight * p.distance);
		if (density <= p.criticalDensity)
			return p.distance / Vr(a, b, roadUsedTimes) + (1 / (Math.sqrt(2 * Math.PI) * steepness))
					* Math.exp(-(Math.pow((density - p.criticalDensity), 2) / (2 * steepness * steepness)));
		else
			return p.distance / Vr(a, b, roadUsedTimes) + (1 / (Math.sqrt(2 * Math.PI) * steepness));
	}

	public double[] totalTimeCost(Vector<Integer>[] routes,double[][] roadUsedTimes, double[] routeLength)
	{
		double[] timeCost = new double[routes.length];
		for (int i = 0; i < routes.length; i++)
		{
			double sum = 0;
			for (int j = 0; j < routes[i].size() - 1; j++)
				sum += timeCost(routes[i].elementAt(j), routes[i].elementAt(j + 1), roadUsedTimes, routeLength[i]);
			timeCost[i] = sum;
		}
		return timeCost;
	}

	public double averageTimeCosts(int[] usedTimes, double[] timeCosts)
	{
		double sum = 0;
		for (int i = 0; i < usedTimes.length; i++)
			sum += timeCosts[i] * usedTimes[i] * (double) carNum / (double) antNum;
		return sum / (double) carNum;
	}

	public void updatePheromone(int[] usedTimes)
	{
		Object[] array = bestRoutes.toArray();
		Vector<Integer>[] routes = new Vector[array.length];
		for (int i = 0; i < routes.length; i++)
			routes[i] = (Vector<Integer>) array[i];
		double[] routeLength = calculateRouteLength(routes);
		//计算每段路有多少车
		double[][] roadUsedTimes=new double[map.getverNum()][map.getverNum()];
		for(int i=0;i<routes.length;i++)
		{
			for(int j=0;j<routes[i].size()-1;j++)
			{
				ArcNode p = map.getVertices()[routes[i].elementAt(j)].firstEdge;
				while (p.adjvex != routes[i].elementAt(j+1))
					p = p.next;
				double percentage = p.distance / routeLength[i];
				roadUsedTimes[routes[i].elementAt(j)][routes[i].elementAt(j+1)]+=
						percentage * usedTimes[i] * ((double) carNum / (double) antNum);
			}
		}
		
		double[] timeCosts = totalTimeCost(routes, roadUsedTimes, routeLength);
		double averageCost = averageTimeCosts(usedTimes, timeCosts);
		double[][] depositMatrix = new double[map.getverNum()][map.getverNum()];

		for (int i = 0; i < map.getverNum(); i++)// evaporate
		{
			ArcNode p = map.getVertices()[i].firstEdge;
			while (p != null)
			{
				p.pheromone = p.pheromone * (1 - evaporation);
				p = p.next;
			}
		}
		// construct deposit matrix
		for (int i = 0; i < timeCosts.length; i++)
		{
			for (int j = 0; j < routes[i].size() - 1; j++)
			{
				depositMatrix[routes[i].elementAt(j)][routes[i].elementAt(j + 1)] += 1 / timeCosts[i] + W / averageCost;
			}
		}
		for (int i = 0; i < map.getverNum(); i++)
			for (int j = 0; j < map.getverNum(); j++)
			{
				ArcNode p = map.getVertices()[i].firstEdge;
				while (p != null && p.adjvex != j)
					p = p.next;
				if (p == null)
					continue;
				p.pheromone += evaporation * depositMatrix[i][j];
			}
	}

	double[] calculateRouteLength(Vector<Integer>[] routes)
	{
		double[] sum = new double[routes.length];
		for (int i = 0; i < routes.length; i++)
		{
			for (int j = 0; j < routes[i].size() - 1; j++)
			{
				ArcNode p = map.getVertices()[routes[i].elementAt(j)].firstEdge;
				while (p.adjvex != routes[i].elementAt(j + 1))
					p = p.next;
				sum[i] += p.distance;
			}
		}
		return sum;
	}
}
