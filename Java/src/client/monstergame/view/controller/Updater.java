package client.monstergame.view.controller;

import java.util.ArrayList;

import client.monstergame.logic.Game;
import client.monstergame.logic.graph.Point;
import client.monstergame.main.Driver;
import client.monstergame.view.input.KeyboardInput;

import static client.monstergame.view.controller.Movement.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 *
 * Diese Klasse k�mmert sich darum, dass die Datens�tze von Monster und Spieler aktualisert werden.
 * Beim Monster passiert dies von Aufruf zu Aufruf, da das Monster dem Pfad folgt.
 * Beim Spieler �ndern sich die Daten nur, wenn eine Eingabe per Tastatur vorgenommen wurde.
 *
 * @author Philipp
 *
 */
public class Updater {
    private Driver driver;
    private ArrayList<Point> points;
	private long monsterTimer;
	private float velocity;

    /**
     * Updater-Konstruktor
     *
     * @param driver Um auf die Daten des Monsters und des Spielers zugreifen zu k�nnen muss dem Updated, der Driver mitgegeben werden.
     */
    public Updater(Driver driver) {
        this.driver = driver;
        points = null;
        velocity = 0.00001f;

    	refreshMonsterPath();
    }

	/**
	 *
	 * Diese Funktion wird bei jedem Frame aufgerufen.
	 * Sie berechnet den neuen Punkt f�r das Monster und falls Tastatureingaben vorgenommen werden, einen neuen Punkt f�r den Spieler
	 *
	 */
    public void update() {
        if (points == null) {
        	System.out.println("Refresh!");
        	refreshMonsterPath();
        }
        else{
        	double deltaT = System.currentTimeMillis() - monsterTimer;
        	double length = deltaT * velocity;

	    	Point[] wAIpoints = whereAmI(length);

	    	double deltaX = wAIpoints[1].getX() - wAIpoints[0].getX();
	    	double deltaY = wAIpoints[1].getY() - wAIpoints[0].getY();

	    	double percent = length / calcLength(wAIpoints[0], wAIpoints[1]);

	    	if(percent <= 1){
		    	Point newP = new Point(wAIpoints[0].getX() + deltaX * percent, wAIpoints[0].getY() + deltaY * percent);
		    	getMove().moveTo(getGame().getMonster(), newP);
		    }
	    	else{
	    		if(getGame().getQuadController().testLineForObstacles(getGame().getPlayer().getPoint(), getGame().getMonster().getPoint())){
	    			refreshMonsterPath();
	    		}
	    	}
        }

        if (KeyboardInput.isKeyDown(GLFW_KEY_W)) {
            getMove().move(getGame().getPlayer(), UP);
            refreshMonsterPath();
        }
        if (KeyboardInput.isKeyDown(GLFW_KEY_S)) {
            getMove().move(getGame().getPlayer(), DOWN);
            refreshMonsterPath();
        }
        if (KeyboardInput.isKeyDown(GLFW_KEY_A)) {
            getMove().move(getGame().getPlayer(), LEFT);
            refreshMonsterPath();
        }
        if (KeyboardInput.isKeyDown(GLFW_KEY_D)) {
            getMove().move(getGame().getPlayer(), RIGHT);
            refreshMonsterPath();
        }
    }

    /**
     * Generiert den Pfad f�r das Monster neu.
     */
    private void refreshMonsterPath(){
    	points = getGame().getPathForMonster().getPoints();
        points.remove(points.size()-1);
        monsterTimer = System.currentTimeMillis();
    }

    /**
     * Gibt zur�ck zwischen welchen zwei Punkten das Monster im Moment ist.
     *
     * @param rest Die bereits gefahrene L�nge des Monsters
     * @return Point[] in dem Array befinden sich zwei Punkte, zwischen diesen zwei Punkten befindet sich das Monster.
     */
    private Point[] whereAmI(double rest){
    	int point = 1;
    	for(int i = 1; i < points.size(); i++){

    		double length = calcLength(points.get(i-1), points.get(i));

    		if(rest > length){
    			rest += length - calcLength(points.get(i-1), points.get(i));
    			point = i;
    		}
    	}
    	return new Point[]{points.get(point-1), points.get(point)};
    }

    /**
     *
     * Satz des Pythagoras - Implementation
     *
     * @param one Punkt eins
     * @param two Punkt zwei
     * @return double, die L�nge zwischen Punkt eins und zwei.
     */
    private double calcLength(Point one, Point two){
    	return Math.abs( Math.pow((one.getX() - two.getX()), 2) + Math.pow((one.getY() - two.getY()), 2) );
    }

    private Game getGame() {
        return driver.getGame();
    }

    private Movement getMove() {
        return driver.getMove();
    }

}
