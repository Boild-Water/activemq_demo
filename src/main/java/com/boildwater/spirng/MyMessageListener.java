package com.boildwater.spirng;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @author jinfei
 * @create 2019-10-27 20:16
 */
public class MyMessageListener implements MessageListener {


    @Override
    public void onMessage(Message message) {

        if (message != null && message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                System.out.println("消费消息:" + textMessage.getText());
            } catch (JMSException e){
                e.printStackTrace();
            }
        }
    }
}
