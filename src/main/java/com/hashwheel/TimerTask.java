package com.hashwheel;

/**
 * 具体任务
 */
public interface TimerTask {


    void run(Timeout timeout) throws Exception;

}
