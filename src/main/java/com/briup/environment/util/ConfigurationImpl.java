package com.briup.environment.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.briup.environment.client.Client;
import com.briup.environment.client.Gather;
import com.briup.environment.server.DBStore;
import com.briup.environment.server.Server;

public class ConfigurationImpl implements Configuration{
	
	//ʹ��Map���ϱ�������ģ��Ķ���
	Map<String, WossModule> map = new HashMap<>();
	
	
	
	public ConfigurationImpl() throws Exception {
		//dom4j�������̣�maven���̲��赼��
		SAXReader reader = new SAXReader();
		Document doc = reader.read("src/main/java/com.briup/environment/util/config.xml");
		Element  root = doc.getRootElement();
		List<Element> elements = root.elements();
		for(Element element:elements) {
			//�ȶ�ȡclass����ֵ����ȡ��ÿ��ģ���ȫ������
			String className= element.attributeValue("class");
			//���÷�����ƴ���ģ��ʵ��������
			WossModule woss = (WossModule) Class.forName(className).newInstance();
			
			//�Ѵ����õĶ��󱣴浽Map������
			map.put(element.getName(), woss);
			
			List<Element> childElements = element.elements();
			Properties pro = new Properties();
			for(Element child:childElements) {
				String key = child.getName();
				String value = child.getText();
				pro.setProperty(key, value);
			}
			woss.init(pro);
		
		}
		for(String key:map.keySet()) {
			WossModule woss = map.get(key);
			//���ô����õ�ģ������setConfiguration()����
			//���Լ�����ȥ
			//���ж����ģ���Ƿ�ʵ����ConfigurationAWare�ӿ�
			if(woss instanceof ConfigurationAWare)
				((ConfigurationAWare)woss).setConfiguration(this);
		}
		
		
	}
	
	

	@Override
	public Log getLogger() throws Exception {
		return (Log) map.get("logger");
	}

	@Override
	public Server getServer() throws Exception {
		return (Server) map.get("server");
	}

	@Override
	public Client getClient() throws Exception {
		return (Client) map.get("client");
	}

	@Override
	public DBStore getDbStore() throws Exception {
		return (DBStore) map.get("dbstore");
	}

	@Override
	public Gather getGather() throws Exception {
		return (Gather) map.get("gather");
	}

	@Override
	public Backup getBackup() throws Exception {
		return (Backup) map.get("backup");
	}

	
}
