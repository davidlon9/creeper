package com.davidlong.demo.traiker.test;

public class TestThread {
    public static void main(String[] args) {
        Thread thread=new Thread(new RunTest2());
        thread.start();
        try {
            Thread.sleep(100);
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("interrupt");
        thread.interrupt();
    }
    public static class RunTest1 implements Runnable{

        @Override
        public void run() {
            int i=0;
            while (true){
                if(Thread.currentThread().isInterrupted()){
                    System.out.println("stoped");
                    break;
                }
                System.out.println(++i);
                if(!Thread.currentThread().isInterrupted()){
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        System.out.println("stop sleep");
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    public static class RunTest2 implements Runnable{
        @Override
        public void run() {
            int i=0;
            System.out.println("start");
            while (i<10000){
                ++i;
                System.out.println(i);
                if(Thread.currentThread().isInterrupted()){
                    break;
                }
            }
            System.out.println("end");
        }
    }
}
