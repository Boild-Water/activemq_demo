package com.boildwater.spirng;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * @author jinfei
 * @create 2019-10-27 17:32
 */
@Service
public class SpringMQ_Consumer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        SpringMQ_Consumer consumer = ac.getBean(SpringMQ_Consumer.class);

        String retValue = (String) consumer.jmsTemplate.receiveAndConvert();

        System.out.println("接收消息：" + retValue);
    }
}
