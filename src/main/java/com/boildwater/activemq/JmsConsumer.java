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
        /**
         * 对于消费者而言：
         *  表示事务的参数也可以为false或者为true
         *  与生产者相同的是，如果为true，也需要在消费者消费消息之后手动提交事务，
         *  如果不提交，那么MQ中的消息将无法被消费成功，会一直保存在MQ中，会导致重复消费的问题
         *
         *  表示签收的属性:(签收的设置偏向于消费者)
         *      AUTO_ACKNOWLEDGE:自动签收
         *      CLIENT_ACKNOWLEDGE:手动签收 如果开启了手动签收，消费者需要手动签收消息，
         *          否则无法消费消息，会导致重复消费的问题
         *      DUPS_OK_ACKNOWLEDGE:允许重复消息
         *
         *  对于消费者来说，事务与签收还有一点需要注意：
         *      如果事务为true，签收为CLIENT_ACKNOWLEDGE，
         *          只要事务提交后(session.commit)，无论是否手动签收(textMessage.acknowledge)，都会签收成功
         *      但是如果事务为true，签收为CLIENT_ACKNOWLEDGE，
         *          仅仅手动签收，而没有手动提交，那么签收还是不能成功，还是会出现重复消费的问题
         *
         */
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);


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

                        //如果设置的是手动签收，那么此处需要下行代码，表示客户端手动签收消息
                        textMessage.acknowledge();

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
