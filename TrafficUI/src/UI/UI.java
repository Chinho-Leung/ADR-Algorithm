package UI;

import java.text.DecimalFormat;
import java.util.Vector;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import ADR_algorithm.VerNode;
import ADR_algorithm.ArcNode;
import ADR_algorithm.Result;
import ADR_algorithm.ADR;

public class UI extends Application
{
	private int animationCarsNum = 50;
	private double shortestTime = 100;
	private ADR_algorithm.Graph graphData;
	private DecimalFormat df = new DecimalFormat("0.00");
	private DecimalFormat df2 = new DecimalFormat("0.00000");
	private int[] correspondingToGraphData =
	{ 0, 1, 0, 2, 1, 3, 3, 4, 4, 5, 5, 6, 2, 6, 4, 7, 5, 8, 6, 9, 7, 8, 8, 9 };
	private ImageView[] carsImage;
	private double criticalDensity;

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		// initiate the graph data
		criticalDensity=100;
		int verNum = 10;
		int arcNum = 24;
		VerNode[] vertices = new VerNode[verNum];
		for (int i = 0; i < verNum; i++)
			vertices[i] = new VerNode(i);
		vertices[0].firstEdge = new ArcNode(1, 22, 0, criticalDensity, 70, 4000);
		vertices[0].firstEdge.next = new ArcNode(2, 7, 0, criticalDensity, 70, 4000);
		vertices[1].firstEdge = new ArcNode(3, 30, 0, criticalDensity, 70, 4000);
		vertices[1].firstEdge.next = new ArcNode(0, 22, 0, criticalDensity, 70, 4000);
		vertices[2].firstEdge = new ArcNode(6, 9, 0, criticalDensity, 70, 4000);
		vertices[2].firstEdge.next = new ArcNode(0, 7, 0, criticalDensity, 70, 4000);
		vertices[3].firstEdge = new ArcNode(1, 30, 0, criticalDensity, 70, 4000);
		vertices[3].firstEdge.next = new ArcNode(4, 18, 0, criticalDensity, 70, 4000);
		vertices[4].firstEdge = new ArcNode(3, 18, 0, criticalDensity, 70, 4000);
		vertices[4].firstEdge.next = new ArcNode(5, 11, 0, criticalDensity, 70, 4000);

		vertices[4].firstEdge.next.next = new ArcNode(7, 19, 0, criticalDensity, 70, 4000);
		vertices[5].firstEdge = new ArcNode(4, 11, 0, criticalDensity, 70, 4000);
		vertices[5].firstEdge.next = new ArcNode(6, 9, 0, criticalDensity, 70, 4000);
		vertices[5].firstEdge.next.next = new ArcNode(8, 20, 0, criticalDensity, 70, 4000);
		vertices[6].firstEdge = new ArcNode(2, 9, 0, criticalDensity, 70, 4000);
		vertices[6].firstEdge.next = new ArcNode(5, 9, 0, criticalDensity, 70, 4000);

		vertices[6].firstEdge.next.next = new ArcNode(9, 22, 0, criticalDensity, 70, 4000);
		vertices[7].firstEdge = new ArcNode(4, 19, 0, criticalDensity, 70, 4000);
		vertices[7].firstEdge.next = new ArcNode(8, 11, 0, criticalDensity, 70, 4000);
		vertices[8].firstEdge = new ArcNode(7, 11, 0, criticalDensity, 70, 4000);
		vertices[8].firstEdge.next = new ArcNode(5, 20, 0, criticalDensity, 70, 4000);
		vertices[8].firstEdge.next.next = new ArcNode(9, 8, 0, criticalDensity, 70, 4000);
		vertices[9].firstEdge = new ArcNode(8, 8, 0, criticalDensity, 70, 4000);
		vertices[9].firstEdge.next = new ArcNode(6, 22, 0, criticalDensity, 70, 4000);
		graphData = new ADR_algorithm.Graph(verNum, arcNum, vertices);

		Graph graph = new Graph();
		Text txt0 = new Text("  初始\n车辆数  ");
		Button set = new Button("set");
		Text txt1 = new Text("起点：     ");
		Text txt2 = new Text("终点：     ");
		Text txt3 = new Text("车辆数：  ");
		Text txt4 = new Text("路径：");
		Text txt5 = new Text("                      将规划的车流数据:");
		TextArea textArea0 = new TextArea();
		textArea0.setText("0,0,0,0,0,0,0,0,0,0,0,0");
		textArea0.setMaxWidth(160);
		textArea0.setMaxHeight(20);
		TextField textFiled1 = new TextField();
		TextField textFiled2 = new TextField();
		TextField textFiled3 = new TextField();
		TextArea textArea = new TextArea();
		textArea.setMaxWidth(200);
		HBox h0 = new HBox(txt0, textArea0, set);
		h0.setAlignment(Pos.CENTER);
		h0.setSpacing(11);
		HBox h1 = new HBox(txt1, textFiled1);
		HBox h2 = new HBox(txt2, textFiled2);
		HBox h3 = new HBox(txt3, textFiled3);
		HBox h4 = new HBox(txt5);
		Button btn = new Button("生成调度策略");

		int[][] a = new int[2][2];
		a[0][0] = 0;
		a[0][1] = 1;
		a[1][0] = 0;
		a[1][1] = 2;
		double[] b = new double[2];
		b[0] = 0.5;
		b[1] = 0.5;
		// To set the graph
		set.setOnAction(e ->
		{
			for (int i = 0; carsImage!=null&&i < carsImage.length; i++)
			{
				if (graph.getChildren().contains(carsImage[i]))
					graph.getChildren().remove(carsImage[i]);
			}
			graph.setCarsNum(graph.getCarsNum());
			int counter = 0;
			int[] carsNum = new int[graph.getRodeNum()];
			String text = textArea0.getText();
			for (int i = 0; i < text.length(); i++)
				if (text.charAt(i) == ',')
					counter++;
			if (counter != 11 || text.charAt(text.length() - 1) == ',')
				textArea0.setText("输入错误。请输入十二个整数。");
			else
			{
				String[] nums = text.split(",");
				for (int i = 0; i < graph.getRodeNum(); i++)
				{
					carsNum[i] = Integer.parseInt(nums[i]);
				}
				graph.setCarsNum(carsNum);
				graphData.updateCars(carsNum, correspondingToGraphData);
			}
		});
		// run the algorithm and show the result.
		btn.setOnAction(e ->
		{
			graph.setCarsNum(graph.getCarsNum());
			for (int i = 0; carsImage!=null&&i < carsImage.length; i++)
			{
				if (graph.getChildren().contains(carsImage[i]))
					graph.getChildren().remove(carsImage[i]);
			}
			ADR solver = new ADR(Integer.parseInt(textFiled1.getText()), Integer.parseInt(textFiled2.getText()),
					graphData, 500, Integer.parseInt(textFiled3.getText()), 10);
			Result result = solver.solve();
			String output = "车辆路径：\n";
			for (int i = 0; i < result.routes.length; i++)
			{
				if(df.format(result.percentage[i]).equals(df.format(0.00)))
					continue;
				for (int j = 0; j < result.routes[i].size(); j++)
					output += result.routes[i].elementAt(j) + " ";
				output += "使用比例:" + df.format(result.percentage[i]) + "\n";
			}
			output += "系统总开销因子:" + df2.format(result.systemTimeCost);
			textArea.setText(output);
			graph.setCarsNumWithUsedTime(result.roadUsedTimes);
			carsImage = createAnimation(graph, result.Vf, result.speed, result.routes, result.percentage);
		});
		VBox vBox = new VBox(h0, h4, h1, h2, h3, txt4, textArea, btn);
		vBox.setSpacing(8);
		vBox.setAlignment(Pos.CENTER);
		HBox hBox = new HBox(graph, vBox);
		Scene scene = new Scene(hBox, 850, 500);
		primaryStage.setTitle("车辆调度系统");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public ImageView[] createAnimation(Graph graph, double Vf, Vector<Double>[] speed, Vector<Integer>[] routes,
			double[] percentage)
	{
		Image image = new Image("UI/car1.png");
		ImageView[] car = new ImageView[animationCarsNum];
		int[] carsOnEachRoute = new int[routes.length];
		for (int i = 0; i < routes.length; i++)
			carsOnEachRoute[i] = (int) (percentage[i] * animationCarsNum);
		int carsSum = 0;
		for (int i : carsOnEachRoute)
			carsSum += i;
		int[] x = graph.getPosX();
		int[] y = graph.getPosY();
		for (int i = 0; i < animationCarsNum; i++)
		{
			car[i] = new ImageView(image);
			car[i].setFitHeight(10);
			car[i].setPreserveRatio(true);
			car[i].setX(x[routes[0].elementAt(0)]);
			car[i].setY(y[routes[0].elementAt(0)]);
		}
		for (int i = 0; i < carsSum; i++)
			graph.getChildren().add(car[i]);
		PathTransition[][] pt = new PathTransition[carsSum][];
		int countCarUsed = 0;
		double longestSleep = 0;
		for (int i = 0; i < routes.length; i++)
		{
			//通过delay来实现不同车先后，同车轨迹先后
			double delayTime = 0;
			for (int j = 0; j < carsOnEachRoute[i]; j++)
			{
				pt[countCarUsed] = new PathTransition[routes[i].size() - 1];
				delayTime = 250 * j;
				for (int k = 0; k < routes[i].size() - 1; k++)
				{
					ArcNode p = graphData.getVertices()[routes[i].elementAt(k)].firstEdge;
					while (p.adjvex != routes[i].elementAt(k + 1))
						p = p.next;
					double distance = p.distance;
					pt[countCarUsed][k] = new PathTransition(
							Duration.millis(distance * shortestTime * Vf / speed[i].elementAt(k)),
							new Line(x[routes[i].elementAt(k)], y[routes[i].elementAt(k)],
									x[routes[i].elementAt(k + 1)], y[routes[i].elementAt(k + 1)]),
							car[countCarUsed]);
					pt[countCarUsed][k].setDelay(Duration.millis(delayTime));
					delayTime += distance * shortestTime * Vf / speed[i].elementAt(k);
					longestSleep = delayTime > longestSleep ? delayTime : longestSleep;
				}
				countCarUsed++;
			}
		}
		for (int i = 0; i < pt.length; i++)
			for (int j = 0; j < pt[i].length; j++)
				pt[i][j].play();
		return car;
	}

	class runAnimation implements Runnable
	{
		private PathTransition[] pts;

		public runAnimation(PathTransition[] pts)
		{
			this.pts = pts;
		}

		@Override
		public void run()
		{
			for (int i = 0; i < pts.length; i++)
			{
				pts[i].play();
			}
		}

	}

	public static void main(String[] args)
	{
		Application.launch(args);
	}
}
