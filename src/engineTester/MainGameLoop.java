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

		ModelTexture texture1 = new ModelTexture(loader.loadTexture("warp"));
		ModelData data = OBJFileLoader.loadOBJ("sten");
		RawModel model = loader.loadToVAO(data.getVertices(),
				data.getTextureCoords(), data.getNormals(), data.getIndices());

		TexturedModel staticModel = new TexturedModel(model, texture1);
		ModelTexture texture = staticModel.getTexture();
		texture.setReflectivity(0.1f);
		texture.setShineDamper(100);
		
		List<Light> lights = new ArrayList<Light>();

		Light light = new Light(new Vector3f(100, 200, -50),
				new Vector3f(0.4f, 0.4f, 0.4f));
		lights.add(light);

		light = new Light(new Vector3f(100, 10, -50),
				new Vector3f(2, 2, 2), new Vector3f(1, 0.01f, 0.002f));
		lights.add(light);
		

		
		TerrainTexture backgroundTexture = new TerrainTexture(
				loader.loadTexture("grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("cash"));
		TerrainTexture gTexture = new TerrainTexture(
				loader.loadTexture("metall"));
		TerrainTexture bTexture = new TerrainTexture(
				loader.loadTexture("nightfront"));
		TerrainTexture blendMap = new TerrainTexture(
				loader.loadTexture("blendMap"));

		TerrainTexturePack texturePack = new TerrainTexturePack(
				backgroundTexture, rTexture, gTexture, bTexture);

		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap,
				"heightmap");

		MasterRenderer renderer = new MasterRenderer(loader);

		Player player = new Player(staticModel, new Vector3f(100, 0, -50), 0,
				0, 0, 1);
		Camera camera = new Camera(player);

		texture1 = new ModelTexture(loader.loadTexture("grass"));
		data = OBJFileLoader.loadOBJ("trädd");
		model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(),
				data.getNormals(), data.getIndices());
		staticModel = new TexturedModel(model, texture1);

		Random rand = new Random();
		List<Entity> entities = new ArrayList<Entity>();
		for (int i = 0; i < 2; i++) {
			float x = rand.nextFloat() * 1000;
			float z = -rand.nextFloat() * 1000;
			float y = terrain.getHeightOfTerrain(x, z);
			Entity entity = new Entity(staticModel, new Vector3f(x, y, z), 0,
					0, 0, 3);
			entities.add(entity);
		}

		texture1 = new ModelTexture(loader.loadTexture("iron"));
		data = OBJFileLoader.loadOBJ("stone");
		model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(),
				data.getNormals(), data.getIndices());
		staticModel = new TexturedModel(model, texture1);

		for (int i = 0; i < 20; i++) {
			float x = rand.nextFloat() * 1000;
			float z = -rand.nextFloat() * 1000;
			float y = terrain.getHeightOfTerrain(x, z);
			Entity entity = new Entity(staticModel, new Vector3f(x, y, z), 0,
					0, 0, 2);
			entities.add(entity);
		}


		while (!Display.isCloseRequested()) {
			camera.move();
			player.move(terrain);

			renderer.processEntity(player);
			renderer.processTerrain(terrain);

			for (Entity entity : entities) {
				renderer.processEntity(entity);
			}
			renderer.render(lights, camera);
			DisplayManager.update();
		}

		renderer.cleanUpp();
		loader.cleanUp();
		DisplayManager.close();

	}
}
