import org.dmg.pmml.*;
import org.jpmml.evaluator.*;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class KM {
    public static void main(String[] args) throws SAXException, JAXBException, IOException {
        Map<String, Object> paramData = new HashMap<>();
        paramData.put("Chinese", 25);
        paramData.put("Math", 100);
        paramData.put("Chinese", 100);
        paramData.put("Math", 50);
        Evaluator evaluator = new LoadingModelEvaluatorBuilder().load(new File("km.pmml")).build();
        //evaluator.verify();
        //FileInputStream inputStream = new FileInputStream("Demo.pmml");
        //解析pmml文件，实际上是用JAXB做xml的解析
       // PMML pmml = org.jpmml.model.PMMLUtil.unmarshal(inputStream);
        //生成评估器
        //ModelEvaluator<?> evaluate = new ModelEvaluatorBuilder(pmml).build();

        //构建输入参数
        Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();
        List<InputField> inputFields = evaluator.getInputFields();
        for (InputField inputField : inputFields) {            //将参数通过模型对应的名称进行添加
            FieldName inputFieldName = inputField.getName();   //获取模型中的参数名
            Object paramValue = paramData.get(inputFieldName.getValue());   //获取模型参数名对应的参数值
            FieldValue fieldValue = inputField.prepare(paramValue);   //将参数值填入模型中的参数中
            arguments.put(inputFieldName, fieldValue);          //存放在map列表中
        }

        //开始评估
        Map<FieldName, ?> target = evaluator.evaluate(arguments);

        //获取评估结果
        List<TargetField> targetFields = evaluator.getTargetFields();
        Object targetFieldValue = target.get(targetFields.get(0).getFieldName());
        System.out.println("targetFieldValue: " + targetFieldValue);
        System.out.println("target: " + target);
    }
}
