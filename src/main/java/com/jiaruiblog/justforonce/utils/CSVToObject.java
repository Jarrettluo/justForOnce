package com.jiaruiblog.justforonce.utils;

import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import org.springframework.util.StringUtils;
import org.springframework.web.util.pattern.PathPattern;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @ClassName CSVToObject
 * @Description TODO 返回内容自定义返回列表或者集合；自定义哪些是不能重复的。
 * 也可以设置为是否转换为map？
 * @Author luojiarui
 * @Date 2022/7/17 1:49 下午
 * @Version 1.0
 **/
public class CSVToObject {

    private static final String [] TITLE_CH_SIM = {"信号名称", "信号类型", "单位", "一级属性", "二级属性", "映射级名称"};

    private static final String [] NESSCESSRY_FIELD = {"name", "signalType", "unit", "attribute1", "attribute2", "mappingName"};

    private static final Integer [] FIELD_LENGTH = {64, 20, 30, 50, 60, 50};

    private static final String [] FIELD_REGEX = {"*", "*", "*", "*", "*", "*"};

    private static Map<String, Integer> titleMap = new HashMap<>(8);

    private static Map<String, Integer> fieldMap = new HashMap<>(8);

    private static final String INFO_MESSAGE = "导入了 {0} 条";

    private Class<? extends Collection> collectionType = Set.class;

    private Class<?> targetClass;

    /**
     * @Author luojiarui
     * @Description // 无餐构造函数，用户实例初始化
     * @Date 1:58 下午 2022/7/17
     * @Param []
     * @return
     **/
    public CSVToObject() {
        this.generateFieldLengthMap();
    }


    public CSVToObject bind(Class<?> targetClass) {
        this.targetClass = targetClass;
        // 初始化阶段必需对每个field的类型进行判断基本类型，通过classloader进行判断
        this.config();
        return this;
    }

    public <T> CSVToObject bind(Class<?> targetClass, Class<T> collectionType) {
        System.out.println(collectionType.getName());
        if( !Collection.class.isAssignableFrom(collectionType)) {
            throw new RuntimeException("collection type is error!");
        }
        this.collectionType = (Class<? extends Collection>) collectionType;
        return bind(targetClass);
    }

    /**
     * @Author luojiarui
     * @Description 对Field的信息进行获取
     * @Date 7:27 下午 2022/7/17
     * @Param []
     * @return void
     **/
    private void config() {

        // todo 这里应该用map进行快速检索。

        List<String> rowNames = new ArrayList<>();
        List<Boolean> isNecessarys = new ArrayList<>();
        List<Integer> fieldLengths = new ArrayList<>();
        List<String> fieldRegexs = new ArrayList<>();
        List<Boolean> isRepeate = new ArrayList<>();

        Field [] fields = targetClass.getDeclaredFields();
        System.out.println(fields);
        for(int x = 0 ; x < fields.length; x++) {
            Transfer transfer = fields[x].getDeclaredAnnotation(Transfer.class);
            if(transfer != null) {
                if( !StringUtils.hasText(transfer.rowName())) {
                    continue;
                }
                rowNames.add(transfer.rowName());
                isNecessarys.add(transfer.isNecessary());
                fieldLengths.add(transfer.fieldLength());
                fieldRegexs.add(transfer.fieldRegex());
                isRepeate.add(transfer.isRepeat());
            }
        }

    }

    /**
     * @Author luojiarui
     * @Description //生成属性的长度限制
     * @Date 1:58 下午 2022/7/17
     * @Param []
     * @return void
     **/
    private void generateFieldLengthMap() {
        for (int i = 0; i < NESSCESSRY_FIELD.length; i++) {
            fieldMap.put(
                    NESSCESSRY_FIELD[i], FIELD_LENGTH[i]
            );
        }
    }

    public <T> List<T> CSVExport(String filePath) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        List<Object> targets = new ArrayList<>();

        Object target = targetClass.getDeclaredConstructor().newInstance();

        targets.add(target);
        if(target != null) {
            return (List<T>) targets;
        }


        // Step.1 参数检查
        // TODO

        // Step.2 逐行读取csv文件出来
        List<String> caseStringList = null;
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath),
            Charset.forName("gb2312"));
            BufferedReader br = new BufferedReader(isr)) {
            caseStringList = getCSVContentList(br);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if(Collections.checkedCollection(caseStringList, String.class))
        if(caseStringList == null || caseStringList.isEmpty()) {
            throw new RuntimeException("caseStringList is empty!");
        }

        // STEP.3 将读到的列表进行重整
        List<List<String>> midSigList = new ArrayList<>();

        // 传入的csv进行判断是否加首列数据
        Integer titleLength = titleMap.size();

        // 判断长度是否足够
        Integer rows = caseStringList.size() / titleLength;

        for(int i = 0; i < rows; i++) {
            List<String> midSigRows = caseStringList.subList(i * titleLength, (i+1) * titleLength);
            midSigList.add(midSigRows);
        }

        // STEP.4 是否进行正则判断

        midSigList = batchIsMatched(midSigList, titleMap);

        if(midSigList.isEmpty()) {
            throw new RuntimeException("midSigList is empty");
        }

        // STEP.5 Object instance
        // 这里判断是否进行分组？
        List<Object> midSignalMap = transformTestCases(midSigList, titleMap);

        // STEP.6 清空
        caseStringList.clear();

        // STEP.7 存库

        return new ArrayList<>();

    }

    public static List<String> getCSVContentList(BufferedReader br) {
        return null;
    }


    /**
     * @Author luojiarui
     * @Description 通过对csv的每一行进行正则匹配，筛选出符合要求的数据
     * @Date 2:34 下午 2022/7/17
     * @Param [list, titleMap]
     * @return java.util.List<java.util.List<java.lang.String>>
     **/
    public static List<List<String>> batchIsMatched(List<List<String>> list, Map<String, Integer> titleMap) {
        return null;
    }


    private static List<Object> transformTestCases(List<List<String>> cases, Map<String, Integer> titleMap) {
        List<Object> objects = new ArrayList<>();
        for (List<String> aCase : cases) {

            Object object = testCaseInstance(titleMap, aCase);
            // 判断每个中间信号的属性是否是完整的，否则就跳过
            if(!getFieldValueByName(NESSCESSRY_FIELD, object)) {
                break;
            }
            objects.add(object);

        }

        return objects;
    }

    private static Object testCaseInstance(Map<String, Integer> titleMap, List<String> caseLine) {
        Object object = new Object();
        for (Map.Entry<String, Integer> entry : titleMap.entrySet()) {

            // 找到需要赋值的内容
            String setValue = caseLine.get(entry.getValue());

            // 如果是某某值，则自动设置为某某值
            if(entry.getKey().equals("unit") && StringUtils.hasText(setValue)) {
                setValue = "/";
            }

            try {
                Method method = object.getClass().getDeclaredMethod("set" +
                        convertInitialUpper(entry.getKey()), String.class);
                method.invoke(object, setValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static boolean getFieldValueByName(String[] fileNames, Object o) {
        boolean flag = false;

        for (String fieldName : fileNames) {
            String getter = "get" + convertInitialUpper(fieldName);
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * @Author luojiarui
     * @Description 首字母转大写
     * @Date 2:47 下午 2022/7/17
     * @Param [value]
     * @return java.lang.String
     **/
    private static String convertInitialUpper(String word) {
        char[] chars = word.toCharArray();
        chars[0] -= 32;
        return new String(chars);
    }


}
