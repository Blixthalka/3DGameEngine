package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		Loader loader = new Loader();

		ModelTexture texture1 = new ModelTexture(loader.loadTexture("pennan"));
		ModelData data = OBJFileLoader.loadOBJ("sten");
		RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		
		TexturedModel staticModel = new TexturedModel(model, texture1);
		ModelTexture texture = staticModel.getTexture();
		texture.setReflectivity(0.1f);
		texture.setShineDamper(100);

		Random rand = new Random();
		List<Entity> entities = new ArrayList<Entity>();
		for (int i = 0; i < 0; i++) {
			Entity entity = new Entity(staticModel, new Vector3f(
					rand.nextFloat() * 2000 - 1000, 0, -rand.nextFloat()* 1000), 0, 0, 0, 1);
			entities.add(entity);
		}

		Light light = new Light(new Vector3f(100,20,10), new Vector3f(1, 1, 1));

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("cash"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("metall"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("tilez"));
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		
		
		
		
		Terrain terrain = new Terrain(0, -1, loader,texturePack,blendMap);
		Terrain terrain2 = new Terrain(0, -2, loader,texturePack,blendMap);

		
		MasterRenderer renderer = new MasterRenderer();
		
		Player player = new Player(staticModel, new Vector3f(100,0,-50), 0, 0, 0, 1);
		Camera camera = new Camera(player);
		while (!Display.isCloseRequested()) {
			// entity.increaseRotation(0.1f, 0.1f, 0);
			camera.move();
			player.move();
			
			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);

			for (Entity entity : entities) {
				renderer.processEntity(entity);
			}
			renderer.render(light, camera);
			DisplayManager.update();
		}

		renderer.cleanUpp();
		loader.cleanUp();
		DisplayManager.close();

	}

}
