
//an individual replay for DeepQNetwork
//used to store a replay
public class Replay {

  float[] Input;
  int Action; 
  float Reward;
  float[] NextInput;
  int [] NextActionMask ;

  Replay(float[] input, int action, float reward, float[] nextInput, int [] nextActionMask) {
    Input = input;
    Action = action;
    Reward = reward;
    NextInput = nextInput;
    NextActionMask = nextActionMask ;
  }
}