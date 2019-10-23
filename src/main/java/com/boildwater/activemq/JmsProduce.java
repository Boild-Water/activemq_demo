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
        //6.通过使用messageProducer来生产3条消息，发送到MQ队列里
        for (int i = 1; i <= 3; i++) {
            //7.使用session创建消息
            //创建TextMessage消息体
            TextMessage textMessage = session.createTextMessage("message " + i);
            /**
             * 同时还可以设置消息属性
             *  消息属性的作用:
             *      如果需要除消息头字段以外的值，那么可以使用消息属性
             *      同时消息属性在识别/去重/重点标注等操作中是非常有用的方法
             */
            textMessage.setStringProperty("hello","activemq");

            //创建MapMessage消息体 (一共有5种类型的消息体，但是常用的就是TextMessage和MapMessage消息体)
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString("k1","v1");

            //8.通过messageProducer发送给MQ队列
            messageProducer.send(textMessage);
            messageProducer.send(mapMessage);

        }

        //9.关闭资源
        messageProducer.close();
        session.close();
        connection.close();

        System.out.println("===========消息发布到MQ完成=================");
    }
}
