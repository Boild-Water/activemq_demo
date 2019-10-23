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

//        /**
//         * 同步阻塞方式接收(receive())
//         *  订阅者或者接受者调用MessageConsumer的receive()方法来接收消息，
//         *  receive方法在能够接收到消息之前(或者超时之前)将会一直阻塞
//         */
//        //6.消费消息
//        while (true) {
//            //7.接收生产者生产的消息，注意消息类型要与生产者生产的消息类型一致。
////            TextMessage textMessage = (TextMessage) messageConsumer.receive();//如果没有接收到消息就会一直等在这
//            TextMessage textMessage = (TextMessage) messageConsumer.receive(3000);//重载方法，3s超时接收
//
//            if (textMessage != null) {
//                System.out.println("==========消费者接收到消息:" + textMessage.getText());
//            }else {
//                break;
//            }
//        }

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

        /**
         * 生产消费的三种情况
         *  1.先生产，启动消费者1
         *  2.先生产，先启动消费者1，然后再启动消费者2
         *      结果:消费者1会消费所有生产的消息
         *  3.先启动消费者1，再启动消费者2，最后启动生产者生产6条消息
         *      结果:消费者1和消费者2平分消费6条消息，即每个消费者消费3条消息 ("负载均衡")
         */
    }
}
