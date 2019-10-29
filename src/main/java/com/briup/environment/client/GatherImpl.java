package com.briup.environment.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import com.briup.environment.bean.Environment;
import com.briup.environment.util.BackupImpl;
import com.briup.environment.util.LogImpl;

//ʵ��Gather�ӿ�
/**
 * �ɼ����ܼҾӵĻ�����Ϣ�������ݷ�װΪEnvironment���Ϸ��ء�
 * 
 * @return �ɼ���װEnvironment���ݵļ���
 * @throws Exception
 */
public class GatherImpl implements Gather {

	LogImpl log = new LogImpl();

	@Override
	public void init(Properties properties) throws Exception {

	}

	// ���زɼ���װEnvironment���ݵļ���
	@Override
	public Collection<Environment> gather() {

		// ������������
		Collection<Environment> coll = new ArrayList<>();
		// �������ݶ���
		BackupImpl bkl = new BackupImpl();
		String backup = "src/main/java/com/briup/environment/GatherBackup.txt";

		try {
			// 1.IO������radwtmp�ļ�
			String path = "src/main/java/com/briup/environment/radwtmp";
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			// �������ļ�
			BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(backup)));
			log.info("IO������...");

			// ͳ�Ʊ����ļ����Ѷ�ȡ���ַ���
			long backupNum = 0;
			String temp;
			while ((temp = br2.readLine()) != null) {
				if (temp.equals(""))
					continue;
				backupNum += Long.parseLong(temp);
			}
			log.info("��ȡ�����ļ�����...");

			// �����Ѷ�ȡ���ַ�
			if (backupNum >= 0) {
				br.skip(backupNum);
			}
			log.info("��ʼ�ɼ�����...");

			// ����Environment����,������ȡ�������ݴ���������
			int size = 0; // ��־�����
			String result;
			int thistime = 0; // ��ʱ������ͳ�Ʊ��ζ�ȡ�ַ��������뱸���ļ���
			while ((result = br.readLine()) != null) {
				Environment env = new Environment();
				String[] results = result.split("\\|");

				env.setSrcId(results[0]);
				env.setDstId(results[1]);
				env.setDevId(results[2]);
				/*
				 * 16�����¶Ⱥ�ʪ�� 256�������ǿ�� 1280���������̼ǿ��
				 */
				env.setSersorAddress(results[3]);
				switch (results[3]) {
				case "16":
					// ��������ʱ�پ����޸�
					break;
				case "256":
					env.setName("����ǿ��");
					break;
				case "1280":
					env.setName("������̼ǿ��");
					break;
				default:
					break;
				}

				env.setCount(Integer.parseInt(results[4]));
				env.setCmd(results[5]);
				env.setStatus(Integer.parseInt(results[7]));
				env.setGather_date(new Timestamp(Long.parseLong(results[8])));
				/*
				 * �¶Ⱥ�ʪ����ͬʱ�ɼ��ģ�����ֻ��name��data�ǲ�ͬ�� �ڴ˴���Ҫ�´�����һ������
				 */
				// "16"��ǰ�棬����Զ������ֿ�ָ���쳣
				if ("16".equals(env.getSersorAddress())) {
					Environment env2 = new Environment();// ʪ�ȵĶ���
					env.setName("�¶�");
					env2.setName("ʪ��");
					// ǰ�����ֽ����¶ȣ��м������ֽ���ʪ�ȡ�
					float tem = (float) Integer.parseInt(results[6].substring(0, 4), 16);// �¶�
					float hum = (float) Integer.parseInt(results[6].substring(4, 8), 16);// ʪ��
					env.setData((float) ((tem * 0.00268127) - 46.85));
					env2.setData((float) ((tem * 0.00190735) - 6));

					// ���¶ȵ�������������copy��ʪ��
					env2.setSrcId(env.getSrcId());
					env2.setCmd(env.getCmd());
					env2.setCount(env.getCount());
					env2.setDevId(env.getDevId());
					env2.setSersorAddress(env.getSersorAddress());
					env2.setDstId(env.getDstId());
					env2.setStatus(env.getStatus());
					env2.setGather_date(env.getGather_date());

					coll.add(env2);
					++size;
				} else {
					// ����ǿ�ȺͶ�����ֱ̼��ת����10����
					long dec = Long.parseLong(results[6].substring(0, 4), 16);
					env.setData((float) dec);

				}

				coll.add(env);
				++size;

				// ͳ�ƶ�ȡ�����ַ���
				thistime += (result.length() + 2);

			}
			log.info("���γɹ��ɼ�" + size + "������," + "��" + thistime + "�ַ���.");

			// д�����ļ�
			PrintWriter pw = new PrintWriter(new FileOutputStream(backup, true));
			// ����ȡ�����ַ���д�뱸���ļ�
			pw.println(thistime);
			log.info("д�뱸���ļ�...");

			pw.flush();
			br.close();
			br2.close();
			pw.close();
			log.info("�ɹ��������ݲ��ر���Դ��");

		} catch (Exception e) {
			log.error("�ɼ�ʧ�ܣ��쳣��Ϣ��"+e.getMessage());
		}
		return coll;
	}

}
