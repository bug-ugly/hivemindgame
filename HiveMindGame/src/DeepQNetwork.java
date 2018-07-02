
import java.util.ArrayList;

public class DeepQNetwork {
	HiveMind parent;
	int ReplayMemoryCapacity;
	ArrayList<Replay> ReplayMemory;
	double Epsilon;
	float Discount;

	NeuralNetwork DeepQ;
	NeuralNetwork TargetDeepQ;

	int BatchSize;
	int UpdateFreq;
	int UpdateCounter;
	int ReplayStartSize;

	int InputLength;
	int NumActions;

	float[] LastInput;
	int LastAction;

	DeepQNetwork(int[] layers, int replayMemoryCapacity, float discount, double epsilon, int batchSize, int updateFreq,
			int replayStartSize, int inputLength, int numActions, float L, HiveMind p) {
		parent = p;
		DeepQ = new NeuralNetwork(layers, L, parent); // initializing two networks because in deep Q learning there are
														// 2 feed forw steps
		TargetDeepQ = new NeuralNetwork(layers, L, parent);
		TargetDeepQ.setParams(DeepQ); // copy the weights from the original network
		ReplayMemoryCapacity = replayMemoryCapacity;
		Epsilon = epsilon;
		Discount = discount;

		BatchSize = batchSize;
		UpdateFreq = updateFreq;
		UpdateCounter = 0;
		ReplayMemory = new ArrayList<Replay>();
		ReplayStartSize = replayStartSize;
		InputLength = inputLength;
		NumActions = numActions;
	}

	DeepQNetwork(int[] layers, NeuralNetwork _net, int replayMemoryCapacity, float discount, double epsilon,
			int batchSize, int updateFreq, int replayStartSize, int inputLength, int numActions, float L, HiveMind p) {
		parent = p;
		DeepQ = new NeuralNetwork(layers, _net, L, parent); // initializing two networks because in deep Q learning
															// there are 2 feed forw steps
		TargetDeepQ = new NeuralNetwork(layers, _net, L, parent);
		TargetDeepQ.setParams(DeepQ); // copy the weights from the original network
		ReplayMemoryCapacity = replayMemoryCapacity;
		Epsilon = epsilon;
		Discount = discount;

		BatchSize = batchSize;
		UpdateFreq = updateFreq;
		UpdateCounter = 0;
		ReplayMemory = new ArrayList<Replay>();
		ReplayStartSize = replayStartSize;
		InputLength = inputLength;
		NumActions = numActions;
	}

	void SetEpsilon(double e) {
		Epsilon = e;
	}

	@SuppressWarnings("unlikely-arg-type")
	void AddReplay(float reward, float[] NextInput, int NextActionMask[]) {
		if (ReplayMemory.size() >= ReplayMemoryCapacity)
			ReplayMemory.remove(parent.random(ReplayMemory.size())); // if there are too many replays, the oldest replay is removed
		ReplayMemory.add(new Replay(LastInput, LastAction, reward, NextInput, NextActionMask));
	}

	Replay[] GetMiniBatch(int BatchSize) {
		int size = ReplayMemory.size() < BatchSize ? ReplayMemory.size() : BatchSize;
		Replay[] retVal = new Replay[size];

		for (int i = 0; i < size; i++) {
			retVal[i] = ReplayMemory.get((int) parent.random(ReplayMemory.size()));
		}
		return retVal;
	}

	float FindMax(float[] NetOutputs, int ActionMask[]) {
		float maxVal = NetOutputs[0];
		for (int i = 0; i < NetOutputs.length; i++) {
			if (NetOutputs[i] > maxVal) {
				maxVal = NetOutputs[i];
			}
		}
		return maxVal;
	}

	// finding the action that got max points
	int FindActionMax(float[] NetOutputs, int ActionMask[]) {

		float maxVal = NetOutputs[0];
		int maxValI = 0;
		for (int i = 0; i < NetOutputs.length; i++) {
			if (NetOutputs[i] > maxVal) {
				maxVal = NetOutputs[i];
				maxValI = i;
			}
		}
		return maxValI;
	}

	// select an action for the agent to perform
	int GetAction(float[] Inputs, int ActionMask[]) {
		LastInput = Inputs;
		float[] outputs = DeepQ.FeedForward(Inputs);
		// println(outputs[0] + " " +outputs[1]+ " " +outputs[2]+ " " +outputs[3]+ " "
		// +outputs[4]+ " : ");

		double r = parent.random((float) 1.1);

		// println ("Epsilon :" +Epsilon + " r : " + r);
		// chance of selecting a random action - epsilon
		if (Epsilon < r) {
			LastAction = (int) parent.random(outputs.length);
			while (ActionMask[LastAction] == 0)
				LastAction = (int) parent.random(outputs.length);
			// System.out.println(LastAction);
			return LastAction;
		}

		// selecting the action that scored the maximum in the prediction
		LastAction = FindActionMax(outputs, ActionMask);
		// System.out.println(LastAction);
		return LastAction;
	}

	// introduce the reward, add a replay and train the network
	void ObserveReward(float Reward, float[] NextInputs, int NextActionMask[]) {
		AddReplay(Reward, NextInputs, NextActionMask);
		// println(ReplayStartSize + " " + ReplayMemory.size());
		// if (ReplayStartSize < ReplayMemory.size())
		TrainNetwork(BatchSize);
		UpdateCounter++;
		if (UpdateCounter == UpdateFreq) {
			UpdateCounter = 0;
			// System.out.println("Reconciling Networks");
			ReconcileNetworks();
		}
	}

	void correctAction(int act, float[] NextInputs, int NextActionMask[]) {
		LastAction = act;
		ObserveReward(100, NextInputs, NextActionMask);
	}

	float[][] CombineInputs(Replay replays[]) {
		float[][] retVal = new float[replays.length][InputLength];
		for (int i = 0; i < replays.length; i++) {
			if (replays[i].Input != null) {
				for (int j = 0; j < replays[i].Input.length; j++) {
					retVal[i][j] = replays[i].Input[j];
				}
			}
		}
		return retVal;
	}

	float[][] CombineNextInputs(Replay replays[]) {
		float[][] retVal = new float[replays.length][InputLength];
		for (int i = 0; i < replays.length; i++) {
			if (replays[i].NextInput != null) {
				for (int j = 0; j < InputLength; j++) {

					retVal[i][j] = replays[i].NextInput[j];
				}
			}
		}

		return retVal;
	}

	void TrainNetwork(int BatchSize) {
		Replay replays[] = GetMiniBatch(BatchSize);
		float[][] CurrInputs = CombineInputs(replays);
		float[][] TargetInputs = CombineNextInputs(replays);

		float TotalError = 0;

		float[][] CurrOutputs = new float[CurrInputs.length][NumActions];
		float[][] TargetOutputs = new float[TargetInputs.length][NumActions];

		for (int row = 0; row < CurrOutputs.length; row++) {

			float[] row1 = DeepQ.FeedForward(parent.getaRow(CurrInputs, row));
			float[] row2 = TargetDeepQ.FeedForward(parent.getaRow(TargetInputs, row));

			for (int i = 0; i < row1.length; i++) {
				CurrOutputs[row][i] = row1[i];
			}
			for (int i = 0; i < row2.length; i++) {
				TargetOutputs[row][i] = row2[i];
			}
			// println ("Total Error: " + TotalError);
		}

		float y[] = new float[replays.length];
		for (int i = 0; i < y.length; i++) {
			int ind[] = { i, replays[i].Action };
			float FutureReward = 0;
			if (replays[i].NextInput != null)

				FutureReward = FindMax(parent.getaRow(TargetOutputs, i), replays[i].NextActionMask);
			float TargetReward = replays[i].Reward + Discount * FutureReward;
			TotalError += (TargetReward - CurrOutputs[ind[0]][ind[1]]) * (TargetReward - CurrOutputs[ind[0]][ind[1]]);
			CurrOutputs[ind[0]][ind[1]] = TargetReward;
		}
		HiveMind.println("Avgerage Error: " + (TotalError / y.length));

		DeepQ.Train(CurrInputs, CurrOutputs);
	}

	void ReconcileNetworks() {
		// setParams copies the weights from a network
		TargetDeepQ.setParams(DeepQ);
	}

}