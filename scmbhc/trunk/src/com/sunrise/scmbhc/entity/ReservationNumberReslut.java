package com.sunrise.scmbhc.entity;

public class ReservationNumberReslut {
private int resultCode;
private String result;
private String number;

public ReservationNumberReslut(String reslut, String number) {
	super();
	this.result = reslut;
	this.number = number;
}



public String getResult() {
	return result;
}



public void setResult(String result) {
	this.result = result;
}



public String getNumber() {
	return number;
}

public void setNumber(String number) {
	this.number = number;
}




public int getResultCode() {
	return resultCode;
}



public void setResultCode(int resultCode) {
	this.resultCode = resultCode;
}



@Override
public String toString() {
	return "ReservationNumberReslut [reslut=" + result + ", number=" + number
			+ "]";
}


}
