package com.sunrise.scmbhc.utils;

import java.util.Observable;

public class DatasUpdateObservable extends Observable{
	
public void refresh(){
	 setChanged();
	 notifyObservers();
}

}
