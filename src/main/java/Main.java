import mind.Kahului;
import model.data.MindData;
import model.data.PredictionData;

public class Main {
	public static void main(String[] args) {
		Kahului dolores = new Kahului();
		//Kahului.playSweetWater();
		MindData theCradle = dolores.gatherData();
		PredictionData predictionData = new PredictionData(theCradle);
		predictionData.calculatePredictionData();
	}
}
