package com.photoplay;

public class Score {
	public int score;
	boolean fromPreviousLvl= false;
	
	public Score(int score){
		this.score= score;
	}
	
	public int getScore(){
		return this.score;
	}

	public int normalMove(int Score){
		this.score= Score-50;
		return this.score;
	}
	public int draggedMove(int s){
		this.score= s-25;
		return this.score;
	}

}