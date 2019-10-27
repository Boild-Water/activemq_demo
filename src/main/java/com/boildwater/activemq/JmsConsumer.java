package com.boildwater.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.IOException;

/**
 * @author jinfei
 * @create 2019-10-23 11:03
 */
public class JmsConsumer {

    public static final String ACTIVEMQ_URL = "tcp://192.168.217.128:61616";
    public static final String QUEUE_NAME = "queue01";

    public static void main(String[] args) throws JMSException, IOException {

        //1.创建连接工厂，按照给定的url地址没采用默认的用户名和密码
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        //2.通过连接工厂获取连接
        Connection connection = factory.createConnection();
        connection.start();
        //3.创建会话session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //4.创建目的地(目的地可以是Queue队列也可以是topic主题)
        Queue queue = session.createQueue(QUEUE_NAME);

        //5.创建消息的消费者
        MessageConsumer messageConsumer = session.createConsumer(queue);



        /**
         * 通过监听的方式来消费消息
         *  异步非阻塞方式(onMessage())
         *  订阅者或者接收者通过MessageConsumer的serMessageListener(MessageListener listener)注册一个消息监听器
         *  当消息到达后，系统自动调用监听器MessageListener的onMessage(Message message)方法
         */
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
        //让程序保持在这，因为onMessage是异步非阻塞的方式
        //防止程序运行过快，执行到下面代码资源已经关闭了，但是消息还没有被消费完。
        System.in.read();

        //8.关闭资源
        messageConsumer.close();
        session.close();
        connection.close();

    }
}
