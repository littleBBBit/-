package com.briup.environment.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import com.briup.environment.bean.Environment;
import com.briup.environment.util.BackupImpl;
import com.briup.environment.util.LogImpl;

public class DBStoreImpl implements DBStore {

	LogImpl log = new LogImpl();
	
	@Override
	public void init(Properties properties) throws Exception {

	}

	/**
	 * ��BIDR���Ͻ��г־û� ��
	 * 
	 * @param coll
	 *            ��Ҫ�����Environment����
	 * @throws Exception
	 */
	@Override
	public void saveDb(Collection<Environment> coll) {

		BackupImpl bkl = new BackupImpl();
		String backup = "DBStoreBackup.txt";
		int temp = 0;

		try {
			// �����ݿ⿪������
			String JDBCpath = "src/main/java/com/briup/environment/server/jdbc.properties";
			Properties pro = new Properties();
			BufferedReader jdbcbr = new BufferedReader(new InputStreamReader(new FileInputStream(JDBCpath)));
			pro.load(jdbcbr);
			Class.forName(pro.getProperty("driverName"));
			Connection conn = DriverManager.getConnection(pro.getProperty("url"), pro.getProperty("user"),
					pro.getProperty("password"));
			log.info("���ݿ�ɹ���������...");
			
			Statement st = conn.createStatement();
			// �ֶ��ύ����
			conn.setAutoCommit(false);

			// ��ѯ���ݿ����Ƿ������ű����򴴽�������ֱ�Ӳ�������
			String sql1 = "select * from user_objects where object_name = 'ENVIRONMENT'";
			if (st.executeQuery(sql1).next() == false) {
				String sql2 = "create table ENVIRONMENT(\r\n" + "		name varchar2(20),\r\n"
						+ "		srcId varchar2(5),\r\n" + "		dstId varchar2(5),\r\n" + "		devId varchar2(5),\r\n"
						+ "		sersorAddress varchar2(7),\r\n" + "		count number(2),\r\n"
						+ "		cmd varchar2(5),\r\n" + "		status number(2),\r\n" + "		data number(9,4),\r\n"
						+ "		gather_date date)";
				st.execute(sql2);
				log.info("���ݿ���޴˱�,�ҳɹ������˱�...");
			}

			// ÿ�δ���֮ǰ���ȼ�鱸���ļ��Ƿ������ݣ��еĻ��ȴ����ݵĲ��֣�û�еĻ���ֱ�Ӵ�
			Object obj = bkl.load(backup);
			if (obj != null) {
				Collection<Environment> collb = (Collection<Environment>)obj;
				coll.addAll(collb);
				// ����֮���ɾ�������ļ�������
				bkl.deleteBackup(backup);
				System.out.println("--------------");
				log.warn("��ȡ�����ļ�,������δ����ɹ��Ĳ�������"+collb.size()+"��.����һͬ����.");
			}
			
			// �������ϣ����������е����ݴ������ݿ����
			String sql3 = "insert into ENVIRONMENT values(?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = conn.prepareStatement(sql3);

			int count = 0;
			for (Environment e : coll) {
				if (e != null) {
					Environment element = (Environment) e;

					pst.setString(1, element.getName());
					pst.setString(2, element.getSrcId());
					pst.setString(3, element.getDstId());
					pst.setString(4, element.getDevId());
					pst.setString(5, element.getSersorAddress());
					pst.setInt(6, element.getCount());
					pst.setString(7, element.getCmd());
					pst.setInt(8, element.getStatus());
					pst.setFloat(9, element.getData());
					pst.setDate(10, new Date(element.getGather_date().getTime()));

					pst.addBatch();
					++count;
					if (count % 500 == 0) {
						pst.executeBatch();
						conn.commit();
						++temp;
					}
				}
			}

			pst.executeBatch();
			conn.commit();
			log.info("���ݳɹ����洢�����δ���"+coll.size()+"�����ݣ�");

			st.close();
			pst.close();
			conn.close();
			if(jdbcbr == null)
				jdbcbr.close();
			log.info("�ر���Դ.");
		} catch (Exception e1) {
			log.error("����ʧ�ܣ��쳣��Ϣ��"+e1.getMessage());
			// �����������ģ���������ݿ��������ʱ�����쳣������δ���������
			try {

				// ǰ�洫��ɹ������ݲ���Ҫ����,����һ��֮ǰ��ɹ��˶�������
				int tergat = coll.size() - (temp * 500);
				log.error(tergat+"������δ�ɹ���⣡"+(temp * 500)+"����������⣡");
				Collection<Environment> newcoll = new ArrayList<Environment>();
				Iterator e = coll.iterator();
				int i = 0;
				while (e.hasNext()) {
					e.next();
					++i;
					if (i > tergat)
						newcoll.add((Environment) e.next());
				}
				bkl.backup(backup, newcoll);
				log.error("δ�ɹ���������ѱ��ݣ�");

			} catch (Exception e) {
				log.error("����ʧ�ܣ��쳣��Ϣ��"+e.getMessage());
			}
		}
	}

}
