import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.*;
import org.xml.sax.SAXException;
import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PMMLDemo {
    private Evaluator loadPmml(){
        PMML pmml = new PMML();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("km.pmml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(inputStream == null){
            return null;
        }
        InputStream is = inputStream;
        try {
            pmml = org.jpmml.model.PMMLUtil.unmarshal(is);
        } catch (SAXException e1) {
            e1.printStackTrace();
        } catch (JAXBException e1) {
            e1.printStackTrace();
        }finally {
            //关闭输入流
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ModelEvaluatorBuilder modelEvaluatorBuilder = new ModelEvaluatorBuilder(pmml);
        Evaluator evaluator = modelEvaluatorBuilder.build();
        pmml = null;
        return evaluator;
    }
    private void predict(Evaluator evaluator,Map<?,?> data) {
        List<InputField> inputFields = evaluator.getInputFields();
        //过模型的原始特征，从画像中获取数据，作为模型输入
        Map<FieldName, FieldValue> arguments = new LinkedHashMap<FieldName, FieldValue>();
        for (InputField inputField : inputFields) {//将参数通过模型对应的名称进行添加
            FieldName inputFieldName = inputField.getName();//获取模型中的参数名
            Object rawValue = data.get(inputFieldName.getValue());//获取模型参数名对应的参数值
            FieldValue inputFieldValue = inputField.prepare(rawValue);//将参数值填入模型中的参数中
            arguments.put(inputFieldName, inputFieldValue);//存放在map列表中
        }
        Map<FieldName, ?> results = evaluator.evaluate(arguments);
        List<TargetField> targetFields = evaluator.getTargetFields();
        Object targetFieldValue = results.get(targetFields.get(0).getFieldName());
        System.out.println("targetFieldValue: " + targetFieldValue);
        System.out.println("target: " + results);
    }
    public static void main(String args[]){
        PMMLDemo demo = new PMMLDemo();
        Evaluator model = demo.loadPmml();
        Map<String, Object> paramData = new HashMap<>();
        paramData.put("Chinese", 25);
        paramData.put("Math", 100);
        demo.predict(model,paramData);
        Map<String, Object> paramData2 = new HashMap<>();
        paramData2.put("Chinese", 100);
        paramData2.put("Math", 50);
        demo.predict(model,paramData2);
    }
}
