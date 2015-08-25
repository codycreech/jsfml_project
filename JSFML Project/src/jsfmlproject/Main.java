package jsfmlproject;


import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;


import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.VideoMode;
import org.jsfml.window.event.Event;


public class Main {
	public static void main(String args[]){
		
		int PosX = 640;
		int PosY = 360;
		ArrayList<Sprite> sprites = new ArrayList<Sprite>();
		int speed = 50;
		
		//Create a Texture instance
		Texture circle1 = new Texture();
		Texture circle2 = new Texture();
		Texture circle3 = new Texture();
		Texture circle4 = new Texture();
		Texture square = new Texture();

		try {
		    //Try to load the texture from files
		    circle1.loadFromFile(Paths.get("circle1.png"));
		    circle2.loadFromFile(Paths.get("circle2.png"));
		    circle3.loadFromFile(Paths.get("circle3.png"));
		    circle4.loadFromFile(Paths.get("circle4.png"));
		    square.loadFromFile(Paths.get("square.png"));
		    circle1.setSmooth(true);
		    circle2.setSmooth(true);
		    circle3.setSmooth(true);
		    circle4.setSmooth(true);
		    square.setSmooth(true);

		    
		} catch(IOException ex) {
		    //Ouch! something went wrong
		    ex.printStackTrace();
		}
		//Create a sprite and make it use the texture
		Sprite circle1_spr = new Sprite(circle1);
		sprites.add(circle1_spr);
		Sprite circle2_spr = new Sprite(circle2);
		sprites.add(circle2_spr);
		Sprite circle3_spr = new Sprite(circle3);
		sprites.add(circle3_spr);
		Sprite circle4_spr = new Sprite(circle4);
		sprites.add(circle4_spr);
		Sprite square_spr = new Sprite(square);

		//Set its origin to its center and put it at the center of the screen
		circle1_spr.setOrigin(Vector2f.div(new Vector2f(circle1.getSize()), 2));
		circle1_spr.setPosition(120,240);
		circle2_spr.setOrigin(Vector2f.div(new Vector2f(circle2.getSize()), 2));
		circle2_spr.setPosition(240,120);
		circle3_spr.setOrigin(Vector2f.div(new Vector2f(circle3.getSize()), 2));
		circle3_spr.setPosition(240,360);
		circle4_spr.setOrigin(Vector2f.div(new Vector2f(circle4.getSize()), 2));
		circle4_spr.setPosition(360,240);
		square_spr.setOrigin(Vector2f.div(new Vector2f(square.getSize()), 2));
		square_spr.setPosition(PosX,PosY);

		//Create a frame clock for rotation
		Clock frameClock = new Clock();
		//Create the window
		RenderWindow window = new RenderWindow();
		window.create(new VideoMode(900, 500), "Hello JSFML!");

		//Limit the framerate
		window.setFramerateLimit(30);

		//Main loop
		while(window.isOpen()) {
		    window.clear();
		    window.draw(circle1_spr);
		    window.draw(circle2_spr);
		    window.draw(circle3_spr);
		    window.draw(circle4_spr);
		    window.draw(square_spr);
		    window.display();
		    
		    Time deltaTime = frameClock.restart();
		    float deltaSeconds = deltaTime.asSeconds();
		    

		  //Handle events
		    for(Event event : window.pollEvents()) {
		    	
		        if(event.type == Event.Type.CLOSED) {
		            //The user pressed the close button
		            window.close();
		        }
		    }
		        if(Keyboard.isKeyPressed(Key.LEFT)){
		        	if(!isColliding(sprites,square_spr)){
		        		square_spr.move(-(deltaSeconds * speed),0);
		        	}
		        	
		        }else if(Keyboard.isKeyPressed(Key.RIGHT)){
		        	
		        	if(!isColliding(sprites,square_spr)){
		        		square_spr.move(deltaSeconds * speed,0);
		        	}
		        	
		        }else if(Keyboard.isKeyPressed(Key.UP)){
		        	
		        	if(!isColliding(sprites,square_spr)){
		        		square_spr.move(0,-(deltaSeconds * speed));
		        	}
		        	
		        }else if(Keyboard.isKeyPressed(Key.DOWN)){
		        	
		        	if(!isColliding(sprites,square_spr)){
		        		square_spr.move(0,deltaSeconds * speed);
		        	}
		        	
		        }
		    }
	}
	public static boolean isColliding(ArrayList<Sprite> sprites, Sprite spriteA){
		
		boolean result = false;
		
		for(Sprite sprite : sprites){
			if(spriteA.getGlobalBounds().intersection(sprite.getGlobalBounds())!= null){
				result = true;
				break;
			}
		}
		return result;
	}
}
