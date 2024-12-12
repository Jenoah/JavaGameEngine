# JavaGameEngine
A simple Java game.myGame engine using LWJGL 3 and some of my own implementations

## How to run
- Run the 'Demo/DemoLauncher.java'
- If on Mac, use the file mentioned above with the '-XstartOnFirstThread' VM parameter

## Includes
- 3D rendering
- Simple lighting
- Custom shaders
- Simple UI
- Text rendering(Credits to [Thin Matrix's font rendering tutorial](https://www.youtube.com/watch?v=mnIQEQoHHCU))
- Post processing
- Marching cubes, multi-threaded terrain generation

## Examples
### **_Pictures below are outdated_**

There is one simple game demo scene that contains all the implementations above
![preview image of render](git-files/images/preview2.png)

A thing I have been working on myself in this engine would be a simple procedural landscape generation.
Although it might be in a very early stage, it is working and keeping a consistent framerate without stutter on the chunk generation.
<div style="text-align: center;">
    <img height='266' width='480' alt='preview of procedural generation in the Java game engine' src="https://github.com/Jenoah/JavaGameEngine/tree/main/git-files/images/preview_proc_gen_lighting_shaders.gif"/>
</div>
