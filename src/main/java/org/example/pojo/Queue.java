package org.example.pojo;
/*
    队列类
 */
public class Queue {
    public int maxSize = 100;
    public int[] data = new int[maxSize];
    public int front; //队首
    public int rear; //队尾
}
