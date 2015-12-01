package org.ku8eye.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ku8eye.domain.Host;
import org.ku8eye.domain.User;
import org.ku8eye.mapping.HostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author jackchen
 *
 */
@Service
public class HostService {
	
	@Autowired
	private HostMapper hostDao;
	private List<Host> hostList;
	private Logger log = Logger.getLogger(this.toString());
	/**
	 * find User by userid
	 * @param pUserId
	 * @return User
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Host> getHostsByZoneId(int  zoneId){	
		return hostDao.selectAll();
	}
	
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Host> getHostsByZoneString(String  zoneString){	
//		StringBuffer str = new StringBuffer();
//		List<Host> hostlist = null;
//		zoneString="1,2,3,4";
//		String arr[]=zoneString.split(",");
//		for(int i=0;i<arr.length;i++){
//			log.info("aaaaaaaaaaa");
//			if(arr[i].length()>0){
//				Host sd=hostDao.selectByPrimaryKey(Integer.parseInt(arr[i]));
//				hostlist.add(sd);
//				log.info("返回的json串为："+sd);
//			}
//		}
//		
//        log.info("返回的json串为："+hostlist);
//		log.info("dddddddddddd");
//		hostList.add(hostDao.selectByPrimaryKey(1));
//		
//		log.info("aaaaaaaaaaaaa");
		return hostDao.selectByIds("1 or 2 or 3");
	}
	
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public int setHostNode(Host host){
		return hostDao.insert(host);
	}
}
