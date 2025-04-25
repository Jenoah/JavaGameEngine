# JavaGameEngine
A simple Java Game engine using LWJGL3 / OpenGL.

## Included features
- 3D rendering
- Game Object Components
- Simple lighting
- Custom shaders
- Fog
- Simple UI
- Text rendering(Credits to [Thin Matrix's font rendering tutorial](https://www.youtube.com/watch?v=mnIQEQoHHCU))
- Post-processing
- Marching cubes, multi-threaded terrain generation with surface features (foliage etc...)

## Planned features
- PBR textures
- Shadows
- Collisions
- Ambient Occlusion
- Reflections
- Expanding UI to buttons
- Audio
- Frustum (and Occlusion) culling
- An actual name for the engine

## How to run
- Run the 'Demo/DemoLauncher.java'
- If on Mac, use the file mentioned above with the '-XstartOnFirstThread' VM parameter

## Examples
All the above-mentioned included features are shown in the demo scene.
This demo scene includes a procedurally generated world. This is done with chunks of [Marching Cubes](https://en.wikipedia.org/wiki/Marching_cubes) that runs on a separate thread to prevent stuttering on generation.

![preview image of render](git-files/images/preview3.png)
![preview of procedural generation in the Java game engine](git-files/images/world_gen_preview.gif)
