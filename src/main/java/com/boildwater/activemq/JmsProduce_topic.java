package com.boildwater.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;


/**
 * @author jinfei
 * @create 2019-10-23 10:42
 */
public class JmsProduce_topic {

    /**
     * activemq_url需要注意的是：
     *  这个端口号是通过ftp连接到linux上ActiveMQ服务器的端口号(61616)
     *  而不是通过HTTP连接到ActiveMQ前端控制台的端口号(8161)
     *  连接到前端控制台的url地址为:http://192.168.217.128:8161
     */
    public static final String ACTIVEMQ_URL = "tcp://192.168.217.128:61616";
    public static final String TOPIC_NAME = "topic01";

    public static void main(String[] args) throws JMSException {

        //1.创建连接工厂，按照给定的url地址没采用默认的用户名和密码
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        //2.通过连接工厂获取连接
        Connection connection = factory.createConnection();
        //3.创建会话session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //4.创建目的地(目的地可以是Queue队列也可以是topic主题)
        Topic topic = session.createTopic(TOPIC_NAME);
        //5.创建消息的生产者
        MessageProducer messageProducer = session.createProducer(topic);

        /**
         * 对于topic的持久化而言，与Queue有些不同
         *  对topic类型的目的地来说，正确启动方式是，先运行消费者，然后再启动生产者。基于这种情况下
         *  如果先运行了消费者，然后将消费者关闭，然后再运行生产者，此时如果再启动消费者，问消费者能否消费消息？
         *      如果配置了持久化，则再次启动的消费者能够消费消息。
         *
         *  这种情况下，可以举一个微信公众号的例子，只要我们之前关注过该公众号，哪怕我们微信退出，手机关机
         *  在这期间，微信公众号，可能会推出一些订阅信息，只要当我们再打开手机，打开微信，那么就能收到这些订阅信息。
         */
        messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);

        //6.通过使用messageProducer来生产3条消息，发送到MQ队列里
        connection.start();
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
