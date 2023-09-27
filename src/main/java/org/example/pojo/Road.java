package org.example.pojo;
/*
    边的权值以及顶点信息
 */
public class Road implements Comparable<Road>{
    public int a, b; //两个顶点
    public double w; //边的权值

    @Override
    public int compareTo(Road o){
        if(this.w > o.w){
            return 1;
        }else if(this.w < o.w){
            return -1;
        }else{
            return 0;
        }
    }
}
