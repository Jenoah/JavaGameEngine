# JavaGameEngine
A simple Java Game engine using LWJGL 3 and some of my own implementations

## How to run
- Run the 'Demo/DemoLauncher.java'
- If on Mac, use the file mentioned above with the '-XstartOnFirstThread' VM parameter

## Includes
- 3D rendering
- Simple lighting
- Custom shaders
- Fog
- Simple UI
- Text rendering(Credits to [Thin Matrix's font rendering tutorial](https://www.youtube.com/watch?v=mnIQEQoHHCU))
- Post processing
- Marching cubes, multi-threaded terrain generation

## Examples

There is one simple game demo scene that contains all the implementations above
![preview image of render](git-files/images/preview2.png)

A thing I have been working on myself in this engine would be a simple procedural landscape generation.
Although it might be in a very early stage, it is working and keeping a consistent framerate without stutter on the chunk generation.

<img alt="preview of procedural generation in the Java game engine" height="266" src="/git-files/images/preview_proc_gen_lighting_shaders.gif" width="480"/>
