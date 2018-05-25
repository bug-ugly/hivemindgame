
//a neural network component of DeepQ net 
class NeuralNetwork {
	HiveMind parent;
	int[] layer;
	Layer[] layers;
	int fitness;

	/// Constructor setting up layers
	NeuralNetwork(int[] layer, float L, HiveMind p) {
		parent = p;
		// deep copy layers

		this.layer = new int[layer.length];

		for (int i = 0; i < layer.length; i++) {
			this.layer[i] = layer[i];
		}

		// creates neural layers
		layers = new Layer[layer.length - 1];

		for (int i = 0; i < layers.length; i++) {
			layers[i] = new Layer(layer[i], layer[i + 1], L, parent);
		}
	}

	NeuralNetwork(int[] layer, NeuralNetwork n, float L, HiveMind p) {
		parent = p;
		// deep copy layers
		this.layer = new int[layer.length];
		for (int i = 0; i < layer.length; i++) {
			this.layer[i] = layer[i];
		}
		// copies neural layers
		layers = new Layer[layer.length - 1];
		for (int i = 0; i < layers.length; i++) {
			layers[i] = new Layer(layer[i], layer[i + 1], n.layers[i].weights, L, parent);
		}
	}

	/// High level feedforward for this network
	public float[] FeedForward(float[] _inputs) {

		// feed forward
		layers[0].FeedForward(_inputs); // feed forw inputs
		for (int i = 1; i < layers.length; i++) { // feed forw neuron outputs

			layers[i].FeedForward(layers[i - 1].outputs);
		}
		float[] retVal = layers[layers.length - 1].outputs;
		return retVal; // return out of last layer
	}

	/// High level feedforward for this network
	public float[] FeedForward(float[][] _inputs) {
		float[] inp = new float[_inputs.length * _inputs.length];
		int inpCounter = 0;
		for (int i = 0; i < _inputs.length; i++) {
			for (int j = 0; j < _inputs[i].length; j++) {
				inp[inpCounter] = _inputs[i][j];
				inpCounter++;
			}
		}
		// feed forward
		layers[0].FeedForward(inp); // feed forw inputs
		for (int i = 1; i < layers.length; i++) { // feed forw neuron outputs

			layers[i].FeedForward(layers[i - 1].outputs);
		}
		float[] retVal = layers[layers.length - 1].outputs;
		return retVal; // return out of last layer
	}

	// copy the weights from an identical neural net
	public void setParams(NeuralNetwork n) {
		for (int i = 0; i < layers.length; i++) {
			layers[i].CopyWeights(n.layers[i]);
		}
	}

	/// High level back porpagation
	/// Note: It is expexted the one feed forward was done before this back prop.

	public void BackProp(float[] expected) {
		// run over all layers backwards
		for (int i = layers.length - 1; i >= 0; i--) {
			if (i == layers.length - 1) {
				layers[i].BackPropOutput(expected); // back prop output
			} else {
				layers[i].BackPropHidden(layers[i + 1].gamma, layers[i + 1].weights);
			}
		}

		// Update weights
		for (int i = 0; i < layers.length; i++) {

			layers[i].UpdateWeights();
		}
	}

	public void Train(float[][] trainingInputs, float[][] trainingOutputs) {
		// run over all layers backwards
		for (int i = 0; i < trainingInputs.length; i++) {
			FeedForward(parent.getaRow(trainingInputs, i));
			BackProp(parent.getaRow(trainingOutputs, i));
		}
	}

	public void Mutate() {
		for (int i = layers.length - 2; i >= 0; i--) {
			layers[i].Mutate();
		}
	}
}