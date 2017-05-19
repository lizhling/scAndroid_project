package com.sunrise.econan.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 城市信息
 * 
 * @author fuheng
 */
public class CityInfo implements Parcelable {
	private String name;
	private String id;

	/**
	 * 指标得分
	 */
	private float indicatorScore;
	/**
	 * 平均得分
	 */
	private float averageScore;
	/**
	 * 排名
	 */
	private int ranking;
	/**
	 * 等级
	 */
	private int level;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return 指标得分
	 */
	public float getIndicatorScore() {
		return indicatorScore;
	}

	/**
	 * @param indicatorScore
	 *            指标得分
	 */
	public void setIndicatorScore(float indicatorScore) {
		this.indicatorScore = indicatorScore;
	}

	/**
	 * @return 平均得分
	 */
	public float getAverageScore() {
		return averageScore;
	}

	/**
	 * @param averageScore
	 *            平均得分
	 */
	public void setAverageScore(float averageScore) {
		this.averageScore = averageScore;
	}

	/**
	 * @return 排名
	 */
	public int getRanking() {
		return ranking;
	}

	/**
	 * @param ranking
	 *            排名
	 */
	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append("id=").append(id);
		sb.append(',').append("name=").append(name);
		sb.append(',').append("indicatorScore=").append(indicatorScore);
		sb.append(',').append("averageScore=").append(averageScore);
		sb.append(',').append("ranking=").append(ranking);
		sb.append(',').append("level=").append(level);
		sb.append(')');
		return sb.toString();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(id);
		arg0.writeString(name);
		arg0.writeFloat(averageScore);
		arg0.writeFloat(indicatorScore);
		arg0.writeInt(ranking);
		arg0.writeInt(level);
	}

	public static final Parcelable.Creator<CityInfo> CREATOR = new Creator<CityInfo>() {

		@Override
		public CityInfo createFromParcel(Parcel arg0) {
			CityInfo info = new CityInfo();
			info.setId(arg0.readString());
			info.setName(arg0.readString());
			info.setAverageScore(arg0.readFloat());
			info.setIndicatorScore(arg0.readFloat());
			info.setRanking(arg0.readInt());
			info.setLevel(arg0.readInt());
			return info;
		}

		@Override
		public CityInfo[] newArray(int size) {
			return new CityInfo[size];
		}
	};
}
