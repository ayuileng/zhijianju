package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.AttachmentDataDao;
import cn.edu.iip.nju.model.AttachmentData;
import org.assertj.core.util.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AttachmentDataService {
    @Autowired
    AttachmentDataDao attachmentDataDao;

    /**
     * 返回所有的企业名称
     * @return
     */
    public Set<String> getAllCompanys(){
        List<Object> companyName = attachmentDataDao.getCompanyName();
        Set<String> companys = Sets.newHashSet();
        for (Object o : companyName) {
            companys.add((String) o);
        }
        return companys;
    }

    /**
     * 返回该企业的案例数
     * @param factoryName
     * @return
     */
    public long numsOfFactory(String factoryName){
        return attachmentDataDao.countAllByFactoryName(factoryName);
    }

    /**
     * 返回该企业的某个检查结果的案例数
     * @param factoryName
     * @param reslut
     * @return
     */
    public long numsOfFactoryByResult(String factoryName,String reslut){
        return attachmentDataDao.countAllByFactoryNameAndResult(factoryName,reslut);
    }

    /**
     * 根据企业名称返回所有的案例
     * @param factoryName
     * @return
     */
    public List<AttachmentData> getByFactoryName(String factoryName){
        return attachmentDataDao.getAllByFactoryName(factoryName);
    }
}
