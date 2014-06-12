package com.photoplay;

import java.util.ArrayList;

import org.andengine.entity.primitive.Vector2;

public class Grid {
	float gridWidth;
	float gridHeight;
	float fieldWidth;
	float fieldHeight;
	Vector2 currentPosition;
	ArrayList<Vector2> unavailablePositions;
	
	public Grid(float gridWidth, float gridHeight, float fieldWidth, float fieldHeight) {
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		this.fieldWidth = fieldWidth;
		this.fieldHeight = fieldHeight;
		currentPosition = new Vector2(0,0);
		unavailablePositions = new ArrayList<Vector2>();
	}
	
	public void UpdatePosition(){
		if (!isFinished()) {
			float X = currentPosition.x;
			float Y = currentPosition.y;
			if (X >= gridWidth){
				X = 0;
				Y += fieldHeight;
			} else {
				X += fieldWidth;
			}
			currentPosition = new Vector2(X,Y);
		}
	}
	
	public Vector2 GetCurrentPosition() {
		return currentPosition;
	}
	
	public boolean isFinished() {
		float X = currentPosition.x;
		float Y = currentPosition.y;
		if (X >= gridWidth && Y >= gridHeight){
			return true;
		} else {
			return false;
		}
	}
	
	public Vector2 getNearestSnapPoint(Vector2 v) {
		float npx, npy;
		float x = v.x, y = v.y;
		float mx = x % fieldWidth, my = y % fieldHeight;

		if (mx < fieldWidth / 2)
			npx = x - mx;
		else
			npx = x + (fieldWidth - mx);

		if (my < fieldHeight / 2)
			npy = y - my;
		else
			npy = y + (fieldHeight - my);
		
		return new Vector2(npx,npy);
	}
	
	public boolean snapPointIsAvailible(Vector2 v) {
		for(Vector2 item: unavailablePositions){
			if (item.x == v.x && item.y == v.y){
				return false;
			}	
		}
		return true;
	}
	
	public void removeUnavailibleSnappoint(Vector2 v) {
		for(Vector2 item: unavailablePositions){
			if (item.x == v.x && item.y == v.y){
				unavailablePositions.remove(item);
			}	
		}
	}
	
	public void addUnavailibleSnappoint(Vector2 v) {
		unavailablePositions.add(v);
	}
}
