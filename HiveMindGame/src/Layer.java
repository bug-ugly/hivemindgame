
/// Each individual layer in the ML

public class Layer {

	Float L;
	HiveMind parent;

	int numberOfInputs; // num of neurons in the previous layer
	int numberOfOutputs; // num of neurons in the current layer

	public float[] outputs; // outputs of this layer
	public float[] inputs; // inputs into this layer
	public float[][] weights; // weights of this layer
	public float[][] weightsDelta; // deltas of this layer
	public float[] gamma; // gamme of this layer
	public float[] error; // error of the output layer

	/// <param name="numberOfInputs">Number of neurons in the previous layer</param>
	/// <param name="numberOfOuputs">Number of neurons in the current layer</param>
	public Layer(int numberOfInputs, int numberOfOutputs, float _L, HiveMind p) {
		parent = p;
		this.numberOfInputs = numberOfInputs;
		this.numberOfOutputs = numberOfOutputs;

		L = _L;
		outputs = new float[numberOfOutputs];
		inputs = new float[numberOfInputs];
		weights = new float[numberOfOutputs][numberOfInputs];
		weightsDelta = new float[numberOfOutputs][numberOfInputs];
		gamma = new float[numberOfOutputs];
		error = new float[numberOfOutputs];

		InitializeWeights();
	}

	public Layer(int numberOfInputs, int numberOfOutputs, float[][] w, float _L, HiveMind p) {
		parent = p;
		this.numberOfInputs = numberOfInputs;
		this.numberOfOutputs = numberOfOutputs;

		L = _L;
		outputs = new float[numberOfOutputs];
		inputs = new float[numberOfInputs];
		weights = new float[numberOfOutputs][numberOfInputs];
		weightsDelta = new float[numberOfOutputs][numberOfInputs];
		gamma = new float[numberOfOutputs];
		error = new float[numberOfOutputs];

		CopyWeights(w);
	}

	// Initilize weights between -0.5 and 0.5
	public void InitializeWeights() {
		for (int i = 0; i < numberOfOutputs; i++) {
			for (int j = 0; j < numberOfInputs; j++) {
				weights[i][j] = (float) (parent.random(1) - 0.5);
			}
		}
	}

	public void CopyWeights(float[][] w) {
		for (int i = 0; i < numberOfOutputs; i++) {
			for (int j = 0; j < numberOfInputs; j++) {
				weights[i][j] = w[i][j];
			}
		}
	}

	public void CopyWeights(Layer l) {
		for (int i = 0; i < numberOfOutputs; i++) {
			for (int j = 0; j < numberOfInputs; j++) {
				weights[i][j] = l.weights[i][j];
			}
		}
	}

	public float[] FeedForward(float[] inputs) {
		this.inputs = inputs; // keep shallow copy which can be used for back propagation
		// feed forwards
		for (int i = 0; i < numberOfOutputs; i++) {
			outputs[i] = 0;
			for (int j = 0; j < numberOfInputs; j++) {
				if (inputs != null) {
					outputs[i] += inputs[j] * weights[i][j];
				}
			}
			outputs[i] = (float) Math.tanh(outputs[i]);
		}
		return outputs;
	}

	/// TanH derivate
	public float TanHDer(float value) {
		return 1 - (value * value);
	}

	/// Back propagation for the output layer
	public void BackPropOutput(float[] expected) {
		for (int i = 0; i < numberOfOutputs; i++) { // error deriv
			error[i] = outputs[i] - expected[i];
		}
		// gamma calc
		for (int i = 0; i < numberOfOutputs; i++) { // error deriv
			gamma[i] = error[i] * TanHDer(outputs[i]);

			// calc delta weights
		}
		for (int i = 0; i < numberOfOutputs; i++) {
			for (int j = 0; j < numberOfInputs; j++) {
				weightsDelta[i][j] = gamma[i] * inputs[j];
			}
		}
	}

	/// Back propagation for the hidden layers
	public void BackPropHidden(float[] gammaForward, float[][] weightsForward) {
		// calculate gamma using sums of the forward layer
		for (int i = 0; i < numberOfOutputs; i++) {
			gamma[i] = 0;
			for (int j = 0; j < gammaForward.length; j++) {
				gamma[i] += gammaForward[j] * weightsForward[j][i];
			}
			gamma[i] *= TanHDer(outputs[i]);
		}
		// Caluclating detla weights
		for (int i = 0; i < numberOfOutputs; i++) {
			for (int j = 0; j < numberOfInputs; j++) {
				weightsDelta[i][j] = gamma[i] * inputs[j];
			}
		}
	}

	/// Updating weights
	public void UpdateWeights() {
		for (int i = 0; i < numberOfOutputs; i++) {
			for (int j = 0; j < numberOfInputs; j++) {
				weights[i][j] -= weightsDelta[i][j] * L;
			}
		}
	}

	// various random mutations for the weights
	public void Mutate() {
		int randomiser = (int) parent.random(0, 10);
		if (randomiser == 1) {
			for (int i = 0; i < numberOfOutputs; i++) {
				for (int j = 0; j < numberOfInputs; j++) {
					float chance = parent.random(1);
					if (chance > 0.9)
						weights[i][j] = weights[i][j] + parent.random(1); // weight + random value
				}
			}
		}
		if (randomiser == 2) {
			for (int i = 0; i < numberOfOutputs; i++) {
				for (int j = 0; j < numberOfInputs; j++) {
					float chance = parent.random(1);
					if (chance > 0.9)
						weights[i][j] = weights[i][j] * -1; // weight sign inversion
				}
			}
		}

		if (randomiser == 3) {
			for (int i = 0; i < numberOfOutputs; i++) {
				for (int j = 0; j < numberOfInputs; j++) {
					float chance = parent.random(1);
					if (chance > 0.9)
						weights[i][j] = weights[i][j] - parent.random(1); // weight-random value
				}
			}
		}
		if (randomiser == 4) {
			for (int i = 0; i < numberOfOutputs; i++) {
				for (int j = 0; j < numberOfInputs; j++) {
					float chance = parent.random(1);
					if (chance > 0.9)
						weights[i][j] = weights[i][j] / 2; // weight divided in half
				}
			}
		}
		if (randomiser == 5) {
			for (int i = 0; i < numberOfOutputs; i++) {
				for (int j = 0; j < numberOfInputs; j++) {
					float chance = parent.random(1);
					if (chance > 0.9)
						weights[i][j] = (float) (weights[i][j] - 0.1); // weight -0.1
				}
			}
		}

		if (randomiser == 6) {
			for (int i = 0; i < numberOfOutputs; i++) {
				for (int j = 0; j < numberOfInputs; j++) {
					float chance = parent.random(1);
					if (chance > 0.9)
						weights[i][j] = (float) (weights[i][j] + 0.1); // weight + 0.1
				}
			}
		}
		if (randomiser == 7) {
			for (int i = 0; i < numberOfOutputs; i++) {
				for (int j = 0; j < numberOfInputs; j++) {
					float chance = parent.random(1);
					if (chance > 0.9)
						weights[i][j] = 0; // setting the weight to 0
				}
			}
		}
	}

}