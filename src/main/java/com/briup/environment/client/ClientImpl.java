package com.briup.environment.client;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

import com.briup.environment.bean.Environment;
import com.briup.environment.util.BackupImpl;
import com.briup.environment.util.LogImpl;

public class ClientImpl implements Client {

	LogImpl log = new LogImpl();
	
	
	@Override
	public void init(Properties properties) throws Exception {

	}

	/**
	 * send����������������˽��н���������Environment�������������ˡ�
	 * 
	 * @param coll
	 * @throws Exception
	 */
	@Override
	public void send(Collection<Environment> coll) {

		BackupImpl bk = new BackupImpl();
		String backup = "ClientBackup.txt";
		ObjectOutputStream oos = null;
		
		try {
			// ���ӷ��������һ�ȡ�ͻ��������
			Socket client = new Socket("127.0.0.1", 8088);
			oos = new ObjectOutputStream(client.getOutputStream());
			log.info("�ɹ����ӷ�����...");

			// ÿ�δ���֮ǰ���ȼ�鱸���ļ��Ƿ����ļ�
			// �еĻ��ѱ����ļ�������ݷŵ�coll��һ�𴫣�û�еĻ���ֱ�Ӵ�
			Object obj = bk.load(backup);
			if (obj != null) {
				Collection<Environment> collb = (Collection<Environment>)obj;
				coll.addAll(collb);
				// ����֮���ɾ�������ļ�
				bk.deleteBackup(backup);
				System.out.println("----------");
				log.warn("��ȡ�����ļ�,������δ����ɹ��Ĳ�������"+collb.size()+"��.����һͬ����.");
			}else {
				log.warn("�ͻ��˱����ļ�������...");
			}

			oos.writeObject(coll);
			log.info("�ɹ��������ݣ�");
			
			oos.flush();
			if(oos != null)
				oos.close();
			log.info("�ر���Դ.");
			
		} catch (Exception e) {
			log.error("����ʧ�ܣ��쳣��Ϣ��"+e.getStackTrace());
			// ����ģ��ͻ��˲����ڷ������ݵ�ʱ������쳣�������ݽ��б���
			try {
				bk.backup(backup, coll);
				log.info("δ�ɹ����������ѱ��ݣ�");
			} catch (Exception e1) {
				log.error("����ʧ�ܣ��쳣��Ϣ��"+e1.getMessage());
			}
		}

	}

}
