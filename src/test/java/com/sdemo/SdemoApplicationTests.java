package com.sdemo;

import com.alibaba.fastjson.JSON;
import com.sdemo.util.HttpClientUtil;
import com.sdemo.util.JsonResult;
import org.dom4j.io.SAXReader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.sdemo.util.JsonUtil;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class SdemoApplicationTests {

    @Test
    void contextLoads() {
        String url1="http://10.220.105.185:8090/PudongDEP/services/PrivilegeControl/webserviceLogin";//?password=pdsw323006";
        String url2="http://10.220.105.185:8090/PudongDEP/services/OuterAppConfig/getDefaultStations";//?plan=PDSZ&appCode=APP_PDCYZX";
        JsonResult<String> result=null;
        String str="";
        try {
            Map<String,Object> map1=new HashMap<String,Object>();
            Map<String,Object> map2=new HashMap<String,Object>();
            map1.put("password","pdsw323006");
            map2.put("plan","PDSZ");
            map2.put("appCode","APP_PDCYZX");
            result =HttpClientUtil.getHttp(url1,map1,null);
            result =HttpClientUtil.getHttp(url2,map2,null);
            System.out.println(result.getData()+"\n"+result.getErrMsg()+"\n"+result.getErrCode()+"\n"+result.getList());
//            str=JsonUtil.objToJson(result.getData());
//            System.out.println(str);

//            str=result.getData().replace("ax27:","");
//            str=str.replace("<ns:getDefaultStationsResponse xmlns:ns=\"http://webservice.dep.kisters.de\" xmlns:ax27=\"http://bean.dep.kisters.de/xsd\">","");
//            str=str.replace("<ns:getDefaultStationsResponse xmlns:ns="+"\\"+"\""+"http://webservice.dep.kisters.de"+"\\"+"\""+" xmlns:ax27="+"\\"+"\""+"http://bean.dep.kisters.de/xsd"+"\\"+"\""+">","");
//            str=str.replace("ns:","");
//            str=str.replace("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ax27:StationBean\"","");
//            System.out.println(str);
            Document doc = null;
            try {
                doc = DocumentHelper.parseText(result.getData());
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            Element root = doc.getRootElement();// 指向根节点

            Iterator it = root.elementIterator();
            while (it.hasNext()) {
                Element element = (Element) it.next();// 一个Item节点
                System.out.println(element.getName() + " : " + element.getTextTrim());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //导入xml
    Document load1(String filename){
        Document doc=null;
        SAXReader saxReader=new SAXReader();
        try {
            doc=saxReader.read(new File(filename));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return doc;
    }
    //导入xml
    Document load2(URL url){
        Document doc=null;
        SAXReader saxReader=new SAXReader();
        try {
            doc=saxReader.read(url);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return doc;
    }

    @Test
    void xmltest(){
        String oldStr = "D:/testfile/s.xml";
        Document document = null ;
        try{
            SAXReader saxReader = new SAXReader();

        }catch (Exception e){

        }
    }




}
