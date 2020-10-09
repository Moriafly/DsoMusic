package com.dirror.music.widget.lyric;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author 24568
 */
public class LyricInfo implements Parcelable {
    public static final Creator<LyricInfo> CREATOR = new Creator<LyricInfo>() {
        /* renamed from: X */
        @Override
        public LyricInfo[] newArray(int i) {
            return new LyricInfo[i];
        }

        /* renamed from: f */
        @Override
        public LyricInfo createFromParcel(Parcel parcel) {
            return new LyricInfo(parcel);
        }
    };
    public String content;
    public float height;
    public Boolean isAdded = Boolean.valueOf(false);
    public int line;
    public float originHeight;
    public float scrollY;
    public long start;
    public float width;

    public LyricInfo(Parcel parcel) {
        this.content = parcel.readString();
        this.line = parcel.readInt();
        this.start = parcel.readLong();
        this.height = parcel.readFloat();
        this.originHeight = parcel.readFloat();
        this.width = parcel.readFloat();
        this.scrollY = parcel.readFloat();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.content);
        parcel.writeInt(this.line);
        parcel.writeLong(this.start);
        parcel.writeFloat(this.height);
        parcel.writeFloat(this.originHeight);
        parcel.writeFloat(this.width);
        parcel.writeFloat(this.scrollY);
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String str) {
        this.content = str;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("LineInfo{content='");
        stringBuilder.append(this.content);
        stringBuilder.append('\'');
        stringBuilder.append(", line=");
        stringBuilder.append(this.line);
        stringBuilder.append(", start=");
        stringBuilder.append(this.start);
        stringBuilder.append(", height=");
        stringBuilder.append(this.height);
        stringBuilder.append(", originHeight=");
        stringBuilder.append(this.originHeight);
        stringBuilder.append(", width=");
        stringBuilder.append(this.width);
        stringBuilder.append(", scrollY=");
        stringBuilder.append(this.scrollY);
        stringBuilder.append(", isAdded=");
        stringBuilder.append(this.isAdded);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

