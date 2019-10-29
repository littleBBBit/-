package com.briup.environment.server;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

import com.briup.environment.bean.Environment;
import com.briup.environment.util.LogImpl;

public class ServerImpl implements Server {

	boolean flag = true;
	LogImpl log = new LogImpl();
	
	@Override
	public void init(Properties properties) throws Exception {

	}

	/**
	 * �÷��������������Server���񣬿�ʼ���տͻ��˴��ݵ���Ϣ��<br/>
	 * ���ҵ���DBStore�����յ����ݳ־û�
	 * 
	 * @return ���ܵ�Environment���ݵļ���
	 * @throws Exception
	 */

	@Override
	public Collection<Environment> reciver() throws Exception {
		// ����Server���񣬿�ʼ���տͻ��˴��ݵ���Ϣ
		ServerSocket ss = new ServerSocket(8088);
		log.info("������������,���ڼ���״̬...");
		Socket client = ss.accept();

		// ���տͻ��˷��͹���������
		ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
		log.info("�ͻ����������Ѿ���...");
		Collection<Environment> coll = (Collection<Environment>) ois.readObject();
		log.info("�ɹ����տͻ�������...");
		

		// ����DBStore�����յ����ݳ־û�
		new DBStoreImpl().saveDb(coll);
		log.info("�������������ģ�齫���ݳ־û�����...");
		
		ois.close();
		ss.close();
		log.info("�������ɹ��������ݲ��洢...");
		
		return coll;
	}

	/**
	 * �÷�������ʹServer��ȫ��ֹͣ���С�
	 */
	@Override
	public void shutdown() {

		flag = false;
		
	}

}
