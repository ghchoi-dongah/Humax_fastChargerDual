package com.dongah.fastcharger.handler;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.basefunction.GlobalVariables;
import com.dongah.fastcharger.websocket.socket.SocketReceiveMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomUnitPriceThread extends  Thread {

    private static final Logger logger = LoggerFactory.getLogger(CustomUnitPriceThread.class);


    boolean stopped = false;
    int delayTime;
    int count = 0;

    SocketReceiveMessage socketReceiveMessage;
    ProcessHandler processHandler;

    public CustomUnitPriceThread(int delayTime) {
        this.delayTime = delayTime;

    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void run() {
        super.run();
        while (!isStopped()) {
            try {
                // Thread loop 빠져나오기 위해 1sec 단위로 시간을 체크하고 interrupt 사용
                // thread restart 가 안되고 new instance 해야 함.
                Thread.sleep(1000);
                count++;
            } catch (Exception e){
                logger.error("thread sleep error : {}", e.getMessage());
            }
            // 60분마다 충전 단가 갖고 오기
            if (count >= (delayTime)) {
                count = 0;
                boolean chk;
                try {
                    socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
                    processHandler = ((MainActivity) MainActivity.mContext).getProcessHandler();
                    GlobalVariables.setCustomUnitPriceReq(true);
                    GlobalVariables.setHumaxUserType("A");
                    processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                            GlobalVariables.MESSAGE_CUSTOM_UNIT_PRICE,
                            1,
                            0,
                            null,
                            null,
                            GlobalVariables.getHumaxUserType(),       /////////Customer unit price 에서만 userType(A:회원, B:비회원) 사용 한다.
                            false));

                } catch (Exception e){
                    logger.error(" CustomUnitPriceThread error : {}", e.getMessage());
                }
            }
        }
    }



    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public boolean isInterrupted() {
        return super.isInterrupted();
    }
}
