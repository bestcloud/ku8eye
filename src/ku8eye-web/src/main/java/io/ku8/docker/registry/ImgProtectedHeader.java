package io.ku8.docker.registry;

public class ImgProtectedHeader {
private int formatLength;
private String  formatTail;
private String time;
public int getFormatLength() {
	return formatLength;
}
public void setFormatLength(int formatLength) {
	this.formatLength = formatLength;
}
public String getFormatTail() {
	return formatTail;
}
public void setFormatTail(String formatTail) {
	this.formatTail = formatTail;
}
public String getTime() {
	return time;
}
public void setTime(String time) {
	this.time = time;
}
@Override
public String toString() {
	return "ImgProtectedHeader [formatLength=" + formatLength + ", formatTail=" + formatTail + ", time=" + time + "]";
}

}
