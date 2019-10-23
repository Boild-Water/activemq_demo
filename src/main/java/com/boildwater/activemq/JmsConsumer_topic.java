package com.boildwater.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.IOException;

/**
 * @author jinfei
 * @create 2019-10-23 11:03
 */
public class JmsConsumer_topic {

    public static final String ACTIVEMQ_URL = "tcp://192.168.217.128:61616";
    public static final String TOPIC_NAME = "topic01";

    public static void main(String[] args) throws JMSException, IOException {

        //1.创建连接工厂，按照给定的url地址没采用默认的用户名和密码
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        //2.通过连接工厂获取连接
        Connection connection = factory.createConnection();
        connection.start();
        //3.创建会话session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //4.创建目的地(目的地可以是Queue队列也可以是topic主题)
        Topic topic = session.createTopic(TOPIC_NAME);

        //5.创建消息的消费者
        MessageConsumer messageConsumer = session.createConsumer(topic);

        messageConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (null != message && message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        System.out.println("==========消费者接收到消息:" + textMessage.getText());
                    }catch (JMSException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        System.in.read();

        //8.关闭资源
        messageConsumer.close();
        session.close();
        connection.close();

        /**
         *  destination为topic时，需要先启动订阅(消费)，再启动生产。下面三种情况:
         *  1.先启动1号订阅，再启动生产，生产3条消息
         *      结果: 1号订阅者消费3条消息
         *  2.先启动1号订阅，再启动2号订阅，再启动3号订阅，最后启动生产，生产3条消息
         *      结果: 1号、2号、3号订阅都消费3条消息
         *  3.先启动生产，生产3条消息，再启动1号订阅者
         *      结果: 1号订阅者无法消费生产的3条消息
         *      说明了:如果没有订阅就先生产的话，后面的订阅者不能消费前面生产的消息。
         */

    }
}
