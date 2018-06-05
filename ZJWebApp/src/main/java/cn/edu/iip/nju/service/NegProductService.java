package cn.edu.iip.nju.service;

import cn.edu.iip.nju.model.InjureCase;
import cn.edu.iip.nju.model.NegProduct;
import cn.edu.iip.nju.model.vo.ProductNegListForm;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class NegProductService {

    @Autowired
    InjureCaseService injureCaseService;

    public List<NegProduct> getByCondition(ProductNegListForm form) {
        List<NegProduct> result = Lists.newArrayList();
        Map<String, NegProduct> map = Maps.newHashMap();
        List<InjureCase> injureCaseList = injureCaseService.getProductListByCondition(form);
        for (InjureCase injureCase : injureCaseList) {
            if (Strings.isNullOrEmpty(injureCase.getProductName())) {
                continue;
            }
            String productName = injureCase.getProductName();
            String[] prods = productName.split(" ");
            for (String prod : prods) {
                if (!map.containsKey(prod)) {
                    NegProduct negProduct = new NegProduct();
                    negProduct.setProductName(prod);
                    negProduct.setCaseNum(1);
                    negProduct.getInjureCases().add(injureCase);
                    negProduct.getArea().add(injureCase.getInjureArea());
                    map.put(prod, negProduct);
                } else {
                    NegProduct negProduct = map.get(prod);
                    negProduct.getArea().add(injureCase.getInjureArea());
                    negProduct.getInjureCases().add(injureCase);
                    negProduct.increaseCaseNum();
                }
            }
        }
        Collection<NegProduct> products = map.values();
        for (NegProduct product : products) {
            product.computeInjureIndex();
            if(product.getArea().size()>3){
                product.setAreaString(org.assertj.core.util.Strings.join(Arrays.asList(product.getArea().toArray()).subList(0,3)).with(" "));
            }else{
                product.setAreaString(org.assertj.core.util.Strings.join(Arrays.asList(product.getArea().toArray())).with(" "));
            }
            result.add(product);
        }
        if (!Strings.isNullOrEmpty(form.getProductName())) {
            Stream<NegProduct> stream = result.stream().filter(negProduct -> negProduct.getProductName().contains(form.getProductName()));
            List<NegProduct> list = stream.collect(Collectors.toList());
            return list;
        }
        return result;
    }
}
