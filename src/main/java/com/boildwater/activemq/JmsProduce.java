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
        /**
         * 对于生产者而言:(事务的设置偏向于生产者)
         * 第一个参数表示是否开启事务？
         *      false表示:不开启事务
         *      true表示:开启事务，需要在资源关闭前，手动的提交生产的消息，session.commit();
         * 第二个参数指定签收
         */
        Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

        try {
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
            //如果事务设置为true，这里需要手动提交上面生产的消息，否则消息将无法发布到MQ
            session.commit();

        } catch (Exception e){
            session.rollback();
            e.printStackTrace();
        } finally {

            //9.关闭资源
            session.close();
            connection.close();
        }
        System.out.println("===========消息发布到MQ完成=================");
    }
}
