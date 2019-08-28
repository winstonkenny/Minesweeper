package com.kenny;
/**
 * 雷区类
 */
public class Point {
    private int x;//雷区的x索引
    private int y;//雷区的y索引
    private int mode;//状态模式

    public Point(int x,int y, int mode){
        this.x=x;
        this.y=y;
        this.mode=mode;
    }

    public int getX(){//拿到雷区中x的索引
        return x;
    }
    public int getY(){
        return y;
    }
    public void setMode(int mode_temp){
        mode=mode_temp;
    }
    public int getMode(){//获得获得雷区的现状
        return mode;
    }
}
