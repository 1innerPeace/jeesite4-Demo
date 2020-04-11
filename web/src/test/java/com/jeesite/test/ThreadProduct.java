package com.jeesite.test;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class ThreadProduct implements Runnable {

    public int ticket;
    public AtomicInteger number;


    public ThreadProduct(int nu){
        this.number.set(nu);
    }

    public void add(){
        number.getAndIncrement();
    }
    public void minus(){
        number.getAndDecrement();
    }

    @Override
    public void run() {
//        while (number.get()==0){
            add();
            System.out.println(number);
//        }
//        while (number.get()>0){
            minus();
            System.out.println(number);
//        }


    }
}
