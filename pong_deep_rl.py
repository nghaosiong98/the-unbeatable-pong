import gym
from tensorflow.keras.layers import Dense
from tensorflow.keras.models import Sequential
import numpy as np

UP_ACTION = 2
DOWN_ACTION = 3

env = gym.make("Pong-v0")


def create_model():
    model = Sequential()
    model.add(Dense(units=200, input_dim=80 * 80, activation='relu', kernel_initializer='glorot_uniform'))
    model.add(Dense(units=1, activation='sigmoid', kernel_initializer='RandomNormal'))
    model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])
    return model


def preprocessing(I):
    """ prepro 210x160x3 uint8 frame into 6400 (80x80) 1D float vector """
    I = I[35:195]  # crop
    I = I[::2, ::2, 0]  # downsample by factor of 2
    I[I == 144] = 0  # erase background (background type 1)
    I[I == 109] = 0  # erase background (background type 2)
    # I[I != 0] = 1 # everything else (paddles, ball) just set to 1
    I = I / 255
    return I.astype(np.float).ravel()  # flattens


def discount_rewards(r, gamma):
    """ take 1D float array of rewards and compute discounted reward """
    r = np.array(r)
    discounted_r = np.zeros_like(r)
    running_add = 0
    # we go from last reward to first one so we don't have to do exponentiations
    for t in reversed(range(0, r.size)):
        if r[t] != 0: running_add = 0  # if the game ended (in Pong), reset the reward sum
        running_add = running_add * gamma + r[
            t]  # the point here is to use Horner's method to compute those rewards efficiently
        discounted_r[t] = running_add
    discounted_r -= np.mean(discounted_r)  # normalizing the result
    discounted_r /= np.std(discounted_r)  # idem
    return discounted_r


def main():
    model = create_model()

    observation = env.reset()
    prev_input = None
    gamma = 0.99
    x_train, y_train, rewards = [], [], []
    reward_sum = 0
    episode_nb = 0

    while (True):
        # preprocess the observation, set input as difference between images
        cur_input = preprocessing(observation)
        x = cur_input - prev_input if prev_input is not None else np.zeros(80 * 80)
        prev_input = cur_input

        # forward the policy network and sample action according to the proba distribution
        proba = model.predict(np.expand_dims(x, axis=1).T)
        action = UP_ACTION if np.random.uniform() < proba else DOWN_ACTION
        y = 1 if action == 2 else 0  # 0 and 1 are our labels

        # log the input and label to train later
        x_train.append(x)
        y_train.append(y)

        # do one step in our environment
        observation, reward, done, info = env.step(action)
        env.render()
        rewards.append(reward)
        reward_sum += reward

        # end of an episode
        if done:
            print('At the end of episode', episode_nb, 'the total reward was :', reward_sum)

            # increment episode number
            episode_nb += 1

            # training
            model.fit(x=np.vstack(x_train), y=np.vstack(y_train), verbose=1,
                      sample_weight=discount_rewards(rewards, gamma))

            # Reinitialization
            x_train, y_train, rewards = [], [], []
            observation = env.reset()
            reward_sum = 0
            prev_input = None


if __name__ == '__main__':
    main()
