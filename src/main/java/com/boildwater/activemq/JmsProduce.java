package com.boildwater.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @author jinfei
 * @create 2019-10-23 10:42
 */
public class JmsProduce {

    /**
     * activemq_url需要注意的是：
     *  这个端口号是通过ftp连接到linux上ActiveMQ服务器的端口号(61616)
     *  而不是通过HTTP连接到ActiveMQ前端控制台的端口号(8161)
     *  连接到前端控制台的url地址为:http://192.168.217.128:8161
     */
    public static final String ACTIVEMQ_URL = "tcp://192.168.217.128:61616";
    public static final String QUEUE_NAME = "queue01";

    public static void main(String[] args) throws JMSException {

        //1.创建连接工厂，按照给定的url地址没采用默认的用户名和密码
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        //2.通过连接工厂获取连接
        Connection connection = factory.createConnection();
        connection.start();
        //3.创建会话session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //4.创建目的地(目的地可以是Queue队列也可以是topic主题)
        Queue queue = session.createQueue(QUEUE_NAME);
        //5.创建消息的生产者
        MessageProducer messageProducer = session.createProducer(queue);

        /**
         * 设置消息是否被持久化，默认会持久化(对于队列而言)
         * 如果设置了非持久化，那么当ActiveMQ宕机，再次启动后，没有消费的消息将会被丢弃。
         */
        messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        //6.通过使用messageProducer来生产3条消息，发送到MQ队列里
        for (int i = 1; i <= 3; i++) {
            //7.使用session创建消息
            TextMessage textMessage = session.createTextMessage("message " + i);

            //8.通过messageProducer发送给MQ队列
            messageProducer.send(textMessage);

        }

        //9.关闭资源
        messageProducer.close();
        session.close();
        connection.close();

        System.out.println("===========消息发布到MQ完成=================");
    }
}
