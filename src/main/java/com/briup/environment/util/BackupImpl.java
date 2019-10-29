package com.briup.environment.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

public class BackupImpl implements Backup {

	@Override
	public void init(Properties properties) {
		
	}

	/**
	 * ��������
	 * 
	 * @param fileName
	 *            �����ļ�
	 * @param data
	 *            ��������
	 * @throws Exception
	 */
	// ��������Ŀ¼·��
	String backup = "src/backup";

	@Override
	public void backup(String fileName, Object data) throws Exception {

		// ���������ļ�
		File file = new File(backup, fileName);
		// �жϱ����ļ��Ƿ����
		if (file.exists() == false) {
			file.createNewFile(); // �������ļ�
		}

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName,true));
		oos.writeObject(data);
		oos.writeObject(null);
		
		if (oos != null) {
			oos.close();
		}
	}

	/**
	 * ���ر���
	 * 
	 * @param fileName
	 *            �����ļ�
	 * @return ��������
	 * @throws Exception
	 */
	@Override
	public Object load(String fileName) {

		try {
			File file = new File(backup, fileName);
			if (file.exists() == false) {
				return null;
			} else {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file,true));
				oos.writeObject(null);
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
				Object obj = ois.readObject();
				if (ois != null) {
					ois.close();
				}
				return obj;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ɾ������
	 * 
	 * @param fileName
	 */
	@Override
	public void deleteBackup(String fileName) {
		File file = new File(backup, fileName);
		if (!file.exists()) {
			System.out.println("�ļ������ڣ�");
		} else {
			file.delete();
		}

	}

}
