package UI;

import java.text.DecimalFormat;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class Graph extends Pane
{
	private int[] posX =
	{ 50, 130, 170, 230, 280, 320, 350, 460, 500, 550 }; // 点的x坐标
	private int[] posY =
	{ 400, 250, 450, 40, 180, 270, 360, 110, 220, 310 }; // 点的y坐标
	private int[] LPosX1 =
	{ 50, 50, 130, 230, 280, 320, 170, 280, 320, 350, 460, 500 }; // 线的x1坐标
	private int[] LPosY1 =
	{ 400, 400, 250, 40, 180, 270, 450, 180, 270, 360, 110, 220 }; // 线的y1坐标
	private int[] LPosX2 =
	{ 130, 170, 230, 280, 320, 350, 350, 460, 500, 550, 500, 550 }; // 线的x2坐标
	private int[] LPosY2 =
	{ 250, 450, 40, 180, 270, 360, 360, 110, 220, 310, 220, 310 }; // 线的y2坐标
	private int[] distance =
	{ 22, 7, 30, 18, 11, 9, 9, 19, 20, 22, 11, 8 };
	private int[] correspondingToGraphData =
	{ 0, 1, 0, 2, 1, 3, 3, 4, 4, 5, 5, 6, 2, 6, 4, 7, 5, 8, 6, 9, 7, 8, 8, 9 };
	private Text[] text1 = new Text[10];
	private Text[] text2 = new Text[12];
	private Line[] line = new Line[12];
	private int[] carsNum = new int[12];
	private double[] density = new double[12];
	private double criticalDensity;
	private DecimalFormat df = new DecimalFormat("0.00");

	public Graph()
	{
		criticalDensity = 100;
		Circle[] circle = new Circle[10];
		text1 = new Text[10];
		text2 = new Text[12];
		for (int i = 0; i < 12; i++)
		{
			density[i] = (double) carsNum[i] / (double) distance[i];
			line[i] = new Line(LPosX1[i], LPosY1[i], LPosX2[i], LPosY2[i]);
			line[i].setStroke(getColor(carsNum[i], distance[i]));
			if (i == 1)
				text2[i] = new Text((LPosX1[i] + LPosX2[i]) / 2 - 18, (LPosY1[i] + LPosY2[i]) / 2 + 17,
						"No." + i + "\nl=" + distance[i] + "\nd=" + df.format(density[i]));
			else if (i == 4 || i == 5)
				text2[i] = new Text((LPosX1[i] + LPosX2[i]) / 2 + 12, (LPosY1[i] + LPosY2[i]) / 2 - 10,
						"No." + i + "\nl=" + distance[i] + "\nd=" + df.format(density[i]));
			else if (i == 10 || i == 11)
				text2[i] = new Text((LPosX1[i] + LPosX2[i]) / 2 + 12, (LPosY1[i] + LPosY2[i]) / 2 - 14,
						"No." + i + "\nl=" + distance[i] + "\nd=" + df.format(density[i]));
			else if (i == 3)
				text2[i] = new Text((LPosX1[i] + LPosX2[i]) / 2 + 12, (LPosY1[i] + LPosY2[i]) / 2 - 10,
						"No." + i + "\nl=" + distance[i] + "\nd=" + df.format(density[i]));
			else
				text2[i] = new Text((LPosX1[i] + LPosX2[i]) / 2 + 12, (LPosY1[i] + LPosY2[i]) / 2 + 7,
						"No." + i + "\nl=" + distance[i] + "\nd=" + df.format(density[i]));
			this.getChildren().addAll(line[i], text2[i]);
		}
		for (int i = 0; i < 10; i++)
		{
			circle[i] = new Circle(posX[i], posY[i], 10);
			circle[i].setFill(Color.WHITE);
			circle[i].setStroke(Color.BLACK);
			text1[i] = new Text(posX[i] - 4, posY[i] + 4, String.valueOf(i));
			this.getChildren().addAll(circle[i], text1[i]);
		}
	}

	// use to update the graph
	public void setCarsNum(int[] carNum)
	{
		for (int i = 0; i < carNum.length; i++)
		{
			carsNum[i] = carNum[i];
			line[i].setStroke(getColor(carsNum[i], distance[i]));
			density[i] = (double) carsNum[i] / (double) distance[i];
			text2[i].setText("No." + i + "\nl=" + distance[i] + "\nd=" + df.format(density[i]));
		}
	}

	public void setCarsNumWithUsedTime(double[][] roadUsedTimes)
	{
		for (int i = 0; i < carsNum.length; i++)
		{
			line[i].setStroke(getColor(
					carsNum[i]
							+ (int) (roadUsedTimes[correspondingToGraphData[2 * i]][correspondingToGraphData[2 * i + 1]]+
									roadUsedTimes[correspondingToGraphData[2 * i+1]][correspondingToGraphData[2 * i]]),
					distance[i]));
			density[i] = (double) (carsNum[i]
					+  (roadUsedTimes[correspondingToGraphData[2 * i]][correspondingToGraphData[2 * i + 1]]+
							roadUsedTimes[correspondingToGraphData[2 * i+1]][correspondingToGraphData[2 * i]]))
					/ (double) distance[i];
			text2[i].setText("No." + i + "\nl=" + distance[i] + "\nd=" + df.format(density[i]));
		}
	}

	public int[] getCarsNum()
	{
		return carsNum;
	}

	public Color getColor(int cars, int length)
	{
		double density = (double) cars / (double) length;
		double one = (255 + 255) / criticalDensity;
		int r = 0, g = 0, b = 0;
		if (density < (1.0 / 2.0) * criticalDensity)
		{
			r = (int) (one * density);
			g = 255;
		} else if (density >= (1.0 / 2.0) * criticalDensity && density < criticalDensity)
		{
			r = 255;
			g = 255 - (int) ((density - (1.0 / 2.0) * criticalDensity) * one);
		} else
		{
			r = 255;
		}
		return Color.rgb(r, g, b);
	}

	public int[] getPosX()
	{
		return posX.clone();
	}

	public int[] getPosY()
	{
		return posY.clone();
	}

	public int getRodeNum()
	{
		return distance.length;
	}
}
