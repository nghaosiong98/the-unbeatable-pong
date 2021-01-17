# Mini Project: The Unbeatable Pong

## Requirement
- gym
- atari-py
- numpy

## Usage
1. Train model
```
python pong.py --save="weight directory"
```
2. To resume training

```
python pong.py --load="path to pkl file"
```
3. To test the model
```
python pong.py --load="path to pkl file" --render
```